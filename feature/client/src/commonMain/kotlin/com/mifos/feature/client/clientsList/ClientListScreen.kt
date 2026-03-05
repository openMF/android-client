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
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_clients
import androidclient.feature.client.generated.resources.feature_client_failed_to_more_clients
import androidclient.feature.client.generated.resources.feature_client_no_more_clients_available
import androidclient.feature.client.generated.resources.string_not_available
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.designsystem.component.BasicDialogState
import com.mifos.core.designsystem.component.MifosBasicDialog
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosPagingAppendProgress
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosRowCard
import com.mifos.core.ui.util.EventsEffect
import com.mifos.core.ui.util.TextUtil
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ClientListScreen(
    createNewClient: () -> Unit,
    onClientClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClientListViewModel = koinViewModel(),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    if (state.isFilterVisible) {
        FilterBottomSheet(
            onDismissRequest = { viewModel.trySendAction(ClientListAction.ToggleFilterVisibility) },
            sheetState = sheetState,
            handleFilterClick = { value, filterType ->
                viewModel.trySendAction(ClientListAction.HandleFilterClick(value, filterType))
            },
            selectedStatuses = state.selectedStatus,
            selectedSort = state.sort,
            handleSortClick = { viewModel.trySendAction(ClientListAction.HandleSortClick(it)) },
            officeNames = state.officeNames,
            selectedOffices = state.selectedOffices,
            clearFilters = { viewModel.trySendAction(ClientListAction.ClearFilters) },
        )
    }

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
        toggleFilterVisibility = { viewModel.trySendAction(ClientListAction.ToggleFilterVisibility) },
        onUpdateOffices = { viewModel.trySendAction(ClientListAction.OnUpdateOffice(it)) },
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
    toggleFilterVisibility: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(KptTheme.spacing.md),
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
                    horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
                ) {
                    Text(
                        text = "Clients",
                        style = MifosTypography.titleMediumEmphasized,
                        color = KptTheme.colorScheme.primary,
                    )
                    Icon(
                        imageVector = MifosIcons.Add,
                        contentDescription = null,
                        tint = KptTheme.colorScheme.primary,
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
        Icon(
            imageVector = MifosIcons.Filter,
            contentDescription = null,
            modifier = Modifier
                .size(DesignToken.sizes.iconAverage)
                .clickable {
                    toggleFilterVisibility()
                },
        )
    }
}

