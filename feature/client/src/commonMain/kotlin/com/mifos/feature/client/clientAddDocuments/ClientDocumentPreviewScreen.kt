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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.documentPreviewScreen.DocumentPreviewScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientDocumentPreviewScreen(
    navigateToAddDocumentScreen: () -> Unit,
    viewmodel: ClientAddDocumentScreenViewmodel = koinViewModel()
) {

    val state by viewmodel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewmodel.eventFlow) {events ->
        when(events){
            ClientAddDocumentScreenEvents.AddDocumentEvent.OnNavigateBack -> {}
            ClientAddDocumentScreenEvents.AddDocumentEvent.NavigateToPreviewScreen -> {}
            ClientAddDocumentScreenEvents
                .PreviewDocumentEvent.OnNavigateToAddDocScreen -> navigateToAddDocumentScreen()
        }
    }

    DocumentPreviewScreen(
        platformFile = state.platformFile,
        canUpdateDocument = state.isDocumentAdded,
        isBottomSheetOpen = state.previewScreenState.showBottomSheet,
        onBack = {
            viewmodel.trySendAction(
                ClientAddDocumentScreenAction.PreviewDocumentActions.ClosePreviewActions
            )
        },
        onSubmit = {
            viewmodel.trySendAction(
                ClientAddDocumentScreenAction.PreviewDocumentActions.SubmitDocument
            )
        },
        onUploadFromGallery = {
            viewmodel.trySendAction(
                ClientAddDocumentScreenAction.PreviewDocumentActions.PickFromGallery
            )
        },
        toggleBottomSheet = {
            viewmodel.trySendAction(
                ClientAddDocumentScreenAction.PreviewDocumentActions.ToggleBottomSheet
            )
        },
        onUploadFromFiles = {
            viewmodel.trySendAction(
                ClientAddDocumentScreenAction.PreviewDocumentActions.PickFromFiles
            )
        },
        onClickMoreOptions = {
            viewmodel.trySendAction(
                ClientAddDocumentScreenAction.PreviewDocumentActions.UseMoreOptions
            )
        },
    )

}
