/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientsList

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.account_number_prefix
import androidclient.feature.client.generated.resources.feature_client_client
import androidclient.feature.client.generated.resources.string_not_available
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import com.mifos.core.designsystem.component.BasicDialogState
import com.mifos.core.designsystem.component.MifosBasicDialog
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosRowCard
import com.mifos.core.ui.util.EventsEffect
import com.mifos.core.ui.util.TextUtil
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientListScreen(
    createNewClient: () -> Unit,
    onClientClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClientListViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is ClientListEvent.OnClientClick -> onClientClick(event.clientId)
            ClientListEvent.NavigateToCreateClient -> createNewClient()
        }
    }

    ClientListContentScreen(
        modifier = modifier,
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    ClientListDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(ClientListAction.OnDismissDialog) }
        },
    )
}

@Composable
private fun ClientActions(
    state: ClientListState,
    onAction: (ClientListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(DesignToken.padding.large),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (!state.isSearchActive) {
                Row(
                    modifier = Modifier.clickable {
                        onAction(ClientListAction.NavigateToCreateClient)
                    },
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.padding.small),
                ) {
                    Text(
                        text = "Clients",
                        style = MifosTypography.titleMediumEmphasized,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Icon(
                        imageVector = MifosIcons.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(DesignToken.sizes.iconAverage),
                    )
                }
//                Icon(
//                    imageVector = MifosIcons.Search,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(DesignToken.sizes.iconAverage)
//                        .clickable{
//                            onAction(ClientListAction.ActivateSearch)
//                        },
//                )
            }
//            else{
//                MifosSearchBar(
//                    query = state.searchQuery,
//                    onQueryChange = {
//                        onAction(ClientListAction.OnQueryChange(it))
//                    },
//                    onBackClick = {
//                        onAction(ClientListAction.DismissSearch)
//                    },
//                    onSearchClick = {
//
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }
        }
        Spacer(Modifier.width(DesignToken.padding.largeIncreased))
//        Icon(
//            imageVector = MifosIcons.Filter,
//            contentDescription = null,
//            modifier = Modifier
//                .size(DesignToken.sizes.iconAverage)
//                .clickable {
//                },
//        )
    }
}

@Composable
private fun ClientListContentScreen(
    state: ClientListState,
    modifier: Modifier = Modifier,
    onAction: (ClientListAction) -> Unit,
) {
    MifosScaffold(
        title = stringResource(Res.string.feature_client_client),
        onBackPressed = { },
        modifier = modifier,
    ) { paddingValues ->
        if (state.isEmpty) {
            MifosEmptyCard("No clients found")
        }
        if (state.clients.isNotEmpty()) {
            ClientListContent(
                clientsList = state.clients,
                onClientClick = { clientId ->
                    onAction(ClientListAction.OnClientClick(clientId))
                },
                modifier = Modifier.padding(DesignToken.padding.large),
                fetchImage = {
                    onAction(ClientListAction.FetchImage(it))
                },
                images = state.clientImages,
            )
        }
        if (state.clientsFlow != null) {
            Column(
                Modifier.fillMaxSize().padding(paddingValues),
            ) {
                if (state.dialogState == null) {
                    ClientActions(
                        state = state,
                        onAction = onAction,
                    )
                }
                LazyColumnForClientListApi(
                    pagingFlow = state.clientsFlow,
                    onRefresh = {
                        onAction(ClientListAction.RefreshClients)
                    },
                    onClientSelect = {
                        onAction(ClientListAction.OnClientClick(it))
                    },
                    modifier = Modifier,
                    fetchImage = {
                        onAction(ClientListAction.FetchImage(it))
                    },
                    images = state.clientImages,
                )
            }
        }
    }
}

@Composable
fun ClientListContent(
    clientsList: List<ClientEntity>,
    onClientClick: (Int) -> Unit,
    fetchImage: (Int) -> Unit,
    images: Map<Int, ByteArray?>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(items = clientsList) { client ->
            LaunchedEffect(client.id) {
                fetchImage(client.id)
            }
            ClientItem(
                client = client,
                byteArray = images[client.id],
                onClientClick = onClientClick,
            )
        }
    }
}

@Composable
fun ClientItem(client: ClientEntity, byteArray: ByteArray?, onClientClick: (Int) -> Unit) {
    MifosRowCard(
        title = client.displayName ?: "",
        byteArray = byteArray,
        leftValues = listOf(
            TextUtil(
                text = stringResource(
                    Res.string.account_number_prefix,
                    (client.accountNo ?: stringResource(Res.string.string_not_available)),
                ),
                style = MifosTypography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            ),
            TextUtil(
                text = client.officeName ?: stringResource(Res.string.string_not_available),
                style = MifosTypography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            ),
        ),
        rightValues = buildList {
            client.status?.value?.let { status ->
                add(
                    TextUtil(
                        text = status,
                        style = MifosTypography.labelSmall,
                        color = when (status) {
                            "Active" -> AppColors.customEnable
                            "Pending" -> AppColors.customYellow
                            else -> MaterialTheme.colorScheme.error
                        },
                    ),
                )
            }

            client.externalId?.takeIf { it.isNotBlank() }?.let { externalId ->
                add(
                    TextUtil(
                        text = externalId,
                        style = MifosTypography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                    ),
                )
            }
        },
        modifier = Modifier
            .clickable {
                onClientClick(client.id)
            }
            .padding(DesignToken.padding.large),
    )
}

@Composable
private fun ClientListDialogs(
    dialogState: ClientListState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is ClientListState.DialogState.Loading -> MifosProgressIndicator()

        is ClientListState.DialogState.Error -> {
            MifosBasicDialog(
                visibilityState = BasicDialogState.Shown(
                    message = dialogState.message,
                ),
                onDismissRequest = onDismissRequest,
            )
        }

        null -> Unit
    }
}

@Composable
internal expect fun LazyColumnForClientListApi(
    pagingFlow: Flow<PagingData<ClientEntity>>,
    onRefresh: () -> Unit,
    onClientSelect: (Int) -> Unit,
    fetchImage: (Int) -> Unit,
    images: Map<Int, ByteArray?>,
    modifier: Modifier = Modifier,
)
