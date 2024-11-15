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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.CreateClientIdentifierUseCase
import com.mifos.core.domain.use_cases.GetClientIdentifierTemplateUseCase
import com.mifos.core.objects.noncore.IdentifierPayload
import com.mifos.core.objects.noncore.IdentifierTemplate
import com.mifos.feature.client.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientIdentifiersDialogViewModel @Inject constructor(
    private val getClientIdentifierTemplateUseCase: GetClientIdentifierTemplateUseCase,
    private val createClientIdentifierUseCase: CreateClientIdentifierUseCase,
) : ViewModel() {

    private val _clientIdentifierDialogUiState =
        MutableStateFlow<ClientIdentifierDialogUiState>(ClientIdentifierDialogUiState.Loading)
    val clientIdentifierDialogUiState = _clientIdentifierDialogUiState.asStateFlow()

    fun loadClientIdentifierTemplate(clientId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getClientIdentifierTemplateUseCase(clientId).collect { result ->
            when (result) {
                is Resource.Error ->
                    _clientIdentifierDialogUiState.value =
                        ClientIdentifierDialogUiState.Error(R.string.feature_client_failed_to_load_identifiers)

                is Resource.Loading ->
                    _clientIdentifierDialogUiState.value =
                        ClientIdentifierDialogUiState.Loading

                is Resource.Success ->
                    _clientIdentifierDialogUiState.value =
                        ClientIdentifierDialogUiState.ClientIdentifierTemplate(
                            result.data ?: IdentifierTemplate(),
                        )
            }
        }
    }

    fun createClientIdentifier(clientId: Int, identifierPayload: IdentifierPayload) =
        viewModelScope.launch(Dispatchers.IO) {
            createClientIdentifierUseCase(clientId, identifierPayload).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _clientIdentifierDialogUiState.value =
                            ClientIdentifierDialogUiState.Error(R.string.feature_client_failed_to_create_identifier)

                    is Resource.Loading ->
                        _clientIdentifierDialogUiState.value =
                            ClientIdentifierDialogUiState.Loading

                    is Resource.Success ->
                        _clientIdentifierDialogUiState.value =
                            ClientIdentifierDialogUiState.IdentifierCreatedSuccessfully
                }
            }
        }
}
