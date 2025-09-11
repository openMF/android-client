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

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.account_number_prefix
import androidclient.feature.client.generated.resources.arrow_up
import androidclient.feature.client.generated.resources.cancel
import androidclient.feature.client.generated.resources.client_signature_delete
import androidclient.feature.client.generated.resources.client_signature_delete_warning_message
import androidclient.feature.client.generated.resources.client_signature_draw
import androidclient.feature.client.generated.resources.client_signature_gallery
import androidclient.feature.client.generated.resources.client_signature_more
import androidclient.feature.client.generated.resources.client_signature_not_found
import androidclient.feature.client.generated.resources.client_signature_upload
import androidclient.feature.client.generated.resources.client_signature_upload_message
import androidclient.feature.client.generated.resources.delete_dialog_title
import androidclient.feature.client.generated.resources.remove
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.BasicDialogState
import com.mifos.core.designsystem.component.MifosBasicDialog
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosBottomSheetOptionItem
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextButton
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosImageCropperDialog
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosSignatureDrawDialog
import com.mifos.core.ui.components.MifosUserSignatureImage
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientSignatureScreen(
    onNavigateBack: () -> Unit,
    navController: NavController,
    viewModel: ClientSignatureViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientSignatureEvent.OnNavigationBack -> onNavigateBack()
        }
    }

    ClientSignatureScaffold(
        navController = navController,
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    ClientSignatureDialog(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
fun ClientSignatureDialog(
    state: ClientSignatureState,
    onAction: (ClientSignatureAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientSignatureState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = {
                    onAction(ClientSignatureAction.OnRetry)
                },
            )
        }

        ClientSignatureState.DialogState.Loading -> MifosProgressIndicator()

        ClientSignatureState.DialogState.ShowDeleteDialog -> {
            MifosBasicDialog(
                visibilityState = BasicDialogState.Shown(
                    title = stringResource(Res.string.delete_dialog_title),
                    message = stringResource(Res.string.client_signature_delete_warning_message),
                ),
                onDismissRequest = {
                    onAction(ClientSignatureAction.OnDismissDialog)
                },
                onConfirm = {
                    onAction(ClientSignatureAction.DeleteSignature)
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

        ClientSignatureState.DialogState.ShowUploadOptions -> {
            ShowUploadOption(
                onAction = onAction,
            )
        }

        ClientSignatureState.DialogState.ShowSignatureDraw -> {
            MifosSignatureDrawDialog(
                onSaveFile = {
                    onAction(ClientSignatureAction.CropImage(it))
                },
                onDismiss = {
                    onAction(ClientSignatureAction.OnDismissDialog)
                },
            )
        }

        is ClientSignatureState.DialogState.ShowImageCrop -> {
            state.cropState.cropState?.let {
                MifosImageCropperDialog(
                    state = it,
                )
            }
        }

        null -> Unit
    }
}

@Composable
internal fun ClientSignatureScaffold(
    navController: NavController,
    state: ClientSignatureState,
    onAction: (ClientSignatureAction) -> Unit,
) {
    MifosScaffold(
        title = "",
        onBackPressed = {
            onAction(ClientSignatureAction.NavigateBack)
        },
    ) { paddingValues ->

        if (state.dialogState !is ClientSignatureState.DialogState.Loading &&
            state.dialogState !is ClientSignatureState.DialogState.Error
        ) {
            Column(Modifier.fillMaxSize().padding(paddingValues)) {
                MifosBreadcrumbNavBar(navController)
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(
                        horizontal = DesignToken.padding.large,
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = state.clientName,
                        style = MifosTypography.titleMediumEmphasized,
                    )
                    Spacer(Modifier.height(DesignToken.padding.extraExtraSmall))
                    Text(
                        text = stringResource(Res.string.account_number_prefix, state.accountNo),
                        style = MifosTypography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))

                    MifosUserSignatureImage(
                        bitmap = state.clientSignatureImage,
                        emptyMessage = stringResource(Res.string.client_signature_not_found),
                    )
                    if (state.signatureId == null) {
                        Spacer(Modifier.height(DesignToken.padding.large))
                        Text(
                            text = stringResource(Res.string.client_signature_upload_message),
                            style = MifosTypography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(Modifier.height(DesignToken.padding.extraExtraLarge))
                    MifosOutlinedButton(
                        text = {
                            Text(
                                text = stringResource(Res.string.client_signature_delete),
                                style = MifosTypography.labelLarge,
                            )
                        },
                        onClick = {
                            onAction(ClientSignatureAction.ShowDeleteDialog)
                        },
                        enabled = state.signatureId != null,
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
                        text = {
                            Text(
                                text = stringResource(Res.string.client_signature_upload),
                                style = MifosTypography.labelLarge,
                            )
                        },
                        onClick = {
                            onAction(ClientSignatureAction.ShowUploadOptionsBottomSheet)
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

@Composable
private fun ShowUploadOption(
    onAction: (ClientSignatureAction) -> Unit,
) {
    MifosBottomSheet(
        onDismiss = {
            onAction(ClientSignatureAction.OnDismissDialog)
        },
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = DesignToken.padding.large,
                    end = DesignToken.padding.large,
                    bottom = DesignToken.padding.large,
                ),
        ) {
            MifosBottomSheetOptionItem(
                label = stringResource(Res.string.client_signature_gallery),
                icon = MifosIcons.Gallery,
                onClick = {
                    onAction(ClientSignatureAction.OpenImagePicker)
                },
            )
            MifosBottomSheetOptionItem(
                label = stringResource(Res.string.client_signature_draw),
                icon = MifosIcons.Draw,
                onClick = {
                    onAction(ClientSignatureAction.OpenSignatureDraw)
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
