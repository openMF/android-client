@file:OptIn(ExperimentalMaterialApi::class)

package com.mifos.feature.document.document_list

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.noncore.Document
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.feature.document.R
import com.mifos.feature.document.document_dialog.DocumentDialogScreen

@Composable
fun DocumentListScreen(
    entityType: String,
    entityId: Int,
    onBackPressed: () -> Unit,
    openFilePicker: () -> Unit,
    uploadDocument: (String, String, String) -> Unit,
    documentAction: String?,
    document: Document?
) {
    val context = LocalContext.current
    val viewModel: DocumentListViewModel = hiltViewModel()
    val state by viewModel.documentListUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadDocumentState.collectAsStateWithLifecycle()
    val removeState by viewModel.removeDocumentState.collectAsStateWithLifecycle()
    val isDialogBoxActive = remember { mutableStateOf(false) }

    if(isDialogBoxActive.value)
    {
        DocumentDialogScreen(
            documentAction = documentAction,
            document = document,
            openFilePicker = { openFilePicker() },
            closeDialog = { isDialogBoxActive.value = false },
            uploadDocument = uploadDocument
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadDocumentList(entityType, entityId)
    }

    LaunchedEffect(downloadState) {
        if (downloadState) {
            Toast.makeText(
                context,
                context.getString(R.string.feature_document_download_successful),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(removeState) {
        if (removeState) {
            Toast.makeText(
                context,
                context.getString(R.string.feature_document_remove_successful),
                Toast.LENGTH_SHORT
            ).show()
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
            isDialogBoxActive.value = true
        },
        onDownloadDocument = { documentId ->
            viewModel.downloadDocument(entityType, entityId, documentId)
        },
        onUpdateDocument = { document ->
            // TODO Implement Add document dialog
        },
        onRemovedDocument = { documentId ->
            viewModel.removeDocument(entityType, entityId, documentId)
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DocumentListScreen(
    state: DocumentListUiState,
    onBackPressed: () -> Unit,
    refreshState: Boolean,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onAddDocument: () -> Unit,
    onDownloadDocument: (Int) -> Unit,
    onUpdateDocument: (Document) -> Unit,
    onRemovedDocument: (Int) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshState,
        onRefresh = onRefresh
    )

    var showSelectOptionsDialog by remember { mutableStateOf(false) }
    var selectedDocument by remember { mutableStateOf<Document?>(null) }

    if (showSelectOptionsDialog) {
        SelectOptionsDialog(onDismissRequest = {
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
            })
    }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_document_title),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                onAddDocument()
            }) {
                Icon(imageVector = MifosIcons.Add, contentDescription = null)
            }
        },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                when (state) {
                    is DocumentListUiState.DocumentList -> {
                        if (state.documents.isEmpty()) {
                            MifosEmptyUi(
                                text = stringResource(id = R.string.feature_document_no_document),
                                icon = MifosIcons.fileTask
                            )
                        } else {
                            DocumentListContent(
                                documents = state.documents,
                                onDocumentClicked = {
                                    selectedDocument = it
                                    showSelectOptionsDialog = true
                                }
                            )
                        }
                    }

                    is DocumentListUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {
                        onRetry()
                    }

                    is DocumentListUiState.Loading -> MifosCircularProgress()
                }

                PullRefreshIndicator(
                    refreshing = refreshState,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
fun DocumentListContent(
    documents: List<Document>,
    onDocumentClicked: (Document) -> Unit
) {
    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(Color.LightGray),
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    text = "Name",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal
                    ),
                    color = Black,
                    textAlign = TextAlign.Start,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Description",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal
                    ),
                    color = Black,
                    textAlign = TextAlign.Start,
                    maxLines = 1
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
fun DocumentItem(
    document: Document,
    onDocumentClicked: (Document) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(White),
        onClick = {
            onDocumentClicked(document)
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                text = document.name.toString(),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal
                ),
                color = Black,
                textAlign = TextAlign.Start,
                maxLines = 1
            )
            Text(
                modifier = Modifier.weight(1f),
                text = document.description ?: "-",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal
                ),
                color = DarkGray,
                textAlign = TextAlign.Start,
                maxLines = 1
            )
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = MifosIcons.cloudDownload,
                contentDescription = null,
                tint = DarkGray
            )
        }
    }
}

@Composable
fun SelectOptionsDialog(
    onDismissRequest: () -> Unit,
    downloadDocument: () -> Unit,
    updateDocument: () -> Unit,
    removeDocument: () -> Unit,
) {

    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            colors = CardDefaults.cardColors(White),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.feature_document_select_option),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { downloadDocument() },
                    colors = ButtonDefaults.buttonColors(BlueSecondary)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_document_download_document),
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = { updateDocument() },
                    colors = ButtonDefaults.buttonColors(BlueSecondary)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_document_update_document),
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = { removeDocument() },
                    colors = ButtonDefaults.buttonColors(BlueSecondary)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_document_remove_document),
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


class DocumentListUiStateProvider : PreviewParameterProvider<DocumentListUiState> {

    override val values: Sequence<DocumentListUiState>
        get() = sequenceOf(
            DocumentListUiState.DocumentList(sampleDocumentList),
            DocumentListUiState.Error(R.string.feature_document_failed_to_load_documents_list),
            DocumentListUiState.Loading
        )
}

@Preview(showBackground = true)
@Composable
private fun DocumentListPreview(
    @PreviewParameter(DocumentListUiStateProvider::class) state: DocumentListUiState
) {
    DocumentListScreen(
        state = state,
        onBackPressed = { },
        refreshState = false,
        onRefresh = { },
        onRetry = { },
        onAddDocument = { },
        onDownloadDocument = { },
        onUpdateDocument = { },
        onRemovedDocument = { }
    )
}

val sampleDocumentList = List(10) {
    Document(name = "Document $it", description = "desc $it")
}