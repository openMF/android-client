/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDashboard

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_approved_amount
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan
import androidclient.feature.loan.generated.resources.feature_loan_remaining_balance
import androidclient.feature.loan.generated.resources.feature_loan_requested_amount
import androidclient.feature.loan.generated.resources.feature_loan_rescheduled_amount
import androidclient.feature.loan.generated.resources.feature_loan_review_in_progress
import androidclient.feature.loan.generated.resources.feature_loan_surplus_balance
import androidclient.feature.loan.generated.resources.feature_loan_timeline_approved_date
import androidclient.feature.loan.generated.resources.feature_loan_timeline_closed
import androidclient.feature.loan.generated.resources.feature_loan_timeline_disbursed_date
import androidclient.feature.loan.generated.resources.feature_loan_timeline_expected_disbursement
import androidclient.feature.loan.generated.resources.feature_loan_timeline_expected_maturity
import androidclient.feature.loan.generated.resources.feature_loan_timeline_overpaid
import androidclient.feature.loan.generated.resources.feature_loan_timeline_rejected
import androidclient.feature.loan.generated.resources.feature_loan_timeline_rescheduled
import androidclient.feature.loan.generated.resources.feature_loan_timeline_submitted_date
import androidclient.feature.loan.generated.resources.feature_loan_timeline_withdrawn
import androidclient.feature.loan.generated.resources.feature_loan_total_outstanding
import androidclient.feature.loan.generated.resources.feature_loan_total_repaid
import androidclient.feature.loan.generated.resources.feature_loan_transaction_accrual
import androidclient.feature.loan.generated.resources.feature_loan_transaction_approve_transfer
import androidclient.feature.loan.generated.resources.feature_loan_transaction_charge_payment
import androidclient.feature.loan.generated.resources.feature_loan_transaction_contra
import androidclient.feature.loan.generated.resources.feature_loan_transaction_disbursement
import androidclient.feature.loan.generated.resources.feature_loan_transaction_initiate_transfer
import androidclient.feature.loan.generated.resources.feature_loan_transaction_recovery_repayment
import androidclient.feature.loan.generated.resources.feature_loan_transaction_refund
import androidclient.feature.loan.generated.resources.feature_loan_transaction_reject_transfer
import androidclient.feature.loan.generated.resources.feature_loan_transaction_repayment
import androidclient.feature.loan.generated.resources.feature_loan_transaction_repayment_at_disbursement
import androidclient.feature.loan.generated.resources.feature_loan_transaction_unknown
import androidclient.feature.loan.generated.resources.feature_loan_transaction_waive_charges
import androidclient.feature.loan.generated.resources.feature_loan_transaction_waive_interest
import androidclient.feature.loan.generated.resources.feature_loan_transaction_withdraw_transfer
import androidclient.feature.loan.generated.resources.feature_loan_transaction_write_off
import androidclient.feature.loan.generated.resources.feature_loan_written_off_amount
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.model.objects.account.loan.Transaction
import com.mifos.core.model.objects.account.loan.Type
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.StringResource
import kotlin.math.roundToInt

