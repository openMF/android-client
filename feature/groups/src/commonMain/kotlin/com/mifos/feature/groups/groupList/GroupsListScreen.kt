/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.groupList

import kpt.feature.groups.generated.resources.Res
import kpt.feature.groups.generated.resources.feature_groups_no_more_groups_available
import kpt.feature.groups.generated.resources.feature_groups_sync
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.components.MifosFAB
import com.mifos.core.ui.components.SelectionModeTopAppBar
import com.mifos.feature.groups.syncGroupDialog.SyncGroupDialogScreen
import com.mifos.room.entities.group.GroupEntity
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kpt.core.base.designsystem.theme.LocalKptColors
import kpt.core.base.designsystem.theme.LocalKptShapes
import kpt.core.base.designsystem.theme.LocalKptSpacing
import kpt.core.base.store.paging.PagingScreenStream
import kpt.core.base.ui.paging.PagingScreenContent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun GroupsListRoute(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onAddGroupClick: () -> Unit,
    onGroupClick: (groupId: Int) -> Unit,
    viewModel: GroupsListViewModel = koinViewModel(),
) {
    val selectedItems = remember {
        mutableStateListOf<GroupEntity>()
    }

    BackHandler(
        enabled = selectedItems.isNotEmpty(),
    ) {
        selectedItems.clear()
    }

    GroupsListScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        selectedItems = selectedItems,
        pagingStream = viewModel.pagingStream,
        onRetry = viewModel::retry,
        onAddGroupClick = onAddGroupClick,
        onGroupClick = onGroupClick,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsListScreen(
    selectedItems: List<GroupEntity>,
    pagingStream: PagingScreenStream<GroupEntity>,
    onRetry: () -> Unit,
    onAddGroupClick: () -> Unit,
    onGroupClick: (groupId: Int) -> Unit,
    onSelectItem: (GroupEntity) -> Unit,
    modifier: Modifier = Modifier,
    // todo isrefreshing logic needs to be implemented
    isRefreshing: Boolean = false,
    resetSelectionMode: () -> Unit,
) {
    val pullRefreshState = rememberPullToRefreshState()

    var syncGroups by rememberSaveable { mutableStateOf(false) }
    if (syncGroups) {
        SyncGroupDialogScreen(
            dismiss = { syncGroups = false },
            hide = {
                // TODO implement hide
            },
        )
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            MifosFAB(icon = MifosIcons.Add, onClick = onAddGroupClick)
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
                    resetSelectionMode = resetSelectionMode,
                    actions = {
                        FilledTonalButton(
                            onClick = {
                                syncGroups = true
                                resetSelectionMode()
                            },
                        ) {
                            Icon(
                                imageVector = MifosIcons.Sync,
                                contentDescription = "Sync Items",
                            )
                            Text(text = stringResource(Res.string.feature_groups_sync))
                        }
                    },
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            PullToRefreshBox(
                modifier = Modifier.semantics {
                    contentDescription = "SwipeRefresh::GroupList"
                },
                state = pullRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = onRetry,
            ) {
                // List body — native Store5 offline-first paging. The framework
                // PagingScreenContent owns the LazyColumn, load-more trigger, footer,
                // and the loading / no-network / error+retry / empty transitions.
                PagingScreenContent(
                    pagingStream = pagingStream,
                    onRetry = onRetry,
                    endMessage = stringResource(Res.string.feature_groups_no_more_groups_available),
                    empty = {
                        MifosEmptyUi(
                            text = stringResource(Res.string.feature_groups_no_more_groups_available),
                        )
                    },
                ) { groups ->
                    items(
                        items = groups,
                        key = { it.id ?: 0 },
                    ) { group ->
                        GroupItem(
                            group = group,
                            doesSelected = selectedItems.contains(group),
                            inSelectionMode = selectedItems.isNotEmpty(),
                            onGroupClick = {
                                group.id?.let { onGroupClick(it) }
                            },
                            onSelectItem = {
                                onSelectItem(group)
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupItem(
    group: GroupEntity,
    doesSelected: Boolean,
    inSelectionMode: Boolean,
    onGroupClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSelectItem: () -> Unit,
) {
    val borderStroke = if (doesSelected) {
        BorderStroke(DesignToken.strokes.thin, Color.Blue)
    } else {
        CardDefaults.outlinedCardBorder()
    }
    val containerColor = if (doesSelected) {
        LocalKptColors.current.secondaryContainer
    } else {
        Color.Unspecified
    }
    // TODO: replace primary with Green after we define Theme colours of mockups
    val indicatorColor = if (group.active == true) {
        LocalKptColors.current.primary
    } else {
        LocalKptColors.current.error
    }

    group.name?.let {
        OutlinedCard(
            modifier = modifier
                .testTag(it)
                .padding(LocalKptSpacing.current.sm)
                .fillMaxWidth()
                .clip(LocalKptShapes.current.small)
                .combinedClickable(
                    onClick = {
                        if (inSelectionMode) {
                            onSelectItem()
                        } else {
                            onGroupClick()
                        }
                    },
                    onLongClick = onSelectItem,
                ),
            shape = LocalKptShapes.current.small,
            colors = CardDefaults.outlinedCardColors(
                containerColor = containerColor,
            ),
            border = borderStroke,
        ) {
            ListItem(
                leadingContent = {
                    Canvas(
                        modifier = Modifier.size(DesignToken.sizes.iconSmall),
                        onDraw = {
                            drawCircle(
                                color = indicatorColor,
                            )
                        },
                    )
                },
                headlineContent = {
                    Text(text = it)
                },
                supportingContent =
                {
                    Text(text = group.accountNo ?: "")
                },
                overlineContent =
                {
                    Text(text = group.officeName ?: "")
                },
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(LocalKptSpacing.current.xs),
                    ) {
                        if (group.sync) {
                            Icon(imageVector = MifosIcons.DoneAll, contentDescription = "Sync")
                        }

                        Icon(
                            imageVector = MifosIcons.ArrowForward,
                            contentDescription = "Arrow Forward Icon",
                        )
                    }
                },
            )
        }
    }
}
