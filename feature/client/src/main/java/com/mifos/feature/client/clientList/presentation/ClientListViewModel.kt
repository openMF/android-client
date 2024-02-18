package com.mifos.feature.client.clientList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.feature.client.clientList.domain.repository.ClientListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientListViewModel @Inject constructor(private val repository: ClientListRepository) :
    ViewModel() {

    val clientListPagingData = repository.getAllClients()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshClientList() = viewModelScope.launch(Dispatchers.IO) {
        _isRefreshing.value = true
        delay(3000)
        _isRefreshing.value = false
    }

}