/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.domain.useCases.GetClientDetailsUseCase
import com.mifos.core.domain.useCases.UploadClientImageUseCase
import com.mifos.core.ui.util.imageToByteArray
import com.mifos.feature.client.utils.compressImage
import com.mifos.feature.client.utils.createImageRequestBody
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import com.mifos.room.entities.client.ClientEntity
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class ClientDetailsViewModel(
    private val uploadClientImageUseCase: UploadClientImageUseCase,
    private val getClientDetailsUseCase: GetClientDetailsUseCase,
    private val clientDetailsRepo: ClientDetailsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val clientId = savedStateHandle.getStateFlow(key = Constants.CLIENT_ID, initialValue = 0)

    private val _clientDetailsUiState =
        MutableStateFlow<ClientDetailsUiState>(ClientDetailsUiState.Empty)
    val clientDetailsUiState = _clientDetailsUiState.asStateFlow()

    private val loanAccounts = MutableStateFlow<List<LoanAccountEntity>?>(null)
    val loanAccount = loanAccounts.asStateFlow()

    private val _savingsAccounts = MutableStateFlow<List<SavingsAccountEntity>?>(null)
    val savingsAccounts = _savingsAccounts.asStateFlow()

    private var _profileImage = MutableStateFlow<ByteArray?>(null)
    val profileImage = _profileImage.asStateFlow()

    private val _client = MutableStateFlow<ClientEntity?>(null)
    val client = _client.asStateFlow()

    private val _showLoading = MutableStateFlow(true)
    val showLoading = _showLoading.asStateFlow()

    init {
        viewModelScope.launch {
            getUserProfile()
        }
    }
    private fun uploadImage(id: Int, imageFile: PlatformFile) = viewModelScope.launch {
        uploadClientImageUseCase(id, createImageRequestBody(imageFile)).collect { result ->
            when (result) {
                is DataState.Error -> {
                    _clientDetailsUiState.value =
                        ClientDetailsUiState.ShowError(result.message)
                    _showLoading.value = false
                }

                is DataState.Loading -> {
                    _showLoading.value = true
                }

                is DataState.Success -> {
                    _clientDetailsUiState.value = ClientDetailsUiState.ShowUploadImageSuccessfully(
                        result.data,
                    )
                    getUserProfile()
                    _showLoading.value = false
                }
            }
        }
    }

    fun deleteClientImage(clientId: Int) = viewModelScope.launch {
        _showLoading.value = true
        try {
            clientDetailsRepo.deleteClientImage(clientId)

            _clientDetailsUiState.value =
                ClientDetailsUiState.ShowClientImageDeletedSuccessfully
            _profileImage.value = null
            _showLoading.value = false
        } catch (e: Exception) {
            _clientDetailsUiState.value =
                ClientDetailsUiState.ShowError(e.message ?: "Unexpected error")
            _showLoading.value = false
        }
    }

    fun loadClientDetailsAndClientAccounts(clientId: Int) = viewModelScope.launch {
        getClientDetailsUseCase(clientId).collect { result ->
            when (result) {
                is DataState.Error -> {
                    _clientDetailsUiState.value =
                        ClientDetailsUiState.ShowError(result.message)
                    _showLoading.value = false
                }

                is DataState.Loading -> _showLoading.value = true

                is DataState.Success -> {
                    _client.value = result.data.client
                    loanAccounts.value = result.data.clientAccounts?.loanAccounts
                    _savingsAccounts.value = result.data.clientAccounts?.savingsAccounts
                    _showLoading.value = false
                }
            }
        }
    }

    fun saveClientImage(clientId: Int, imageFile: PlatformFile?) {
        imageFile ?: return
        viewModelScope.launch {
            try {
                _showLoading.value = true
                val compressed = compressImage(imageFile, clientId.toString())
                uploadImage(clientId, compressed)
            } catch (e: Exception) {
                _showLoading.value = false
                _clientDetailsUiState.value = ClientDetailsUiState.ShowError(e.message ?: "Unexpected error")
            }
        }
    }

    suspend fun getUserProfile() {
        clientDetailsRepo.getImage(clientId.value).collect { result ->
            when (result) {
                is DataState.Error -> {}
                DataState.Loading -> _showLoading.value = true
                is DataState.Success -> {
                    _profileImage.value = imageToByteArray(result.data)
                }
            }
        }
    }
}
