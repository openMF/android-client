package com.mifos.feature.groups.sync_group_payloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SyncGroupPayloadsRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.domain.use_cases.AllDatabaseGroupPayloadUseCase
import com.mifos.core.domain.use_cases.CreateGroupUseCase
import com.mifos.core.domain.use_cases.DeleteAndUpdateGroupPayloadUseCase
import com.mifos.core.domain.use_cases.UpdateGroupPayloadUseCase
import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.response.SaveResponse
import com.mifos.feature.groups.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class SyncGroupPayloadsViewModel @Inject constructor(
    private val allDatabaseGroupPayloadUseCase: AllDatabaseGroupPayloadUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val deleteAndUpdateGroupPayloadUseCase: DeleteAndUpdateGroupPayloadUseCase,
    private val updateGroupPayloadUseCase: UpdateGroupPayloadUseCase,
    private val prefManager: PrefManager
) : ViewModel() {

    val syncGroupPayloadsUiState get() = _syncGroupPayloadsUiState
    private val _syncGroupPayloadsUiState =
        MutableStateFlow<SyncGroupPayloadsUiState>(SyncGroupPayloadsUiState.Loading)

    val groupPayloadsList get() = _groupPayloadsList
    private val _groupPayloadsList = MutableStateFlow<List<GroupPayload>>(listOf())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var groupPayloadSyncIndex = 0

    /***
     * check use cases names
     */
    fun refreshGroupPayload() {
        _isRefreshing.value = true
        loanDatabaseGroupPayload()
        _isRefreshing.value = false
    }

    fun getUserStatus() : Boolean {
        return prefManager.userStatus
    }

    fun loanDatabaseGroupPayload() = viewModelScope.launch {
        allDatabaseGroupPayloadUseCase().collect { result ->
            when (result) {
                is Resource.Error -> _syncGroupPayloadsUiState.value =
                    SyncGroupPayloadsUiState.Error(R.string.feature_groups_error_failed_to_load_groupPayload)

                is Resource.Loading -> _syncGroupPayloadsUiState.value =
                    SyncGroupPayloadsUiState.Loading

                is Resource.Success -> {
                    _groupPayloadsList.value = result.data ?: emptyList()
                    _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.Success(
                        if ((result.data
                                ?: emptyList()).isEmpty()
                        ) GroupPayloadEmptyState.NOTHING_TO_SYNC
                        else null
                    )
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

    private fun syncGroupPayload(groupPayload: GroupPayload?) = viewModelScope.launch {
        createGroupUseCase(groupPayload!!).collect { result ->
            when (result) {
                is Resource.Error -> {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.Error(R.string.feature_groups_error_group_sync_failed)
                    updateGroupPayload()
                }

                is Resource.Loading -> _syncGroupPayloadsUiState.value =
                    SyncGroupPayloadsUiState.Loading

                is Resource.Success -> deleteAndUpdateGroupPayload()
            }
        }
    }

    private fun deleteAndUpdateGroupPayload() = viewModelScope.launch {
        val id = groupPayloadsList.value[groupPayloadSyncIndex].id

        deleteAndUpdateGroupPayloadUseCase(id).collect { result ->
            when (result) {
                is Resource.Error -> _syncGroupPayloadsUiState.value =
                    SyncGroupPayloadsUiState.Error(R.string.feature_groups_error_failed_to_update_list)

                is Resource.Loading -> Unit

                is Resource.Success -> {
                    groupPayloadSyncIndex = 0
                    _groupPayloadsList.value = result.data ?: emptyList()
                    _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.Success(
                        if ((result.data ?: emptyList()).isEmpty()
                        ) GroupPayloadEmptyState.ALL_SYNCED
                        else null
                    )
                }
            }
        }
    }

    private fun updateGroupPayload() = viewModelScope.launch {
        val groupPayload = groupPayloadsList.value[groupPayloadSyncIndex]

        updateGroupPayloadUseCase(groupPayload).collect { result ->
            when (result) {
                is Resource.Error -> _syncGroupPayloadsUiState.value =
                    SyncGroupPayloadsUiState.Error(R.string.feature_groups_error_failed_to_load_groupPayload)

                is Resource.Loading -> _syncGroupPayloadsUiState.value =
                    SyncGroupPayloadsUiState.Loading

                is Resource.Success -> {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.Success()
                    val payloadList = groupPayloadsList.value.toMutableList()
                    payloadList[groupPayloadSyncIndex] = groupPayload
                    groupPayloadsList.value = payloadList
                    groupPayloadSyncIndex += 1
                    if (groupPayloadsList.value.size != groupPayloadSyncIndex) {
                        syncGroupPayload()
                    }
                }
            }
        }
    }
}