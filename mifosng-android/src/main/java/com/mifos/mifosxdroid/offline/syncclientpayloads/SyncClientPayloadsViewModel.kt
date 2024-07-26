package com.mifos.mifosxdroid.offline.syncclientpayloads

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
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
class SyncClientPayloadsViewModel @Inject constructor(
    private val repository: SyncClientPayloadsRepository
) : ViewModel() {

    private val _syncClientPayloadsRepository =
        MutableStateFlow<SyncClientPayloadsUiState>(SyncClientPayloadsUiState.ShowProgressbar)

    val syncClientPayloadsUiState: StateFlow<SyncClientPayloadsUiState>
        get() = _syncClientPayloadsRepository

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var mClientPayloads: MutableList<ClientPayload> = mutableListOf()
    private var mClientSyncIndex = 0

    fun refreshClientPayloads() {
        _isRefreshing.value = true
        loadDatabaseClientPayload()
        _isRefreshing.value = false
    }

    fun loadDatabaseClientPayload() {
        _syncClientPayloadsRepository.value = SyncClientPayloadsUiState.ShowProgressbar
        repository.allDatabaseClientPayload()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<ClientPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncClientPayloadsRepository.value =
                        SyncClientPayloadsUiState.ShowError(e.message.toString())
                }

                override fun onNext(clientPayloads: List<ClientPayload>) {
                    mClientPayloads = clientPayloads.toMutableList()
                    _syncClientPayloadsRepository.value =
                        SyncClientPayloadsUiState.ShowPayloads(mClientPayloads)
                }
            })

    }

    private fun syncClientPayload(clientPayload: ClientPayload?) {
        _syncClientPayloadsRepository.value = SyncClientPayloadsUiState.ShowProgressbar
        repository.createClient(clientPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Client> {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncClientPayloadsRepository.value =
                        SyncClientPayloadsUiState.ShowError(e.message.toString())
                    updateClientPayload(clientPayload)
                }

                override fun onNext(client: Client) {
                    mClientPayloads[mClientSyncIndex].id?.let {
                        mClientPayloads[mClientSyncIndex].clientCreationTime?.let { it1 ->
                            deleteAndUpdateClientPayload(
                                it,
                                it1
                            )
                        }
                    }
                }
            })

    }

    fun deleteAndUpdateClientPayload(id: Int, clientCreationTIme: Long) {
        _syncClientPayloadsRepository.value = SyncClientPayloadsUiState.ShowProgressbar
        repository.deleteAndUpdatePayloads(id, clientCreationTIme)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<List<ClientPayload>> {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncClientPayloadsRepository.value =
                        SyncClientPayloadsUiState.ShowError(e.message.toString())
                }

                override fun onNext(clientPayloads: List<ClientPayload>) {
                    mClientSyncIndex = 0
                    if (clientPayloads.isNotEmpty()) {
                        syncClientPayload()
                    }
                    mClientPayloads = clientPayloads.toMutableList()
                    _syncClientPayloadsRepository.value =
                        SyncClientPayloadsUiState.ShowPayloads(mClientPayloads)
                }
            })

    }

    fun updateClientPayload(clientPayload: ClientPayload?) {
        _syncClientPayloadsRepository.value = SyncClientPayloadsUiState.ShowProgressbar
        repository.updateClientPayload(clientPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ClientPayload>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncClientPayloadsRepository.value =
                        SyncClientPayloadsUiState.ShowError(e.message.toString())
                }

                override fun onNext(clientPayload: ClientPayload) {
                    mClientPayloads[mClientSyncIndex] = clientPayload
                    mClientSyncIndex += 1
                    if (mClientPayloads.size != mClientSyncIndex) {
                        syncClientPayload()
                    }
                }
            })

    }

    fun syncClientPayload() {
        for (i in mClientPayloads.indices) {
            if (mClientPayloads[i].errorMessage == null) {
                syncClientPayload(mClientPayloads[i])
                mClientSyncIndex = i
                break
            } else {
                mClientPayloads[i].errorMessage?.let {
                    Log.d(
                        LOG_TAG,
                        it
                    )
                }
            }
        }
    }
}