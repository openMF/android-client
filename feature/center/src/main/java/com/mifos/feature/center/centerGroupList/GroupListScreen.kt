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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Status
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.core.objects.group.Group
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.feature.center.R

@Composable
internal fun GroupListScreen(
    onBackPressed: () -> Unit,
    loadClientsOfGroup: (List<Client>) -> Unit,
    viewModel: GroupListViewModel = hiltViewModel(),
) {
    val centerId by viewModel.centerId.collectAsStateWithLifecycle()
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
        viewModel.loadGroupByCenter(centerId)
    }

    GroupListScreen(
        state = state,
        onBackPressed = onBackPressed,
        onGroupClick = {
            groupClicked = true
            viewModel.loadGroups(it)
        },
        onRetry = {
            viewModel.loadGroupByCenter(centerId)
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
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_center_groups),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (state) {
                is GroupListUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {
                    onRetry()
                }

                is GroupListUiState.Loading -> MifosCircularProgress()

                is GroupListUiState.GroupList -> {
                    if (state.centerWithAssociations.groupMembers.isEmpty()) {
                        MifosEmptyUi(
                            text = stringResource(id = R.string.feature_center_no_group_list_to_show),
                            icon = MifosIcons.fileTask,
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
    group: Group,
    onGroupClick: (Int) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
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
                    text = if (group.status?.value?.let { Status.isActive(it) } == true) {
                        stringResource(id = R.string.feature_center_active)
                    } else {
                        stringResource(id = R.string.feature_center_inactive)
                    },
                )
                Canvas(modifier = Modifier.size(16.dp)) {
                    if (group.status?.value?.let { Status.isActive(it) } == true) {
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

    override val values: Sequence<GroupListUiState>
        get() = sequenceOf(
            GroupListUiState.Loading,
            GroupListUiState.Error(R.string.feature_center_failed_to_load_group_list),
            GroupListUiState.GroupList(sampleCenterWithAssociations),
        )
}

@Preview(showBackground = true)
@Composable
private fun GroupListScreenPreview(
    @PreviewParameter(GroupListUiStateProvider::class) state: GroupListUiState,
) {
    GroupListScreen(
        state = state,
        onBackPressed = {},
        onGroupClick = {},
        onRetry = {},
    )
}

val sampleCenterWithAssociations = CenterWithAssociations()
