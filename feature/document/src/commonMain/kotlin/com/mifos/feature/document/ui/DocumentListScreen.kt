/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.document.ui

import androidclient.feature.document.generated.resources.Res
import androidclient.feature.document.generated.resources.feature_document_cd_add_document
import androidclient.feature.document.generated.resources.feature_document_cd_download_icon
import androidclient.feature.document.generated.resources.feature_document_column_description
import androidclient.feature.document.generated.resources.feature_document_column_name
import androidclient.feature.document.generated.resources.feature_document_download_document
import androidclient.feature.document.generated.resources.feature_document_download_successful
import androidclient.feature.document.generated.resources.feature_document_failed_to_download_document
import androidclient.feature.document.generated.resources.feature_document_failed_to_remove_document
import androidclient.feature.document.generated.resources.feature_document_no_description_placeholder
import androidclient.feature.document.generated.resources.feature_document_remove_document
import androidclient.feature.document.generated.resources.feature_document_remove_successful
import androidclient.feature.document.generated.resources.feature_document_select_option
import androidclient.feature.document.generated.resources.feature_document_title
import androidclient.feature.document.generated.resources.feature_document_update_document
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import template.core.base.store.screen.DataFreshness
import template.core.base.store.screen.ScreenState
import template.core.base.store.submit.SubmitState
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.model.objects.noncoreobjects.Document
import com.mifos.core.ui.util.DevicePreview
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import template.core.base.designsystem.theme.KptTheme
import template.core.base.ui.screen.ScreenContent

