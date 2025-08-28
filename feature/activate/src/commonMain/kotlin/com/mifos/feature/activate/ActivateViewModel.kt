/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
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
import com.mifos.core.domain.useCases.ActivateCenterUseCase
import com.mifos.core.domain.useCases.ActivateClientUseCase
import com.mifos.core.domain.useCases.ActivateGroupUseCase
import com.mifos.core.model.objects.clients.ActivatePayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActivateViewModel(
    private val activateClientUseCase: ActivateClientUseCase,
    private val activateCenterUseCase: ActivateCenterUseCase,
    private val activateGroupUseCase: ActivateGroupUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val id = savedStateHandle.toRoute<ActivateRoute>().id
    val activateType = savedStateHandle.toRoute<ActivateRoute>().type

    private val _activateUiState = MutableStateFlow<ActivateUiState>(ActivateUiState.Initial)
    val activateUiState = _activateUiState.asStateFlow()

    fun activateClient(clientId: Int, clientPayload: ActivatePayload) =
        viewModelScope.launch {
            activateClientUseCase(clientId, clientPayload).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _activateUiState.value =
                            ActivateUiState.Error(Res.string.feature_activate_failed_to_activate_client)

                    is DataState.Loading -> _activateUiState.value = ActivateUiState.Loading

                    is DataState.Success ->
                        _activateUiState.value =
                            ActivateUiState.ActivatedSuccessfully(Res.string.feature_activate_client)
                }
            }
        }

    fun activateCenter(centerId: Int, centerPayload: ActivatePayload) =
        viewModelScope.launch {
            activateCenterUseCase(centerId, centerPayload).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _activateUiState.value =
                            ActivateUiState.Error(Res.string.feature_activate_failed_to_activate_center)

                    is DataState.Loading -> _activateUiState.value = ActivateUiState.Loading

                    is DataState.Success ->
                        _activateUiState.value =
                            ActivateUiState.ActivatedSuccessfully(Res.string.feature_activate_center)
                }
            }
        }

    fun activateGroup(groupId: Int, groupPayload: ActivatePayload) =
        viewModelScope.launch {
            val result = activateGroupUseCase(groupId, groupPayload)
            when (result) {
                is DataState.Error ->
                    _activateUiState.value =
                        ActivateUiState.Error(Res.string.feature_activate_failed_to_activate_group)

                DataState.Loading -> Unit // unreachable

                is DataState.Success ->
                    _activateUiState.value =
                        ActivateUiState.ActivatedSuccessfully(Res.string.feature_activate_group)
            }
        }
}
