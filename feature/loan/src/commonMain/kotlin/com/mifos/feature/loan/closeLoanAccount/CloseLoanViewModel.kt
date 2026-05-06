/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.closeLoanAccount

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_close_failed
import androidclient.feature.loan.generated.resources.feature_loan_close_failed_to_load_template
import androidclient.feature.loan.generated.resources.feature_loan_profile_failed_to_load_loan
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.domain.useCases.CloseLoanUseCase
import com.mifos.core.domain.useCases.GetCloseLoanTemplateUseCase
import com.mifos.core.model.objects.account.loan.CloseLoanRequest
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Close Loan Account screen.
 *
 * Loads the close-loan template and the current loan (to get the disbursement date used as the
 * picker lower bound), then handles the submit flow via [CloseLoanAction.OnSubmit].
 *
 * @property savedStateHandle Handle to saved state for this ViewModel.
 * @property closeLoanUseCase Use case to close a loan account.
 * @property getCloseLoanTemplateUseCase Use case to fetch the close-loan template.
 * @property loanRepository Repository for fetching loan account details.
 */
class CloseLoanViewModel(
    savedStateHandle: SavedStateHandle,
    private val closeLoanUseCase: CloseLoanUseCase,
    private val getCloseLoanTemplateUseCase: GetCloseLoanTemplateUseCase,
    private val loanRepository: LoanAccountSummaryRepository,
) : BaseViewModel<CloseLoanState, CloseLoanEvent, CloseLoanAction>(
    initialState = CloseLoanState(),
) {

    private val route = savedStateHandle.toRoute<CloseLoanScreenRoute>()

    /**
     * The unique identifier of the loan account this screen is operating on.
     */
    val loanId: Int get() = route.loanId

    init {
        loadTemplate()
    }

    /**
     * Processes incoming user actions and updates the state or sends events.
     *
     * @param action The user-initiated action.
     */
    override fun handleAction(action: CloseLoanAction) {
        when (action) {
            is CloseLoanAction.OnDateChange -> mutableStateFlow.update {
                it.copy(closedOnDateMillis = action.millis, showDatePicker = false)
            }
            is CloseLoanAction.OnNoteChange -> mutableStateFlow.update {
                it.copy(note = action.note)
            }
            CloseLoanAction.OnShowDatePicker -> mutableStateFlow.update {
                it.copy(showDatePicker = true)
            }
            CloseLoanAction.OnHideDatePicker -> mutableStateFlow.update {
                it.copy(showDatePicker = false)
            }
            CloseLoanAction.OnSubmit -> submitClose()
            CloseLoanAction.OnDismissError -> mutableStateFlow.update {
                it.copy(dialogState = null)
            }
            CloseLoanAction.OnRetryLoadTemplate -> loadTemplate()
            CloseLoanAction.NavigateBack -> sendEvent(CloseLoanEvent.NavigateBack)
        }
    }

    /**
     * Loads the close-loan template and current loan details.
     *
     * The template provides closure metadata, while the loan details are used to
     * determine the disbursement date as the lower bound for closure.
     */
    private fun loadTemplate() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(isTemplateLoading = true, loadError = null) }
            val templateResult = getCloseLoanTemplateUseCase(loanId)
                .first { it !is DataState.Loading }
            if (templateResult is DataState.Error) {
                mutableStateFlow.update {
                    it.copy(
                        isTemplateLoading = false,
                        loadError = Res.string.feature_loan_close_failed_to_load_template,
                    )
                }
                return@launch
            }

            val loanResult = loanRepository.getLoanById(loanId)
                .first { it !is DataState.Loading }
            when (loanResult) {
                is DataState.Success -> {
                    val disbursement = loanResult.data
                        ?.timeline
                        ?.actualDisbursementDate
                        ?.filterNotNull()
                        ?.let { DateHelper.getDateAsLongFromList(it) }
                    mutableStateFlow.update {
                        it.copy(
                            isTemplateLoading = false,
                            disbursementDateMillis = disbursement,
                        )
                    }
                }
                is DataState.Error -> mutableStateFlow.update {
                    it.copy(
                        isTemplateLoading = false,
                        loadError = Res.string.feature_loan_profile_failed_to_load_loan,
                    )
                }
                DataState.Loading -> Unit
            }
        }
    }

    /**
     * Submits the close-loan request.
     *
     * Formats the selected date and note into a [CloseLoanRequest] and invokes the use case.
     */
    private fun submitClose() {
        val closedOnMillis = state.closedOnDateMillis ?: return
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(dialogState = CloseLoanState.DialogState.Submitting) }

            val closedOnDate = ApiDateFormatter.formatForApi(closedOnMillis)
            val noteValue = state.note
            val request = CloseLoanRequest(
                closedOnDate = closedOnDate,
                transactionDate = closedOnDate,
                dateFormat = ApiDateFormatter.DATE_FORMAT,
                locale = ApiDateFormatter.LOCALE,
                note = noteValue.ifBlank { null },
            )

            closeLoanUseCase(loanId, request).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        mutableStateFlow.update { it.copy(dialogState = null) }
                        sendEvent(CloseLoanEvent.CloseSuccess)
                    }
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = CloseLoanState.DialogState.Error(
                                    Res.string.feature_loan_close_failed,
                                ),
                            )
                        }
                    }
                    DataState.Loading -> Unit
                }
            }
        }
    }
}
