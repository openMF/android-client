/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalTime::class)

package com.mifos.feature.loan.assignLoanOfficer

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.model.objects.account.loan.AssignLoanOfficerRequest
import com.mifos.core.model.utils.DateConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


internal class AssignLoanOfficerViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanAccountSummaryRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<AssignLoanOfficerRoute>()
    private val loanId: Int = route.loanId

    private val _uiState = MutableStateFlow(
        AssignLoanOfficerUiState(
            assignmentDateMillis = Clock.System.now().toEpochMilliseconds(),
        ),
    )
    val uiState: StateFlow<AssignLoanOfficerUiState> = _uiState.asStateFlow()

    private var officersLoadJob: Job? = null
    private var officersLoadStarted: Boolean = false

    init {
        viewModelScope.launch {
            repository.getLoanById(loanId).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        _uiState.update { it.copy(isLoading = true, loadError = null) }
                    }
                    is DataState.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loadError = dataState.message,
                            )
                        }
                    }
                    is DataState.Success -> {
                        val loan = dataState.data
                        if (loan == null) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    loadError = getString(Res.string.feature_loan_failed_to_load_loan),
                                )
                            }
                        } else {
                            _uiState.update { it.copy(loan = loan) }
                            if (!officersLoadStarted) {
                                officersLoadStarted = true
                                officersLoadJob = viewModelScope.launch {
                                    loadOfficers(loan.clientOfficeId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadOfficers(officeId: Int) {
        repository.getLoanOfficersForOffice(officeId).collect { dataState ->
            when (dataState) {
                is DataState.Loading -> Unit
                is DataState.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadError = dataState.message,
                        )
                    }
                }
                is DataState.Success -> {
                    _uiState.update {
                        it.copy(
                            officers = dataState.data,
                            isLoading = false,
                            loadError = null,
                        )
                    }
                }
            }
        }
    }

    fun onOfficerSelected(index: Int) {
        _uiState.update {
            it.copy(
                selectedOfficerIndex = index,
                officerShowError = false,
            )
        }
    }

    fun onAssignmentDateMillis(millis: Long) {
        _uiState.update { it.copy(assignmentDateMillis = millis) }
    }

    fun submit() {
        val state = _uiState.value
        if (state.selectedOfficerIndex < 0) {
            _uiState.update { it.copy(officerShowError = true) }
            return
        }
        val officer = state.officers.getOrNull(state.selectedOfficerIndex) ?: return
        val loan = state.loan ?: return
        val officerId = officer.id ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    submitInProgress = true,
                    submitError = null,
                )
            }
            val apiDate = DateHelper.getDateMonthYearStringFromLong(state.assignmentDateMillis)
            val request = AssignLoanOfficerRequest(
                toLoanOfficerId = officerId,
                assignmentDate = apiDate,
                locale = DateConstants.LOCALE,
                dateFormat = DateConstants.DATE_FORMAT,
                fromLoanOfficerId = loan.loanOfficerId.takeIf { it > 0 },
            )
            repository.assignLoanOfficer(loan.id, request).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> Unit
                    is DataState.Error -> {
                        _uiState.update {
                            it.copy(
                                submitInProgress = false,
                                submitError = dataState.message,
                            )
                        }
                    }
                    is DataState.Success -> {
                        _uiState.update {
                            it.copy(
                                submitInProgress = false,
                                completed = true,
                            )
                        }
                    }
                }
            }
        }
    }

    fun consumeSubmitError() {
        _uiState.update { it.copy(submitError = null) }
    }

    override fun onCleared() {
        super.onCleared()
        officersLoadJob?.cancel()
    }
}
