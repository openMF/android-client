/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.feature.document.documentList


import androidclient.feature.document.generated.resources.Res
import androidclient.feature.document.generated.resources.feature_document_download_document
import androidclient.feature.document.generated.resources.feature_document_download_successful
import androidclient.feature.document.generated.resources.feature_document_no_document
import androidclient.feature.document.generated.resources.feature_document_remove_document
import androidclient.feature.document.generated.resources.feature_document_remove_successful
import androidclient.feature.document.generated.resources.feature_document_select_option
import androidclient.feature.document.generated.resources.feature_document_title
import androidclient.feature.document.generated.resources.feature_document_update_document
import androidclient.feature.document.generated.resources.feature_document_upload_document
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.rememberMifosPullToRefreshState
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.objects.noncoreobjects.Document
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.feature.document.documentDialog.DocumentDialogScreen
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DocumentListScreen(
    viewModel: DocumentListViewModel = koinViewModel(),
    onBackPressed: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.documentListUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadDocumentState.collectAsStateWithLifecycle()
    val removeState by viewModel.removeDocumentState.collectAsStateWithLifecycle()
    var isDialogBoxActive by rememberSaveable { mutableStateOf(false) }
    var dialogBoxAction by rememberSaveable { mutableStateOf("") }
    var dialogDocument by rememberSaveable { mutableStateOf(Document()) }
    val entityId by viewModel.entityId.collectAsStateWithLifecycle()
    val entityType by viewModel.entityType.collectAsStateWithLifecycle()

    if (isDialogBoxActive) {
        DocumentDialogScreen(
            entityType = entityType,
            entityId = entityId,
            documentAction = dialogBoxAction,
            document = dialogDocument,
            closeDialog = { isDialogBoxActive = false },
            closeScreen = {
                isDialogBoxActive = false
                onBackPressed()
            },
        )
    }

    LaunchedEffect(Unit) {
        Logger.e("documentListDebugLog"){
            "id : $entityId, type : $entityType"
        }
        viewModel.loadDocumentList(entityType, entityId)
    }

    LaunchedEffect(downloadState) {
        if (downloadState) {
            snackbarHostState.showSnackbar(message = getString(Res.string.feature_document_download_successful)
        }
    }

    LaunchedEffect(removeState) {
        if (removeState) {
            snackbarHostState.showSnackbar(message =  getString(Res.string.feature_document_remove_successful))
        }
    }

    DocumentListScreen(
        state = state,
        onBackPressed = onBackPressed,
        refreshState = refreshState,
        onRefresh = {
            viewModel.refreshDocumentList(entityType, entityId)
        },
        onRetry = {
            viewModel.loadDocumentList(entityType, entityId)
        },
        onAddDocument = {
            dialogBoxAction = "Upload Document"
            isDialogBoxActive = true
        },
        onDownloadDocument = { documentId ->
            viewModel.downloadDocument(entityType, entityId, documentId)
        },
        onUpdateDocument = { document ->
            dialogDocument = document
            dialogBoxAction = "Update Document"
            isDialogBoxActive = true
        },
        onRemovedDocument = { documentId ->
            viewModel.removeDocument(entityType, entityId, documentId)
        },
        snackbarHostState=snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DocumentListScreen(
    state: DocumentListUiState,
    onBackPressed: () -> Unit,
    snackbarHostState: SnackbarHostState,
    refreshState: Boolean,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onAddDocument: () -> Unit,
    onDownloadDocument: (Int) -> Unit,
    onUpdateDocument: (Document) -> Unit,
    modifier: Modifier = Modifier,
    onRemovedDocument: (Int) -> Unit,
) {
    val pullRefreshState = rememberPullToRefreshState()


    var showSelectOptionsDialog by remember { mutableStateOf(false) }
    var selectedDocument by remember { mutableStateOf<Document?>(null) }

    if (showSelectOptionsDialog) {
        SelectOptionsDialog(
            onDismissRequest = {
                run {
                    showSelectOptionsDialog = !showSelectOptionsDialog
                }
            },
            downloadDocument = {
                selectedDocument?.id?.let { onDownloadDocument(it) }
                showSelectOptionsDialog = !showSelectOptionsDialog
            },
            updateDocument = {
                selectedDocument?.let { onUpdateDocument(it) }
                showSelectOptionsDialog = !showSelectOptionsDialog
            },
            removeDocument = {
                selectedDocument?.id?.let { onRemovedDocument(it) }
                showSelectOptionsDialog = !showSelectOptionsDialog
            },
        )
    }

    MifosScaffold(
        modifier = modifier,
        title = stringResource(Res.string.feature_document_title),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                onAddDocument()
            }) {
                Icon(imageVector = MifosIcons.Add, contentDescription = null)
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box() {
                PullToRefreshBox(
                    state = pullRefreshState,
                    modifier = Modifier.fillMaxSize(),
                    isRefreshing = refreshState,
                    onRefresh = onRefresh,
                ){

                when (state) {
                    is DocumentListUiState.DocumentList -> {
                        if (state.documents.isEmpty()) {
                            MifosEmptyUi(
                                text = stringResource(Res.string.feature_document_no_document),
                                icon = MifosIcons.FileTask,
                            )
                        } else {
                            DocumentListContent(
                                documents = state.documents,
                                onDocumentClicked = {
                                    selectedDocument = it
                                    showSelectOptionsDialog = true
                                },
                            )
                        }
                    }

                    is DocumentListUiState.Error -> MifosSweetError(message = stringResource(state.message)) {
                        onRetry()
                    }

                    is DocumentListUiState.Loading -> MifosCircularProgress()
                }
                }
            }
        }
    }
}

@Composable
private fun DocumentListContent(
    documents: List<Document>,
    modifier: Modifier = Modifier,
    onDocumentClicked: (Document) -> Unit,
) {
    Column(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    text = "Name",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Description",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    ),
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
    modifier: Modifier = Modifier,
    onDocumentClicked: (Document) -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(White),
        onClick = {
            onDocumentClicked(document)
        },
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                text = document.name.toString(),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                ),
                textAlign = TextAlign.Start,
                maxLines = 1,
            )
            Text(
                modifier = Modifier.weight(1f),
                text = document.description ?: "-",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                ),
                textAlign = TextAlign.Start,
                maxLines = 1,
            )
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = MifosIcons.CloudDownload,
                contentDescription = null,
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
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.feature_document_select_option),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(20.dp))

                MifosButton(
                    onClick = { downloadDocument() },
                ) {
                    Text(
                        text = stringResource( Res.string.feature_document_download_document),
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                    )
                }
                MifosButton(
                    onClick = { updateDocument() },
                ) {
                    Text(
                        text = stringResource(Res.string.feature_document_update_document),
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                    )
                }
                MifosButton(
                    onClick = { removeDocument() },
                ) {
                    Text(
                        text = stringResource( Res.string.feature_document_remove_document),
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

//private class DocumentListUiStateProvider : PreviewParameterProvider<DocumentListUiState> {
//
//    override val values: Sequence<DocumentListUiState>
//        get() = sequenceOf(
//            DocumentListUiState.DocumentList(sampleDocumentList),
//            DocumentListUiState.Error(R.string.feature_document_failed_to_load_documents_list),
//            DocumentListUiState.Loading,
//        )
//}
//
//@Preview(showBackground = true)
//@Composable
//private fun DocumentListPreview(
//    @PreviewParameter(DocumentListUiStateProvider::class) state: DocumentListUiState,
//) {
//    DocumentListScreen(
//        state = state,
//        onBackPressed = { },
//        refreshState = false,
//        onRefresh = { },
//        onRetry = { },
//        onAddDocument = { },
//        onDownloadDocument = { },
//        onUpdateDocument = { },
//        onRemovedDocument = { },
//    )
//}

//private val sampleDocumentList = List(10) {
//    Document(name = "Document $it", description = "desc $it")
//}
