package com.mifos.feature.groupsList.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosPaginationSweetError
import com.mifos.core.designsystem.component.MifosPagingAppendProgress
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.objects.group.Group
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.components.MifosFAB
import com.mifos.core.ui.components.SelectionModeTopAppBar
import com.mifos.feature.groups.R

@Composable
fun GroupsListRoute(
    onAddGroupClick: () -> Unit,
    onGroupClick: (Group) -> Unit,
    onSyncClick: (List<Group>) -> Unit,
    viewModel: GroupsListViewModel = hiltViewModel()
) {
    val data = viewModel.data.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = data.loadState.refresh is LoadState.Loading
    )

    val selectedItems = remember {
        mutableStateListOf<Group>()
    }

    BackHandler(
        enabled = selectedItems.isNotEmpty()
    ) {
        selectedItems.clear()
    }

    GroupsListScreen(
        modifier = Modifier
            .fillMaxSize(),
        lazyListState = lazyListState,
        swipeRefreshState = swipeRefreshState,
        selectedItems = selectedItems,
        data = data,
        onAddGroupClick = onAddGroupClick,
        onGroupClick = onGroupClick,
        onSyncClick = onSyncClick,
        onSelectItem = {
            if (selectedItems.contains(it)) {
                selectedItems.remove(it)
            } else {
                selectedItems.add(it)
            }
        },
        resetSelectionMode = {
            selectedItems.clear()
        },
    )
}

@Composable
fun GroupsListScreen(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    swipeRefreshState: SwipeRefreshState,
    selectedItems: List<Group>,
    data: LazyPagingItems<Group>,
    onAddGroupClick: () -> Unit,
    onGroupClick: (Group) -> Unit,
    onSyncClick: (List<Group>) -> Unit,
    onSelectItem: (Group) -> Unit,
    resetSelectionMode: () -> Unit,
) {
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
                exit = fadeOut(tween(500)),
            ) {
                SelectionModeTopAppBar(
                    modifier = Modifier
                        .semantics {
                            contentDescription = "GroupList::ContextualTopAppBar"
                        },
                    itemCount = selectedItems.size,
                    syncClicked = { onSyncClick(selectedItems.toList()) },
                    resetSelectionMode = resetSelectionMode
                )
            }
        },
    ) { paddingValues ->
        SwipeRefresh(
            modifier = Modifier.semantics {
                contentDescription = "SwipeRefresh::GroupList"
            },
            state = swipeRefreshState,
            onRefresh = { data.refresh() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = lazyListState,
                verticalArrangement = if (data.itemCount < 1) Arrangement.Center else Arrangement.Top
            ) {
                when (data.loadState.refresh) {
                    is LoadState.Error -> {
                        item {
                            MifosSweetError(
                                message = stringResource(id = R.string.failed_to_fetch_groups),
                                onclick = { data.refresh() }
                            )
                        }
                    }

                    is LoadState.Loading -> {
                        item {
                            MifosCircularProgress("GroupItems::Loading")
                        }
                    }

                    is LoadState.NotLoading -> {
                        if (data.itemCount < 1) {
                            item {
                                MifosEmptyUi(
                                    text = stringResource(id = R.string.no_more_groups_available)
                                )
                            }
                        }
                    }
                }

                items(
                    count = data.itemCount
                ) { index ->
                    data[index]?.let { group ->
                        GroupItem(
                            group = group,
                            doesSelected = selectedItems.contains(group),
                            inSelectionMode = selectedItems.isNotEmpty(),
                            onGroupClick = {
                                onGroupClick(group)
                            },
                            onSelectItem = {
                                onSelectItem(group)
                            }
                        )
                    }
                }

                when (data.loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            MifosPagingAppendProgress()
                        }
                    }

                    is LoadState.Error -> {
                        item {
                            MifosPaginationSweetError {
                                data.retry()
                            }
                        }
                    }

                    is LoadState.NotLoading -> {
                        if (data.loadState.append.endOfPaginationReached) {
                            item {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(6.dp),
                                    text = stringResource(id = R.string.no_more_groups_available),
                                    style = TextStyle(fontSize = 14.sp),
                                    color = DarkGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupItem(
    modifier: Modifier = Modifier,
    group: Group,
    doesSelected: Boolean,
    inSelectionMode: Boolean,
    onGroupClick: () -> Unit,
    onSelectItem: () -> Unit,
) {
    val borderStroke = if (doesSelected) BorderStroke(1.dp, BluePrimary) else {
        CardDefaults.outlinedCardBorder()
    }
    val containerColor = if (doesSelected) BlueSecondary else Color.Unspecified

    group.name?.let {
        OutlinedCard(
            modifier = modifier
                .testTag(it)
                .fillMaxWidth()
                .padding(8.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    onClick = {
                        if (inSelectionMode) {
                            onSelectItem()
                        } else {
                            onGroupClick()
                        }
                    },
                    onLongClick = onSelectItem
                ),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = containerColor
            ),
            border = borderStroke
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
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