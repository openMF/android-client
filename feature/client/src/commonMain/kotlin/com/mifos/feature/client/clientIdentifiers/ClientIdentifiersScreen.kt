/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.client.clientIdentifiers

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_description
import androidclient.feature.client.generated.resources.feature_client_documents
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client_identifiers
import androidclient.feature.client.generated.resources.feature_client_id
import androidclient.feature.client.generated.resources.feature_client_identifier_deleted_successfully
import androidclient.feature.client.generated.resources.feature_client_identifiers
import androidclient.feature.client.generated.resources.feature_client_remove
import androidclient.feature.client.generated.resources.feature_client_there_is_no_identifier_to_show
import androidclient.feature.client.generated.resources.feature_client_type
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.identifierTextStyleDark
import com.mifos.core.designsystem.theme.identifierTextStyleLight
import com.mifos.core.model.objects.noncoreobjects.Identifier
import com.mifos.core.model.objects.noncoreobjects.IdentifierPayload
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.util.DevicePreview
import com.mifos.feature.client.clientIdentifiersDialog.ClientIdentifierDialogUiState
import com.mifos.feature.client.clientIdentifiersDialog.ClientIdentifiersDialogScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientIdentifiersScreen(
    onBackPressed: () -> Unit,
    onDocumentClicked: (Int) -> Unit,
    viewModel: ClientIdentifiersViewModel = koinViewModel(),
) {
    val clientIdentifiersUiState by viewModel.clientIdentifiersUiState.collectAsStateWithLifecycle()
    val clientIdentifiersDialogUiState by viewModel.clientIdentifierDialogUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val showCreateDialog by viewModel.showCreateDialog.collectAsStateWithLifecycle()

    ClientIdentifiersScreen(
        state = clientIdentifiersUiState,
        dialogState = clientIdentifiersDialogUiState,
        showCreateDialog = showCreateDialog,
        onShowDialog = viewModel::showCreateIdentifierDialog,
        onHideDialog = viewModel::hideCreateIdentifierDialog,
        onBackPressed = onBackPressed,
        onDeleteIdentifier = { identifierId ->
            viewModel.deleteIdentifier(identifierId)
        },
        onCreateIdentifier = { identifierPayload ->
            viewModel.createClientIdentifier(identifierPayload)
        },
        refreshState = refreshState,
        onRefresh = viewModel::refreshIdentifiersList,
        onRetry = viewModel::loadIdentifiers,
        onDocumentClicked = onDocumentClicked,
        events = viewModel.events,
    )
}

@Composable
internal fun ClientIdentifiersScreen(
    state: ClientIdentifiersUiState,
    dialogState: ClientIdentifierDialogUiState,
    showCreateDialog: Boolean,
    onShowDialog: () -> Unit,
    onHideDialog: () -> Unit,
    onBackPressed: () -> Unit,
    onDeleteIdentifier: (Int) -> Unit,
    onCreateIdentifier: (IdentifierPayload) -> Unit,
    refreshState: Boolean,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onDocumentClicked: (Int) -> Unit,
    events: Flow<ClientIdentifiersViewModel.ClientIdentifiersEvent>,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is ClientIdentifiersViewModel.ClientIdentifiersEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(message = getString(event.message))
                }
            }
        }
    }

    if (showCreateDialog) {
        ClientIdentifiersDialogScreen(
            state = dialogState,
            onDismiss = {
                onHideDialog()
            },
            onRetry = onRetry,
            onCreateIdentifier = onCreateIdentifier,
        )
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_client_identifiers),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(
                onClick = {
                    onShowDialog()
                },
            ) {
                Icon(
                    imageVector = MifosIcons.Add,
                    contentDescription = null,
                )
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            PullToRefreshBox(
                state = pullToRefreshState,
                onRefresh = onRefresh,
                isRefreshing = refreshState,
            ) {
                when (state) {
                    is ClientIdentifiersUiState.ClientIdentifiers -> {
                        when (state.identifiers.isEmpty()) {
                            true -> {
                                MifosEmptyUi(
                                    text = stringResource(Res.string.feature_client_there_is_no_identifier_to_show),
                                    icon = MifosIcons.FileTask,
                                )
                            }

                            false -> ClientIdentifiersContent(
                                identifiers = state.identifiers,
                                onDeleteIdentifier = onDeleteIdentifier,
                                onDocumentClicked = onDocumentClicked,
                            )
                        }
                    }

                    is ClientIdentifiersUiState.Error -> MifosSweetError(
                        message = stringResource(
                            state.message,
                        ),
                    ) {
                        onRetry()
                    }

                    is ClientIdentifiersUiState.IdentifierDeletedSuccessfully -> {
                    }

                    is ClientIdentifiersUiState.Loading -> MifosCircularProgress()
                }
            }
        }
    }
}

