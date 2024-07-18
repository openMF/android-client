package com.mifos.mifosxdroid.offline.syncgrouppayloads

import androidx.lifecycle.ViewModel
import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.response.SaveResponse
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class SyncGroupPayloadsViewModel @Inject constructor(private val repository: SyncGroupPayloadsRepository) :
    ViewModel() {

    val syncGroupPayloadsUiState get() = _syncGroupPayloadsUiState
    private val _syncGroupPayloadsUiState =
        MutableStateFlow<SyncGroupPayloadsUiState>(SyncGroupPayloadsUiState.Loading)

    val groupPayloadsList get() = _groupPayloadsList
    private val _groupPayloadsList = MutableStateFlow<List<GroupPayload>>(listOf())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var groupPayloadSyncIndex = 0

    fun refreshGroupPayload() {
        _isRefreshing.value = true
        loanDatabaseGroupPayload()
        _isRefreshing.value = false
    }

    fun loanDatabaseGroupPayload() {
        _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.Loading
        repository.allDatabaseGroupPayload()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<GroupPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.Error(R.string.failed_to_load_grouppayload)
                }

                override fun onNext(groupPayloads: List<GroupPayload>) {
                    _groupPayloadsList.value = groupPayloads
                    _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.Success(
                        if (groupPayloads.isEmpty()) GroupPayloadEmptyState.NOTHING_TO_SYNC
                        else null
                    )
                }
            })

    }

    fun syncGroupPayloadFromStart() {
        groupPayloadSyncIndex = 0
        syncGroupPayload()
    }

    fun syncGroupPayload() {
        groupPayloadsList.value.indexOfFirst { it.errorMessage == null }.takeIf { it != -1 }
            ?.let { index ->
                groupPayloadSyncIndex = index
                syncGroupPayload(groupPayloadsList.value[index])
            }
    }

    private fun syncGroupPayload(groupPayload: GroupPayload?) {
        _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.Loading
        repository.createGroup(groupPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SaveResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.Error(R.string.group_sync_failed)
                    updateGroupPayload()
                }

                override fun onNext(group: SaveResponse) {
                    deleteAndUpdateGroupPayload()
                }
            })
    }

    fun deleteAndUpdateGroupPayload() {
        val id = groupPayloadsList.value[groupPayloadSyncIndex].id
        repository.deleteAndUpdateGroupPayloads(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<GroupPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.Error(R.string.failed_to_update_list)
                }

                override fun onNext(groupPayloads: List<GroupPayload>) {
                    groupPayloadSyncIndex = 0
                    _groupPayloadsList.value = groupPayloads
                    _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.Success(
                        if (groupPayloads.isEmpty()) GroupPayloadEmptyState.ALL_SYNCED
                        else null
                    )
                }
            })

    }

    fun updateGroupPayload() {
        val groupPayload = groupPayloadsList.value[groupPayloadSyncIndex]
        _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.Loading
        repository.updateGroupPayload(groupPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GroupPayload>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.Error(R.string.failed_to_load_grouppayload)
                }

                override fun onNext(groupPayload: GroupPayload) {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.Success()
                    var payloadList = groupPayloadsList.value.toMutableList()
                    payloadList[groupPayloadSyncIndex] = groupPayload
                    groupPayloadsList.value = payloadList
                    groupPayloadSyncIndex += 1
                    if (groupPayloadsList.value.size != groupPayloadSyncIndex) {
                        syncGroupPayload()
                    }
                }
            })

    }
}