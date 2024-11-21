/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientCharges

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.ClientChargeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientChargesViewModel @Inject constructor(
    private val repository: ClientChargeRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val clientId = savedStateHandle.getStateFlow(key = Constants.CLIENT_ID, initialValue = 0)

    // for refresh feature
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshCenterList(clientId: Int) {
        _isRefreshing.value = true
        loadCharges(clientId = clientId)
        _isRefreshing.value = false
    }

    private val _clientChargesUiState =
        MutableStateFlow<ClientChargeUiState>(ClientChargeUiState.Loading)
    val clientChargesUiState = _clientChargesUiState.asStateFlow()

    fun loadCharges(clientId: Int) = viewModelScope.launch(Dispatchers.IO) {
        val response = repository.getClientCharges(clientId)
        _clientChargesUiState.value = ClientChargeUiState.ChargesList(response)
    }
}
