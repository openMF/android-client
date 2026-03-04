/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanReject

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_reject_date_error_future
import androidclient.feature.loan.generated.resources.feature_loan_unknown_error_occured
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.common.utils.DateHelper.today
import com.mifos.core.domain.useCases.RejectLoanUseCase
import com.mifos.core.model.objects.account.loan.RejectLoanPayload
import com.mifos.core.model.utils.DateConstants
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.coroutines.cancellation.CancellationException

/**
 * ViewModel for reject-loan state and actions.
 */
internal class RejectLoanViewModel(
    private val rejectLoanUseCase: RejectLoanUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<RejectLoanViewState, RejectLoanEvent, RejectLoanAction>(
    initialState = RejectLoanViewState(rejectedOnDate = today()),
) {

    private val loanId = savedStateHandle.get<Int>("loanId")
        ?: savedStateHandle.toRoute<LoanRejectScreenRoute>().loanId

    private val initialDate = today()

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
            RejectLoanAction.DismissError -> {
                mutableStateFlow.update { it.copy(submissionError = null) }
            }

            RejectLoanAction.DiscardConfirmed -> {
                mutableStateFlow.update { it.copy(showDiscardDialog = false) }
                sendEvent(RejectLoanEvent.NavigateBack)
            }

            RejectLoanAction.DiscardDismissed -> {
                mutableStateFlow.update { it.copy(showDiscardDialog = false) }
            }
        }
    }

    private fun onCancelClicked() {
        if (isDirty(state)) {
            mutableStateFlow.update { it.copy(showDiscardDialog = true) }
        } else {
            sendEvent(RejectLoanEvent.NavigateBack)
        }
    }

    private fun isDirty(state: RejectLoanViewState): Boolean {
        return state.rejectedOnDate != initialDate || state.note.isNotBlank()
    }

    private fun submitLoanRejection() {
        if (state.isLoading) return

        viewModelScope.launch {
            val validatedState = validate(state)
            mutableStateFlow.value = validatedState

            if (validatedState.rejectedOnDateError != null) {
                return@launch
            }

            mutableStateFlow.update {
                it.copy(isLoading = true, submissionError = null)
            }

            val payload = RejectLoanPayload(
                rejectedOnDate = ApiDateFormatter.formatForApi(validatedState.rejectedOnDate),
                note = validatedState.note.takeIf { it.isNotBlank() },
                locale = DateConstants.LOCALE,
                dateFormat = DateConstants.DATE_FORMAT,
            )

            try {
                rejectLoanUseCase(loanId, payload)
                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        submissionError = null,
                    )
                }
                sendEvent(RejectLoanEvent.RejectSuccess)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val errorMessage = e.message
                    ?.takeIf { it.isNotBlank() }
                    ?: getString(Res.string.feature_loan_unknown_error_occured)

                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        submissionError = errorMessage,
                    )
                }
            }
        }
    }

    private suspend fun validate(state: RejectLoanViewState): RejectLoanViewState {
        return if (state.rejectedOnDate > today()) {
            state.copy(
                rejectedOnDateError = getString(
                    Res.string.feature_loan_reject_date_error_future,
                ),
            )
        } else {
            state.copy(rejectedOnDateError = null)
        }
    }

}