@Composable
private fun ClientIdentifiersContent(
    identifiers: List<Identifier>,
    onDeleteIdentifier: (Int) -> Unit,
    onDocumentClicked: (Int) -> Unit,
) {
    LazyColumn {
        items(identifiers) { identifier ->
            ClientIdentifiersItem(
                identifier = identifier,
                onDeleteIdentifier = onDeleteIdentifier,
                onDocumentClicked = onDocumentClicked,
            )
        }
    }
}

@Composable
private fun ClientIdentifiersItem(
    identifier: Identifier,
    onDeleteIdentifier: (Int) -> Unit,
    onDocumentClicked: (Int) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(0.dp),
        colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.surfaceVariant),
        onClick = {},
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Canvas(
                modifier = Modifier
                    .width(16.dp)
                    .height(94.dp),
            ) {
                drawRect(
                    // TODO use lightGreen color
                    color = Color.Green,
                    size = Size(size.width, size.height),
                )
            }
            Column(
                modifier = Modifier
                    .weight(3f),
            ) {
                MifosIdentifierDetailsText(
                    field = stringResource(Res.string.feature_client_id),
                    value = identifier.id.toString(),
                )
                MifosIdentifierDetailsText(
                    field = stringResource(Res.string.feature_client_type),
                    value = identifier.documentType?.name ?: "-",
                )
                MifosIdentifierDetailsText(
                    field = stringResource(Res.string.feature_client_description),
                    value = identifier.description ?: "-",
                )
            }
            IconButton(modifier = Modifier.weight(.5f), onClick = { showMenu = showMenu.not() }) {
                Icon(imageVector = MifosIcons.MoreVert, contentDescription = null)
                DropdownMenu(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    MifosMenuDropDownItem(
                        option = stringResource(Res.string.feature_client_remove),
                        onClick = {
                            identifier.id?.let { onDeleteIdentifier(it) }
                            showMenu = false
                        },
                    )
                    MifosMenuDropDownItem(
                        option = stringResource(Res.string.feature_client_documents),
                        onClick = {
                            identifier.id?.let { onDocumentClicked(it) }
                            showMenu = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun MifosIdentifierDetailsText(field: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = field,
            style = identifierTextStyleDark,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = value,
            style = identifierTextStyleLight,
        )
    }
}

private class ClientIdentifiersUiStateProvider :
    PreviewParameterProvider<ClientIdentifiersUiState> {

    override val values: Sequence<ClientIdentifiersUiState>
        get() = sequenceOf(
            ClientIdentifiersUiState.Loading,
            ClientIdentifiersUiState.Error(Res.string.feature_client_failed_to_load_client_identifiers),
            ClientIdentifiersUiState.IdentifierDeletedSuccessfully(Res.string.feature_client_identifier_deleted_successfully),
            ClientIdentifiersUiState.ClientIdentifiers(sampleClientIdentifiers),
        )
}

@DevicePreview
@Composable
private fun ClientIdentifiersScreenPreview(
    @PreviewParameter(ClientIdentifiersUiStateProvider::class) state: ClientIdentifiersUiState,
) {
    ClientIdentifiersScreen(
        state = state,
        dialogState = ClientIdentifierDialogUiState.Loading,
        onBackPressed = {},
        onDeleteIdentifier = {},
        refreshState = true,
        onRefresh = {},
        onRetry = {},
        onDocumentClicked = {},
        onShowDialog = {},
        onCreateIdentifier = {},
        showCreateDialog = false,
        onHideDialog = {},
        events = emptyFlow(),
    )
}

val sampleClientIdentifiers = List(10) {
    Identifier(id = it, description = "description $it")
}
