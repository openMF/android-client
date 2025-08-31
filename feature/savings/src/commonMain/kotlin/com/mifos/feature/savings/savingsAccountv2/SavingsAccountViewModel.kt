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

import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update

internal class SavingsAccountViewModel :
    BaseViewModel<SavingsAccountState, SavingsAccountEvent, SavingsAccountAction>(
        initialState = SavingsAccountState(),
    ) {

    override fun handleAction(action: SavingsAccountAction) {
        when (action) {
            SavingsAccountAction.NavigateBack -> sendEvent(SavingsAccountEvent.NavigateBack)
            SavingsAccountAction.NextStep -> moveToNextStep()
            SavingsAccountAction.Finish -> sendEvent(SavingsAccountEvent.Finish)
            is SavingsAccountAction.OnStepChange ->
                mutableStateFlow.value =
                    mutableStateFlow.value.copy(currentStep = action.newIndex)
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
            sendEvent(SavingsAccountEvent.Finish)
        }
    }
}

data class SavingsAccountState(
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }
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
}
