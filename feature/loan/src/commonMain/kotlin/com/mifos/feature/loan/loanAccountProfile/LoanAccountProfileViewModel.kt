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
import androidclient.feature.loan.generated.resources.feature_loan_profile_status_active
import androidclient.feature.loan.generated.resources.feature_loan_profile_status_overpaid
import androidclient.feature.loan.generated.resources.feature_loan_profile_status_pending
import androidclient.feature.loan.generated.resources.feature_loan_profile_status_unknown
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.Constants
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

/**
 * ViewModel for the Loan Account Profile screen.
 *
 * This ViewModel manages the loading and display of a loan's summary and its associations.
 * It also handles navigation to various loan actions (Approve, Repayment, Transfer, etc.)
 * and observes network status to show error states.
 *
 * @property savedStateHandle Handle to saved state for this ViewModel.
 * @property networkMonitor Utility to observe network connectivity.
 * @property loanRepository Repository to fetch loan account summary details.
 */
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

    /**
     * Observes the network connectivity and navigation results.
     *
     * Triggers a data load when the device comes online if data is missing.
     * Also observes [Constants.LOAN_CLOSED] from [SavedStateHandle] to refresh the profile
     * after a successful loan closure.
     */
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

        viewModelScope.launch {
            savedStateHandle.getStateFlow(Constants.LOAN_CLOSED, false)
                .collect { isClosed ->
                    if (isClosed) {
                        loadLoanAccountDetails(route.loanId)
                        savedStateHandle[Constants.LOAN_CLOSED] = false
                    }
                }
        }
    }

    /**
     * Loads the loan account details from the repository.
     *
     * @param loanId The unique identifier of the loan account.
     */
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
            LoanAccountAction.OnRefresh -> loadLoanAccountDetails(route.loanId)
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

/**
 * Represents the status of a loan in the profile context.
 */
enum class LoanProfileStatus {
    /** The loan is active and current. */
    ACTIVE,
    /** The loan is awaiting approval. */
    PENDING,
    /** The loan has been overpaid. */
    OVERPAID,
    /** The status is unknown or unsupported. */
    UNKNOWN,
}

/**
 * UI state for the Loan Account Profile screen.
 *
 * @property loanAccount The loan account details, or null if not yet loaded.
 * @property dialogState Current modal dialog state (Loading, Error, or null).
 * @property networkConnection True if the device has an active network connection.
 * @property statusUiModel Visual representation of the loan status (label and color).
 * @property nextActionButtonRes The string resource for the primary action button.
 */
data class LoanAccountState(
    val loanAccount: LoanWithAssociationsEntity? = null,
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
    val statusUiModel: LoanStatusUiModel? = null,
    val nextActionButtonRes: StringResource = Res.string.feature_loan_profile_action_view,
) {
    /**
     * Dialog states for the profile screen.
     */
    sealed interface DialogState {
        /**
         * Error state with a displayable message.
         * @property message The error message resource.
         */
        data class Error(val message: StringResource) : DialogState

        /** Loading state shown during initial fetch. */
        data object Loading : DialogState
    }
}

/**
 * UI model for the loan status badge.
 * @property labelRes The string resource for the status label.
 * @property color The color to use for the status badge.
 */
data class LoanStatusUiModel(
    val labelRes: StringResource,
    val color: Color,
)

/** Actions that can be performed on a loan profile. */
sealed interface LoanProfileAction {
    /** Approve a pending loan application. */
    data object Approve : LoanProfileAction
    /** Make a repayment. */
    data object Repayment : LoanProfileAction
    /** Perform an account transfer. */
    data object Transfer : LoanProfileAction
    /** Close an active loan. */
    data object CloseLoan : LoanProfileAction
}

/** Events emitted by the [LoanAccountProfileViewModel]. */
sealed interface LoanAccountEvent {
    /** Navigate back to the previous screen. */
    data object NavigateBack : LoanAccountEvent
    /** Navigate to a specific loan action screen. @property action The action to perform. */
    data class NavigateToAction(val action: LoanProfileAction) : LoanAccountEvent
    /** Navigate to a detail sub-screen. @property detailItem The detail item to show. */
    data class NavigateToDetail(val detailItem: LoanAccountProfileActionItem) : LoanAccountEvent
    /** Navigate to the full account details screen. */
    data object NavigateToAccountDetails : LoanAccountEvent
}

/** User actions handled by the [LoanAccountProfileViewModel]. */
sealed interface LoanAccountAction {
    /** User tapped back. */
    data object NavigateBack : LoanAccountAction
    /** User tapped retry on an error state. */
    data object OnRetry : LoanAccountAction
    /** Programmatic or user-triggered refresh of data. */
    data object OnRefresh : LoanAccountAction
    /** User tapped the primary "Next Action" button. */
    data object OnNextActionClick : LoanAccountAction
    /** User tapped a detail item (Transactions, Charges, etc.). @property item The item tapped. */
    data class OnDetailItemClick(val item: LoanAccountProfileActionItem) : LoanAccountAction
    /** User tapped the loan account card for more details. */
    data object OnAccountClick : LoanAccountAction
}
