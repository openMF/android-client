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

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.no_internet_message
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.FileKitUtil
import com.mifos.core.data.repository.DocumentDialogRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.clientAddDocuments.ClientAddDocumentState.DialogState.Error
import com.mifos.feature.client.utils.createDocumentRequestBody
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.nameWithoutExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

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
        when (action) {
            ClientAddDocumentAction.AddNewDocument -> {
                mutableStateFlow.update {
                    it.copy(showFilePickerBottomSheet = true)
                }
            }
            ClientAddDocumentAction.DismissBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(showFilePickerBottomSheet = false)
                }
            }
            ClientAddDocumentAction.NavigateBack -> {
                sendEvent(ClientAddDocumentEvents.OnNavigateBack)
            }
            ClientAddDocumentAction.PickFromFiles -> {
                pickDocumentFromFiles()
            }
            ClientAddDocumentAction.PickFromGallery -> {
                pickDocumentFromGallery()
            }
            is ClientAddDocumentAction.SubmitFromDocumentPreviewScreen -> {
                mutableStateFlow.update {
                    it.copy(
                        pickedDocumentName = action.fileName,
                        isDocumentAdded = true,
                        showDocumentPreviewScreen = false,
                    )
                }
            }
            is ClientAddDocumentAction.UpdateDescription -> {
                mutableStateFlow.update {
                    it.copy(enteredDocumentDescription = action.text)
                }
            }
            is ClientAddDocumentAction.UpdateName -> {
                mutableStateFlow.update {
                    it.copy(enteredFileName = action.text)
                }
            }
            ClientAddDocumentAction.UploadDocument -> {
                observeNetworkAndUpload()
            }
            ClientAddDocumentAction.UploadNewDocument -> {
                observeNetworkAndUpdate()
            }
            ClientAddDocumentAction.UseMoreOptions -> {}
            ClientAddDocumentAction.CloseDocumentPreviewScreen ->{
                mutableStateFlow.update {
                    it.copy(showDocumentPreviewScreen = false)
                }
            }
            ClientAddDocumentAction.PreviewUploadedDocument -> {
                mutableStateFlow.update {
                    it.copy(
                        showDocumentPreviewScreen = true,
                        isDocumentUpdatingEnabled = true
                    )
                }
            }

            ClientAddDocumentAction.RetryUpdate -> {
                observeNetworkAndUpdate()
            }
            ClientAddDocumentAction.RetryUpload -> {
                observeNetworkAndUpload()
            }
        }
    }

    private fun observeNetworkAndUpload() {
        viewModelScope.launch {
            val isConnected = networkMonitor.isOnline.first()
            mutableStateFlow.update { it.copy(isNetworkAvailable = isConnected) }
            when(isConnected){
                true -> {
                    uploadDocument().collect { dataState ->
                        when(dataState){
                            is DataState.Error<*> -> {
                                mutableStateFlow.update {
                                    it.copy(
                                        dialogState = Error(dataState.message)
                                    )
                                }
                            }
                            DataState.Loading -> {
                                mutableStateFlow.update {
                                    it.copy(
                                        showProgressBar = true,
                                    )
                                }
                            }
                            is DataState.Success<*> ->{
                                mutableStateFlow.update {
                                    it.copy(
                                        showDocumentPreviewScreen = false,
                                        isDocumentAdded = false,
                                        isDocumentUpdatingEnabled = false,
                                    )
                                }
                                sendEvent(ClientAddDocumentEvents.OnNavigateBack)
                            }
                        }
                    }
                }
                false -> {
                    ClientAddDocumentState.DialogState.UploadError(
                        getString(Res.string.no_internet_message)
                    )
                }
            }

        }
    }

    private fun observeNetworkAndUpdate() {
        viewModelScope.launch {
            val isConnected = networkMonitor.isOnline.first()
            mutableStateFlow.update { it.copy(isNetworkAvailable = isConnected) }
            when(isConnected){
                true -> {
                    updateDocument().collect { dataState ->
                        when(dataState){
                            is DataState.Error<*> -> {
                                mutableStateFlow.update {
                                    it.copy(
                                        dialogState = Error(dataState.message)
                                    )
                                }
                            }
                            DataState.Loading -> {
                                mutableStateFlow.update {
                                    it.copy(showProgressBar = true)
                                }
                            }
                            is DataState.Success<*> ->{
                                sendEvent(ClientAddDocumentEvents.OnNavigateBack)
                            }
                        }
                    }
                }
                false -> {
                    ClientAddDocumentState.DialogState.UpdateError(
                        getString(Res.string.no_internet_message)
                    )
                }
            }
        }
    }

    private fun uploadDocument() = flow {
        emit(DataState.Loading)
        val result = try {
            val document = PlatformFile(state.document)

            val multiPartFormDataContent = getMultiPartFormDataContent(document)

            documentDialogRepository.createDocument(
                entityType = entityType,
                entityId = clientId,
                file = multiPartFormDataContent,
            )
        } catch (e: Exception) {
            DataState.Error(e)
        }
        emit(result)
    }

    private fun updateDocument() = flow {
        emit(DataState.Loading)

        val result = try {
            val document = PlatformFile(state.document)
            val multiPartFormDataContent = getMultiPartFormDataContent(document)
            val result = documentDialogRepository.updateDocument(
                entityType = entityType,
                entityId = clientId,
                documentId = documentId,
                file = multiPartFormDataContent,
            )
            result
        } catch (e: Exception) {
            DataState.Error(e)
        }
        emit(result)
    }
    private suspend fun getMultiPartFormDataContent(file: PlatformFile) = createDocumentRequestBody(
        file,
        file.nameWithoutExtension,
        file.extension,
    )

    private fun pickDocumentFromGallery() {
        viewModelScope.launch(Dispatchers.IO) {
            FileKitUtil.pickImageAndSaveToCache("").collect { imageData ->
                when (imageData) {
                    is DataState.Error<*> -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = Error(
                                    imageData.message,
                                ),
                            )
                        }
                    }
                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = ClientAddDocumentState.DialogState.Loading)
                        }
                    }
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = null,
                                document = imageData.data,
                                showDocumentPreviewScreen = true,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun pickDocumentFromFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            FileKitUtil.pickPdfFile("").collect { documentFile ->
                when (documentFile) {
                    is DataState.Error<*> -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = Error(
                                    documentFile.message,
                                ),
                            )
                        }
                    }
                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = ClientAddDocumentState.DialogState.Loading)
                        }
                    }
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                showDocumentPreviewScreen = true,
                                dialogState = null,
                                document = documentFile.data,
                            )
                        }
                    }
                }
            }
        }
    }
}

