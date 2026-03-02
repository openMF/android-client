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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.formatDate
import com.mifos.core.domain.useCases.RejectLoanUseCase
import com.mifos.core.model.objects.account.loan.RejectLoanPayload
import com.mifos.core.model.utils.DateConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import org.jetbrains.compose.resources.getString

/**
 * ViewModel for reject-loan state and actions.
 */
internal class RejectLoanViewModel(
    private val rejectLoanUseCase: RejectLoanUseCase,
    savedStateHandle: SavedStateHandle,
    private val currentDateProvider: () -> LocalDate = ::today,
    private val futureDateErrorProvider: suspend () -> String = {
        getString(Res.string.feature_loan_reject_date_error_future)
    },
    private val unknownErrorProvider: suspend () -> String = {
        getString(Res.string.feature_loan_unknown_error_occured)
    },
) : ViewModel() {

    private val loanId = savedStateHandle.get<Int>("loanId")
        ?: savedStateHandle.toRoute<LoanRejectScreenRoute>().loanId

    private val initialDate = currentDateProvider()

    private val _state = MutableStateFlow(
        RejectLoanViewState(
            rejectedOnDate = initialDate,
        ),
    )
    val state: StateFlow<RejectLoanViewState> = _state.asStateFlow()

    /**
     * Main intent processor for reject-loan interactions.
     */
    fun processIntent(intent: RejectLoanViewIntent) {
        when (intent) {
            is RejectLoanViewIntent.RejectedOnDateChanged -> {
                _state.update {
                    it.copy(
                        rejectedOnDate = intent.date,
                        rejectedOnDateError = null,
                    )
                }
            }

            is RejectLoanViewIntent.NoteChanged -> {
                _state.update { it.copy(note = intent.note) }
            }

            RejectLoanViewIntent.SubmitClicked -> submitLoanRejection()
            RejectLoanViewIntent.CancelClicked -> onCancelClicked()
            RejectLoanViewIntent.DismissError -> _state.update { it.copy(submissionError = null) }
            RejectLoanViewIntent.DiscardConfirmed -> {
                _state.update {
                    it.copy(
                        showDiscardDialog = false,
                        shouldNavigateBack = true,
                    )
                }
            }

            RejectLoanViewIntent.DiscardDismissed -> {
                _state.update { it.copy(showDiscardDialog = false) }
            }

            RejectLoanViewIntent.NavigationHandled -> {
                _state.update { it.copy(shouldNavigateBack = false) }
            }
        }
    }

    private fun onCancelClicked() {
        _state.update {
            if (isDirty(it)) {
                it.copy(showDiscardDialog = true)
            } else {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun isDirty(state: RejectLoanViewState): Boolean {
        return state.rejectedOnDate != initialDate || state.note.isNotBlank()
    }

    private fun submitLoanRejection() {
        viewModelScope.launch {
            val validatedState = validate(_state.value)
            _state.value = validatedState

            if (validatedState.rejectedOnDateError != null) {
                return@launch
            }

            val payload = RejectLoanPayload(
                rejectedOnDate = validatedState.rejectedOnDate.toApiDate(),
                note = validatedState.note.takeIf { it.isNotBlank() },
                locale = DateConstants.LOCALE,
                dateFormat = DateConstants.DATE_FORMAT,
            )

            rejectLoanUseCase(loanId, payload).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true,
                                submissionError = null,
                            )
                        }
                    }

                    is DataState.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                submissionError = null,
                                shouldNavigateBack = true,
                            )
                        }
                    }

                    is DataState.Error -> {
                        val errorMessage = dataState.exception.message
                            ?.takeIf { it.isNotBlank() }
                            ?: unknownErrorProvider()

                        _state.update {
                            it.copy(
                                isLoading = false,
                                submissionError = errorMessage,
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun validate(state: RejectLoanViewState): RejectLoanViewState {
        return if (state.rejectedOnDate > currentDateProvider()) {
            state.copy(
                rejectedOnDateError = futureDateErrorProvider(),
            )
        } else {
            state.copy(
                rejectedOnDateError = null,
            )
        }
    }

    private fun LocalDate.toApiDate(): String {
        return formatDate(
            this.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        )
    }
}
