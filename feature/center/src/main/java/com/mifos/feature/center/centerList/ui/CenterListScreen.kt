/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package com.mifos.feature.center.centerList.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosPagingAppendProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.objects.group.Center
import com.mifos.core.ui.components.SelectionModeTopAppBar
import com.mifos.feature.center.R
import com.mifos.feature.center.syncCentersDialog.SyncCenterDialogScreen
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun CenterListScreen(
    paddingValues: PaddingValues,
    createNewCenter: () -> Unit,
    onCenterSelect: (Int) -> Unit,
    viewModel: CenterListViewModel = hiltViewModel(),
) {
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val state by viewModel.centerListUiState.collectAsStateWithLifecycle()

    CenterListScreen(
        paddingValues = paddingValues,
        state = state,
        createNewCenter = createNewCenter,
        onRefresh = {
            viewModel.refreshCenterList()
        },
        refreshState = refreshState,
        onCenterSelect = onCenterSelect,
        //   syncClicked = syncClicked
    )
}

@Composable
internal fun CenterListScreen(
    paddingValues: PaddingValues,
    state: CenterListUiState,
    createNewCenter: () -> Unit,
    onRefresh: () -> Unit,
    refreshState: Boolean,
    onCenterSelect: (Int) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val selectedItems = remember { SelectedItemsState() }
    val sync = rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(enabled = selectedItems.size() > 0) {
        selectedItems.clear()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshState,
        onRefresh = onRefresh,
    )

    MifosScaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {
            if (selectedItems.size() > 0) {
                SelectionModeTopAppBar(
                    itemCount = selectedItems.size(),
                    resetSelectionMode = selectedItems::clear,
                    actions = {
                        FilledTonalButton(
                            onClick = {
                                sync.value = true
                            },
                        ) {
                            Icon(
                                imageVector = MifosIcons.sync,
                                contentDescription = "Sync Items",
                            )
                            Text(text = stringResource(id = R.string.feature_center_sync))
                        }
                    },
                )
            }
        },
        snackbarHostState = snackbarHostState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = createNewCenter,
            ) {
                Icon(
                    imageVector = MifosIcons.Add,
                    contentDescription = null,
                )
            }
        },
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue),
            verticalArrangement = Arrangement.Center,
        ) {
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                when (state) {
                    is CenterListUiState.Error -> {
                        MifosSweetError(message = stringResource(id = state.message)) {
                            onRefresh()
                        }
                    }

                    is CenterListUiState.Loading -> {
                        MifosCircularProgress()
                    }

                    is CenterListUiState.CenterList -> {
                        CenterListContent(
                            modifier = Modifier,
                            centerPagingList = state.centers.collectAsLazyPagingItems(),
                            selectedItems = selectedItems,
                            onRefresh = onRefresh,
                            onCenterClick = {
                                onCenterSelect(it)
                            },
                        )
                    }

                    is CenterListUiState.CenterListDb -> {
                        CenterListDbContent(centerList = state.centers)
                    }
                }
                if (sync.value) {
                    SyncCenterDialogScreen(
                        dismiss = {
                            sync.value = false
                            selectedItems.clear()
                        },
                        hide = { sync.value = false },
                        centers = selectedItems.toList(),
                    )
                }
                PullRefreshIndicator(
                    refreshing = refreshState,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        }
    }
}

private class SelectedItemsState(initialSelectedItems: List<Center> = emptyList()) {
    private val selectedItems = mutableStateListOf<Center>().also {
        it.addAll(initialSelectedItems)
    }

    fun add(item: Center) {
        if (item in selectedItems) {
            selectedItems.remove(item)
        } else {
            selectedItems.add(item)
        }
    }

    fun toList(): List<Center> {
        return selectedItems.toList()
    }

    fun contains(item: Center): Boolean {
        return selectedItems.contains(item)
    }

    fun clear() {
        selectedItems.clear()
    }

    fun size(): Int {
        return selectedItems.size
    }
}

