package com.mifos.feature.client.documentPreviewScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.FileKitUtil
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.clientAddDocuments.DocumentState
import io.github.vinceglb.filekit.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class DocumentPreviewScreenViewModel(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<
        DocumentPreviewState,
        DocumentPreviewEvent,
        DocumentPreviewScreenAction,
        >(DocumentPreviewState()) {

    private val route = savedStateHandle.toRoute<DocumentPreviewScreenRoute>()
    private val documentState = route.documentState
    private val documentPath = documentState.documentPath
    private val comingFromServer = route.comingFromServer

    init {
        viewModelScope.launch {
            Logger.e { "File path: $documentPath" }
            try {
                val platformFile = PlatformFile(documentPath)
                Logger.e { "File inside try catch: $platformFile" }
                Logger.e { "File path inside try catch: ${platformFile.path}" }
                mutableStateFlow.update {
                    it.copy(documentPath = platformFile.path)
                }
                val canUpdateDocument =route.canUpdateDocument
                if (canUpdateDocument) {
                    sendAction(DocumentPreviewScreenAction.EnableUpdating)
                }
                getDocumentType(extension = platformFile.extension)?.let {
                    sendAction(
                        DocumentPreviewScreenAction.LoadDocument(it),
                    )
                } ?: sendEvent(DocumentPreviewEvent.OnNavigateBack(documentState))
            } catch (e: Exception) {
                Logger.e(e) { "Failed to load file"}
                sendEvent(DocumentPreviewEvent.OnNavigateBack(documentState))
            }
        }
    }

    override fun handleAction(action: DocumentPreviewScreenAction) {
        when (action) {
            DocumentPreviewScreenAction.NavigateBack -> {
                sendEvent(DocumentPreviewEvent.OnNavigateBack(documentState))
            }

            DocumentPreviewScreenAction.CancelUpdating -> {
                sendEvent(DocumentPreviewEvent.OnCancelUpdating(documentState))
            }

            DocumentPreviewScreenAction.DismissBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = false)
                }
            }

            DocumentPreviewScreenAction.EnableUpdating -> {
                mutableStateFlow.update {
                    it.copy(showUpdateButton = true)
                }
            }

            is DocumentPreviewScreenAction.LoadDocument -> {
                if (state.documentPath.isNotBlank()) {
                    mutableStateFlow.update {
                        it.copy(documentType = action.documentType)
                    }
                    loadDocumentFromPath(state.documentPath)
                } else {
                    sendEvent(DocumentPreviewEvent.OnDocumentRejected(documentState))
                }
            }

            DocumentPreviewScreenAction.PickFromFile -> {
                selectImageFromFiles()
            }

            DocumentPreviewScreenAction.PickFromGallery -> {
                selectImageFromGallery()
            }

            DocumentPreviewScreenAction.RejectDocument -> {
                sendEvent(DocumentPreviewEvent.OnDocumentRejected(documentState))
            }

            DocumentPreviewScreenAction.SubmitClicked -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = true)
                }
            }

            DocumentPreviewScreenAction.UpdateNew -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = true)
                }
            }

            DocumentPreviewScreenAction.UseMoreOptions -> {}
        }
    }

    private fun selectImageFromGallery() {
        viewModelScope.launch {
            FileKitUtil.pickImage().collect { dataState ->
                when (dataState) {
                    is DataState.Error<*> -> {
                        errorDialogState(dataState.message)
                    }

                    DataState.Loading -> {
                        loadingDialogState()
                    }

                    is DataState.Success -> {
                        sendAction(DocumentPreviewScreenAction.DismissBottomSheet)
                        dataState.data?.let { platformFile ->
                            mutableStateFlow.update {
                                it.copy(
                                    showUpdateButton = false,
                                    dialogState = null,
                                    documentPath = platformFile.path,
                                )
                            }

                            sendEvent(
                                DocumentPreviewEvent.OnSubmitClinked(
                                    documentState = documentState,
                                    newDocumentPath = platformFile.path,
                                    updateForServer = comingFromServer,
                                )
                            )
                        } ?: nullDialogState()
                    }
                }
            }
        }
    }

    private fun selectImageFromFiles() {
        viewModelScope.launch {
            FileKitUtil.pickPdfFile().collect { dataState ->
                when (dataState) {
                    is DataState.Error<*> -> {
                        errorDialogState(dataState.message)
                    }

                    DataState.Loading -> {
                        loadingDialogState()
                    }

                    is DataState.Success -> {
                        sendAction(DocumentPreviewScreenAction.DismissBottomSheet)
                        dataState.data?.let { platformFile ->
                            mutableStateFlow.update {
                                it.copy(
                                    showUpdateButton = false,
                                    dialogState = null,
                                    documentPath = platformFile.path,
                                )
                            }
                            sendEvent(
                                DocumentPreviewEvent.OnSubmitClinked(
                                    documentState = documentState,
                                    newDocumentPath = platformFile.path,
                                    updateForServer = comingFromServer
                                )
                            )
                        } ?: nullDialogState()
                    }
                }
            }
        }
    }

    private fun loadDocumentFromPath(documentPath: String) {
        viewModelScope.launch {
            try {
                val platformFile = PlatformFile(documentPath)
                Logger.e { "Selected document: $platformFile" }
                Logger.e { "Does file exist: ${platformFile.exists()}" }

                val bytesString = platformFile.readBytes()
                mutableStateFlow.update {
                    it.copy(
                        documentPath = documentPath,
                        documentContent = bytesString,
                    )
                }

            } catch (e: Exception) {
                Logger.e { "Exception: ${e.message}" }
                errorDialogState("Failed to load file.")
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
            it.copy(dialogState = DocumentPreviewState.DialogState.Error(message))
        }
    }

    private fun loadingDialogState() {
        mutableStateFlow.update {
            it.copy(dialogState = DocumentPreviewState.DialogState.Loading)
        }
    }


}

data class DocumentPreviewState(
    val dialogState: DialogState? = null,
    val showBottomSheet: Boolean = false,
    val showUpdateButton: Boolean = false,
    val documentPath: String = "",
    val documentType: DocumentType? = DocumentType.Image("jpg"),
    val documentContent: ByteArray? = null,
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
    object EnableUpdating : DocumentPreviewScreenAction
    data class LoadDocument(val documentType: DocumentType) : DocumentPreviewScreenAction
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
    data class OnCancelUpdating(val documentState: DocumentState) : DocumentPreviewEvent
    data class OnNavigateBack(val documentState: DocumentState) : DocumentPreviewEvent
    data class OnDocumentRejected(val documentState: DocumentState) : DocumentPreviewEvent
    data class OnSubmitClinked(
        val documentState: DocumentState,
        val newDocumentPath: String,
        val updateForServer: Boolean
    ): DocumentPreviewEvent
}