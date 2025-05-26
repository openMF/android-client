/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.document.documentDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.DocumentDialogRepository
import com.mifos.core.network.GenericResponse
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.content.PartData
import io.ktor.util.rootCause
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

class DocumentDialogViewModel(
    private val repository: DocumentDialogRepository,
) : ViewModel() {

    private val _documentDialogUiState =
        MutableStateFlow<DocumentDialogUiState>(DocumentDialogUiState.Initial)

    val documentDialogUiState: StateFlow<DocumentDialogUiState>
        get() = _documentDialogUiState

    @OptIn(InternalAPI::class)
    fun createDocument(type: String?, id: Int, name: String?, desc: String?, file: File) {
        _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar
        viewModelScope.launch {
            repository.createDocument(
                type!!, id, name!!, desc!!, getRequestFileBody(file)
            ).collect {result->
                when(result){
                    is DataState.Error<*> -> {
                        when(result.exception){
                            is ClientRequestException, is ServerResponseException -> {
                                _documentDialogUiState.value = DocumentDialogUiState.ShowUploadError(result.exception.message ?: "Server error occurred")
                            }
                            is IOException -> {
                                _documentDialogUiState.value = DocumentDialogUiState.ShowError(result.exception.rootCause?.message ?: "Network error occurred")
                            }
                            is SerializationException -> {
                                _documentDialogUiState.value = DocumentDialogUiState.ShowError("Data parsing error")
                            }
                            else -> {
                                _documentDialogUiState.value = DocumentDialogUiState.ShowError(result.exception.rootCause?.message ?: "Unknown error")
                            }
                        }
                    }
                    DataState.Loading -> {
                        _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar
                    }
                    is DataState.Success<GenericResponse> -> {
                        _documentDialogUiState.value =
                            DocumentDialogUiState.ShowDocumentedCreatedSuccessfully(result.data)
                    }
                }

            }
        }
    }

    fun updateDocument(
        entityType: String?,
        entityId: Int,
        documentId: Int,
        name: String?,
        desc: String?,
        file: File,
    ) {
        _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar
        viewModelScope.launch {
            repository.updateDocument(
                entityType!!,
                entityId,
                documentId,
                name!!,
                desc!!,
                getRequestFileBody(file),
            ).collect { result->
                when(result)
                {
                    is DataState.Error<*> -> {
                        _documentDialogUiState.value =
                            DocumentDialogUiState.ShowError(result.message)
                    }
                    DataState.Loading -> {
                        _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar
                    }
                    is DataState.Success<GenericResponse> -> {
                        _documentDialogUiState.value =
                            DocumentDialogUiState.ShowDocumentUpdatedSuccessfully(result.data)

                    }
                }

            }
        }
    }

    private fun getRequestFileBody(file: File): PartData {
        // create RequestBody instance from file
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        // PartData is used to send also the actual file name
        return PartData.createFormData("file", file.name, requestFile)
    }
}
