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
import androidclient.feature.loan.generated.resources.feature_loan_error_network_not_available
import androidclient.feature.loan.generated.resources.feature_loan_reject_date_error_future
import androidclient.feature.loan.generated.resources.feature_loan_unknown_error_occured
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanAccountRejectRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.payloads.RejectLoanPayload
import com.mifos.core.model.utils.DateConstants
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class RejectLoanViewModel(
    private val repository: LoanAccountRejectRepository,
    private val networkMonitor: NetworkMonitor,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<RejectLoanState, RejectLoanEvent, RejectLoanAction>(
    initialState = RejectLoanState(
        rejectedOnDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
    ),
) {

    private val loanId = savedStateHandle.toRoute<LoanRejectScreenRoute>().loanId

    private val initialDate = state.rejectedOnDate

    init {
        observeNetworkStatus()
    }

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

            RejectLoanAction.DiscardConfirmed -> {
                mutableStateFlow.update { it.copy(showDiscardDialog = false) }
                sendEvent(RejectLoanEvent.NavigateBack)
            }

            RejectLoanAction.DiscardDismissed -> {
                mutableStateFlow.update { it.copy(showDiscardDialog = false) }
            }

            RejectLoanAction.DismissDialog -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
            }

            RejectLoanAction.DismissSuccessDialog -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
                sendEvent(RejectLoanEvent.NavigateBackWithSuccess)
            }
        }
    }

    private fun onCancelClicked() {
        if (isDirty()) {
            mutableStateFlow.update { it.copy(showDiscardDialog = true) }
        } else {
            sendEvent(RejectLoanEvent.NavigateBack)
        }
    }

    private fun isDirty(): Boolean =
        state.rejectedOnDate != initialDate || state.note.isNotBlank()

    private fun submitLoanRejection() {
        if (state.isLoading) return

        viewModelScope.launch {
            val validatedState = validate(state)
            mutableStateFlow.value = validatedState

            if (validatedState.rejectedOnDateError != null) return@launch

            val isOnline = validatedState.networkConnection || networkMonitor.isOnline.first()
            if (!isOnline) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = RejectLoanState.DialogState.Error(
                            getString(Res.string.feature_loan_error_network_not_available),
                        ),
                    )
                }
                return@launch
            }

            mutableStateFlow.update { it.copy(isLoading = true, dialogState = null) }

            val payload = RejectLoanPayload(
                rejectedOnDate = ApiDateFormatter.formatForApi(validatedState.rejectedOnDate),
                note = validatedState.note.takeIf { it.isNotBlank() },
                locale = DateConstants.LOCALE,
                dateFormat = DateConstants.DATE_FORMAT,
            )

            val result = repository.rejectLoan(loanId, payload)

            when {
                result is DataState.Success -> mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        dialogState = RejectLoanState.DialogState.Success,
                    )
                }
                result is DataState.Error -> mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        dialogState = RejectLoanState.DialogState.Error(
                            result.message.ifBlank { getString(Res.string.feature_loan_unknown_error_occured) },
                        ),
                    )
                }
                else -> mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        dialogState = RejectLoanState.DialogState.Error(
                            getString(Res.string.feature_loan_unknown_error_occured),
                        ),
                    )
                }
            }
        }
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkMonitor.isOnline
                .distinctUntilChanged()
                .collect { isOnline ->
                    mutableStateFlow.update { it.copy(networkConnection = isOnline) }
                }
        }
    }

    private suspend fun validate(state: RejectLoanState): RejectLoanState {
        return if (state.rejectedOnDate > Clock.System.now().toLocalDateTime(TimeZone.UTC).date) {
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
    val rejectedOnDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
    val note: String = "",
    val networkConnection: Boolean = false,
    val isLoading: Boolean = false,
    val rejectedOnDateError: String? = null,
    val showDiscardDialog: Boolean = false,
    val dialogState: DialogState? = null,
) {
    /**
     * Dialog states for the reject-loan screen.
     */
    internal sealed interface DialogState {
        data object Success : DialogState
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
    data object DiscardConfirmed : RejectLoanAction
    data object DiscardDismissed : RejectLoanAction
    data object DismissDialog : RejectLoanAction
    data object DismissSuccessDialog : RejectLoanAction
}
