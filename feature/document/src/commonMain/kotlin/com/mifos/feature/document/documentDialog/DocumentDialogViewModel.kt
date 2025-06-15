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
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.util.DeflateEncoder.name
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DocumentDialogViewModel(
    private val repository: DocumentDialogRepository,
) : ViewModel() {

    private val _documentDialogUiState =
        MutableStateFlow<DocumentDialogUiState>(DocumentDialogUiState.Initial)

    val documentDialogUiState = _documentDialogUiState.asStateFlow()

    fun openFilePicker(onFilePicked: (PlatformFile?) -> Unit) {
        viewModelScope.launch {
            try {
                val file = FileKit.openFilePicker(
                    type = FileKitType.File(
                        extensions = listOf(
                            "xls",
                            "xlsx",
                            "pdf",
                            "doc",
                            "docx",
                            "png",
                            "jpeg",
                            "jpg",
                        ),
                    ),
                )
                onFilePicked(file)
            } catch (e: Exception) {
                e.printStackTrace()
                onFilePicked(null)
            }
        }
    }

    fun resetDialogUiState() {
        _documentDialogUiState.value = DocumentDialogUiState.Initial
    }

    @OptIn(InternalAPI::class)
    fun createDocument(
        entityType: String,
        entityId: Int,
        documentName: String,
        desciption: String,
        file: PlatformFile,
    ) {
        viewModelScope.launch {
            val result = repository.createDocument(
                entityType = entityType,
                entityId = entityId,
                file = createDocumentRequestBody(file, documentName, desciption),
            )
            when (result) {
                is DataState.Error ->
                    _documentDialogUiState.value =
                        DocumentDialogUiState.ShowError(result.message)

                DataState.Loading -> _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar

                is DataState.Success ->
                    _documentDialogUiState.value =
                        DocumentDialogUiState.ShowDocumentedCreatedSuccessfully
            }
        }
    }

    fun updateDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
        documentName: String,
        description: String,
        file: PlatformFile,
    ) {
        viewModelScope.launch {
            val result = repository.updateDocument(
                entityType,
                entityId,
                documentId,
                createDocumentRequestBody(file, documentName, description),
            )
            when (result) {
                is DataState.Error ->
                    _documentDialogUiState.value =
                        DocumentDialogUiState.ShowError(result.message)

                DataState.Loading -> _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar

                is DataState.Success ->
                    _documentDialogUiState.value =
                        DocumentDialogUiState.ShowDocumentUpdatedSuccessfully
            }
        }
    }

    @OptIn(InternalAPI::class)
    private suspend fun createDocumentRequestBody(
        file: PlatformFile,
        name: String,
        description: String,
    ): MultiPartFormDataContent {
        val mimeType = getMimeTypeFromPlatformFile(file)
        val byteArray = file.readBytes()
        return MultiPartFormDataContent(
            formData {
                append(
                    "file",
                    byteArray,
                    Headers.build {
                        append(HttpHeaders.ContentType, mimeType)
                        append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                    },
                )
                append("name", name)
                append("description", description)
            },
        )
    }

    fun getMimeTypeFromPlatformFile(file: PlatformFile): String {
        return when (file.extension.lowercase()) {
            "jpeg", "jpg" -> "image/jpeg"
            "png" -> "image/png"
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            else -> "application/octet-stream"
        }
    }
}
