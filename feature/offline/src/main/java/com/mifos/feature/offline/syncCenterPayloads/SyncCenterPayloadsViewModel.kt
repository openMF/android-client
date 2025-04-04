/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncCenterPayloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.FileUtils
import com.mifos.core.data.repository.SyncCenterPayloadsRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.room.entities.center.CenterPayloadEntity
import kotlinx.coroutines.Dispatchers
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
class SyncCenterPayloadsViewModel(
    private val prefManager: UserPreferencesRepository,
    private val repository: SyncCenterPayloadsRepository,
) : ViewModel() {

    private val _syncCenterPayloadsUiState = MutableStateFlow<SyncCenterPayloadsUiState>(
        SyncCenterPayloadsUiState.ShowProgressbar,
    )
    val syncCenterPayloadsUiState: StateFlow<SyncCenterPayloadsUiState> = _syncCenterPayloadsUiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var mCenterPayloads: MutableList<CenterPayloadEntity> = mutableListOf()
    private var centerSyncIndex = 0

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
        viewModelScope.launch(Dispatchers.IO) {
            repository.allDatabaseCenterPayload()
                .catch {
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowError(it.message.toString())
                }.collect { mCenterPayloads ->
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowCenters(mCenterPayloads)
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
                .catch {
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowError(it.message.toString())
                }.collect {
                    centerSyncIndex = 0
                    mCenterPayloads = it.toMutableList()
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
//                    Log.d(
//                        FileUtils.logger,
//                        it,
//                    )
                }
            }
        }
    }
}
