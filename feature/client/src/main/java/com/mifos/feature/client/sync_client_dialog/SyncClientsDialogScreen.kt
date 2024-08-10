package com.mifos.feature.client.sync_client_dialog

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.feature.client.R

@Composable
fun SyncClientsDialogScreen(
    viewModel: SyncClientsDialogViewModel = hiltViewModel(),
    dismiss: () -> Unit,
    hide: () -> Unit,
) {
    val uiState by viewModel.syncClientsDialogUiState.collectAsStateWithLifecycle()
    val uiData by viewModel.syncClientData.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.syncClient()
    }

    SyncClientsDialogScreen(
        uiState = uiState,
        uiData = uiData,
        dismiss = dismiss,
        hide = hide
    )
}

@Composable
fun SyncClientsDialogScreen(
    uiState: SyncClientsDialogUiState,
    uiData: SyncClientsDialogData,
    dismiss: () -> Unit,
    hide: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Box {
        SyncClientsDialogContent(
            uiData = uiData,
            okClicked = dismiss,
            hideClicked = hide,
            cancelClicked = dismiss
        )

        when (uiState) {
            is SyncClientsDialogUiState.Success -> Unit

            is SyncClientsDialogUiState.Loading -> MifosCircularProgress()

            is SyncClientsDialogUiState.Error -> {
                val message = uiState.message
                    ?: uiState.messageResId?.let { stringResource(uiState.messageResId) }
                    ?: stringResource(id = R.string.feature_client_something_went_wrong)
                LaunchedEffect(key1 = message) {
                    snackBarHostState.showSnackbar(message = message)
                }
                dismiss()
            }
        }
    }
}

@Composable
fun SyncClientsDialogContent(
    uiData: SyncClientsDialogData,
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
            text = stringResource(id = R.string.feature_client_sync_clients_full_information)
        )

        PayloadField(
            label = stringResource(id = R.string.feature_client_name),
            value = uiData.clientName,
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(id = R.string.feature_client_total),
            value = uiData.clientList.size.toString() + stringResource(R.string.feature_client_space) + stringResource(
                R.string.feature_client_clients
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(id = R.string.feature_client_syncing_client),
            value = uiData.clientName,
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(id = R.string.feature_client_total_sync_progress),
            value = stringResource(R.string.feature_client_space) + uiData.totalSyncCount + stringResource(id = R.string.feature_client_slash) + uiData.clientList.size,
        )

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        PayloadField(
            label = stringResource(id = R.string.feature_client_failed_sync),
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
                    Text(text = stringResource(id = R.string.feature_client_dialog_action_ok))
                }
            } else {
                FilledTonalButton(
                    onClick = { cancelClicked() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.feature_client_cancel))
                }

                Spacer(modifier = Modifier.width(10.dp))

                FilledTonalButton(
                    onClick = { hideClicked() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.feature_client_hide))
                }
            }
        }
    }
}

@Composable
fun PayloadField(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SyncClientsDialogScreenPreview() {
    SyncClientsDialogScreen(
        dismiss = { },
        uiState = SyncClientsDialogUiState.Success,
        uiData = SyncClientsDialogData(),
        hide = { }
    )
}