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
import androidclient.feature.client.generated.resources.no_internet_message
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
import com.mifos.feature.client.clientDocuments.ClientDocumentsScreenState.DialogState.ConfirmDocumentDeletion
import io.ktor.http.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
        updateEntityDetails()
    }


    override fun handleAction(action: ClientDocumentsActions) {
        when (action) {
            ClientDocumentsActions.CloseDialog -> {
                observeNetworkAndLoadDocuments()
            }

            is ClientDocumentsActions.ConfirmDeleteDocument -> {
                deleteDocument(action.documentId)
            }

            is ClientDocumentsActions.DeleteDocument -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ConfirmDocumentDeletion(
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
                    it.copy(isRefreshing = true)
                }
                observeNetworkAndLoadDocuments()
                mutableStateFlow.update {
                    it.copy(isRefreshing = false)
                }
            }

            ClientDocumentsActions.SearchDocument -> {
                mutableStateFlow.update { it ->
                    it.copy(
                        clientDocuments = it.clientDocuments.filter { document ->
                            document.fileName.toString().contains(state.searchText)
                        },
                    )
                }
            }

            ClientDocumentsActions.ToggleSearch -> {
                mutableStateFlow.update {
                    it.copy(
                        isSearchBarActive = !it.isSearchBarActive,
                    )
                }
            }

            is ClientDocumentsActions.UpdateSearchQuery -> {
                mutableStateFlow.update {
                    it.copy(searchText = action.query)
                }
            }

            is ClientDocumentsActions.ViewDocument -> {
                downloadAndSaveDocument(action.documentId)
            }

            is ClientDocumentsActions.AddDocument -> {
                sendEvent(ClientDocumentsEvents.OnAddDocument)
            }
        }
    }


    private fun updateEntityDetails() {
        entityDocumentStateFlow.update {
            it.copy(
                entityId = route.clientId,
                entityType = EntityDocumentState.EntityType.Clients
            )
        }
    }

    private fun observeNetworkAndLoadDocuments() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update {
                    it.copy(isNetworkConnected = isConnected)
                }
                when (isConnected) {
                    true -> {
                        loadClientDocuments()
                    }
                    false -> {
                        mutableStateFlow.update { it.copy(isRefreshing = false) }
                        errorDialogState(getString(Res.string.no_internet_message),)
                    }
                }
            }
        }
    }

    private suspend fun loadClientDocuments() {
        val documentsListFlow = documentsRepository.getDocumentsList(
            entityType = entityType,
            entityId = route.clientId,
        )

        documentsListFlow.collect { dataState ->
            when (dataState) {
                is DataState.Error<*> -> {
                    errorDialogState(dataState.message)
                }

                DataState.Loading -> {
                    loadingDialogState()
                }

                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = null,
                            clientDocuments = dataState.data
                        )
                    }
                }
            }
        }
    }

    private fun deleteDocument(documentId: Int)  {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(dialogState = ClientDocumentsScreenState.DialogState.Loading)
            }

            documentsRepository.removeDocument(
                entityType = entityType,
                entityId = route.clientId,
                documentId = documentId,
            )

            mutableStateFlow.update {
                it.copy(dialogState = null)
            }
        }
    }

    private fun downloadAndSaveDocument(documentId: Int) {
        viewModelScope.launch {
            val isConnected = networkMonitor.isOnline.first()
            mutableStateFlow.update {
                it.copy(isNetworkConnected = isConnected,)
            }
            when(isConnected) {
                true -> {
                    val deferredLoading = async {
                        collectLoadingState()
                    }
                    val deferredResult = async {
                        val result = documentSelectAndUploadRepository
                            .downloadDocumentAndSaveToAppCache()
                        result.onSuccess {
                            entityDocumentStateFlow.update {
                                it.copy(uploadType = EntityDocumentState.UploadType.Update)
                            }
                            sendEvent(ClientDocumentsEvents.OnViewDocument)
                        }.onFailure {throwable ->

                            errorDialogState(throwable.message?:"Unknown error",)
                        }
                    }
                    awaitAll(deferredLoading, deferredResult)
                }
                false -> {
                   errorDialogState(getString(Res.string.no_internet_message),)
                }
            }
        }
    }
    private fun loadingDialogState(){
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientDocumentsScreenState.DialogState.Loading
            )
        }
    }
    private fun errorDialogState(message: String){
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientDocumentsScreenState.DialogState.Error(message)
            )
        }
    }
    private suspend fun collectLoadingState(){
        entityDocumentStateFlow.collect {entityDocumentState ->
            mutableStateFlow.update {
                it.copy(
                    dialogState = if(entityDocumentState.isLoading)
                        ClientDocumentsScreenState.DialogState.Loading
                    else null
                )
            }
        }
    }


}

private fun getFileExtension(headers: Headers?): String? {
    return  headers?.get("Content-Type")?.split('/')[1]
}

data class ClientDocumentsScreenState(
    val clientId: Int = -1,
    val isRefreshing: Boolean = false,
    val clientDocuments: List<Document> = emptyList(),
    val searchText: String = "",
    val isNetworkConnected: Boolean = false,
    val dialogState: DialogState? = null,
    val isSearchBarActive: Boolean = false,
) {
    sealed interface DialogState {
        data object Loading : DialogState
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

    data class ConfirmDeleteDocument(val documentId: Int) : ClientDocumentsActions
    data object Refresh : ClientDocumentsActions
    data object AddDocument : ClientDocumentsActions
    data object ToggleSearch : ClientDocumentsActions
    data object SearchDocument : ClientDocumentsActions
    data class UpdateSearchQuery(val query: String) : ClientDocumentsActions
    data object CloseDialog : ClientDocumentsActions
}


