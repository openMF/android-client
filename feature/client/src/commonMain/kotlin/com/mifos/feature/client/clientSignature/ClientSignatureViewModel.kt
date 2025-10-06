/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientSignature

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.ImageCropper
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.crop.imageCropper
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.CreateSignatureUseCase
import com.mifos.core.domain.useCases.DownloadDocumentUseCase
import com.mifos.core.domain.useCases.GetDocumentsListUseCase
import com.mifos.core.domain.useCases.RemoveDocumentUseCase
import com.mifos.core.domain.useCases.UpdateSignatureUseCase
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.multipartRequestBody
import com.mifos.feature.client.utils.toPlatformFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientSignatureViewModel(
    private val createSignatureUseCase: CreateSignatureUseCase,
    private val removeDocumentUseCase: RemoveDocumentUseCase,
    private val getDocumentListUseCase: GetDocumentsListUseCase,
    private val updateSignatureUseCase: UpdateSignatureUseCase,
    private val downloadDocumentUseCase: DownloadDocumentUseCase,
    private val networkMonitor: NetworkMonitor,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ClientSignatureState, ClientSignatureEvent, ClientSignatureAction>(
    initialState = ClientSignatureState(),
) {
    val route = savedStateHandle.toRoute<ClientSignatureRoute>()

    companion object Signature {
        const val NAME = "clientSignature"
        const val DESCRIPTION = "Client Signature"
    }

    init {
        mutableStateFlow.update {
            it.copy(
                clientName = route.name,
                accountNo = route.accountNo,
            )
        }
        getSignatureOptionsAndObserveNetwork()
    }

    private fun getSignatureOptionsAndObserveNetwork() {
        viewModelScope.launch {
            observeNetwork()

            // Fetch the signatureId once
            getSignatureId()

            /**
             * Only run and fetch the signature if the signature already save in
             * server and we can find by 'signatureId' if it not null it
             * mean signature found if null it mean signature not found.
             */
            if (state.signatureId != null) {
                getSignature()
            }
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
            }
        }
    }

    private suspend fun createSignature(file: ImageBitmap) {
        createSignatureUseCase(
            Constants.CLIENTS,
            route.clientId,
            multipartRequestBody(
                file.toPlatformFile(route.accountNo),
                Signature.NAME,
                Signature.DESCRIPTION,
            ),
        ).collect { state ->
            when (state) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientSignatureState.DialogState.Error(state.message),
                        )
                    }
                }

                DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientSignatureState.DialogState.Loading,
                        )
                    }
                }

                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = null,
                        )
                    }
                    getSignatureOptionsAndObserveNetwork()
                }
            }
        }
    }

    private suspend fun updateSignature(file: ImageBitmap) {
        state.signatureId?.let { signatureId ->
            updateSignatureUseCase(
                Constants.CLIENTS,
                route.clientId,
                signatureId,
                multipartRequestBody(
                    file.toPlatformFile(route.accountNo),
                    Signature.NAME,
                    Signature.DESCRIPTION,
                ),
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientSignatureState.DialogState.Error(
                                    dataState.message,
                                ),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientSignatureState.DialogState.Loading,
                            )
                        }
                    }

                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = null,
                            )
                        }

                        getSignatureOptionsAndObserveNetwork()
                    }
                }
            }
        }
    }

    private suspend fun deleteSignature() {
        state.signatureId?.let { signatureId ->
            removeDocumentUseCase(Constants.CLIENTS, route.clientId, signatureId)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ClientSignatureState.DialogState.Error(
                                        dataState.message,
                                    ),
                                )
                            }
                        }

                        DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ClientSignatureState.DialogState.Loading,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = null,
                                    clientSignatureImage = null,
                                    signatureId = null,
                                )
                            }
                        }
                    }
                }
        }
    }

    /**
     * Fetch the `signatureId` from the document list and update the state.
     *
     * #### Fetching signature document ID approach:
     * - Upload the signature document with:
     *   - **Name**: `clientSignature`
     *   - **Description**: `Client Signature`
     * - The document is uploaded as part of a `multiPartDataForm`.
     * - Once uploaded, query the document list to find the document
     *   where `name == "clientSignature"`.
     * - Extract and update the `signatureId` in the state for later use
     *   (e.g., retrieval, deletion, or update).
     */
    private suspend fun getSignatureId() {
        getDocumentListUseCase(Constants.CLIENTS, route.clientId)
            .collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientSignatureState.DialogState.Error(dataState.message),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientSignatureState.DialogState.Loading,
                            )
                        }
                    }

                    is DataState.Success -> {
                        val signatureId =
                            dataState.data.firstOrNull { it.name == Signature.NAME }?.id

                        mutableStateFlow.update {
                            it.copy(
                                signatureId = signatureId,
                                dialogState = null,
                            )
                        }
                    }
                }
            }
    }

    private suspend fun getSignature() {
        state.signatureId?.let { signatureId ->
            downloadDocumentUseCase(Constants.CLIENTS, route.clientId, signatureId)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ClientSignatureState.DialogState.Error(
                                        dataState.message,
                                    ),
                                )
                            }
                        }

                        DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ClientSignatureState.DialogState.Loading,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    clientSignatureImage = dataState.data.readRawBytes(),
                                    dialogState = null,
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun imageCropper(file: ImageBitmap) {
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientSignatureState.DialogState.ShowImageCrop,
            )
        }

        viewModelScope.launch {
            val result = state.cropState.crop(bmp = file)
            mutableStateFlow.update {
                it.copy(
                    dialogState = ClientSignatureState.DialogState.Loading,
                )
            }
            when (result) {
                is CropError -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientSignatureState.DialogState.Error("Unexpected error"),
                        )
                    }
                }

                is CropResult.Success -> {
                    if (state.signatureId == null) {
                        createSignature(result.bitmap)
                    } else {
                        updateSignature(result.bitmap)
                    }
                }
                CropResult.Cancelled -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = null,
                        )
                    }
                }
            }
        }
    }

    override fun handleAction(action: ClientSignatureAction) {
        when (action) {
            ClientSignatureAction.NavigateBack -> sendEvent(ClientSignatureEvent.OnNavigationBack)
            ClientSignatureAction.OnRetry -> {
                getSignatureOptionsAndObserveNetwork()
            }

            ClientSignatureAction.DeleteSignature -> {
                viewModelScope.launch {
                    deleteSignature()
                }
            }

            ClientSignatureAction.ShowDeleteDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientSignatureState.DialogState.ShowDeleteDialog,
                    )
                }
            }

            ClientSignatureAction.ShowUploadOptionsBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientSignatureState.DialogState.ShowUploadOptions,
                    )
                }
            }

            ClientSignatureAction.OnDismissDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                    )
                }
            }

            ClientSignatureAction.OpenImagePicker -> {
                viewModelScope.launch {
                    val file = FileKit.openFilePicker(
                        mode = FileKitMode.Single,
                        type = FileKitType.Image,
                    )

                    file?.let {
                        imageCropper(it.toImageBitmap())
                    }
                }
            }

            ClientSignatureAction.OpenSignatureDraw -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientSignatureState.DialogState.ShowSignatureDraw,
                    )
                }
            }

            is ClientSignatureAction.CropImage -> {
                // it trigger from draw signature dialog
                viewModelScope.launch {
                    imageCropper(action.file)
                }
            }
        }
    }
}

data class ClientSignatureState(
    val clientSignatureImage: ByteArray? = null,
    val signatureId: Int? = null,
    val pickedImage: ImageBitmap? = null,
    val accountNo: String = "",
    val cropState: ImageCropper = imageCropper(),
    val clientName: String = "",
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = true,
) {
    sealed interface DialogState {
        object Loading : DialogState
        data class Error(val message: String) : DialogState
        data object ShowDeleteDialog : DialogState
        data object ShowUploadOptions : DialogState
        data object ShowImageCrop : DialogState
        data object ShowSignatureDraw : DialogState
    }
}

sealed interface ClientSignatureEvent {
    data object OnNavigationBack : ClientSignatureEvent
}

sealed interface ClientSignatureAction {
    data object OnRetry : ClientSignatureAction
    data object NavigateBack : ClientSignatureAction
    data object DeleteSignature : ClientSignatureAction
    data object ShowDeleteDialog : ClientSignatureAction
    data object OnDismissDialog : ClientSignatureAction
    data object ShowUploadOptionsBottomSheet : ClientSignatureAction
    data object OpenImagePicker : ClientSignatureAction
    data object OpenSignatureDraw : ClientSignatureAction
    data class CropImage(val file: ImageBitmap) : ClientSignatureAction
}
