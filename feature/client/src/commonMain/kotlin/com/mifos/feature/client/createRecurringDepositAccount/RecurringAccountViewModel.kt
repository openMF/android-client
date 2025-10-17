/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createRecurringDepositAccount

import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update

class RecurringAccountViewModel :
    BaseViewModel<RecurringAccountState, RecurringAccountEvent, RecurringAccountAction>(RecurringAccountState()) {

    override fun handleAction(action: RecurringAccountAction) {
        when (action) {
            RecurringAccountAction.NextStep -> {
                mutableStateFlow.update { state ->
                    val maxIndex = 4
                    state.copy(currentStep = (state.currentStep + 1).coerceAtMost(maxIndex))
                }
            }
            is RecurringAccountAction.OnStepChange -> {
                mutableStateFlow.update { it.copy(currentStep = action.index) }
            }
            RecurringAccountAction.NavigateBack -> {
                sendEvent(RecurringAccountEvent.NavigateBack)
            }
            RecurringAccountAction.Finish -> {
                sendEvent(RecurringAccountEvent.Finish)
            }
        }
    }
}

data class RecurringAccountState(
    val currentStep: Int = 0,
    val dialogState: Any? = null,
)

sealed class RecurringAccountAction {
    object NextStep : RecurringAccountAction()
    data class OnStepChange(val index: Int) : RecurringAccountAction()
    object NavigateBack : RecurringAccountAction()
    object Finish : RecurringAccountAction()
}

sealed class RecurringAccountEvent {
    object NavigateBack : RecurringAccountEvent()
    object Finish : RecurringAccountEvent()
}