@Composable
private fun CenterListContent(
    centerPagingList: LazyPagingItems<Center>,
    selectedItems: SelectedItemsState,
    onRefresh: () -> Unit,
    onCenterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    when (centerPagingList.loadState.refresh) {
        is LoadState.Error -> {
            MifosSweetError(
                message = stringResource(id = R.string.feature_center_error_loading_centers),
                onclick = onRefresh,
            )
        }

        is LoadState.Loading -> MifosCircularProgress()

        is LoadState.NotLoading -> Unit
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            count = centerPagingList.itemCount,
            key = {
                centerPagingList[it]?.id ?: it
            },
        ) { index ->
            val center: Center = centerPagingList[index]!!

            CenterCard(
                center = center,
                selected = selectedItems.contains(center),
                isInSelectionMode = selectedItems.size() > 0,
                onSelect = {
                    selectedItems.add(it)
                },
                onClick = {
                    onCenterClick(it.id ?: 0)
                },
            )
        }

        when (centerPagingList.loadState.append) {
            is LoadState.Error -> {}

            is LoadState.Loading -> {
                item {
                    MifosPagingAppendProgress()
                }
            }

            is LoadState.NotLoading -> Unit
        }
        when (centerPagingList.loadState.append.endOfPaginationReached) {
            true -> {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        text = stringResource(id = R.string.feature_center_no_more_centers),
                        style = TextStyle(
                            fontSize = 14.sp,
                        ),
                        color = DarkGray,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            false -> Unit
        }
    }
}

@Composable
private fun CenterListDbContent(
    centerList: List<Center>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
    ) {
        items(
            items = centerList,
            key = {
                it.id ?: it.hashCode()
            },
        ) { center ->
            CenterCard(
                center = center,
                selected = false,
                isInSelectionMode = false,
                onSelect = {},
                onClick = {},
            )
        }
    }
}

@Composable
private fun CenterCard(
    center: Center,
    selected: Boolean,
    isInSelectionMode: Boolean,
    onSelect: (Center) -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    unselectedColor: Color = MaterialTheme.colorScheme.surface,
    onClick: (Center) -> Unit,
) {
    val containerColor = if (selected) selectedColor else unselectedColor

    OutlinedCard(
        modifier = modifier
            .clip(CardDefaults.outlinedShape)
            .combinedClickable(
                onClick = {
                    if (isInSelectionMode) {
                        onSelect(center)
                    } else {
                        onClick(center)
                    }
                },
                onLongClick = {
                    onSelect(center)
                },
            ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
    ) {
        ListItem(
            leadingContent = {
                Canvas(
                    modifier = Modifier.size(16.dp),
                    onDraw = {
                        drawCircle(
                            color = if (center.active == true) Color.Green else Color.Red,
                        )
                    },
                )
            },
            headlineContent = {
                Text(text = center.name.toString())
            },
            supportingContent = center.accountNo?.let {
                { Text(text = it) }
            },
            overlineContent = center.officeName?.let {
                { Text(text = it) }
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (center.sync == true) {
                        AsyncImage(
                            modifier = Modifier.size(20.dp),
                            model = R.drawable.feature_center_ic_done_all_black_24dp,
                            contentDescription = null,
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = null,
                    )
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Unspecified,
            ),
        )
    }
}

class CenterListUiStateProvider : PreviewParameterProvider<CenterListUiState> {
    override val values: Sequence<CenterListUiState>
        get() = sequenceOf(
            CenterListUiState.Loading,
            CenterListUiState.Error(R.string.feature_center_error_loading_centers),
            CenterListUiState.CenterListDb(sampleCenterListDb),
            CenterListUiState.CenterList(sampleCenterList),
        )
}

@Preview(showBackground = true)
@Composable
private fun CenterListContentPreview() {
    CenterListContent(
        centerPagingList = sampleCenterList.collectAsLazyPagingItems(),
        selectedItems = SelectedItemsState(),
        onRefresh = {},
        onCenterClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun CenterListDbContentPreview() {
    CenterListDbContent(sampleCenterListDb)
}

@Preview(showBackground = true)
@Composable
private fun CenterListScreenPreview(
    @PreviewParameter(CenterListUiStateProvider::class)
    centerListUiState: CenterListUiState,
) {
    CenterListScreen(
        paddingValues = PaddingValues(),
        state = centerListUiState,
        createNewCenter = {},
        onRefresh = {},
        refreshState = false,
        onCenterSelect = {},
    )
}

val sampleCenterListDb = List(10) {
    Center(
        name = "Center $it",
        officeId = it,
        officeName = "Office $it",
        staffId = it,
        staffName = "Staff $it",
        active = it % 2 == 0,
    )
}

val sampleCenterList = flowOf(PagingData.from(sampleCenterListDb))
