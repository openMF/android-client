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
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_client_template
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_offices
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_staffs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class CreateNewClientViewModel(
    private val repository: CreateNewClientRepository,
//    private val clientTemplateUseCase: ClientTemplateUseCase,
//    private val getOfficeListUseCase: GetOfficeListUseCase,
) : ViewModel() {

    private val _createNewClientUiState =
        MutableStateFlow<CreateNewClientUiState>(CreateNewClientUiState.ShowProgressbar)
    val createNewClientUiState: StateFlow<CreateNewClientUiState> get() = _createNewClientUiState

    private val _staffInOffices = MutableStateFlow<List<StaffEntity>>(emptyList())
    val staffInOffices: StateFlow<List<StaffEntity>> get() = _staffInOffices

    private val _showOffices = MutableStateFlow<List<OfficeEntity>>(emptyList())
    val showOffices: StateFlow<List<OfficeEntity>> get() = _showOffices

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
                _createNewClientUiState.value =
                    CreateNewClientUiState.ShowClientTemplate(it as ClientsTemplateEntity)
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
                    _showOffices.value = offices
                }
        }
    }

    fun loadStaffInOffices(officeId: Int) {
        viewModelScope.launch {
            repository.getStaffInOffice(officeId)
                .catch {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowError(Res.string.feature_client_failed_to_fetch_staffs)
                }.collect { staffs ->
                    _staffInOffices.value = staffs
                }
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
                        CreateNewClientUiState.ShowWaitingForCheckerApproval(0)
                }
            } catch (e: ClientRequestException) {
                val errorMessage = e.message
                Logger.d("CreateClient: $errorMessage", e)
                _createNewClientUiState.value = CreateNewClientUiState.ShowStringError(
                    "HTTP Error: $errorMessage",
                )
            } catch (e: ServerResponseException) {
                val errorMessage = e.message
                Logger.d("CreateClient: $errorMessage", e)
                _createNewClientUiState.value = CreateNewClientUiState.ShowStringError(
                    "HTTP Error: $errorMessage",
                )
            } catch (e: Exception) {
                val errorMessage = e.message.orEmpty()
                Logger.e("CreateClient: Unexpected error", e)
                // Todo check if we need to assign value to uiState here. else remove
                _createNewClientUiState.value = CreateNewClientUiState.ShowStringError(
                    "Unexpected Error: $errorMessage",
                )
            }
        }
    }

    fun uploadImage(id: Int, pngFile: File) {
        _createNewClientUiState.value =
            CreateNewClientUiState.ShowProgress("Uploading Client's Picture...")
//        val imagePath = pngFile.absolutePath

        // create RequestBody instance from file
        val requestFile = pngFile.asRequestBody("image/png".toMediaTypeOrNull())

        // PartData is used to send also the actual file name
        val body = PartData.createFormData("file", pngFile.name, requestFile)

        viewModelScope.launch {
            try {
                repository.uploadClientImage(id, body)

                _createNewClientUiState.value =
                    CreateNewClientUiState.OnImageUploadSuccess(Res.string.feature_client_Image_Upload_Successful)
            } catch (e: Exception) {
                _createNewClientUiState.value =
                    CreateNewClientUiState.ShowError(Res.string.feature_client_Image_Upload_Failed)
            }
        }
    }
}