internal class LoanDashboardViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanAccountSummaryRepository,
) : BaseViewModel<LoanDashboardState, LoanDashboardEvent, LoanDashboardAction>(
    initialState = LoanDashboardState(),
) {

    private val loanId =
        savedStateHandle.toRoute<LoanDashboardScreenRoute>().loanId

    private var loadLoanJob: Job? = null

    init {
        loadLoanDetails()
    }

    private fun loadLoanDetails() {
        loadLoanJob?.cancel()
        loadLoanJob = repository.getLoanById(loanId).onEach { state ->
            when (state) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            viewState = LoanDashboardState.ViewState.Error(Res.string.feature_loan_failed_to_load_loan),
                        )
                    }
                }

                DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(
                            viewState = LoanDashboardState.ViewState.Loading,
                        )
                    }
                }

                is DataState.Success ->
                    state.data?.let(::populateLoanDashboardState) ?: run {
                        mutableStateFlow.update {
                            it.copy(
                                viewState = LoanDashboardState.ViewState.Empty,
                            )
                        }
                    }
            }
        }.launchIn(viewModelScope)
    }

    private fun populateLoanDashboardState(loanDetails: LoanWithAssociationsEntity) {
        val currencyCode = loanDetails.currency.code
        val maxDigits = loanDetails.currency.decimalPlaces

        val loanStatus = getLoanStatus(loanDetails)

        val periodsGraphValues = getPeriodsGraphValues(loanDetails)

        val heroLabel = getHeroLabel(loanStatus)

        val heroValue = getHeroValue(loanStatus, loanDetails)

        val (nextRepaymentDueDate, nextRepaymentAmount) =
            getNextRepaymentInfo(loanStatus, loanDetails, currencyCode, maxDigits)

        val recentTransactions = getRecentTransactions(
            transactions = loanDetails.transactions,
            currencyCode = currencyCode,
            maxDigits = maxDigits,
        )

        val repaymentProgressData = getRepaymentProgressData(
            loanDetails = loanDetails,
            currencyCode = currencyCode,
            maxDigits = maxDigits,
        )

        val timelineSteps = getTimelineSteps(loanStatus, loanDetails)

        mutableStateFlow.update {
            it.copy(
                loanStatus = loanStatus,
                heroLabel = heroLabel,
                heroValue = CurrencyFormatter.format(heroValue, currencyCode, maxDigits),
                nextRepaymentDueDate = nextRepaymentDueDate,
                nextRepaymentAmount = nextRepaymentAmount,
                recentTransactions = recentTransactions,
                periodsGraphValues = periodsGraphValues,
                viewState = LoanDashboardState.ViewState.Success,
                loanDetails = loanDetails,
                repaymentProgressData = repaymentProgressData,
                timelineSteps = timelineSteps,
            )
        }
    }

    private fun getPeriodsGraphValues(
        loanDetails: LoanWithAssociationsEntity,
    ): List<PeriodGraphValue> =
        loanDetails.repaymentSchedule.periods
            ?.drop(1)
            ?.map {
                PeriodGraphValue(
                    interest = it.interestDue ?: 0.0,
                    principal = it.principalDue ?: 0.0,
                )
            } ?: emptyList()

    private fun getLoanStatus(loanDetails: LoanWithAssociationsEntity): LoanStatus =
        when {
            loanDetails.status.overpaid == true -> LoanStatus.OVERPAID
            loanDetails.status.closedWrittenOff == true -> LoanStatus.CLOSED_WRITTEN_OFF
            loanDetails.status.closedRescheduled == true -> LoanStatus.CLOSED_RESCHEDULED
            loanDetails.status.closedObligationsMet == true -> LoanStatus.CLOSED_OBLIGATIONS_MET
            loanDetails.status.active == true -> LoanStatus.ACTIVE
            loanDetails.status.waitingForDisbursal == true -> LoanStatus.WAITING_FOR_DISBURSAL
            loanDetails.status.code == "loanStatusType.withdrawn.by.client" -> LoanStatus.WITHDRAWN_BY_APPLICANT
            loanDetails.status.pendingApproval == true -> LoanStatus.PENDING_APPROVAL
            else -> LoanStatus.REJECTED
        }

    private fun getHeroLabel(status: LoanStatus) =
        when (status) {
            LoanStatus.PENDING_APPROVAL, LoanStatus.REJECTED, LoanStatus.WITHDRAWN_BY_APPLICANT ->
                Res.string.feature_loan_requested_amount

            LoanStatus.WAITING_FOR_DISBURSAL ->
                Res.string.feature_loan_approved_amount

            LoanStatus.ACTIVE ->
                Res.string.feature_loan_total_outstanding

            LoanStatus.OVERPAID ->
                Res.string.feature_loan_surplus_balance

            LoanStatus.CLOSED_OBLIGATIONS_MET ->
                Res.string.feature_loan_total_repaid

            LoanStatus.CLOSED_WRITTEN_OFF ->
                Res.string.feature_loan_written_off_amount

            LoanStatus.CLOSED_RESCHEDULED ->
                Res.string.feature_loan_rescheduled_amount
        }

    private fun getHeroValue(
        status: LoanStatus,
        loanDetails: LoanWithAssociationsEntity,
    ) = when (status) {
        LoanStatus.PENDING_APPROVAL, LoanStatus.REJECTED, LoanStatus.WITHDRAWN_BY_APPLICANT ->
            loanDetails.principal

        LoanStatus.WAITING_FOR_DISBURSAL ->
            loanDetails.approvedPrincipal

        LoanStatus.ACTIVE ->
            loanDetails.summary.totalOutstanding

        LoanStatus.OVERPAID ->
            loanDetails.totalOverpaid

        LoanStatus.CLOSED_OBLIGATIONS_MET ->
            loanDetails.summary.totalRepayment

        LoanStatus.CLOSED_WRITTEN_OFF ->
            loanDetails.summary.totalWrittenOff

        LoanStatus.CLOSED_RESCHEDULED ->
            loanDetails.summary.principalOutstanding
    }

    private fun getNextRepaymentInfo(
        loanStatus: LoanStatus,
        loanDetails: LoanWithAssociationsEntity,
        currencyCode: String?,
        maxDigits: Int?,
    ): Pair<String, String> {
        if (loanStatus != LoanStatus.ACTIVE &&
            loanStatus != LoanStatus.WAITING_FOR_DISBURSAL
        ) {
            return Pair("", "")
        }

        val nextPeriod = loanDetails.repaymentSchedule.periods
            ?.firstOrNull { it.complete == false }

        val dueDate = nextPeriod?.dueDate?.let { DateHelper.getDateAsString(it) }

        val amount = nextPeriod?.totalDueForPeriod?.let {
            CurrencyFormatter.format(it, currencyCode, maxDigits)
        }

        return Pair(dueDate ?: "", amount ?: "")
    }

    private fun getRecentTransactions(
        transactions: List<Transaction>,
        currencyCode: String?,
        maxDigits: Int?,
    ): List<RecentTransaction> {
        return transactions
            .sortedWith(
                compareByDescending<Transaction> { it.date.getOrElse(0) { 0 } }
                    .thenByDescending { it.date.getOrElse(1) { 0 } }
                    .thenByDescending { it.date.getOrElse(2) { 0 } },
            )
            .take(3)
            .map { transaction ->

                val typeResource = getTransactionTypeResource(transaction.type)
                val isIncreasingDebt: Boolean? = transaction.type?.let { type ->
                    when {
                        type.disbursement == true ||
                            type.accrual == true ||
                            type.refund == true -> true

                        type.repayment == true ||
                            type.repaymentAtDisbursement == true ||
                            type.recoveryRepayment == true ||
                            type.chargePayment == true ||
                            type.waiveInterest == true ||
                            type.waiveCharges == true -> false

                        else -> null
                    }
                }
                val formattedAmount = transaction.amount?.let { amount ->
                    CurrencyFormatter.format(amount, currencyCode, maxDigits)
                } ?: ""
                val formattedDate =
                    DateHelper.getDateAsString(transaction.date)

                RecentTransaction(
                    type = typeResource,
                    amount = formattedAmount,
                    date = formattedDate,
                    isIncreasingDebt = isIncreasingDebt,
                )
            }
    }

    private fun getRepaymentProgressData(
        loanDetails: LoanWithAssociationsEntity,
        currencyCode: String?,
        maxDigits: Int?,
    ): RepaymentProgressData {
        val summary = loanDetails.summary

        val totalRepayment = summary.totalRepayment ?: 0.0
        val totalExpected = summary.totalExpectedRepayment ?: 0.0
        val totalOutstanding = summary.totalOutstanding ?: 0.0

        val rawPercent = if (totalExpected > 0) (totalRepayment / totalExpected) else 0.0
        val percentFloat = rawPercent.coerceIn(0.0, 1.0).toFloat()
        val percentText = percentText(percentFloat)

        val secondaryPercentFloat = (1f - percentFloat).coerceIn(0f, 1f)
        val secondaryPercentText = percentText(secondaryPercentFloat)

        return RepaymentProgressData(
            primaryLabel = Res.string.feature_loan_total_repaid,
            primaryAmount = CurrencyFormatter.format(
                totalRepayment,
                currencyCode,
                maxDigits,
            ),
            primaryPercent = percentFloat,
            primaryPercentText = percentText,
            secondaryLabel = Res.string.feature_loan_remaining_balance,
            secondaryAmount = CurrencyFormatter.format(
                totalOutstanding,
                currencyCode,
                maxDigits,
            ),
            secondaryPercent = secondaryPercentFloat,
            secondaryPercentText = secondaryPercentText,
        )
    }

    private fun percentText(value: Float): String {
        val scaled = (value * 10000f).roundToInt()
        val whole = scaled / 100
        val frac = (scaled % 100).let { if (it < 0) -it else it }
        return "$whole.${frac.toString().padStart(2, '0')}%"
    }

    private fun getTimelineSteps(
        status: LoanStatus,
        loanDetails: LoanWithAssociationsEntity,
    ): List<TimelineStep> {
        val steps = mutableListOf<TimelineStep>()
        val timeline = loanDetails.timeline

        val submittedDate = timeline.submittedOnDate?.let {
            DateHelper.getDateAsString(it)
        } ?: ""
        val approvedDate = timeline.approvedOnDate?.let {
            DateHelper.getDateAsString(it)
        } ?: ""
        val disbursedDate = timeline.actualDisbursementDate
            ?.filterNotNull()
            ?.let { DateHelper.getDateAsString(it) }
            ?: ""
        val expectedDisbursementDate = timeline.expectedDisbursementDate?.let {
            DateHelper.getDateAsString(it)
        } ?: ""
        val expectedMaturityDate = timeline.expectedMaturityDate?.let {
            DateHelper.getDateAsString(it)
        } ?: ""
        val closedDate = timeline.closedOnDate?.let {
            DateHelper.getDateAsString(it)
        } ?: ""
        val overpaidDate = loanDetails.overpaidOnDate?.let {
            DateHelper.getDateAsString(it)
        } ?: ""
        val withdrawnDate = timeline.withdrawnOnDate?.let {
            DateHelper.getDateAsString(it)
        } ?: ""

        if (timeline.submittedOnDate != null) {
            steps.add(
                TimelineStep(
                    title = Res.string.feature_loan_timeline_submitted_date,
                    date = submittedDate,
                    state = TimelineStepState.COMPLETED,
                ),
            )
        }

        if (status == LoanStatus.REJECTED) {
            steps.add(
                TimelineStep(
                    title = Res.string.feature_loan_timeline_rejected,
                    date = closedDate,
                    state = TimelineStepState.ERROR,
                ),
            )
            return steps
        }

        if (status == LoanStatus.WITHDRAWN_BY_APPLICANT) {
            if (timeline.approvedOnDate != null) {
                steps.add(
                    TimelineStep(
                        title = Res.string.feature_loan_timeline_approved_date,
                        date = approvedDate,
                        state = TimelineStepState.COMPLETED,
                    ),
                )
            }

            steps.add(
                TimelineStep(
                    title = Res.string.feature_loan_timeline_withdrawn,
                    date = withdrawnDate,
                    state = TimelineStepState.COMPLETED,
                ),
            )
            return steps
        }

        if (status == LoanStatus.PENDING_APPROVAL) {
            steps.add(
                TimelineStep(
                    Res.string.feature_loan_review_in_progress,
                    "",
                    TimelineStepState.CURRENT,
                ),
            )
            return steps
        }

        if (status == LoanStatus.WAITING_FOR_DISBURSAL) {
            steps.add(
                TimelineStep(
                    title = Res.string.feature_loan_timeline_approved_date,
                    date = approvedDate,
                    state = TimelineStepState.COMPLETED,
                ),
            )
            steps.add(
                TimelineStep(
                    title = Res.string.feature_loan_timeline_expected_disbursement,
                    date = expectedDisbursementDate,
                    state = TimelineStepState.CURRENT,
                ),
            )
            steps.add(
                TimelineStep(
                    title = Res.string.feature_loan_timeline_expected_maturity,
                    date = expectedMaturityDate,
                    state = TimelineStepState.CURRENT,
                ),
            )
            return steps
        }

        if (
            status == LoanStatus.ACTIVE ||
            status == LoanStatus.CLOSED_OBLIGATIONS_MET ||
            status == LoanStatus.CLOSED_RESCHEDULED ||
            status == LoanStatus.CLOSED_WRITTEN_OFF ||
            status == LoanStatus.OVERPAID
        ) {
            steps.add(
                TimelineStep(
                    title = Res.string.feature_loan_timeline_approved_date,
                    date = approvedDate,
                    state = TimelineStepState.COMPLETED,
                ),
            )
            steps.add(
                TimelineStep(
                    title = Res.string.feature_loan_timeline_disbursed_date,
                    date = disbursedDate,
                    state = TimelineStepState.COMPLETED,
                ),
            )
        }

        when (status) {
            LoanStatus.ACTIVE -> {
                steps.add(
                    TimelineStep(
                        title = Res.string.feature_loan_timeline_expected_maturity,
                        date = expectedMaturityDate,
                        state = TimelineStepState.CURRENT,
                    ),
                )
            }

            LoanStatus.OVERPAID -> {
                steps.add(
                    TimelineStep(
                        title = Res.string.feature_loan_timeline_overpaid,
                        date = overpaidDate,
                        state = TimelineStepState.CURRENT,
                    ),
                )
            }

            LoanStatus.CLOSED_OBLIGATIONS_MET -> {
                steps.add(
                    TimelineStep(
                        title = Res.string.feature_loan_timeline_closed,
                        date = closedDate,
                        state = TimelineStepState.COMPLETED,
                    ),
                )
            }

            LoanStatus.CLOSED_RESCHEDULED -> {
                steps.add(
                    TimelineStep(
                        title = Res.string.feature_loan_timeline_rescheduled,
                        date = closedDate,
                        state = TimelineStepState.COMPLETED,
                    ),
                )
            }

            LoanStatus.CLOSED_WRITTEN_OFF -> {
                steps.add(
                    TimelineStep(
                        title = Res.string.feature_loan_timeline_closed,
                        date = closedDate,
                        state = TimelineStepState.ERROR,
                    ),
                )
            }

            else -> {}
        }
        return steps
    }

    private fun getTransactionTypeResource(type: Type?): StringResource {
        if (type == null) return Res.string.feature_loan_transaction_unknown

        return when {
            type.disbursement == true -> Res.string.feature_loan_transaction_disbursement
            type.repayment == true -> Res.string.feature_loan_transaction_repayment
            type.repaymentAtDisbursement == true -> Res.string.feature_loan_transaction_repayment_at_disbursement
            type.recoveryRepayment == true -> Res.string.feature_loan_transaction_recovery_repayment
            type.waiveInterest == true -> Res.string.feature_loan_transaction_waive_interest
            type.waiveCharges == true -> Res.string.feature_loan_transaction_waive_charges
            type.chargePayment == true -> Res.string.feature_loan_transaction_charge_payment
            type.refund == true -> Res.string.feature_loan_transaction_refund
            type.writeOff == true -> Res.string.feature_loan_transaction_write_off
            type.accrual == true -> Res.string.feature_loan_transaction_accrual
            type.contra == true -> Res.string.feature_loan_transaction_contra
            type.initiateTransfer == true -> Res.string.feature_loan_transaction_initiate_transfer
            type.approveTransfer == true -> Res.string.feature_loan_transaction_approve_transfer
            type.withdrawTransfer == true -> Res.string.feature_loan_transaction_withdraw_transfer
            type.rejectTransfer == true -> Res.string.feature_loan_transaction_reject_transfer
            else -> Res.string.feature_loan_transaction_unknown
        }
    }

    override fun handleAction(action: LoanDashboardAction) {
        when (action) {
            LoanDashboardAction.OnRetry -> {
                loadLoanDetails()
            }

            LoanDashboardAction.NavigateBack -> {
                sendEvent(LoanDashboardEvent.NavigateBack)
            }

            is LoanDashboardAction.TogglePrincipalVisibility -> {
                mutableStateFlow.update {
                    it.copy(
                        isGraphPrincipalVisible = !it.isGraphPrincipalVisible,
                    )
                }
            }

            is LoanDashboardAction.ToggleInterestVisibility -> {
                mutableStateFlow.update {
                    it.copy(
                        isGraphInterestVisible = !it.isGraphInterestVisible,
                    )
                }
            }

            is LoanDashboardAction.NavigateToTransactions -> {
                sendEvent(LoanDashboardEvent.NavigateToTransactions(action.loanId))
            }

            is LoanDashboardAction.RejectLoan -> {
                sendEvent(LoanDashboardEvent.NavigateToRejectLoan(action.loanId))
            }

            is LoanDashboardAction.ApproveLoan -> {
                sendEvent(LoanDashboardEvent.NavigateToApproveLoan(action.loanId))
            }

            is LoanDashboardAction.UndoApproval -> {
                sendEvent(LoanDashboardEvent.NavigateToUndoApproval(action.loanId))
            }

            is LoanDashboardAction.DisburseLoan -> {
                sendEvent(LoanDashboardEvent.NavigateToDisburseLoan(action.loanId))
            }

            is LoanDashboardAction.MakeRepayment -> {
                sendEvent(LoanDashboardEvent.NavigateToMakeRepayment(action.loanId))
            }

            is LoanDashboardAction.CreditBalanceRefund -> {
                sendEvent(LoanDashboardEvent.NavigateToCreditBalanceRefund(action.loanId))
            }
        }
    }
}

