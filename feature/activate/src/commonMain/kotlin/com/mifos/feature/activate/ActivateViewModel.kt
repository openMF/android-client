/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.activate

import androidclient.feature.activate.generated.resources.Res
import androidclient.feature.activate.generated.resources.feature_activate_center
import androidclient.feature.activate.generated.resources.feature_activate_client
import androidclient.feature.activate.generated.resources.feature_activate_failed_to_activate_center
import androidclient.feature.activate.generated.resources.feature_activate_failed_to_activate_client
import androidclient.feature.activate.generated.resources.feature_activate_failed_to_activate_group
import androidclient.feature.activate.generated.resources.feature_activate_group
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.store.SubmitState
import com.mifos.core.data.store.submitHandler
import com.mifos.core.domain.useCases.ActivateCenterUseCase
import com.mifos.core.domain.useCases.ActivateClientUseCase
import com.mifos.core.domain.useCases.ActivateGroupUseCase
import com.mifos.core.model.objects.clients.ActivatePayload
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.compose.resources.StringResource

class ActivateViewModel(
    private val activateClientUseCase: ActivateClientUseCase,
    private val activateCenterUseCase: ActivateCenterUseCase,
    private val activateGroupUseCase: ActivateGroupUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val id = savedStateHandle.toRoute<ActivateRoute>().id
    val activateType = savedStateHandle.toRoute<ActivateRoute>().type

    private val submit = viewModelScope.submitHandler<Unit>()
    private var successMessage: StringResource = Res.string.feature_activate_client
    private var failureMessage: StringResource = Res.string.feature_activate_failed_to_activate_client

    val activateUiState: StateFlow<ActivateUiState> = submit.state
        .map { state ->
            when (state) {
                is SubmitState.Idle -> ActivateUiState.Initial
                is SubmitState.Submitting -> ActivateUiState.Loading
                is SubmitState.Submitted<*> -> ActivateUiState.ActivatedSuccessfully(successMessage)
                is SubmitState.Failed -> ActivateUiState.Error(failureMessage)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ActivateUiState.Initial,
        )

    fun activateClient(clientId: Int, clientPayload: ActivatePayload) {
        successMessage = Res.string.feature_activate_client
        failureMessage = Res.string.feature_activate_failed_to_activate_client
        submit.submit {
            val terminal = activateClientUseCase(clientId, clientPayload)
                .first { it !is DataState.Loading }
            if (terminal is DataState.Error) throw terminal.exception
        }
    }

    fun activateCenter(centerId: Int, centerPayload: ActivatePayload) {
        successMessage = Res.string.feature_activate_center
        failureMessage = Res.string.feature_activate_failed_to_activate_center
        submit.submit {
            val terminal = activateCenterUseCase(centerId, centerPayload)
                .first { it !is DataState.Loading }
            if (terminal is DataState.Error) throw terminal.exception
        }
    }

    fun activateGroup(groupId: Int, groupPayload: ActivatePayload) {
        successMessage = Res.string.feature_activate_group
        failureMessage = Res.string.feature_activate_failed_to_activate_group
        submit.submit {
            val terminal = activateGroupUseCase(groupId, groupPayload)
            if (terminal is DataState.Error) throw terminal.exception
        }
    }
}
