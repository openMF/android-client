/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountAction

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_action_error_details_not_found
import androidclient.feature.loan.generated.resources.feature_loan_action_failed_to_load_loan_actions
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_network_not_available
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.loan.loanUtils.LoanStatus
import com.mifos.feature.loan.loanUtils.getLoanStatus
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

internal class LoanAccountActionsViewModel(
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val loanRepository: LoanAccountSummaryRepository,
) : BaseViewModel<LoanAccountActionsState, LoanAccountActionsEvent, LoanAccountActionsAction>(
    initialState = LoanAccountActionsState(),
) {
    private val route = savedStateHandle.toRoute<LoanAccountActionRoute>()
    private var loadJob: Job? = null

    init {
        observeNetworkAndLoad()
    }

    private fun observeNetworkAndLoad() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
                if (isConnected) {
                    loadLoanAccountDetails(route.loanId)
                } else {
                    mutableStateFlow.update {
                        it.copy(
                            viewState = LoanAccountActionsState.ViewState.Error(Res.string.feature_loan_profile_error_network_not_available),
                        )
                    }
                }
            }
        }
    }

    private fun loadLoanAccountDetails(loanId: Int) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            loanRepository.getLoanById(loanId).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                viewState = LoanAccountActionsState.ViewState.Error(Res.string.feature_loan_action_failed_to_load_loan_actions),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                viewState = LoanAccountActionsState.ViewState.Loading,
                            )
                        }
                    }

                    is DataState.Success -> {
                        val loan = result.data
                        if (loan == null) {
                            mutableStateFlow.update {
                                it.copy(
                                    viewState = LoanAccountActionsState.ViewState.Error(Res.string.feature_loan_action_error_details_not_found),
                                )
                            }
                            return@collect
                        }
                        val status = loan.status.getLoanStatus()
                        val flags = calculateFlags(loan, status)
                        val actions = generateActions(status, flags)
                        mutableStateFlow.update {
                            it.copy(
                                viewState = LoanAccountActionsState.ViewState.Content(actions),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun calculateFlags(
        loan: LoanWithAssociationsEntity,
        status: LoanStatus,
    ): LoanFeatureFlags {
        var isLoanReAged = false
        var isLoanReAmortized = false
        var disbursementCount = 0

        loan.transactions.forEach { lt ->
            if (lt.manuallyReversed == false) {
                if (status == LoanStatus.ACTIVE) {
                    if (lt.type?.reAge == true) {
                        isLoanReAged = true
                    } else if (lt.type?.reAmortize == true) {
                        isLoanReAmortized = true
                    }
                }

                if (lt.type?.disbursement == true) {
                    disbursementCount++
                }
            }
        }

        return LoanFeatureFlags(
            enableBuyDownFee = loan.enableBuyDownFee,
            enableIncomeCapitalization = loan.enableIncomeCapitalization,
            multiDisburseLoan = loan.multiDisburseLoan,
            canDisburse = loan.canDisburse,
            moreThanOneDisbursement = disbursementCount > 1,
            recalculateInterest = loan.isInterestRecalculationEnabled,
            chargedOff = loan.chargedOff,
            loanReAged = isLoanReAged,
            loanReAmortized = isLoanReAmortized,
            isVariableInstallmentsAllowed = loan.allowPartialPeriodInterestCalculation,
            advancedPaymentAllocationStrategy = loan.syncDisbursementWithMeeting,
            canAssignLoanOfficer = loan.loanOfficerName.isBlank(),
        )
    }

    private fun generateActions(
        status: LoanStatus,
        flags: LoanFeatureFlags,
    ): List<LoanAccountActionItem> {
        val generatedActions = mutableListOf<LoanAccountActionItem>()

        when (status) {
            LoanStatus.ACTIVE -> {
                generatedActions.add(LoanAccountActionItem.AddLoanCharge)
                generatedActions.add(LoanAccountActionItem.Foreclosure)
                generatedActions.add(LoanAccountActionItem.MakeRepayment)
                generatedActions.add(LoanAccountActionItem.UndoDisbursal)

                if (flags.recalculateInterest) {
                    generatedActions.add(LoanAccountActionItem.AddInterestPause)
                }

                if (flags.canAssignLoanOfficer) {
                    generatedActions.add(LoanAccountActionItem.AssignLoanOfficer)
                } else {
                    generatedActions.add(LoanAccountActionItem.ChangeLoanOfficer)
                }

                if (flags.recalculateInterest) {
                    generatedActions.add(LoanAccountActionItem.PrepayLoan)
                }

                generatedActions.add(LoanAccountActionItem.ChargeOff)

                if (flags.loanReAged) {
                    generatedActions.add(LoanAccountActionItem.UndoReAge)
                } else {
                    generatedActions.add(LoanAccountActionItem.ReAge)
                }

                if (flags.loanReAmortized) {
                    generatedActions.add(LoanAccountActionItem.UndoReAmortize)
                } else {
                    generatedActions.add(LoanAccountActionItem.ReAmortize)
                }

                generatedActions.add(LoanAccountActionItem.Payments)

                generatedActions.add(LoanAccountActionItem.WaiveInterest)
                generatedActions.add(LoanAccountActionItem.Reschedule)
                generatedActions.add(LoanAccountActionItem.WriteOff)
                generatedActions.add(LoanAccountActionItem.CloseAsRescheduled)
                generatedActions.add(LoanAccountActionItem.Close)
                generatedActions.add(LoanAccountActionItem.LoanScreenReport)
                generatedActions.add(LoanAccountActionItem.ViewGuarantors)
                generatedActions.add(LoanAccountActionItem.CreateGuarantors)
                generatedActions.add(LoanAccountActionItem.RecoverFromGuarantor)
                generatedActions.add(LoanAccountActionItem.SellLoan)
                generatedActions.add(LoanAccountActionItem.ContractTermination)

                if (flags.enableBuyDownFee) {
                    generatedActions.add(LoanAccountActionItem.BuyDownFee)
                }

                if (flags.enableIncomeCapitalization) {
                    generatedActions.add(LoanAccountActionItem.CapitalizedIncome)
                }

                if (flags.multiDisburseLoan || flags.canDisburse) {
                    generatedActions.add(LoanAccountActionItem.Disburse)
                }

                if (flags.moreThanOneDisbursement && flags.multiDisburseLoan) {
                    generatedActions.add(LoanAccountActionItem.UndoLastDisbursal)
                }
            }

            LoanStatus.PENDING_APPROVAL -> {
                generatedActions.add(LoanAccountActionItem.AddLoanCharge)
                generatedActions.add(LoanAccountActionItem.Approve)
                generatedActions.add(LoanAccountActionItem.ModifyApplication)
                generatedActions.add(LoanAccountActionItem.Reject)

                generatedActions.add(LoanAccountActionItem.WithdrawnByClient)
                generatedActions.add(LoanAccountActionItem.Delete)
                generatedActions.add(LoanAccountActionItem.AddCollateral)
                generatedActions.add(LoanAccountActionItem.ViewGuarantors)
                generatedActions.add(LoanAccountActionItem.CreateGuarantors)
                generatedActions.add(LoanAccountActionItem.LoanScreenReport)

                if (flags.canAssignLoanOfficer) {
                    generatedActions.add(LoanAccountActionItem.AssignLoanOfficer)
                } else {
                    generatedActions.add(LoanAccountActionItem.ChangeLoanOfficer)
                }

                if (flags.isVariableInstallmentsAllowed) {
                    generatedActions.add(LoanAccountActionItem.EditRepaymentSchedule)
                }
            }

            LoanStatus.APPROVED -> {
                generatedActions.add(LoanAccountActionItem.Disburse)
                generatedActions.add(LoanAccountActionItem.DisburseToSavings)
                generatedActions.add(LoanAccountActionItem.UndoApproval)

                if (flags.canAssignLoanOfficer) {
                    generatedActions.add(LoanAccountActionItem.AssignLoanOfficer)
                } else {
                    generatedActions.add(LoanAccountActionItem.ChangeLoanOfficer)
                }

                generatedActions.add(LoanAccountActionItem.AddLoanCharge)
                generatedActions.add(LoanAccountActionItem.ViewGuarantors)
                generatedActions.add(LoanAccountActionItem.CreateGuarantors)
                generatedActions.add(LoanAccountActionItem.LoanScreenReport)

                if (flags.isVariableInstallmentsAllowed) {
                    generatedActions.add(LoanAccountActionItem.EditRepaymentSchedule)
                }
            }

            LoanStatus.CLOSED_OVERPAID -> {
                generatedActions.add(LoanAccountActionItem.TransferFunds)
                generatedActions.add(LoanAccountActionItem.CreditBalanceRefund)

                if (flags.multiDisburseLoan) {
                    generatedActions.add(LoanAccountActionItem.Disburse)
                }

                if (flags.advancedPaymentAllocationStrategy) {
                    generatedActions.add(LoanAccountActionItem.Reschedule)
                }
            }

            LoanStatus.CLOSED_WRITTEN_OFF -> {
                generatedActions.add(LoanAccountActionItem.RecoveryPayment)
                generatedActions.add(LoanAccountActionItem.UndoWriteOff)
            }

            LoanStatus.CLOSED_OBLIGATIONS_MET -> {
                generatedActions.add(LoanAccountActionItem.GoodwillCredit)
                generatedActions.add(LoanAccountActionItem.InterestPaymentWaiver)
                generatedActions.add(LoanAccountActionItem.PaymentRefund)
                generatedActions.add(LoanAccountActionItem.MerchantIssuedRefund)

                if (flags.multiDisburseLoan) {
                    generatedActions.add(LoanAccountActionItem.Disburse)
                }

                if (flags.advancedPaymentAllocationStrategy) {
                    generatedActions.add(LoanAccountActionItem.Reschedule)
                }
            }

            LoanStatus.WITHDRAWN_BY_APPLICANT -> Unit
            LoanStatus.CLOSED_RESCHEDULED -> Unit
            LoanStatus.REJECTED -> Unit
        }
        return generatedActions
    }

    override fun handleAction(action: LoanAccountActionsAction) {
        when (action) {
            LoanAccountActionsAction.NavigateBack -> sendEvent(LoanAccountActionsEvent.NavigateBack)
            is LoanAccountActionsAction.OnLoanAccountActionItemClick -> {
                sendEvent(LoanAccountActionsEvent.NavigateToAction(action.action, route.loanId))
            }

            LoanAccountActionsAction.OnRetry -> {
                if (stateFlow.value.networkConnection) {
                    loadLoanAccountDetails(route.loanId)
                } else {
                    mutableStateFlow.update {
                        it.copy(viewState = LoanAccountActionsState.ViewState.Error(Res.string.feature_loan_profile_error_network_not_available))
                    }
                }
            }
        }
    }
}

@Serializable
data class LoanFeatureFlags(
    val enableBuyDownFee: Boolean = false,
    val enableIncomeCapitalization: Boolean = false,
    val multiDisburseLoan: Boolean = false,
    val canDisburse: Boolean = false,
    val moreThanOneDisbursement: Boolean = false,
    val recalculateInterest: Boolean = false,
    val chargedOff: Boolean = false,
    val loanReAged: Boolean = false,
    val loanReAmortized: Boolean = false,
    val isVariableInstallmentsAllowed: Boolean = false,
    val advancedPaymentAllocationStrategy: Boolean = false,
    val canAssignLoanOfficer: Boolean = false,
)

data class LoanAccountActionsState(
    val viewState: ViewState = ViewState.Loading,
    val networkConnection: Boolean = false,
) {
    sealed interface ViewState {
        data class Error(val message: StringResource) : ViewState
        data object Loading : ViewState
        data class Content(val actions: List<LoanAccountActionItem>) : ViewState
        data object Empty : ViewState
    }
}

sealed interface LoanAccountActionsEvent {
    data object NavigateBack : LoanAccountActionsEvent
    data class NavigateToAction(val action: LoanAccountActionItem, val loanId: Int) :
        LoanAccountActionsEvent
}

sealed interface LoanAccountActionsAction {
    data object NavigateBack : LoanAccountActionsAction
    data class OnLoanAccountActionItemClick(val action: LoanAccountActionItem) :
        LoanAccountActionsAction

    data object OnRetry : LoanAccountActionsAction
}
