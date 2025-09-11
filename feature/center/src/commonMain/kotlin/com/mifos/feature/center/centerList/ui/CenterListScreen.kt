/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.centerList.ui

import androidclient.feature.center.generated.resources.Res
import androidclient.feature.center.generated.resources.feature_center_failed_to_load_db_centers
import androidclient.feature.center.generated.resources.feature_center_ic_done_all_black_24dp
import androidclient.feature.center.generated.resources.feature_center_sync
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import coil3.compose.AsyncImage
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.SelectionModeTopAppBar
import com.mifos.feature.center.syncCentersDialog.SyncCenterDialogScreen
import com.mifos.room.entities.group.CenterEntity
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CenterListScreen(
    createNewCenter: () -> Unit,
    onCenterSelect: (Int) -> Unit,
    viewModel: CenterListViewModel = koinViewModel(),
) {
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val state by viewModel.centerListUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.getCenterList()
    }

    CenterListScreen(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CenterListScreen(
    state: CenterListUiState,
    createNewCenter: () -> Unit,
    onRefresh: () -> Unit,
    refreshState: Boolean,
    onCenterSelect: (Int) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val selectedItems = remember { SelectedItemsState() }
    val isInSelectionMode = selectedItems.size() > 0

    val resetSelectionMode = {
        selectedItems.clear()
    }
    val sync = rememberSaveable {
        mutableStateOf(false)
    }

    val pullRefreshState = rememberPullToRefreshState()

    MifosScaffold(
        modifier = Modifier,
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
                            Text(text = stringResource(Res.string.feature_center_sync))
                        }
                    },
                )
            }
        },
        snackbarHostState = snackbarHostState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { createNewCenter() },
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            PullToRefreshBox(
                state = pullRefreshState,
                onRefresh = onRefresh,
                isRefreshing = refreshState,
            ) {
                when (state) {
                    is CenterListUiState.Error -> {
                        MifosSweetError(message = stringResource(state.message)) {
                            onRefresh()
                        }
                    }

                    is CenterListUiState.Loading -> {
                        MifosProgressIndicator()
                    }

                    is CenterListUiState.CenterList -> {
                        CenterListContent(
                            state = state,
                            isInSelectionMode = isInSelectionMode,
                            selectedItems = selectedItems,
                            onRefresh = {
                                onRefresh()
                            },
                            onCenterSelect = {
                                onCenterSelect(it)
                            },
                        )
                    }

                    is CenterListUiState.CenterListDb -> CenterListDbContent(state.centers)
                }
                if (sync.value) {
                    SyncCenterDialogScreen(
                        dismiss = {
                            sync.value = false
                            selectedItems.clear()
                            resetSelectionMode()
                        },
                        hide = { sync.value = false },
                        centers = selectedItems.toList(),
                    )
                }
            }
        }
    }
}

class SelectedItemsState(initialSelectedItems: List<CenterEntity> = emptyList()) {
    private val _selectedItems = mutableStateListOf<CenterEntity>().also { it.addAll(initialSelectedItems) }
    val selectedItems: State<List<CenterEntity>> = derivedStateOf { _selectedItems }

    fun add(item: CenterEntity) {
        _selectedItems.add(item)
    }

    fun remove(item: CenterEntity) {
        _selectedItems.remove(item)
    }
    fun toList(): List<CenterEntity> {
        return _selectedItems.toList()
    }
    fun contains(item: CenterEntity): Boolean {
        return _selectedItems.contains(item)
    }

    fun clear() {
        _selectedItems.clear()
    }

    fun size(): Int {
        return _selectedItems.size
    }
    fun isEmpty(): Boolean {
        return _selectedItems.isEmpty()
    }
}

@Composable
expect fun CenterListContent(
    state: CenterListUiState,
    isInSelectionMode: Boolean,
    selectedItems: SelectedItemsState,
    onRefresh: () -> Unit,
    onCenterSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CenterCard(
    center: CenterEntity,
    selected: Boolean,
    isInSelectionMode: Boolean,
    onSelect: (CenterEntity) -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    unselectedColor: Color = MaterialTheme.colorScheme.surface,
    onClick: (CenterEntity) -> Unit,
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
                    if (center.sync) {
                        AsyncImage(
                            modifier = Modifier.size(20.dp),
                            model = Res.drawable.feature_center_ic_done_all_black_24dp,
                            contentDescription = null,
                        )
                    }

                    Icon(
                        imageVector = MifosIcons.ArrowForward,
                        contentDescription = "Arrow forward icon",
                    )
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Unspecified,
            ),
        )
    }
}

@Composable
fun CenterListDbContent(
    centerList: List<CenterEntity>?,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    if (centerList != null) {
        LazyColumn(
            modifier = modifier,
            state = lazyListState,
        ) {
            items(
                centerList.size,
            ) { index ->
                CenterCard(
                    center = centerList[index],
                    selected = false,
                    isInSelectionMode = false,
                    onSelect = {},
                    onClick = {},
                )
            }
        }
    }
}

val sampleCenterListDb = List(10) {
    CenterEntity(
        name = "Center $it",
        officeId = it,
        officeName = "Office $it",
        staffId = it,
        staffName = "Staff $it",
        active = it % 2 == 0,
    )
}

val sampleCenterList = flowOf(PagingData.from(sampleCenterListDb))

class CenterListUiStateProvider : PreviewParameterProvider<CenterListUiState> {
    override val values = sequenceOf(
        CenterListUiState.CenterListDb(sampleCenterListDb),
        CenterListUiState.Error(Res.string.feature_center_failed_to_load_db_centers),
        CenterListUiState.CenterList(sampleCenterList),
    )
}

@Preview
@Composable
fun CenterListScreenPreview(
    @PreviewParameter(CenterListUiStateProvider::class) state: CenterListUiState,
) {
    CenterListScreen(
        state = state,
        createNewCenter = {},
        onRefresh = {},
        refreshState = false,
        onCenterSelect = {},
    )
}
