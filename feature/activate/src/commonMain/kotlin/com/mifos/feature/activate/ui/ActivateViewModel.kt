/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.activate.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.activate.ActivateRepository
import com.mifos.core.data.store.SubmitState
import com.mifos.core.data.store.submitHandler
import com.mifos.core.ui.store.BaseViewModel
import com.mifos.feature.activate.navigation.ActivateRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ActivateViewModel(
    private val activateRepository: ActivateRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ActivateState, Nothing, ActivateAction>(initialState = ActivateState()) {

    val id: Int = savedStateHandle.toRoute<ActivateRoute>().id
    val activateType: String = savedStateHandle.toRoute<ActivateRoute>().type

    private val submit = viewModelScope.submitHandler<Unit>()

    val submitState: StateFlow<SubmitState<Unit>> = submit.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SubmitState.Idle,
    )

    override fun handleAction(action: ActivateAction) {
        when (action) {
            is ActivateAction.ActivateClient -> {
                mutableStateFlow.value = state.copy(targetType = ActivateState.TargetType.Client)
                submit.submit { activateRepository.activateClient(action.clientId, action.payload) }
            }

            is ActivateAction.ActivateCenter -> {
                mutableStateFlow.value = state.copy(targetType = ActivateState.TargetType.Center)
                submit.submit { activateRepository.activateCenter(action.centerId, action.payload) }
            }

            is ActivateAction.ActivateGroup -> {
                mutableStateFlow.value = state.copy(targetType = ActivateState.TargetType.Group)
                submit.submit { activateRepository.activateGroup(action.groupId, action.payload) }
            }
        }
    }

    fun onSubmitConsumed() {
        submit.reset()
    }
}
