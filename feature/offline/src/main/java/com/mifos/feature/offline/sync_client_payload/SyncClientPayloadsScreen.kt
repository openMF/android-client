package com.mifos.feature.offline.sync_client_payload

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.objects.client.ClientPayload
import com.mifos.feature.offline.R

@Composable
fun SyncClientPayloadsScreenRoute(
    viewModel: SyncClientPayloadsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.syncClientPayloadsUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()

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
        userStatus = viewModel.getUserStatus()
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SyncClientPayloadsScreen(
    uiState: SyncClientPayloadsUiState,
    onBackPressed: () -> Unit,
    refreshState: Boolean,
    onRefresh: () -> Unit,
    syncClientPayloads: () -> Unit,
    userStatus : Boolean
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshState, onRefresh = onRefresh)

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_offline_sync_clients_payloads),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                when (userStatus) {
                    false -> checkNetworkConnectionAndSync(context, syncClientPayloads)
                    true -> TODO("Implement OfflineModeDialog()")
                }
            }) {
                Icon(
                    MifosIcons.sync,
                    contentDescription = stringResource(id = R.string.feature_offline_sync_clients)
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
            PullRefreshIndicator(
                refreshing = refreshState,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun ClientPayloadsList(clientPayloads: List<ClientPayload>) {
    LazyColumn {
        items(clientPayloads) { payload ->
            ClientPayloadItem(payload)
        }
    }
}

@Composable
fun ClientPayloadItem(payload: ClientPayload) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val payloadStatus: String = if(payload.active == true){
                "true"
            }else{
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

            PayloadField(stringResource(id = R.string.feature_offline_first_name), payload.firstname ?: "")
            PayloadField(stringResource(id = R.string.feature_offline_middle_name), payload.middlename ?: "")
            PayloadField(stringResource(id = R.string.feature_offline_last_name), payload.lastname ?: "")
            PayloadField(stringResource(id = R.string.feature_offline_mobile_no), payload.mobileNo ?: "")
            PayloadField(stringResource(id = R.string.feature_offline_external_id), payload.externalId ?: "")
            PayloadField(stringResource(id = R.string.feature_offline_gender), gender)
            PayloadField(stringResource(id = R.string.feature_offline_dob), payload.dateOfBirth ?: "")
            PayloadField(stringResource(id = R.string.feature_offline_office_id), payload.officeId?.toString() ?: "")
            PayloadField(stringResource(id = R.string.feature_offline_activation_date), payload.activationDate ?: "")
            PayloadField(stringResource(id = R.string.feature_offline_active), payloadStatus)

            if (payload.errorMessage != null) {
                Text(
                    text = payload.errorMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
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
fun ErrorStateScreen(message: String, onRefresh: () -> Unit) {
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
            Text(stringResource(id = R.string.feature_offline_click_to_refresh))
        }
    }
}

fun checkNetworkConnectionAndSync(
    context: Context,
    syncClientPayloads: () -> Unit
) {
    if (Network.isOnline(context)) {
        syncClientPayloads()
    } else {
        Toast.makeText(
            context,
            context.getString(R.string.feature_offline_error_not_connected_internet),
            Toast.LENGTH_SHORT
        ).show()
    }
}

class SyncClientPayloadsUiStateProvider : PreviewParameterProvider<SyncClientPayloadsUiState> {
    override val values = sequenceOf(
        SyncClientPayloadsUiState.ShowProgressbar,
        SyncClientPayloadsUiState.ShowError("Failed to load client payloads"),
        SyncClientPayloadsUiState.ShowPayloads(sampleClientPayloads)
    )
}

@Preview(showBackground = true)
@Composable
private fun SyncClientPayloadsScreenPreview(
    @PreviewParameter(SyncClientPayloadsUiStateProvider::class) uiState: SyncClientPayloadsUiState
) {
    SyncClientPayloadsScreen(
        uiState = uiState,
        onBackPressed = {},
        refreshState = false,
        onRefresh = {},
        syncClientPayloads = {},
        userStatus = true
    )
}

// Sample data for previews
val sampleClientPayloads = List(5) { index ->
    ClientPayload().apply {
        firstname = "John$index"
        middlename = "Sam$index"
        lastname = "Doe$index"
        mobileNo = "123456789$index"
        externalId = "EXT-$index"
        officeId = index
        active = index % 2 == 0
        activationDate = "2023-07-${15 + index}"
        genderId = if (index % 3 == 0) 24 else 22
        dateOfBirth = "1990-01-0$index"
        errorMessage = if (index % 2 == 0) null else "Error in payload"
    }
}

@Preview(showBackground = true)
@Composable
fun ClientPayloadItemPreview() {
    val sampleClientPayload = ClientPayload().apply {
        firstname = "John"
        middlename = "Michael"
        lastname = "Doe"
        mobileNo = "1234567890"
        externalId = "EXT-001"
        genderId = 22
        dateOfBirth = "1990-01-01"
        officeId = 12
        activationDate = "2023-07-15"
        active = true
        errorMessage = null
    }

    ClientPayloadItem(payload = sampleClientPayload)
}

class PayloadFieldPreviewProvider : PreviewParameterProvider<Pair<String, String>> {
    override val values = sequenceOf(
        "First Name" to "John",
        "Last Name" to "Doe",
        "Mobile No" to "1234567890",
        "External ID" to "EXT-001",
        "Gender" to "Male",
        "Date of Birth" to "1990-01-01",
        "Office ID" to "12345",
        "Activation Date" to "2023-07-15",
        "Active" to "true"
    )
}

@Preview(showBackground = true)
@Composable
fun PayloadFieldPreview(
    @PreviewParameter(PayloadFieldPreviewProvider::class) labelValuePair: Pair<String, String>
) {
    PayloadField(label = labelValuePair.first, value = labelValuePair.second)
}
