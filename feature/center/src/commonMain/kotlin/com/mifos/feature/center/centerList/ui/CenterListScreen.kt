/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.center.centerList.ui

import kpt.feature.center.generated.resources.Res
import kpt.feature.center.generated.resources.feature_center_ic_done_all_black_24dp
import kpt.feature.center.generated.resources.feature_center_no_more_centers
import kpt.feature.center.generated.resources.feature_center_sync
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImage
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.SelectionModeTopAppBar
import com.mifos.feature.center.syncCentersDialog.SyncCenterDialogScreen
import com.mifos.room.entities.group.CenterEntity
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kpt.core.base.designsystem.theme.LocalKptColors
import kpt.core.base.designsystem.theme.LocalKptSpacing
import kpt.core.base.store.paging.PagingScreenStream
import kpt.core.base.ui.paging.PagingScreenContent

@Composable
internal fun CenterListScreen(
    createNewCenter: () -> Unit,
    onCenterSelect: (Int) -> Unit,
    viewModel: CenterListViewModel = koinViewModel(),
) {
    CenterListScreen(
        pagingStream = viewModel.pagingStream,
        onRetry = viewModel::retry,
        createNewCenter = createNewCenter,
        onCenterSelect = onCenterSelect,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CenterListScreen(
    pagingStream: PagingScreenStream<CenterEntity>,
    onRetry: () -> Unit,
    createNewCenter: () -> Unit,
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
                                contentDescription = "Sync Items", // i18n:skip
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
                onRefresh = onRetry,
                isRefreshing = false,
            ) {
                // List body — native Store5 offline-first paging. The framework
                // PagingScreenContent owns the LazyColumn, load-more trigger, footer,
                // and the loading / no-network / error+retry / empty transitions. The
                // surrounding selection-mode toolbar / FAB / sync dialog are unchanged.
                PagingScreenContent(
                    pagingStream = pagingStream,
                    onRetry = onRetry,
                    endMessage = stringResource(Res.string.feature_center_no_more_centers),
                ) { centers ->
                    items(
                        items = centers,
                        key = { it.id ?: 0 },
                    ) { center ->
                        CenterCard(
                            center = center,
                            selected = selectedItems.contains(center),
                            isInSelectionMode = selectedItems.size() > 0,
                            onSelect = {
                                if (selectedItems.contains(it)) {
                                    selectedItems.remove(it)
                                } else {
                                    selectedItems.add(it)
                                }
                            },
                            onClick = {
                                onCenterSelect(it.id ?: 0)
                            },
                        )
                    }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CenterCard(
    center: CenterEntity,
    selected: Boolean,
    isInSelectionMode: Boolean,
    onSelect: (CenterEntity) -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = LocalKptColors.current.secondaryContainer,
    unselectedColor: Color = LocalKptColors.current.surface,
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
                    modifier = Modifier.size(DesignToken.sizes.iconSmall),
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
                    horizontalArrangement = Arrangement.spacedBy(LocalKptSpacing.current.xs),
                ) {
                    if (center.sync) {
                        AsyncImage(
                            modifier = Modifier.size(DesignToken.sizes.iconAverage),
                            model = Res.drawable.feature_center_ic_done_all_black_24dp,
                            contentDescription = null,
                        )
                    }

                    Icon(
                        imageVector = MifosIcons.ArrowForward,
                        contentDescription = "Arrow forward icon", // i18n:skip
                    )
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Unspecified,
            ),
        )
    }
}
