package com.mifos.mifosxdroid.offline.syncgrouppayloads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.response.SaveResponse
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _syncGroupPayloadsUiState = MutableLiveData<SyncGroupPayloadsUiState>()

    val syncGroupPayloadsUiState: LiveData<SyncGroupPayloadsUiState>
        get() = _syncGroupPayloadsUiState

    fun loanDatabaseGroupPayload() {
        _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.ShowProgressbar
        repository.allDatabaseGroupPayload()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<GroupPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.ShowError(R.string.failed_to_load_grouppayload)
                }

                override fun onNext(groupPayloads: List<GroupPayload>) {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.ShowGroups(groupPayloads)
                }
            })

    }

    fun syncGroupPayload(groupPayload: GroupPayload?) {
        _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.ShowProgressbar
        repository.createGroup(groupPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SaveResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.ShowGroupSyncFailed(e.message.toString())
                }

                override fun onNext(group: SaveResponse) {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.ShowGroupSyncResponse(group)
                }
            })

    }

    fun deleteAndUpdateGroupPayload(id: Int) {
        repository.deleteAndUpdateGroupPayloads(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<GroupPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.ShowError(R.string.failed_to_update_list)
                }

                override fun onNext(groupPayloads: List<GroupPayload>) {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.ShowPayloadDeletedAndUpdatePayloads(groupPayloads)
                }
            })

    }

    fun updateGroupPayload(groupPayload: GroupPayload?) {
        _syncGroupPayloadsUiState.value = SyncGroupPayloadsUiState.ShowProgressbar
        repository.updateGroupPayload(groupPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GroupPayload>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.ShowError(R.string.failed_to_load_grouppayload)
                }

                override fun onNext(groupPayload: GroupPayload) {
                    _syncGroupPayloadsUiState.value =
                        SyncGroupPayloadsUiState.ShowGroupPayloadUpdated(groupPayload)
                }
            })

    }
}