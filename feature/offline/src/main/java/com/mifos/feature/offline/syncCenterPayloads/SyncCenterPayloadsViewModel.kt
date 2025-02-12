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

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.FileUtils.LOG_TAG
import com.mifos.core.data.repository.SyncCenterPayloadsRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.room.entities.center.CenterPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class SyncCenterPayloadsViewModel @Inject constructor(
    private val prefManager: PrefManager,
    private val repository: SyncCenterPayloadsRepository,
) : ViewModel() {

    private val _syncCenterPayloadsUiState = MutableStateFlow<SyncCenterPayloadsUiState>(
        SyncCenterPayloadsUiState.ShowProgressbar,
    )
    val syncCenterPayloadsUiState: StateFlow<SyncCenterPayloadsUiState> = _syncCenterPayloadsUiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var mCenterPayloads: MutableList<CenterPayload> = mutableListOf()
    private var centerSyncIndex = 0

    fun getUserStatus(): Boolean {
        return prefManager.userStatus
    }

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

    private fun syncCenterPayload(centerPayload: CenterPayload?) {
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

    private fun updateCenterPayload(centerPayload: CenterPayload?) {
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
                    Log.d(
                        LOG_TAG,
                        it,
                    )
                }
            }
        }
    }
}
