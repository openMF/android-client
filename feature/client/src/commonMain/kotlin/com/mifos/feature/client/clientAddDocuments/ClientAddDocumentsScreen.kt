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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosFilePickerBottomSheet
import com.mifos.core.ui.util.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientAddDocumentsScreen(
    navController: NavController,
    navigateBack: () -> Unit,
    viewModel: ClientAddDocumentViewModel = koinViewModel()
) {

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow){ events ->
        when(events){
            ClientAddDocumentEvents.OnNavigateBack -> navigateBack
        }
    }


    ClientAddDocumentsScreenDialog(
        state,
        onAction = remember(viewModel){{viewModel.trySendAction(it)}}
    )

    if(state.showDocumentPreviewScreen){
        DocumentPreviewScreen(
            canUpdateDocument = state.isDocumentUpdatingEnabled,
            onBack = {
                viewModel.trySendAction(ClientAddDocumentAction.CloseDocumentPreviewScreen)
            },
            onSubmit = {
                viewModel.trySendAction(
                    ClientAddDocumentAction.SubmitFromDocumentPreviewScreen(it)
                )
            },
            onUploadFromGallery = {
                viewModel.trySendAction(ClientAddDocumentAction.PickFromGallery)
            },
            onUploadFromFiles = {
                viewModel.trySendAction(ClientAddDocumentAction.PickFromFiles)
            },
            onClickMoreOptions = {
                viewModel.trySendAction(ClientAddDocumentAction.UseMoreOptions)
            },
        )
    } else {
        ClientAddDocumentScaffold(
            navController,
            state,
            onAction = remember(viewModel){{viewModel.trySendAction(it)}}
        )
    }
}

@Composable
private fun ClientAddDocumentsScreenDialog(
    state: ClientAddDocumentState,
    modifier: Modifier = Modifier,
    onAction: (ClientAddDocumentAction) -> Unit,
) {

    when (state.dialogState) {
        is ClientAddDocumentState.DialogState.Error -> {
            MifosErrorComponent(
                modifier = modifier,
                isNetworkConnected = state.isNetworkAvailable,
                message = state.dialogState.message,
                isRetryEnabled = false,
                onRetry = {},
            )
        }
        ClientAddDocumentState.DialogState.Loading -> {
            MifosCircularProgress()
        }
        null -> {}
        is ClientAddDocumentState.DialogState.UpdateError -> {
            MifosErrorComponent(
                modifier = modifier,
                isNetworkConnected = state.isNetworkAvailable,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = {
                    onAction(ClientAddDocumentAction.RetryUpdate)
                },
            )
        }
        is ClientAddDocumentState.DialogState.UploadError -> {
            MifosErrorComponent(
                modifier = modifier,
                isNetworkConnected = state.isNetworkAvailable,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = {
                    onAction(ClientAddDocumentAction.RetryUpload)
                },
            )
        }
    }

}

@Composable
private fun ClientAddDocumentScaffold(
    navController: NavController,
    state: ClientAddDocumentState,
    modifier: Modifier = Modifier,
    onAction: (ClientAddDocumentAction) -> Unit,
) {
    MifosScaffold(
        modifier = modifier,
        onBackPressed = {
            onAction(ClientAddDocumentAction.NavigateBack)
        },
        bottomBar = {
            MifosFilePickerBottomSheet(
                state.showFilePickerBottomSheet,
                onDismiss = {
                    onAction(ClientAddDocumentAction.DismissBottomSheet)
                },
                onGalleryClick = {
                    onAction(ClientAddDocumentAction.PickFromFiles)
                },
                onFilesClick = {
                    onAction(ClientAddDocumentAction.PickFromFiles)
                },
                onMoreClick ={
                    onAction(ClientAddDocumentAction.UseMoreOptions)
                },
            )
        },
        title = "",
    ) { paddingValues ->

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
                    "Add Document",
                    style = MifosTypography.titleMedium,
                )

                Spacer(Modifier.height(DesignToken.spacing.largeIncreased))

                MifosOutlinedTextField(
                    value = state.enteredFileName,
                    placeholder = "Enter document name",
                    onValueChange = {
                        onAction(ClientAddDocumentAction.UpdateName(it))
                    },
                    label = "Document Name",
                    maxLines = 1,
                    shape = DesignToken.shapes.medium,
                )

                MifosOutlinedTextField(
                    value = state.enteredDocumentDescription,
                    placeholder = "Enter description",
                    onValueChange = {
                        onAction(ClientAddDocumentAction.UpdateDescription(it))
                    },
                    label = "Description",
                    maxLines = 1,
                    shape = DesignToken.shapes.medium,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )

                AddViewFileAndFileNameRow(
                    state = state,
                    onAction = onAction
                )

                Spacer(Modifier.height(DesignToken.spacing.largeIncreased))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    MifosOutlinedButton(
                        onClick = {
                            onAction(ClientAddDocumentAction.NavigateBack)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(
                            1.dp,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        shape = DesignToken.shapes.medium,
                        modifier = Modifier
                            .height(40.dp)
                            .weight(1f)
                    ){
                        Icon(
                            imageVector = MifosIcons.ArrowBack,
                            "back button",
                            modifier = Modifier.size(DesignToken.sizes.iconSmall),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.width(DesignToken.spacing.small))
                        Text(
                            "Back",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }

                    Spacer(Modifier.width(DesignToken.spacing.small))

                    MifosOutlinedButton(
                        onClick = {
                            onAction(ClientAddDocumentAction.AddNewDocument)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = BorderStroke(
                            1.dp,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        shape = DesignToken.shapes.medium,
                        modifier = Modifier
                            .height(40.dp)
                            .weight(1f)
                    ){
                        Icon(
                            imageVector = MifosIcons.RightTick,
                            "back button",
                            modifier = Modifier.size(DesignToken.sizes.iconMiny),
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                        Spacer(Modifier.width(DesignToken.spacing.small))
                        Text(
                            "Submit",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                }
            }
        }
    }
}


@Composable
private fun AddViewFileAndFileNameRow(
    state: ClientAddDocumentState,
    onAction: (ClientAddDocumentAction) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = DesignToken.shapes.medium)
            .border(
                1.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = DesignToken.shapes.medium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = if (!state.isDocumentAdded) {
                "No File Selected"
            } else {
                state.pickedDocumentName
            },
            style = MaterialTheme . typography . labelLarge,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(
                start = 16.dp,
                top = 18.dp,
                bottom = 18.dp
            )
                .weight(.6f),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        MifosOutlinedButton(
            onClick = {
                if(state.isDocumentAdded){
                    onAction(ClientAddDocumentAction.AddNewDocument)
                } else{
                    onAction(ClientAddDocumentAction.PreviewUploadedDocument)
                }
            },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = DesignToken.shapes.small,
            border = BorderStroke(
                1.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier
                .padding(
                    end = 16.dp
                )
                .height(36.dp)
                .width(72.dp)
        ){
            Text(
                text = if (!state.isDocumentAdded) {
                    "Add"
                } else {
                    "View"
                },
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
