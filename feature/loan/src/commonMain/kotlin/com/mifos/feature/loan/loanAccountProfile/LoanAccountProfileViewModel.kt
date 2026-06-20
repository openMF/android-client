/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountProfile

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_profile_action_approve
import androidclient.feature.loan.generated.resources.feature_loan_profile_action_repayment
import androidclient.feature.loan.generated.resources.feature_loan_profile_action_transfer
import androidclient.feature.loan.generated.resources.feature_loan_profile_action_view
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_details_not_found
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_network_not_available
import androidclient.feature.loan.generated.resources.feature_loan_profile_failed_to_load_loan
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.loan.loanAccountProfile.LoanAccountEvent.NavigateToDetail
import com.mifos.feature.loan.loanAccountProfile.LoanAccountState.DialogState.Error
import com.mifos.feature.loan.loanAccountProfile.components.LoanAccountProfileActionItem
import com.mifos.feature.loan.utils.LoanStatus
import com.mifos.feature.loan.utils.getLoanStatus
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

internal class LoanAccountProfileViewModel(
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val loanRepository: LoanAccountSummaryRepository,
) : BaseViewModel<LoanAccountState, LoanAccountEvent, LoanAccountAction>(
    initialState = LoanAccountState(),
) {

    private val route = savedStateHandle.toRoute<LoanAccountRoute>()
    private var loadJob: Job? = null

    init {
        observeNetworkAndLoad()
    }

    private fun observeNetworkAndLoad() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
                if (isConnected) {
                    if (mutableStateFlow.value.loanAccount == null) {
                        loadLoanAccountDetails(route.loanId)
                    }
                } else if (mutableStateFlow.value.loanAccount == null) {
                    mutableStateFlow.update {
                        it.copy(dialogState = LoanAccountState.DialogState.Error(Res.string.feature_loan_profile_error_network_not_available))
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
                    is DataState.Success -> {
                        val loan = result.data
                        if (loan == null) {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = LoanAccountState.DialogState.Error(Res.string.feature_loan_profile_error_details_not_found),
                                )
                            }
                            return@collect
                        }
                        val currentStatus = loan.status.getLoanStatus()

                        mutableStateFlow.update {
                            it.copy(
                                loanAccount = loan,
                                dialogState = null,
                                nextActionButtonRes = calculateNextActionResource(currentStatus),
                            )
                        }
                    }
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = LoanAccountState.DialogState.Error(Res.string.feature_loan_profile_failed_to_load_loan))
                        }
                    }
                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = LoanAccountState.DialogState.Loading)
                        }
                    }
                }
            }
        }
    }

    private fun calculateNextActionResource(status: LoanStatus): StringResource {
        return when (status) {
            LoanStatus.PENDING_APPROVAL -> Res.string.feature_loan_profile_action_approve
            LoanStatus.CLOSED_OVERPAID -> Res.string.feature_loan_profile_action_transfer
            LoanStatus.ACTIVE -> Res.string.feature_loan_profile_action_repayment
            else -> Res.string.feature_loan_profile_action_view
        }
    }

    override fun handleAction(action: LoanAccountAction) {
        when (action) {
            LoanAccountAction.NavigateBack -> sendEvent(LoanAccountEvent.NavigateBack)
            LoanAccountAction.OnRetry -> if (stateFlow.value.networkConnection) {
                loadLoanAccountDetails(route.loanId)
            } else {
                mutableStateFlow.update {
                    it.copy(dialogState = Error(Res.string.feature_loan_profile_error_details_not_found))
                }
            }
            LoanAccountAction.OnNextActionClick -> handleNextAction()
            is LoanAccountAction.OnDetailItemClick -> sendEvent(NavigateToDetail(action.item))
            LoanAccountAction.OnAccountClick -> sendEvent(LoanAccountEvent.NavigateToAccountDetails)
            LoanAccountAction.OnArrowClick -> sendEvent(LoanAccountEvent.NavigateToLoanAction)
        }
    }

    private fun handleNextAction() {
        val account = mutableStateFlow.value.loanAccount ?: return

        when (account.status.getLoanStatus()) {
            LoanStatus.PENDING_APPROVAL -> sendEvent(LoanAccountEvent.NavigateToAction(LoanProfileAction.Approve))
            LoanStatus.CLOSED_OVERPAID -> sendEvent(LoanAccountEvent.NavigateToAction(LoanProfileAction.Transfer))
            LoanStatus.ACTIVE -> sendEvent(LoanAccountEvent.NavigateToAction(LoanProfileAction.Repayment))
            else -> sendEvent(LoanAccountEvent.NavigateToAccountDetails)
        }
    }
}

data class LoanAccountState(
    val loanAccount: LoanWithAssociationsEntity? = null,
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
    val nextActionButtonRes: StringResource = Res.string.feature_loan_profile_action_view,
) {
    sealed interface DialogState {
        data class Error(val message: StringResource) : DialogState
        data object Loading : DialogState
    }
}

sealed interface LoanProfileAction {
    data object Approve : LoanProfileAction
    data object Repayment : LoanProfileAction
    data object Transfer : LoanProfileAction
}

sealed interface LoanAccountEvent {
    data object NavigateBack : LoanAccountEvent
    data class NavigateToAction(val action: LoanProfileAction) : LoanAccountEvent
    data class NavigateToDetail(val detailItem: LoanAccountProfileActionItem) : LoanAccountEvent
    data object NavigateToAccountDetails : LoanAccountEvent
    data object NavigateToLoanAction : LoanAccountEvent
}

sealed interface LoanAccountAction {
    data object NavigateBack : LoanAccountAction
    data object OnRetry : LoanAccountAction
    data object OnNextActionClick : LoanAccountAction
    data class OnDetailItemClick(val item: LoanAccountProfileActionItem) : LoanAccountAction
    data object OnAccountClick : LoanAccountAction
    data object OnArrowClick : LoanAccountAction
}
