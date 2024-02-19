package com.mifos.feature.client.clientList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.datastore.PrefManager
import com.mifos.feature.client.clientList.domain.repository.ClientListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientListViewModel @Inject constructor(
    private val repository: ClientListRepository,
    private val prefManager: PrefManager
) : ViewModel() {

    init {
        getClientList()
    }

    private val _clientListUiState = MutableStateFlow<ClientListUiState>(ClientListUiState.Empty)
    val clientListUiState = _clientListUiState.asStateFlow()

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
        _clientListUiState.value = ClientListUiState.ClientListApi(repository.getAllClients())
    }

    private fun loadClientsFromDb() = viewModelScope.launch(Dispatchers.IO) {
        repository.allDatabaseClients()
            .collect {
                _clientListUiState.value = ClientListUiState.ClientListDb(it.pageItems)
            }
    }

}