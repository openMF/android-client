/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncClientPayloads

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.FileUtils.LOG_TAG
import com.mifos.core.data.repository.SyncClientPayloadsRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.entity.client.Client
import com.mifos.core.entity.client.ClientPayload
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
    private val repository: SyncClientPayloadsRepository,
    private val prefManager: PrefManager,
) : ViewModel() {

    private val _syncClientPayloadsUiState =
        MutableStateFlow<SyncClientPayloadsUiState>(SyncClientPayloadsUiState.ShowProgressbar)

    val syncClientPayloadsUiState: StateFlow<SyncClientPayloadsUiState>
        get() = _syncClientPayloadsUiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var mClientPayloads: MutableList<ClientPayload> = mutableListOf()
    private var mClientSyncIndex = 0

    fun getUserStatus(): Boolean {
        return prefManager.userStatus
    }

    fun refreshClientPayloads() {
        _isRefreshing.value = true
        loadDatabaseClientPayload()
        _isRefreshing.value = false
    }

    fun loadDatabaseClientPayload() {
        _syncClientPayloadsUiState.value = SyncClientPayloadsUiState.ShowProgressbar
        repository.allDatabaseClientPayload()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<ClientPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncClientPayloadsUiState.value =
                        SyncClientPayloadsUiState.ShowError(e.message.toString())
                }

                override fun onNext(clientPayloads: List<ClientPayload>) {
                    mClientPayloads = clientPayloads.toMutableList()
                    _syncClientPayloadsUiState.value =
                        SyncClientPayloadsUiState.ShowPayloads(mClientPayloads)
                }
            })
    }

    private fun syncClientPayload(clientPayload: ClientPayload?) {
        _syncClientPayloadsUiState.value = SyncClientPayloadsUiState.ShowProgressbar
        repository.createClient(clientPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Client> {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncClientPayloadsUiState.value =
                        SyncClientPayloadsUiState.ShowError(e.message.toString())
                    updateClientPayload(clientPayload)
                }

                override fun onNext(client: Client) {
                    mClientPayloads[mClientSyncIndex].id?.let {
                        mClientPayloads[mClientSyncIndex].clientCreationTime?.let { it1 ->
                            deleteAndUpdateClientPayload(
                                it,
                                it1,
                            )
                        }
                    }
                }
            })
    }

    fun deleteAndUpdateClientPayload(id: Int, clientCreationTIme: Long) {
        _syncClientPayloadsUiState.value = SyncClientPayloadsUiState.ShowProgressbar
        repository.deleteAndUpdatePayloads(id, clientCreationTIme)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<List<ClientPayload>> {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncClientPayloadsUiState.value =
                        SyncClientPayloadsUiState.ShowError(e.message.toString())
                }

                override fun onNext(clientPayloads: List<ClientPayload>) {
                    mClientSyncIndex = 0
                    if (clientPayloads.isNotEmpty()) {
                        syncClientPayload()
                    }
                    mClientPayloads = clientPayloads.toMutableList()
                    _syncClientPayloadsUiState.value =
                        SyncClientPayloadsUiState.ShowPayloads(mClientPayloads)
                }
            })
    }

    fun updateClientPayload(clientPayload: ClientPayload?) {
        _syncClientPayloadsUiState.value = SyncClientPayloadsUiState.ShowProgressbar
        repository.updateClientPayload(clientPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ClientPayload>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncClientPayloadsUiState.value =
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
                        it,
                    )
                }
            }
        }
    }
}
