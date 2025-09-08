package com.mifos.feature.client.documentPreviewScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.FileKitUtil
import com.mifos.core.ui.util.BaseViewModel
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
    private val documentName = route.documentPath
    private val comingFromServer = route.comingFromServer

    init {
        viewModelScope.launch {
            Logger.e { "File path: $documentName" }
            try {
                val platformFile = FileKitUtil.appCache/documentName
                Logger.e { "File inside try catch: $platformFile" }
                Logger.e { "File path inside try catch: ${platformFile.path}" }
                mutableStateFlow.update {
                    it.copy(documentPath = platformFile)
                }
                val canUpdateDocument =route.canUpdateDocument
                if (canUpdateDocument) {
                    sendAction(DocumentPreviewScreenAction.EnableUpdating)
                }
                mutableStateFlow.update {
                    it.copy(documentPath = platformFile)
                }
                sendAction(DocumentPreviewScreenAction.LoadDocument(DocumentType.Image("jpg")))
                loadDocumentFromPath(platformFile.path)
//                if (platformFile.extension == "pdf" ||
//                    platformFile.extension == "jpg" || platformFile.extension == "jpeg" || platformFile.extension == "png"
//                ) {
////                    sendAction(
////                        DocumentPreviewScreenAction.LoadDocument(
////                            if (platformFile.extension == "pdf") DocumentType.Pdf
////                            else DocumentType.Image(platformFile.extension),
////                        ),
////                    )
//
//                } else {
//                    sendEvent(DocumentPreviewEvent.OnNavigateBack)
//                }
            } catch (e: Exception) {
                Logger.e(e) { "Failed to load file"}
                sendEvent(DocumentPreviewEvent.OnNavigateBack)
            }
        }
    }

    override fun handleAction(action: DocumentPreviewScreenAction) {
        when (action) {
            DocumentPreviewScreenAction.NavigateBack -> {
                sendEvent(DocumentPreviewEvent.OnNavigateBack)
            }

            DocumentPreviewScreenAction.CancelUpdating -> {
                sendEvent(DocumentPreviewEvent.OnCancelUpdating)
            }

            DocumentPreviewScreenAction.DismissBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = false)
                }
            }

            DocumentPreviewScreenAction.EnableUpdating -> {
                mutableStateFlow.update {
                    it.copy(canUpdate = true)
                }
            }

            is DocumentPreviewScreenAction.LoadDocument -> {
                if (documentName.isBlank()) {
                    mutableStateFlow.update {
                        it.copy(documentType = action.documentType)
                    }
                    loadDocumentFromPath(documentName)
                } else {
                    sendEvent(DocumentPreviewEvent.OnDocumentRejected)
                }
            }

            DocumentPreviewScreenAction.PickFromFile -> {
                selectImageFromFiles()
            }

            DocumentPreviewScreenAction.PickFromGallery -> {
                selectImageFromGallery()
            }

            DocumentPreviewScreenAction.RejectDocument -> {
                sendEvent(DocumentPreviewEvent.OnDocumentRejected)
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
                                    canUpdate = false,
                                    dialogState = null,
                                    documentPath = platformFile,
                                )
                            }

                            if(comingFromServer) {
                                sendEvent(
                                    DocumentPreviewEvent.SendUpdatedDocument(platformFile.path, true)
                                )
                            } else {
                                sendEvent(
                                    DocumentPreviewEvent.SendUpdatedDocument(platformFile.path, false)
                                )
                            }
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
                                    canUpdate = false,
                                    dialogState = null,
                                    documentPath = platformFile,
                                )
                            }

                            if(comingFromServer) {
                                sendEvent(
                                    DocumentPreviewEvent.SendUpdatedDocument(platformFile.path, true)
                                )
                            } else {
                                sendEvent(
                                    DocumentPreviewEvent.SendUpdatedDocument(platformFile.path, false)
                                )
                            }
                        } ?: nullDialogState()
                    }
                }
            }
        }
    }

    private fun loadDocumentFromPath(filePath: String) {
        viewModelScope.launch {
            try {
                val platformFile = FileKitUtil.appCache/documentName
                Logger.e { "Selected document: $platformFile" }
                Logger.e { "Does file exist: ${platformFile.exists()}" }

                val bytesString = platformFile.readBytes()
                mutableStateFlow.update {
                    it.copy(
                        documentPath = platformFile,
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
    val canUpdate: Boolean = false,
    val documentPath: PlatformFile? = null,
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

sealed interface DocumentPreviewEvent {
    object OnCancelUpdating : DocumentPreviewEvent
    object OnNavigateBack : DocumentPreviewEvent
    object OnDocumentRejected : DocumentPreviewEvent
    data class SendUpdatedDocument(
        val documentPath: String,
        val updateForServer: Boolean
    ) : DocumentPreviewEvent
    data class OnSubmitClinked(
        val documentPath: String,
    ): DocumentPreviewEvent
}