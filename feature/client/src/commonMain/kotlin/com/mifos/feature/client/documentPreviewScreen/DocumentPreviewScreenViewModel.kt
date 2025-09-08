package com.mifos.feature.client.documentPreviewScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.FileKitUtil
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.utils.fromBase64DataUri
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
import io.ktor.util.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class DocumentPreviewScreenViewModel(
    savedStateHandle: SavedStateHandle,
): BaseViewModel<
        DocumentPreviewState,
        DocumentPreviewEvent,
        DocumentPreviewScreenAction
>(DocumentPreviewState()) {
    override fun handleAction(action: DocumentPreviewScreenAction) {
        TODO("Not yet implemented")
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
                        dataState.data?.let { platformFile ->
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = null,
                                    documentPath = platformFile,
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
                        dataState.data?.let { platformFile ->
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = null,
                                    documentPath = platformFile,
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
            val appCache = PlatformFile(filePath)

            FileKitUtil.loadFile(appCache.path).collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        errorDialogState(dataState.message)
                    }

                    DataState.Loading -> {
                        loadingDialogState()
                    }

                    is DataState.Success -> {
                        val bytesString = dataState.data.readBytes().encodeBase64()
                        mutableStateFlow.update {
                            it.copy(
                                documentPath = dataState.data,
                                documentContent = DocumentPreviewState.Content(bytesString),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun nullDialogState() {
        mutableStateFlow.update {
            it.copy(dialogState = null)
        }
    }

    private fun errorDialogState(message: String){
        mutableStateFlow.update {
            it.copy(dialogState = DocumentPreviewState.DialogState.Error(message))
        }
    }

    private fun loadingDialogState(){
        mutableStateFlow.update {
            it.copy(dialogState = DocumentPreviewState.DialogState.Loading)
        }
    }


}

data class DocumentPreviewState(
    val dialogState: DialogState? = null,
    val showBottomSheet: Boolean = false,
    val enableUpdating: Boolean = false,
    val documentPath: PlatformFile? = null,
    val documentContent: Content? = null
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
    }
    class Content(val response: String) {
        val byteArray: ByteArray
            get() = response.fromBase64DataUri()
    }
}

sealed interface DocumentPreviewScreenAction {
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
    object OnDocumentRejected : DocumentPreviewEvent
    data class SendUpdatedDocument(val documentPath: String): DocumentPreviewEvent
    data object OnSubmitClinked: DocumentPreviewEvent
}