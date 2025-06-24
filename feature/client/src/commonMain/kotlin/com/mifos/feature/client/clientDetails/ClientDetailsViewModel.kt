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
import coil3.request.ImageResult
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.domain.useCases.GetClientDetailsUseCase
import com.mifos.core.domain.useCases.UploadClientImageUseCase
import com.mifos.core.network.utils.ImageLoaderUtils
import com.mifos.feature.client.utils.createImageRequestBody
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import com.mifos.room.entities.client.ClientEntity
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class ClientDetailsViewModel(
    private val uploadClientImageUseCase: UploadClientImageUseCase,
    private val getClientDetailsUseCase: GetClientDetailsUseCase,
    private val imageLoaderUtils: ImageLoaderUtils,
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

    private val _client = MutableStateFlow<ClientEntity?>(null)
    val client = _client.asStateFlow()

    private val _showLoading = MutableStateFlow(true)
    val showLoading = _showLoading.asStateFlow()

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
                        imageFile.absolutePath(),
                    )
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

    fun saveClientImage(clientId: Int, imageFile: PlatformFile) {
        viewModelScope.launch {
            saveAutoClientImage(clientId, imageFile)
        }
    }

    suspend fun saveAutoClientImage(clientId: Int, imageFile: PlatformFile) {
        try {
            val bytes = FileKit.compressImage(
                file = imageFile,
                imageFormat = ImageFormat.PNG,
                quality = 100,
            )
            val outFile = FileKit.filesDir / "client_image_$clientId.png"
            outFile.write(bytes)
            uploadImage(clientId, outFile)
        } catch (e: Exception) {
            _clientDetailsUiState.value = ClientDetailsUiState.ShowError(e.message.toString())
        }
    }

    suspend fun getClientImageUrl(clientId: Int): ImageResult {
        return imageLoaderUtils.loadImage(clientId)
    }
}
