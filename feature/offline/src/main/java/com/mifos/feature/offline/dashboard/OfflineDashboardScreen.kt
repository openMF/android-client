/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.dashboard

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.feature.offline.R

/**
 * Created by Pronay Sarker on 27/08/2024 (12:09 AM)
 */
@Composable
internal fun OfflineDashboardRoute(
    onBackPressed: () -> Unit,
    syncClientPayload: () -> Unit,
    syncGroupPayload: () -> Unit,
    syncCenterPayload: () -> Unit,
    syncLoanRepayment: () -> Unit,
    syncSavingsAccountTransactions: () -> Unit,
    viewModel: OfflineDashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.offlineDashboardUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadDatabaseClientPayload()
        viewModel.loadDatabaseGroupPayload()
        viewModel.loadDatabaseCenterPayload()
        viewModel.loadDatabaseLoanRepaymentTransactions()
        viewModel.loadDatabaseSavingsAccountTransactions()
    }

    OfflineDashboardScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        syncClientPayload = syncClientPayload,
        syncGroupPayload = syncGroupPayload,
        syncCenterPayload = syncCenterPayload,
        syncSavingsAccountTransactions = syncSavingsAccountTransactions,
        syncLoanRepayment = syncLoanRepayment,
    )
}

@Composable
internal fun OfflineDashboardScreen(
    uiState: OfflineDashboardUiState,
    onBackPressed: () -> Unit,
    syncClientPayload: () -> Unit,
    syncGroupPayload: () -> Unit,
    syncCenterPayload: () -> Unit,
    syncLoanRepayment: () -> Unit,
    modifier: Modifier = Modifier,
    syncSavingsAccountTransactions: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var errorVisibility by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = uiState) {
        if (uiState is OfflineDashboardUiState.SyncUiState) {
            var mPayloadIndex = 5

            uiState.list.forEach { item ->
                Log.d("itemCountOfflineSync", "${item.count}")
                if (item.count == 0) {
                    mPayloadIndex -= 1
                    if (mPayloadIndex == 0) {
                        errorVisibility = true
                    }
                } else {
                    errorVisibility = false
                }
            }
        }
    }

    MifosScaffold(
        modifier = modifier,
        snackbarHostState = snackBarHostState,
        onBackPressed = onBackPressed,
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_offline_offline_Sync),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            if (errorVisibility) {
                MifosEmptyUi(
                    text = stringResource(id = R.string.feature_offline_nothing_to_sync),
                    icon = MifosIcons.assignmentTurnedIn,
                )
            } else {
                when (uiState) {
                    is OfflineDashboardUiState.SyncUiState -> {
                        LazyColumn {
                            items(uiState.list) { item ->
                                if (item.count != 0) {
                                    OfflineDashboardItemCard(
                                        paymentItem = item.name,
                                        count = item.count,
                                        onClick = {
                                            when (item.type) {
                                                Type.SYNC_CLIENTS -> syncClientPayload()
                                                Type.SYNC_GROUPS -> syncGroupPayload()
                                                Type.SYNC_CENTERS -> syncCenterPayload()
                                                Type.SYNC_LOAN_REPAYMENTS -> syncLoanRepayment()
                                                Type.SYNC_SAVINGS_ACCOUNT_TRANSACTION -> syncSavingsAccountTransactions()
                                            }
                                        },
                                    )
                                }

                                LaunchedEffect(key1 = item.errorMsg) {
                                    if (item.errorMsg != null) {
                                        Toast.makeText(context, item.errorMsg, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OfflineDashboardItemCard(
    modifier: Modifier = Modifier,
    paymentItem: Int = -1,
    count: Int = 0,
    onClick: () -> Unit,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(id = paymentItem),
                style = MaterialTheme.typography.bodyLarge,
            )

            Text(
                text = "$count",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun PreviewOfflineDashboardScreen(modifier: Modifier = Modifier) {
    val data = listOf(
        SyncStateData(
            count = 3,
            name = R.string.feature_offline_sync_clients,
            type = Type.SYNC_CLIENTS,
        ),
        SyncStateData(
            count = 3,
            name = R.string.feature_offline_sync_groups,
            type = Type.SYNC_GROUPS,
        ),
        SyncStateData(
            count = 2,
            name = R.string.feature_offline_sync_centers,
            type = Type.SYNC_CENTERS,
        ),
        SyncStateData(
            count = 4,
            name = R.string.feature_offline_sync_loanRepayments,
            type = Type.SYNC_LOAN_REPAYMENTS,
        ),
        SyncStateData(
            count = 5,
            name = R.string.feature_offline_sync_savingsAccountTransactions,
            type = Type.SYNC_SAVINGS_ACCOUNT_TRANSACTION,
        ),
    )

    OfflineDashboardScreen(
        modifier = modifier,
        uiState = OfflineDashboardUiState.SyncUiState(data),
        onBackPressed = { },
        syncClientPayload = { },
        syncGroupPayload = { },
        syncCenterPayload = { },
        syncLoanRepayment = { },
    ) {
    }
}

enum class Type {
    SYNC_CLIENTS,
    SYNC_GROUPS,
    SYNC_CENTERS,
    SYNC_LOAN_REPAYMENTS,
    SYNC_SAVINGS_ACCOUNT_TRANSACTION,
}
