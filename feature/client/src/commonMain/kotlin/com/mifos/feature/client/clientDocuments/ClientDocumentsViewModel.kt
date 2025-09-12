/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientDocuments

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_documents_failed_to_delete
import androidclient.feature.client.generated.resources.no_internet_message
import androidclient.feature.client.generated.resources.unknown_error
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.noncoreobjects.Document
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.DocumentSelectAndUploadRepository
import com.mifos.feature.client.EntityDocumentState
import com.mifos.feature.client.EntityDocumentState.EntityType
import com.mifos.feature.client.utils.openPdfWithDefaultExternalApp
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class ClientDocumentsViewModel(
    savedStateHandle: SavedStateHandle,
    private val documentsRepository: DocumentListRepository,
    private val documentSelectAndUploadRepository: DocumentSelectAndUploadRepository,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<
    ClientDocumentsScreenState,
    ClientDocumentsEvents,
    ClientDocumentsActions,
    >(
    initialState = ClientDocumentsScreenState(
        clientId = savedStateHandle.toRoute<ClientDocumentsRoute>().clientId,
    ),
) {

    private val route = savedStateHandle.toRoute<ClientDocumentsRoute>()
    private val entityType = "clients"

    private val entityDocumentStateFlow =
        documentSelectAndUploadRepository.entityDocumentStateMutableStateFlow

    init {
        observeNetworkAndLoadDocuments()
        observeForRefresh()
    }

    override fun handleAction(action: ClientDocumentsActions) {
        when (action) {
            ClientDocumentsActions.AddDocument -> {
                documentSelectAndUploadRepository.updateStep(EntityDocumentState.Step.ADD)
                sendEvent(ClientDocumentsEvents.OnAddDocument)
            }

            ClientDocumentsActions.CloseDialog -> {
                nullDialogState()
            }

            is ClientDocumentsActions.ConfirmDeleteDocument -> {
                deleteDocument(action.documentId)
                observeNetworkAndLoadDocuments()
            }

            is ClientDocumentsActions.DeleteDocument -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientDocumentsScreenState.DialogState.ConfirmDocumentDeletion(
                            documentName = action.documentName,
                            documentId = action.documentId,
                        ),
                    )
                }
            }

            ClientDocumentsActions.NavigateBack -> {
                sendEvent(ClientDocumentsEvents.OnNavigateBack)
            }

            ClientDocumentsActions.Refresh -> {
                mutableStateFlow.update {
                    it.copy(pullDownRefresh = true)
                }
                observeNetworkAndLoadDocuments()
            }

            is ClientDocumentsActions.UpdateSearchQuery -> {
                mutableStateFlow.update {
                    it.copy(searchText = action.query)
                }
            }
            is ClientDocumentsActions.ViewDocument -> {
                downloadDownloadAndCache(action.documentId)
            }

            is ClientDocumentsActions.OpenExternalPdfViewer -> {
                previewPdfInExternalApp(action.platformFile)
            }

            ClientDocumentsActions.SearchDocument -> {
                observeNetworkAndLoadDocuments()
            }

            ClientDocumentsActions.ToggleSearch -> {
                mutableStateFlow.update {
                    it.copy(isSearchBarActive = !it.isSearchBarActive)
                }
            }
        }
    }

    private fun observeNetworkAndLoadDocuments() {
        viewModelScope.launch {
            val isConnected = observeNetwork()
            updateNetworkState(isConnected)
            when (isConnected) {
                true -> {
                    documentsRepository.getDocumentsList(
                        entityType = entityType,
                        route.clientId,
                    ).collect { dataState ->
                        when (dataState) {
                            is DataState.Error<*> -> {
                                errorDialogState(dataState.message)
                                mutableStateFlow.update {
                                    it.copy(
                                        pullDownRefresh = false,
                                    )
                                }
                            }
                            DataState.Loading -> {
                                loadingDialogState()
                            }
                            is DataState.Success -> {
                                updateEntityDocumentState()
                                nullDialogState()
                                mutableStateFlow.update {
                                    it.copy(
                                        clientDocuments = dataState.data.reversed()
                                            .filter { document ->
                                                document.fileName?.contains(state.searchText) ?: false
                                            },
                                        pullDownRefresh = false,
                                    )
                                }
                            }
                        }
                    }
                }
                false -> {
                    errorDialogState(getString(Res.string.no_internet_message))
                }
            }
            documentSelectAndUploadRepository.resetRefreshState()
        }
    }

    private fun deleteDocument(documentId: Int) {
        viewModelScope.launch {
            loadingDialogState()
            runCatching {
                documentsRepository.removeDocument(
                    entityId = route.clientId,
                    entityType = entityType,
                    documentId = documentId,
                )
            }.onFailure {
                errorDialogState(it.message ?: getString(Res.string.client_documents_failed_to_delete))
            }.onSuccess {
                nullDialogState()
                sendAction(ClientDocumentsActions.Refresh)
            }
        }
    }

    private fun downloadDownloadAndCache(documentId: Int) {
        viewModelScope.launch {
            val isConnected = observeNetwork()
            updateNetworkState(isConnected)
            when (isConnected) {
                true -> {
                    entityDocumentStateFlow.update {
                        it.copy(documentId = documentId)
                    }
                    documentSelectAndUploadRepository.downloadDocumentAndCache().collect { dataState ->
                        when (dataState) {
                            is DataState.Error<*> -> {
                                errorDialogState(dataState.message)
                            }
                            DataState.Loading -> {
                                loadingDialogState()
                            }
                            is DataState.Success -> {
                                documentSelectAndUploadRepository.updateEntityDocument(platformFile = dataState.data)
                                nullDialogState()
                                if (dataState.data.extension == "pdf") {
                                    sendAction(ClientDocumentsActions.OpenExternalPdfViewer(dataState.data))
                                } else {
                                    // Uncomment them when you want to enable document update on backend.
                                    // And also enable the button on the UI Screen, for SubmitMode.UPDATE.
                                    // ( do this after uncommenting these line)
//                                documentSelectAndUploadRepository.updateStep(step = EntityDocumentState.Step.UPDATE_PREVIEW)
//                                documentSelectAndUploadRepository.changeSubmitMode(EntityDocumentState.SubmitMode.UPDATE)
                                    sendEvent(ClientDocumentsEvents.OnViewDocument)
                                }
                            }
                        }
                    }
                }
                false -> {
                    errorDialogState(getString(Res.string.no_internet_message))
                }
            }
        }
    }

    private fun previewPdfInExternalApp(platformFile: PlatformFile) {
        viewModelScope.launch {
            loadingDialogState()
            try {
                openPdfWithDefaultExternalApp(platformFile)
            } catch (e: Exception) {
                errorDialogState(e.message ?: getString(Res.string.unknown_error))
            }
            nullDialogState()
        }
    }

    private suspend fun observeNetwork() = networkMonitor.isOnline.first()

    private fun updateNetworkState(isConnected: Boolean) {
        mutableStateFlow.update {
            it.copy(isNetworkConnected = isConnected)
        }
    }
    private fun loadingDialogState() {
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientDocumentsScreenState.DialogState.Loading,
            )
        }
    }
    private fun errorDialogState(message: String) {
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientDocumentsScreenState.DialogState.Error(message),
            )
        }
    }
    private fun nullDialogState() {
        mutableStateFlow.update {
            it.copy(dialogState = null)
        }
    }

    private fun updateEntityDocumentState() {
        viewModelScope.launch {
            mutableStateFlow.collect { clientState ->
                entityDocumentStateFlow.update {
                    it.copy(
                        entityId = clientState.clientId,
                        entityType = EntityType.Clients,
                    )
                }
            }
        }
    }

    private fun observeForRefresh() {
        viewModelScope.launch {
            entityDocumentStateFlow.collect { clientDocuments ->
                if (clientDocuments.doARefresh) {
                    sendAction(ClientDocumentsActions.Refresh)
                }
            }
        }
    }
}

