/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDisburse

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_disburse_amount_greater_than_zero
import androidclient.feature.loan.generated.resources.feature_loan_disburse_failed
import androidclient.feature.loan.generated.resources.feature_loan_disburse_failed_to_load_template
import androidclient.feature.loan.generated.resources.feature_loan_disburse_success
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.common.utils.DateHelper.getDateAsLongFromList
import com.mifos.core.domain.useCases.loanDisburse.GetLoanDisburseTemplateUseCase
import com.mifos.core.domain.useCases.loanDisburse.LoanDisburseUseCase
import com.mifos.core.model.objects.account.loan.loanDisburse.LoanDisburseInput
import com.mifos.core.model.objects.account.loan.loanDisburse.LoanDisburseTemplate
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

internal class LoanDisburseViewModel(
    private val getTemplateUseCase: GetLoanDisburseTemplateUseCase,
    private val disburseUseCase: LoanDisburseUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LoanDisburseState, LoanDisburseEvent, LoanDisburseAction>(
    initialState = LoanDisburseState(loanId = savedStateHandle.toRoute<LoanDisburseRoute>().loanId),
) {
    private val route = savedStateHandle.toRoute<LoanDisburseRoute>()

    init {
        loadTemplate()
    }

    private fun loadTemplate() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(viewState = LoanDisburseState.ViewState.Loading) }
            val result = getTemplateUseCase(route.loanId)
            sendAction(LoanDisburseAction.Internal.ReceiveTemplateResult(result))
        }
    }

    private fun submitDisburse() {
        val currentState = mutableStateFlow.value

        viewModelScope.launch {
            mutableStateFlow.update { it.copy(isSubmitting = true) }
            val input = LoanDisburseInput(
                actualDisbursementDate = DateHelper.getDateAsStringFromLong(currentState.disbursedDate),
                transactionAmount = currentState.transactionAmount,
                paymentTypeId = currentState.selectedPaymentType?.id,
                note = currentState.note.ifEmpty { null },
                externalId = currentState.externalId.toIntOrNull(),
                accountNumber = currentState.accountNumber.ifEmpty { null },
                checkNumber = currentState.chequeNumber.ifEmpty { null },
                routingCode = currentState.routingCode.ifEmpty { null },
                receiptNumber = currentState.receiptNumber.ifEmpty { null },
                bankNumber = currentState.bankNumber.ifEmpty { null },
            )
            val result = disburseUseCase(route.loanId, input)
            sendAction(LoanDisburseAction.Internal.ReceiveDisburseResult(result))
        }
    }

    private fun handleTemplateResult(result: DataState<LoanDisburseTemplate>) {
        when (result) {
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(viewState = LoanDisburseState.ViewState.Error(Res.string.feature_loan_disburse_failed_to_load_template))
                }
            }

            DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(viewState = LoanDisburseState.ViewState.Loading)
                }
            }

            is DataState.Success -> {
                val template = result.data
                val minDate = getDateAsLongFromList(template.date) ?: 0L
                mutableStateFlow.update {
                    it.copy(
                        viewState = LoanDisburseState.ViewState.Success(template),
                        transactionAmount = template.netDisbursalAmount,
                        netDisbursalAmount = template.netDisbursalAmount,
                        availableAmount = template.netDisbursalAmount.toString(),
                        paymentTypes = template.paymentTypeOptions,
                        disbursedDateText = DateHelper.getDateAsStringFromLong(it.disbursedDate),
                        minDisbursementDate = minDate,
                    )
                }
            }
        }
    }

    private fun handleDisburseResult(result: DataState<Unit>) {
        when (result) {
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        isSubmitting = false,
                        dialogMessage = Res.string.feature_loan_disburse_failed,
                    )
                }
            }

            DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(isSubmitting = true)
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        isSubmitting = false,
                        dialogMessage = Res.string.feature_loan_disburse_success,
                        isDisburseSuccessful = true,
                    )
                }
            }
        }
    }

    override fun handleAction(action: LoanDisburseAction) {
        when (action) {
            LoanDisburseAction.NavigateBack -> sendEvent(LoanDisburseEvent.NavigateBack)
            LoanDisburseAction.OnRetry -> loadTemplate()
            LoanDisburseAction.DismissDialog -> handleDismissDialog()
            LoanDisburseAction.TogglePaymentDetails -> {
                mutableStateFlow.update { it.copy(showPaymentDetails = !it.showPaymentDetails) }
            }

            is LoanDisburseAction.DisbursedDateChanged ->
                mutableStateFlow.update {
                    it.copy(
                        disbursedDate = action.dateMillis,
                        disbursedDateText = DateHelper.getDateAsStringFromLong(action.dateMillis),
                    )
                }

            is LoanDisburseAction.TransactionAmountChanged -> {
                val error = validateTransactionAmount(action.amount)
                mutableStateFlow.update {
                    it.copy(
                        transactionAmount = action.amount,
                        transactionAmountError = error,
                    )
                }
            }

            is LoanDisburseAction.ExternalIdChanged ->
                mutableStateFlow.update { it.copy(externalId = action.value) }

            is LoanDisburseAction.PaymentTypeSelected -> handlePaymentTypeSelection(action.index)

            is LoanDisburseAction.AccountNumberChanged ->
                mutableStateFlow.update { it.copy(accountNumber = action.value) }

            is LoanDisburseAction.ChequeNumberChanged ->
                mutableStateFlow.update { it.copy(chequeNumber = action.value) }

            is LoanDisburseAction.RoutingCodeChanged ->
                mutableStateFlow.update { it.copy(routingCode = action.value) }

            is LoanDisburseAction.ReceiptNumberChanged ->
                mutableStateFlow.update { it.copy(receiptNumber = action.value) }

            is LoanDisburseAction.BankNumberChanged ->
                mutableStateFlow.update { it.copy(bankNumber = action.value) }

            is LoanDisburseAction.NoteChanged ->
                mutableStateFlow.update { it.copy(note = action.value) }

            LoanDisburseAction.ShowDatePicker ->
                mutableStateFlow.update { it.copy(showDatePicker = true) }

            LoanDisburseAction.HideDatePicker ->
                mutableStateFlow.update { it.copy(showDatePicker = false) }

            LoanDisburseAction.Submit -> submitDisburse()

            is LoanDisburseAction.Internal.ReceiveTemplateResult -> handleTemplateResult(action.result)
            is LoanDisburseAction.Internal.ReceiveDisburseResult -> handleDisburseResult(action.result)
        }
    }

    private fun handleDismissDialog() {
        val currentState = mutableStateFlow.value

        mutableStateFlow.update {
            it.copy(
                dialogMessage = null,
            )
        }

        if (currentState.isDisburseSuccessful) {
            sendEvent(LoanDisburseEvent.DisburseSuccess)
        }
    }

    private fun handlePaymentTypeSelection(index: Int) {
        val paymentTypes = mutableStateFlow.value.paymentTypes
        if (index in paymentTypes.indices) {
            val selected = paymentTypes[index]
            mutableStateFlow.update {
                it.copy(selectedPaymentType = selected)
            }
        }
    }

    private fun validateTransactionAmount(amount: Double): StringResource? {
        return if (amount <= 0) {
            Res.string.feature_loan_disburse_amount_greater_than_zero
        } else {
            null
        }
    }
}

