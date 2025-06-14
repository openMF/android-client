/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.document.documentList

import androidclient.feature.document.generated.resources.Res
import androidclient.feature.document.generated.resources.feature_document_failed_to_download_document
import androidclient.feature.document.generated.resources.feature_document_failed_to_load_documents_list
import androidclient.feature.document.generated.resources.feature_document_failed_to_remove_document
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.DownloadDocumentUseCase
import com.mifos.core.domain.useCases.GetDocumentsListUseCase
import com.mifos.core.domain.useCases.RemoveDocumentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DocumentListViewModel(
    private val getDocumentsListUseCase: GetDocumentsListUseCase,
    private val downloadDocumentUseCase: DownloadDocumentUseCase,
    private val removeDocumentUseCase: RemoveDocumentUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val entityId = savedStateHandle.getStateFlow(key = Constants.ENTITY_ID, initialValue = 1)
    val entityType = savedStateHandle.getStateFlow(key = Constants.ENTITY_TYPE, initialValue = "")

    private val _documentListUiState = MutableStateFlow<DocumentListUiState>(DocumentListUiState.Loading)
    val documentListUiState = _documentListUiState.asStateFlow()

    private val _removeDocumentState = MutableStateFlow(false)
    val removeDocumentState = _removeDocumentState.asStateFlow()

    private val _downloadDocumentState = MutableStateFlow(false)
    val downloadDocumentState = _downloadDocumentState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshDocumentList(type: String, id: Int) {
        _isRefreshing.value = true
        loadDocumentList(type, id)
        _isRefreshing.value = false
    }

    fun loadDocumentList(entityType: String, entityId: Int) =
        viewModelScope.launch {
            getDocumentsListUseCase(entityType, entityId).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _documentListUiState.value =
                            DocumentListUiState.Error(Res.string.feature_document_failed_to_load_documents_list)

                    is DataState.Loading -> _documentListUiState.value = DocumentListUiState.Loading

                    is DataState.Success ->
                        _documentListUiState.value =
                            DocumentListUiState.DocumentList(result.data)
                }
            }
        }

    fun downloadDocument(entityType: String, entityId: Int, documentId: Int) =
        viewModelScope.launch {
            downloadDocumentUseCase(entityType, entityId, documentId).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _documentListUiState.value =
                            DocumentListUiState.Error(Res.string.feature_document_failed_to_download_document)

                    is DataState.Loading -> _documentListUiState.value = DocumentListUiState.Loading

                    is DataState.Success -> {
                        _downloadDocumentState.value = true
                        loadDocumentList(entityType, entityId)
                    }
                }
            }
        }

    fun removeDocument(entityType: String, entityId: Int, documentId: Int) =
        viewModelScope.launch {
            removeDocumentUseCase(entityType, entityId, documentId).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _documentListUiState.value =
                            DocumentListUiState.Error(Res.string.feature_document_failed_to_remove_document)

                    is DataState.Loading -> _documentListUiState.value = DocumentListUiState.Loading

                    is DataState.Success -> {
                        _removeDocumentState.value = true
                        loadDocumentList(entityType, entityId)
                    }
                }
            }
        }
}
