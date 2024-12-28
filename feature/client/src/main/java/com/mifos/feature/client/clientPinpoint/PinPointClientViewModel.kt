/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientPinpoint

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.useCases.AddClientPinpointLocationUseCase
import com.mifos.core.domain.useCases.DeleteClientAddressPinpointUseCase
import com.mifos.core.domain.useCases.GetClientPinpointLocationsUseCase
import com.mifos.core.domain.useCases.UpdateClientPinpointUseCase
import com.mifos.core.objects.clients.ClientAddressRequest
import com.mifos.feature.client.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class PinPointClientViewModel @Inject constructor(
    private val getClientPinpointLocationsUseCase: GetClientPinpointLocationsUseCase,
    private val addClientPinpointLocationUseCase: AddClientPinpointLocationUseCase,
    private val deleteClientAddressPinpointUseCase: DeleteClientAddressPinpointUseCase,
    private val updateClientPinpointUseCase: UpdateClientPinpointUseCase,
    private val stateHandle: SavedStateHandle,
) : ViewModel() {

    val clientId = stateHandle.getStateFlow(key = "clientId", initialValue = 0)

    private val _pinPointClientUiState =
        MutableStateFlow<PinPointClientUiState>(PinPointClientUiState.Loading)
    val pinPointClientUiState = _pinPointClientUiState.asStateFlow()

    // for refresh feature
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshPinpointLocations(clientId: Int) {
        _isRefreshing.value = true
        getClientPinpointLocations(clientId = clientId)
        _isRefreshing.value = false
    }

    fun getClientPinpointLocations(clientId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getClientPinpointLocationsUseCase(clientId).collect { result ->
            when (result) {
                is Resource.Error ->
                    _pinPointClientUiState.value =
                        PinPointClientUiState.Error(R.string.feature_client_failed_to_load_pinpoint)

                is Resource.Loading -> _pinPointClientUiState.value = PinPointClientUiState.Loading

                is Resource.Success ->
                    _pinPointClientUiState.value =
                        if (result.data.isNullOrEmpty()) {
                            PinPointClientUiState.Error(R.string.feature_client_no_pinpoint_found)
                        } else {
                            PinPointClientUiState.ClientPinpointLocations(result.data ?: emptyList())
                        }
            }
        }
    }

    fun addClientPinpointLocation(clientId: Int, addressRequest: ClientAddressRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            addClientPinpointLocationUseCase(clientId, addressRequest).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _pinPointClientUiState.value =
                            PinPointClientUiState.Error(R.string.feature_client_failed_to_add_pinpoint)

                    is Resource.Loading ->
                        _pinPointClientUiState.value =
                            PinPointClientUiState.Loading

                    is Resource.Success ->
                        _pinPointClientUiState.value =
                            PinPointClientUiState.SuccessMessage(R.string.feature_client_pinpoint_location_added)
                }
            }
        }

    fun deleteClientPinpointLocation(apptableId: Int, datatableId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            deleteClientAddressPinpointUseCase(apptableId, datatableId).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _pinPointClientUiState.value =
                            PinPointClientUiState.Error(R.string.feature_client_failed_to_delete_pinpoint)

                    is Resource.Loading ->
                        _pinPointClientUiState.value =
                            PinPointClientUiState.Loading

                    is Resource.Success ->
                        _pinPointClientUiState.value =
                            PinPointClientUiState.SuccessMessage(R.string.feature_client_pinpoint_location_deleted)
                }
            }
        }

    fun updateClientPinpointLocation(
        apptableId: Int,
        datatableId: Int,
        addressRequest: ClientAddressRequest,
    ) = viewModelScope.launch(Dispatchers.IO) {
        updateClientPinpointUseCase(apptableId, datatableId, addressRequest).collect { result ->
            when (result) {
                is Resource.Error ->
                    _pinPointClientUiState.value =
                        PinPointClientUiState.Error(R.string.feature_client_failed_to_update_pinpoint)

                is Resource.Loading -> _pinPointClientUiState.value = PinPointClientUiState.Loading

                is Resource.Success ->
                    _pinPointClientUiState.value =
                        PinPointClientUiState.SuccessMessage(R.string.feature_client_pinpoint_location_updated)
            }
        }
    }
}
