package com.mifos.feature.document.document_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.DownloadDocumentUseCase
import com.mifos.core.domain.use_cases.GetDocumentsListUseCase
import com.mifos.core.domain.use_cases.RemoveDocumentUseCase
import com.mifos.feature.document.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentListViewModel @Inject constructor(
    private val getDocumentsListUseCase: GetDocumentsListUseCase,
    private val downloadDocumentUseCase: DownloadDocumentUseCase,
    private val removeDocumentUseCase: RemoveDocumentUseCase
) : ViewModel() {

    private val _documentListUiState =
        MutableStateFlow<DocumentListUiState>(DocumentListUiState.Loading)
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
        viewModelScope.launch(Dispatchers.IO) {
            getDocumentsListUseCase(entityType, entityId).collect { result ->
                when (result) {
                    is Resource.Error -> _documentListUiState.value =
                        DocumentListUiState.Error(R.string.feature_document_failed_to_load_documents_list)

                    is Resource.Loading -> _documentListUiState.value = DocumentListUiState.Loading

                    is Resource.Success -> _documentListUiState.value =
                        DocumentListUiState.DocumentList(result.data ?: emptyList())
                }

            }
        }

    fun downloadDocument(entityType: String, entityId: Int, documentId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            downloadDocumentUseCase(entityType, entityId, documentId).collect { result ->
                when (result) {
                    is Resource.Error -> _documentListUiState.value =
                        DocumentListUiState.Error(R.string.feature_document_failed_to_download_document)

                    is Resource.Loading -> _documentListUiState.value = DocumentListUiState.Loading

                    is Resource.Success -> {
                        _downloadDocumentState.value = true
                        loadDocumentList(entityType, entityId)
                    }
                }
            }

        }

    fun removeDocument(entityType: String, entityId: Int, documentId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            removeDocumentUseCase(entityType, entityId, documentId).collect { result ->
                when (result) {
                    is Resource.Error -> _documentListUiState.value =
                        DocumentListUiState.Error(R.string.feature_document_failed_to_remove_document)

                    is Resource.Loading -> _documentListUiState.value = DocumentListUiState.Loading

                    is Resource.Success -> {
                        _removeDocumentState.value = true
                        loadDocumentList(entityType, entityId)
                    }
                }
            }
        }


}