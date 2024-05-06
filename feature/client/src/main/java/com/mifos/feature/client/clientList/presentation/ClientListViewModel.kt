package com.mifos.feature.client.clientList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Network
import com.mifos.core.datastore.PrefManager
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Page
import com.mifos.feature.client.clientList.domain.repository.ClientListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 21/02/24.
 */

@HiltViewModel
class ClientListViewModel @Inject constructor(
    private val repository: ClientListRepository,
    private val prefManager: PrefManager
) : ViewModel() {

    private val _clientListUiState = MutableStateFlow<ClientListUiState>(ClientListUiState.Empty)
    val clientListUiState = _clientListUiState.asStateFlow()

    init {
        getClientList()
    }

    // for refresh feature
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshClientList() {
        _isRefreshing.value = true
        getClientList()
        _isRefreshing.value = false
    }

    private fun getClientList() {
        if (prefManager.userStatus) loadClientsFromDb()
        else loadClientsFromApi()
    }

    private fun loadClientsFromApi() = viewModelScope.launch(Dispatchers.IO) {
        val response = repository.getAllClients()
        _clientListUiState.value = ClientListUiState.ClientListApi(response)
    }

    private fun loadClientsFromDb() = viewModelScope.launch(Dispatchers.IO) {
        repository.allDatabaseClients()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Page<Client>>() {
                override fun onCompleted() {
                }

                override fun onError(error: Throwable) {
                    _clientListUiState.value = ClientListUiState.Error("Failed to Fetch Clients")
                }

                override fun onNext(clients: Page<Client>) {
                    _clientListUiState.value = ClientListUiState.ClientListDb(clients.pageItems)
                }
            })
    }

}