/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientIdentifiersAddUpdate

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.FileKitUtil
import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.data.repository.DocumentCreateUpdateRepository
import com.mifos.core.data.util.Error
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.extractErrorMessage
import com.mifos.core.domain.useCases.CreateClientIdentifierUseCase
import com.mifos.core.domain.useCases.DownloadDocumentUseCase
import com.mifos.core.domain.useCases.GetDocumentsListUseCase
import com.mifos.core.model.objects.noncoreobjects.DocumentType
import com.mifos.core.model.objects.noncoreobjects.IdentifierPayload
import com.mifos.core.ui.components.Status
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.multipartRequestBody
import com.mifos.feature.client.utils.toPlatformFile
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class ClientIdentifiersAddUpdateViewModel(
    private val clientIdentifiersRepository: ClientIdentifiersRepository,
    private val createClientIdentifierUseCase: CreateClientIdentifierUseCase,
    private val downloadDocumentUseCase: DownloadDocumentUseCase,
    private val getDocumentListUseCase: GetDocumentsListUseCase,
    private val repository: DocumentCreateUpdateRepository,
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ClientIdentifiersAddUpdateState, ClientIdentifiersAddUpdateEvent, ClientIdentifiersAddUpdateAction>(
    initialState = ClientIdentifiersAddUpdateState(),
) {
    private val route = savedStateHandle.toRoute<ClientIdentitiesAddUpdateRoute>()

    init {
        mutableStateFlow.update {
            it.copy(
                clientId = route.clientId,
                feature = route.feature,
            )
        }
        if (route.feature == Feature.ADD_IDENTIFIER) {
            getIdentifiersOptionsAndObserveNetwork()
        } else {
            viewModelScope.launch {
                getDocumentId()
            }
        }
    }

    private fun getIdentifiersOptionsAndObserveNetwork() {
        viewModelScope.launch {
            observeNetwork()

            getIdentifiersTemplate()
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
            }
        }
    }

    private suspend fun createClientIdentifier(identifierPayload: IdentifierPayload) {
        createClientIdentifierUseCase(route.clientId.toLong(), identifierPayload)
            .collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentifiersAddUpdateState.DialogState.Error(
                                    dataState.message,
                                ),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                isOverlayLoading = true,
                                dialogState = ClientIdentifiersAddUpdateState.DialogState.Loading,
                            )
                        }
                    }

                    is DataState.Success -> {
                        val error = extractErrorMessage(dataState.data)

                        if (error == Error.MSG_NOT_FOUND) {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = null,
                                    isOverlayLoading = false,
                                    feature = Feature.ADD_UPDATE_DOCUMENT,
                                )
                            }
                        } else {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ClientIdentifiersAddUpdateState.DialogState.Error(
                                        error.replace("unique", "document").replace("under", " under"),
                                    ),
                                    handleServerResponse = true,
                                )
                            }
                        }
                    }
                }
            }
    }

    private suspend fun getIdentifiersTemplate() {
        clientIdentifiersRepository.getClientIdentifierTemplate(clientId = route.clientId.toLong())
            .collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentifiersAddUpdateState.DialogState.Error(
                                    dataState.message,
                                ),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentifiersAddUpdateState.DialogState.Loading,
                            )
                        }
                    }

                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = null,
                                identifierTemplate = dataState.data.allowedDocumentTypes,
                            )
                        }
                    }
                }
            }
    }

    private suspend fun getDocument(extension: String?) {
        state.documentId?.let { documentId ->
            downloadDocumentUseCase(
                Constants.ENTITY_TYPE_CLIENT_IDENTIFIERS,
                route.clientId,
                documentId,
            )
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ClientIdentifiersAddUpdateState.DialogState.Error(
                                        dataState.message,
                                    ),
                                )
                            }
                        }

                        DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ClientIdentifiersAddUpdateState.DialogState.Loading,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = null,
                                    documentImageFile = dataState.data.readRawBytes(),
                                    fileExtension = extension,
                                )
                            }
                        }
                    }
                }
        }
    }

    /**
     * Retrieves the document ID for the given key.
     *
     * - If the document ID is `null`, it means the document does not exist.
     *   In this case, we display an error message along with a "Create Document" option,
     *   allowing the user to create a new document.
     * - If the document ID is found, it will be used to update the state accordingly.
     */
    private suspend fun getDocumentId() {
        getDocumentListUseCase(Constants.ENTITY_TYPE_CLIENT_IDENTIFIERS, route.clientId)
            .collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentifiersAddUpdateState.DialogState.Error(
                                    dataState.message,
                                ),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentifiersAddUpdateState.DialogState.Loading,
                            )
                        }
                    }

                    is DataState.Success -> {
                        val data =
                            dataState.data.firstOrNull { it.description == route.uniqueKeyForHandleDocument }

                        if (data?.id == null) {
                            // Handle missing document
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ClientIdentifiersAddUpdateState.DialogState.Error(
                                        "Document not found",
                                    ),
                                    documentNotFount = true,
                                )
                            }
                        } else {
                            // Update state with valid document details
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = null,
                                    documentId = data.id,
                                    imageFileName = data.fileName,
                                    documentName = data.fileName,
                                    previewButtonHandle = if (state.feature == Feature.ADD_UPDATE_DOCUMENT) PreviewButtonHandle.Submit else PreviewButtonHandle.Hide,
                                )
                            }

                            val extension = data.type?.substringAfterLast("/", "")
                            getDocument(extension)
                        }
                    }
                }
            }
    }

    private suspend fun createDocument(file: PlatformFile, name: String?, uniqueKeyForHandleDocument: String) {
        repository.createDocument(
            Constants.ENTITY_TYPE_CLIENT_IDENTIFIERS,
            route.clientId,
            multipartRequestBody(
                file = file,
                name = name,
                description = uniqueKeyForHandleDocument,
            ),
        ).collect { state ->
            when (state) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientIdentifiersAddUpdateState.DialogState.Error(state.message),
                        )
                    }
                }

                DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(
                            isOverlayLoading = true,
                            dialogState = ClientIdentifiersAddUpdateState.DialogState.Loading,
                        )
                    }
                }

                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            isOverlayLoading = false,
                            dialogState = null,
                        )
                    }

                    sendEvent(ClientIdentifiersAddUpdateEvent.NavigateBackWithUpdatedList)
                }
            }
        }
    }

    private suspend fun updateDocument(file: PlatformFile, name: String?, uniqueKeyForHandleDocument: String?) {
        state.documentId?.let { documentId ->
            repository.updateDocument(
                entityType = Constants.CLIENTS,
                entityId = route.clientId,
                documentId = documentId,
                file = multipartRequestBody(
                    file = file,
                    name = name,
                    description = uniqueKeyForHandleDocument,
                ),
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentifiersAddUpdateState.DialogState.Error(
                                    dataState.message,
                                ),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentifiersAddUpdateState.DialogState.Loading,
                            )
                        }
                    }

                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = null,
                            )
                        }

                        sendEvent(ClientIdentifiersAddUpdateEvent.NavigateBackWithUpdatedList)
                    }
                }
            }
        }
    }

    override fun handleAction(action: ClientIdentifiersAddUpdateAction) {
        when (action) {
            ClientIdentifiersAddUpdateAction.CloseDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                    )
                }
            }

            ClientIdentifiersAddUpdateAction.NavigateBack -> {
                sendEvent(ClientIdentifiersAddUpdateEvent.NavigateBack)
            }

            is ClientIdentifiersAddUpdateAction.OnDocumentTypeChange -> {
                mutableStateFlow.update {
                    it.copy(
                        documentType = state.identifierTemplate?.get(action.index)?.name,
                        documentTypeId = state.identifierTemplate?.get(action.index)?.id,
                    )
                }
            }

            is ClientIdentifiersAddUpdateAction.OnStatusChange -> {
                mutableStateFlow.update {
                    it.copy(
                        status = state.statusList[action.index],
                    )
                }
            }

            is ClientIdentifiersAddUpdateAction.OnDescriptionChange -> {
                mutableStateFlow.update {
                    it.copy(
                        description = action.value,
                    )
                }
            }

            is ClientIdentifiersAddUpdateAction.OnDocumentKeyChange -> {
                mutableStateFlow.update {
                    it.copy(
                        documentKey = action.value,
                    )
                }
            }

            ClientIdentifiersAddUpdateAction.OnCreateClientIdentifier -> {
                viewModelScope.launch {
                    createClientIdentifier(
                        IdentifierPayload(
                            documentKey = state.documentKey,
                            documentTypeId = state.documentTypeId,
                            status = state.status,
                            description = state.description,
                        ),
                    )
                }
            }

            ClientIdentifiersAddUpdateAction.OnRetry -> {
                getIdentifiersOptionsAndObserveNetwork()
            }

            is ClientIdentifiersAddUpdateAction.OnDocumentNameChange -> {
                mutableStateFlow.update {
                    it.copy(
                        documentName = action.value,
                    )
                }
            }

            ClientIdentifiersAddUpdateAction.OnShowBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientIdentifiersAddUpdateState.DialogState.ShowBottomSheet,
                    )
                }
            }

            ClientIdentifiersAddUpdateAction.OnSelectFile -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                    )
                }

                viewModelScope.launch {
                    FileKitUtil.pickFile().collect { dataState ->
                        when (dataState) {
                            is DataState.Success -> {
                                mutableStateFlow.update {
                                    it.copy(
                                        documentImageFile = dataState.data?.readBytes(),
                                        imageFileName = dataState.data?.name,
                                        fileExtension = dataState.data?.extension,
                                        dialogState = null,
                                        feature = Feature.VIEW_DOCUMENT,
                                        previewButtonHandle = PreviewButtonHandle.Submit,
                                    )
                                }
                            }

                            is DataState.Error -> {
                                mutableStateFlow.update {
                                    it.copy(
                                        dialogState = ClientIdentifiersAddUpdateState.DialogState.Error(
                                            dataState.message,
                                        ),
                                    )
                                }
                            }

                            DataState.Loading -> {}
                        }
                    }
                }
            }

            ClientIdentifiersAddUpdateAction.OnSelectImage -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                    )
                }

                viewModelScope.launch {
                    FileKitUtil.pickImage().collect { dataState ->
                        when (dataState) {
                            is DataState.Success -> {
                                mutableStateFlow.update {
                                    it.copy(
                                        documentImageFile = dataState.data?.readBytes(),
                                        imageFileName = dataState.data?.name,
                                        fileExtension = dataState.data?.extension,
                                        dialogState = null,
                                        feature = Feature.VIEW_DOCUMENT,
                                        previewButtonHandle = PreviewButtonHandle.Submit,
                                    )
                                }
                            }

                            is DataState.Error -> {
                                mutableStateFlow.update {
                                    it.copy(
                                        dialogState = ClientIdentifiersAddUpdateState.DialogState.Error(
                                            dataState.message,
                                        ),
                                    )
                                }
                            }

                            DataState.Loading -> {}
                        }
                    }
                }
            }

            ClientIdentifiersAddUpdateAction.OnCreateDocument -> {
                viewModelScope.launch {
                    val fileName = state.imageFileName.orEmpty()
                    val file = state.documentImageFile?.toPlatformFile(fileName) ?: return@launch

                    /**
                     * A unique key generated for document operations (update/delete).
                     *
                     * This key is built by concatenating:
                     * - the document type name
                     * - the document key
                     * - the current status
                     *
                     * It ensures each document can be uniquely identified and handled
                     * even if multiple documents share the same type or key.
                     */
                    val uniqueKey = state.documentType + state.documentKey + state.status

                    if (state.documentId == null) {
                        createDocument(
                            file = file,
                            name = state.documentName,
                            uniqueKeyForHandleDocument = route.uniqueKeyForHandleDocument ?: uniqueKey,
                        )
                    } else {
                        updateDocument(
                            file = file,
                            name = state.documentName,
                            uniqueKeyForHandleDocument = route.uniqueKeyForHandleDocument,
                        )
                    }
                }
            }

            ClientIdentifiersAddUpdateAction.OnClosePreview -> {
                mutableStateFlow.update {
                    it.copy(
                        feature = Feature.ADD_UPDATE_DOCUMENT,
                    )
                }
            }

            ClientIdentifiersAddUpdateAction.OnOpenPreview -> {
                mutableStateFlow.update {
                    it.copy(
                        feature = Feature.VIEW_DOCUMENT,
                        previewButtonHandle = PreviewButtonHandle.UploadNew,
                    )
                }
            }

            ClientIdentifiersAddUpdateAction.OnNotFoundDocument -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                        feature = Feature.ADD_UPDATE_DOCUMENT,
                        documentKey = "",
                    )
                }
            }
        }
    }
}

