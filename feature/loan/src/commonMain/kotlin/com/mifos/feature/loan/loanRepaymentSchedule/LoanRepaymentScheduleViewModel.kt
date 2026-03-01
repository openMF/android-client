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
import androidclient.feature.loan.generated.resources.account_number
import androidclient.feature.loan.generated.resources.disbursement_date
import androidclient.feature.loan.generated.resources.feature_loan_error_fetching_repayment_schedule
import androidclient.feature.loan.generated.resources.principle_paid_off
import androidclient.feature.loan.generated.resources.total_installments
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanRepaymentScheduleRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.account.loan.Period
import kotlinx.coroutines.flow.MutableStateFlow
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
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
class LoanRepaymentScheduleViewModel(
    private val repository: LoanRepaymentScheduleRepository,
    private val networkMonitor: NetworkMonitor,
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
        observeNetwork()
    }

    /**
     * Observes the network connectivity status and updates state accordingly.
     */
    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline
                .distinctUntilChanged()
                .collect { isOnline ->
                    trySendAction(LoanRepaymentScheduleAction.ReceiveNetworkStatus(isOnline))
                }
        }
    }

    override fun handleAction(action: LoanRepaymentScheduleAction) {
        when (action) {
            LoanRepaymentScheduleAction.OnNavigateBack -> {
                sendEvent(LoanRepaymentScheduleEvent.NavigateBack)
            }

            is LoanRepaymentScheduleAction.ReceiveNetworkStatus -> {
                handleNetworkStatus(action.isOnline)
            }

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
    private fun mapToTableData(loan: LoanWithAssociationsEntity): LoanRepaymentScheduleState.RepaymentScheduleTableData {
        val currencyCode = loan.currency.code
        val maxDigits = loan.currency.decimalPlaces

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

    private fun formatDate(date: List<Int>?): String =
        date?.let { DateHelper.getDateAsString(it) }.orEmpty()
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
 * @property networkStatus The network connectivity status.
 * @property screenState The overall state of the screen.
 */
data class LoanRepaymentScheduleState(
    val loanId: Int = 0,
    val basicDetails: Map<String, String?> = emptyMap(),
    val repaymentScheduleTableData: RepaymentScheduleTableData? = null,
    val dialogState: DialogState? = null,
    val networkStatus: Boolean = false,
    val screenState: ScreenState = ScreenState.Loading,
) {
    /**
     * Represents the possible screen states.
     */
    sealed interface ScreenState {
        data object Loading : ScreenState
        data object Success : ScreenState
        data object Network : ScreenState
        data class Error(val message: StringResource) : ScreenState
    }

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

    /**
     * Action to receive the network status.
     *
     * @param isOnline Whether the device is online.
     */
    data class ReceiveNetworkStatus(val isOnline: Boolean) : LoanRepaymentScheduleAction
}
