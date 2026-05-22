/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepaymentSchedule

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_account_number
import androidclient.feature.loan.generated.resources.feature_loan_disbursed_date
import androidclient.feature.loan.generated.resources.feature_loan_error_fetching_repayment_schedule
import androidclient.feature.loan.generated.resources.principal_paid_off
import androidclient.feature.loan.generated.resources.total_installments
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DataState.Error
import com.mifos.core.common.utils.DataState.Loading
import com.mifos.core.common.utils.DataState.Success
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanRepaymentScheduleRepository
import com.mifos.core.model.objects.account.loan.Period
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.loan.entity.LoanWithAssociationsEntity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
class LoanRepaymentScheduleViewModel(
    private val repository: LoanRepaymentScheduleRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LoanRepaymentScheduleState, LoanRepaymentScheduleEvent, LoanRepaymentScheduleAction>(
    initialState = run {
        val route = savedStateHandle.toRoute<LoanRepaymentScheduleScreenRoute>()
        LoanRepaymentScheduleState(
            loanId = route.loanAccountNumber,
        )
    },
) {

    init {
        viewModelScope.launch {
            loadLoanRepaySchedule()
        }
    }

    override fun handleAction(action: LoanRepaymentScheduleAction) {
        when (action) {
            LoanRepaymentScheduleAction.OnNavigateBack -> {
                sendEvent(LoanRepaymentScheduleEvent.NavigateBack)
            }

            LoanRepaymentScheduleAction.Retry -> {
                retry()
            }

            LoanRepaymentScheduleAction.ExportToPdf -> {
                sendEvent(LoanRepaymentScheduleEvent.ExportPdf)
            }

            is LoanRepaymentScheduleAction.PdfExportError -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = LoanRepaymentScheduleState.DialogState.Error(
                            title = action.title,
                            message = action.message,
                        ),
                    )
                }
            }

            LoanRepaymentScheduleAction.DismissErrorDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }
        }
    }

    private fun retry() {
        viewModelScope.launch {
            loadLoanRepaySchedule()
        }
    }

    private fun loadLoanRepaySchedule() {
        mutableStateFlow.update {
            it.copy(dataState = DataState.Loading)
        }

        viewModelScope.launch {
            repository.getLoanRepaySchedule(state.loanId).collect { dataState ->
                when (dataState) {
                    is Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dataState = DataState.Error(
                                    exception = Exception(getString(Res.string.feature_loan_error_fetching_repayment_schedule)),
                                ),
                            )
                        }
                    }

                    Loading -> {
                        mutableStateFlow.update {
                            it.copy(dataState = DataState.Loading)
                        }
                    }

                    is Success -> {
                        val tableData = mapToTableData(dataState.data)
                        mutableStateFlow.update {
                            it.copy(
                                repaymentScheduleTableData = tableData,
                                basicDetails = mapOf(
                                    getString(Res.string.feature_loan_account_number) to tableData.accountNo,
                                    getString(Res.string.feature_loan_disbursed_date) to tableData.disbursementDate,
                                    getString(Res.string.principal_paid_off) to tableData.principalPaid,
                                    getString(Res.string.total_installments) to "${tableData.installmentsPaid} / ${tableData.totalInstallments}",
                                ),
                                dataState = DataState.Success(dataState.data),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun mapToTableData(loan: LoanWithAssociationsEntity): LoanRepaymentScheduleState.RepaymentScheduleTableData {
        val currencyCode = loan.currency.code
        val maxDigits = loan.currency.decimalPlaces

        val periods = loan.repaymentSchedule.periods?.filter { it.period != null } ?: emptyList()

        return LoanRepaymentScheduleState.RepaymentScheduleTableData(
            accountNo = loan.accountNo,
            clientName = loan.clientName,
            productName = loan.loanProductName,
            disbursementDate = loan.timeline.actualDisbursementDate?.filterNotNull()?.let {
                DateHelper.getDateAsString(it)
            } ?: "",
            loanAmount = CurrencyFormatter.format(
                loan.summary.principalDisbursed,
                currencyCode,
                maxDigits,
            ),
            principalPaid = CurrencyFormatter.format(
                loan.summary.principalPaid,
                currencyCode,
                maxDigits,
            ),
            installmentsPaid = periods.count { it.complete == true }.toString(),
            installmentsLeft = periods.count { it.complete != true }.toString(),
            totalInstallments = loan.numberOfRepayments.toString(),
            currencyCode = currencyCode,
            periods = mapPeriodsData(periods, currencyCode, maxDigits),
            totals = calculateTotals(periods, currencyCode, maxDigits),
        )
    }

    private fun mapPeriodsData(
        periods: List<Period>,
        currencyCode: String?,
        maxDigits: Int?,
    ): List<LoanRepaymentScheduleState.RepaymentScheduleTableData.PeriodData> {
        return periods.map { period ->
            LoanRepaymentScheduleState.RepaymentScheduleTableData.PeriodData(
                number = period.period?.toString() ?: "",
                days = period.daysInPeriod?.toString() ?: "-",
                dueDate = period.dueDate?.let { DateHelper.getDateAsString(it) } ?: "",
                paidDate = period.obligationsMetOnDate?.let {
                    DateHelper.getDateAsString(it)
                } ?: "-",
                isPaid = period.complete == true,
                balanceOfLoan = CurrencyFormatter.format(
                    period.principalLoanBalanceOutstanding,
                    currencyCode,
                    maxDigits,
                ),
                principalDue = CurrencyFormatter.format(
                    period.principalDue,
                    currencyCode,
                    maxDigits,
                ),
                interest = CurrencyFormatter.format(period.interestDue, currencyCode, maxDigits),
                fees = CurrencyFormatter.format(
                    period.feeChargesDue ?: 0.0,
                    currencyCode,
                    maxDigits,
                ),
                penalties = CurrencyFormatter.format(
                    period.penaltyChargesDue ?: 0.0,
                    currencyCode,
                    maxDigits,
                ),
                due = CurrencyFormatter.format(period.totalDueForPeriod, currencyCode, maxDigits),
                paid = CurrencyFormatter.format(
                    period.totalPaidForPeriod ?: 0.0,
                    currencyCode,
                    maxDigits,
                ),
                inAdvance = CurrencyFormatter.format(
                    period.totalPaidInAdvanceForPeriod ?: 0.0,
                    currencyCode,
                    maxDigits,
                ),
                late = CurrencyFormatter.format(
                    period.totalPaidLateForPeriod ?: 0.0,
                    currencyCode,
                    maxDigits,
                ),
                outstanding = CurrencyFormatter.format(
                    period.totalOutstandingForPeriod ?: 0.0,
                    currencyCode,
                    maxDigits,
                ),
            )
        }
    }

    private fun calculateTotals(
        periods: List<Period>,
        currencyCode: String?,
        maxDigits: Int?,
    ): LoanRepaymentScheduleState.RepaymentScheduleTableData.TotalsData {
        return LoanRepaymentScheduleState.RepaymentScheduleTableData.TotalsData(
            principalDue = CurrencyFormatter.format(
                periods.sumOf { it.principalDue ?: 0.0 },
                currencyCode,
                maxDigits,
            ),
            interest = CurrencyFormatter.format(
                periods.sumOf { it.interestDue ?: 0.0 },
                currencyCode,
                maxDigits,
            ),
            fees = CurrencyFormatter.format(
                periods.sumOf { it.feeChargesDue ?: 0.0 },
                currencyCode,
                maxDigits,
            ),
            penalties = CurrencyFormatter.format(
                periods.sumOf { it.penaltyChargesDue ?: 0.0 },
                currencyCode,
                maxDigits,
            ),
            due = CurrencyFormatter.format(
                periods.sumOf { it.totalDueForPeriod ?: 0.0 },
                currencyCode,
                maxDigits,
            ),
            paid = CurrencyFormatter.format(
                periods.sumOf { it.totalPaidForPeriod ?: 0.0 },
                currencyCode,
                maxDigits,
            ),
            inAdvance = CurrencyFormatter.format(
                periods.sumOf { it.totalPaidInAdvanceForPeriod ?: 0.0 },
                currencyCode,
                maxDigits,
            ),
            late = CurrencyFormatter.format(
                periods.sumOf { it.totalPaidLateForPeriod ?: 0.0 },
                currencyCode,
                maxDigits,
            ),
            outstanding = CurrencyFormatter.format(
                periods.sumOf { it.totalOutstandingForPeriod ?: 0.0 },
                currencyCode,
                maxDigits,
            ),
        )
    }
}

/**
 * Represents the state of the repayment schedule screen.
 *
 * @property loanId The ID of the loan account.
 * @property basicDetails A map of basic details about the loan.
 * @property repaymentScheduleTableData The repayment schedule data.
 * @property dialogState The state of the dialog to display.
 */
data class LoanRepaymentScheduleState(
    val loanId: Int = 0,
    val basicDetails: Map<String, String?> = emptyMap(),
    val repaymentScheduleTableData: RepaymentScheduleTableData? = null,
    val dialogState: DialogState? = null,
    val dataState: DataState<LoanWithAssociationsEntity> = Loading,
) {
    /**
     * Represents the possible dialog states.
     */
    sealed interface DialogState {
        data class Error(val title: String, val message: String) : DialogState
    }

    /**
     * Pre-formatted table data for the repayment schedule display and PDF export.
     * All values are pre-formatted as strings for direct use in UI and HTML generation.
     */
    data class RepaymentScheduleTableData(
        val accountNo: String,
        val clientName: String,
        val productName: String,
        val disbursementDate: String,
        val loanAmount: String,
        val principalPaid: String,
        val installmentsPaid: String,
        val installmentsLeft: String,
        val totalInstallments: String,
        val currencyCode: String?,
        val periods: List<PeriodData>,
        val totals: TotalsData,
    ) {
        data class PeriodData(
            val number: String,
            val days: String,
            val dueDate: String,
            val paidDate: String,
            val isPaid: Boolean,
            val balanceOfLoan: String,
            val principalDue: String,
            val interest: String,
            val fees: String,
            val penalties: String,
            val due: String,
            val paid: String,
            val inAdvance: String,
            val late: String,
            val outstanding: String,
        )

        data class TotalsData(
            val principalDue: String,
            val interest: String,
            val fees: String,
            val penalties: String,
            val due: String,
            val paid: String,
            val inAdvance: String,
            val late: String,
            val outstanding: String,
        )
    }
}

/**
 * One-time events emitted by the ViewModel.
 */
sealed interface LoanRepaymentScheduleEvent {
    data object NavigateBack : LoanRepaymentScheduleEvent
    data object ExportPdf : LoanRepaymentScheduleEvent
}

/**
 * Actions that can be sent to the ViewModel.
 */
sealed interface LoanRepaymentScheduleAction {
    data object OnNavigateBack : LoanRepaymentScheduleAction
    data object Retry : LoanRepaymentScheduleAction
    data object ExportToPdf : LoanRepaymentScheduleAction
    data class PdfExportError(val title: String, val message: String) : LoanRepaymentScheduleAction
    data object DismissErrorDialog : LoanRepaymentScheduleAction
}
