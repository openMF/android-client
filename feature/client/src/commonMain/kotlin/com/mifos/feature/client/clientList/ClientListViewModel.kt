/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientList

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.ClientListRepository
import com.mifos.core.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 21/02/24.
 */

class ClientListViewModel(
    private val repository: ClientListRepository,
    private val prefManager: UserPreferencesRepository,
) : ViewModel() {

    private val _clientListUiState = MutableStateFlow<ClientListUiState>(ClientListUiState.Empty)
    val clientListUiState = _clientListUiState.asStateFlow()

    init {
        getClientList()
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshClientList() {
        _isRefreshing.value = true
        getClientList()
        _isRefreshing.value = false
    }

    fun getClientList() {
        viewModelScope.launch {
            val userStatus = prefManager.userInfo.first().userStatus
            if (userStatus) {
                loadClientsFromDb()
            } else {
                loadClientsFromApi()
            }
        }
    }

    private fun loadClientsFromApi() = viewModelScope.launch {
        val response = repository.getAllClients()
        _clientListUiState.value = ClientListUiState.ClientListApi(response)
    }

    private fun loadClientsFromDb() {
        viewModelScope.launch {
            repository.allDatabaseClients()
                .catch {
                    _clientListUiState.value =
                        ClientListUiState.Error(Res.string.feature_client_failed_to_load_client)
                }.collect {
                    _clientListUiState.value = ClientListUiState.ClientListDb(it.data?.pageItems)
                }
        }
    }
}
