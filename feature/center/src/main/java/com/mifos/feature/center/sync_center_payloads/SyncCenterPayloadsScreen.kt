package com.mifos.feature.center.sync_center_payloads

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.TurnedIn
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Network
import com.mifos.core.data.CenterPayload
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.feature.center.R


@Composable
fun SyncCenterPayloadsScreenRoute(
    viewModel: SyncCenterPayloadsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.syncCenterPayloadsUiState.collectAsStateWithLifecycle()
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
        userStatus = viewModel.getUserStatus()
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SyncCenterPayloadsScreen(
    uiState: SyncCenterPayloadsUiState,
    onBackPressed: () -> Unit,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    syncCenterPayloads: () -> Unit,
    userStatus : Boolean
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = onRefresh)

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_center_sync_centers_payloads),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                when (userStatus) {
                    false -> checkNetworkConnectionAndSync(context, syncCenterPayloads)
                    true -> TODO("Implement OfflineModeDialog()")
                }
            }) {
                Icon(
                    MifosIcons.sync,
                    contentDescription = stringResource(id = R.string.feature_center_sync_centers)
                )
            }
        },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            when (uiState) {
                is SyncCenterPayloadsUiState.ShowProgressbar -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SyncCenterPayloadsUiState.ShowError -> {
                    ErrorState(message = uiState.message, onRefresh = onRefresh)
                }
                is SyncCenterPayloadsUiState.ShowCenters -> {
                    CenterPayloadsList(centerPayloads = uiState.centerPayloads)
                }
            }
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun CenterPayloadsList(centerPayloads: List<CenterPayload>) {
    if (centerPayloads.isEmpty()) {
        EmptyState()
    } else {
        LazyColumn {
            items(centerPayloads) { payload ->
                CenterPayloadItem(payload)
            }
        }
    }
}

@Composable
fun CenterPayloadItem(payload: CenterPayload) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            PayloadField(
                label = stringResource(R.string.feature_center_name),
                value = payload.name ?: ""
            )
            PayloadField(
                label = stringResource(R.string.feature_center_office_id),
                value = payload.officeId?.toString() ?: ""
            )
            PayloadField(
                label = stringResource(R.string.feature_center_activation_date),
                value = payload.activationDate ?: ""
            )
            PayloadField(
                label = stringResource(R.string.feature_center_active),
                value = if (payload.active) true.toString() else false.toString()
            )
            payload.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
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

@Composable
fun ErrorState(message: String, onRefresh: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Text(text = message, modifier = Modifier.padding(vertical = 16.dp))
        Button(onClick = onRefresh) {
            Text(stringResource(id = R.string.feature_center_click_to_refresh))
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector =  Icons.Default.TurnedIn,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = stringResource(id = R.string.feature_center_no_center_payload_to_sync),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

fun checkNetworkConnectionAndSync(
    context: Context,
    syncCenterPayloads: () -> Unit
) {
    if (Network.isOnline(context)) {
        syncCenterPayloads()
    } else {
        Toast.makeText(
            context,
            context.getString(R.string.feature_center_error_not_connected_internet),
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Preview(showBackground = true)
@Composable
fun SyncCenterPayloadsScreenPreview(
    @PreviewParameter(SyncCenterPayloadsUiStateProvider::class) uiState: SyncCenterPayloadsUiState
) {
    SyncCenterPayloadsScreen(
        uiState = uiState,
        onBackPressed = {},
        refreshing = false,
        onRefresh = {},
        syncCenterPayloads = {},
        userStatus = true
    )
}

class SyncCenterPayloadsUiStateProvider : PreviewParameterProvider<SyncCenterPayloadsUiState> {
    override val values = sequenceOf(
        SyncCenterPayloadsUiState.ShowProgressbar,
        SyncCenterPayloadsUiState.ShowError("Failed to load center payloads"),
        SyncCenterPayloadsUiState.ShowCenters(sampleCenterPayloads)
    )
}

// Sample data for previews
val sampleCenterPayloads = List(5) { index ->
    CenterPayload().apply {
        name = "Center $index"
        officeId = index + 1
        activationDate = "2023-07-${15 + index}"
        active = index % 2 == 0
        errorMessage = if (index % 3 == 0) "Error in payload" else null
    }
}

@Preview(showBackground = true)
@Composable
fun CenterPayloadItemPreview() {
    val sampleCenterPayload = CenterPayload().apply {
        name = "Sample Center"
        officeId = 12345
        activationDate = "2023-07-15"
        active = true
        errorMessage = null
    }

    CenterPayloadItem(payload = sampleCenterPayload)
}

@Preview(showBackground = true)
@Composable
fun ErrorStatePreview() {
    ErrorState(
        message = "Failed to load center payloads",
        onRefresh = {}
    )
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    EmptyState()
}

@Preview(showBackground = true)
@Composable
fun PayloadFieldPreview() {
    PayloadField(label = "Sample Label", value = "Sample Value")
}