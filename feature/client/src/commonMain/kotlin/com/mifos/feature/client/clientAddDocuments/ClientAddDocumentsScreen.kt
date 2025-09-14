/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientAddDocuments

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.action_add
import androidclient.feature.client.generated.resources.action_go_back
import androidclient.feature.client.generated.resources.action_view
import androidclient.feature.client.generated.resources.add_document_title
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.btn_submit
import androidclient.feature.client.generated.resources.document_name
import androidclient.feature.client.generated.resources.feature_client_description
import androidclient.feature.client.generated.resources.hint_description
import androidclient.feature.client.generated.resources.hint_document_name
import androidclient.feature.client.generated.resources.no_file_selected
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosFilePickerBottomSheet
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.EntityDocumentState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientAddDocumentsScreen(
    navController: NavController,
    navigateBack: () -> Unit,
    navigateToDocumentPreviewScreen: () -> Unit,
    viewModel: ClientAddDocumentScreenViewmodel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { events ->
        when (events) {
            ClientAddDocumentScreenEvents.OnNavigateBack -> navigateBack()
            is ClientAddDocumentScreenEvents.OnNavigateToPreviewScreen -> navigateToDocumentPreviewScreen()
        }
    }

    ClientAddDocumentScaffold(
        navController,
        state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
private fun ClientAddDocumentsScreenDialog(
    state: ClientAddDocumentScreenState,
    onAction: (ClientAddDocumentScreenAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientAddDocumentScreenState.DialogState.Error -> {
            MifosSweetError(
                message = state.dialogState.message,
                isRetryEnabled = false,
                buttonText = stringResource(Res.string.action_go_back),
            ) {
                onAction(ClientAddDocumentScreenAction.NavigateBack)
            }
        }
        ClientAddDocumentScreenState.DialogState.Loading -> {
            MifosProgressIndicator()
        }

        else -> {}
    }
}

@Composable
private fun ClientAddDocumentScaffold(
    navController: NavController,
    state: ClientAddDocumentScreenState,
    modifier: Modifier = Modifier,
    onAction: (ClientAddDocumentScreenAction) -> Unit,
) {
    MifosScaffold(
        title = "",
        onBackPressed = {
            onAction(ClientAddDocumentScreenAction.NavigateBack)
        },
        modifier = modifier
            .background(MaterialTheme.colorScheme.onPrimary),
    ) { paddingValues ->
        if (state.dialogState != null) {
            ClientAddDocumentsScreenDialog(
                state,
                onAction = onAction,
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                MifosBreadcrumbNavBar(navController)
                Column(
                    Modifier.fillMaxSize()
                        .padding(
                            horizontal = DesignToken.padding.large,
                        ),
                ) {
                    Text(
                        stringResource(Res.string.add_document_title),
                        style = MifosTypography.titleMedium,
                    )

                    Spacer(Modifier.height(DesignToken.spacing.largeIncreased))

                    MifosOutlinedTextField(
                        value = state.enteredFileName,
                        placeholder = stringResource(Res.string.hint_document_name),
                        onValueChange = {
                            onAction(
                                ClientAddDocumentScreenAction.UpdateFileName(it),
                            )
                        },
                        label = stringResource(Res.string.document_name),
                        maxLines = 1,
                        shape = DesignToken.shapes.medium,
                    )

                    MifosOutlinedTextField(
                        value = state.enteredDocumentDescription,
                        placeholder = stringResource(Res.string.hint_description),
                        onValueChange = {
                            onAction(
                                ClientAddDocumentScreenAction.UpdateDescription(it),
                            )
                        },
                        label = stringResource(Res.string.feature_client_description),
                        maxLines = 1,
                        shape = DesignToken.shapes.medium,
                        modifier = Modifier
                            .padding(bottom = DesignToken.padding.small),
                    )

                    MifosRowWithTextAndButton(
                        text = if (state.step == EntityDocumentState.Step.ADD) {
                            stringResource(Res.string.no_file_selected)
                        } else {
                            state.pickedDocumentName
                        },
                        onBtnClick = {
                            if (state.step == EntityDocumentState.Step.ADD) {
                                onAction(ClientAddDocumentScreenAction.AddNewDocument)
                            } else {
                                onAction(ClientAddDocumentScreenAction.ViewDocument)
                            }
                        },
                        btnText = if (state.step == EntityDocumentState.Step.ADD) {
                            stringResource(Res.string.action_add)
                        } else {
                            stringResource(Res.string.action_view)
                        },

                    )

                    Spacer(Modifier.height(DesignToken.spacing.largeIncreased))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        MifosOutlinedButton(
                            onClick = {
                                onAction(ClientAddDocumentScreenAction.NavigateBack)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                            border = BorderStroke(
                                1.dp,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            shape = DesignToken.shapes.medium,
                            modifier = Modifier
                                .height(DesignToken.sizes.iconExtraLarge)
                                .weight(1f),
                        ) {
                            Icon(
                                imageVector = MifosIcons.ArrowBack,
                                "back button",
                                modifier = Modifier.size(DesignToken.sizes.iconMedium),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(Modifier.width(DesignToken.spacing.extraSmall))
                            Text(
                                stringResource(Res.string.btn_back),
                                style = MaterialTheme.typography.labelLarge,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }

                        Spacer(Modifier.width(DesignToken.spacing.small))

                        MifosOutlinedButton(
                            onClick = {
                                if (state.submitMode == EntityDocumentState.SubmitMode.UPLOAD) {
                                    onAction(ClientAddDocumentScreenAction.UploadDocument)
                                } else {
                                    onAction(ClientAddDocumentScreenAction.UpdateDocument)
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = .12f,
                                ),
                                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(
                                    .5f,
                                ),
                            ),
                            border = BorderStroke(
                                if (state.step == EntityDocumentState.Step.VIEW) {
                                    1.dp
                                } else {
                                    0.dp
                                },
                                color = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            shape = DesignToken.shapes.medium,
                            modifier = Modifier
                                .height(DesignToken.sizes.iconExtraLarge)
                                .weight(1f),
                            enabled = state.step == EntityDocumentState.Step.VIEW,
                        ) {
                            Icon(
                                imageVector = MifosIcons.RightTick,
                                contentDescription = "submit button",
                                modifier = Modifier.size(DesignToken.sizes.iconSmall),
                            )
                            Spacer(Modifier.width(DesignToken.spacing.small))
                            Text(
                                stringResource(Res.string.btn_submit),
                                style = MaterialTheme.typography.labelLarge,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }
                    }

                    if (state.showBottomSheet) {
                        MifosFilePickerBottomSheet(
                            onDismiss = {
                                onAction(ClientAddDocumentScreenAction.DismissBottomSheet)
                            },
                            onGalleryClick = {
                                onAction(ClientAddDocumentScreenAction.PickFromGallery)
                            },
                            onFilesClick = {
                                onAction(ClientAddDocumentScreenAction.PickFromFiles)
                            },
                            onMoreClick = {
                                onAction(ClientAddDocumentScreenAction.UseMoreOptions)
                            },
                        )
                    }
                }
            }
        }
    }
}
