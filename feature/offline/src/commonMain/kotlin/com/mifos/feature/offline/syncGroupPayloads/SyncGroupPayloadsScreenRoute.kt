/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncGroupPayloads

import androidclient.feature.offline.generated.resources.Res
import androidclient.feature.offline.generated.resources.feature_offline_activation_date
import androidclient.feature.offline.generated.resources.feature_offline_active
import androidclient.feature.offline.generated.resources.feature_offline_click_to_refresh
import androidclient.feature.offline.generated.resources.feature_offline_error_not_connected_internet
import androidclient.feature.offline.generated.resources.feature_offline_external_id
import androidclient.feature.offline.generated.resources.feature_offline_name
import androidclient.feature.offline.generated.resources.feature_offline_office_id
import androidclient.feature.offline.generated.resources.feature_offline_submit_date
import androidclient.feature.offline.generated.resources.feature_offline_sync
import androidclient.feature.offline.generated.resources.feature_offline_sync_groups
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosErrorContent
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.util.DevicePreview
import com.mifos.room.entities.group.GroupPayloadEntity
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SyncGroupPayloadsScreenRoute(
    viewModel: SyncGroupPayloadsViewModel = koinViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.syncGroupPayloadsUiState.collectAsStateWithLifecycle()
    val userStatus by viewModel.userStatus.collectAsStateWithLifecycle()
    val groupPayloadsList by viewModel.groupPayloadsList.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isOnline by viewModel.isNetworkAvailable.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loanDatabaseGroupPayload()
    }

    SyncGroupPayloadsScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        refreshState = refreshState,
        groupPayloadsList = groupPayloadsList,
        onRefresh = {
            viewModel.refreshGroupPayload()
        },
        syncGroupPayloads = {
            viewModel.syncGroupPayloadFromStart()
        },
        userStatus = userStatus,
        isOnline = isOnline,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SyncGroupPayloadsScreen(
    uiState: SyncGroupPayloadsUiState,
    onBackPressed: () -> Unit,
    refreshState: Boolean,
    groupPayloadsList: List<GroupPayloadEntity>,
    onRefresh: () -> Unit,
    syncGroupPayloads: () -> Unit,
    userStatus: Boolean,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()
    val offlineMessage = stringResource(Res.string.feature_offline_error_not_connected_internet)
    val scope = rememberCoroutineScope()

    MifosScaffold(
        modifier = modifier,
        title = stringResource(Res.string.feature_offline_sync_groups),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(
                onClick = {
                    when (userStatus) {
                        false -> when (isOnline) {
                            true -> syncGroupPayloads
                            false -> {
                                scope.launch {
                                    snackBarHostState.showSnackbar(offlineMessage)
                                }
                            }
                        }

                        true -> TODO("Implement OfflineModeDialog()")
                    }
                },
            ) {
                Icon(
                    MifosIcons.Sync,
                    contentDescription = stringResource(Res.string.feature_offline_sync),
                )
            }
        },
        snackbarHostState = snackBarHostState,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues),
        ) {
            PullToRefreshBox(
                state = pullRefreshState,
                isRefreshing = refreshState,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxWidth(),
            ) {
                when (uiState) {
                    is SyncGroupPayloadsUiState.Loading -> {
                        MifosCircularProgress()
                    }

                    is SyncGroupPayloadsUiState.Error -> {
                        MifosErrorContent(
                            message = stringResource(uiState.messageResId),
                            onRefresh = onRefresh,
                            refreshButtonText = stringResource(Res.string.feature_offline_click_to_refresh),
                        )
                    }

                    is SyncGroupPayloadsUiState.Success -> {
                        if (uiState.emptyState != null) {
                            MifosErrorContent(
                                imageVector = vectorResource(uiState.emptyState.iconResId),
                                message = stringResource(uiState.emptyState.messageResId),
                                isRefreshEnabled = false,
                            )
                        } else {
                            GroupPayloadsContent(groupPayloadsList)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupPayloadsContent(
    groupPayloadList: List<GroupPayloadEntity>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(groupPayloadList) { payload ->
            GroupPayloadItem(payload)
        }
    }
}

@Composable
private fun GroupPayloadItem(
    payload: GroupPayloadEntity,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val status: String = if (payload.active) {
                true.toString()
            } else {
                false.toString()
            }

            GroupPayloadField(
                stringResource(Res.string.feature_offline_name),
                payload.name ?: "",
            )
            GroupPayloadField(
                stringResource(Res.string.feature_offline_external_id),
                payload.externalId ?: "",
            )
            GroupPayloadField(
                stringResource(Res.string.feature_offline_office_id),
                payload.officeId.toString(),
            )
            GroupPayloadField(
                stringResource(Res.string.feature_offline_submit_date),
                payload.submittedOnDate ?: "",
            )
            GroupPayloadField(
                stringResource(Res.string.feature_offline_activation_date),
                payload.activationDate ?: "",
            )
            GroupPayloadField(stringResource(Res.string.feature_offline_active), status)

            if (payload.errorMessage != null) {
                Text(
                    text = payload.errorMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun GroupPayloadField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
}

@DevicePreview
@Composable
private fun SyncGroupPayloadsScreenPreview() {
    SyncGroupPayloadsScreen(
        uiState = SyncGroupPayloadsUiState.Success(),
        onRefresh = { },
        onBackPressed = { },
        refreshState = false,
        syncGroupPayloads = { },
        groupPayloadsList = dummyGroupPayloads,
        userStatus = true,
        isOnline = true,
    )
}
