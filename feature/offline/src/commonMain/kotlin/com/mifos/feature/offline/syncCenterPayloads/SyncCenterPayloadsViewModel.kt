/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncCenterPayloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.FileUtils
import com.mifos.core.data.repository.SyncCenterPayloadsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.room.entities.center.CenterPayloadEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@Suppress("UNCHECKED_CAST")
class SyncCenterPayloadsViewModel(
    private val prefManager: UserPreferencesRepository,
    private val repository: SyncCenterPayloadsRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _syncCenterPayloadsUiState = MutableStateFlow<SyncCenterPayloadsUiState>(
        SyncCenterPayloadsUiState.ShowProgressbar,
    )
    val syncCenterPayloadsUiState: StateFlow<SyncCenterPayloadsUiState> =
        _syncCenterPayloadsUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var mCenterPayloads: MutableList<CenterPayloadEntity> = mutableListOf()
    private var centerSyncIndex = 0

    val isNetworkAvailable = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val userStatus: StateFlow<Boolean> = prefManager.userInfo
        .map { it.userStatus }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    fun refreshCenterPayloads() {
        _isRefreshing.value = true
        loadDatabaseCenterPayload()
        _isRefreshing.value = false
    }

    fun loadDatabaseCenterPayload() {
        viewModelScope.launch {
            _syncCenterPayloadsUiState.value =
                SyncCenterPayloadsUiState.ShowProgressbar
            repository.getAllDatabaseCenterPayload()
                .catch { error ->
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowError(error.message ?: "")
                }
                .collect { centerPayloads ->
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowCenters(centerPayloads)
                }
        }
    }

    private fun syncCenterPayload(centerPayload: CenterPayloadEntity?) {
        viewModelScope.launch {
            _syncCenterPayloadsUiState.value =
                SyncCenterPayloadsUiState.ShowProgressbar
            try {
                repository.createCenter(centerPayload)

                deleteAndUpdateCenterPayload(
                    mCenterPayloads[centerSyncIndex].id,
                )
            } catch (e: Exception) {
                _syncCenterPayloadsUiState.value =
                    SyncCenterPayloadsUiState.ShowError(e.message.toString())
                updateCenterPayload(centerPayload)
            }
        }
    }

    private fun deleteAndUpdateCenterPayload(id: Int) {
        viewModelScope.launch {
            _syncCenterPayloadsUiState.value =
                SyncCenterPayloadsUiState.ShowProgressbar
            repository.deleteAndUpdateCenterPayloads(id)
                .catch { error ->
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowError(error.message ?: "")
                }
                .collect { centerPayloads ->
                    centerSyncIndex = 0
                    mCenterPayloads = centerPayloads.toMutableList()
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowCenters(mCenterPayloads)

                    if (mCenterPayloads.isNotEmpty()) {
                        syncCenterPayload()
                    }
                }
        }
    }

    private fun updateCenterPayload(centerPayload: CenterPayloadEntity?) {
        deleteAndUpdateCenterPayload(
            mCenterPayloads[centerSyncIndex].id,
        )
        if (centerPayload != null) {
            viewModelScope.launch {
                try {
                    repository.updateCenterPayload(centerPayload)

                    mCenterPayloads[centerSyncIndex] = centerPayload
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowCenters(mCenterPayloads)
                    centerSyncIndex += 1
                    if (mCenterPayloads.size != centerSyncIndex) {
                        syncCenterPayload()
                    }
                } catch (e: Exception) {
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowError(e.message.toString())
                }
            }
        }
    }

    fun syncCenterPayload() {
        for (i in mCenterPayloads.indices) {
            if (mCenterPayloads[i].errorMessage == null) {
                syncCenterPayload(mCenterPayloads[i])
                centerSyncIndex = i
                break
            } else {
                mCenterPayloads[i].errorMessage?.let {
                    FileUtils.logger.d { it }
                }
            }
        }
    }
}
