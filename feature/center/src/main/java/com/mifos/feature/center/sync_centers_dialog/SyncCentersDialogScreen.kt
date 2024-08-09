package com.mifos.feature.center.sync_centers_dialog

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.feature.center.R
import com.mifos.feature.center.sync_center_payloads.PayloadField

@Composable
fun SyncCenterDialogScreen(
    viewModel: SyncCentersDialogViewModel = hiltViewModel(),
    dismiss: () -> Unit,
    hide: () -> Unit,
) {
    val uiState by viewModel.syncCentersDialogUiState.collectAsStateWithLifecycle()
    val uiData by viewModel.syncCenterData.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.syncCenter()
    }

    SyncCenterDialogScreen(
        uiState = uiState,
        uiData = uiData,
        dismiss = dismiss,
        hide = hide
    )
}

@Composable
fun SyncCenterDialogScreen(
    uiState: SyncCentersDialogUiState,
    uiData: SyncCentersDialogData,
    dismiss: () -> Unit,
    hide: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Box {
        SyncGroupDialogContent(
            uiData = uiData,
            okClicked = dismiss,
            hideClicked = hide,
            cancelClicked = dismiss
        )

        when (uiState) {
            is SyncCentersDialogUiState.Success -> Unit

            is SyncCentersDialogUiState.Loading -> MifosCircularProgress()

            is SyncCentersDialogUiState.Error -> {
                val message = uiState.message
                    ?: uiState.messageResId?.let { stringResource(uiState.messageResId) }
                    ?: stringResource(id = R.string.feature_center_something_went_wrong)
                LaunchedEffect(key1 = message) {
                    snackBarHostState.showSnackbar(message = message)
                }
                dismiss()
            }
        }
    }
}

@Composable
fun SyncGroupDialogContent(
    uiData: SyncCentersDialogData,
    okClicked: () -> Unit,
    hideClicked: () -> Unit,
    cancelClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.feature_center_sync_centers_full_information)
        )

        PayloadField(
            label = stringResource(id = R.string.feature_center_name),
            value = uiData.centerName,
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(id = R.string.feature_center_total),
            value = uiData.centersList.size.toString() + stringResource(R.string.feature_center_space) + stringResource(
                com.mifos.feature.center.R.string.feature_center_center
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(id = R.string.feature_center_syncing_center),
            value = uiData.centerName,
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(id = R.string.feature_center_syncing_group),
            value = "syncing_group",
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(id = R.string.feature_center_syncing_client),
            value = "syncing_client",
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(id = R.string.feature_center_total_sync_progress),
            value = stringResource(R.string.feature_center_space) + uiData.totalSyncCount + stringResource(id = R.string.feature_center_slash) + uiData.centersList.size,
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(id = R.string.feature_center_failed_sync),
            value = uiData.failedSyncGroupCount.toString(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiData.isSyncSuccess) {
                FilledTonalButton(
                    onClick = { okClicked() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.feature_center_dialog_action_ok))
                }
            } else {
                FilledTonalButton(
                    onClick = { cancelClicked() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.feature_center_cancel))
                }

                Spacer(modifier = Modifier.width(10.dp))

                FilledTonalButton(
                    onClick = { hideClicked() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.feature_center_hide))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SyncCenterDialogScreenPreview() {
    SyncCenterDialogScreen(
        dismiss = { },
        uiState = SyncCentersDialogUiState.Success,
        uiData = SyncCentersDialogData(),
        hide = { }
    )
}