/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientIdentifiers

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_create_identifier
import androidclient.feature.client.generated.resources.feature_client_failed_to_delete_identifier
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client_identifiers
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_identifiers
import androidclient.feature.client.generated.resources.feature_client_identifier_created_successfully
import androidclient.feature.client.generated.resources.feature_client_identifier_deleted_successfully
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.domain.useCases.CreateClientIdentifierUseCase
import com.mifos.core.domain.useCases.DeleteIdentifierUseCase
import com.mifos.core.domain.useCases.GetClientIdentifierTemplateUseCase
import com.mifos.core.model.objects.noncoreobjects.IdentifierPayload
import com.mifos.feature.client.clientIdentifiersDialog.ClientIdentifierDialogUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
class ClientIdentifiersViewModel(
    private val clientIdentifiersRepository: ClientIdentifiersRepository,
    private val deleteIdentifierUseCase: DeleteIdentifierUseCase,
    private val getClientIdentifierTemplateUseCase: GetClientIdentifierTemplateUseCase,
    private val createClientIdentifierUseCase: CreateClientIdentifierUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val clientId = savedStateHandle.getStateFlow(key = Constants.CLIENT_ID, initialValue = 0)

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog = _showCreateDialog.asStateFlow()

    private val _events = MutableSharedFlow<ClientIdentifiersEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()
    private val _clientIdentifiersUiState =
        MutableStateFlow<ClientIdentifiersUiState>(ClientIdentifiersUiState.Loading)
    val clientIdentifiersUiState = _clientIdentifiersUiState.asStateFlow()

    private val _clientIdentifierDialogUiState =
        MutableStateFlow<ClientIdentifierDialogUiState>(ClientIdentifierDialogUiState.Loading)
    val clientIdentifierDialogUiState = _clientIdentifierDialogUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        loadIdentifiers()
    }

    fun showCreateIdentifierDialog() {
        loadClientIdentifierTemplate()
        _showCreateDialog.value = true
    }

    fun hideCreateIdentifierDialog() {
        _showCreateDialog.value = false
    }

    fun refreshIdentifiersList() = viewModelScope.launch {
        _isRefreshing.value = true
        clientIdentifiersRepository.getClientIdentifiers(clientId.value).collect { result ->
            when (result) {
                is DataState.Error -> {
                    _clientIdentifiersUiState.value =
                        ClientIdentifiersUiState.Error(Res.string.feature_client_failed_to_load_client_identifiers)
                    _isRefreshing.value = false
                }
                is DataState.Loading -> {
                    _clientIdentifiersUiState.value =
                        ClientIdentifiersUiState.Loading
                }
                is DataState.Success -> {
                    _clientIdentifiersUiState.value =
                        ClientIdentifiersUiState.ClientIdentifiers(result.data)
                    _isRefreshing.value = false
                }
            }
        }
    }

    fun loadIdentifiers() = viewModelScope.launch {
        clientIdentifiersRepository.getClientIdentifiers(clientId.value).collect { result ->
            when (result) {
                is DataState.Error ->
                    _clientIdentifiersUiState.value =
                        ClientIdentifiersUiState.Error(Res.string.feature_client_failed_to_load_client_identifiers)

                is DataState.Loading ->
                    _clientIdentifiersUiState.value =
                        ClientIdentifiersUiState.Loading

                is DataState.Success ->
                    _clientIdentifiersUiState.value =
                        ClientIdentifiersUiState.ClientIdentifiers(result.data)
            }
        }
    }

    fun deleteIdentifier(identifierId: Int) = viewModelScope.launch {
        deleteIdentifierUseCase(clientId.value, identifierId).collect { result ->
            when (result) {
                is DataState.Error ->
                    _clientIdentifiersUiState.value =
                        ClientIdentifiersUiState.Error(Res.string.feature_client_failed_to_delete_identifier)

                is DataState.Loading -> {
                }

                is DataState.Success -> {
                    _events.tryEmit(
                        ClientIdentifiersEvent.ShowMessage(
                            Res.string.feature_client_identifier_deleted_successfully,
                        ),
                    )
                    loadIdentifiers()
                }
            }
        }
    }

    fun loadClientIdentifierTemplate() = viewModelScope.launch {
        getClientIdentifierTemplateUseCase(clientId.value).collect { result ->
            when (result) {
                is DataState.Error ->
                    _clientIdentifierDialogUiState.value =
                        ClientIdentifierDialogUiState.Error(Res.string.feature_client_failed_to_load_identifiers)

                is DataState.Loading ->
                    _clientIdentifierDialogUiState.value =
                        ClientIdentifierDialogUiState.Loading

                is DataState.Success ->
                    _clientIdentifierDialogUiState.value =
                        ClientIdentifierDialogUiState.ClientIdentifierTemplate(
                            result.data,
                        )
            }
        }
    }

    fun createClientIdentifier(identifierPayload: IdentifierPayload) =
        viewModelScope.launch {
            hideCreateIdentifierDialog()
            _clientIdentifiersUiState.value = ClientIdentifiersUiState.Loading
            createClientIdentifierUseCase(clientId.value, identifierPayload).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        _events.tryEmit(
                            ClientIdentifiersEvent.ShowMessage(
                                Res.string.feature_client_failed_to_create_identifier,
                            ),
                        )
                        loadIdentifiers()
                    }

                    is DataState.Loading -> {
                    }

                    is DataState.Success -> {
                        _events.tryEmit(
                            ClientIdentifiersEvent.ShowMessage(
                                Res.string.feature_client_identifier_created_successfully,
                            ),
                        )
                        loadIdentifiers()
                    }
                }
            }
        }
    sealed interface ClientIdentifiersEvent {
        data class ShowMessage(val message: StringResource) : ClientIdentifiersEvent
    }
}
