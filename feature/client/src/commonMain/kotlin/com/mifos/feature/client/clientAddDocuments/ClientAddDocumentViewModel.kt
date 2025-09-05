/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientAddDocuments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.FileKitUtil
import com.mifos.core.data.repository.DocumentDialogRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.utils.createDocumentRequestBody
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.nameWithoutExtension
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientAddDocumentViewModel(
    stateHandler: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val documentDialogRepository: DocumentDialogRepository,
) : BaseViewModel<
    ClientAddDocumentState,
    ClientAddDocumentEvents,
    ClientAddDocumentAction,
    >(
    initialState = ClientAddDocumentState(),
) {

    private val clientId = stateHandler.toRoute<ClientAddDocumentRoute>().clientId
    private val documentId = stateHandler.toRoute<ClientAddDocumentRoute>().documentId
    private val entityType = stateHandler.toRoute<ClientAddDocumentRoute>().entityType

    override fun handleAction(action: ClientAddDocumentAction) {
        when(action){
            ClientAddDocumentAction.AddNewDocument -> TODO()
            ClientAddDocumentAction.NavigateBack -> {
                sendEvent(ClientAddDocumentEvents.OnNavigateBack)
            }
            ClientAddDocumentAction.PickFromFiles -> {
                pickDocumentFromFiles()
            }
            ClientAddDocumentAction.PickFromGallery -> {
                pickDocumentFromGallery()
            }
            ClientAddDocumentAction.SubmitFromDocumentPreviewScreen -> {

            }
            is ClientAddDocumentAction.UpdateDescription -> {
                mutableStateFlow.update {
                    it.copy(description = action.text,)
                }
            }
            is ClientAddDocumentAction.UpdateName -> {
                mutableStateFlow.update {
                    it.copy(description = action.text,)
                }
            }
            ClientAddDocumentAction.UploadDocument -> {

            }
            ClientAddDocumentAction.UploadNewDocument -> {

            }
            ClientAddDocumentAction.UseMoreOptions -> {

            }
        }
    }

    private fun uploadDocument() = flow {
        emit(DataState.Loading)

        val dataState = state.document?.let {
            val multiPartFormDataContent = getMultiPartFormDataContent(it)
            try {
                documentDialogRepository.createDocument(
                    entityType = entityType,
                    entityId = clientId,
                    file = multiPartFormDataContent,
                )

            } catch (e: Exception){
                DataState.Error(e)
            }
        }
        emit(dataState)
    }

    private fun updateDocument()  = flow {
        emit(DataState.Loading)

        val dataState = state.document?.let {
            val multiPartFormDataContent = getMultiPartFormDataContent(it)
            try {
                 documentDialogRepository.updateDocument(
                    entityType = entityType,
                    entityId = clientId,
                    documentId = documentId,
                    file = multiPartFormDataContent,
                )

            } catch (e: Exception){
                DataState.Error(e)
            }
        }
        emit(dataState)
    }

    private suspend fun getMultiPartFormDataContent(file: PlatformFile) = createDocumentRequestBody(
        file,
        file.nameWithoutExtension,
        file.extension
    )

    private fun pickDocumentFromGallery() {
        viewModelScope.launch {
            FileKitUtil.pickImageFile("").collect {imageData->
                when(imageData){
                    is DataState.Error<*> -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientAddDocumentState.DialogState.Error(
                                    imageData.message
                                )
                            )
                        }
                    }
                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = ClientAddDocumentState.DialogState.Loading)
                        }
                    }
                    is DataState.Success<*> -> {
                        imageData.data?.let {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = null,
                                    document = imageData.data
                                )
                            }
                            sendEvent(ClientAddDocumentEvents.ShowDocumentPreviewScreen)
                        } ?: mutableStateFlow.update { it.copy(dialogState = null,) }
                    }
                }
            }
        }
    }

    private fun pickDocumentFromFiles(){
        viewModelScope.launch {
            FileKitUtil.pickPdfFile("").collect {documentFile->
                when(documentFile){
                    is DataState.Error<*> -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientAddDocumentState.DialogState.Error(
                                    documentFile.message
                                )
                            )
                        }
                    }
                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = ClientAddDocumentState.DialogState.Loading)
                        }
                    }
                    is DataState.Success<*> -> {
                        documentFile.data?.let {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = null,
                                    document = documentFile.data
                                )
                            }
                            sendEvent(ClientAddDocumentEvents.ShowDocumentPreviewScreen)
                        } ?: mutableStateFlow.update { it.copy(dialogState = null,) }
                    }
                }
            }
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(isNetworkAvailable = isConnected) }
            }
        }
    }

}

data class ClientAddDocumentState(
    val documentId: Int = -1,
    val isNetworkAvailable: Boolean = false,
    val dialogState: DialogState? = null,
    val documentName: String = "",
    val description: String = "",
    val fileName: String = "",
    val document: PlatformFile? = null,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
    }
}

sealed interface ClientAddDocumentAction {
    data object NavigateBack : ClientAddDocumentAction
    data object AddNewDocument : ClientAddDocumentAction
    data object SubmitFromDocumentPreviewScreen : ClientAddDocumentAction
    data object UploadDocument : ClientAddDocumentAction
    data object UploadNewDocument : ClientAddDocumentAction
    data class UpdateName(val text: String) : ClientAddDocumentAction
    data class UpdateDescription(val text: String) : ClientAddDocumentAction
    data object PickFromGallery : ClientAddDocumentAction
    data object PickFromFiles : ClientAddDocumentAction
    data object UseMoreOptions : ClientAddDocumentAction
}

sealed interface ClientAddDocumentEvents {
    data object OnNavigateBack : ClientAddDocumentEvents
    data object SubmitDocument: ClientAddDocumentEvents
    data object ShowDocumentPreviewScreen : ClientAddDocumentEvents
}

enum class DocumentPreviewScreenAction {
    SUBMIT,
    UPDATE
}
