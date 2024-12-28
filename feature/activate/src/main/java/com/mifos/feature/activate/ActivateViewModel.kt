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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.useCases.ActivateCenterUseCase
import com.mifos.core.domain.useCases.ActivateClientUseCase
import com.mifos.core.domain.useCases.ActivateGroupUseCase
import com.mifos.core.objects.clients.ActivatePayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivateViewModel @Inject constructor(
    private val activateClientUseCase: ActivateClientUseCase,
    private val activateCenterUseCase: ActivateCenterUseCase,
    private val activateGroupUseCase: ActivateGroupUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val id = savedStateHandle.getStateFlow(key = Constants.ACTIVATE_ID, initialValue = 0)
    val activateType = savedStateHandle.getStateFlow(key = Constants.ACTIVATE_TYPE, initialValue = "")

    private val _activateUiState = MutableStateFlow<ActivateUiState>(ActivateUiState.Initial)
    val activateUiState = _activateUiState.asStateFlow()

    fun activateClient(clientId: Int, clientPayload: ActivatePayload) =
        viewModelScope.launch(Dispatchers.IO) {
            activateClientUseCase(clientId, clientPayload).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _activateUiState.value =
                            ActivateUiState.Error(R.string.feature_activate_failed_to_activate_client)

                    is Resource.Loading -> _activateUiState.value = ActivateUiState.Loading

                    is Resource.Success ->
                        _activateUiState.value =
                            ActivateUiState.ActivatedSuccessfully(R.string.feature_activate_client)
                }
            }
        }

    fun activateCenter(centerId: Int, centerPayload: ActivatePayload) =
        viewModelScope.launch(Dispatchers.IO) {
            activateCenterUseCase(centerId, centerPayload).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _activateUiState.value =
                            ActivateUiState.Error(R.string.feature_activate_failed_to_activate_center)

                    is Resource.Loading -> _activateUiState.value = ActivateUiState.Loading

                    is Resource.Success ->
                        _activateUiState.value =
                            ActivateUiState.ActivatedSuccessfully(R.string.feature_activate_center)
                }
            }
        }

    fun activateGroup(groupId: Int, groupPayload: ActivatePayload) =
        viewModelScope.launch(Dispatchers.IO) {
            activateGroupUseCase(groupId, groupPayload).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _activateUiState.value =
                            ActivateUiState.Error(R.string.feature_activate_failed_to_activate_group)

                    is Resource.Loading -> _activateUiState.value = ActivateUiState.Loading

                    is Resource.Success ->
                        _activateUiState.value =
                            ActivateUiState.ActivatedSuccessfully(R.string.feature_activate_group)
                }
            }
        }
}
