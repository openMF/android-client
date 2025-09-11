/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.centerGroupList

import androidclient.feature.center.generated.resources.Res
import androidclient.feature.center.generated.resources.feature_center_active
import androidclient.feature.center.generated.resources.feature_center_failed_to_load_group_list
import androidclient.feature.center.generated.resources.feature_center_groups
import androidclient.feature.center.generated.resources.feature_center_inactive
import androidclient.feature.center.generated.resources.feature_center_no_group_list_to_show
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCard
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.client.ClientStatusEntity
import com.mifos.room.entities.group.CenterWithAssociations
import com.mifos.room.entities.group.GroupEntity
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun GroupListScreen(
    onBackPressed: () -> Unit,
    loadClientsOfGroup: (List<ClientEntity>) -> Unit,
    viewModel: GroupListViewModel = koinViewModel(),
) {
    val state by viewModel.groupListUiState.collectAsStateWithLifecycle()
    val groupAssociationState by viewModel.groupAssociationState.collectAsStateWithLifecycle()
    var groupClicked by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = groupAssociationState) {
        groupAssociationState?.let {
            if (groupClicked) {
                loadClientsOfGroup(it.clientMembers)
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.loadGroupByCenter()
    }

    GroupListScreen(
        state = state,
        onBackPressed = onBackPressed,
        onGroupClick = {
            groupClicked = true
            viewModel.loadGroups(it)
        },
        onRetry = {
            viewModel.loadGroupByCenter()
        },
    )
}

@Composable
internal fun GroupListScreen(
    state: GroupListUiState,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    onGroupClick: (Int) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        title = stringResource(Res.string.feature_center_groups),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (state) {
                is GroupListUiState.Error -> MifosSweetError(message = stringResource(state.message)) {
                    onRetry()
                }

                is GroupListUiState.Loading -> MifosProgressIndicator()

                is GroupListUiState.GroupList -> {
                    if (state.centerWithAssociations.groupMembers.isEmpty()) {
                        MifosEmptyUi(
                            text = stringResource(Res.string.feature_center_no_group_list_to_show),
                            icon = MifosIcons.FileTask,
                        )
                    } else {
                        GroupListContent(
                            centerWithAssociations = state.centerWithAssociations,
                            onGroupClick = onGroupClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupListContent(
    centerWithAssociations: CenterWithAssociations,
    onGroupClick: (Int) -> Unit,
) {
    LazyColumn {
        items(centerWithAssociations.groupMembers) { group ->
            GroupItem(group = group, onGroupClick = onGroupClick)
        }
    }
}

@Composable
private fun GroupItem(
    group: GroupEntity,
    onGroupClick: (Int) -> Unit,
) {
    MifosCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        onClick = { group.id?.let { onGroupClick(it) } },
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp),
                    text = group.name.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = group.officeName.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    text = if (group.status?.value?.let { ClientStatusEntity.isActive(it) } == true) {
                        stringResource(Res.string.feature_center_active)
                    } else {
                        stringResource(Res.string.feature_center_inactive)
                    },
                )
                Canvas(modifier = Modifier.size(16.dp)) {
                    if (group.status?.value?.let { ClientStatusEntity.isActive(it) } == true) {
                        drawRect(Color.Green)
                    } else {
                        drawRect(Color.Red)
                    }
                }
            }
        }
    }
    HorizontalDivider()
}

class GroupListUiStateProvider : PreviewParameterProvider<GroupListUiState> {
    override val values = sequenceOf(
        GroupListUiState.Loading,
        GroupListUiState.Error(Res.string.feature_center_failed_to_load_group_list),
        GroupListUiState.GroupList(CenterWithAssociations()),
    )
}

@Preview
@Composable
fun GroupListScreenPreview(
    @PreviewParameter(GroupListUiStateProvider::class) state: GroupListUiState,
) {
    GroupListScreen(
        state = state,
        onBackPressed = {},
        onGroupClick = {},
        onRetry = {},
    )
}
