/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.document.documentDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.DocumentCreateUpdateRepository
import com.mifos.core.ui.util.multipartRequestBody
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.readBytes
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DocumentDialogViewModel(
    private val repository: DocumentCreateUpdateRepository,
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
        description: String,
        file: PlatformFile,
    ) {
        viewModelScope.launch {
            _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar
            repository.createDocument(
                entityType = entityType,
                entityId = entityId,
                file = multipartRequestBody(
                    file = file.readBytes(),
                    name = documentName,
                    extension = file.extension,
                    description = description,
                ),
            )
                .catch {
                    _documentDialogUiState.value =
                        DocumentDialogUiState.ShowError(it.message.toString())
                }
                .collect {
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
            _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar
            repository.updateDocument(
                entityType,
                entityId,
                documentId,
                file = multipartRequestBody(
                    file = file.readBytes(),
                    name = documentName,
                    extension = file.extension,
                    description = description,
                ),
            )
                .catch {
                    _documentDialogUiState.value =
                        DocumentDialogUiState.ShowError(it.message.toString())
                }
                .collect {
                    _documentDialogUiState.value =
                        DocumentDialogUiState.ShowDocumentUpdatedSuccessfully
                }
        }
    }
}
