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
import com.mifos.core.data.repository.CloseLoanRepository
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.model.objects.account.loan.CloseLoanRequest
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Close Loan Account screen.
 *
 * Loads the close-loan template and the current loan (to get the disbursement date used as the
 * picker lower bound), then handles the submit flow via [CloseLoanAction.OnSubmit]. Asynchronous
 * results re-enter the action pipeline as [CloseLoanAction.Internal] events so the
 * [handleAction] reducer remains the single source of state mutation.
 *
 * @property closeLoanRepository Repository for the close-loan transaction flow.
 * @property loanRepository Repository for fetching loan account details.
 */
class CloseLoanViewModel(
    savedStateHandle: SavedStateHandle,
    private val closeLoanRepository: CloseLoanRepository,
    private val loanRepository: LoanAccountSummaryRepository,
) : BaseViewModel<CloseLoanState, CloseLoanEvent, CloseLoanAction>(
    initialState = CloseLoanState(),
) {

    private val route = savedStateHandle.toRoute<CloseLoanScreenRoute>()

    val loanId: Int get() = route.loanId

    init {
        loadInitialData()
    }

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
            CloseLoanAction.OnRetryLoadTemplate -> loadInitialData()
            CloseLoanAction.NavigateBack -> sendEvent(CloseLoanEvent.NavigateBack)
            is CloseLoanAction.Internal.TemplateLoaded ->
                handleTemplateLoaded(action.result)
            is CloseLoanAction.Internal.LoanLoaded ->
                handleLoanLoaded(action.result)
            is CloseLoanAction.Internal.CloseResult ->
                handleCloseResult(action.result)
        }
    }

    private fun loadInitialData() {
        mutableStateFlow.update { it.copy(isTemplateLoading = true, loadError = null) }
        fetchTemplate()
        fetchLoan()
    }

    private fun fetchTemplate() {
        viewModelScope.launch {
            val result = closeLoanRepository.getCloseLoanTemplate(loanId)
                .first { it !is DataState.Loading }
            sendAction(CloseLoanAction.Internal.TemplateLoaded(result))
        }
    }

    private fun fetchLoan() {
        viewModelScope.launch {
            val result = loanRepository.getLoanById(loanId)
                .first { it !is DataState.Loading }
            sendAction(CloseLoanAction.Internal.LoanLoaded(result))
        }
    }

    private fun handleTemplateLoaded(result: DataState<LoanTransactionTemplate?>) {
        if (result is DataState.Error) {
            mutableStateFlow.update {
                it.copy(
                    isTemplateLoading = false,
                    loadError = Res.string.feature_loan_close_failed_to_load_template,
                )
            }
        }
    }

    private fun handleLoanLoaded(result: DataState<LoanWithAssociationsEntity?>) {
        when (result) {
            is DataState.Success -> mutableStateFlow.update {
                it.copy(
                    isTemplateLoading = false,
                    disbursementDateMillis = extractDisbursementMillis(result.data),
                )
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

    private fun extractDisbursementMillis(loan: LoanWithAssociationsEntity?): Long? =
        loan?.timeline
            ?.actualDisbursementDate
            ?.filterNotNull()
            ?.let { DateHelper.getDateAsLongFromList(it) }

    private fun submitClose() {
        val closedOnMillis = state.closedOnDateMillis ?: return
        mutableStateFlow.update { it.copy(dialogState = CloseLoanState.DialogState.Submitting) }

        val request = buildCloseRequest(closedOnMillis, state.note)

        viewModelScope.launch {
            val result = closeLoanRepository.closeLoanAccount(loanId, request)
            if (result is DataState.Success) {
                closeLoanRepository.syncLoanAccount(loanId)
            }
            sendAction(CloseLoanAction.Internal.CloseResult(result))
        }
    }

    private fun buildCloseRequest(closedOnMillis: Long, note: String): CloseLoanRequest {
        val closedOnDate = ApiDateFormatter.formatForApi(closedOnMillis)
        return CloseLoanRequest(
            closedOnDate = closedOnDate,
            transactionDate = closedOnDate,
            dateFormat = ApiDateFormatter.DATE_FORMAT,
            locale = ApiDateFormatter.LOCALE,
            note = note.ifBlank { null },
        )
    }

    private fun handleCloseResult(result: DataState<Unit>) {
        when (result) {
            is DataState.Success -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
                sendEvent(CloseLoanEvent.CloseSuccess)
            }
            is DataState.Error -> mutableStateFlow.update {
                it.copy(
                    dialogState = CloseLoanState.DialogState.Error(
                        Res.string.feature_loan_close_failed,
                    ),
                )
            }
            DataState.Loading -> Unit
        }
    }
}
