/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDisbursement

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_invalid_amount_error
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_details_not_found
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_network_not_available
import androidclient.feature.loan.generated.resources.feature_loan_profile_failed_to_load_loan
import androidclient.feature.loan.generated.resources.feature_loan_submission_failed
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanAccountDisbursementRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.account.loan.LoanDisbursement
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock

class LoanAccountDisbursementViewModel(
    private val repository: LoanAccountDisbursementRepository,
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<LoanDisbursementState, LoanDisbursementEvent, LoanDisbursementAction>(
    initialState = LoanDisbursementState(),
) {

    private val route = savedStateHandle.toRoute<LoanDisbursementRoute>()
    val loanId = route.loanId

    private var loadJob: Job? = null

    init {
        observeNetworkAndLoad()
    }

    private fun observeNetworkAndLoad() {
        viewModelScope.launch {
            networkMonitor.isOnline
                .distinctUntilChanged()
                .collect { isConnected ->
                    mutableStateFlow.update { it.copy(networkConnection = isConnected) }
                    if (isConnected) {
                        if (mutableStateFlow.value.template == null) {
                            loadLoanTemplate()
                        }
                    } else if (mutableStateFlow.value.template == null) {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = LoanDisbursementState.DialogState.FetchingError(
                                    Res.string.feature_loan_profile_error_network_not_available,
                                ),
                            )
                        }
                    }
                }
        }
    }

    override fun handleAction(action: LoanDisbursementAction) {
        when (action) {
            LoanDisbursementAction.DismissDialog -> mutableStateFlow.update {
                it.copy(dialogState = null)
            }
            LoanDisbursementAction.OnRetry -> {
                if (stateFlow.value.networkConnection) {
                    loadLoanTemplate()
                } else {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = LoanDisbursementState.DialogState.FetchingError(
                                Res.string.feature_loan_profile_error_details_not_found,
                            ),
                        )
                    }
                }
            }

            LoanDisbursementAction.Submit -> validateAndSubmit()

            is LoanDisbursementAction.UpdateDate ->
                mutableStateFlow.update { it.copy(disbursementDate = action.date) }

            is LoanDisbursementAction.UpdateAmount ->
                mutableStateFlow.update { it.copy(amount = action.amount, amountError = null) }

            is LoanDisbursementAction.UpdateExternalId ->
                mutableStateFlow.update { it.copy(externalId = action.id) }

            is LoanDisbursementAction.UpdatePaymentType ->
                mutableStateFlow.update {
                    it.copy(selectedPaymentType = action.paymentType, paymentTypeError = null)
                }

            is LoanDisbursementAction.TogglePaymentDetails ->
                mutableStateFlow.update { it.copy(showPaymentDetails = action.show) }

            is LoanDisbursementAction.UpdateAccountNumber ->
                mutableStateFlow.update { it.copy(accountNumber = action.value) }

            is LoanDisbursementAction.UpdateCheckNumber ->
                mutableStateFlow.update { it.copy(checkNumber = action.value) }

            is LoanDisbursementAction.UpdateRoutingCode ->
                mutableStateFlow.update { it.copy(routingCode = action.value) }

            is LoanDisbursementAction.UpdateReceiptNumber ->
                mutableStateFlow.update { it.copy(receiptNumber = action.value) }

            is LoanDisbursementAction.UpdateBankNumber ->
                mutableStateFlow.update { it.copy(bankNumber = action.value) }

            is LoanDisbursementAction.UpdateNote ->
                mutableStateFlow.update { it.copy(note = action.value) }
        }
    }

    private fun loadLoanTemplate() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.getLoanTransactionTemplate(loanId, APIEndPoint.DISBURSE).collect { result ->
                when (result) {
                    is DataState.Loading -> mutableStateFlow.update {
                        it.copy(dialogState = LoanDisbursementState.DialogState.Loading)
                    }
                    is DataState.Success -> mutableStateFlow.update {
                        val template = result.data
                        if (template == null) {
                            it.copy(
                                dialogState = LoanDisbursementState.DialogState.FetchingError(
                                    Res.string.feature_loan_profile_error_details_not_found,
                                ),
                            )
                        } else {
                            it.copy(
                                dialogState = null,
                                template = template,
                                amount = template.amount?.toString() ?: "",
                                amountError = null,
                            )
                        }
                    }
                    is DataState.Error -> mutableStateFlow.update {
                        it.copy(
                            dialogState = LoanDisbursementState.DialogState.FetchingError(
                                Res.string.feature_loan_profile_failed_to_load_loan,
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun validateAndSubmit() {
        val currentState = stateFlow.value
        var isValid = true
        var amountErr: StringResource? = null

        if (currentState.amount.isNotBlank() && currentState.amount.toDoubleOrNull() == null) {
            amountErr = Res.string.feature_loan_invalid_amount_error
            isValid = false
        }

        if (!isValid) {
            mutableStateFlow.update { it.copy(amountError = amountErr) }
            return
        }

        val formattedDateString = ApiDateFormatter.formatForApi(currentState.disbursementDate)

        val payload = LoanDisbursement(
            actualDisbursementDate = formattedDateString,
            transactionAmount = currentState.amount.toDoubleOrNull(),
            paymentTypeId = currentState.selectedPaymentType?.id,
            externalId = currentState.externalId.takeIf { it.isNotBlank() },
            accountNumber = currentState.accountNumber.takeIf { currentState.showPaymentDetails && it.isNotBlank() },
            checkNumber = currentState.checkNumber.takeIf { currentState.showPaymentDetails && it.isNotBlank() },
            routingCode = currentState.routingCode.takeIf { currentState.showPaymentDetails && it.isNotBlank() },
            receiptNumber = currentState.receiptNumber.takeIf { currentState.showPaymentDetails && it.isNotBlank() },
            bankNumber = currentState.bankNumber.takeIf { currentState.showPaymentDetails && it.isNotBlank() },
            note = currentState.note.takeIf { currentState.showPaymentDetails && it.isNotBlank() },

            dateFormat = ApiDateFormatter.DATE_FORMAT,
            locale = ApiDateFormatter.LOCALE,
        )

        submitDisbursement(payload)
    }

    private fun submitDisbursement(loanDisbursement: LoanDisbursement) {
        viewModelScope.launch {
            repository.disburseLoan(loanId, loanDisbursement).collect { result ->
                when (result) {
                    is DataState.Loading ->
                        mutableStateFlow.update {
                            it.copy(dialogState = LoanDisbursementState.DialogState.Loading)
                        }

                    is DataState.Success -> {
                        mutableStateFlow.update { it.copy(dialogState = null) }
                        sendEvent(LoanDisbursementEvent.NavigateToLoanProfile(loanId))
                    }

                    is DataState.Error -> mutableStateFlow.update {
                        val backendErrorMsg = result.exception.message
                        it.copy(
                            dialogState = LoanDisbursementState.DialogState.ActionError(
                                messageRes = if (backendErrorMsg.isNullOrBlank()) Res.string.feature_loan_submission_failed else null,
                                backendMessage = backendErrorMsg,
                            ),
                        )
                    }
                }
            }
        }
    }
}

data class LoanDisbursementState(
    val template: LoanTransactionTemplate? = null,
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,

    val disbursementDate: Long = Clock.System.now().toEpochMilliseconds(),
    val amount: String = "",
    val amountError: StringResource? = null,
    val externalId: String = "",
    val selectedPaymentType: PaymentTypeOptionEntity? = null,
    val paymentTypeError: StringResource? = null,
    val showPaymentDetails: Boolean = false,

    val accountNumber: String = "",
    val checkNumber: String = "",
    val routingCode: String = "",
    val receiptNumber: String = "",
    val bankNumber: String = "",
    val note: String = "",
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class FetchingError(val messageRes: StringResource) : DialogState
        data class ActionError(val messageRes: StringResource?, val backendMessage: String? = null) : DialogState
    }
}

sealed interface LoanDisbursementAction {
    data object OnRetry : LoanDisbursementAction
    data object DismissDialog : LoanDisbursementAction
    data class UpdateDate(val date: Long) : LoanDisbursementAction
    data class UpdateAmount(val amount: String) : LoanDisbursementAction
    data class UpdateExternalId(val id: String) : LoanDisbursementAction
    data class UpdatePaymentType(val paymentType: PaymentTypeOptionEntity) : LoanDisbursementAction
    data class TogglePaymentDetails(val show: Boolean) : LoanDisbursementAction
    data class UpdateAccountNumber(val value: String) : LoanDisbursementAction
    data class UpdateCheckNumber(val value: String) : LoanDisbursementAction
    data class UpdateRoutingCode(val value: String) : LoanDisbursementAction
    data class UpdateReceiptNumber(val value: String) : LoanDisbursementAction
    data class UpdateBankNumber(val value: String) : LoanDisbursementAction
    data class UpdateNote(val value: String) : LoanDisbursementAction
    data object Submit : LoanDisbursementAction
}

sealed interface LoanDisbursementEvent {
    data object NavigateBack : LoanDisbursementEvent
    data class NavigateToLoanProfile(val loanId: Int) : LoanDisbursementEvent
}
