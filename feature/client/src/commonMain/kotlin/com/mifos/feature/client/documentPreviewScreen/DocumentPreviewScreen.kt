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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
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
import com.mifos.core.designsystem.component.*
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosFilePickerBottomSheet
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DocumentPreviewScreen(
    navigateOnDocumentUpdate: (String) -> Unit,
    navigateOnCancelUpdating: () -> Unit,
    navigateOnDocumentRejected: () -> Unit,
    navigateOnSubmitClicked: () -> Unit,
    viewmodel: DocumentPreviewScreenViewModel = koinViewModel()
) {

    val state by viewmodel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewmodel.eventFlow){event ->
        when (event) {
            DocumentPreviewEvent.OnCancelUpdating -> navigateOnCancelUpdating()
            DocumentPreviewEvent.OnDocumentRejected -> navigateOnDocumentRejected()
            DocumentPreviewEvent.OnSubmitClinked -> navigateOnSubmitClicked()
            is DocumentPreviewEvent.SendUpdatedDocument -> navigateOnDocumentUpdate(event.documentPath)
        }
    }

    ViewDocumentScaffold(
        state = state,
        onAction = remember(viewmodel){{viewmodel.trySendAction(it)}}
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
        bottomBar = {
            MifosFilePickerBottomSheet(
                showBottomSheet = state.showBottomSheet,
                onDismiss = {
                    onAction(DocumentPreviewScreenAction.DismissBottomSheet)
                },
                onGalleryClick =  {
                    onAction(DocumentPreviewScreenAction.PickFromGallery)
                },
                onFilesClick =  {
                    onAction(DocumentPreviewScreenAction.PickFromFile)
                },
                onMoreClick =  {},
            )
        },
        onBackPressed = {}
    ) {paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(DesignToken.padding.large)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if(state.dialogState!=null){
                DocumentsPreviewScreenDialog(state)
            } else {
                when (state.documentContent) {
                    null -> MifosProgressIndicator()
                    is DocumentPreviewState.Content -> {
                        ViewDocumentsScreenContent(
                            state = state,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(DesignToken.spacing.largeIncreased))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MifosOutlinedButton(
                        onClick = {
                            if (state.canUpdate) {
                                onAction(DocumentPreviewScreenAction.CancelUpdating)
                            } else {
                                onAction(DocumentPreviewScreenAction.RejectDocument)
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
                            .height(40.dp)
                            .weight(1f)
                    ) {
                        Text(
                            "Back",
                            fontFamily = FontFamily.SansSerif,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    MifosOutlinedButton(
                        onClick = {
                            if (state.canUpdate) {
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
                        modifier = Modifier
                            .height(40.dp)
                            .weight(1f)
                    ) {
                        Text(
                            if (state.canUpdate) {
                                "Update New"
                            } else {
                                "Submit"
                            },
                            fontFamily = FontFamily.SansSerif,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun DocumentsPreviewScreenDialog(
    state: DocumentPreviewState,
) {
    when (state.dialogState) {
        is DocumentPreviewState.DialogState.Error -> {
            MifosSweetError(
                message = state.dialogState.message,
                isRetryEnabled = false,
            )
        }
        DocumentPreviewState.DialogState.Loading -> {
            MifosCircularProgress()
        }
        null -> {}
    }
}


@Composable
private fun ViewDocumentsScreenContent(
    state: DocumentPreviewState,
    modifier: Modifier = Modifier,
) {
    MifosCard(
        modifier = modifier,
        elevation = 4.dp,
        colors = CardDefaults.cardColors(containerColor = AppColors.customWhite),
        borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (state.documentType) {
                is DocumentType.Image -> {
                    AsyncImage(
                        model = state.documentContent?.byteArray,
                        contentDescription = "Document Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                    )
                }
                DocumentType.Pdf -> {
                    Image(
                        imageVector = MifosIcons.Error,
                        "failed to load pdf",
                    )
                }
                null -> {}
            }
        }
    }
}