data class ClientDocumentsScreenState(
    val clientId: Int = -1,
    val pullDownRefresh: Boolean = false,
    val clientDocuments: List<Document> = emptyList(),
    val searchText: String = "",
    val isNetworkConnected: Boolean = false,
    val dialogState: DialogState? = null,
    val isSearchBarActive: Boolean = false,
) {
    sealed interface DialogState {
        object Loading : DialogState
        data class Error(val message: String) : DialogState
        data class ConfirmDocumentDeletion(val documentName: String, val documentId: Int) : DialogState
    }
}

sealed interface ClientDocumentsEvents {
    object OnNavigateBack : ClientDocumentsEvents
    object OnViewDocument : ClientDocumentsEvents
    object OnAddDocument : ClientDocumentsEvents
}

sealed interface ClientDocumentsActions {
    data object NavigateBack : ClientDocumentsActions
    data class ViewDocument(
        val documentId: Int,
    ) : ClientDocumentsActions
    data class DeleteDocument(val documentName: String, val documentId: Int) :
        ClientDocumentsActions

    data class OpenExternalPdfViewer(val platformFile: PlatformFile) : ClientDocumentsActions
    data class ConfirmDeleteDocument(val documentId: Int) : ClientDocumentsActions
    data object Refresh : ClientDocumentsActions
    data object AddDocument : ClientDocumentsActions
    data object ToggleSearch : ClientDocumentsActions
    data object SearchDocument : ClientDocumentsActions
    data class UpdateSearchQuery(val query: String) : ClientDocumentsActions
    data object CloseDialog : ClientDocumentsActions
}
