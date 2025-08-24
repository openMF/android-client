/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientEditProfile

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.UploadClientImageUseCase
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.imageToByteArray
import com.mifos.feature.client.clientSignature.toPlatformFile
import com.mifos.feature.client.utils.createImageRequestBody
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ClientProfileEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val uploadClientImageUseCase: UploadClientImageUseCase,
    private val clientDetailsRepo: ClientDetailsRepository,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ClientProfileEditState, ClientProfileEditEvent, ClientProfileEditAction>(
    initialState = ClientProfileEditState(),
) {
    private val route = savedStateHandle.toRoute<ClientEditProfileRoute>()

    init {
        mutableStateFlow.update {
            it.copy(
                id = route.id,
                name = route.name,
                accountNo = route.accountNo,
            )
        }
        observeNetwork()
        loadImage(route.id)
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
            }
        }
    }

    override fun handleAction(action: ClientProfileEditAction) {
        when (action) {
            ClientProfileEditAction.NavigateBack -> sendEvent(ClientProfileEditEvent.NavigateBack)
            is ClientProfileEditAction.OnNameChanged -> mutableStateFlow.update { it.copy(name = action.name) }
            is ClientProfileEditAction.OnAccountNoChanged -> mutableStateFlow.update { it.copy(accountNo = action.accountNo) }
            ClientProfileEditAction.OnRetry -> Unit
            ClientProfileEditAction.OnDeleteImage -> {
                mutableStateFlow.update {
                    it.copy(dialogState = ClientProfileEditState.DialogState.ShowDeleteDialog)
                }
            }
            ClientProfileEditAction.CancelDeleteClick -> mutableStateFlow.update { it.copy(dialogState = null) }
            ClientProfileEditAction.OnConfirmDeleteClick -> deleteClientImage()
            ClientProfileEditAction.OnUploadNewPhotoClick -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientProfileEditState.DialogState.ShowUploadOptions,
                        openImagePicker = true,
                    )
                }
            }
            ClientProfileEditAction.DismissModalBottomSheet -> mutableStateFlow.update { it.copy(dialogState = null) }
            is ClientProfileEditAction.UpdateImagePicker -> {
                mutableStateFlow.update {
                    it.copy(
                        openImagePicker = action.status,
                    )
                }
            }

            is ClientProfileEditAction.OnImageSelected -> {
                uploadImage(state.id, action.image)
            }
        }
    }

    private fun loadImage(clientId: Int) {
        viewModelScope.launch {
            clientDetailsRepo.getImage(clientId).collect { result ->
                when (result) {
                    is DataState.Success -> mutableStateFlow.update {
                        it.copy(
                            profileImage = imageToByteArray(result.data),
                            dialogState = null,
                        )
                    }
                    is DataState.Loading -> mutableStateFlow.update {
                        it.copy(dialogState = ClientProfileEditState.DialogState.Loading)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun uploadImage(id: Int, imageFile: ImageBitmap) = viewModelScope.launch {
        uploadClientImageUseCase(
            id,
            createImageRequestBody(imageFile.toPlatformFile(id.toString())),
        ).collect { result ->
            when (result) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientProfileEditState.DialogState.Error(result.message),
                        )
                    }
                }

                is DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientProfileEditState.DialogState.Loading,
                        )
                    }
                }

                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientProfileEditState.DialogState.Loading,
                            openImagePicker = false,
                        )
                    }
                    loadImage(route.id)
                }
            }
        }
    }

    fun deleteClientImage() {
        viewModelScope.launch {
            try {
                clientDetailsRepo.deleteClientImage(state.id)
                mutableStateFlow.update {
                    it.copy(
                        profileImage = null,
                        dialogState = null,
                    )
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientProfileEditState.DialogState.Error(
                            e.message ?: "Unknown Error",
                        ),
                    )
                }
            }
        }
    }
}

data class ClientProfileEditState(
    val id: Int = -1,
    val name: String = "",
    val accountNo: String = "",
    val profileImage: ByteArray? = null,
    val openImagePicker: Boolean = false,
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data object ShowDeleteDialog : DialogState
        data object ShowUploadOptions : DialogState
    }
}

sealed interface ClientProfileEditEvent {
    data object NavigateBack : ClientProfileEditEvent
    data object OnSaveSuccess : ClientProfileEditEvent
}

sealed interface ClientProfileEditAction {
    data object NavigateBack : ClientProfileEditAction
    data object OnRetry : ClientProfileEditAction
    data object OnDeleteImage : ClientProfileEditAction
    data object CancelDeleteClick : ClientProfileEditAction
    data object OnConfirmDeleteClick : ClientProfileEditAction
    data object OnUploadNewPhotoClick : ClientProfileEditAction
    data class OnNameChanged(val name: String) : ClientProfileEditAction
    data class OnAccountNoChanged(val accountNo: String) : ClientProfileEditAction
    data object DismissModalBottomSheet : ClientProfileEditAction
    data class UpdateImagePicker(val status: Boolean) : ClientProfileEditAction
    data class OnImageSelected(val image: ImageBitmap) : ClientProfileEditAction
}
