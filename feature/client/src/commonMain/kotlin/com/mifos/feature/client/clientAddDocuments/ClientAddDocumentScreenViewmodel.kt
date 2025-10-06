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
import androidclient.feature.client.generated.resources.error_document_size_exceeded
import androidclient.feature.client.generated.resources.no_internet_message
import androidclient.feature.client.generated.resources.unknown_error
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.DocumentSelectAndUploadRepository
import com.mifos.feature.client.EntityDocumentState
import com.mifos.feature.client.utils.openPdfWithDefaultExternalApp
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class ClientAddDocumentScreenViewmodel(
    private val networkMonitor: NetworkMonitor,
    private val documentSelectAndUploadRepository: DocumentSelectAndUploadRepository,
) : BaseViewModel<
    ClientAddDocumentScreenState,
    ClientAddDocumentScreenEvents,
    ClientAddDocumentScreenAction,
    >(
    initialState = ClientAddDocumentScreenState(),
) {
    private val entityDocumentStateFlow =
        documentSelectAndUploadRepository.entityDocumentStateMutableStateFlow

    init {
        updateAddDocumentStateReactively()
    }

    override fun handleAction(action: ClientAddDocumentScreenAction) {
        when (action) {
            ClientAddDocumentScreenAction.AddNewDocument -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = true)
                }
            }

            ClientAddDocumentScreenAction.DismissBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = false)
                }
            }

            ClientAddDocumentScreenAction.NavigateBack -> {
                sendEvent(ClientAddDocumentScreenEvents.OnNavigateBack)
            }

            ClientAddDocumentScreenAction.PickFromFiles -> {
                pickFromFiles()
            }

            ClientAddDocumentScreenAction.PickFromGallery -> {
                pickFromGallery()
            }

            ClientAddDocumentScreenAction.ViewDocument -> {
                if (state.platformFile?.extension == "pdf") {
                    state.platformFile?.let {
                        previewPdfInExternalApp(it)
                    }
                }
                documentSelectAndUploadRepository.updateStep(EntityDocumentState.Step.UPDATE_PREVIEW)
                sendEvent(ClientAddDocumentScreenEvents.OnNavigateToPreviewScreen)
            }

            is ClientAddDocumentScreenAction.UpdateDescription -> {
                mutableStateFlow.update {
                    it.copy(enteredDocumentDescription = action.text)
                }
            }

            ClientAddDocumentScreenAction.UpdateDocument -> {
                updateDocument()
            }

            is ClientAddDocumentScreenAction.UpdateFileName -> {
                mutableStateFlow.update {
                    it.copy(enteredFileName = action.text)
                }
            }

            ClientAddDocumentScreenAction.UploadDocument -> {
                uploadDocument()
            }

            ClientAddDocumentScreenAction.UseMoreOptions -> {}
        }
    }

    private fun pickFromGallery() {
        viewModelScope.launch {
            documentSelectAndUploadRepository.selectImageFromGallery()
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(showBottomSheet = false)
                            }
                            errorDialogState(dataState.message)
                        }
                        DataState.Loading -> {
                            loadingDialogState()
                        }
                        is DataState.Success -> {
                            nullDialogState()
                            dataState.data?.let { platformFile ->
                                mutableStateFlow.update {
                                    it.copy(showBottomSheet = false)
                                }
                                if (platformFile.size() > 1048576L) {
                                    mutableStateFlow.update {
                                        it.copy(
                                            dialogState = ClientAddDocumentScreenState
                                                .DialogState.Error(
                                                    getString(Res.string.error_document_size_exceeded),
                                                ),
                                        )
                                    }
                                } else {
                                    mutableStateFlow.update {
                                        it.copy(dialogState = null)
                                    }
                                    documentSelectAndUploadRepository.updateStep(EntityDocumentState.Step.PREVIEW)
                                    documentSelectAndUploadRepository.updateEntityDocument(
                                        platformFile,
                                    )
                                    sendEvent(ClientAddDocumentScreenEvents.OnNavigateToPreviewScreen)
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun pickFromFiles() {
        viewModelScope.launch {
            documentSelectAndUploadRepository.selectDocumentFromFile()
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(showBottomSheet = false)
                            }
                            errorDialogState(dataState.message)
                        }
                        DataState.Loading -> {
                            loadingDialogState()
                        }
                        is DataState.Success -> {
                            nullDialogState()
                            dataState.data?.let { platformFile ->
                                mutableStateFlow.update {
                                    it.copy(showBottomSheet = false)
                                }
                                if (platformFile.size() > 1048576L) {
                                    mutableStateFlow.update {
                                        it.copy(
                                            dialogState = ClientAddDocumentScreenState
                                                .DialogState.Error(
                                                    getString(Res.string.error_document_size_exceeded),
                                                ),
                                        )
                                    }
                                } else {
                                    mutableStateFlow.update {
                                        it.copy(dialogState = null)
                                    }
                                    documentSelectAndUploadRepository.updateEntityDocument(
                                        platformFile,
                                    )
                                    if (platformFile.extension == "pdf") {
                                        documentSelectAndUploadRepository.updateStep(EntityDocumentState.Step.VIEW)
                                    } else {
                                        documentSelectAndUploadRepository.updateStep(EntityDocumentState.Step.PREVIEW)
                                        sendEvent(ClientAddDocumentScreenEvents.OnNavigateToPreviewScreen)
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun uploadDocument() {
        viewModelScope.launch {
            val isConnected = observerNetwork()
            when (isConnected) {
                true -> {
                    documentSelectAndUploadRepository.uploadDocument(
                        state.enteredFileName,
                        state.enteredDocumentDescription,
                    ).collect { dataState ->
                        when (dataState) {
                            is DataState.Error -> {
                                errorDialogState(dataState.message)
                            }
                            DataState.Loading -> {
                                loadingDialogState()
                            }
                            is DataState.Success -> {
                                nullDialogState()
                                documentSelectAndUploadRepository.resetStateAndRefresh()
                                sendEvent(ClientAddDocumentScreenEvents.OnNavigateBack)
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

    private fun updateDocument() {
        viewModelScope.launch {
            val isConnected = observerNetwork()
            when (isConnected) {
                true -> {
                    documentSelectAndUploadRepository.updateDocument(
                        state.enteredFileName,
                        state.enteredDocumentDescription,
                    ).collect { dataState ->
                        when (dataState) {
                            is DataState.Error -> {
                                errorDialogState(dataState.message)
                            }
                            DataState.Loading -> {
                                loadingDialogState()
                            }
                            is DataState.Success -> {
                                nullDialogState()
                                documentSelectAndUploadRepository.resetStateAndRefresh()
                                sendEvent(ClientAddDocumentScreenEvents.OnNavigateBack)
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

    private suspend fun observerNetwork() = networkMonitor.isOnline.first()

    private fun updateAddDocumentStateReactively() {
        viewModelScope.launch {
            entityDocumentStateFlow.collect { state ->
                mutableStateFlow.update {
                    it.copy(
                        platformFile = state.entityDocument,
                        pickedDocumentName = state.entityDocument?.name ?: "",
                        step = state.step,
                        submitMode = state.submitMode,
                    )
                }
            }
        }
    }

    private fun previewPdfInExternalApp(platformFile: PlatformFile) {
        viewModelScope.launch {
            loadingDialogState()
            try {
                openPdfWithDefaultExternalApp(platformFile)
                nullDialogState()
            } catch (e: Exception) {
                errorDialogState(e.message ?: getString(Res.string.unknown_error))
            }
        }
    }

    private fun nullDialogState() {
        mutableStateFlow.update {
            it.copy(dialogState = null)
        }
    }

    private fun errorDialogState(message: String) {
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientAddDocumentScreenState.DialogState.Error(message),
            )
        }
    }

    private fun loadingDialogState() {
        mutableStateFlow.update {
            it.copy(dialogState = ClientAddDocumentScreenState.DialogState.Loading)
        }
    }
}

data class ClientAddDocumentScreenState(
    val platformFile: PlatformFile? = null,
    val documentAccepted: Boolean = false,
    val pickedDocumentName: String = "",
    val isNetworkAvailable: Boolean = false,
    val enteredDocumentDescription: String = "",
    val enteredFileName: String = "",
    val dialogState: DialogState? = null,
    val showBottomSheet: Boolean = false,
    val step: EntityDocumentState.Step = EntityDocumentState.Step.ADD,
    val submitMode: EntityDocumentState.SubmitMode = EntityDocumentState.SubmitMode.UPLOAD,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
    }
}

sealed interface ClientAddDocumentScreenAction {

    data object NavigateBack : ClientAddDocumentScreenAction
    data object AddNewDocument : ClientAddDocumentScreenAction
    data object DismissBottomSheet : ClientAddDocumentScreenAction
    data object UploadDocument : ClientAddDocumentScreenAction
    data object UpdateDocument : ClientAddDocumentScreenAction
    data object PickFromGallery : ClientAddDocumentScreenAction
    data object PickFromFiles : ClientAddDocumentScreenAction
    data object UseMoreOptions : ClientAddDocumentScreenAction
    data object ViewDocument : ClientAddDocumentScreenAction
    data class UpdateFileName(val text: String) : ClientAddDocumentScreenAction
    data class UpdateDescription(val text: String) : ClientAddDocumentScreenAction
}

sealed interface ClientAddDocumentScreenEvents {
    object OnNavigateBack : ClientAddDocumentScreenEvents
    object OnNavigateToPreviewScreen : ClientAddDocumentScreenEvents
}
