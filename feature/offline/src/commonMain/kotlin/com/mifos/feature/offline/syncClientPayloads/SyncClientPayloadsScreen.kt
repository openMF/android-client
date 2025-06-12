/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncClientPayloads

import androidclient.feature.offline.generated.resources.Res
import androidclient.feature.offline.generated.resources.feature_offline_activation_date
import androidclient.feature.offline.generated.resources.feature_offline_active
import androidclient.feature.offline.generated.resources.feature_offline_click_to_refresh
import androidclient.feature.offline.generated.resources.feature_offline_dob
import androidclient.feature.offline.generated.resources.feature_offline_error_not_connected_internet
import androidclient.feature.offline.generated.resources.feature_offline_external_id
import androidclient.feature.offline.generated.resources.feature_offline_first_name
import androidclient.feature.offline.generated.resources.feature_offline_gender
import androidclient.feature.offline.generated.resources.feature_offline_last_name
import androidclient.feature.offline.generated.resources.feature_offline_middle_name
import androidclient.feature.offline.generated.resources.feature_offline_mobile_no
import androidclient.feature.offline.generated.resources.feature_offline_office_id
import androidclient.feature.offline.generated.resources.feature_offline_sync_clients
import androidclient.feature.offline.generated.resources.feature_offline_sync_clients_payloads
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.room.entities.client.ClientPayloadEntity
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SyncClientPayloadsScreenRoute(
    viewModel: SyncClientPayloadsViewModel = koinViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.syncClientPayloadsUiState.collectAsStateWithLifecycle()
    val userStatus by viewModel.userStatus.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isOnline by viewModel.isNetworkAvailable.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadDatabaseClientPayload()
    }

    SyncClientPayloadsScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        refreshState = refreshState,
        onRefresh = {
            viewModel.refreshClientPayloads()
        },
        syncClientPayloads = {
            viewModel.syncClientPayload()
        },
        userStatus = userStatus,
        isOnline = isOnline,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SyncClientPayloadsScreen(
    uiState: SyncClientPayloadsUiState,
    onBackPressed: () -> Unit,
    refreshState: Boolean,
    onRefresh: () -> Unit,
    syncClientPayloads: () -> Unit,
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
        title = stringResource(Res.string.feature_offline_sync_clients_payloads),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(
                onClick = {
                    when (userStatus) {
                        false -> when (isOnline) {
                            true -> syncClientPayloads()
                            false -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = offlineMessage,
                                    )
                                }
                            }
                        }

                        true -> TODO("Implement OfflineModeDialog()")
                    }
                },
            ) {
                Icon(
                    MifosIcons.Sync,
                    contentDescription = stringResource(Res.string.feature_offline_sync_clients),
                )
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        PullToRefreshBox(
            state = pullToRefreshState,
            onRefresh = onRefresh,
            isRefreshing = refreshState,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            when (uiState) {
                is SyncClientPayloadsUiState.ShowProgressbar -> {
                    MifosCircularProgress()
                }

                is SyncClientPayloadsUiState.ShowError -> {
                    ErrorStateScreen(uiState.message, onRefresh)
                }

                is SyncClientPayloadsUiState.ShowPayloads -> {
                    ClientPayloadsList(uiState.clientPayloads)
                }
            }
        }
    }
}

@Composable
private fun ClientPayloadsList(
    clientPayloads: List<ClientPayloadEntity>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(clientPayloads) { payload ->
            ClientPayloadItem(payload)
        }
    }
}

@Composable
private fun ClientPayloadItem(
    payload: ClientPayloadEntity,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val payloadStatus: String = if (payload.active == true) {
                "true"
            } else {
                "false"
            }

            val gender = when (payload.genderId.toString()) {
                24.toString() -> {
                    "Female"
                }

                91.toString() -> {
                    "Homosexual"
                }

                else -> {
                    "Male"
                }
            }

            PayloadField(
                stringResource(Res.string.feature_offline_first_name),
                payload.firstname ?: "",
            )
            PayloadField(
                stringResource(Res.string.feature_offline_middle_name),
                payload.middlename ?: "",
            )
            PayloadField(
                stringResource(Res.string.feature_offline_last_name),
                payload.lastname ?: "",
            )
            PayloadField(
                stringResource(Res.string.feature_offline_mobile_no),
                payload.mobileNo ?: "",
            )
            PayloadField(
                stringResource(Res.string.feature_offline_external_id),
                payload.externalId ?: "",
            )
            PayloadField(stringResource(Res.string.feature_offline_gender), gender)
            PayloadField(
                stringResource(Res.string.feature_offline_dob),
                payload.dateOfBirth ?: "",
            )
            PayloadField(
                stringResource(Res.string.feature_offline_office_id),
                payload.officeId?.toString() ?: "",
            )
            PayloadField(
                stringResource(Res.string.feature_offline_activation_date),
                payload.activationDate ?: "",
            )
            PayloadField(stringResource(Res.string.feature_offline_active), payloadStatus)

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

@Composable
private fun ErrorStateScreen(
    message: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = MifosIcons.Error,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
        )
        Text(text = message, modifier = Modifier.padding(vertical = 16.dp))
        Button(onClick = onRefresh) {
            Text(stringResource(Res.string.feature_offline_click_to_refresh))
        }
    }
}

class SyncClientPayloadsUiStateProvider : PreviewParameterProvider<SyncClientPayloadsUiState> {
    override val values = sequenceOf(
        SyncClientPayloadsUiState.ShowProgressbar,
        SyncClientPayloadsUiState.ShowError("Failed to load client payloads"),
        SyncClientPayloadsUiState.ShowPayloads(sampleClientPayloads),
    )
}

@Composable
@Preview
private fun SyncClientPayloadsScreenPreview(
    @PreviewParameter(SyncClientPayloadsUiStateProvider::class) uiState: SyncClientPayloadsUiState,
) {
    SyncClientPayloadsScreen(
        uiState = uiState,
        onBackPressed = {},
        refreshState = false,
        onRefresh = {},
        syncClientPayloads = {},
        userStatus = true,
        isOnline = true,
    )
}

val sampleClientPayloads = List(5) { index ->
    ClientPayloadEntity(
        firstname = "John$index",
        middlename = "Sam$index",
        lastname = "Doe$index",
        mobileNo = "123456789$index",
        externalId = "EXT-$index",
        officeId = index,
        active = index % 2 == 0,
        activationDate = "2023-07-${15 + index}",
        genderId = if (index % 3 == 0) 24 else 22,
        dateOfBirth = "1990-01-0$index",
        errorMessage = if (index % 2 == 0) null else "Error in payload",
    )
}
