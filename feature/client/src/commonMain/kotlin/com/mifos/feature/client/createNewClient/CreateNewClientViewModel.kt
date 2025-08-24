/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createNewClient

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_Image_Upload_Failed
import androidclient.feature.client.generated.resources.feature_client_Image_Upload_Successful
import androidclient.feature.client.generated.resources.feature_client_client_created_successfully
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_address_configuration
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_address_template
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_client_template
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_offices
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_staffs
import androidclient.feature.client.generated.resources.feature_client_waiting_for_checker_approval
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.MFErrorParser
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.feature.client.utils.compressImage
import com.mifos.feature.client.utils.createImageRequestBody
import com.mifos.room.entities.client.AddressTemplate
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class CreateNewClientViewModel(
    private val repository: CreateNewClientRepository,
) : ViewModel() {

    private val _createNewClientUiState =
        MutableStateFlow<CreateNewClientUiState>(CreateNewClientUiState.ShowProgressbar)
    val createNewClientUiState: StateFlow<CreateNewClientUiState> get() = _createNewClientUiState

    private val _staffInOffices = MutableStateFlow<List<StaffEntity>>(emptyList())
    val staffInOffices: StateFlow<List<StaffEntity>> get() = _staffInOffices

    private val _showOffices = MutableStateFlow<List<OfficeEntity>>(emptyList())
    val showOffices: StateFlow<List<OfficeEntity>> get() = _showOffices

    private val isAddressEnabled = MutableStateFlow(false)

    private val addressTemplate = MutableStateFlow<AddressTemplate?>(null)

    private val selectedImage = MutableStateFlow<PlatformFile?>(null)

    fun updateSelectedImage(image: PlatformFile?) {
        selectedImage.value = image
    }

    fun loadOfficeAndClientTemplate() {
        _createNewClientUiState.value = CreateNewClientUiState.ShowProgressbar
        // todo combine these 2
        loadClientTemplate()
        loadOffices()
    }

    private fun loadClientTemplate() {
        viewModelScope.launch {
            repository.clientTemplate().catch {
                _createNewClientUiState.value =
                    CreateNewClientUiState.ShowError(Res.string.feature_client_failed_to_fetch_client_template)
            }.collect {
                loadAddressConfiguration()
                _createNewClientUiState.value =
                    CreateNewClientUiState.ShowClientTemplate(
                        clientsTemplate = it.data ?: ClientsTemplateEntity(),
                        isAddressEnabled = isAddressEnabled.value,
                        addressTemplate = addressTemplate.value ?: AddressTemplate(),
                    )
            }
        }
    }

    private fun loadOffices() {
        viewModelScope.launch {
            repository.offices()
                .catch {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowError(Res.string.feature_client_failed_to_fetch_offices)
                }.collect { offices ->
                    _showOffices.value = offices.data ?: emptyList()
                }
        }
    }

    fun loadStaffInOffices(officeId: Int) {
        viewModelScope.launch {
            repository.getStaffInOffice(officeId).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _createNewClientUiState.value =
                            CreateNewClientUiState.ShowError(Res.string.feature_client_failed_to_fetch_staffs)
                    DataState.Loading -> Unit
                    is DataState.Success -> _staffInOffices.value = result.data
                }
            }
        }
    }

    suspend fun loadAddressConfiguration() {
        try {
            val addressConfig = repository.getAddressConfiguration()
            isAddressEnabled.value = addressConfig.enabled

            if (addressConfig.enabled) {
                loadAddressTemplate()
            }
        } catch (e: Exception) {
            _createNewClientUiState.value =
                CreateNewClientUiState.ShowError(Res.string.feature_client_failed_to_fetch_address_configuration)
        }
    }

    suspend fun loadAddressTemplate() {
        try {
            val template = repository.getAddressTemplate()
            addressTemplate.value = template
        } catch (e: Exception) {
            _createNewClientUiState.value =
                CreateNewClientUiState.ShowError(Res.string.feature_client_failed_to_fetch_address_template)
        }
    }

    fun createClient(clientPayload: ClientPayloadEntity) {
        viewModelScope.launch {
            _createNewClientUiState.value = CreateNewClientUiState.ShowProgressbar

            try {
                val clientId = repository.createClient(clientPayload)

                clientId?.let {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowClientCreatedSuccessfully(
                            Res.string.feature_client_client_created_successfully,
                        )
                    _createNewClientUiState.value = CreateNewClientUiState.SetClientId(it)
                } ?: run {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowWaitingForCheckerApproval(Res.string.feature_client_waiting_for_checker_approval)
                }
            } catch (e: Exception) {
                MFErrorParser.errorMessage(e)
            }
        }
    }

    fun uploadImage(id: Int) {
        if (selectedImage.value == null) {
            return
        }
        _createNewClientUiState.value =
            CreateNewClientUiState.ShowProgress("Uploading Client's Picture...")

        viewModelScope.launch {
            try {
                val compressedImage = compressImage(selectedImage.value!!, id.toString())
                val requestFile = createImageRequestBody(compressedImage)

                repository.uploadClientImage(id, requestFile)

                _createNewClientUiState.value =
                    CreateNewClientUiState.OnImageUploadSuccess(Res.string.feature_client_Image_Upload_Successful)
            } catch (e: Exception) {
                _createNewClientUiState.value =
                    CreateNewClientUiState.ShowError(Res.string.feature_client_Image_Upload_Failed)
                MFErrorParser.errorMessage(e)
            }
        }
    }
}
