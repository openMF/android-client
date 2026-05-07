/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalTime::class)

package com.mifos.feature.loan.assignLoanOfficer

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_assign_loan_officer_no_officers
import androidclient.feature.loan.generated.resources.feature_loan_assign_loan_officer_same_officer
import androidclient.feature.loan.generated.resources.feature_loan_assign_loan_officer_success
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.domain.useCases.AssignLoanOfficerUseCase
import com.mifos.core.domain.useCases.GetLoanForAssignOfficerUseCase
import com.mifos.core.domain.useCases.GetLoanOfficerStaffOptionsUseCase
import com.mifos.core.model.objects.account.loan.AssignLoanOfficerInput
import com.mifos.core.model.utils.DateConstants
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class AssignLoanOfficerViewModel(
    savedStateHandle: SavedStateHandle,
    private val getLoanForAssignOfficerUseCase: GetLoanForAssignOfficerUseCase,
    private val getLoanOfficerStaffOptionsUseCase: GetLoanOfficerStaffOptionsUseCase,
    private val assignLoanOfficerUseCase: AssignLoanOfficerUseCase,
) : BaseViewModel<AssignLoanOfficerUiState, AssignLoanOfficerEffect, AssignLoanOfficerAction>(
    initialState = AssignLoanOfficerUiState.Loading,
) {

    private val route = savedStateHandle.toRoute<AssignLoanOfficerRoute>()
    private val loanId: Int = route.loanId

    val uiState: StateFlow<AssignLoanOfficerUiState> = stateFlow
    val effects = eventFlow

    private var officersLoadJob: Job? = null
    private var officersLoadStarted: Boolean = false

    init {
        trySendAction(AssignLoanOfficerAction.LoadLoan)
    }

    override fun handleAction(action: AssignLoanOfficerAction) {
        when (action) {
            AssignLoanOfficerAction.LoadLoan -> loadLoan()
            is AssignLoanOfficerAction.LoadOfficers -> {
                officersLoadJob = viewModelScope.launch { loadOfficers(action.officeId) }
            }
            is AssignLoanOfficerAction.SelectOfficer -> {
                updateContent { it.copy(selectedOfficerIndex = action.index, officerShowError = false) }
            }
            is AssignLoanOfficerAction.UpdateAssignmentDate -> {
                updateContent { it.copy(assignmentDateMillis = action.millis) }
            }
            AssignLoanOfficerAction.Submit -> submitInternal()
        }
    }

    private fun loadLoan() {
        viewModelScope.launch {
            getLoanForAssignOfficerUseCase(loanId).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> mutableStateFlow.value = AssignLoanOfficerUiState.Loading
                    is DataState.Error -> showLoadError(dataState.message)
                    is DataState.Success -> {
                        val loan = dataState.data
                        if (loan == null) {
                            showLoadError(getString(Res.string.feature_loan_failed_to_load_loan))
                            return@collect
                        }
                        mutableStateFlow.value = AssignLoanOfficerUiState.Content(
                            loan = loan,
                            assignmentDateMillis = Clock.System.now().toEpochMilliseconds(),
                        )
                        if (!officersLoadStarted) {
                            officersLoadStarted = true
                            trySendAction(AssignLoanOfficerAction.LoadOfficers(loan.clientOfficeId))
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadOfficers(officeId: Int) {
        try {
            getLoanOfficerStaffOptionsUseCase(officeId).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> Unit
                    is DataState.Error -> showLoadError(dataState.message)
                    is DataState.Success -> {
                        val officers = dataState.data
                        if (officers.isEmpty()) {
                            showLoadError(getString(Res.string.feature_loan_assign_loan_officer_no_officers))
                        } else {
                            updateContent { current ->
                                current.copy(
                                    officers = officers,
                                    selectedOfficerIndex = current.selectedOfficerIndex.takeIf { it in officers.indices } ?: -1,
                                )
                            }
                        }
                    }
                }
            }
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) throw throwable
            showLoadError(throwable.message)
        }
    }

    fun onOfficerSelected(index: Int) {
        trySendAction(AssignLoanOfficerAction.SelectOfficer(index))
    }

    fun onAssignmentDateMillis(millis: Long) {
        trySendAction(AssignLoanOfficerAction.UpdateAssignmentDate(millis))
    }

    fun submit() {
        trySendAction(AssignLoanOfficerAction.Submit)
    }

    private fun submitInternal() {
        val state = mutableStateFlow.value as? AssignLoanOfficerUiState.Content ?: return
        if (state.submitInProgress) return
        if (state.selectedOfficerIndex < 0) {
            updateContent { it.copy(officerShowError = true) }
            return
        }
        val officer = state.officers.getOrNull(state.selectedOfficerIndex) ?: return
        val officerId = officer.id
        val loan = state.loan

        viewModelScope.launch {
            if (loan.loanOfficerId > 0 && officerId == loan.loanOfficerId) {
                sendEvent(
                    AssignLoanOfficerEffect.ShowMessage(
                        getString(Res.string.feature_loan_assign_loan_officer_same_officer),
                    ),
                )
                return@launch
            }

            updateContent { it.copy(submitInProgress = true) }
            val request = AssignLoanOfficerInput(
                toLoanOfficerId = officerId,
                assignmentDate = DateHelper.getDateAsStringFromLong(state.assignmentDateMillis),
                locale = DateConstants.LOCALE,
                dateFormat = DateHelper.SHORT_MONTH,
                fromLoanOfficerId = loan.loanOfficerId.takeIf { it > 0 },
            )

            when (val result = assignLoanOfficerUseCase(loan.id, request)) {
                is DataState.Success -> {
                    updateContent { it.copy(submitInProgress = false) }
                    sendEvent(
                        AssignLoanOfficerEffect.ShowMessage(
                            getString(Res.string.feature_loan_assign_loan_officer_success),
                        ),
                    )
                    sendEvent(AssignLoanOfficerEffect.NavigateBack)
                }
                is DataState.Error -> {
                    updateContent { it.copy(submitInProgress = false) }
                    sendEvent(AssignLoanOfficerEffect.ShowMessage(result.message))
                }
                DataState.Loading -> Unit
            }
        }
    }

    private suspend fun showLoadError(message: String?) {
        mutableStateFlow.value = AssignLoanOfficerUiState.Error(message)
        sendEvent(
            AssignLoanOfficerEffect.ShowMessage(
                message.orEmpty().ifBlank { getString(Res.string.feature_loan_failed_to_load_loan) },
            ),
        )
        sendEvent(AssignLoanOfficerEffect.NavigateBack)
    }

    private fun updateContent(block: (AssignLoanOfficerUiState.Content) -> AssignLoanOfficerUiState.Content) {
        val current = mutableStateFlow.value as? AssignLoanOfficerUiState.Content ?: return
        mutableStateFlow.value = block(current)
    }

    override fun onCleared() {
        super.onCleared()
        officersLoadJob?.cancel()
    }
}
