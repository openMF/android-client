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
import com.mifos.feature.client.utils.createDocumentRequestBody
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString


class ClientAddDocumentScreenViewmodel(
    stateHandler: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val documentDialogRepository: DocumentDialogRepository,
) : BaseViewModel
<
        ClientAddDocumentCombinedScreenState,
        ClientAddDocumentScreenEvents,
        ClientAddDocumentScreenAction,
        >(
    initialState = ClientAddDocumentCombinedScreenState(),
) {
    private val clientId = stateHandler.toRoute<ClientAddDocumentGraphRoute>().clientId
    private val documentId = stateHandler.toRoute<ClientAddDocumentGraphRoute>().documentId
    private val entityType = stateHandler.toRoute<ClientAddDocumentGraphRoute>().entityType


    init {
        if(stateHandler.toRoute<ClientAddDocumentGraphRoute>().isUpdating) {
            sendEvent(ClientAddDocumentScreenEvents.AddDocumentEvent.NavigateToPreviewScreen)
        }
    }

    override fun handleAction(action: ClientAddDocumentScreenAction) {
        when (action) {
            is ClientAddDocumentScreenAction.AddDocumentScreen -> {
                handleAddDocumentScreen(action)
            }

            is ClientAddDocumentScreenAction.PreviewDocumentActions -> {
                handleDocumentPreviewScreenAction(action)
            }
        }
    }

    private fun handleAddDocumentScreen(action: ClientAddDocumentScreenAction.AddDocumentScreen) {

        when (action) {
            ClientAddDocumentScreenAction.AddDocumentScreen.AddNewDocument -> {
                updateAddDocumentScreenState {
                    it.copy(showBottomSheet = true)
                }
            }

            ClientAddDocumentScreenAction.AddDocumentScreen.DismissBottomSheet -> {
                updateAddDocumentScreenState {
                    it.copy(showBottomSheet = false)
                }
            }

            ClientAddDocumentScreenAction.AddDocumentScreen.NavigateBack -> {
                sendEvent(
                    ClientAddDocumentScreenEvents
                        .AddDocumentEvent.OnNavigateBack,
                )
            }

            ClientAddDocumentScreenAction.AddDocumentScreen.PickFromFiles -> {
                selectImageFromFiles(UploadScreen.ADD)
            }

            ClientAddDocumentScreenAction.AddDocumentScreen.PickFromGallery -> {
                selectImageFromGallery(UploadScreen.ADD)
            }

            ClientAddDocumentScreenAction.AddDocumentScreen.PreviewUploadDocument -> {
                sendEvent(
                    ClientAddDocumentScreenEvents
                        .AddDocumentEvent.NavigateToPreviewScreen,
                )
            }

            ClientAddDocumentScreenAction.AddDocumentScreen.RetryUpdate -> {
                observerNetworkAndUpdate()
            }

            ClientAddDocumentScreenAction.AddDocumentScreen.RetryUpload -> {
                observerNetworkAndUpload()
            }
            ClientAddDocumentScreenAction.AddDocumentScreen.ViewDocument -> {
                sendEvent(
                    ClientAddDocumentScreenEvents.PreviewDocumentEvent.OnNavigateToAddDocScreen
                )
            }

            is ClientAddDocumentScreenAction.AddDocumentScreen.UpdateDescription -> {
                updateAddDocumentScreenState {
                    it.copy(enteredDocumentDescription = action.text)
                }
            }

            ClientAddDocumentScreenAction.AddDocumentScreen.UpdateDocument -> {
                observerNetworkAndUpdate()
            }

            is ClientAddDocumentScreenAction.AddDocumentScreen.UpdateFileName -> {
                updateAddDocumentScreenState {
                    it.copy(enteredFileName = action.text)
                }
            }

            ClientAddDocumentScreenAction.AddDocumentScreen.UploadDocument -> {
                observerNetworkAndUpload()
            }

            ClientAddDocumentScreenAction.AddDocumentScreen.UseMoreOptions -> {}
        }

    }

    private fun handleDocumentPreviewScreenAction(action: ClientAddDocumentScreenAction.PreviewDocumentActions) {
        when (action) {
            ClientAddDocumentScreenAction.PreviewDocumentActions.ClosePreviewActions -> {
                updateDocumentPreviewScreenState {
                    it.copy(isUpdatingDocument = false)
                }
                sendEvent(
                    ClientAddDocumentScreenEvents
                        .PreviewDocumentEvent.OnNavigateToAddDocScreen,
                )
            }

            ClientAddDocumentScreenAction.PreviewDocumentActions.PickFromFiles -> {
                selectImageFromFiles(UploadScreen.PREVIEW)
            }

            ClientAddDocumentScreenAction.PreviewDocumentActions.PickFromGallery -> {
                selectImageFromGallery(UploadScreen.PREVIEW)
            }

            ClientAddDocumentScreenAction.PreviewDocumentActions.ToggleBottomSheet -> {
                updateDocumentPreviewScreenState {
                    it.copy(showBottomSheet = !it.showBottomSheet)
                }
            }

            ClientAddDocumentScreenAction.PreviewDocumentActions.SubmitDocument -> {
                mutableStateFlow.update {
                    it.copy(isDocumentAdded = true)
                }
                sendEvent(
                    ClientAddDocumentScreenEvents
                        .PreviewDocumentEvent.OnNavigateToAddDocScreen,
                )
            }

            ClientAddDocumentScreenAction.PreviewDocumentActions.UseMoreOptions -> {}
            ClientAddDocumentScreenAction.PreviewDocumentActions.SubmitNew -> {
                updateDocumentPreviewScreenState {
                    it.copy(showBottomSheet = true)
                }
            }
        }
    }

    private fun observerNetworkAndUpload() {
        viewModelScope.launch {
            val isConnected = observerNetwork()
            updateAddDocumentScreenState {
                it.copy(isNetworkAvailable = isConnected,)
            }
            when (isConnected) {
                true -> {
                    uploadDocument().collect { dataState ->
                        when (dataState) {
                            is DataState.Error<*> -> {
                                updateAddDocumentScreenState {
                                    it.copy(
                                        dialogState = AddDocumentScreenState
                                            .DialogState.UploadError(dataState.message),
                                    )
                                }
                            }

                            DataState.Loading -> {
                                updateAddDocumentScreenState {
                                    it.copy(showProgressBar = true)
                                }
                            }

                            is DataState.Success<*> -> {
                                updateAddDocumentScreenState {
                                    it.copy(showProgressBar = false)
                                }
                                sendEvent(
                                    ClientAddDocumentScreenEvents
                                        .AddDocumentEvent.OnNavigateBack,
                                )
                            }
                        }
                    }
                }

                false -> {
                    noInternetErrorDialog()
                }
            }
        }
    }


    private fun observerNetworkAndUpdate() {
        viewModelScope.launch {
            val isConnected = observerNetwork()
            updateAddDocumentScreenState {
                it.copy(isNetworkAvailable = isConnected)
            }
            when (isConnected) {
                true -> {
                    updateDocument().collect { dataState ->
                        when (dataState) {
                            is DataState.Error<*> -> {
                                updateAddDocumentScreenState {
                                    it.copy(
                                        dialogState = AddDocumentScreenState
                                            .DialogState.UpdateError(dataState.message),
                                    )
                                }
                            }

                            DataState.Loading -> {
                                updateAddDocumentScreenState {
                                    it.copy(showProgressBar = true)
                                }
                            }

                            is DataState.Success<*> -> {
                                updateAddDocumentScreenState {
                                    it.copy(showProgressBar = true)
                                }
                                sendEvent(
                                    ClientAddDocumentScreenEvents
                                        .AddDocumentEvent.OnNavigateBack,
                                )
                            }
                        }
                    }
                }

                false -> {
                    noInternetErrorDialog()
                }
            }
        }
    }


    private fun uploadDocument() = flow {
        emit(DataState.Loading)

        val result = try {
            val document = state.platformFile
            if (document == null) {
                DataState.Error(IllegalStateException("Document not loaded."))
            } else {
                val multiPartFormDataContent = getMultiPartFormDataContent(document)
                val result = documentDialogRepository.createDocument(
                    entityType = entityType,
                    entityId = clientId,
                    file = multiPartFormDataContent,
                )
                result
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
        emit(result)
    }

    private fun updateDocument() = flow {
        emit(DataState.Loading)
        val result = try {
            val document = state.platformFile
            if (document == null) {
                DataState.Error(IllegalStateException("Document not loaded."))
            } else {
                val multiPartFormDataContent = getMultiPartFormDataContent(document)
                val result = documentDialogRepository.updateDocument(
                    entityType = entityType,
                    entityId = clientId,
                    documentId = documentId,
                    file = multiPartFormDataContent,
                )
                result
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
        emit(result)
    }

    private fun selectImageFromGallery(uploadScreen: UploadScreen) {
        viewModelScope.launch {
            FileKitUtil.pickImage().collect { dataState ->
                when (dataState) {
                    is DataState.Error<*> -> {
                        updateDialogState(
                            uploadScreen,
                            addDocumentScreenDialogState = AddDocumentScreenState.DialogState.Error(
                                dataState.message,
                            ),
                            documentPreviewScreenStateState = DocumentPreviewScreenState.DialogState.Error(
                                dataState.message,
                            ),
                        )
                    }

                    DataState.Loading -> {
                        loadingDialogState(uploadScreen)
                    }

                    is DataState.Success<*> -> {
                        updateDialogState(
                            uploadScreen = uploadScreen,
                            addDocumentScreenDialogState = null,
                            documentPreviewScreenStateState = null,
                        )
                        if (dataState.data != null) {
                            mutableStateFlow.update {
                                it.copy(
                                    platformFile = dataState.data,
                                    pickedDocumentName = dataState.data?.nameWithoutExtension ?: "",
                                )
                            }
                            when(uploadScreen){
                                UploadScreen.ADD -> {
                                    updateAddDocumentScreenState {
                                        it.copy(showBottomSheet = false)
                                    }
                                    sendEvent(
                                        ClientAddDocumentScreenEvents.AddDocumentEvent.NavigateToPreviewScreen
                                    )
                                }
                                UploadScreen.PREVIEW -> {
                                    updateDocumentPreviewScreenState {
                                        it.copy(showBottomSheet = false)
                                    }
                                    sendEvent(
                                        ClientAddDocumentScreenEvents.PreviewDocumentEvent.OnNavigateToAddDocScreen
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private fun selectImageFromFiles(uploadScreen: UploadScreen) {
        viewModelScope.launch {
            FileKitUtil.pickPdfFile().collect { dataState ->
                when (dataState) {
                    is DataState.Error<*> -> {
                        updateDialogState(
                            uploadScreen,
                            addDocumentScreenDialogState = AddDocumentScreenState.DialogState.Error(
                                dataState.message,
                            ),
                            documentPreviewScreenStateState = DocumentPreviewScreenState.DialogState.Error(
                                dataState.message,
                            ),
                        )
                    }

                    DataState.Loading -> {
                        loadingDialogState(uploadScreen)
                    }

                    is DataState.Success<*> -> {
                        updateDialogState(
                            uploadScreen = uploadScreen,
                            addDocumentScreenDialogState = null,
                            documentPreviewScreenStateState = null,
                        )
                        if (dataState.data != null) {

                            mutableStateFlow.update {
                                it.copy(
                                    platformFile = dataState.data,
                                    pickedDocumentName = dataState.data?.nameWithoutExtension ?: "",
                                )
                            }
                            when(uploadScreen){
                                UploadScreen.ADD -> {
                                    updateAddDocumentScreenState {
                                        it.copy(showBottomSheet = false)
                                    }
                                    sendEvent(
                                        ClientAddDocumentScreenEvents.AddDocumentEvent.NavigateToPreviewScreen
                                    )
                                }
                                UploadScreen.PREVIEW -> {
                                    updateDocumentPreviewScreenState {
                                        it.copy(
                                            showBottomSheet = false,
                                        )
                                    }
                                    sendEvent(
                                        ClientAddDocumentScreenEvents.PreviewDocumentEvent.OnNavigateToAddDocScreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun getMultiPartFormDataContent(file: PlatformFile) = createDocumentRequestBody(
        file,
        state.addDocumentScreenState.enteredFileName,
        state.addDocumentScreenState.enteredDocumentDescription,
    )

    private suspend fun observerNetwork() =
        networkMonitor.isOnline.first()

    private fun loadingDialogState(uploadScreen: UploadScreen) {
        updateDialogState(
            uploadScreen,
            addDocumentScreenDialogState = AddDocumentScreenState.DialogState.Loading,
            documentPreviewScreenStateState = DocumentPreviewScreenState.DialogState.Loading,
        )
    }

    private fun updateDialogState(
        uploadScreen: UploadScreen,
        addDocumentScreenDialogState: AddDocumentScreenState.DialogState?,
        documentPreviewScreenStateState: DocumentPreviewScreenState.DialogState?,
    ) {
        when (uploadScreen) {
            UploadScreen.ADD -> {
                updateAddDocumentScreenState {
                    it.copy(
                        dialogState = addDocumentScreenDialogState,
                    )
                }
            }

            UploadScreen.PREVIEW -> {
                updateDocumentPreviewScreenState {
                    it.copy(
                        dialogState = documentPreviewScreenStateState,
                    )
                }
            }
        }
    }

    private fun updateAddDocumentScreenState(
        updateAddDocumentScreenState: (AddDocumentScreenState) ->
        AddDocumentScreenState,
    ) {
        mutableStateFlow.update {
            it.copy(
                addDocumentScreenState = updateAddDocumentScreenState(it.addDocumentScreenState),
            )
        }
    }

    private fun updateDocumentPreviewScreenState(
        updatePreviewScreenState: (DocumentPreviewScreenState) ->
        DocumentPreviewScreenState,
    ) {
        mutableStateFlow.update {
            it.copy(
                previewScreenState = updatePreviewScreenState(it.previewScreenState),
            )
        }
    }

    private fun noInternetErrorDialog() {
        viewModelScope.launch {
            val noInternet = getString(Res.string.no_internet_message)
            updateDialogState(
                UploadScreen.ADD,
                addDocumentScreenDialogState = AddDocumentScreenState.DialogState.Error(
                    noInternet,
                ),
                documentPreviewScreenStateState = DocumentPreviewScreenState.DialogState.Error(
                    noInternet,
                ),
            )
        }
    }

    private fun loadDocument(extension: String){
        viewModelScope.launch {
            val appCache = FileKitUtil.appCache/"attachment.${extension}"

            FileKitUtil.readFileAsByteArray(appCache.path).collect {

            }
        }
    }

}

enum class UploadScreen {
    ADD,
    PREVIEW
}


data class ClientAddDocumentCombinedScreenState(
    val platformFile: PlatformFile? = null,
    val isDocumentAdded: Boolean = false,
    val pickedDocumentName: String = "",
    val previewScreenState: DocumentPreviewScreenState = DocumentPreviewScreenState(),
    val addDocumentScreenState: AddDocumentScreenState = AddDocumentScreenState(),
)

data class AddDocumentScreenState(
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


data class DocumentPreviewScreenState(
    val dialogState: DialogState? = null,
    val isUpdatingDocument: Boolean = false,
    val showBottomSheet: Boolean = false,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
    }
}


sealed interface ClientAddDocumentScreenAction {
    sealed interface AddDocumentScreen : ClientAddDocumentScreenAction {

        data object NavigateBack : AddDocumentScreen
        data object AddNewDocument : AddDocumentScreen
        data object ViewDocument: AddDocumentScreen
        data object DismissBottomSheet : AddDocumentScreen
        data object UploadDocument : AddDocumentScreen
        data object UpdateDocument : AddDocumentScreen
        data object RetryUpdate : AddDocumentScreen
        data object RetryUpload : AddDocumentScreen
        data object PickFromGallery : AddDocumentScreen
        data object PickFromFiles : AddDocumentScreen
        data object UseMoreOptions : AddDocumentScreen
        data object PreviewUploadDocument : AddDocumentScreen
        data class UpdateFileName(val text: String) : AddDocumentScreen
        data class UpdateDescription(val text: String) : AddDocumentScreen
    }

    sealed interface PreviewDocumentActions : ClientAddDocumentScreenAction {
        data object ClosePreviewActions : PreviewDocumentActions
        data object PickFromGallery : PreviewDocumentActions
        data object PickFromFiles : PreviewDocumentActions
        data object UseMoreOptions : PreviewDocumentActions
        data object SubmitDocument : PreviewDocumentActions
        data object SubmitNew : PreviewDocumentActions
        data object ToggleBottomSheet : PreviewDocumentActions
    }

}


sealed interface ClientAddDocumentScreenEvents {
    sealed interface AddDocumentEvent : ClientAddDocumentScreenEvents {
        data object OnNavigateBack : AddDocumentEvent
        data object NavigateToPreviewScreen : AddDocumentEvent

    }

    sealed interface PreviewDocumentEvent : ClientAddDocumentScreenEvents {
        data object OnNavigateToAddDocScreen : PreviewDocumentEvent

    }

}