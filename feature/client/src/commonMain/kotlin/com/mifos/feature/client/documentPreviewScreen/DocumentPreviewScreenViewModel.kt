package com.mifos.feature.client.documentPreviewScreen

import androidx.lifecycle.viewModelScope
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.DocumentSelectAndUploadRepository
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class DocumentPreviewScreenViewModel(
    private val documentSelectAndUploadRepository: DocumentSelectAndUploadRepository
) : BaseViewModel<
        DocumentPreviewState,
        DocumentPreviewEvent,
        DocumentPreviewScreenAction,
>(DocumentPreviewState()) {

    private val documentSelectAndUploadFlow =
        documentSelectAndUploadRepository.entityDocumentStateMutableStateFlow

    override fun handleAction(action: DocumentPreviewScreenAction) {
        when (action) {
            DocumentPreviewScreenAction.CancelUpdating -> {
                sendEvent(DocumentPreviewEvent.OnNavigateBack)
            }
            DocumentPreviewScreenAction.DismissBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = false)
                }
            }
            DocumentPreviewScreenAction.EnableUpdating -> {
                documentSelectAndUploadFlow.update {
                    it.copy(documentPreviewedAndAccepted = true)
                }
                mutableStateFlow.update {
                    it.copy(showUpdateButton = true)
                }
            }
            DocumentPreviewScreenAction.NavigateBack -> {
                sendEvent(DocumentPreviewEvent.OnNavigateBack)
            }
            DocumentPreviewScreenAction.PickFromFile -> {
                pickFromGallery()
            }
            DocumentPreviewScreenAction.PickFromGallery -> {
                pickFromFiles()
            }
            DocumentPreviewScreenAction.RejectDocument -> {
                documentSelectAndUploadFlow.update {
                    it.copy(
                        documentPreviewedAndAccepted = false,
                        entityDocument = null,
                        documentId = -1
                    )
                }
                sendEvent(DocumentPreviewEvent.OnNavigateBack)
            }
            DocumentPreviewScreenAction.SubmitClicked -> {
                documentSelectAndUploadFlow.update {
                    it.copy(documentPreviewedAndAccepted = true)
                }
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

    private fun pickFromGallery(){
        viewModelScope.launch {
            val result = documentSelectAndUploadRepository.selectImageFromGallery()
            result.onSuccess {
                documentSelectAndUploadFlow.collect { entityState->

                    entityState.entityDocument?.readBytes()?.let { bytes->
                        mutableStateFlow.update {
                            it.copy(
                                documentType = getDocumentType(entityState.entityDocument.extension),
                                showUpdateButton = false,
                                showBottomSheet = false,
                                documentBytes = bytes
                            )
                        }
                    } ?: mutableStateFlow.update {
                        it.copy(
                            showUpdateButton = true,
                            exception = Exception("Failed to read image"),
                            showBottomSheet = false,
                        )
                    }
                    updateDocumentPreviewState()
                    sendAction(DocumentPreviewScreenAction.EnableUpdating)
                }

            }.onFailure { throwable ->
                mutableStateFlow.update {
                    it.copy(
                        exception = Exception(throwable),
                        showBottomSheet = false,
                    )
                }
            }
        }
    }

    private fun pickFromFiles() {
        viewModelScope.launch {
            val result = documentSelectAndUploadRepository.selectImageFromFile()
            result.onSuccess {
                val entityDocState = documentSelectAndUploadFlow.first()

                entityDocState.entityDocument?.readBytes()?.let {bytes->
                    mutableStateFlow.update {
                        it.copy(
                            documentType = getDocumentType(entityDocState.entityDocument.extension),
                            showUpdateButton = false,
                            showBottomSheet = false,
                            documentBytes = bytes
                        )
                    }
                } ?: mutableStateFlow.update {
                    it.copy(
                        showUpdateButton = true,
                        exception = Exception("Failed to read document"),
                        showBottomSheet = false,
                    )
                }

                updateDocumentPreviewState()

            }.onFailure { throwable ->
                mutableStateFlow.update {
                    it.copy(
                        exception = Exception(throwable),
                        showBottomSheet = false,
                    )
                }
            }
        }
    }


    private fun updateDocumentPreviewState() {
        viewModelScope.launch {
            documentSelectAndUploadFlow.collect {state ->
                mutableStateFlow.update {
                    it.copy(
                        showUpdateButton = state.documentPreviewedAndAccepted,
                        documentBytes = state.entityDocument?.readBytes(),
                        documentType = getDocumentType(state.entityDocument?.extension ?: ""),
                    )
                }
            }
        }
    }

}

data class DocumentPreviewState(
    val exception: Exception? = null,
    val showBottomSheet: Boolean = false,
    val showUpdateButton: Boolean = false,
    val documentType: DocumentType? = null,
    val documentBytes: ByteArray? = null,
)


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
    object EnableUpdating : DocumentPreviewScreenAction
}

sealed interface DocumentType {
    data class Image(val extension: String) : DocumentType
    data object Pdf : DocumentType
}

private fun getDocumentType(extension: String): DocumentType? {
    return if (extension == "pdf" ) DocumentType.Pdf
    else if(
        extension=="png" ||
        extension=="jpeg" ||
        extension=="jpg"
    ) DocumentType.Image(extension)
    else null
}

sealed interface DocumentPreviewEvent {
    object OnNavigateBack : DocumentPreviewEvent
}