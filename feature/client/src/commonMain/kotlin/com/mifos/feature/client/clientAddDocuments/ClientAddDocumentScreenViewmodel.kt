package com.mifos.feature.client.clientAddDocuments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.DocumentDialogRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.DocumentSelectAndUploadRepository
import com.mifos.feature.client.EntityDocumentState
import com.mifos.feature.client.documentPreviewScreen.DocumentType
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ClientAddDocumentScreenViewmodel(
    stateHandler: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val documentSelectAndUploadRepository: DocumentSelectAndUploadRepository,
) : BaseViewModel<
        ClientAddDocumentScreenState,
        ClientAddDocumentScreenEvents,
        ClientAddDocumentScreenAction,
        >(
    initialState = ClientAddDocumentScreenState(),
) {
    private val route = stateHandler.toRoute<ClientAddDocumentRoute>()

    private val entityDocumentStateFlow =
        documentSelectAndUploadRepository.entityDocumentStateMutableStateFlow

    val documentSelectAndUploadState = documentSelectAndUploadRepository.state

    init {
        updateStateReactively()
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
                sendEvent(ClientAddDocumentScreenEvents.OnNavigateToPreviewScreen)
            }

            ClientAddDocumentScreenAction.RetryUpdate -> {

            }

            ClientAddDocumentScreenAction.RetryUpload -> {
            }

            is ClientAddDocumentScreenAction.UpdateDescription -> {
                mutableStateFlow.update {
                    it.copy(enteredDocumentDescription = action.text)
                }
            }

            ClientAddDocumentScreenAction.UpdateDocument -> {

            }

            is ClientAddDocumentScreenAction.UpdateFileName -> {
                mutableStateFlow.update {
                    it.copy(enteredFileName = action.text)
                }
            }

            ClientAddDocumentScreenAction.UploadDocument -> {

            }

            ClientAddDocumentScreenAction.UseMoreOptions -> {}
        }
    }

    private fun pickFromGallery(){
        viewModelScope.launch {
            val deferredLoadingState = async {  collectLoadingState() }
            val resultDeferred = async {
                val result = documentSelectAndUploadRepository.selectImageFromGallery()

                result.onSuccess {
                    mutableStateFlow.update {
                        it.copy(showBottomSheet = false,)
                    }
                    sendEvent(ClientAddDocumentScreenEvents.OnNavigateToPreviewScreen)
                }.onFailure { throwable ->
                    mutableStateFlow.update {
                        it.copy(showBottomSheet = false,)
                    }
                    errorDialogState(throwable.message?: "Unknown error")
                }
            }
            awaitAll(deferredLoadingState, resultDeferred)
        }
    }

    private fun pickFromFiles() {
        viewModelScope.launch {
            val deferredLoadingState = async {  collectLoadingState() }
            val resultDeferred = async {
                val result = documentSelectAndUploadRepository.selectImageFromFile()

                result.onSuccess {
                    mutableStateFlow.update {
                        it.copy(showBottomSheet = false,)
                    }
                    sendEvent(ClientAddDocumentScreenEvents.OnNavigateToPreviewScreen)
                }.onFailure { throwable ->
                    mutableStateFlow.update {
                        it.copy(showBottomSheet = false,)
                    }
                    errorDialogState(throwable.message?: "Unknown error")
                }
            }
            awaitAll(deferredLoadingState, resultDeferred)
        }
    }

    private fun uploadDocument() {
        viewModelScope.launch {
            documentSelectAndUploadRepository.uploadDocument(
                state.enteredFileName,
                state.enteredDocumentDescription
            ).collect {dataState ->
                when(dataState) {
                    is DataState.Error<*> -> {
                        errorDialogState(dataState.message)
                    }
                    DataState.Loading -> {
                        loadingDialogState()
                    }
                    is DataState.Success<*> -> {
                        sendEvent(ClientAddDocumentScreenEvents.OnNavigateBack)
                    }
                }
            }
        }
    }

    private suspend fun observerNetwork() = networkMonitor.isOnline.first()

    private suspend fun collectLoadingState(){
        entityDocumentStateFlow.collect {entityDocumentState ->
            mutableStateFlow.update {
                it.copy(
                    dialogState = if(entityDocumentState.isLoading)
                        ClientAddDocumentScreenState.DialogState.Loading
                    else null
                )
            }
        }
    }

    private fun updateStateReactively(){
        viewModelScope.launch {
            entityDocumentStateFlow.collect { state ->
                mutableStateFlow.update {
                    it.copy(
                        isDocumentAdded = state.documentPreviewedAndAccepted,
                        updatingDocument = if(state.uploadType== EntityDocumentState.UploadType.Update) true
                        else false
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
                dialogState = ClientAddDocumentScreenState.DialogState.Error(message),
            )
        }
    }

    private fun loadingDialogState() {
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientAddDocumentScreenState.DialogState.Loading,
            )
        }
    }

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


data class ClientAddDocumentScreenState(
    val platformFile: PlatformFile? = null,
    val isDocumentAdded: Boolean = false,
    val updatingDocument: Boolean = false,
    val pickedDocumentName: String = "",
    val isNetworkAvailable: Boolean = false,
    val enteredDocumentDescription: String = "",
    val enteredFileName: String = "",
    val dialogState: DialogState? = null,
    val showBottomSheet: Boolean = false,
    val showProgressBar: Boolean = false,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
        data class UpdateError(val message: String) : DialogState
        data class UploadError(val message: String) : DialogState
    }
}


sealed interface ClientAddDocumentScreenAction {

    data object NavigateBack : ClientAddDocumentScreenAction
    data object AddNewDocument : ClientAddDocumentScreenAction
    data object DismissBottomSheet : ClientAddDocumentScreenAction
    data object UploadDocument : ClientAddDocumentScreenAction
    data object UpdateDocument : ClientAddDocumentScreenAction
    data object RetryUpdate : ClientAddDocumentScreenAction
    data object RetryUpload : ClientAddDocumentScreenAction
    data object PickFromGallery : ClientAddDocumentScreenAction
    data object PickFromFiles : ClientAddDocumentScreenAction
    data object UseMoreOptions : ClientAddDocumentScreenAction
    data object ViewDocument : ClientAddDocumentScreenAction
    data class UpdateFileName(val text: String) : ClientAddDocumentScreenAction
    data class UpdateDescription(val text: String) : ClientAddDocumentScreenAction

}


sealed interface ClientAddDocumentScreenEvents {
    object OnNavigateBack : ClientAddDocumentScreenEvents
    object OnNavigateToPreviewScreen: ClientAddDocumentScreenEvents
}