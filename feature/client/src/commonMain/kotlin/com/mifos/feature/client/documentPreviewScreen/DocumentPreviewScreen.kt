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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mifos.core.designsystem.component.MifosCard
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.ui.components.MifosFilePickerBottomSheet
import com.mifos.core.ui.components.MifosProgressIndicator
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientDocumentPreviewScreen(
    navigateOnDocumentUpdate: (String) -> Unit,
    navigateOnCancelUpdating: () -> Unit,
    navigateOnDocumentRejected: () -> Unit,
    navigateOnSubmitClicked: () -> Unit,
    viewmodel: DocumentPreviewScreenViewModel = koinViewModel()
) {

}


@Composable
private fun ViewDocumentsScreen(
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
    ) {
        Column {
            when (state.documentContent) {
                null -> MifosProgressIndicator()
                is DocumentPreviewState.Content -> {
                    ViewDocumentsScreenContent(
                        state = state.documentContent,
                        modifier = modifier,
                    )
                }
            }

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween,

            ){
                MifosOutlinedButton(
                    onClick = {

                    }
                ){

                }
            }
        }
    }
}


@Composable
private fun ViewDocumentsScreenContent(
    state: DocumentPreviewState.Content,
    modifier: Modifier = Modifier,
) {

    MifosCard(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        elevation = 4.dp,
        colors = CardDefaults.cardColors(containerColor = AppColors.customWhite),
        borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = state.byteArray,
                contentDescription = "view image",
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
            )
        }
    }
}