@Composable
private fun ClientListContentScreen(
    state: ClientListState,
    modifier: Modifier = Modifier,
    onAction: (ClientListAction) -> Unit,
    toggleFilterVisibility: () -> Unit,
    onUpdateOffices: (List<String?>) -> Unit,
    isRefreshing: Boolean = false,
) {
    val pullRefreshState = rememberPullToRefreshState()
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        if (!state.isEmpty) {
            ClientActions(
                state = state,
                onAction = onAction,
                toggleFilterVisibility = toggleFilterVisibility,
            )
        }

        PullToRefreshBox(
            state = pullRefreshState,
            onRefresh = { onAction(ClientListAction.RefreshClients) },
            isRefreshing = isRefreshing,
        ) {
            when {
                state.clients.isNotEmpty() -> {
                    ClientListContent(
                        clientsList = state.clients,
                        onClientClick = { clientId ->
                            onAction(ClientListAction.OnClientClick(clientId))
                        },
                        modifier = Modifier.padding(KptTheme.spacing.md),
                        fetchImage = {
                            onAction(ClientListAction.FetchImage(it))
                        },
                        images = state.clientImages,
                    )
                }

                state.clientsFlow != null -> {
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
                        sort = state.sort,
                        onUpdateOffices = onUpdateOffices,
                    )
                }

                else -> {
                    MifosEmptyCard("No clients found")
                }
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
                color = KptTheme.colorScheme.secondary,
            ),
            TextUtil(
                text = client.officeName ?: stringResource(Res.string.string_not_available),
                style = MifosTypography.bodySmall,
                color = KptTheme.colorScheme.secondary,
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
                            else -> KptTheme.colorScheme.error
                        },
                    ),
                )
            }

            client.externalId?.takeIf { it.isNotBlank() }?.let { externalId ->
                add(
                    TextUtil(
                        text = externalId,
                        style = MifosTypography.labelSmall,
                        color = KptTheme.colorScheme.secondary,
                    ),
                )
            }
        },
        modifier = Modifier
            .clickable {
                onClientClick(client.id)
            }
            .padding(KptTheme.spacing.md),
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
internal fun LazyColumnForClientListApi(
    pagingFlow: Flow<PagingData<ClientEntity>>,
    onRefresh: () -> Unit,
    onClientSelect: (Int) -> Unit,
    fetchImage: (Int) -> Unit,
    images: Map<Int, ByteArray?>,
    modifier: Modifier,
    sort: SortTypes?,
    onUpdateOffices: (List<String?>) -> Unit,
) {
    val clientPagingList = pagingFlow.collectAsLazyPagingItems()

    val items = clientPagingList.itemSnapshotList.items
    if (items.isNotEmpty()) {
        val offices = items.map { it.officeName }
            .distinct()
        LaunchedEffect(offices) { onUpdateOffices(offices) }
    }

    when (clientPagingList.loadState.refresh) {
        is LoadState.Error -> {
            MifosSweetError(message = stringResource(Res.string.feature_client_failed_to_fetch_clients)) {
                onRefresh()
            }
        }

        is LoadState.Loading -> MifosProgressIndicator()

        is LoadState.NotLoading -> Unit
    }

    if (sort != null) {
        val currentItems = clientPagingList.itemSnapshotList.items

        val sortedItems = when (sort) {
            SortTypes.NAME -> {
                currentItems.sortedBy { it.displayName?.lowercase() }
            }
            SortTypes.ACCOUNT_NUMBER -> {
                currentItems.sortedBy { it.accountNo }
            }
            SortTypes.EXTERNAL_ID -> {
                currentItems.sortedBy { it.externalId }
            }
            else -> currentItems
        }

        LazyColumn(
            modifier = modifier,
        ) {
            items(
                items = sortedItems,
                key = { client -> client.id },
            ) { client ->
                LaunchedEffect(client.id) {
                    fetchImage(client.id)
                }
                ClientItem(
                    client = client,
                    byteArray = images[client.id],
                    onClientClick = onClientSelect,
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier,
        ) {
            items(
                count = clientPagingList.itemCount,
                key = { index -> clientPagingList[index]?.id ?: index },
            ) { index ->
                clientPagingList[index]?.let { client ->
                    LaunchedEffect(client.id) {
                        fetchImage(client.id)
                    }
                    ClientItem(
                        client = client,
                        byteArray = images[client.id],
                        onClientClick = onClientSelect,
                    )
                }
            }

            when (clientPagingList.loadState.append) {
                is LoadState.Error -> {
                    item {
                        MifosSweetError(message = stringResource(Res.string.feature_client_failed_to_more_clients)) {
                            clientPagingList.retry()
                        }
                    }
                }

                is LoadState.Loading -> {
                    item {
                        MifosPagingAppendProgress()
                    }
                }

                is LoadState.NotLoading -> {
                    if (clientPagingList.loadState.append.endOfPaginationReached &&
                        clientPagingList.itemCount > 0
                    ) {
                        item {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = DesignToken.padding.extraExtraLarge),
                                text = stringResource(Res.string.feature_client_no_more_clients_available),
                                style = MifosTypography.bodyMedium,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    handleFilterClick: (String, FilterType) -> Unit,
    selectedStatuses: List<String>,
    selectedSort: SortTypes?,
    handleSortClick: (SortTypes) -> Unit,
    officeNames: List<String?>,
    selectedOffices: List<String>,
    clearFilters: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = KptTheme.colorScheme.background,
    ) {
        val sortTypes = listOf(SortTypes.NAME, SortTypes.ACCOUNT_NUMBER, SortTypes.EXTERNAL_ID)
        val statusTypes = listOf("Active", "Pending", "Closed")

        Column(
            modifier = Modifier.padding(DesignToken.padding.dp15),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
                    .padding(DesignToken.padding.dp10),
            ) {
                Text(
                    text = "Filters",
                    style = MifosTypography.titleLargeEmphasized,
                    color = KptTheme.colorScheme.primary,
                )
                Row {
                    IconButton(
                        onClick = {
                            clearFilters()
                            onDismissRequest()
                        },
                    ) {
                        Icon(
                            imageVector = MifosIcons.Redo,
                            contentDescription = "Clear",
                        )
                    }
                    IconButton(
                        onClick = onDismissRequest,
                    ) {
                        Icon(
                            imageVector = MifosIcons.Check,
                            contentDescription = "Apply",
                        )
                    }
                }
            }
            HorizontalDivider(Modifier.fillMaxWidth(), thickness = 1.5.dp)
            Column(
                modifier = Modifier.padding(DesignToken.padding.dp10),
            ) {
                var isExpanded by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable(onClick = {
                            isExpanded = !isExpanded
                        }),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Sort by",
                        style = MifosTypography.titleMediumEmphasized,
                    )
                    if (isExpanded) {
                        Icon(
                            imageVector = MifosIcons.ArrowDropUp,
                            contentDescription = "",
                        )
                    } else {
                        Icon(
                            imageVector = MifosIcons.ArrowDropDown,
                            contentDescription = "",
                        )
                    }
                }
                AnimatedVisibility(
                    visible = isExpanded,
                ) {
                    Column {
                        sortTypes.forEach { sort ->
                            val isSelected = (sort == selectedSort)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Spacer(modifier = Modifier.width(DesignToken.spacing.dp10))
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        handleSortClick(sort)
                                    },
                                )
                                Text(text = sort.value)
                            }
                        }
                    }
                }
            }
            HorizontalDivider(Modifier.fillMaxWidth(), thickness = 1.5.dp)
            Column(
                modifier = Modifier.padding(DesignToken.padding.dp10),
            ) {
                var isExpanded by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable(onClick = {
                            isExpanded = !isExpanded
                        }),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Account Status",
                        style = MifosTypography.titleMediumEmphasized,
                    )
                    if (isExpanded) {
                        Icon(
                            imageVector = MifosIcons.ArrowDropUp,
                            contentDescription = "",
                        )
                    } else {
                        Icon(
                            imageVector = MifosIcons.ArrowDropDown,
                            contentDescription = "",
                        )
                    }
                }
                AnimatedVisibility(
                    visible = isExpanded,
                ) {
                    Column {
                        statusTypes.forEach { status ->
                            val isChecked = selectedStatuses.contains(status)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Spacer(modifier = Modifier.width(DesignToken.spacing.dp10))
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { handleFilterClick(status, FilterType.STATUS) },
                                )
                                Text(text = status)
                            }
                        }
                    }
                }
            }
            HorizontalDivider(Modifier.fillMaxWidth(), thickness = 1.5.dp)

            Column(
                modifier = Modifier.padding(DesignToken.spacing.dp10),
            ) {
                var isExpanded by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable(onClick = {
                            isExpanded = !isExpanded
                        }),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        "Office Name",
                        style = MifosTypography.titleMediumEmphasized,
                    )
                    if (isExpanded) {
                        Icon(
                            imageVector = MifosIcons.ArrowDropUp,
                            contentDescription = "",
                        )
                    } else {
                        Icon(
                            imageVector = MifosIcons.ArrowDropDown,
                            contentDescription = "",
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isExpanded,
                ) {
                    Column {
                        officeNames.forEach { name ->
                            val isChecked = selectedOffices.contains(name)
                            if (name != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Spacer(modifier = Modifier.width(DesignToken.spacing.dp10))
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { handleFilterClick(name, FilterType.OFFICE) },
                                    )
                                    Text(text = name)
                                }
                            }
                        }
                    }
                }
            }
            HorizontalDivider(Modifier.fillMaxWidth(), thickness = 1.5.dp)
        }
    }
}
