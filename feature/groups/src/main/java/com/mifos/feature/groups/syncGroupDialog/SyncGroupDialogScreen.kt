/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.syncGroupDialog

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.feature.groups.R

@Composable
internal fun SyncGroupDialogScreen(
    dismiss: () -> Unit,
    viewModel: SyncGroupsDialogViewModel = hiltViewModel(),
    hide: () -> Unit,
) {
    val uiState by viewModel.syncGroupsDialogUiState.collectAsStateWithLifecycle()
    val uiData by viewModel.syncGroupData.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.syncGroups()
    }

    SyncGroupDialogScreen(
        uiState = uiState,
        uiData = uiData,
        dismiss = dismiss,
        hide = hide,
    )
}

@Composable
internal fun SyncGroupDialogScreen(
    uiState: SyncGroupsDialogUiState,
    uiData: SyncGroupDialogData,
    dismiss: () -> Unit,
    modifier: Modifier = Modifier,
    hide: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Box(modifier = modifier) {
        SyncGroupDialogContent(
            uiData = uiData,
            okClicked = dismiss,
            hideClicked = hide,
            cancelClicked = dismiss,
        )

        when (uiState) {
            is SyncGroupsDialogUiState.Success -> Unit

            is SyncGroupsDialogUiState.Loading -> MifosCircularProgress()

            is SyncGroupsDialogUiState.Error -> {
                val message = uiState.message
                    ?: uiState.messageResId?.let { stringResource(uiState.messageResId) }
                    ?: stringResource(id = R.string.feature_groups_something_went_wrong)
                LaunchedEffect(key1 = message) {
                    snackBarHostState.showSnackbar(message = message)
                }
                dismiss()
            }
        }
    }
}

@Composable
private fun SyncGroupDialogContent(
    uiData: SyncGroupDialogData,
    okClicked: () -> Unit,
    hideClicked: () -> Unit,
    modifier: Modifier = Modifier,
    cancelClicked: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.feature_groups_sync_groups_full_information),
        )

        GroupPayloadField(
            label = stringResource(id = R.string.feature_groups_name),
            value = uiData.groupName,
        )

        Spacer(modifier = Modifier.height(12.dp))

        GroupPayloadField(
            label = stringResource(id = R.string.feature_groups_total),
            value = uiData.groupList.size.toString() + stringResource(R.string.feature_groups_space) + stringResource(R.string.feature_groups_groups),
        )

        Spacer(modifier = Modifier.height(12.dp))

        GroupPayloadField(
            label = stringResource(id = R.string.feature_groups_syncing_group),
            value = uiData.groupName,
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        GroupPayloadField(
            label = stringResource(id = R.string.feature_groups_syncing_client),
            value = "syncing_client",
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        GroupPayloadField(
            label = stringResource(id = R.string.feature_groups_total_sync_progress),
            value = stringResource(R.string.feature_groups_space) + uiData.totalSyncCount + stringResource(id = R.string.feature_groups_slash) + uiData.groupList.size,
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        GroupPayloadField(
            label = stringResource(id = R.string.feature_groups_failed_sync),
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
                    Text(text = stringResource(id = R.string.feature_groups_dialog_action_ok))
                }
            } else {
                FilledTonalButton(
                    onClick = { cancelClicked() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(id = R.string.feature_groups_cancel))
                }

                Spacer(modifier = Modifier.width(10.dp))

                FilledTonalButton(
                    onClick = { hideClicked() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(id = R.string.feature_groups_hide))
                }
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

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SyncGroupDialogScreenPreview() {
    SyncGroupDialogScreen(
        dismiss = { },
        uiState = SyncGroupsDialogUiState.Success,
        uiData = SyncGroupDialogData(),
        hide = { },
    )
}