data class ClientIdentifiersAddUpdateState(
    val identifierTemplate: List<DocumentType>? = emptyList(),
    val statusList: List<String> = listOf(Status.Inactive.name, Status.Active.name),
    val status: String? = null,
    val documentKey: String? = null,
    val description: String? = null,
    val documentTypeId: Int? = null,
    val documentType: String? = null,
    val documentName: String? = null,
    val imageFileName: String? = null,
    val documentImageFile: ByteArray? = null,
    val documentId: Int? = null,
    val fileExtension: String? = null,
    val dialogState: DialogState? = null,
    val clientId: Int = -1,
    val isOverlayLoading: Boolean = false,
    val handleServerResponse: Boolean = false,
    val documentKeyForUpdate: String? = null,
    val feature: Feature = Feature.ADD_IDENTIFIER,
    val documentNotFount: Boolean = false,
    val previewButtonHandle: PreviewButtonHandle = PreviewButtonHandle.Submit,
    val networkConnection: Boolean = true,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data object ShowBottomSheet : DialogState
    }
}

sealed interface ClientIdentifiersAddUpdateEvent {
    data object NavigateBack : ClientIdentifiersAddUpdateEvent
    data object NavigateBackWithUpdatedList : ClientIdentifiersAddUpdateEvent
}

