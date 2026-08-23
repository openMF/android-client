/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanChargeOff

import kpt.feature.loan.generated.resources.Res
import kpt.feature.loan.generated.resources.feature_loan_charge_off_failed
import kpt.feature.loan.generated.resources.feature_loan_charge_off_failed_to_load_charge_off_reasons
import kpt.feature.loan.generated.resources.feature_loan_charge_off_success
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.domain.useCases.loanChargeOff.GetLoanChargeOffTemplateUseCase
import com.mifos.core.domain.useCases.loanChargeOff.LoanChargeOffUseCase
import com.mifos.core.model.objects.account.loan.ChargeOffReasonOption
import com.mifos.core.model.objects.account.loan.LoanChargeOffInput
import kpt.core.base.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class LoanChargeOffViewModel(
    private val getTemplateUseCase: GetLoanChargeOffTemplateUseCase,
    private val chargeOffUseCase: LoanChargeOffUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LoanChargeOffState, LoanChargeOffEvent, LoanChargeOffAction>(
    initialState = LoanChargeOffState(loanId = savedStateHandle.toRoute<LoanChargeOffRoute>().loanId),
) {
    private val route = savedStateHandle.toRoute<LoanChargeOffRoute>()

    init {
        loadTemplate()
    }

    private fun loadTemplate() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(viewState = LoanChargeOffState.ViewState.Loading) }
            try {
                val result = getTemplateUseCase(route.loanId)
                sendAction(LoanChargeOffAction.Internal.ReceiveTemplateResult(result))
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(viewState = LoanChargeOffState.ViewState.Error(Res.string.feature_loan_charge_off_failed_to_load_charge_off_reasons))
                }
            }
        }
    }

    private fun submitChargeOff() {
        val currentState = mutableStateFlow.value
        if (currentState.selectedReason == null) {
            mutableStateFlow.update { it.copy(isReasonError = true) }
            return
        }

        viewModelScope.launch {
            mutableStateFlow.update { it.copy(isSubmitting = true) }
            val input = LoanChargeOffInput(
                chargeOffReasonId = currentState.selectedReason.id,
                transactionDate = DateHelper.getDateAsStringFromLong(currentState.transactionDate),
                externalId = currentState.externalId.ifEmpty { null },
                note = currentState.note.ifEmpty { null },
            )
            try {
                chargeOffUseCase(route.loanId, input)
                sendAction(LoanChargeOffAction.Internal.ReceiveChargeOffResult)
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        isSubmitting = false,
                        dialogMessage = Res.string.feature_loan_charge_off_failed,
                    )
                }
            }
        }
    }

    private fun handleTemplateResult(result: List<ChargeOffReasonOption>) {
        mutableStateFlow.update {
            it.copy(viewState = LoanChargeOffState.ViewState.Success(result))
        }
    }

    private fun handleChargeOffResult() {
        mutableStateFlow.update {
            it.copy(
                isSubmitting = false,
                dialogMessage = Res.string.feature_loan_charge_off_success,
                isChargeOffSuccessful = true,
            )
        }
    }

    private fun handleReasonSelection(index: Int) {
        val viewState = mutableStateFlow.value.viewState
        if (viewState is LoanChargeOffState.ViewState.Success) {
            val options = viewState.reasonOptions
            if (index in options.indices) {
                val selected = options[index]
                mutableStateFlow.update {
                    it.copy(
                        selectedReason = selected,
                        isReasonError = false,
                    )
                }
            }
        }
    }

    private fun handleDismissDialog() {
        val currentState = mutableStateFlow.value

        mutableStateFlow.update {
            it.copy(
                dialogMessage = null,
            )
        }

        if (currentState.isChargeOffSuccessful) {
            sendEvent(LoanChargeOffEvent.ChargeOffSuccess)
        }
    }

    override fun handleAction(action: LoanChargeOffAction) {
        when (action) {
            LoanChargeOffAction.NavigateBack -> sendEvent(LoanChargeOffEvent.NavigateBack)
            LoanChargeOffAction.OnRetry -> loadTemplate()
            LoanChargeOffAction.DismissDialog -> handleDismissDialog()

            is LoanChargeOffAction.ReasonSelected -> handleReasonSelection(action.index)

            is LoanChargeOffAction.TransactionDateChanged ->
                mutableStateFlow.update {
                    it.copy(
                        transactionDate = action.dateMillis,
                        transactionDateText = DateHelper.getDateAsStringFromLong(
                            action.dateMillis,
                        ),
                    )
                }

            is LoanChargeOffAction.ExternalIdChanged ->
                mutableStateFlow.update { it.copy(externalId = action.value) }

            is LoanChargeOffAction.NoteChanged ->
                mutableStateFlow.update { it.copy(note = action.value) }

            LoanChargeOffAction.ShowDatePicker ->
                mutableStateFlow.update { it.copy(showDatePicker = true) }

            LoanChargeOffAction.HideDatePicker ->
                mutableStateFlow.update { it.copy(showDatePicker = false) }

            LoanChargeOffAction.Submit -> submitChargeOff()

            is LoanChargeOffAction.Internal.ReceiveTemplateResult -> handleTemplateResult(action.result)
            LoanChargeOffAction.Internal.ReceiveChargeOffResult -> handleChargeOffResult()
        }
    }
}

sealed interface LoanChargeOffEvent {
    data object NavigateBack : LoanChargeOffEvent
    data object ChargeOffSuccess : LoanChargeOffEvent
}

sealed interface LoanChargeOffAction {
    data object NavigateBack : LoanChargeOffAction
    data object OnRetry : LoanChargeOffAction
    data object DismissDialog : LoanChargeOffAction
    data class ReasonSelected(val index: Int) : LoanChargeOffAction
    data class TransactionDateChanged(val dateMillis: Long) : LoanChargeOffAction
    data class ExternalIdChanged(val value: String) : LoanChargeOffAction
    data class NoteChanged(val value: String) : LoanChargeOffAction
    data object ShowDatePicker : LoanChargeOffAction
    data object HideDatePicker : LoanChargeOffAction
    data object Submit : LoanChargeOffAction

    sealed interface Internal : LoanChargeOffAction {
        data class ReceiveTemplateResult(
            val result: List<ChargeOffReasonOption>,
        ) : Internal

        data object ReceiveChargeOffResult : Internal
    }
}
