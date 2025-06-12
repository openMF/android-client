/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncCenterPayloads

import androidclient.feature.offline.generated.resources.Res
import androidclient.feature.offline.generated.resources.feature_offline_activation_date
import androidclient.feature.offline.generated.resources.feature_offline_active
import androidclient.feature.offline.generated.resources.feature_offline_error_not_connected_internet
import androidclient.feature.offline.generated.resources.feature_offline_name
import androidclient.feature.offline.generated.resources.feature_offline_no_center_payload_to_sync
import androidclient.feature.offline.generated.resources.feature_offline_office_id
import androidclient.feature.offline.generated.resources.feature_offline_sync_centers
import androidclient.feature.offline.generated.resources.feature_offline_sync_centers_payloads
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.room.entities.center.CenterPayloadEntity
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SyncCenterPayloadsScreenRoute(
    viewModel: SyncCenterPayloadsViewModel = koinViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.syncCenterPayloadsUiState.collectAsStateWithLifecycle()
    val userStatus by viewModel.userStatus.collectAsStateWithLifecycle()
    val refreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isOnline by viewModel.isNetworkAvailable.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadDatabaseCenterPayload()
    }

    SyncCenterPayloadsScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        refreshing = refreshing,
        onRefresh = { viewModel.refreshCenterPayloads() },
        syncCenterPayloads = { viewModel.syncCenterPayload() },
        userStatus = userStatus,
        isOnline = isOnline,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SyncCenterPayloadsScreen(
    uiState: SyncCenterPayloadsUiState,
    onBackPressed: () -> Unit,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    syncCenterPayloads: () -> Unit,
    userStatus: Boolean,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()
    val offlineMessage = stringResource(Res.string.feature_offline_error_not_connected_internet)
    val scope = rememberCoroutineScope()

    MifosScaffold(
        modifier = modifier,
        title = stringResource(Res.string.feature_offline_sync_centers_payloads),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(
                onClick = {
                    when (userStatus) {
                        false -> {
                            when (isOnline) {
                                true -> syncCenterPayloads()

                                false -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = offlineMessage,
                                        )
                                    }
                                }
                            }
                        }

                        true -> TODO("Implement OfflineModeDialog()")
                    }
                },
            ) {
                Icon(
                    MifosIcons.Sync,
                    contentDescription = stringResource(Res.string.feature_offline_sync_centers),
                )
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        PullToRefreshBox(
            state = pullToRefreshState,
            onRefresh = onRefresh,
            isRefreshing = refreshing,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            when (uiState) {
                is SyncCenterPayloadsUiState.ShowProgressbar -> {
                    MifosCircularProgress()
                }

                is SyncCenterPayloadsUiState.ShowError -> {
                    MifosSweetError(message = uiState.message, onclick = onRefresh)
                }

                is SyncCenterPayloadsUiState.ShowCenters -> {
                    CenterPayloadsList(centerPayloads = uiState.centerPayloads)
                }
            }
        }
    }
}

@Composable
private fun CenterPayloadsList(
    centerPayloads: List<CenterPayloadEntity>,
    modifier: Modifier = Modifier,
) {
    if (centerPayloads.isEmpty()) {
        MifosEmptyUi(text = stringResource(Res.string.feature_offline_no_center_payload_to_sync))
    } else {
        LazyColumn(modifier = modifier) {
            items(centerPayloads) { payload ->
                CenterPayloadItem(payload)
            }
        }
    }
}

@Composable
private fun CenterPayloadItem(
    payload: CenterPayloadEntity,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            PayloadField(
                label = stringResource(Res.string.feature_offline_name),
                value = payload.name ?: "",
            )
            PayloadField(
                label = stringResource(Res.string.feature_offline_office_id),
                value = payload.officeId?.toString() ?: "",
            )
            PayloadField(
                label = stringResource(Res.string.feature_offline_activation_date),
                value = payload.activationDate ?: "",
            )
            PayloadField(
                label = stringResource(Res.string.feature_offline_active),
                value = if (payload.active) true.toString() else false.toString(),
            )
            payload.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun PayloadField(
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
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
}

class SyncCenterPayloadsUiStateProvider : PreviewParameterProvider<SyncCenterPayloadsUiState> {
    override val values = sequenceOf(
        SyncCenterPayloadsUiState.ShowProgressbar,
        SyncCenterPayloadsUiState.ShowError("Failed to load center payloads"),
        SyncCenterPayloadsUiState.ShowCenters(sampleCenterPayloads),
    )
}

@Composable
@Preview
private fun SyncCenterPayloadsScreenPreview(
    @PreviewParameter(SyncCenterPayloadsUiStateProvider::class) uiState: SyncCenterPayloadsUiState,
) {
    SyncCenterPayloadsScreen(
        uiState = uiState,
        onBackPressed = {},
        refreshing = false,
        onRefresh = {},
        syncCenterPayloads = {},
        isOnline = true,
        userStatus = true,
    )
}

private val sampleCenterPayloads = List(5) { index ->
    CenterPayloadEntity(
        name = "Center $index",
        officeId = index + 1,
        activationDate = "2023-07-${15 + index}",
        active = index % 2 == 0,
        errorMessage = if (index % 3 == 0) "Error in payload" else null,
    )
}
