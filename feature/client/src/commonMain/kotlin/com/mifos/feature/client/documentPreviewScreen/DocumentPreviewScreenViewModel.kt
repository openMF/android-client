/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.documentPreviewScreen

import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.DocumentSelectAndUploadRepository
import com.mifos.feature.client.EntityDocumentState
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocumentPreviewScreenViewModel(
    private val documentSelectAndUploadRepository: DocumentSelectAndUploadRepository,
) : BaseViewModel<
    DocumentPreviewState,
    DocumentPreviewEvent,
    DocumentPreviewScreenAction,
    >(DocumentPreviewState()) {

    private val documentSelectAndUploadFlow =
        documentSelectAndUploadRepository.entityDocumentStateMutableStateFlow

    init {
        updateDocumentPreviewState()
    }

    override fun handleAction(action: DocumentPreviewScreenAction) {
        when (action) {
            DocumentPreviewScreenAction.CancelUpdating -> {
                documentSelectAndUploadRepository.updateStep(EntityDocumentState.Step.VIEW)
                sendEvent(DocumentPreviewEvent.OnNavigateBack)
            }
            DocumentPreviewScreenAction.DismissBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = false)
                }
            }

            DocumentPreviewScreenAction.NavigateBack -> {
                sendEvent(DocumentPreviewEvent.OnNavigateBack)
            }
            DocumentPreviewScreenAction.PickFromFile -> {
                pickFromFiles()
            }
            DocumentPreviewScreenAction.PickFromGallery -> {
                pickFromGallery()
            }
            DocumentPreviewScreenAction.RejectDocument -> {
                documentSelectAndUploadRepository.resetState()
                sendEvent(DocumentPreviewEvent.OnNavigateBack)
            }
            DocumentPreviewScreenAction.SubmitClicked -> {
                documentSelectAndUploadRepository.updateStep(
                    EntityDocumentState.Step.VIEW,
                )
                sendEvent(DocumentPreviewEvent.OnNavigateBack)
            }
            DocumentPreviewScreenAction.UpdateNew -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = true)
                }
            }
            DocumentPreviewScreenAction.UseMoreOptions -> {}
        }
    }

    private fun pickFromGallery() {
        viewModelScope.launch {
            documentSelectAndUploadRepository.selectImageFromGallery()
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error<*> -> {
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
                                    it.copy(
                                        showBottomSheet = false,
                                        documentBytes = platformFile.readBytes(),
                                    )
                                }
                                if (documentSelectAndUploadFlow.first().step == EntityDocumentState.Step.PREVIEW) {
                                    documentSelectAndUploadRepository.updateStep(EntityDocumentState.Step.UPDATE_PREVIEW)
                                } else {
                                    documentSelectAndUploadRepository.updateStep(EntityDocumentState.Step.PREVIEW)
                                }
                                documentSelectAndUploadRepository.updateEntityDocument(
                                    platformFile,
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun pickFromFiles() {
        viewModelScope.launch {
            documentSelectAndUploadRepository.selectImageFromFile()
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error<*> -> {
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
                                    it.copy(
                                        showBottomSheet = false,
                                        documentBytes = platformFile.readBytes(),
                                    )
                                }
                                if (documentSelectAndUploadFlow.first().step == EntityDocumentState.Step.PREVIEW) {
                                    documentSelectAndUploadRepository.updateStep(EntityDocumentState.Step.UPDATE_PREVIEW)
                                } else {
                                    documentSelectAndUploadRepository.updateStep(EntityDocumentState.Step.PREVIEW)
                                }
                                documentSelectAndUploadRepository.updateEntityDocument(
                                    platformFile,
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun updateDocumentPreviewState() {
        viewModelScope.launch {
            documentSelectAndUploadFlow.collect { state ->
                mutableStateFlow.update {
                    it.copy(
                        step = state.step,
                        documentBytes = state.entityDocument?.readBytes(),
                        documentType = getDocumentType(state.entityDocument?.extension ?: ""),
                    )
                }
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
                dialogState = DocumentPreviewState.DialogState.Error(message),
            )
        }
    }

    private fun loadingDialogState() {
        mutableStateFlow.update {
            it.copy(dialogState = DocumentPreviewState.DialogState.Loading)
        }
    }
}

data class DocumentPreviewState(
    val showBottomSheet: Boolean = false,
    val documentType: DocumentType? = null,
    val dialogState: DialogState? = null,
    val step: EntityDocumentState.Step = EntityDocumentState.Step.PREVIEW,
    val documentBytes: ByteArray? = null,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
    }
}

sealed interface DocumentPreviewScreenAction {
    object NavigateBack : DocumentPreviewScreenAction
    object CancelUpdating : DocumentPreviewScreenAction
    object RejectDocument : DocumentPreviewScreenAction
    object SubmitClicked : DocumentPreviewScreenAction
    object UpdateNew : DocumentPreviewScreenAction
    object DismissBottomSheet : DocumentPreviewScreenAction
    object PickFromGallery : DocumentPreviewScreenAction
    object PickFromFile : DocumentPreviewScreenAction
    object UseMoreOptions : DocumentPreviewScreenAction
}

sealed interface DocumentType {
    data class Image(val extension: String) : DocumentType
    data object Pdf : DocumentType
}

private fun getDocumentType(extension: String): DocumentType? {
    return if (extension == "pdf") {
        DocumentType.Pdf
    } else if (
        extension == "png" ||
        extension == "jpeg" ||
        extension == "jpg"
    ) {
        DocumentType.Image(extension)
    } else {
        null
    }
}

sealed interface DocumentPreviewEvent {
    object OnNavigateBack : DocumentPreviewEvent
}
