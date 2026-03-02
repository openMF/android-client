/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
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
import androidclient.feature.loan.generated.resources.feature_loan_profile_status_active
import androidclient.feature.loan.generated.resources.feature_loan_profile_status_overpaid
import androidclient.feature.loan.generated.resources.feature_loan_profile_status_pending
import androidclient.feature.loan.generated.resources.feature_loan_profile_status_unknown
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.loan.loanAccountProfile.components.LoanAccountProfileActionItem
import com.mifos.room.entities.accounts.loans.LoanStatusEntity
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
                        val currentStatus = loan.status.toProfileStatus()

                        mutableStateFlow.update {
                            it.copy(
                                loanAccount = loan,
                                dialogState = null,
                                statusUiModel = calculateStatusUi(currentStatus),
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

    private fun calculateNextActionResource(status: LoanProfileStatus): StringResource {
        return when (status) {
            LoanProfileStatus.PENDING -> Res.string.feature_loan_profile_action_approve
            LoanProfileStatus.OVERPAID -> Res.string.feature_loan_profile_action_transfer
            LoanProfileStatus.ACTIVE -> Res.string.feature_loan_profile_action_repayment
            LoanProfileStatus.UNKNOWN -> Res.string.feature_loan_profile_action_view
        }
    }

    private fun calculateStatusUi(status: LoanProfileStatus): LoanStatusUiModel {
        return when (status) {
            LoanProfileStatus.ACTIVE -> LoanStatusUiModel(Res.string.feature_loan_profile_status_active, AppColors.loanActiveStatus)
            LoanProfileStatus.PENDING -> LoanStatusUiModel(Res.string.feature_loan_profile_status_pending, AppColors.loanPendingStatus)
            LoanProfileStatus.OVERPAID -> LoanStatusUiModel(Res.string.feature_loan_profile_status_overpaid, AppColors.loanOverpaidStatus)
            LoanProfileStatus.UNKNOWN -> LoanStatusUiModel(Res.string.feature_loan_profile_status_unknown, AppColors.loanUnknownStatus)
        }
    }

    override fun handleAction(action: LoanAccountAction) {
        when (action) {
            LoanAccountAction.NavigateBack -> sendEvent(LoanAccountEvent.NavigateBack)
            LoanAccountAction.OnRetry -> if (stateFlow.value.networkConnection) {
                loadLoanAccountDetails(route.loanId)
            } else {
                mutableStateFlow.update {
                    it.copy(dialogState = LoanAccountState.DialogState.Error(Res.string.feature_loan_profile_error_details_not_found))
                }
            }
            LoanAccountAction.OnNextActionClick -> handleNextAction()
            is LoanAccountAction.OnDetailItemClick -> sendEvent(LoanAccountEvent.NavigateToDetail(action.item))
            LoanAccountAction.OnAccountClick -> sendEvent(LoanAccountEvent.NavigateToAccountDetails)
        }
    }

    private fun handleNextAction() {
        val account = mutableStateFlow.value.loanAccount ?: return

        when (account.status.toProfileStatus()) {
            LoanProfileStatus.PENDING -> sendEvent(LoanAccountEvent.NavigateToAction(LoanProfileAction.Approve))
            LoanProfileStatus.OVERPAID -> sendEvent(LoanAccountEvent.NavigateToAction(LoanProfileAction.Transfer))
            LoanProfileStatus.ACTIVE -> sendEvent(LoanAccountEvent.NavigateToAction(LoanProfileAction.Repayment))
            LoanProfileStatus.UNKNOWN -> sendEvent(LoanAccountEvent.NavigateToAccountDetails)
        }
    }

    private fun LoanStatusEntity?.toProfileStatus(): LoanProfileStatus {
        if (this == null) return LoanProfileStatus.UNKNOWN
        return when {
            this.pendingApproval == true -> LoanProfileStatus.PENDING
            this.overpaid == true -> LoanProfileStatus.OVERPAID
            this.active == true -> LoanProfileStatus.ACTIVE
            else -> LoanProfileStatus.UNKNOWN
        }
    }
}

enum class LoanProfileStatus {
    ACTIVE,
    PENDING,
    OVERPAID,
    UNKNOWN,
}

data class LoanAccountState(
    val loanAccount: LoanWithAssociationsEntity? = null,
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
    val statusUiModel: LoanStatusUiModel? = null,
    val nextActionButtonRes: StringResource = Res.string.feature_loan_profile_action_view,
) {
    sealed interface DialogState {
        data class Error(val message: StringResource) : DialogState
        data object Loading : DialogState
    }
}

data class LoanStatusUiModel(
    val labelRes: StringResource,
    val color: Color,
)

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
}

sealed interface LoanAccountAction {
    data object NavigateBack : LoanAccountAction
    data object OnRetry : LoanAccountAction
    data object OnNextActionClick : LoanAccountAction
    data class OnDetailItemClick(val item: LoanAccountProfileActionItem) : LoanAccountAction
    data object OnAccountClick : LoanAccountAction
}
