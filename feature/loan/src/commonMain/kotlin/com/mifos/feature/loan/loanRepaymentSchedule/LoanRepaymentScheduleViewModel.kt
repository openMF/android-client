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
import com.mifos.core.model.objects.account.loan.RepaymentScheduleRowData
import com.mifos.core.model.objects.account.loan.RepaymentScheduleTableData
import com.mifos.core.model.objects.account.loan.RepaymentScheduleTotalsData
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class LoanRepaymentScheduleViewModel(
    private val repository: LoanRepaymentScheduleRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val loanId = savedStateHandle.toRoute<LoanRepaymentScheduleScreenRoute>().loanAccountNumber

    private val retryTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    val loanRepaymentScheduleUiState: StateFlow<LoanRepaymentScheduleUiState> =
        retryTrigger
            .onStart { emit(Unit) }
            .flatMapLatest {
                repository.getLoanRepaySchedule(loanId).map { state ->
                    mapToUiState(state)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = LoanRepaymentScheduleUiState.ShowProgressbar,
            )

    fun loadLoanRepaymentSchedule() {
        retryTrigger.tryEmit(Unit)
    }

    private fun mapToUiState(state: DataState<LoanWithAssociationsEntity>): LoanRepaymentScheduleUiState {
        return when (state) {
            is DataState.Loading -> LoanRepaymentScheduleUiState.ShowProgressbar
            is DataState.Error -> LoanRepaymentScheduleUiState.ShowFetchingError(state.message)
            is DataState.Success -> {
                val data = state.data
                val currencyCode = data.currency?.code.orEmpty()
                val maxDigits = data.currency?.decimalPlaces ?: 2
                val allPeriods = data.repaymentSchedule.periods.orEmpty()
                val periods = data.repaymentSchedule.getListOfActualPeriods()

                val disbursementRow = allPeriods.firstOrNull()
                    ?.takeIf { it.period == null }
                    ?.let { buildRowData(it, "", currencyCode, maxDigits) }

                val rows = periods.mapIndexed { index, period ->
                    buildRowData(period, (index + 1).toString(), currencyCode, maxDigits)
                }

                LoanRepaymentScheduleUiState.ShowLoanRepaymentSchedule(
                    tableData = RepaymentScheduleTableData(
                        disbursementRow = disbursementRow,
                        rows = rows,
                        totals = buildTotalsData(periods, currencyCode, maxDigits),
                        completeCount = RepaymentSchedule.getNumberOfRepaymentsComplete(periods),
                        overdueCount = RepaymentSchedule.getNumberOfRepaymentsOverDue(periods),
                        pendingCount = RepaymentSchedule.getNumberOfRepaymentsPending(periods),
                    ),
                )
            }
        }
    }

    private fun buildRowData(
        period: Period,
        number: String,
        currencyCode: String,
        maxDigits: Int,
    ) = RepaymentScheduleRowData(
        number = number,
        days = period.daysInPeriod?.toString().orEmpty(),
        date = formatDate(period.dueDate),
        paidDate = formatDate(period.obligationsMetOnDate),
        balance = formatAmount(period.principalLoanBalanceOutstanding, currencyCode, maxDigits),
        principal = formatAmount(period.principalDue, currencyCode, maxDigits),
        interest = formatAmount(period.interestDue, currencyCode, maxDigits),
        fees = formatAmount(period.feeChargesDue, currencyCode, maxDigits),
        penalties = formatAmount(period.penaltyChargesDue, currencyCode, maxDigits),
        due = formatAmount(period.totalDueForPeriod, currencyCode, maxDigits),
        paid = formatAmount(period.totalPaidForPeriod, currencyCode, maxDigits),
        inAdvance = formatAmount(period.totalPaidInAdvanceForPeriod, currencyCode, maxDigits),
        late = formatAmount(period.totalPaidLateForPeriod, currencyCode, maxDigits),
        outstanding = formatAmount(period.totalOutstandingForPeriod, currencyCode, maxDigits),
    )

    private fun buildTotalsData(
        periods: List<Period>,
        currencyCode: String,
        maxDigits: Int,
    ) = RepaymentScheduleTotalsData(
        principal = formatAmount(periods.sumOf { it.principalDue ?: 0.0 }, currencyCode, maxDigits),
        interest = formatAmount(periods.sumOf { it.interestDue ?: 0.0 }, currencyCode, maxDigits),
        fees = formatAmount(periods.sumOf { it.feeChargesDue ?: 0.0 }, currencyCode, maxDigits),
        penalties = formatAmount(periods.sumOf { it.penaltyChargesDue ?: 0.0 }, currencyCode, maxDigits),
        due = formatAmount(periods.sumOf { it.totalDueForPeriod ?: 0.0 }, currencyCode, maxDigits),
        paid = formatAmount(periods.sumOf { it.totalPaidForPeriod ?: 0.0 }, currencyCode, maxDigits),
        inAdvance = formatAmount(periods.sumOf { it.totalPaidInAdvanceForPeriod ?: 0.0 }, currencyCode, maxDigits),
        late = formatAmount(periods.sumOf { it.totalPaidLateForPeriod ?: 0.0 }, currencyCode, maxDigits),
        outstanding = formatAmount(periods.sumOf { it.totalOutstandingForPeriod ?: 0.0 }, currencyCode, maxDigits),
    )

    private fun formatAmount(amount: Double?, currencyCode: String, maxDigits: Int): String {
        amount ?: return ""
        return CurrencyFormatter.format(
            balance = amount,
            currencyCode = currencyCode,
            maximumFractionDigits = maxDigits,
        )
    }

    private fun formatDate(date: List<Int>?): String =
        date?.let { DateHelper.getDateAsString(it) }.orEmpty()
}
