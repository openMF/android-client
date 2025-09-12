/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.documentPreviewScreen

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.action_go_back
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.btn_submit
import androidclient.feature.client.generated.resources.btn_update_new
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
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
import coil3.compose.AsyncImage
import com.mifos.core.designsystem.component.MifosCard
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosFilePickerBottomSheet
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.EntityDocumentState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DocumentPreviewScreen(
    navigateBack: () -> Unit,
    documentRejected: () -> Unit,
    viewmodel: DocumentPreviewScreenViewModel = koinViewModel(),
) {
    val state by viewmodel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewmodel.eventFlow) { event ->
        when (event) {
            DocumentPreviewEvent.OnNavigateBack -> navigateBack()
            DocumentPreviewEvent.OnDocumentRejected -> documentRejected()
        }
    }

    ViewDocumentScaffold(
        state = state,
        onAction = remember(viewmodel) { { viewmodel.trySendAction(it) } },
    )
}

@Composable
private fun ViewDocumentScaffold(
    state: DocumentPreviewState,
    modifier: Modifier = Modifier,
    onAction: (DocumentPreviewScreenAction) -> Unit,
) {
    MifosScaffold(
        modifier = Modifier
            .fillMaxSize(),
        title = "",
        onBackPressed = {
            onAction(DocumentPreviewScreenAction.NavigateBack)
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(DesignToken.padding.large)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ViewDocumentsScreenContent(
                state = state,
                modifier = Modifier.weight(1f),
                onAction = onAction,
            )
            Spacer(modifier = Modifier.height(DesignToken.spacing.largeIncreased))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MifosOutlinedButton(
                    onClick = {
                        if (state.step == EntityDocumentState.Step.PREVIEW) {
                            onAction(DocumentPreviewScreenAction.RejectDocument)
                        } else {
                            onAction(DocumentPreviewScreenAction.NavigateBack)
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    shape = DesignToken.shapes.small,
                    modifier = Modifier
                        .height(DesignToken.sizes.iconExtraLarge)
                        .weight(1f),
                ) {
                    Text(
                        stringResource(Res.string.btn_back),
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                MifosOutlinedButton(
                    onClick = {
                        if (state.step == EntityDocumentState.Step.PREVIEW) {
                            onAction(DocumentPreviewScreenAction.SubmitClicked)
                        } else {
                            onAction(DocumentPreviewScreenAction.UpdateNew)
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    shape = DesignToken.shapes.small,
                    enabled = state.step == EntityDocumentState.Step.PREVIEW ||
                        state.step == EntityDocumentState.Step.UPDATE_PREVIEW,
                    modifier = Modifier
                        .height(DesignToken.sizes.iconExtraLarge)
                        .weight(1f),
                ) {
                    Text(
                        if (state.step == EntityDocumentState.Step.UPDATE_PREVIEW) {
                            stringResource(Res.string.btn_update_new)
                        } else {
                            stringResource(Res.string.btn_submit)
                        },
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            if (state.showBottomSheet) {
                MifosFilePickerBottomSheet(
                    onDismiss = {
                        onAction(DocumentPreviewScreenAction.DismissBottomSheet)
                    },
                    onGalleryClick = {
                        onAction(DocumentPreviewScreenAction.PickFromGallery)
                    },
                    onFilesClick = {
                        onAction(DocumentPreviewScreenAction.PickFromFile)
                    },
                    onMoreClick = {
                        onAction(DocumentPreviewScreenAction.UseMoreOptions)
                    },
                )
            }
        }
    }
}

@Composable
private fun DocumentsPreviewScreenDialog(
    state: DocumentPreviewState,
    onAction: (DocumentPreviewScreenAction) -> Unit,
) {
    when (state.dialogState) {
        is DocumentPreviewState.DialogState.Error -> {
            MifosSweetError(
                message = state.dialogState.message,
                isRetryEnabled = false,
                buttonText = stringResource(Res.string.action_go_back),
            ) {
                onAction(DocumentPreviewScreenAction.RejectDocument)
            }
        }
        DocumentPreviewState.DialogState.Loading -> {
            MifosProgressIndicator()
        }
        null -> {}
    }
}

@Composable
private fun ViewDocumentsScreenContent(
    state: DocumentPreviewState,
    modifier: Modifier = Modifier,
    onAction: (DocumentPreviewScreenAction) -> Unit,
) {
    MifosCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = 0.dp,
        borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer),
    ) {
        if (state.dialogState != null) {
            DocumentsPreviewScreenDialog(
                state,
                onAction = onAction,
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                when (state.documentType) {
                    is DocumentType.Image -> {
                        AsyncImage(
                            model = state.documentBytes,
                            contentDescription = "Document Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center),
                        )
                    }

                    DocumentType.Pdf -> {
                        Icon(
                            imageVector = MifosIcons.PickDocument,
                            "load pdf",
                            modifier = Modifier
                                .size(DesignToken.sizes.profile)
                                .align(Alignment.Center),
                        )
                    }
                    null -> {}
                }
            }
        }
    }
}
