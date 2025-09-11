/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountv2

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.GetClientTemplateUseCase
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.TextFieldsValidator
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import com.mifos.room.entities.templates.clients.SavingProductOptionsEntity
import com.mifos.room.entities.templates.clients.StaffOptionsEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class SavingsAccountViewModel(
    private val networkMonitor: NetworkMonitor,
    private val getClientTemplateUseCase: GetClientTemplateUseCase,
    val savedStateHandle: SavedStateHandle,
) :
    BaseViewModel<SavingsAccountState, SavingsAccountEvent, SavingsAccountAction>(
        initialState = run {
            SavingsAccountState(clientId = savedStateHandle.toRoute<SavingsAccountRoute>().clientId)
        },
    ) {

    init {
        loadClientTemplate()
    }

    override fun handleAction(action: SavingsAccountAction) {
        when (action) {
            is SavingsAccountAction.NavigateBack -> sendEvent(SavingsAccountEvent.NavigateBack)
            is SavingsAccountAction.NextStep -> moveToNextStep()
            is SavingsAccountAction.Finish -> sendEvent(SavingsAccountEvent.Finish)
            is SavingsAccountAction.OnStepChange -> handleStepChange(action)
            is SavingsAccountAction.OnSubmissionDatePick -> handleSubmissionDatePick(action)
            is SavingsAccountAction.Retry -> handleRetry()
            is SavingsAccountAction.OnSubmissionDateChange -> handleSubmissionDateChange(action)
            is SavingsAccountAction.OnDetailsSubmit -> handleOnDetailsSubmit()
            is SavingsAccountAction.OnExternalIdChange -> handleExternalIdChange(action)
            is SavingsAccountAction.OnProductNameChange -> handleOnProductNameChange(action)
            is SavingsAccountAction.Internal.OnReceivingClientTemplate -> handleClientTemplateResponse(action.clientTemplate)
            is SavingsAccountAction.OnFieldOfficerChange -> handleFieldOfficerChange(action)
        }
    }

    private fun handleFieldOfficerChange(action: SavingsAccountAction.OnFieldOfficerChange) {
        mutableStateFlow.update { it.copy(fieldOfficerIndex = action.index) }
    }

    private fun handleClientTemplateResponse(result: DataState<ClientsTemplateEntity>) {
        when (result) {
            is DataState.Loading -> mutableStateFlow.update {
                it.copy(
                    screenState = SavingsAccountState.ScreenState.Loading,
                )
            }

            is DataState.Error -> mutableStateFlow.update {
                it.copy(
                    dialogState = SavingsAccountState.DialogState.Error(result.message),
                )
            }

            is DataState.Success -> mutableStateFlow.update {
                it.copy(
                    dialogState = null,
                    screenState = SavingsAccountState.ScreenState.Success,
                    savingProductOptions = result.data.savingProductOptions ?: emptyList(),
                    fieldOfficerOptions = result.data.staffOptions ?: emptyList(),
                )
            }
        }
    }

    private fun handleOnProductNameChange(action: SavingsAccountAction.OnProductNameChange) {
        mutableStateFlow.update { it.copy(savingsProductSelected = action.index) }
    }

    private fun handleExternalIdChange(action: SavingsAccountAction.OnExternalIdChange) {
        mutableStateFlow.update { it.copy(externalId = action.value) }
    }

    private fun handleStepChange(action: SavingsAccountAction.OnStepChange) {
        mutableStateFlow.update { it.copy(currentStep = action.newIndex) }
    }

    private fun handleSubmissionDatePick(action: SavingsAccountAction.OnSubmissionDatePick) {
        mutableStateFlow.update { it.copy(showSubmissionDatePick = action.state) }
    }

    private fun handleSubmissionDateChange(action: SavingsAccountAction.OnSubmissionDateChange) {
        mutableStateFlow.update { it.copy(submissionDate = action.date) }
    }

    private fun handleOnDetailsSubmit() {
        mutableStateFlow.update {
            it.copy(
                externalIdError = null,
            )
        }
        val externalIdError = TextFieldsValidator.optionalStringValidator(state.externalId)
        if (externalIdError != null) {
            mutableStateFlow.update { it.copy(externalIdError = externalIdError) }
            return
        } else {
            moveToNextStep()
        }
    }

    private fun loadClientTemplate() = viewModelScope.launch {
        val online = networkMonitor.isOnline.first()
        if (online) {
            getClientTemplateUseCase().collect { result ->
                sendAction(SavingsAccountAction.Internal.OnReceivingClientTemplate(result))
            }
        } else {
            mutableStateFlow.update {
                it.copy(
                    screenState = SavingsAccountState.ScreenState.NetworkError,
                )
            }
        }
    }

    private fun handleRetry() {
        mutableStateFlow.update {
            it.copy(
                dialogState = null,
            )
        }
        loadClientTemplate()
    }

    private fun moveToNextStep() {
        val current = state.currentStep
        if (current < state.totalSteps) {
            mutableStateFlow.update {
                it.copy(
                    currentStep = current + 1,
                )
            }
        } else {
            sendEvent(SavingsAccountEvent.Finish)
        }
    }
}

data class SavingsAccountState
@OptIn(ExperimentalTime::class)
constructor(
    val clientId: Int,
    val fieldOfficerIndex: Int = -1,
    val fieldOfficerOptions: List<StaffOptionsEntity> = emptyList(),
    val isOverLayLoadingActive: Boolean = false,
    val savingsProductSelected: Int = -1,
    val savingProductOptions: List<SavingProductOptionsEntity> = emptyList(),
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    val dialogState: DialogState? = null,
    val externalId: String = "",
    val externalIdError: StringResource? = null,
    val screenState: ScreenState = ScreenState.Loading,
    val submissionDate: String = DateHelper.getDateAsStringFromLong(Clock.System.now().toEpochMilliseconds()),
    val showSubmissionDatePick: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
    }

    sealed interface ScreenState {
        data object Loading : ScreenState
        data object Success : ScreenState
        data object NetworkError : ScreenState
    }

    val isDetailsNextEnabled = submissionDate.isNotEmpty() &&
        savingsProductSelected != -1 &&
        fieldOfficerIndex != -1
}

sealed interface SavingsAccountEvent {
    data object NavigateBack : SavingsAccountEvent
    data object Finish : SavingsAccountEvent
}

sealed interface SavingsAccountAction {
    data object NavigateBack : SavingsAccountAction
    data object NextStep : SavingsAccountAction
    data object Finish : SavingsAccountAction
    data class OnStepChange(val newIndex: Int) : SavingsAccountAction
    data class OnSubmissionDateChange(val date: String) : SavingsAccountAction
    data class OnSubmissionDatePick(val state: Boolean) : SavingsAccountAction
    data object OnDetailsSubmit : SavingsAccountAction
    data class OnProductNameChange(val index: Int) : SavingsAccountAction
    data class OnFieldOfficerChange(val index: Int) : SavingsAccountAction
    data class OnExternalIdChange(val value: String) : SavingsAccountAction
    data object Retry : SavingsAccountAction

    sealed interface Internal : SavingsAccountAction {
        data class OnReceivingClientTemplate(val clientTemplate: DataState<ClientsTemplateEntity>) : Internal
    }
}
