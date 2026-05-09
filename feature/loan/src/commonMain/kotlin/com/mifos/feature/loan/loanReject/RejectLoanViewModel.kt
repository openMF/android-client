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

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_reject_date_error_future
import androidclient.feature.loan.generated.resources.feature_loan_unknown_error_occured
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.RejectLoanUseCase
import com.mifos.core.model.objects.loan.RejectLoanInput
import com.mifos.core.model.utils.DateConstants
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class RejectLoanViewModel(
    private val rejectLoanUseCase: RejectLoanUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<RejectLoanState, RejectLoanEvent, RejectLoanAction>(
    initialState = RejectLoanState(
        rejectedOnDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    ),
) {

    private val loanId = savedStateHandle.toRoute<LoanRejectScreenRoute>().loanId

    private val initialDate = state.rejectedOnDate

    override fun handleAction(action: RejectLoanAction) {
        when (action) {
            is RejectLoanAction.RejectedOnDateChanged -> {
                mutableStateFlow.update {
                    it.copy(
                        rejectedOnDate = action.date,
                        rejectedOnDateError = null,
                    )
                }
            }

            is RejectLoanAction.NoteChanged -> {
                mutableStateFlow.update { it.copy(note = action.note) }
            }

            RejectLoanAction.SubmitClicked -> submitLoanRejection()

            RejectLoanAction.CancelClicked -> onCancelClicked()

            RejectLoanAction.PreventAccidentalBackConfirmed -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
                sendEvent(RejectLoanEvent.NavigateBack)
            }

            RejectLoanAction.DismissDialog -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
            }

            RejectLoanAction.DismissSuccessDialog -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
                sendEvent(RejectLoanEvent.NavigateBackWithSuccess)
            }

            is RejectLoanAction.Internal.ReceiveRejectResult -> handleRejectResult(action)
        }
    }

    private fun onCancelClicked() {
        // Ignore cancel/back while a reject submission is in flight; the in-flight
        // call must either succeed or surface its error before the user can leave.
        if (state.dialogState is RejectLoanState.DialogState.Loading) return

        if (isDirty()) {
            mutableStateFlow.update {
                it.copy(dialogState = RejectLoanState.DialogState.PreventAccidentalBack)
            }
        } else {
            sendEvent(RejectLoanEvent.NavigateBack)
        }
    }

    private fun isDirty(): Boolean =
        state.rejectedOnDate != initialDate || state.note.isNotBlank()

    private fun submitLoanRejection() {
        if (state.dialogState is RejectLoanState.DialogState.Loading) return

        viewModelScope.launch {
            val validatedState = validate(state)
            mutableStateFlow.value = validatedState

            if (validatedState.rejectedOnDateError != null) return@launch

            mutableStateFlow.update {
                it.copy(dialogState = RejectLoanState.DialogState.Loading)
            }

            val input = RejectLoanInput(
                rejectedOnDate = ApiDateFormatter.formatForApi(validatedState.rejectedOnDate),
                note = validatedState.note.takeIf { it.isNotBlank() },
                locale = DateConstants.LOCALE,
                dateFormat = DateConstants.DATE_FORMAT,
            )

            val result = rejectLoanUseCase(loanId, input)
            sendAction(RejectLoanAction.Internal.ReceiveRejectResult(result))
        }
    }

    private fun handleRejectResult(action: RejectLoanAction.Internal.ReceiveRejectResult) {
        when (val result = action.result) {
            is DataState.Success -> mutableStateFlow.update {
                it.copy(dialogState = RejectLoanState.DialogState.Success)
            }

            is DataState.Error -> viewModelScope.launch {
                val message = result.message.ifBlank {
                    getString(Res.string.feature_loan_unknown_error_occured)
                }
                mutableStateFlow.update {
                    it.copy(dialogState = RejectLoanState.DialogState.Error(message))
                }
            }

            DataState.Loading -> Unit
        }
    }

    private suspend fun validate(state: RejectLoanState): RejectLoanState {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return if (state.rejectedOnDate > today) {
            state.copy(
                rejectedOnDateError = getString(Res.string.feature_loan_reject_date_error_future),
            )
        } else {
            state.copy(rejectedOnDateError = null)
        }
    }
}

/**
 * UI state for the reject-loan screen.
 */
@OptIn(ExperimentalTime::class)
@Immutable
internal data class RejectLoanState(
    val rejectedOnDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val note: String = "",
    val rejectedOnDateError: String? = null,
    val dialogState: DialogState? = null,
) {
    /**
     * Dialog states for the reject-loan screen.
     */
    internal sealed interface DialogState {
        data object Loading : DialogState
        data object Success : DialogState
        data object PreventAccidentalBack : DialogState
        data class Error(val message: String) : DialogState
    }
}

/**
 * One-shot events emitted by the reject-loan ViewModel.
 */
internal sealed interface RejectLoanEvent {
    data object NavigateBack : RejectLoanEvent
    data object NavigateBackWithSuccess : RejectLoanEvent
}

/**
 * User-initiated and internal actions for the reject-loan screen.
 */
internal sealed interface RejectLoanAction {
    data class RejectedOnDateChanged(val date: LocalDate) : RejectLoanAction
    data class NoteChanged(val note: String) : RejectLoanAction
    data object SubmitClicked : RejectLoanAction
    data object CancelClicked : RejectLoanAction
    data object PreventAccidentalBackConfirmed : RejectLoanAction
    data object DismissDialog : RejectLoanAction
    data object DismissSuccessDialog : RejectLoanAction

    sealed interface Internal : RejectLoanAction {
        data class ReceiveRejectResult(val result: DataState<Unit>) : Internal
    }
}
