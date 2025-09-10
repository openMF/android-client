/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.syncCentersDialog

import androidclient.feature.center.generated.resources.Res
import androidclient.feature.center.generated.resources.feature_center_cancel
import androidclient.feature.center.generated.resources.feature_center_center
import androidclient.feature.center.generated.resources.feature_center_dialog_action_ok
import androidclient.feature.center.generated.resources.feature_center_failed_sync
import androidclient.feature.center.generated.resources.feature_center_hide
import androidclient.feature.center.generated.resources.feature_center_name
import androidclient.feature.center.generated.resources.feature_center_slash
import androidclient.feature.center.generated.resources.feature_center_something_went_wrong
import androidclient.feature.center.generated.resources.feature_center_space
import androidclient.feature.center.generated.resources.feature_center_sync_centers_full_information
import androidclient.feature.center.generated.resources.feature_center_syncing_center
import androidclient.feature.center.generated.resources.feature_center_syncing_client
import androidclient.feature.center.generated.resources.feature_center_syncing_group
import androidclient.feature.center.generated.resources.feature_center_total
import androidclient.feature.center.generated.resources.feature_center_total_sync_progress
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.DevicePreview
import com.mifos.room.entities.group.CenterEntity
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SyncCenterDialogScreen(
    dismiss: () -> Unit,
    hide: () -> Unit,
    centers: List<CenterEntity>? = listOf(),
    viewModel: SyncCentersDialogViewModel = koinViewModel(),
) {
    val uiState by viewModel.syncCentersDialogUiState.collectAsStateWithLifecycle()
    val uiData by viewModel.syncCenterData.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        centers?.let {
            viewModel.setCentersList(centersList = centers)
        }
        viewModel.syncCenter()
    }

    SyncCenterDialogScreen(
        uiState = uiState,
        uiData = uiData,
        dismiss = dismiss,
        hide = hide,
    )
}

@Composable
internal fun SyncCenterDialogScreen(
    uiState: SyncCentersDialogUiState,
    uiData: SyncCentersDialogData,
    dismiss: () -> Unit,
    hide: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Box {
        SyncGroupDialogContent(
            uiData = uiData,
            okClicked = dismiss,
            hideClicked = hide,
            cancelClicked = dismiss,
        )

        when (uiState) {
            is SyncCentersDialogUiState.Success -> Unit

            is SyncCentersDialogUiState.Loading -> MifosProgressIndicator()

            is SyncCentersDialogUiState.Error -> {
                val message = uiState.message
                    ?: uiState.messageResId?.let { stringResource(uiState.messageResId) }
                    ?: stringResource(Res.string.feature_center_something_went_wrong)
                LaunchedEffect(key1 = message) {
                    snackBarHostState.showSnackbar(message = message)
                }
                dismiss()
            }

            else -> {}
        }
    }
}

@Composable
private fun SyncGroupDialogContent(
    uiData: SyncCentersDialogData,
    okClicked: () -> Unit,
    hideClicked: () -> Unit,
    cancelClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(Res.string.feature_center_sync_centers_full_information),
        )

        PayloadField(
            label = stringResource(Res.string.feature_center_name),
            value = uiData.centerName,
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(Res.string.feature_center_total),
            value = uiData.centersList.size.toString() + stringResource(Res.string.feature_center_space) + stringResource(
                Res.string.feature_center_center,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(Res.string.feature_center_syncing_center),
            value = uiData.centerName,
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(Res.string.feature_center_syncing_group),
            value = "syncing_group",
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(Res.string.feature_center_syncing_client),
            value = "syncing_client",
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(Res.string.feature_center_total_sync_progress),
            value = stringResource(Res.string.feature_center_space) + uiData.totalSyncCount + stringResource(Res.string.feature_center_slash) + uiData.centersList.size,
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(Res.string.feature_center_failed_sync),
            value = uiData.failedSyncGroupCount.toString(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiData.isSyncSuccess) {
                FilledTonalButton(
                    onClick = { okClicked() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(Res.string.feature_center_dialog_action_ok))
                }
            } else {
                FilledTonalButton(
                    onClick = { cancelClicked() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(Res.string.feature_center_cancel))
                }

                Spacer(modifier = Modifier.width(10.dp))

                FilledTonalButton(
                    onClick = { hideClicked() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(Res.string.feature_center_hide))
                }
            }
        }
    }
}

@Composable
private fun PayloadField(label: String, value: String) {
    Row(
        modifier = Modifier
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
private fun SyncCenterDialogScreenPreview() {
    SyncCenterDialogScreen(
        dismiss = { },
        uiState = SyncCentersDialogUiState.Success,
        uiData = SyncCentersDialogData(),
        hide = { },
    )
}
