package com.mifos.mifosxdroid.offline.synccenterpayloads

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mifos.core.data.CenterPayload
import com.mifos.core.objects.response.SaveResponse
import com.mifos.mifosxdroid.dialogfragments.syncclientsdialog.SyncClientsDialogFragment.Companion.LOG_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _syncCenterPayloadsUiState = MutableStateFlow<SyncCenterPayloadsUiState>(SyncCenterPayloadsUiState.ShowProgressbar)
    val syncCenterPayloadsUiState: StateFlow<SyncCenterPayloadsUiState> = _syncCenterPayloadsUiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var mCenterPayloads: MutableList<CenterPayload> = mutableListOf()
    private var centerSyncIndex = 0

    fun refreshCenterPayloads() {
        _isRefreshing.value = true
        loadDatabaseCenterPayload()
        _isRefreshing.value = false
    }

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
                    mCenterPayloads = centerPayloads.toMutableList()
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowCenters(mCenterPayloads)
                }
            })

    }

    private fun syncCenterPayload(centerPayload: CenterPayload?) {
        _syncCenterPayloadsUiState.value = SyncCenterPayloadsUiState.ShowProgressbar
        repository.createCenter(centerPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<SaveResponse?> {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowError(e.message.toString())
                    updateCenterPayload(centerPayload)
                }

                override fun onNext(center: SaveResponse?) {
                    deleteAndUpdateCenterPayload(
                        mCenterPayloads[centerSyncIndex].id
                    )
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
                    centerSyncIndex = 0
                    mCenterPayloads = centerPayloads.toMutableList()
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowCenters(mCenterPayloads)
                    if (mCenterPayloads.isNotEmpty()) {
                        syncCenterPayload()
                    }
                }
            })

    }

    fun updateCenterPayload(centerPayload: CenterPayload?) {
        deleteAndUpdateCenterPayload(
            mCenterPayloads[centerSyncIndex].id
        )
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
                    mCenterPayloads[centerSyncIndex] = centerPayload
                    _syncCenterPayloadsUiState.value =
                        SyncCenterPayloadsUiState.ShowCenters(mCenterPayloads)
                    centerSyncIndex += 1
                    if (mCenterPayloads.size != centerSyncIndex) {
                        syncCenterPayload()
                    }
                }
            })

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
                        it
                    )
                }
            }
        }
    }

}