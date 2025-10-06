/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.client.clientIdentifiersAddUpdate

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.add_document_title
import androidclient.feature.client.generated.resources.client_identifier_btn_add
import androidclient.feature.client.generated.resources.client_identifier_btn_back
import androidclient.feature.client.generated.resources.client_identifier_btn_create_new
import androidclient.feature.client.generated.resources.client_identifier_btn_next
import androidclient.feature.client.generated.resources.client_identifier_btn_submit
import androidclient.feature.client.generated.resources.client_identifier_btn_update
import androidclient.feature.client.generated.resources.client_identifier_btn_upload_new
import androidclient.feature.client.generated.resources.client_identifier_btn_view
import androidclient.feature.client.generated.resources.client_identifier_description
import androidclient.feature.client.generated.resources.client_identifier_document_key
import androidclient.feature.client.generated.resources.client_identifier_document_name
import androidclient.feature.client.generated.resources.client_identifier_document_type
import androidclient.feature.client.generated.resources.client_identifier_no_file_selected
import androidclient.feature.client.generated.resources.client_identifier_status
import androidclient.feature.client.generated.resources.client_identifier_title
import androidclient.feature.client.generated.resources.client_identifiers_error_text
import androidclient.feature.client.generated.resources.client_update_document_title
import androidclient.feature.client.generated.resources.feature_client_cancel
import androidclient.feature.client.generated.resources.feature_client_dialog_action_ok
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.mifos.core.designsystem.component.MifosCard
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosFilePickerBottomSheet
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.utils.PdfPreview
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientIdentifiersAddUpdateScreen(
    onBackPressed: () -> Unit,
    onUpdatedListBack: (Int) -> Unit,
    navController: NavController,
    viewModel: ClientIdentifiersAddUpdateViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientIdentifiersAddUpdateEvent.NavigateBack -> onBackPressed()
            ClientIdentifiersAddUpdateEvent.NavigateBackWithUpdatedList -> onUpdatedListBack(state.clientId)
        }
    }

    ClientIdentifiersAddUpdateScaffold(
        state = state,
        navController = navController,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    ClientIdentifiersAddUpdateDialog(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
private fun ClientIdentifiersAddUpdateDialog(
    state: ClientIdentifiersAddUpdateState,
    onAction: (ClientIdentifiersAddUpdateAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientIdentifiersAddUpdateState.DialogState.Error -> {
            if (state.documentNotFount || state.handleServerResponse) {
                MifosAlertDialog(
                    dialogTitle = when {
                        state.documentNotFount -> stringResource(Res.string.client_identifiers_error_text)
                        else -> null
                    },
                    dialogText = state.dialogState.message,
                    confirmationText = when {
                        state.documentNotFount -> stringResource(Res.string.client_identifier_btn_create_new)
                        else -> stringResource(Res.string.feature_client_dialog_action_ok)
                    },
                    dismissText = when {
                        state.documentNotFount -> stringResource(Res.string.feature_client_cancel)
                        else -> null
                    },
                    onDismissRequest = {
                        if (state.documentNotFount) {
                            onAction(ClientIdentifiersAddUpdateAction.NavigateBack)
                        }
                    },
                    onConfirmation = {
                        if (state.documentNotFount) {
                            onAction(ClientIdentifiersAddUpdateAction.OnNotFoundDocument)
                        } else {
                            onAction(ClientIdentifiersAddUpdateAction.CloseDialog)
                        }
                    },
                )
            } else {
                MifosErrorComponent(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    message = state.dialogState.message,
                    isRetryEnabled = true,
                    onRetry = {
                        onAction(ClientIdentifiersAddUpdateAction.OnRetry)
                    },
                )
            }
        }

        ClientIdentifiersAddUpdateState.DialogState.Loading -> {
            if (state.isOverlayLoading) {
                MifosProgressIndicatorOverlay()
            } else {
                MifosProgressIndicator()
            }
        }

        ClientIdentifiersAddUpdateState.DialogState.ShowBottomSheet -> {
            MifosFilePickerBottomSheet(
                onDismiss = {
                    onAction(ClientIdentifiersAddUpdateAction.CloseDialog)
                },
                onGalleryClick = {
                    onAction(ClientIdentifiersAddUpdateAction.OnSelectImage)
                },
                onFilesClick = {
                    onAction(ClientIdentifiersAddUpdateAction.OnSelectFile)
                },
                onMoreClick = {
                    // implement further
                },
            )
        }

        null -> Unit
    }
}

@Composable
internal fun ClientIdentifiersAddUpdateScaffold(
    state: ClientIdentifiersAddUpdateState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onAction: (ClientIdentifiersAddUpdateAction) -> Unit,
) {
    MifosScaffold(
        title = "",
        onBackPressed = {
            onAction(ClientIdentifiersAddUpdateAction.NavigateBack)
        },
        modifier = modifier.background(MaterialTheme.colorScheme.onPrimary),
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
        ) {
            if (state.feature != Feature.VIEW_DOCUMENT) {
                MifosBreadcrumbNavBar(navController)
            }

            Column(
                modifier = modifier.fillMaxSize().padding(
                    horizontal = DesignToken.padding.large,
                ),
            ) {
                if (state.feature != Feature.VIEW_DOCUMENT) {
                    Text(
                        text = when {
                            state.feature == Feature.VIEW_DOCUMENT -> stringResource(Res.string.client_identifier_title)

                            state.documentKey == null -> stringResource(Res.string.client_update_document_title)

                            else -> stringResource(Res.string.add_document_title)
                        },
                        style = MifosTypography.titleMedium,
                    )
                }

                Spacer(Modifier.height(DesignToken.spacing.large))

                when (state.feature) {
                    Feature.ADD_IDENTIFIER -> {
                        ClientIdentifiersAddIdentifier(
                            state = state,
                            onAction = onAction,
                        )
                    }

                    Feature.ADD_UPDATE_DOCUMENT -> {
                        ClientIdentifiersAddUpdateDocument(
                            state = state,
                            onAction = onAction,
                        )
                    }

                    Feature.VIEW_DOCUMENT -> {
                        ClientIdentifiersDocumentPreview(
                            state = state,
                            onAction = onAction,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientIdentifiersAddIdentifier(
    state: ClientIdentifiersAddUpdateState,
    onAction: (ClientIdentifiersAddUpdateAction) -> Unit,
) {
    MifosTextFieldDropdown(
        value = state.documentType ?: "",
        onValueChanged = {},
        onOptionSelected = { index, value ->
            onAction(ClientIdentifiersAddUpdateAction.OnDocumentTypeChange(index))
        },
        options = state.identifierTemplate?.map {
            it.name ?: ""
        } ?: emptyList(),
        label = stringResource(Res.string.client_identifier_document_type),
    )

    MifosTextFieldDropdown(
        value = state.status ?: "",
        onValueChanged = {},
        onOptionSelected = { index, value ->
            onAction(ClientIdentifiersAddUpdateAction.OnStatusChange(index))
        },
        options = state.statusList.map {
            it
        },
        label = stringResource(Res.string.client_identifier_status),
    )

    MifosOutlinedTextField(
        value = state.documentKey ?: "",
        onValueChange = {
            onAction(ClientIdentifiersAddUpdateAction.OnDocumentKeyChange(it))
        },
        label = stringResource(Res.string.client_identifier_document_key),
    )

    Spacer(Modifier.height(DesignToken.padding.large))

    MifosOutlinedTextField(
        value = state.description ?: "",
        onValueChange = {
            onAction(ClientIdentifiersAddUpdateAction.OnDescriptionChange(it))
        },
        label = stringResource(Res.string.client_identifier_description),
    )

    Spacer(Modifier.height(DesignToken.spacing.largeIncreased))

    MifosTwoButtonRow(
        firstBtnText = stringResource(Res.string.client_identifier_btn_back),
        secondBtnText = stringResource(Res.string.client_identifier_btn_next),
        onFirstBtnClick = {
            onAction(ClientIdentifiersAddUpdateAction.NavigateBack)
        },
        onSecondBtnClick = {
            onAction(ClientIdentifiersAddUpdateAction.OnCreateClientIdentifier)
        },
        isSecondButtonEnabled = !state.documentKey.isNullOrEmpty() && !state.documentType.isNullOrEmpty() && !state.status.isNullOrEmpty(),
    )
}

@Composable
private fun ClientIdentifiersAddUpdateDocument(
    state: ClientIdentifiersAddUpdateState,
    onAction: (ClientIdentifiersAddUpdateAction) -> Unit,
) {
    MifosOutlinedTextField(
        value = state.documentName ?: "",
        onValueChange = {
            onAction(ClientIdentifiersAddUpdateAction.OnDocumentNameChange(it))
        },
        label = stringResource(Res.string.client_identifier_document_name),
    )

    Spacer(Modifier.height(DesignToken.padding.large))

    MifosRowWithTextAndButton(
        text = state.imageFileName ?: stringResource(Res.string.client_identifier_no_file_selected),
        onBtnClick = {
            if (state.imageFileName == null) {
                onAction(ClientIdentifiersAddUpdateAction.OnShowBottomSheet)
            } else {
                onAction(ClientIdentifiersAddUpdateAction.OnOpenPreview)
            }
        },
        btnText = if (state.imageFileName == null) {
            stringResource(Res.string.client_identifier_btn_add)
        } else {
            stringResource(
                Res.string.client_identifier_btn_view,
            )
        },
    )

    Spacer(Modifier.height(DesignToken.spacing.largeIncreased))

    MifosTwoButtonRow(
        firstBtnText = stringResource(Res.string.client_identifier_btn_back),
        secondBtnText = when {
            state.documentKey == null -> stringResource(Res.string.client_identifier_btn_update)
            else -> stringResource(Res.string.client_identifier_btn_submit)
        },
        onFirstBtnClick = {
            onAction(ClientIdentifiersAddUpdateAction.NavigateBack)
        },
        onSecondBtnClick = {
            onAction(ClientIdentifiersAddUpdateAction.OnCreateDocument)
        },
        isSecondButtonEnabled = !state.imageFileName.isNullOrEmpty() && !state.documentName.isNullOrEmpty(),
    )
}

@Composable
private fun ClientIdentifiersDocumentPreview(
    state: ClientIdentifiersAddUpdateState,
    modifier: Modifier = Modifier,
    onAction: (ClientIdentifiersAddUpdateAction) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MifosCard(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
            elevation = 0.dp,
            borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Box(
                modifier = Modifier.aspectRatio(0.707f, true),
                contentAlignment = Alignment.Center,
            ) {
                if (state.fileExtension == "pdf") {
                    PdfPreview(state.documentImageFile!!, Modifier.matchParentSize())
                } else {
                    AsyncImage(
                        model = state.documentImageFile,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().align(Alignment.Center),
                    )
                }
            }
        }

        Spacer(Modifier.height(DesignToken.spacing.largeIncreased))

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.client_identifier_btn_back),
            secondBtnText = if (state.previewButtonHandle == PreviewButtonHandle.UploadNew) {
                stringResource(
                    Res.string.client_identifier_btn_upload_new,
                )
            } else {
                stringResource(
                    Res.string.client_identifier_btn_submit,
                )
            },
            onFirstBtnClick = {
                if (state.previewButtonHandle == PreviewButtonHandle.Hide) {
                    onAction(ClientIdentifiersAddUpdateAction.NavigateBack)
                } else {
                    onAction(ClientIdentifiersAddUpdateAction.OnClosePreview)
                }
            },
            onSecondBtnClick = {
                if (state.previewButtonHandle == PreviewButtonHandle.UploadNew) {
                    onAction(ClientIdentifiersAddUpdateAction.OnShowBottomSheet)
                } else {
                    onAction(ClientIdentifiersAddUpdateAction.OnClosePreview)
                }
            },
            isSecondButtonEnabled = state.previewButtonHandle != PreviewButtonHandle.Hide,
            isButtonIconVisible = false,
        )
    }
}
