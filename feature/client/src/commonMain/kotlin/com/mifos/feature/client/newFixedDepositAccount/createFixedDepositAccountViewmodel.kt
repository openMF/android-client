/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.newFixedDepositAccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.FixedDepositRepository
import com.mifos.core.model.objects.template.recurring.FieldOfficerOption
import com.mifos.core.network.model.FixedDepositTemplate
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.fixedDepositAccount.FixedDepositAccountRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateFixedDepositAccountViewmodel(
    savedStateHandle: SavedStateHandle,
    private val fixedDepositRepository: FixedDepositRepository,
) :
    BaseViewModel<
            NewFixedDepositAccountState,
            NewFixedDepositAccountEvent,
            NewFixedDepositAccountAction,
            >(
        NewFixedDepositAccountState(
            clientId = savedStateHandle.toRoute<FixedDepositAccountRoute>().clientId,
        ),
    ) {


    init {
        loadFixedDepositAccountTemplate()
    }

    override fun handleAction(action: NewFixedDepositAccountAction) {
        when (action) {
            is NewFixedDepositAccountAction.OnNextPress -> moveToNextStep()
            is NewFixedDepositAccountAction.OnStepChange -> handleStepChange(action)
            is NewFixedDepositAccountAction.NavigateBack -> sendEvent(NewFixedDepositAccountEvent.NavigateBack)
            is NewFixedDepositAccountAction.Finish -> sendEvent(NewFixedDepositAccountEvent.Finish)
            is NewFixedDepositAccountAction.OnSubmissionDatePick -> handleSubmissionDatePick(action)
            is NewFixedDepositAccountAction.OnSubmissionDateChange -> handleSubmissionDateChange(action)
            is NewFixedDepositAccountAction.OnProductNameChange -> handleOnProductNameChange(action)
            is NewFixedDepositAccountAction.OnFieldOfficerChange -> handleFieldOfficerChange(action)
            is NewFixedDepositAccountAction.OnExternalIdChange -> handleExternalIdChange(action)
            NewFixedDepositAccountAction.OnDetailsSubmit -> handleOnDetailsSubmit()
            NewFixedDepositAccountAction.Retry -> handleRetry()
        }
    }

    private fun handleRetry(){
        loadFixedDepositAccountTemplate()
    }


    private fun loadFixedDepositAccountTemplate() = viewModelScope.launch {

        fixedDepositRepository.getFixedDepositTemplate(
            clientId = state.clientId,
            productId = state.template.productOptions?.get(state.fixedDepositAccountDetail.productSelected)?.id,
        ).collect { state ->
            when (state) {
                is DataState.Success -> {
                    setSuccessState()
                    mutableStateFlow.update {
                        it.copy(
                            template = state.data,
                        )
                    }
                }

                is DataState.Error -> {
                    setErrorState(state.message)
                }

                DataState.Loading -> {
                    setLoadingState()
                }
            }
        }
    }

    private fun setLoadingState() {
        mutableStateFlow.update {
            it.copy(
                screenState = NewFixedDepositAccountState.ScreenState.Loading,
            )
        }
    }

    private fun setSuccessState() {
        mutableStateFlow.update {
            it.copy(screenState = NewFixedDepositAccountState.ScreenState.Success)
        }
    }
    private fun setErrorState(message: String) {
        mutableStateFlow.update {
            it.copy(
                screenState = NewFixedDepositAccountState.ScreenState.Error(message),
            )
        }
    }

    private fun handleSubmissionDateChange(action: NewFixedDepositAccountAction.OnSubmissionDateChange) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountDetail = it.fixedDepositAccountDetail.copy(
                    submissionDate = action.date
                )
            )
        }
    }

    private fun handleStepChange(action: NewFixedDepositAccountAction.OnStepChange) {
        mutableStateFlow.update { it.copy(currentStep = action.newIndex) }
    }

    private fun handleSubmissionDatePick(action: NewFixedDepositAccountAction.OnSubmissionDatePick) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountDetail = it.fixedDepositAccountDetail.copy(
                    showSubmissionDatePick = action.state,
                ),
            )
        }
    }

    private fun handleOnProductNameChange(action: NewFixedDepositAccountAction.OnProductNameChange) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountDetail = it.fixedDepositAccountDetail.copy(
                    productSelected = action.index,
                ),
            )
        }
        loadFixedDepositAccountTemplate()
    }

    private fun handleFieldOfficerChange(action: NewFixedDepositAccountAction.OnFieldOfficerChange) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountDetail = it.fixedDepositAccountDetail.copy(
                    fieldOfficerIndex = action.index,
                ),
            )
        }
    }

    private fun handleExternalIdChange(action: NewFixedDepositAccountAction.OnExternalIdChange) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountDetail = it.fixedDepositAccountDetail.copy(
                    externalId = action.value,
                ),
            )
        }
    }

    private fun handleOnDetailsSubmit() {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountDetail = it.fixedDepositAccountDetail.copy(
                    externalIdError = null,
                ),
            )
        }
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
            sendEvent(NewFixedDepositAccountEvent.Finish)
        }
    }


}

data class NewFixedDepositAccountState constructor(
    val clientId: Int = -1,
    val currentStep: Int = 0,
    val dialogState: Any? = null,
    val totalSteps: Int = 4,
    val screenState: ScreenState = ScreenState.Loading,
    val fixedDepositAccountDetail: FixedDepositAccountDetailsState = FixedDepositAccountDetailsState(),
    val template: FixedDepositTemplate = FixedDepositTemplate()
) {
    sealed interface ScreenState {
        data class Error(val message: String) : ScreenState
        data object Loading : ScreenState
        data object Success : ScreenState
    }
}

data class FixedDepositAccountDetailsState @OptIn(ExperimentalTime::class) constructor(
    val submittedOnDate: String = "",
    val fieldOfficer: FieldOfficerOption? = null,
    val showSubmissionDatePick: Boolean = false,
    val productSelected: Int = -1,
    val submissionDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val fieldOfficerIndex: Int = -1,
    val fieldOfficerError: String? = null,
    val externalId: String = "",
    val externalIdError: StringResource? = null,
    val isMiniLoaderActive: Boolean = false,
    val fieldOfficerOptions: List<FieldOfficerOption>? = null,
) {
    val isDetailsNextEnabled = submissionDate.isNotEmpty() && fieldOfficerIndex != -1
}

sealed class NewFixedDepositAccountAction() {
    object OnNextPress : NewFixedDepositAccountAction()
    data class OnStepChange(val newIndex: Int) : NewFixedDepositAccountAction()

    object NavigateBack : NewFixedDepositAccountAction()
    data class OnSubmissionDatePick(val state: Boolean) : NewFixedDepositAccountAction()
    data class OnSubmissionDateChange(val date: String) : NewFixedDepositAccountAction()
    data class OnProductNameChange(val index: Int) : NewFixedDepositAccountAction()
    object Finish : NewFixedDepositAccountAction()
    data class OnFieldOfficerChange(val index: Int) : NewFixedDepositAccountAction()
    data class OnExternalIdChange(val value: String) : NewFixedDepositAccountAction()
    data object OnDetailsSubmit : NewFixedDepositAccountAction()

    data object Retry : NewFixedDepositAccountAction()


}

sealed class NewFixedDepositAccountEvent() {
    object NavigateBack : NewFixedDepositAccountEvent()
    object Finish : NewFixedDepositAccountEvent()
}
