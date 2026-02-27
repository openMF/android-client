/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepaymentSchedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanRepaymentScheduleRepository
import com.mifos.core.model.objects.account.loan.Period
import com.mifos.core.model.objects.account.loan.RepaymentSchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoanRepaymentScheduleViewModel(
    private val repository: LoanRepaymentScheduleRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val loanId = savedStateHandle.toRoute<LoanRepaymentScheduleScreenRoute>().loanAccountNumber

    private val _loanRepaymentScheduleUiState =
        MutableStateFlow<LoanRepaymentScheduleUiState>(LoanRepaymentScheduleUiState.ShowProgressbar)
    val loanRepaymentScheduleUiState: StateFlow<LoanRepaymentScheduleUiState> get() = _loanRepaymentScheduleUiState

    fun loadLoanRepaySchedule() {
        viewModelScope.launch {
            repository.getLoanRepaySchedule(loanId).collect { state ->
                when (state) {
                    is DataState.Error ->
                        _loanRepaymentScheduleUiState.value =
                            LoanRepaymentScheduleUiState.ShowFetchingError(state.message)
                    DataState.Loading ->
                        _loanRepaymentScheduleUiState.value = LoanRepaymentScheduleUiState.ShowProgressbar
                    is DataState.Success -> {
                        val loanWithAssociations = state.data
                        val currencyCode = loanWithAssociations.currency?.code.orEmpty()
                        val maxDigits = loanWithAssociations.currency?.decimalPlaces ?: 2
                        val allPeriods = loanWithAssociations.repaymentSchedule.periods.orEmpty()
                        val periods = loanWithAssociations.repaymentSchedule.getListOfActualPeriods()

                        fun fmt(amount: Double?) = if (amount == null) {
                            ""
                        } else {
                            CurrencyFormatter.format(
                                balance = amount,
                                currencyCode = currencyCode,
                                maximumFractionDigits = maxDigits,
                            )
                        }

                        fun fmtDate(date: List<Int>?) = date?.let { DateHelper.getDateAsString(it) } ?: ""

                        fun periodToRowData(period: Period, number: String) = RepaymentScheduleRowData(
                            number = number,
                            days = period.daysInPeriod?.toString() ?: "",
                            date = fmtDate(period.dueDate),
                            paidDate = fmtDate(period.obligationsMetOnDate),
                            balance = fmt(period.principalLoanBalanceOutstanding),
                            principal = fmt(period.principalDue),
                            interest = fmt(period.interestDue),
                            fees = fmt(period.feeChargesDue),
                            penalties = fmt(period.penaltyChargesDue),
                            due = fmt(period.totalDueForPeriod),
                            paid = fmt(period.totalPaidForPeriod),
                            inAdvance = fmt(period.totalPaidInAdvanceForPeriod),
                            late = fmt(period.totalPaidLateForPeriod),
                            outstanding = fmt(period.totalOutstandingForPeriod),
                        )

                        val disbursementRow = allPeriods.firstOrNull()
                            ?.takeIf { it.period == null }
                            ?.let { periodToRowData(it, "") }

                        val rows = periods.mapIndexed { index, period ->
                            periodToRowData(period, (index + 1).toString())
                        }

                        val totals = RepaymentScheduleTotalsData(
                            principal = fmt(periods.sumOf { it.principalDue ?: 0.0 }),
                            interest = fmt(periods.sumOf { it.interestDue ?: 0.0 }),
                            fees = fmt(periods.sumOf { it.feeChargesDue ?: 0.0 }),
                            penalties = fmt(periods.sumOf { it.penaltyChargesDue ?: 0.0 }),
                            due = fmt(periods.sumOf { it.totalDueForPeriod ?: 0.0 }),
                            paid = fmt(periods.sumOf { it.totalPaidForPeriod ?: 0.0 }),
                            inAdvance = fmt(periods.sumOf { it.totalPaidInAdvanceForPeriod ?: 0.0 }),
                            late = fmt(periods.sumOf { it.totalPaidLateForPeriod ?: 0.0 }),
                            outstanding = fmt(periods.sumOf { it.totalOutstandingForPeriod ?: 0.0 }),
                        )

                        _loanRepaymentScheduleUiState.value =
                            LoanRepaymentScheduleUiState.ShowLoanRepaySchedule(
                                tableData = RepaymentScheduleTableData(
                                    disbursementRow = disbursementRow,
                                    rows = rows,
                                    totals = totals,
                                    completeCount = RepaymentSchedule.getNumberOfRepaymentsComplete(periods),
                                    overdueCount = RepaymentSchedule.getNumberOfRepaymentsOverDue(periods),
                                    pendingCount = RepaymentSchedule.getNumberOfRepaymentsPending(periods),
                                ),
                            )
                    }
                }
            }
        }
    }
}