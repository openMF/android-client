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
import com.mifos.core.common.utils.FileUtils
import com.mifos.core.data.repository.SyncClientPayloadsRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.room.entities.client.ClientPayloadEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncClientPayloadsViewModel(
    private val repository: SyncClientPayloadsRepository,
    private val prefManager: PrefManager,
) : ViewModel() {

    private val _syncClientPayloadsUiState =
        MutableStateFlow<SyncClientPayloadsUiState>(SyncClientPayloadsUiState.ShowProgressbar)

    val syncClientPayloadsUiState: StateFlow<SyncClientPayloadsUiState>
        get() = _syncClientPayloadsUiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var mClientPayloads: MutableList<ClientPayloadEntity> = mutableListOf()
    private var mClientSyncIndex = 0

    fun getUserStatus(): Boolean {
        return prefManager.userStatus
    }

    fun refreshClientPayloads() {
        _isRefreshing.value = true
        loadDatabaseClientPayload()
        _isRefreshing.value = false
    }

    fun loadDatabaseClientPayload() {
        _syncClientPayloadsUiState.value = SyncClientPayloadsUiState.ShowProgressbar
        viewModelScope.launch {
            repository.allDatabaseClientPayload()
                .catch {
                    _syncClientPayloadsUiState.value =
                        SyncClientPayloadsUiState.ShowError(it.message.toString())
                }.collect { clientPayloads ->
                    mClientPayloads = clientPayloads.toMutableList()
                    _syncClientPayloadsUiState.value =
                        SyncClientPayloadsUiState.ShowPayloads(mClientPayloads)
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
            _syncClientPayloadsUiState.value = SyncClientPayloadsUiState.ShowProgressbar

            repository.deleteAndUpdatePayloads(id, clientCreationTIme)
                .catch { e ->
                    _syncClientPayloadsUiState.value =
                        SyncClientPayloadsUiState.ShowError(e.message.toString())
                }.collect { clientPayloads ->
                    mClientSyncIndex = 0
                    if (clientPayloads.isNotEmpty()) {
                        syncClientPayload()
                    }
                    mClientPayloads = clientPayloads.toMutableList()
                    _syncClientPayloadsUiState.value =
                        SyncClientPayloadsUiState.ShowPayloads(mClientPayloads)
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
//                    Log.d(
//                        LOG_TAG,
//                        it,
//                    )
                }
            }
        }
    }
}
