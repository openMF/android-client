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
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData
import io.ktor.http.headersOf
import io.ktor.util.rootCause
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException



class DocumentDialogViewModel(
    private val repository: DocumentDialogRepository,
) : ViewModel() {

    private val _documentDialogUiState =
        MutableStateFlow<DocumentDialogUiState>(DocumentDialogUiState.Initial)

    val documentDialogUiState: StateFlow<DocumentDialogUiState>
        get() = _documentDialogUiState

    fun openFilePicker(onFilePicked: (PlatformFile?) -> Unit) {
        viewModelScope.launch {
            try {
                val file = FileKit.openFilePicker(type = FileKitType.Image)
                onFilePicked(file)
            } catch (e: Exception) {
                e.printStackTrace()
                onFilePicked(null)
            }
        }
    }


    @OptIn(InternalAPI::class)
    fun createDocument(type: String, id: Int, name: String, desc: String, file: PlatformFile) {
        _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar
        viewModelScope.launch {
            repository.createDocument(
                entityId = id,
                entityType = type,
                name=name,
                desc = desc,
                file = getRequestFileBody(file,name)
            ).collect { state ->
                when(state){
                    is DataState.Error -> DocumentDialogUiState.ShowError(state.message)
                    DataState.Loading -> DocumentDialogUiState.ShowProgressbar
                    is DataState.Success -> DocumentDialogUiState.ShowDocumentUpdatedSuccessfully(state.data)
                }

            }
        }
    }

     fun updateDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
        name: String,
        desc: String,
        file: PlatformFile,
    ) {
        _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar
         viewModelScope.launch {
             repository.updateDocument(
                 entityType,
                 entityId,
                 documentId,
                 name,
                 desc,
                 getRequestFileBody(file,name),
             ).collect { state ->
                 when(state){
                     is DataState.Error -> DocumentDialogUiState.ShowError(state.message)
                     DataState.Loading -> DocumentDialogUiState.ShowProgressbar
                     is DataState.Success -> DocumentDialogUiState.ShowDocumentUpdatedSuccessfully(state.data)
                 }

             }
         }
    }

    @OptIn(InternalAPI::class)
    private suspend fun getRequestFileBody(file: PlatformFile, name:String): MultiPartFormDataContent {
        val formData = MultiPartFormDataContent(
            formData {
                append(
                    "file",
                    file,
                    Headers.build {
                        append(HttpHeaders.ContentType, "multipart/form-data")
                        append(HttpHeaders.ContentDisposition, "filename=\"$name\"")
                    },
                )
            },
        )
        return formData
    }
}
