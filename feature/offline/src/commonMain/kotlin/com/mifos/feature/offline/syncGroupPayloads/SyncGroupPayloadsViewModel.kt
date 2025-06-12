/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncGroupPayloads

import androidclient.feature.offline.generated.resources.Res
import androidclient.feature.offline.generated.resources.feature_offline_error_failed_to_load_groupPayload
import androidclient.feature.offline.generated.resources.feature_offline_error_failed_to_update_list
import androidclient.feature.offline.generated.resources.feature_offline_error_group_sync_failed
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.SyncGroupPayloadsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.room.entities.group.GroupPayloadEntity
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
class SyncGroupPayloadsViewModel(
    private val prefManager: UserPreferencesRepository,
    private val repository: SyncGroupPayloadsRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    val syncGroupPayloadsUiState get() = _syncGroupPayloadsUiState
    private val _syncGroupPayloadsUiState = MutableStateFlow<SyncGroupPayloadsUiState>(
        SyncGroupPayloadsUiState.Loading,
    )

    val groupPayloadsList get() = _groupPayloadsList
    private val _groupPayloadsList = MutableStateFlow<List<GroupPayloadEntity>>(listOf())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var groupPayloadSyncIndex = 0

    val isNetworkAvailable = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    /***
     * check use cases names
     */
    fun refreshGroupPayload() {
        _isRefreshing.value = true
        loanDatabaseGroupPayload()
        _isRefreshing.value = false
    }

    val userStatus: StateFlow<Boolean> = prefManager.userInfo
        .map { it.userStatus }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    fun loanDatabaseGroupPayload() {
        viewModelScope.launch {
            repository.allDatabaseGroupPayload()
                .collect { state ->
                    when (state) {
                        is DataState.Success -> {
                            val list = state.data
                            _groupPayloadsList.value = list
                            _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.Success(
                                if (list.isEmpty()) {
                                    GroupPayloadEmptyState.NOTHING_TO_SYNC
                                } else {
                                    null
                                },
                            )
                        }

                        is DataState.Error -> {
                            _syncGroupPayloadsUiState.value =
                                SyncGroupPayloadsUiState.Error(Res.string.feature_offline_error_failed_to_load_groupPayload)
                        }

                        is DataState.Loading -> {
                            _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.Loading
                        }
                    }
                }
        }
    }

    fun syncGroupPayloadFromStart() {
        groupPayloadSyncIndex = 0
        syncGroupPayload()
    }

    private fun syncGroupPayload() {
        groupPayloadsList.value.indexOfFirst { it.errorMessage == null }.takeIf { it != -1 }
            ?.let { index ->
                groupPayloadSyncIndex = index
                syncGroupPayload(groupPayloadsList.value[index])
            }
    }

    private fun syncGroupPayload(groupPayload: GroupPayloadEntity?) {
        viewModelScope.launch {
            _syncGroupPayloadsUiState.value =
                SyncGroupPayloadsUiState.Loading
            try {
                repository.createGroup(groupPayload!!)
                deleteAndUpdateGroupPayload()
            } catch (e: Exception) {
                _syncGroupPayloadsUiState.value =
                    SyncGroupPayloadsUiState.Error(Res.string.feature_offline_error_group_sync_failed)
                updateGroupPayload()
            }
        }
    }

    private fun deleteAndUpdateGroupPayload() {
        viewModelScope.launch {
            val id = groupPayloadsList.value[groupPayloadSyncIndex].id
            repository.deleteAndUpdateGroupPayloads(id)
                .collect { state ->
                    when (state) {
                        is DataState.Success -> {
                            val updatedList = state.data
                            groupPayloadSyncIndex = 0
                            _groupPayloadsList.value = updatedList
                            _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.Success(
                                if (updatedList.isEmpty()) GroupPayloadEmptyState.ALL_SYNCED else null,
                            )
                        }

                        is DataState.Error -> {
                            _syncGroupPayloadsUiState.value =
                                SyncGroupPayloadsUiState.Error(Res.string.feature_offline_error_failed_to_update_list)
                        }

                        is DataState.Loading -> {
                            _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.Loading
                        }
                    }
                }
        }
    }

    private fun updateGroupPayload() {
        viewModelScope.launch {
            val groupPayload = groupPayloadsList.value[groupPayloadSyncIndex]
            _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.Loading
            try {
                repository.updateGroupPayload(groupPayload)

                _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.Success()

                val payloadList = groupPayloadsList.value.toMutableList()
                payloadList[groupPayloadSyncIndex] = groupPayload
                groupPayloadsList.value = payloadList
                groupPayloadSyncIndex += 1
                if (groupPayloadsList.value.size != groupPayloadSyncIndex) {
                    syncGroupPayload()
                }
            } catch (e: Exception) {
                _syncGroupPayloadsUiState.value =
                    SyncGroupPayloadsUiState.Error(Res.string.feature_offline_error_failed_to_load_groupPayload)
            }
        }
    }
}
