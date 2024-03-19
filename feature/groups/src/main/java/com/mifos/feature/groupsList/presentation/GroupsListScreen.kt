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
import androidx.compose.ui.res.stringResource
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
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mifos.core.designsystem.component.MifosCircularProgress
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
fun GroupsListScreen(
    onAddGroupClick: () -> Unit,
    onGroupClick: (Group) -> Unit,
    onSyncClick: (List<Group>) -> Unit,
    viewModel: GroupsListViewModel = hiltViewModel()
) {
    val lazyListState = rememberLazyListState()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = isLoading
    )

    val selectedItems = remember {
        mutableStateListOf<Group>()
    }

    BackHandler(
        enabled = selectedItems.isNotEmpty()
    ) {
        selectedItems.clear()
    }

    Scaffold(
        modifier = Modifier
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
            onRefresh = viewModel::refreshData
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(4.dp)
            ) {
                Crossfade(
                    targetState = uiState,
                    label = "GroupsList::State",
                ) { state ->
                    when (state) {
                        is GroupsListState.Loading -> {
                            MifosCircularProgress()
                        }

                        is GroupsListState.ShowGroupsList -> {
                            GroupsListItem(
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
                                    } else {
                                        selectedItems.add(it)
                                    }
                                },
                                onRefresh = viewModel::refreshData
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupsListItem(
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
                    message = stringResource(id = R.string.failed_to_fetch_groups),
                    onclick = onRefresh
                )
            }

            is LoadState.Loading -> MifosCircularProgress()

            is LoadState.NotLoading -> {
                LazyColumn(
                    modifier = modifier,
                    state = lazyListState,
                ) {
                    items(
                        count = data.itemCount,
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

                    data.loadState.apply {
                        when {
                            data.itemCount < 1 -> {
                                item {
                                    MifosEmptyUi(
                                        text = stringResource(id = R.string.no_more_groups_available)
                                    )
                                }
                            }

                            data.loadState.append is LoadState.Loading -> {
                                item {
                                    MifosPagingAppendProgress()
                                }
                            }

                            data.loadState.append.endOfPaginationReached -> {
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
    val borderStroke = if (doesSelected(group)) BorderStroke(1.dp, BluePrimary) else {
        CardDefaults.outlinedCardBorder()
    }
    val containerColor = if (doesSelected(group)) BlueSecondary else Color.Unspecified

    group.name?.let {
        OutlinedCard(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(8.dp))
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