sealed interface LoanDashboardAction {
    data object OnRetry : LoanDashboardAction
    data object NavigateBack : LoanDashboardAction
    data class NavigateToTransactions(val loanId: Int) : LoanDashboardAction
    data object TogglePrincipalVisibility : LoanDashboardAction
    data object ToggleInterestVisibility : LoanDashboardAction
    data class RejectLoan(val loanId: Int) : LoanDashboardAction
    data class ApproveLoan(val loanId: Int) : LoanDashboardAction
    data class UndoApproval(val loanId: Int) : LoanDashboardAction
    data class DisburseLoan(val loanId: Int) : LoanDashboardAction
    data class MakeRepayment(val loanId: Int) : LoanDashboardAction
    data class CreditBalanceRefund(val loanId: Int) : LoanDashboardAction
}

sealed interface LoanDashboardEvent {
    data object NavigateBack : LoanDashboardEvent
    data class NavigateToTransactions(val loanId: Int) : LoanDashboardEvent
    data class NavigateToRejectLoan(val loanId: Int) : LoanDashboardEvent
    data class NavigateToApproveLoan(val loanId: Int) : LoanDashboardEvent
    data class NavigateToUndoApproval(val loanId: Int) : LoanDashboardEvent
    data class NavigateToDisburseLoan(val loanId: Int) : LoanDashboardEvent
    data class NavigateToMakeRepayment(val loanId: Int) : LoanDashboardEvent
    data class NavigateToCreditBalanceRefund(val loanId: Int) : LoanDashboardEvent
}