@Composable
internal fun DocumentListScreen(
    onBackPressed: () -> Unit,
    viewModel: DocumentListViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadState.collectAsStateWithLifecycle()
    val removeState by viewModel.removeState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            DocumentListEvent.NavigateBack -> onBackPressed()
        }
    }

    // Surface download success / failure as snackbars; the VM auto-reloads on
    // success and reset()s the handler before the next click.
    LaunchedEffect(downloadState) {
        when (val s = downloadState) {
            is SubmitState.Submitted -> {
                snackbarHostState.showSnackbar(getString(Res.string.feature_document_download_successful))
                viewModel.onDownloadConsumed()
            }
            is SubmitState.Failed -> {
                snackbarHostState.showSnackbar(getString(Res.string.feature_document_failed_to_download_document))
                viewModel.onDownloadConsumed()
            }
            else -> Unit
        }
        // Silence unused warning for sealed exhaustiveness on Submitting/Idle.
        @Suppress("UNUSED_VARIABLE")
        val unused = s
    }

    LaunchedEffect(removeState) {
        when (val s = removeState) {
            is SubmitState.Submitted -> {
                snackbarHostState.showSnackbar(getString(Res.string.feature_document_remove_successful))
                viewModel.onRemoveConsumed()
            }
            is SubmitState.Failed -> {
                snackbarHostState.showSnackbar(getString(Res.string.feature_document_failed_to_remove_document))
                viewModel.onRemoveConsumed()
            }
            else -> Unit
        }
        @Suppress("UNUSED_VARIABLE")
        val unused = s
    }

    DocumentListScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    state.selectedDocument?.let { _ ->
        SelectOptionsDialog(
            onDismissRequest = { viewModel.trySendAction(DocumentListAction.DismissRowActionDialog) },
            downloadDocument = { viewModel.trySendAction(DocumentListAction.OnDownloadSelected) },
            updateDocument = { viewModel.trySendAction(DocumentListAction.OnUpdateSelected) },
            removeDocument = { viewModel.trySendAction(DocumentListAction.OnRemoveSelected) },
        )
    }

    val pendingKind = state.pendingDialog
    if (pendingKind != null) {
        UploadDocumentDialog(
            viewModel = koinViewModel(
                key = "upload-document-${state.entityType}-${state.entityId}-${state.documentBeingEdited?.id ?: 0}",
            ) {
                parametersOf(
                    state.entityId,
                    state.entityType,
                    pendingKind,
                    state.documentBeingEdited?.id,
                    state.documentBeingEdited?.name.orEmpty(),
                    state.documentBeingEdited?.description.orEmpty(),
                )
            },
            onDismiss = { viewModel.trySendAction(DocumentListAction.OnUploadDialogDismissed) },
            onSubmitted = { viewModel.trySendAction(DocumentListAction.OnUploadFinished) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DocumentListScreenContent(
    state: DocumentListState,
    snackbarHostState: SnackbarHostState,
    onAction: (DocumentListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullToRefreshState()

    MifosScaffold(
        modifier = modifier,
        title = stringResource(Res.string.feature_document_title),
        onBackPressed = { onAction(DocumentListAction.NavigateBack) },
        actions = {
            IconButton(onClick = { onAction(DocumentListAction.OnClickAddDocument) }) {
                Icon(
                    imageVector = MifosIcons.Add,
                    contentDescription = stringResource(Res.string.feature_document_cd_add_document),
                )
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(DocumentListAction.OnRefresh) },
                state = pullRefreshState,
                modifier = Modifier.fillMaxSize(),
            ) {
                ScreenContent(
                    state = state.screenState,
                    onRetry = { onAction(DocumentListAction.OnRetry) },
                    modifier = Modifier.fillMaxSize(),
                ) { documents, _ ->
                    DocumentListBody(
                        documents = documents,
                        onDocumentClicked = { onAction(DocumentListAction.OnDocumentClicked(it)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentListBody(
    documents: List<Document>,
    onDocumentClicked: (Document) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(KptTheme.spacing.sm),
            shape = RectangleShape,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KptTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = KptTheme.spacing.md),
                    text = stringResource(Res.string.feature_document_column_name),
                    style = KptTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.feature_document_column_description),
                    style = KptTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                )
            }
        }
        LazyColumn {
            items(documents) { document ->
                DocumentItem(document = document, onDocumentClicked = onDocumentClicked)
            }
        }
    }
}

@Composable
private fun DocumentItem(
    document: Document,
    onDocumentClicked: (Document) -> Unit,
    modifier: Modifier = Modifier,
) {
    val placeholder = stringResource(Res.string.feature_document_no_description_placeholder)
    val downloadCd = stringResource(Res.string.feature_document_cd_download_icon)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = KptTheme.spacing.xs, horizontal = KptTheme.spacing.sm),
        onClick = { onDocumentClicked(document) },
    ) {
        Row(
            modifier = Modifier
                .padding(KptTheme.spacing.md)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = KptTheme.spacing.md),
                text = document.name.orEmpty(),
                style = KptTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                maxLines = 1,
            )
            Text(
                modifier = Modifier.weight(1f),
                text = document.description ?: placeholder,
                style = KptTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                maxLines = 1,
            )
            Icon(
                modifier = Modifier.size(DesignToken.sizes.dp18),
                imageVector = MifosIcons.CloudDownload,
                contentDescription = downloadCd,
            )
        }
    }
}

@Composable
private fun SelectOptionsDialog(
    onDismissRequest: () -> Unit,
    downloadDocument: () -> Unit,
    updateDocument: () -> Unit,
    removeDocument: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Card(
            shape = DesignToken.shapes.largeIncreased,
        ) {
            Column(
                modifier = Modifier.padding(DesignToken.padding.dp30),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.feature_document_select_option),
                    modifier = Modifier.fillMaxWidth(),
                    style = KptTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                MifosButton(onClick = downloadDocument) {
                    Text(
                        text = stringResource(Res.string.feature_document_download_document),
                        modifier = Modifier.fillMaxWidth(),
                        style = KptTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
                MifosButton(onClick = updateDocument) {
                    Text(
                        text = stringResource(Res.string.feature_document_update_document),
                        modifier = Modifier.fillMaxWidth(),
                        style = KptTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
                MifosButton(onClick = removeDocument) {
                    Text(
                        text = stringResource(Res.string.feature_document_remove_document),
                        modifier = Modifier.fillMaxWidth(),
                        style = KptTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

// region Previews

private val demoDocuments: List<Document> = List(3) {
    Document(
        id = it + 1,
        name = "Document $it",
        description = "Description $it",
    )
}

@DevicePreview
@Composable
private fun PreviewDocumentListLoading() {
    DocumentListScreenContent(
        state = DocumentListState(screenState = ScreenState.Loading),
        snackbarHostState = remember { SnackbarHostState() },
        onAction = {},
    )
}

@DevicePreview
@Composable
private fun PreviewDocumentListContent() {
    DocumentListScreenContent(
        state = DocumentListState(
            screenState = ScreenState.Content(
                data = demoDocuments,
                freshness = DataFreshness.FRESH,
            ),
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onAction = {},
    )
}

@DevicePreview
@Composable
private fun PreviewDocumentListEmpty() {
    DocumentListScreenContent(
        state = DocumentListState(screenState = ScreenState.Empty),
        snackbarHostState = remember { SnackbarHostState() },
        onAction = {},
    )
}

// endregion
