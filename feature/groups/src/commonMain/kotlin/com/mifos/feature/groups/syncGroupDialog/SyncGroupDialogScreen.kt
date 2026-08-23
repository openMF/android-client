/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.syncGroupDialog

import kpt.feature.groups.generated.resources.Res
import kpt.feature.groups.generated.resources.feature_groups_cancel
import kpt.feature.groups.generated.resources.feature_groups_dialog_action_ok
import kpt.feature.groups.generated.resources.feature_groups_failed_sync
import kpt.feature.groups.generated.resources.feature_groups_groups
import kpt.feature.groups.generated.resources.feature_groups_hide
import kpt.feature.groups.generated.resources.feature_groups_name
import kpt.feature.groups.generated.resources.feature_groups_slash
import kpt.feature.groups.generated.resources.feature_groups_something_went_wrong
import kpt.feature.groups.generated.resources.feature_groups_space
import kpt.feature.groups.generated.resources.feature_groups_sync_groups_full_information
import kpt.feature.groups.generated.resources.feature_groups_syncing_client
import kpt.feature.groups.generated.resources.feature_groups_syncing_group
import kpt.feature.groups.generated.resources.feature_groups_total
import kpt.feature.groups.generated.resources.feature_groups_total_sync_progress
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosProgressIndicator
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kpt.core.base.designsystem.KptTheme
import kpt.core.base.designsystem.theme.LocalKptColors
import kpt.core.base.designsystem.theme.LocalKptTypography
import kpt.core.base.designsystem.theme.LocalKptSpacing

@Composable
internal fun SyncGroupDialogScreen(
    dismiss: () -> Unit,
    viewModel: SyncGroupsDialogViewModel = koinViewModel(),
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

            is SyncGroupsDialogUiState.Loading -> MifosProgressIndicator()

            is SyncGroupsDialogUiState.Error -> {
                val message = uiState.message
                    ?: uiState.messageResId?.let { stringResource(uiState.messageResId) }
                    ?: stringResource(Res.string.feature_groups_something_went_wrong)
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
            .padding(LocalKptSpacing.current.sm)
            .background(color = LocalKptColors.current.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(Res.string.feature_groups_sync_groups_full_information),
        )

        GroupPayloadField(
            label = stringResource(Res.string.feature_groups_name),
            value = uiData.groupName,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        GroupPayloadField(
            label = stringResource(Res.string.feature_groups_total),
            value = uiData.groupList.size.toString() + stringResource(Res.string.feature_groups_space) + stringResource(
                Res.string.feature_groups_groups,
            ),
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        GroupPayloadField(
            label = stringResource(Res.string.feature_groups_syncing_group),
            value = uiData.groupName,
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        GroupPayloadField(
            label = stringResource(Res.string.feature_groups_syncing_client),
            value = "syncing_client",
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        GroupPayloadField(
            label = stringResource(Res.string.feature_groups_total_sync_progress),
            value = stringResource(Res.string.feature_groups_space) + uiData.totalSyncCount +
                stringResource(Res.string.feature_groups_slash) + uiData.groupList.size,
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        GroupPayloadField(
            label = stringResource(Res.string.feature_groups_failed_sync),
            value = uiData.failedSyncGroupCount.toString(),
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiData.isSyncSuccess) {
                FilledTonalButton(
                    onClick = { okClicked() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(Res.string.feature_groups_dialog_action_ok))
                }
            } else {
                FilledTonalButton(
                    onClick = { cancelClicked() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(Res.string.feature_groups_cancel))
                }
                Spacer(modifier = Modifier.width(DesignToken.spacing.dp10))

                FilledTonalButton(
                    onClick = { hideClicked() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(Res.string.feature_groups_hide))
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
            .padding(vertical = LocalKptSpacing.current.xs),
    ) {
        Text(
            text = label,
            style = LocalKptTypography.current.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = LocalKptTypography.current.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
@Preview
private fun SyncGroupDialogScreenPreview() {
    SyncGroupDialogScreen(
        dismiss = { },
        uiState = SyncGroupsDialogUiState.Success,
        uiData = SyncGroupDialogData(),
        hide = { },
    )
}
