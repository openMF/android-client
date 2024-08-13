@file:OptIn(ExperimentalMaterialApi::class)

package com.mifos.feature.client.clientIdentifiers

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.LightGray
import com.mifos.core.designsystem.theme.LightGreen
import com.mifos.core.designsystem.theme.White
import com.mifos.core.designsystem.theme.identifierTextStyleDark
import com.mifos.core.designsystem.theme.identifierTextStyleLight
import com.mifos.core.objects.noncore.Identifier
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.feature.client.R
import com.mifos.feature.client.clientIdentifiersDialog.ClientIdentifiersDialogScreen

@Composable
fun ClientIdentifiersScreen(
    onBackPressed: () -> Unit,
    onDocumentClicked: (Int) -> Unit
) {
    val viewModel: ClientIdentifiersViewModel = hiltViewModel()
    val clientId by viewModel.clientId.collectAsStateWithLifecycle()
    val state by viewModel.clientIdentifiersUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadIdentifiers(clientId)
    }

    ClientIdentifiersScreen(
        clientId = clientId,
        state = state,
        onBackPressed = onBackPressed,
        onDeleteIdentifier = { identifierId ->
            viewModel.deleteIdentifier(clientId, identifierId)
        },
        refreshState = refreshState,
        onRefresh = {
            viewModel.refreshIdentifiersList(clientId)
        },
        onRetry = {
            viewModel.loadIdentifiers(clientId)
        },
        onIdentifierCreated = {
            viewModel.loadIdentifiers(clientId)
        },
        onDocumentClicked = onDocumentClicked
    )

}

@Composable
fun ClientIdentifiersScreen(
    clientId: Int,
    state: ClientIdentifiersUiState,
    onBackPressed: () -> Unit,
    onDeleteIdentifier: (Int) -> Unit,
    refreshState: Boolean,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onIdentifierCreated: () -> Unit,
    onDocumentClicked: (Int) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshState,
        onRefresh = onRefresh
    )
    var showCreateIdentifierDialog by remember { mutableStateOf(false) }


    if (showCreateIdentifierDialog) {
        ClientIdentifiersDialogScreen(
            clientId = clientId,
            onDismiss = { showCreateIdentifierDialog = false },
            onIdentifierCreated = {
                showCreateIdentifierDialog = false
                onIdentifierCreated()
            }
        )
    }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_client_identifiers),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                showCreateIdentifierDialog = true
            }) {
                Icon(
                    imageVector = MifosIcons.Add,
                    contentDescription = null
                )
            }
        },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                when (state) {
                    is ClientIdentifiersUiState.ClientIdentifiers -> {
                        when (state.identifiers.isEmpty()) {
                            true -> {
                                MifosEmptyUi(
                                    text = stringResource(id = R.string.feature_client_there_is_no_identifier_to_show),
                                    icon = MifosIcons.fileTask
                                )
                            }

                            false -> ClientIdentifiersContent(
                                identifiers = state.identifiers,
                                onDeleteIdentifier = onDeleteIdentifier,
                                onDocumentClicked = onDocumentClicked
                            )
                        }
                    }

                    is ClientIdentifiersUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {
                        onRetry()
                    }

                    is ClientIdentifiersUiState.IdentifierDeletedSuccessfully -> {
                        Toast.makeText(
                            LocalContext.current,
                            stringResource(id = R.string.feature_client_identifier_deleted),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ClientIdentifiersUiState.Loading -> MifosCircularProgress()
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
fun ClientIdentifiersContent(
    identifiers: List<Identifier>,
    onDeleteIdentifier: (Int) -> Unit,
    onDocumentClicked: (Int) -> Unit
) {
    LazyColumn {
        items(identifiers) { identifier ->
            ClientIdentifiersItem(
                identifier = identifier,
                onDeleteIdentifier = onDeleteIdentifier,
                onDocumentClicked = onDocumentClicked
            )
        }
    }
}

@Composable
fun ClientIdentifiersItem(
    identifier: Identifier,
    onDeleteIdentifier: (Int) -> Unit,
    onDocumentClicked: (Int) -> Unit
) {

    var showMenu by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(0.dp),
        colors = CardDefaults.elevatedCardColors(LightGray),
        onClick = {}
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(
                modifier = Modifier
                    .width(16.dp)
                    .height(94.dp)
            ) {
                drawRect(
                    color = LightGreen,
                    size = Size(size.width, size.height),
                )
            }
            Column(
                modifier = Modifier
                    .weight(3f)
            ) {
                MifosIdentifierDetailsText(
                    field = stringResource(id = R.string.feature_client_id),
                    value = identifier.id.toString()
                )
                MifosIdentifierDetailsText(
                    field = stringResource(id = R.string.feature_client_type),
                    value = identifier.documentType?.name ?: "-"
                )
                MifosIdentifierDetailsText(
                    field = stringResource(id = R.string.feature_client_description),
                    value = identifier.description ?: "-"
                )
            }
            IconButton(modifier = Modifier.weight(.5f), onClick = { showMenu = showMenu.not() }) {
                Icon(imageVector = MifosIcons.moreVert, contentDescription = null)
                DropdownMenu(
                    modifier = Modifier.background(White),
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    MifosMenuDropDownItem(option = stringResource(id = R.string.feature_client_remove)) {
                        identifier.id?.let { onDeleteIdentifier(it) }
                        showMenu = false
                    }
                    MifosMenuDropDownItem(option = stringResource(id = R.string.feature_client_documents)) {
                        identifier.id?.let { onDocumentClicked(it) }
                        showMenu = false
                    }
                }
            }
        }
    }
}

@Composable
fun MifosIdentifierDetailsText(field: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = field,
            style = identifierTextStyleDark
        )
        Text(
            modifier = Modifier.weight(1f),
            text = value,
            style = identifierTextStyleLight
        )
    }
}


class ClientIdentifiersUiStateProvider : PreviewParameterProvider<ClientIdentifiersUiState> {

    override val values: Sequence<ClientIdentifiersUiState>
        get() = sequenceOf(
            ClientIdentifiersUiState.Loading,
            ClientIdentifiersUiState.Error(R.string.feature_client_failed_to_load_client_identifiers),
            ClientIdentifiersUiState.IdentifierDeletedSuccessfully,
            ClientIdentifiersUiState.ClientIdentifiers(sampleClientIdentifiers)
        )
}

@Preview(showBackground = true)
@Composable
private fun ClientIdentifiersScreenPreview(
    @PreviewParameter(ClientIdentifiersUiStateProvider::class) state: ClientIdentifiersUiState
) {
    ClientIdentifiersScreen(
        clientId = 1,
        state = state,
        onBackPressed = {},
        onDeleteIdentifier = {},
        refreshState = true,
        onRefresh = {},
        onRetry = {},
        onIdentifierCreated = {},
        onDocumentClicked = {}
    )
}

val sampleClientIdentifiers = List(10) {
    Identifier(id = it, description = "description $it")
}