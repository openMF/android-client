/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientIdentifiersDialog

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_create_identifier
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_identifiers
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.CreateClientIdentifierUseCase
import com.mifos.core.domain.useCases.GetClientIdentifierTemplateUseCase
import com.mifos.core.model.objects.noncoreobjects.IdentifierPayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClientIdentifiersDialogViewModel(
    private val getClientIdentifierTemplateUseCase: GetClientIdentifierTemplateUseCase,
    private val createClientIdentifierUseCase: CreateClientIdentifierUseCase,
) : ViewModel() {

    private val _clientIdentifierDialogUiState =
        MutableStateFlow<ClientIdentifierDialogUiState>(ClientIdentifierDialogUiState.Loading)
    val clientIdentifierDialogUiState = _clientIdentifierDialogUiState.asStateFlow()

    fun loadClientIdentifierTemplate(clientId: Int) = viewModelScope.launch {
        getClientIdentifierTemplateUseCase(clientId).collect { result ->
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

    fun createClientIdentifier(clientId: Int, identifierPayload: IdentifierPayload) =
        viewModelScope.launch {
            createClientIdentifierUseCase(clientId, identifierPayload).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _clientIdentifierDialogUiState.value =
                            ClientIdentifierDialogUiState.Error(Res.string.feature_client_failed_to_create_identifier)

                    is DataState.Loading ->
                        _clientIdentifierDialogUiState.value =
                            ClientIdentifierDialogUiState.Loading

                    is DataState.Success ->
                        _clientIdentifierDialogUiState.value =
                            ClientIdentifierDialogUiState
                                .IdentifierCreatedSuccessfully
                }
            }
        }

    fun resetUiState() {
        _clientIdentifierDialogUiState.value = ClientIdentifierDialogUiState.Loading
    }
}
