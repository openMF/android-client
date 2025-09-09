package com.mifos.feature.client.documentPreviewScreen

import androidx.lifecycle.viewModelScope
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.DocumentSelectAndUploadRepository
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.readBytes
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

    val documentSelectAndUploadState = documentSelectAndUploadRepository.entityDocumentState

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
                    it.copy(documentPreviewedAndAccepted = false)
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
            collectLoadingState()
            val result = documentSelectAndUploadRepository.selectImageFromGallery()
            result.onSuccess {
                 documentSelectAndUploadState.entityDocument?.readBytes()?.let {bytes->
                        mutableStateFlow.update {
                            it.copy(
                                documentType = getDocumentType(documentSelectAndUploadState.entityDocument?.extension ?: ""),
                                showUpdateButton = false,
                                showBottomSheet = false,
                                documentBytes = bytes
                            )
                        }
                } ?: mutableStateFlow.update {
                        it.copy(
                            showUpdateButton = true,
                            isException = Exception("Failed to read image"),
                            showBottomSheet = false,
                        )
                    }
                updateStateReactively()
                sendAction(DocumentPreviewScreenAction.EnableUpdating)
            }.onFailure { throwable ->
                mutableStateFlow.update {
                    it.copy(
                        isException = Exception(throwable),
                        showBottomSheet = false,
                    )
                }
            }
        }
    }

    private fun pickFromFiles() {
        viewModelScope.launch {
            collectLoadingState()
            val result = documentSelectAndUploadRepository.selectImageFromFile()
            result.onSuccess {
                documentSelectAndUploadState.entityDocument?.readBytes()?.let {bytes->
                    mutableStateFlow.update {
                        it.copy(
                            documentType = getDocumentType(documentSelectAndUploadState.entityDocument.extension),
                            showUpdateButton = false,
                            showBottomSheet = false,
                            documentBytes = bytes
                        )
                    }
                } ?: mutableStateFlow.update {
                    it.copy(
                        showUpdateButton = true,
                        isException = Exception("Failed to read document"),
                        showBottomSheet = false,
                    )
                }

                updateStateReactively()

            }.onFailure { throwable ->
                mutableStateFlow.update {
                    it.copy(
                        isException = Exception(throwable),
                        showBottomSheet = false,
                    )
                }
            }
        }
    }

    private suspend fun collectLoadingState(){
        documentSelectAndUploadFlow.collect {entityDocumentState ->
            mutableStateFlow.update {
                it.copy(
                    isLoading = entityDocumentState.isLoading,
                )
            }
        }
    }
    private fun updateStateReactively() {
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
    val isLoading: Boolean = false,
    val isException: Exception? = null,
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