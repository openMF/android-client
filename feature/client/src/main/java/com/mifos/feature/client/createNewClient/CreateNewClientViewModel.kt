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

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.feature.client.R
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

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
                    CreateNewClientUiState.ShowError(R.string.feature_client_failed_to_fetch_client_template)
            }.collect {
                _createNewClientUiState.value =
                    CreateNewClientUiState.ShowClientTemplate(it ?: ClientsTemplateEntity())
            }
        }
    }

    private fun loadOffices() {
        viewModelScope.launch {
            repository.offices()
                .catch {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowError(R.string.feature_client_failed_to_fetch_offices)
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
                        CreateNewClientUiState.ShowError(R.string.feature_client_failed_to_fetch_staffs)
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
                            R.string.feature_client_client_created_successfully,
                        )
                    _createNewClientUiState.value = CreateNewClientUiState.SetClientId(it)
                } ?: run {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowWaitingForCheckerApproval(0)
                }
            } catch (e: HttpException) {
                val errorMessage = e.response()?.errorBody()?.string().orEmpty()
                Log.d("CreateClient", errorMessage)
                _createNewClientUiState.value = CreateNewClientUiState.ShowStringError(
                    "HTTP Error: $errorMessage",
                )
            } catch (e: Exception) {
                val errorMessage = e.message.orEmpty()
                Log.e("CreateClient", "Unexpected error", e)
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

        // MultipartBody.Part is used to send also the actual file name
        val body = MultipartBody.Part.createFormData("file", pngFile.name, requestFile)

        viewModelScope.launch {
            try {
                repository.uploadClientImage(id, body)

                _createNewClientUiState.value =
                    CreateNewClientUiState.OnImageUploadSuccess(R.string.feature_client_Image_Upload_Successful)
            } catch (e: Exception) {
                _createNewClientUiState.value =
                    CreateNewClientUiState.ShowError(R.string.feature_client_Image_Upload_Failed)
            }
        }
    }
}
