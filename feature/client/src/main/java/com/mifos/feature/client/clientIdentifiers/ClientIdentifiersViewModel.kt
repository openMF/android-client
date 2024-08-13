package com.mifos.feature.client.clientIdentifiers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.DeleteIdentifierUseCase
import com.mifos.core.domain.use_cases.GetClientIdentifiersUseCase
import com.mifos.feature.client.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientIdentifiersViewModel @Inject constructor(
    private val getClientIdentifiersUseCase: GetClientIdentifiersUseCase,
    private val deleteIdentifierUseCase: DeleteIdentifierUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val clientId = savedStateHandle.getStateFlow(key = Constants.CLIENT_ID, initialValue = 0)

    private val _clientIdentifiersUiState =
        MutableStateFlow<ClientIdentifiersUiState>(ClientIdentifiersUiState.Loading)
    val clientIdentifiersUiState = _clientIdentifiersUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshIdentifiersList(clientId: Int) {
        _isRefreshing.value = true
        loadIdentifiers(clientId = clientId)
        _isRefreshing.value = false
    }

    fun loadIdentifiers(clientId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getClientIdentifiersUseCase(clientId).collect { result ->
            when (result) {
                is Resource.Error -> _clientIdentifiersUiState.value =
                    ClientIdentifiersUiState.Error(
                        R.string.feature_client_failed_to_load_client_identifiers
                    )

                is Resource.Loading -> _clientIdentifiersUiState.value =
                    ClientIdentifiersUiState.Loading

                is Resource.Success -> _clientIdentifiersUiState.value =
                    ClientIdentifiersUiState.ClientIdentifiers(result.data ?: emptyList())
            }
        }
    }

    fun deleteIdentifier(clientId: Int, identifierId: Int) = viewModelScope.launch(Dispatchers.IO) {
        deleteIdentifierUseCase(clientId, identifierId).collect { result ->
            when (result) {
                is Resource.Error -> _clientIdentifiersUiState.value =
                    ClientIdentifiersUiState.Error(R.string.feature_client_failed_to_delete_identifier)

                is Resource.Loading -> _clientIdentifiersUiState.value =
                    ClientIdentifiersUiState.Loading

                is Resource.Success -> {
                    _clientIdentifiersUiState.value =
                        ClientIdentifiersUiState.IdentifierDeletedSuccessfully
                    loadIdentifiers(clientId)
                }
            }
        }
    }
}