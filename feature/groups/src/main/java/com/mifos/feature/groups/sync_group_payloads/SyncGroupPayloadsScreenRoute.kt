package com.mifos.feature.groups.sync_group_payloads

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Network
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosErrorContent
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.objects.group.GroupPayload
import com.mifos.feature.groups.R

@Composable
fun SyncGroupPayloadsScreenRoute(
    viewModel: SyncGroupPayloadsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.syncGroupPayloadsUiState.collectAsStateWithLifecycle()
    val groupPayloadsList by viewModel.groupPayloadsList.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loanDatabaseGroupPayload()
    }

    SyncGroupPayloadsScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        refreshState = refreshState,
        groupPayloadsList = groupPayloadsList,
        onRefresh = {
            viewModel.refreshGroupPayload()
        },
        syncGroupPayloads = {
            viewModel.syncGroupPayloadFromStart()
        },
        userStatus = viewModel.getUserStatus()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncGroupPayloadsScreen(
    uiState: SyncGroupPayloadsUiState,
    onBackPressed: () -> Unit,
    refreshState: Boolean,
    groupPayloadsList: List<GroupPayload>,
    onRefresh: () -> Unit,
    syncGroupPayloads: () -> Unit,
    userStatus : Boolean
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val pullRefreshState = rememberPullToRefreshState()

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_groups_sync_groups),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                when (userStatus) {
                    false -> checkNetworkConnectionAndSync(
                        context = context,
                        syncGroupPayloads = syncGroupPayloads
                    )

                    true -> TODO("Implement OfflineModeDialog()")
                }
            }) {
                Icon(
                    MifosIcons.sync,
                    contentDescription = stringResource(id = R.string.feature_groups_sync)
                )
            }
        },
        snackbarHostState = snackBarHostState
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
            when (uiState) {
                is SyncGroupPayloadsUiState.Loading -> {
                    MifosCircularProgress()
                }

                is SyncGroupPayloadsUiState.Error -> {
                    MifosErrorContent(
                        message = stringResource(id = uiState.messageResId),
                        onRefresh = onRefresh,
                        refreshButtonText = stringResource(id = R.string.feature_groups_click_to_refresh)
                    )
                }

                is SyncGroupPayloadsUiState.Success -> {
                    if(uiState.emptyState != null) {
                        MifosErrorContent(
                            imageVector = ImageVector.vectorResource(id = uiState.emptyState.iconResId),
                            message = stringResource(id = uiState.emptyState.messageResId),
                            isRefreshEnabled = false
                        )
                    } else {
                        GroupPayloadsContent(groupPayloadsList)
                    }
                }
            }

            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        LaunchedEffect(key1 = refreshState) {
            if (refreshState)
                pullRefreshState.startRefresh()
        }

        LaunchedEffect(key1 = pullRefreshState.isRefreshing) {
            if (pullRefreshState.isRefreshing) {
                if (Network.isOnline(context)) {
                    onRefresh()
                } else {
                    Toast.makeText(context, context.resources.getText(R.string.feature_groups_error_not_connected_internet), Toast.LENGTH_SHORT,).show()
                }
                pullRefreshState.endRefresh()
            }
        }
    }
}

@Composable
fun GroupPayloadsContent(groupPayloadList: List<GroupPayload>) {
    LazyColumn {
        items(groupPayloadList) { payload ->
            GroupPayloadItem(payload)
        }
    }
}

@Composable
fun GroupPayloadItem(payload: GroupPayload) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val status: String = if (payload.active) {
                true.toString()
            } else {
                false.toString()
            }

            GroupPayloadField(stringResource(id = R.string.feature_groups_name), payload.name ?: "")
            GroupPayloadField(stringResource(id = R.string.feature_groups_external_id), payload.externalId ?: "")
            GroupPayloadField(
                stringResource(id = R.string.feature_groups_office_id),
                payload.officeId.toString() ?: ""
            )
            GroupPayloadField(
                stringResource(id = R.string.feature_groups_submit_date),
                payload.submittedOnDate ?: ""
            )
            GroupPayloadField(
                stringResource(id = R.string.feature_groups_activation_date),
                payload.activationDate ?: ""
            )
            GroupPayloadField(stringResource(id = R.string.feature_groups_active), status)

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
fun GroupPayloadField(label: String, value: String) {
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

fun checkNetworkConnectionAndSync(
    context: Context,
    syncGroupPayloads: () -> Unit
) {
    if (Network.isOnline(context)) {
        syncGroupPayloads()
    } else {
        Toast.makeText(
            context,
            context.getString(R.string.feature_groups_error_not_connected_internet),
            Toast.LENGTH_SHORT
        ).show()
    }
}


@Preview
@Composable
fun SyncGroupPayloadsScreenPreview() {
    SyncGroupPayloadsScreen(
        uiState = SyncGroupPayloadsUiState.Success(),
        onRefresh = { },
        onBackPressed = { },
        refreshState = false,
        syncGroupPayloads = { },
        groupPayloadsList = dummyGroupPayloads,
        userStatus = true
    )
}