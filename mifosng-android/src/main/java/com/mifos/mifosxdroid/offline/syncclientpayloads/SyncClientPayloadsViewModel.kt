package com.mifos.mifosxdroid.offline.syncclientpayloads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
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
class SyncClientPayloadsViewModel @Inject constructor(private val repository: SyncClientPayloadsRepository) :
    ViewModel() {

    private val _syncClientPayloadsRepository = MutableLiveData<SyncClientPayloadsUiState>()

    val syncClientPayloadsUiState: LiveData<SyncClientPayloadsUiState>
        get() = _syncClientPayloadsRepository

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
                    _syncClientPayloadsRepository.value =
                        SyncClientPayloadsUiState.ShowPayloads(clientPayloads)
                }
            })

    }

    fun syncClientPayload(clientPayload: ClientPayload?) {
        _syncClientPayloadsRepository.value = SyncClientPayloadsUiState.ShowProgressbar
        repository.createClient(clientPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Client> {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncClientPayloadsRepository.value =
                        SyncClientPayloadsUiState.ShowError(e.message.toString())
                }

                override fun onNext(client: Client) {
                    _syncClientPayloadsRepository.value = SyncClientPayloadsUiState.ShowSyncResponse
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
                    _syncClientPayloadsRepository.value =
                        SyncClientPayloadsUiState.ShowPayloadDeletedAndUpdatePayloads(clientPayloads)
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
                    _syncClientPayloadsRepository.value =
                        SyncClientPayloadsUiState.ShowClientPayloadUpdated(clientPayload)
                }
            })

    }
}