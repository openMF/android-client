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
import com.mifos.core.ui.util.multipartRequestBody
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
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
            repository.createDocument(
                entityType = entityType,
                entityId = entityId,
                file = multipartRequestBody(file, documentName, desciption),
            ).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _documentDialogUiState.value =
                            DocumentDialogUiState.ShowError(result.message)

                    DataState.Loading ->
                        _documentDialogUiState.value =
                            DocumentDialogUiState.ShowProgressbar

                    is DataState.Success ->
                        _documentDialogUiState.value =
                            DocumentDialogUiState.ShowDocumentedCreatedSuccessfully
                }
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
            repository.updateDocument(
                entityType,
                entityId,
                documentId,
                multipartRequestBody(file, documentName, description),
            ).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _documentDialogUiState.value =
                            DocumentDialogUiState.ShowError(result.message)

                    DataState.Loading ->
                        _documentDialogUiState.value =
                            DocumentDialogUiState.ShowProgressbar

                    is DataState.Success ->
                        _documentDialogUiState.value =
                            DocumentDialogUiState.ShowDocumentUpdatedSuccessfully
                }
            }
        }
    }
}
