/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientEditProfile

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.account_number_prefix
import androidclient.feature.client.generated.resources.arrow_up
import androidclient.feature.client.generated.resources.camera
import androidclient.feature.client.generated.resources.cancel
import androidclient.feature.client.generated.resources.client_profile_edit_failure_title
import androidclient.feature.client.generated.resources.client_profile_edit_success_title
import androidclient.feature.client.generated.resources.client_signature_more
import androidclient.feature.client.generated.resources.delete_dialog_message
import androidclient.feature.client.generated.resources.delete_dialog_title
import androidclient.feature.client.generated.resources.delete_photo
import androidclient.feature.client.generated.resources.dialog_continue
import androidclient.feature.client.generated.resources.gallery
import androidclient.feature.client.generated.resources.remove
import androidclient.feature.client.generated.resources.update_profile_photo_message
import androidclient.feature.client.generated.resources.upload_new_photo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.attafitamim.krop.core.crop.AspectRatio
import com.mifos.core.designsystem.component.BasicDialogState
import com.mifos.core.designsystem.component.MifosBasicDialog
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosBottomSheetOptionItem
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosTextButton
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosImageCropperDialog
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.components.MifosUserImage
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.utils.rememberPlatformCameraLauncher
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

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

    ClientProfileEditContent(
        modifier = modifier,
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        navController = navController,
    )

    ClientProfileEditDialogs(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientProfileEditContent(
    state: ClientProfileEditState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onAction: (ClientProfileEditAction) -> Unit,
) {
    if (state.dialogState != ClientProfileEditState.DialogState.Loading) {
        Column(modifier.fillMaxSize()) {
            MifosBreadcrumbNavBar(navController)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = KptTheme.spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(DesignToken.padding.largeIncreased))

                Text(
                    text = state.name,
                    style = MifosTypography.titleMediumEmphasized,
                )
                Spacer(Modifier.height(DesignToken.padding.extraExtraSmall))
                Text(
                    text = stringResource(Res.string.account_number_prefix, state.accountNo),
                    style = MifosTypography.bodySmall,
                    color = KptTheme.colorScheme.secondary,
                )
                Spacer(Modifier.height(DesignToken.padding.largeIncreased))
                MifosUserImage(
                    bitmap = state.profileImage,
                    modifier = Modifier.size(DesignToken.sizes.avatarLargeLarge),
                    hasBorder = true,
                )
                if (state.profileImage == null) {
                    Spacer(Modifier.height(KptTheme.spacing.md))
                    Text(
                        text = stringResource(Res.string.update_profile_photo_message),
                        style = MifosTypography.bodySmall,
                        color = KptTheme.colorScheme.secondary,
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
                Spacer(Modifier.height(KptTheme.spacing.md))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientProfileEditDialogs(
    state: ClientProfileEditState,
    onAction: (ClientProfileEditAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientProfileEditState.DialogState.Loading -> MifosProgressIndicator()

        is ClientProfileEditState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = {
                    onAction(ClientProfileEditAction.OnRetry)
                },
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
                        tint = KptTheme.colorScheme.primary,
                        modifier = Modifier.size(DesignToken.sizes.iconMedium),
                    )
                },
            )
        }

        null -> Unit

        ClientProfileEditState.DialogState.ShowUploadOptions -> {
            ShowUploadOption(
                onAction = onAction,
            )
        }

        is ClientProfileEditState.DialogState.ShowStatusDialog -> {
            LaunchedEffect(state.dialogState.status) {
                if (state.dialogState.status == ResultStatus.SUCCESS) {
                    delay(1500)
                    onAction(ClientProfileEditAction.OnNext)
                }
            }
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                ),
            ) {
                Surface(
                    shape = KptTheme.shapes.extraLarge,
                    color = KptTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(KptTheme.spacing.md),
                ) {
                    MifosStatusDialog(
                        status = state.dialogState.status,
                        onConfirm = {
                            onAction(ClientProfileEditAction.OnNext)
                        },
                        btnText = stringResource(Res.string.dialog_continue),
                        successTitle = stringResource(Res.string.client_profile_edit_success_title),
                        successMessage = state.dialogState.msg,
                        failureTitle = stringResource(Res.string.client_profile_edit_failure_title),
                        failureMessage = state.dialogState.msg,
                        showButton = state.dialogState.status == ResultStatus.FAILURE,
                    )
                }
            }
        }

        ClientProfileEditState.DialogState.ShowImageCrop -> {
            state.cropState.cropState?.let {
                MifosImageCropperDialog(
                    state = it,
                    AspectRatio(1, 1),
                )
            }
        }
    }
}

@Composable
private fun ShowUploadOption(
    onAction: (ClientProfileEditAction) -> Unit,
) {
    val cameraLauncher = rememberPlatformCameraLauncher(
        onImageCapturedPath = { file ->
            onAction(ClientProfileEditAction.OpenCamera(file))
        },
    )

    MifosBottomSheet(
        onDismiss = {
            onAction(ClientProfileEditAction.OnDismissDialog)
        },
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = KptTheme.spacing.md,
                    end = KptTheme.spacing.md,
                    bottom = KptTheme.spacing.md,
                ),
        ) {
            MifosBottomSheetOptionItem(
                label = stringResource(Res.string.gallery),
                icon = MifosIcons.Gallery,
                onClick = {
                    onAction(ClientProfileEditAction.OpenImagePicker)
                },
            )
            MifosBottomSheetOptionItem(
                label = stringResource(Res.string.camera),
                icon = MifosIcons.Camera,
                onClick = {
                    cameraLauncher.launch()
                },
            )
            MifosBottomSheetOptionItem(
                label = stringResource(Res.string.client_signature_more),
                icon = MifosIcons.MoreHoriz,
                onClick = {
                    // it implement further
                },
            )
        }
    }
}
