package com.mifos.feature.client.clientAddDocuments

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.no_internet_message
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.FileKitUtil
import com.mifos.core.data.repository.DocumentDialogRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.utils.createDocumentRequestBody
import io.github.vinceglb.filekit.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString


class ClientAddDocumentScreenViewmodel(
    stateHandler: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val documentDialogRepository: DocumentDialogRepository,
) : BaseViewModel<
        ClientAddDocumentScreenState,
        ClientAddDocumentScreenEvents,
        ClientAddDocumentScreenAction,
        >(
    initialState = ClientAddDocumentScreenState(),
) {
    private val route = stateHandler.toRoute<ClientAddDocumentRoute>()
    private val documentStateString = route.documentStateString
    private val documentState = Json.decodeFromString<DocumentState>(documentStateString)
    private val clientId = documentState.clientId
    private val documentId = documentState.documentId
    private val entityType = documentState.entityType

    private  val updateForServer = route.updateOnServer


    init {
        viewModelScope.launch {
            val isComingFromPreviewScreen = route.comingFromPreviewScreen
            val isDocumentRejected = route.isDocumentRejected
            val documentPath = route.newDocumentPath
            try {
                if (isComingFromPreviewScreen) {
                    if (isDocumentRejected) {
                        mutableStateFlow.update {
                            it.copy(
                                platformFile = null,
                                isDocumentAdded = false,
                                pickedDocumentName = "",
                            )
                        }
                    } else {
                        val platformFile = PlatformFile(documentPath)
                        mutableStateFlow.update {
                            it.copy(
                                platformFile = platformFile,
                                isDocumentAdded = true,
                                pickedDocumentName = platformFile.name,
                                updatingDocument = updateForServer
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                errorDialogState(e.message ?: "Exception occurred")
            }
        }
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
                selectImageFromFiles()
            }

            ClientAddDocumentScreenAction.PickFromGallery -> {
                selectImageFromGallery()
            }

            ClientAddDocumentScreenAction.ViewDocument -> {
                sendEvent(
                    ClientAddDocumentScreenEvents.OnNavigateToPreviewScreen(
                        documentState = DocumentState(
                            clientId = clientId,
                            documentId = documentId,
                            entityType = entityType,
                        ),
                    ),
                )
            }

            ClientAddDocumentScreenAction.RetryUpdate -> {
                observerNetworkAndUpdate()
            }

            ClientAddDocumentScreenAction.RetryUpload -> {
                observerNetworkAndUpload()
            }

            is ClientAddDocumentScreenAction.UpdateDescription -> {
                mutableStateFlow.update {
                    it.copy(enteredDocumentDescription = action.text)
                }
            }

            ClientAddDocumentScreenAction.UpdateDocument -> {
                observerNetworkAndUpdate()
            }

            is ClientAddDocumentScreenAction.UpdateFileName -> {
                mutableStateFlow.update {
                    it.copy(enteredFileName = action.text)
                }
            }

            ClientAddDocumentScreenAction.UploadDocument -> {
                observerNetworkAndUpload()
            }

            ClientAddDocumentScreenAction.UseMoreOptions -> {}
        }
    }


    private fun observerNetworkAndUpload() {
        viewModelScope.launch {
            val isConnected = observerNetwork()
            mutableStateFlow.update {
                it.copy(isNetworkAvailable = isConnected)
            }
            when (isConnected) {
                true -> {
                    uploadDocument().collect { dataState ->
                        when (dataState) {
                            is DataState.Error<*> -> {
                                errorDialogState(dataState.message)
                            }

                            DataState.Loading -> {
                                mutableStateFlow.update {
                                    it.copy(showProgressBar = true)
                                }
                            }

                            is DataState.Success<*> -> {
                                mutableStateFlow.update {
                                    it.copy(showProgressBar = false)
                                }
                                sendEvent(ClientAddDocumentScreenEvents.OnNavigateBack)
                            }
                        }
                    }
                }

                false -> {
                    errorDialogState(
                        getString(Res.string.no_internet_message),
                    )
                }
            }
        }
    }


    private fun observerNetworkAndUpdate() {
        viewModelScope.launch {
            val isConnected = observerNetwork()
            mutableStateFlow.update {
                it.copy(isNetworkAvailable = isConnected)
            }
            when (isConnected) {
                true -> {
                    updateDocument().collect { dataState ->
                        when (dataState) {
                            is DataState.Error<*> -> {
                                errorDialogState(dataState.message)
                            }

                            DataState.Loading -> {
                                mutableStateFlow.update {
                                    it.copy(showProgressBar = true)
                                }
                            }

                            is DataState.Success<*> -> {
                                mutableStateFlow.update {
                                    it.copy(showProgressBar = false)
                                }
                                sendEvent(ClientAddDocumentScreenEvents.OnNavigateBack)
                            }
                        }
                    }
                }

                false -> {
                    errorDialogState(
                        getString(Res.string.no_internet_message),
                    )
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

                    is DataState.Success<*> -> {
                        dataState.data?.let { platformFile ->
                            Logger.e { "File from add screen: $platformFile" }
                            Logger.e { "File absolute path: ${platformFile.absolutePath()}" }
                            val fileBytes = platformFile.readBytes()
                            FileKitUtil.writeFileToCache(
                                platformFile.nameWithoutExtension,
                                platformFile.extension,
                                fileBytes,
                            ).collect { writeState ->
                                when (writeState) {
                                    is DataState.Error<*> -> {
                                        errorDialogState(writeState.message)
                                    }

                                    DataState.Loading -> {
                                        loadingDialogState()
                                    }

                                    is DataState.Success<*> -> {
                                        mutableStateFlow.update {
                                            it.copy(
                                                dialogState = null,
                                                platformFile = FileKitUtil.appCache/platformFile.name,
                                                pickedDocumentName = platformFile.nameWithoutExtension,
                                            )
                                        }

                                        sendAction(ClientAddDocumentScreenAction.DismissBottomSheet)
                                        sendEvent(
                                            ClientAddDocumentScreenEvents.OnNavigateToPreviewScreen(
                                                documentState = DocumentState(
                                                    clientId = clientId,
                                                    documentId = documentId,
                                                    entityType = entityType,
                                                    documentPath = state.platformFile!!.path,
                                                ),
                                            ),
                                        )

                                    }
                                }

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

                    is DataState.Success<*> -> {
                        dataState.data?.let { platformFile ->
                            Logger.e { "File from add screen: $platformFile" }
                            Logger.e { "File absolute path: ${platformFile.absolutePath()}" }
                            val fileBytes = platformFile.readBytes()
                            FileKitUtil.writeFileToCache(
                                platformFile.nameWithoutExtension,
                                platformFile.extension,
                                fileBytes,
                            ).collect { writeState ->
                                when (writeState) {
                                    is DataState.Error<*> -> {
                                        errorDialogState(writeState.message)
                                    }

                                    DataState.Loading -> {
                                        loadingDialogState()
                                    }

                                    is DataState.Success<*> -> {
                                        mutableStateFlow.update {
                                            it.copy(
                                                dialogState = null,
                                                platformFile = FileKitUtil.appCache/platformFile.name,
                                                pickedDocumentName = platformFile.nameWithoutExtension,
                                            )
                                        }

                                        sendAction(ClientAddDocumentScreenAction.DismissBottomSheet)
                                        sendEvent(
                                            ClientAddDocumentScreenEvents.OnNavigateToPreviewScreen(
                                                documentState = DocumentState(
                                                    clientId = clientId,
                                                    documentId = documentId,
                                                    entityType = entityType,
                                                    documentPath = state.platformFile!!.path,
                                                ),
                                            ),
                                        )

                                    }
                                }

                            }
                        } ?: nullDialogState()
                    }
                }
            }
        }
    }

    private suspend fun getMultiPartFormDataContent(file: PlatformFile) = createDocumentRequestBody(
        file,
        state.enteredFileName,
        state.enteredDocumentDescription,
    )

    private suspend fun observerNetwork() = networkMonitor.isOnline.first()


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
    data class OnNavigateToPreviewScreen(
        val documentState: DocumentState,
    ) : ClientAddDocumentScreenEvents
}