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

// import com.mifos.core.common.utils.Network
import androidclient.feature.offline.generated.resources.Res
import androidclient.feature.offline.generated.resources.feature_offline_activation_date
import androidclient.feature.offline.generated.resources.feature_offline_active
import androidclient.feature.offline.generated.resources.feature_offline_name
import androidclient.feature.offline.generated.resources.feature_offline_no_center_payload_to_sync
import androidclient.feature.offline.generated.resources.feature_offline_office_id
import androidclient.feature.offline.generated.resources.feature_offline_sync_centers
import androidclient.feature.offline.generated.resources.feature_offline_sync_centers_payloads
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.util.DevicePreview
import com.mifos.room.entities.center.CenterPayloadEntity
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SyncCenterPayloadsScreenRoute(
    viewModel: SyncCenterPayloadsViewModel = koinViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.syncCenterPayloadsUiState.collectAsStateWithLifecycle()
    val userStatus by viewModel.userStatus.collectAsStateWithLifecycle()
    val refreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

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
    )
}

@Composable
internal fun SyncCenterPayloadsScreen(
    uiState: SyncCenterPayloadsUiState,
    onBackPressed: () -> Unit,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    syncCenterPayloads: () -> Unit,
    userStatus: Boolean,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
//    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = onRefresh)

    MifosScaffold(
        modifier = modifier,
        title = stringResource(Res.string.feature_offline_sync_centers_payloads),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(
                onClick = {
                    when (userStatus) {
                        false -> checkNetworkConnectionAndSync(syncCenterPayloads)
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
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            when (uiState) {
                is SyncCenterPayloadsUiState.ShowProgressbar -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is SyncCenterPayloadsUiState.ShowError -> {
                    MifosSweetError(message = uiState.message, onclick = onRefresh)
                }

                is SyncCenterPayloadsUiState.ShowCenters -> {
                    CenterPayloadsList(centerPayloads = uiState.centerPayloads)
                }
            }

            if (refreshing) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp),
                )
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

// @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
private fun checkNetworkConnectionAndSync(
    syncCenterPayloads: () -> Unit,
) {
    Log.d("C", context.packageName)
//    if (Network.isOnline(context)) {
    syncCenterPayloads()
//    } else {
//        Toast.makeText(
//            context,
//            context.getString(R.string.feature_offline_error_not_connected_internet),
//            Toast.LENGTH_SHORT,
//        ).show()
//    }
}

@DevicePreview
@Composable
private fun SyncCenterPayloadsScreenPreview(
    @PreviewParameter(SyncCenterPayloadsUiStateProvider::class) uiState: SyncCenterPayloadsUiState,
) {
    SyncCenterPayloadsScreen(
        uiState = uiState,
        onBackPressed = {},
        refreshing = false,
        onRefresh = {},
        syncCenterPayloads = {},
        userStatus = true,
    )
}

class SyncCenterPayloadsUiStateProvider : PreviewParameterProvider<SyncCenterPayloadsUiState> {
    override val values = sequenceOf(
        SyncCenterPayloadsUiState.ShowProgressbar,
        SyncCenterPayloadsUiState.ShowError("Failed to load center payloads"),
        SyncCenterPayloadsUiState.ShowCenters(sampleCenterPayloads),
    )
}

// Sample data for previews
val sampleCenterPayloads = List(5) { index ->
    CenterPayloadEntity(
        name = "Center $index",
        officeId = index + 1,
        activationDate = "2023-07-${15 + index}",
        active = index % 2 == 0,
        errorMessage = if (index % 3 == 0) "Error in payload" else null,
    )
}

@DevicePreview
@Composable
private fun CenterPayloadItemPreview() {
//    val sampleCenterPayload = CenterPayload().apply {
//        name = "Sample Center"
//        officeId = 12345
//        activationDate = "2023-07-15"
//        active = true
//        errorMessage = null
//    }

//    CenterPayloadItem(payload = sampleCenterPayload)
}

@DevicePreview
@Composable
private fun PayloadFieldPreview() {
    PayloadField(label = "Sample Label", value = "Sample Value")
}