sealed interface LoanDisburseEvent {
    data object NavigateBack : LoanDisburseEvent
    data object DisburseSuccess : LoanDisburseEvent
}

sealed interface LoanDisburseAction {
    data object NavigateBack : LoanDisburseAction
    data object OnRetry : LoanDisburseAction
    data object DismissDialog : LoanDisburseAction
    data class DisbursedDateChanged(val dateMillis: Long) : LoanDisburseAction
    data class TransactionAmountChanged(val amount: Double) : LoanDisburseAction
    data class ExternalIdChanged(val value: String) : LoanDisburseAction
    data class PaymentTypeSelected(val index: Int) : LoanDisburseAction
    data object TogglePaymentDetails : LoanDisburseAction
    data class AccountNumberChanged(val value: String) : LoanDisburseAction
    data class ChequeNumberChanged(val value: String) : LoanDisburseAction
    data class RoutingCodeChanged(val value: String) : LoanDisburseAction
    data class ReceiptNumberChanged(val value: String) : LoanDisburseAction
    data class BankNumberChanged(val value: String) : LoanDisburseAction
    data class NoteChanged(val value: String) : LoanDisburseAction
    data object ShowDatePicker : LoanDisburseAction
    data object HideDatePicker : LoanDisburseAction
    data object Submit : LoanDisburseAction

    sealed interface Internal : LoanDisburseAction {
        data class ReceiveTemplateResult(
            val result: DataState<LoanDisburseTemplate>,
        ) : Internal

        data class ReceiveDisburseResult(
            val result: DataState<Unit>,
        ) : Internal
    }
}
