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

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_profile_photo_updated_failure
import androidclient.feature.client.generated.resources.client_profile_photo_updated_success
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.ImageCropper
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.crop.imageCropper
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.UploadClientImageUseCase
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.imageToByteArray
import com.mifos.core.ui.util.multipartRequestBody
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

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
            is ClientProfileEditAction.OnAccountNoChanged -> mutableStateFlow.update {
                it.copy(
                    accountNo = action.accountNo,
                )
            }

            ClientProfileEditAction.OnRetry -> Unit
            ClientProfileEditAction.OnDeleteImage -> {
                mutableStateFlow.update {
                    it.copy(dialogState = ClientProfileEditState.DialogState.ShowDeleteDialog)
                }
            }

            ClientProfileEditAction.CancelDeleteClick -> mutableStateFlow.update {
                it.copy(
                    dialogState = null,
                )
            }

            ClientProfileEditAction.OnConfirmDeleteClick -> deleteClientImage()
            ClientProfileEditAction.OnUploadNewPhotoClick -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientProfileEditState.DialogState.ShowUploadOptions,
                        openImagePicker = true,
                    )
                }
            }

            ClientProfileEditAction.DismissModalBottomSheet -> mutableStateFlow.update {
                it.copy(
                    dialogState = null,
                )
            }

            is ClientProfileEditAction.UpdateImagePicker -> {
                mutableStateFlow.update {
                    it.copy(
                        openImagePicker = action.status,
                    )
                }
            }

            ClientProfileEditAction.OnNext -> sendEvent(ClientProfileEditEvent.NavigateBack)
            ClientProfileEditAction.OnDismissDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                    )
                }
            }

            is ClientProfileEditAction.OpenCamera -> {
                viewModelScope.launch {
                    action.file ?: return@launch
                    imageCropper(action.file.toImageBitmap(), action.file.name)
                }
            }

            ClientProfileEditAction.OpenImagePicker -> {
                viewModelScope.launch {
                    val file = FileKit.openFilePicker(
                        mode = FileKitMode.Single,
                        type = FileKitType.Image,
                    )

                    file?.let {
                        imageCropper(it.toImageBitmap(), it.name)
                    }
                }
            }
        }
    }

    private fun imageCropper(file: ImageBitmap, fileName: String) {
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientProfileEditState.DialogState.ShowImageCrop,
            )
        }

        viewModelScope.launch {
            val result = state.cropState.crop(bmp = file)
            mutableStateFlow.update {
                it.copy(
                    dialogState = ClientProfileEditState.DialogState.Loading,
                )
            }
            when (result) {
                is CropError -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientProfileEditState.DialogState.Error("Unexpected error"),
                        )
                    }
                }

                is CropResult.Success -> {
                    uploadImage(state.id, result.bitmap, fileName)
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

    private fun loadImage(clientId: Int) {
        viewModelScope.launch {
            clientDetailsRepo.getImage(clientId).collect { result ->
                when (result) {
                    is DataState.Success -> mutableStateFlow.update {
                        val newDialogState =
                            if (state.dialogState is ClientProfileEditState.DialogState.ShowStatusDialog) {
                                state.dialogState
                            } else {
                                null
                            }
                        state.copy(
                            profileImage = imageToByteArray(result.data),
                            dialogState = newDialogState,
                        )
                    }

                    is DataState.Loading -> mutableStateFlow.update {
                        if (it.dialogState !is ClientProfileEditState.DialogState.ShowStatusDialog) {
                            it.copy(dialogState = ClientProfileEditState.DialogState.Loading)
                        } else {
                            it
                        }
                    }

                    is DataState.Error -> mutableStateFlow.update {
                        if (it.dialogState is ClientProfileEditState.DialogState.ShowStatusDialog) {
                            it
                        } else {
                            it.copy(dialogState = null)
                        }
                    }
                }
            }
        }
    }

    private fun uploadImage(id: Int, imageFile: ImageBitmap, fileName: String) =
        viewModelScope.launch {
            uploadClientImageUseCase(
                id,
                multipartRequestBody(
                    file = imageFile.encodeToByteArray(ImageFormat.PNG),
                    name = fileName,
                    extension = ImageFormat.PNG.name,
                ),
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
                        clientDetailsRepo.triggerClientUpdate()
                        loadImage(route.id)
                        mutableStateFlow.update {
                            it.copy(
                                openImagePicker = false,
                                dialogState = ClientProfileEditState.DialogState.ShowStatusDialog(
                                    status = ResultStatus.SUCCESS,
                                    msg = getString(Res.string.client_profile_photo_updated_success),
                                ),
                            )
                        }
                    }
                }
            }
        }

    fun deleteClientImage() {
        viewModelScope.launch {
            try {
                clientDetailsRepo.deleteClientImage(state.id)
                clientDetailsRepo.triggerClientUpdate()
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
                            e.message ?: getString(Res.string.client_profile_photo_updated_failure),
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
    val cropState: ImageCropper = imageCropper(),
    val openImagePicker: Boolean = false,
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data object ShowDeleteDialog : DialogState
        data object ShowUploadOptions : DialogState
        data object ShowImageCrop : DialogState
        data class ShowStatusDialog(val status: ResultStatus, val msg: String = "") : DialogState
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
    data object OnNext : ClientProfileEditAction
    object OnDismissDialog : ClientProfileEditAction
    object OpenImagePicker : ClientProfileEditAction
    data class OpenCamera(val file: PlatformFile?) : ClientProfileEditAction
}
