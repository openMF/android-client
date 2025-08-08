/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.mifos.feature.client.clientList

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_ic_done_all_black_24dp
import androidclient.feature.client.generated.resources.feature_client_ic_dp_placeholder
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.components.SelectionModeTopAppBar
import com.mifos.core.ui.util.DevicePreview
import com.mifos.feature.client.syncClientDialog.SyncClientsDialogScreen
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by Aditya Gupta on 21/02/24.
 */

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun ClientListScreen(
    paddingValues: PaddingValues,
    createNewClient: () -> Unit,
    onClientSelect: (Int) -> Unit,
    viewModel: ClientListViewModel = koinViewModel(),
) {
    LaunchedEffect(key1 = true) {
        viewModel.getClientList()
    }
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()

    val snackbarHostState = remember { SnackbarHostState() }

    val state = viewModel.clientListUiState.collectAsState().value

    var isInSelectionMode by remember { mutableStateOf(false) }
    val selectedItems = remember { ClientSelectionState() }

    val resetSelectionMode = {
        isInSelectionMode = false
        selectedItems.clear()
    }
    val sync = rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(enabled = isInSelectionMode) {
        resetSelectionMode()
    }

    LaunchedEffect(
        key1 = isInSelectionMode,
        key2 = selectedItems.size(),
    ) {
        if (isInSelectionMode && selectedItems.isEmpty()) {
            isInSelectionMode = false
        }
    }

    MifosScaffold(
        modifier = Modifier
            .padding(paddingValues),
        topBar = {
            if (isInSelectionMode) {
                SelectionModeTopAppBar(
                    itemCount = selectedItems.size(),
                    resetSelectionMode = resetSelectionMode,
                    actions = {
                        FilledTonalButton(
                            onClick = {
                                sync.value = true
                            },
                        ) {
                            Icon(
                                imageVector = MifosIcons.Sync,
                                contentDescription = "Sync Items",
                            )
                            Text("Test")
                        }
                    },
                )
//                SelectionModeTopAppBar(
//                    currentSelectedItems = selectedItems.selectedItems.value,
//                    syncClicked = { sync.value = true },
//                    resetSelectionMode = resetSelectionMode,
//                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { createNewClient() },
                containerColor = MaterialTheme.colorScheme.secondary,
            ) {
                Icon(
                    imageVector = MifosIcons.AddFilled,
                    contentDescription = null,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        snackbarHostState = snackbarHostState,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    top = if (isInSelectionMode) paddingValues.calculateTopPadding() else 0.dp,
                ),
            verticalArrangement = Arrangement.Center,
        ) {
            PullToRefreshBox(
                state = pullToRefreshState,
                onRefresh = viewModel::refreshClientList,
                isRefreshing = isRefreshing,
            ) {
                when (state) {
                    is ClientListUiState.ClientListApi -> {
                        LazyColumnForClientListApi(
                            pagingFlow = state.list,
                            isInSelectionMode = isInSelectionMode,
                            selectedItems = selectedItems,
                            onClientSelect = {
                                onClientSelect(it)
                            },
                            failedRefresh = { viewModel.refreshClientList() },
                            selectedMode = {
                                isInSelectionMode = true
                            },
                        )
                    }

                    is ClientListUiState.ClientListDb -> {
                        LazyColumnForClientListDb(clientList = state.list ?: emptyList())
                    }

                    ClientListUiState.Empty -> {
                    }

                    is ClientListUiState.Error -> {
                        MifosSweetError(message = stringResource(state.message)) {
                            viewModel.refreshClientList()
                        }
                    }
                }
            }
            if (sync.value) {
                SyncClientsDialogScreen(
                    dismiss = {
                        resetSelectionMode.invoke()
                        selectedItems.clear()
                        sync.value = false
                    },
                    hide = { sync.value = false },
                    list = selectedItems.selectedItems.value,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectionModeTopAppBar(
    currentSelectedItems: List<ClientEntity>,
    syncClicked: () -> Unit,
    resetSelectionMode: () -> Unit,
) {
    val selectedItems = currentSelectedItems.toMutableStateList()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary,
        ),
        title = {
            Text(
                text = "${selectedItems.size} selected",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
        navigationIcon = {
            IconButton(
                onClick = resetSelectionMode,
            ) {
                Icon(
                    imageVector = MifosIcons.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    syncClicked()
                    resetSelectionMode()
                },
            ) {
                Icon(
                    imageVector = MifosIcons.Sync,
                    contentDescription = "Sync",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
    )
}

class ClientSelectionState(initialSelectedItems: List<ClientEntity> = emptyList()) {
    private val _selectedItems =
        mutableStateListOf<ClientEntity>().also { it.addAll(initialSelectedItems) }
    var selectedItems: State<List<ClientEntity>> = derivedStateOf { _selectedItems }

    fun add(client: ClientEntity) {
        _selectedItems.add(client)
    }

    fun remove(client: ClientEntity) {
        _selectedItems.remove(client)
    }

    fun contains(client: ClientEntity): Boolean {
        return _selectedItems.contains(client)
    }

    fun isEmpty(): Boolean {
        return _selectedItems.isEmpty()
    }

    fun clear() {
        _selectedItems.clear()
    }

    fun size(): Int {
        return _selectedItems.size
    }

    fun toList(): List<ClientEntity> {
        return _selectedItems.toList()
    }
}

@Composable
internal expect fun LazyColumnForClientListApi(
    pagingFlow: Flow<PagingData<ClientEntity>>,
    isInSelectionMode: Boolean,
    selectedItems: ClientSelectionState,
    failedRefresh: () -> Unit,
    onClientSelect: (Int) -> Unit,
    selectedMode: () -> Unit,
)

@Composable
private fun LazyColumnForClientListDb(clientList: List<ClientEntity>) {
    LazyColumn {
        items(clientList) { client ->

            OutlinedCard(
                modifier = Modifier.padding(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 24.dp,
                            bottom = 24.dp,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.feature_client_ic_dp_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp),
                    ) {
                        client.displayName?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        Text(
                            text = client.accountNo.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    if (client.sync) {
                        Image(
                            painter = painterResource(Res.drawable.feature_client_ic_done_all_black_24dp),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

@DevicePreview
@Composable
private fun ClientListScreenPreview() {
    ClientListScreen(
        paddingValues = PaddingValues(),
        createNewClient = {},
        onClientSelect = {},
    )
}

@DevicePreview
@Composable
private fun LazyColumnForClientListDbPreview() {
    val clientList = listOf(
        ClientEntity(
            id = 1,
            displayName = "Arian",
            accountNo = "1234567890",
            sync = true,
        ),
        ClientEntity(
            id = 2,
            displayName = "oreo",
            accountNo = "9876543210",
            sync = false,
        ),
        ClientEntity(
            id = 2,
            displayName = "biscuit",
            accountNo = "98765983210",
            sync = false,
        ),
    )

    LazyColumnForClientListDb(clientList = clientList)
}