data class ClientAddDocumentState(
    val isNetworkAvailable: Boolean = false,
    val dialogState: DialogState? = null,
    val showFilePickerBottomSheet: Boolean = false,
    val enteredDocumentDescription: String = "",
    val enteredFileName: String = "",
    val document: String ="",
    val pickedDocumentName: String = "",
    val isDocumentAdded: Boolean = false,
    val isDocumentUpdatingEnabled: Boolean  = false,
    val showDocumentPreviewScreen : Boolean = false,
    val showProgressBar: Boolean = false,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
        data class UploadError(val message: String) : DialogState
        data class UpdateError(val message: String) : DialogState
    }
}

sealed interface ClientAddDocumentAction {
    data object NavigateBack : ClientAddDocumentAction
    data object AddNewDocument : ClientAddDocumentAction
    data object DismissBottomSheet: ClientAddDocumentAction
    data class SubmitFromDocumentPreviewScreen(val fileName: String) : ClientAddDocumentAction
    data object UploadDocument : ClientAddDocumentAction
    data object UploadNewDocument : ClientAddDocumentAction
    data class UpdateName(val text: String) : ClientAddDocumentAction
    data class UpdateDescription(val text: String) : ClientAddDocumentAction
    data object PickFromGallery : ClientAddDocumentAction
    data object PickFromFiles : ClientAddDocumentAction
    data object UseMoreOptions : ClientAddDocumentAction

    data object PreviewUploadedDocument: ClientAddDocumentAction

    data object CloseDocumentPreviewScreen: ClientAddDocumentAction

    data object RetryUpdate: ClientAddDocumentAction
    data object RetryUpload: ClientAddDocumentAction
}

sealed interface ClientAddDocumentEvents {
    data object OnNavigateBack : ClientAddDocumentEvents


}

enum class DocumentPreviewScreenAction {
    SUBMIT,
    UPDATE,
}
