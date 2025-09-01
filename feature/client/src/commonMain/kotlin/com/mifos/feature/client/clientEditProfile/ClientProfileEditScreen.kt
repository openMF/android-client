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
import androidclient.feature.client.generated.resources.account_number_prefix
import androidclient.feature.client.generated.resources.arrow_up
import androidclient.feature.client.generated.resources.cancel
import androidclient.feature.client.generated.resources.choose_from_option
import androidclient.feature.client.generated.resources.delete_dialog_message
import androidclient.feature.client.generated.resources.delete_dialog_title
import androidclient.feature.client.generated.resources.delete_photo
import androidclient.feature.client.generated.resources.edit_profile_title
import androidclient.feature.client.generated.resources.from_camera
import androidclient.feature.client.generated.resources.from_gallery
import androidclient.feature.client.generated.resources.remove
import androidclient.feature.client.generated.resources.update_profile_photo_message
import androidclient.feature.client.generated.resources.upload_new_photo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.BasicDialogState
import com.mifos.core.designsystem.component.MifosBasicDialog
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextButton
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosUserImage
import com.mifos.core.ui.util.EventsEffect
import network.chaintech.cmpimagepickncrop.CMPImagePickNCropDialog
import network.chaintech.cmpimagepickncrop.imagecropper.ImageAspectRatio
import network.chaintech.cmpimagepickncrop.imagecropper.rememberImageCropper
import network.chaintech.cmpimagepickncrop.utils.ImagePickerDialogStyle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientProfileEditScreen(
    onNavigateBack: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ClientProfileEditViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientProfileEditEvent.NavigateBack -> onNavigateBack()
            ClientProfileEditEvent.OnSaveSuccess -> {
                onNavigateBack()
            }
        }
    }

    ClientProfileEditScaffold(
        modifier = modifier,
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        navController = navController,
    )

    ClientProfileEditDialogs(
        state = state,
        onRetry = remember(viewModel) {
            { viewModel.trySendAction(ClientProfileEditAction.OnRetry) }
        },
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientProfileEditScaffold(
    state: ClientProfileEditState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onAction: (ClientProfileEditAction) -> Unit,
) {
    MifosScaffold(
        title = stringResource(Res.string.edit_profile_title),
        onBackPressed = { onAction(ClientProfileEditAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        if (state.dialogState != ClientProfileEditState.DialogState.Loading) {
            Column(Modifier.fillMaxSize().padding(paddingValues)) {
                MifosBreadcrumbNavBar(navController)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            horizontal = DesignToken.padding.large,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = state.name,
                        style = MifosTypography.titleMediumEmphasized,
                    )
                    Spacer(Modifier.height(DesignToken.padding.extraExtraSmall))
                    Text(
                        text = stringResource(Res.string.account_number_prefix, state.accountNo),
                        style = MifosTypography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))
                    MifosUserImage(
                        bitmap = state.profileImage,
                        modifier = Modifier.size(DesignToken.sizes.avatarLargeLarge),
                        hasBorder = true,
                    )
                    if (state.profileImage == null) {
                        Spacer(Modifier.height(DesignToken.padding.large))
                        Text(
                            text = stringResource(Res.string.update_profile_photo_message),
                            style = MifosTypography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(Modifier.height(DesignToken.padding.extraExtraLarge))
                    MifosOutlinedButton(
                        text = { Text(stringResource(Res.string.delete_photo)) },
                        onClick = {
                            onAction(ClientProfileEditAction.OnDeleteImage)
                        },
                        enabled = state.profileImage != null,
                        leadingIcon = {
                            Icon(
                                imageVector = MifosIcons.DeleteDocument,
                                contentDescription = null,
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(DesignToken.padding.large))
                    MifosTextButton(
                        text = { Text(stringResource(Res.string.upload_new_photo)) },
                        onClick = {
                            onAction(ClientProfileEditAction.OnUploadNewPhotoClick)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_up),
                                contentDescription = null,
                                modifier = Modifier.size(DesignToken.sizes.iconAverage),
                            )
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientProfileEditDialogs(
    state: ClientProfileEditState,
    onRetry: () -> Unit,
    onAction: (ClientProfileEditAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientProfileEditState.DialogState.Loading -> MifosProgressIndicator()

        is ClientProfileEditState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = onRetry,
            )
        }

        ClientProfileEditState.DialogState.ShowDeleteDialog -> {
            MifosBasicDialog(
                visibilityState = BasicDialogState.Shown(
                    title = stringResource(Res.string.delete_dialog_title),
                    message = stringResource(Res.string.delete_dialog_message),
                ),
                onDismissRequest = {
                    onAction(ClientProfileEditAction.CancelDeleteClick)
                },
                onConfirm = {
                    onAction(ClientProfileEditAction.OnConfirmDeleteClick)
                },
                confirmText = stringResource(Res.string.remove),
                dismissText = stringResource(Res.string.cancel),
                icon = {
                    Icon(
                        imageVector = MifosIcons.DeleteDocument,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(DesignToken.sizes.iconMedium),
                    )
                },
            )
        }

        null -> Unit

        ClientProfileEditState.DialogState.ShowUploadOptions -> {
            val imageCropper = rememberImageCropper()
            val scope = rememberCoroutineScope()
            CMPImagePickNCropDialog(
                imageCropper = imageCropper,
                openImagePicker = state.openImagePicker,
                defaultAspectRatio = ImageAspectRatio(16, 9),
                imagePickerDialogStyle = ImagePickerDialogStyle(
                    title = stringResource(Res.string.choose_from_option),
                    txtCamera = stringResource(Res.string.from_camera),
                    txtGallery = stringResource(Res.string.from_gallery),
                    txtCameraColor = MaterialTheme.colorScheme.primary,
                    txtGalleryColor = MaterialTheme.colorScheme.primary,
                    cameraIconTint = MaterialTheme.colorScheme.primary,
                    galleryIconTint = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.background,
                ),
                autoZoom = true,
                imagePickerDialogHandler = {
                    onAction(ClientProfileEditAction.UpdateImagePicker(it))
                },
                selectedImageCallback = {
                    onAction(ClientProfileEditAction.OnImageSelected(it))
                },
                selectedImageFileCallback = {},
            )
        }
    }
}