sealed interface ClientIdentifiersAddUpdateAction {
    data object CloseDialog : ClientIdentifiersAddUpdateAction
    data object NavigateBack : ClientIdentifiersAddUpdateAction
    data object OnRetry : ClientIdentifiersAddUpdateAction
    data class OnDocumentTypeChange(val index: Int) : ClientIdentifiersAddUpdateAction
    data class OnStatusChange(val index: Int) : ClientIdentifiersAddUpdateAction
    data class OnDocumentKeyChange(val value: String) : ClientIdentifiersAddUpdateAction
    data class OnDescriptionChange(val value: String) : ClientIdentifiersAddUpdateAction
    data class OnDocumentNameChange(val value: String) : ClientIdentifiersAddUpdateAction
    data object OnCreateClientIdentifier : ClientIdentifiersAddUpdateAction
    data object OnCreateDocument : ClientIdentifiersAddUpdateAction
    data object OnShowBottomSheet : ClientIdentifiersAddUpdateAction
    data object OnSelectImage : ClientIdentifiersAddUpdateAction
    data object OnSelectFile : ClientIdentifiersAddUpdateAction
    data object OnClosePreview : ClientIdentifiersAddUpdateAction
    data object OnOpenPreview : ClientIdentifiersAddUpdateAction
    data object OnNotFoundDocument : ClientIdentifiersAddUpdateAction
}

@Serializable
enum class Feature {
    ADD_IDENTIFIER,
    ADD_UPDATE_DOCUMENT,
    VIEW_DOCUMENT,
}

enum class PreviewButtonHandle {
    Hide,
    Submit,
    UploadNew,
}
