/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncClientPayloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.FileUtils
import com.mifos.core.data.repository.SyncClientPayloadsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.room.entities.client.ClientPayloadEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 16/08/23.
 */

class SyncClientPayloadsViewModel(
    private val repository: SyncClientPayloadsRepository,
    private val prefManager: UserPreferencesRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _syncClientPayloadsUiState =
        MutableStateFlow<SyncClientPayloadsUiState>(SyncClientPayloadsUiState.ShowProgressbar)

    val syncClientPayloadsUiState: StateFlow<SyncClientPayloadsUiState>
        get() = _syncClientPayloadsUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var mClientPayloads: MutableList<ClientPayloadEntity> = mutableListOf()
    private var mClientSyncIndex = 0

    val userStatus: StateFlow<Boolean> = prefManager.userInfo
        .map { it.userStatus }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val isNetworkAvailable = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun refreshClientPayloads() {
        _isRefreshing.value = true
        loadDatabaseClientPayload()
        _isRefreshing.value = false
    }

    fun loadDatabaseClientPayload() {
        viewModelScope.launch {
            repository.allDatabaseClientPayload()
                .collect { state ->
                    when (state) {
                        is DataState.Success -> {
                            mClientPayloads = state.data.toMutableList()
                            _syncClientPayloadsUiState.value =
                                SyncClientPayloadsUiState.ShowPayloads(mClientPayloads)
                        }

                        is DataState.Error -> {
                            _syncClientPayloadsUiState.value =
                                SyncClientPayloadsUiState.ShowError(state.message)
                        }

                        is DataState.Loading -> {
                            _syncClientPayloadsUiState.value =
                                SyncClientPayloadsUiState.ShowProgressbar
                        }
                    }
                }
        }
    }

    private fun syncClientPayload(clientPayload: ClientPayloadEntity?) {
        viewModelScope.launch {
            _syncClientPayloadsUiState.value = SyncClientPayloadsUiState.ShowProgressbar

            try {
                repository.createClient(clientPayload!!)

                mClientPayloads[mClientSyncIndex].id?.let {
                    mClientPayloads[mClientSyncIndex].clientCreationTime?.let { it1 ->
                        deleteAndUpdateClientPayload(
                            it,
                            it1,
                        )
                    }
                }
            } catch (e: Exception) {
                _syncClientPayloadsUiState.value =
                    SyncClientPayloadsUiState.ShowError(e.message.toString())
                updateClientPayload(clientPayload)
            }
        }
    }

    fun deleteAndUpdateClientPayload(id: Int, clientCreationTIme: Long) {
        viewModelScope.launch {
            repository.deleteAndUpdatePayloads(id, clientCreationTIme)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Success -> {
                            mClientSyncIndex = 0
                            val list = dataState.data
                            if (list.isNotEmpty()) {
                                syncClientPayload()
                            }
                            mClientPayloads = list.toMutableList()
                            _syncClientPayloadsUiState.value =
                                SyncClientPayloadsUiState.ShowPayloads(mClientPayloads)
                        }

                        is DataState.Error -> {
                            _syncClientPayloadsUiState.value =
                                SyncClientPayloadsUiState.ShowError(dataState.message)
                        }

                        is DataState.Loading -> {
                            _syncClientPayloadsUiState.value =
                                SyncClientPayloadsUiState.ShowProgressbar
                        }
                    }
                }
        }
    }

    fun updateClientPayload(clientPayload: ClientPayloadEntity?) {
        viewModelScope.launch {
            _syncClientPayloadsUiState.value = SyncClientPayloadsUiState.ShowProgressbar

            try {
                repository.updateClientPayload(clientPayload!!)

                mClientPayloads[mClientSyncIndex] = clientPayload
                mClientSyncIndex += 1
                if (mClientPayloads.size != mClientSyncIndex) {
                    syncClientPayload()
                }
            } catch (e: Exception) {
                _syncClientPayloadsUiState.value =
                    SyncClientPayloadsUiState.ShowError(e.message.toString())
            }
        }
    }

    fun syncClientPayload() {
        for (i in mClientPayloads.indices) {
            if (mClientPayloads[i].errorMessage == null) {
                syncClientPayload(mClientPayloads[i])
                mClientSyncIndex = i
                break
            } else {
                mClientPayloads[i].errorMessage?.let {
                    FileUtils.logger.d { it }
                }
            }
        }
    }
}
