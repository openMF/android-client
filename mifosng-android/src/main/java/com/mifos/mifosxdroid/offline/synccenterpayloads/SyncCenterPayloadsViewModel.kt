package com.mifos.mifosxdroid.offline.synccenterpayloads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.data.CenterPayload
import com.mifos.core.objects.response.SaveResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Observer
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class SyncCenterPayloadsViewModel @Inject constructor(private val repository: SyncCenterPayloadsRepository) :
    ViewModel() {

    private val _syncCenterPayloadsUiState = MutableLiveData<SyncCenterPayloadsUiState>()

    val syncCenterPayloadsUiState: LiveData<SyncCenterPayloadsUiState>
        get() = _syncCenterPayloadsUiState

    fun loadDatabaseCenterPayload() {
        repository.allDatabaseCenterPayload()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<CenterPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowError(e.message.toString())
                }

                override fun onNext(centerPayloads: List<CenterPayload>) {
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowCenters(centerPayloads)
                }
            })

    }

    fun syncCenterPayload(centerPayload: CenterPayload?) {
        _syncCenterPayloadsUiState.value = SyncCenterPayloadsUiState.ShowProgressbar
        repository.createCenter(centerPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<SaveResponse?> {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowCenterSyncFailed(e.message.toString())
                }

                override fun onNext(center: SaveResponse?) {
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowCenterSyncResponse
                }
            })

    }

    fun deleteAndUpdateCenterPayload(id: Int) {
        _syncCenterPayloadsUiState.value = SyncCenterPayloadsUiState.ShowProgressbar
        repository.deleteAndUpdateCenterPayloads(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<List<CenterPayload>> {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowError(e.message.toString())
                }

                override fun onNext(centerPayloads: List<CenterPayload>) {
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowPayloadDeletedAndUpdatePayloads(centerPayloads)
                }
            })

    }

    fun updateCenterPayload(centerPayload: CenterPayload?) {
        _syncCenterPayloadsUiState.value = SyncCenterPayloadsUiState.ShowCenterSyncResponse
        repository.updateCenterPayload(centerPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<CenterPayload>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowError(e.message.toString())
                }

                override fun onNext(centerPayload: CenterPayload) {
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowCenterPayloadUpdated(centerPayload)
                }
            })

    }
}