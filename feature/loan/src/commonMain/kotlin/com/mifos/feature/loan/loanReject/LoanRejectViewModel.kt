/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanReject

import kpt.feature.loan.generated.resources.Res
import kpt.feature.loan.generated.resources.feature_loan_reject_failed
import kpt.feature.loan.generated.resources.feature_loan_reject_success
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.domain.useCases.loanReject.RejectLoanUseCase
import com.mifos.core.model.objects.account.loan.RejectLoanInput
import kpt.core.base.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class LoanRejectViewModel(
    private val rejectLoanUseCase: RejectLoanUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LoanRejectState, LoanRejectEvent, LoanRejectAction>(
    initialState = LoanRejectState(loanId = savedStateHandle.toRoute<LoanRejectRoute>().loanId),
) {
    private val route = savedStateHandle.toRoute<LoanRejectRoute>()

    private fun submitRejectLoan() {
        val currentState = mutableStateFlow.value
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(isSubmitting = true) }
            val request = RejectLoanInput(
                rejectedOnDate = DateHelper.getDateAsStringFromLong(currentState.rejectedOnDate),
                note = currentState.note.ifBlank { null },
            )
            val result = rejectLoanUseCase(route.loanId, request)
            sendAction(LoanRejectAction.Internal.ReceiveRejectResult(result))
        }
    }

    private fun handleRejectResult(result: DataState<Unit>) {
        when (result) {
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        isSubmitting = false,
                        dialogMessage = Res.string.feature_loan_reject_failed,
                    )
                }
            }

            DataState.Loading -> Unit

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        isSubmitting = false,
                        dialogMessage = Res.string.feature_loan_reject_success,
                        isRejectSuccessful = true,
                    )
                }
            }
        }
    }

    private fun handleDismissDialog() {
        val currentState = mutableStateFlow.value
        mutableStateFlow.update { it.copy(dialogMessage = null) }
        if (currentState.isRejectSuccessful) {
            sendEvent(LoanRejectEvent.RejectSuccess)
        }
    }

    override fun handleAction(action: LoanRejectAction) {
        when (action) {
            LoanRejectAction.NavigateBack -> sendEvent(LoanRejectEvent.NavigateBack)
            LoanRejectAction.Submit -> submitRejectLoan()
            LoanRejectAction.DismissDialog -> handleDismissDialog()
            LoanRejectAction.ShowDatePicker -> mutableStateFlow.update { it.copy(showDatePicker = true) }
            LoanRejectAction.HideDatePicker -> mutableStateFlow.update { it.copy(showDatePicker = false) }
            is LoanRejectAction.NoteChanged -> mutableStateFlow.update { it.copy(note = action.value) }
            is LoanRejectAction.RejectedOnDateChanged -> mutableStateFlow.update {
                it.copy(
                    rejectedOnDate = action.dateMillis,
                    rejectedOnDateText = DateHelper.getDateAsStringFromLong(action.dateMillis),
                )
            }

            is LoanRejectAction.Internal.ReceiveRejectResult -> handleRejectResult(action.result)
        }
    }
}

sealed interface LoanRejectEvent {
    data object NavigateBack : LoanRejectEvent
    data object RejectSuccess : LoanRejectEvent
}

sealed interface LoanRejectAction {
    data object NavigateBack : LoanRejectAction
    data object Submit : LoanRejectAction
    data object DismissDialog : LoanRejectAction
    data object ShowDatePicker : LoanRejectAction
    data object HideDatePicker : LoanRejectAction
    data class NoteChanged(val value: String) : LoanRejectAction
    data class RejectedOnDateChanged(val dateMillis: Long) : LoanRejectAction

    sealed interface Internal : LoanRejectAction {
        data class ReceiveRejectResult(
            val result: DataState<Unit>,
        ) : Internal
    }
}
