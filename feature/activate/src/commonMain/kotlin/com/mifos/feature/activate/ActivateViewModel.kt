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

import kpt.feature.activate.generated.resources.Res
import kpt.feature.activate.generated.resources.feature_activate_center
import kpt.feature.activate.generated.resources.feature_activate_client
import kpt.feature.activate.generated.resources.feature_activate_failed_to_activate_center
import kpt.feature.activate.generated.resources.feature_activate_failed_to_activate_client
import kpt.feature.activate.generated.resources.feature_activate_failed_to_activate_group
import kpt.feature.activate.generated.resources.feature_activate_group
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.domain.useCases.ActivateCenterUseCase
import com.mifos.core.domain.useCases.ActivateClientUseCase
import com.mifos.core.domain.useCases.ActivateGroupUseCase
import com.mifos.core.model.objects.clients.ActivatePayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
            _activateUiState.value = ActivateUiState.Loading
            activateClientUseCase(clientId, clientPayload)
                .catch {
                    _activateUiState.value =
                        ActivateUiState.Error(Res.string.feature_activate_failed_to_activate_client)
                }
                .collect {
                    _activateUiState.value =
                        ActivateUiState.ActivatedSuccessfully(Res.string.feature_activate_client)
                }
        }

    fun activateCenter(centerId: Int, centerPayload: ActivatePayload) =
        viewModelScope.launch {
            _activateUiState.value = ActivateUiState.Loading
            activateCenterUseCase(centerId, centerPayload)
                .catch {
                    _activateUiState.value =
                        ActivateUiState.Error(Res.string.feature_activate_failed_to_activate_center)
                }
                .collect {
                    _activateUiState.value =
                        ActivateUiState.ActivatedSuccessfully(Res.string.feature_activate_center)
                }
        }

    fun activateGroup(groupId: Int, groupPayload: ActivatePayload) =
        viewModelScope.launch {
            _activateUiState.value = ActivateUiState.Loading
            try {
                activateGroupUseCase(groupId, groupPayload)
                _activateUiState.value =
                    ActivateUiState.ActivatedSuccessfully(Res.string.feature_activate_group)
            } catch (e: Exception) {
                _activateUiState.value =
                    ActivateUiState.Error(Res.string.feature_activate_failed_to_activate_group)
            }
        }
}
