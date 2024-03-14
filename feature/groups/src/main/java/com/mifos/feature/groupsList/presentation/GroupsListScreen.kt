package com.mifos.feature.groupsList.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosFAB
import com.mifos.core.designsystem.component.MifosPagingAppendProgress
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.SelectionModeTopAppBar
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.PaddingLarge
import com.mifos.core.designsystem.theme.PaddingMedium
import com.mifos.core.designsystem.theme.PaddingSmall
import com.mifos.core.objects.group.Group

@Composable
fun GroupsListRoute(
    onAddGroupClick: () -> Unit,
    onGroupClick: (Group) -> Unit,
    onSyncClick: (List<Group>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GroupsListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isRefreshing by viewModel.isLoading.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    GroupsListScreen(
        modifier = modifier,
        uiState = uiState,
        swipeRefreshState = swipeRefreshState,
        onRefresh = viewModel::refreshData,
        onAddGroupClick = onAddGroupClick,
        onGroupClick = onGroupClick,
        onSyncClick = onSyncClick,
    )
}


@Composable
fun GroupsListScreen(
    modifier: Modifier = Modifier,
    uiState: GroupsListState,
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit,
    onAddGroupClick: () -> Unit,
    onGroupClick: (Group) -> Unit,
    onSyncClick: (List<Group>) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val selectedItems = remember {
        mutableStateListOf<Group>()
    }

    BackHandler(
        enabled = selectedItems.isNotEmpty()
    ) {
        selectedItems.clear()
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        floatingActionButton = {
            MifosFAB(icon = Icons.Default.Add, onClick = onAddGroupClick)
        },
        floatingActionButtonPosition = FabPosition.End,
        topBar = {
            AnimatedVisibility(
                visible = selectedItems.isNotEmpty(),
                enter = fadeIn(tween(500)),
                exit = fadeOut(tween(500))
            ) {
                SelectionModeTopAppBar(
                    itemCount = selectedItems.size,
                    syncClicked = { onSyncClick(selectedItems.toList()) },
                    resetSelectionMode = {
                        selectedItems.clear()
                    }
                )
            }
        },
    ) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = onRefresh,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
                    .padding(PaddingSmall)
            ) {
                Crossfade(
                    targetState = uiState,
                    label = "GroupsList::State",
                ) { state ->
                    when (state) {
                        is GroupsListState.Empty -> {}

                        is GroupsListState.Error -> {
                            MifosSweetError(
                                message = state.message,
                                onclick = onRefresh,
                            )
                        }

                        is GroupsListState.GroupsFromAPI -> {
                            GroupsListItemFromAPI(
                                lazyListState = lazyListState,
                                data = state.groups.collectAsLazyPagingItems(),
                                doesSelected = {
                                    selectedItems.contains(it)
                                },
                                doesInSelectionMode = {
                                    selectedItems.isNotEmpty()
                                },
                                onGroupClick = onGroupClick,
                                onSelectItem = {
                                    if (selectedItems.contains(it)) {
                                        selectedItems.remove(it)
                                    }else {
                                        selectedItems.add(it)
                                    }
                                },
                                onRefresh = onRefresh
                            )
                        }

                        is GroupsListState.GroupsFromLocalDB -> {
                            GroupsListItemFromLocalDB(
                                lazyListState = lazyListState,
                                data = state,
                                doesSelected = {
                                    selectedItems.contains(it)
                                },
                                doesInSelectionMode = {
                                    selectedItems.isNotEmpty()
                                },
                                onGroupClick = onGroupClick,
                                onSelectItem = {
                                    if (selectedItems.contains(it)) {
                                        selectedItems.remove(it)
                                    }else {
                                        selectedItems.add(it)
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupsListItemFromAPI(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    data: LazyPagingItems<Group>,
    doesSelected: (Group) -> Boolean,
    doesInSelectionMode: () -> Boolean,
    onGroupClick: (Group) -> Unit,
    onSelectItem: (Group) -> Unit,
    onRefresh: () -> Unit,
) {
    Crossfade(
        targetState = data.loadState.refresh,
        label = "Load::States"
    ) {
        when (it) {
            is LoadState.Error -> {
                MifosSweetError(
                    message = "Unable to fetch data from server.",
                    onclick = onRefresh
                )
            }

            is LoadState.Loading -> MifosCircularProgress()
            is LoadState.NotLoading -> {}
        }
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
    ) {
        items(
            count = data.itemCount,
            key = {
                data[it]?.id ?: it
            }
        ) { index ->
            data[index]?.let { group ->
                GroupItem(
                    group = group,
                    doesSelected = doesSelected,
                    doesInSelectionMode = doesInSelectionMode,
                    onGroupClick = onGroupClick,
                    onSelectItem = onSelectItem
                )
            }
        }

        when (data.loadState.append) {
            is LoadState.Error -> {

            }

            is LoadState.Loading -> {
                item {
                    MifosPagingAppendProgress()
                }
            }

            is LoadState.NotLoading -> Unit
        }

        when (data.loadState.append.endOfPaginationReached) {
            true -> {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        text = "No More Groups Available", style = TextStyle(
                            fontSize = 14.sp
                        ), color = DarkGray, textAlign = TextAlign.Center
                    )
                }
            }

            false -> Unit
        }
    }
}


@Composable
fun GroupsListItemFromLocalDB(
    modifier: Modifier = Modifier,
    data: GroupsListState.GroupsFromLocalDB,
    lazyListState: LazyListState,
    doesSelected: (Group) -> Boolean,
    doesInSelectionMode: () -> Boolean,
    onGroupClick: (Group) -> Unit,
    onSelectItem: (Group) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(PaddingSmall),
        state = lazyListState
    ) {
        items(
            items = data.groups,
            key = {
                it.id ?: 0
            }
        ){
            GroupItem(
                group = it,
                doesSelected = doesSelected,
                doesInSelectionMode = doesInSelectionMode,
                onGroupClick = onGroupClick,
                onSelectItem = onSelectItem
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupItem(
    modifier: Modifier = Modifier,
    group: Group,
    doesSelected: (Group) -> Boolean,
    doesInSelectionMode: () -> Boolean,
    onGroupClick: (Group) -> Unit,
    onSelectItem: (Group) -> Unit,
) {
    val borderStroke = if (doesSelected(group)) BorderStroke(1.dp, BluePrimary) else{
        CardDefaults.outlinedCardBorder()
    }
    val containerColor = if (doesSelected(group)) BlueSecondary else Color.Unspecified

    group.name?.let {
        OutlinedCard(
            modifier = modifier
                .fillMaxWidth()
                .padding(PaddingMedium)
                .height(70.dp)
                .clip(RoundedCornerShape(PaddingMedium))
                .combinedClickable(
                    onClick = {
                        if (doesInSelectionMode()) {
                            onSelectItem(group)
                        } else {
                            onGroupClick(group)
                        }
                    },
                    onLongClick = {
                        onSelectItem(group)
                    }
                ),
            shape = RoundedCornerShape(PaddingMedium),
            colors = CardDefaults.outlinedCardColors(
                containerColor = containerColor
            ),
            border = borderStroke
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(PaddingLarge),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium
                )

                if (group.sync) {
                    Icon(imageVector = Icons.Default.DoneAll, contentDescription = "Sync")
                }
            }
        }
    }
}