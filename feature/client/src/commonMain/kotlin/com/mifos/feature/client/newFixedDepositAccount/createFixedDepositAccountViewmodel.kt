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

import com.mifos.core.common.utils.DateHelper
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.templates.clients.SavingProductOptionsEntity
import com.mifos.room.entities.templates.clients.StaffOptionsEntity
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateFixedDepositAccountViewmodel :
    BaseViewModel<
        NewFixedDepositAccountState,
        NewFixedDepositAccountEvent,
        NewFixedDepositAccountAction,
        >(NewFixedDepositAccountState()) {
    override fun handleAction(action: NewFixedDepositAccountAction) {
        when (action) {
            is NewFixedDepositAccountAction.NextStep -> moveToNextStep()
            is NewFixedDepositAccountAction.OnStepChange -> handleStepChange(action)
            is NewFixedDepositAccountAction.NavigateBack -> sendEvent(NewFixedDepositAccountEvent.NavigateBack)
            is NewFixedDepositAccountAction.Finish -> sendEvent(NewFixedDepositAccountEvent.Finish)
            is NewFixedDepositAccountAction.OnSubmissionDatePick -> handleSubmissionDatePick(action)
            is NewFixedDepositAccountAction.OnSubmissionDateChange -> handleSubmissionDateChange(action)
            is NewFixedDepositAccountAction.OnProductNameChange -> handleOnProductNameChange(action)
            is NewFixedDepositAccountAction.OnFieldOfficerChange -> handleFieldOfficerChange(action)
            is NewFixedDepositAccountAction.OnExternalIdChange -> handleExternalIdChange(action)
            NewFixedDepositAccountAction.OnDetailsSubmit -> handleOnDetailsSubmit()
        }
    }
    private fun handleSubmissionDateChange(action: NewFixedDepositAccountAction.OnSubmissionDateChange) {
        mutableStateFlow.update { it.copy(submissionDate = action.date) }
    }
    private fun handleStepChange(action: NewFixedDepositAccountAction.OnStepChange) {
        mutableStateFlow.update { it.copy(currentStep = action.newIndex) }
    }

    private fun handleSubmissionDatePick(action: NewFixedDepositAccountAction.OnSubmissionDatePick) {
        mutableStateFlow.update { it.copy(showSubmissionDatePick = action.state) }
    }
    private fun handleOnProductNameChange(action: NewFixedDepositAccountAction.OnProductNameChange) {
        mutableStateFlow.update { it.copy(fixedDepositProductSelected = action.index) }
    }
    private fun handleFieldOfficerChange(action: NewFixedDepositAccountAction.OnFieldOfficerChange) {
        mutableStateFlow.update { it.copy(fieldOfficerIndex = action.index) }
    }
    private fun handleExternalIdChange(action: NewFixedDepositAccountAction.OnExternalIdChange) {
        mutableStateFlow.update { it.copy(externalId = action.value) }
    }
    private fun handleOnDetailsSubmit() {
        mutableStateFlow.update {
            it.copy(
                externalIdError = null,
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

data class NewFixedDepositAccountState @OptIn(ExperimentalTime::class) constructor(
    val currentStep: Int = 0,
    val dialogState: Any? = null,
    val showSubmissionDatePick: Boolean = false,
    val fixedDepositProductSelected: Int = -1,
    val fixedDepositProductOptions: List<SavingProductOptionsEntity> = emptyList(),
    val submissionDate: String = DateHelper.getDateAsStringFromLong(Clock.System.now().toEpochMilliseconds()),
    val fieldOfficerIndex: Int = -1,
    val fieldOfficerOptions: List<StaffOptionsEntity> = emptyList(),
    val externalId: String = "",
    val externalIdError: StringResource? = null,
    val totalSteps: Int = 4,


){
    val isDetailsNextEnabled = submissionDate.isNotEmpty() &&
            fixedDepositProductSelected != -1 &&
            fieldOfficerIndex != -1
}
sealed class NewFixedDepositAccountAction() {
    object NextStep : NewFixedDepositAccountAction()
    data class OnStepChange(val newIndex: Int) : NewFixedDepositAccountAction()
    object NavigateBack : NewFixedDepositAccountAction()
    data class OnSubmissionDatePick(val state: Boolean) : NewFixedDepositAccountAction()
    data class OnSubmissionDateChange(val date: String) : NewFixedDepositAccountAction()
    data class OnProductNameChange(val index: Int) : NewFixedDepositAccountAction()
    object Finish : NewFixedDepositAccountAction()
    data class OnFieldOfficerChange(val index: Int) : NewFixedDepositAccountAction()
    data class OnExternalIdChange(val value: String) : NewFixedDepositAccountAction()
    data object OnDetailsSubmit : NewFixedDepositAccountAction()


}
sealed class NewFixedDepositAccountEvent() {
    object NavigateBack : NewFixedDepositAccountEvent()
    object Finish : NewFixedDepositAccountEvent()
}
