/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncSavingsAccountTransaction

import androidclient.feature.offline.generated.resources.Res
import androidclient.feature.offline.generated.resources.feature_offline_error_not_connected_internet
import androidclient.feature.offline.generated.resources.feature_offline_failed_to_load_savingaccounttransaction
import androidclient.feature.offline.generated.resources.feature_offline_no_transaction_to_sync
import androidclient.feature.offline.generated.resources.feature_offline_nothing_to_sync
import androidclient.feature.offline.generated.resources.feature_offline_payment_type
import androidclient.feature.offline.generated.resources.feature_offline_retry
import androidclient.feature.offline.generated.resources.feature_offline_savings_account_id
import androidclient.feature.offline.generated.resources.feature_offline_sync_savingsAccountTransactions
import androidclient.feature.offline.generated.resources.feature_offline_transaction_amount
import androidclient.feature.offline.generated.resources.feature_offline_transaction_date
import androidclient.feature.offline.generated.resources.feature_offline_transaction_type
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
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
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SyncSavingsAccountTransactionScreenRoute(
    viewModel: SyncSavingsAccountTransactionViewModel = koinViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.syncSavingsAccountTransactionUiState.collectAsStateWithLifecycle()
    val userStatus by viewModel.userStatus.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isOnline by viewModel.isNetworkAvailable.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadDatabaseSavingsAccountTransactions()
        viewModel.loadPaymentTypeOption()
    }

    SyncSavingsAccountTransactionScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        refreshState = refreshState,
        onRefresh = {
            viewModel.refreshTransactions()
        },
        syncSavingsAccountTransactions = {
            viewModel.syncSavingsAccountTransactions()
        },
        userStatus = userStatus,
        isOnline = isOnline,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SyncSavingsAccountTransactionScreen(
    uiState: SyncSavingsAccountTransactionUiState,
    onBackPressed: () -> Unit,
    refreshState: Boolean,
    onRefresh: () -> Unit,
    syncSavingsAccountTransactions: () -> Unit,
    userStatus: Boolean,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }
    val pullToRefreshState = rememberPullToRefreshState()
    val offlineMessage = stringResource(Res.string.feature_offline_error_not_connected_internet)
    val scope = rememberCoroutineScope()

    MifosScaffold(
        modifier = modifier,
        title = stringResource(Res.string.feature_offline_sync_savingsAccountTransactions),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(
                onClick = {
                    when (userStatus) {
                        false -> when (isOnline) {
                            true -> syncSavingsAccountTransactions
                            false -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(offlineMessage)
                                }
                            }
                        }

                        true -> TODO() // Implement OfflineModeDialog()
                    }
                },
            ) {
                Icon(MifosIcons.Sync, contentDescription = "Sync")
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = refreshState,
                modifier = Modifier.fillMaxSize(),
                onRefresh = onRefresh,
                contentAlignment = Alignment.TopCenter,
            ) {
                when (uiState) {
                    is SyncSavingsAccountTransactionUiState.Loading -> {
                        MifosCircularProgress()
                    }

                    is SyncSavingsAccountTransactionUiState.ShowEmptySavingsAccountTransactions -> {
                        MifosEmptyUi(
                            text = stringResource(Res.string.feature_offline_nothing_to_sync),
                            icon = MifosIcons.Sync,
                        )
                    }

                    is SyncSavingsAccountTransactionUiState.ShowError -> {
                        val message = uiState.message
                        ErrorStateScreen(message.toString(), onRefresh)
                    }

                    is SyncSavingsAccountTransactionUiState.ShowSavingsAccountTransactions -> {
                        val transactions = uiState.savingsList
                        val paymentTypeOptions = uiState.paymentTypeOptions
                        LazyColumn {
                            items(transactions) { transaction ->
                                SavingsAccountTransactionItem(transaction, paymentTypeOptions)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavingsAccountTransactionItem(
    transaction: SavingsAccountTransactionRequestEntity,
    paymentTypeOptions: List<PaymentTypeOptionEntity>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            TransactionRow(
                label = stringResource(Res.string.feature_offline_savings_account_id),
                value = transaction.savingAccountId.toString(),
            )
            TransactionRow(
                label = stringResource(Res.string.feature_offline_payment_type),
                value = transaction.paymentTypeId?.toInt()?.let {
                    getPaymentTypeName(it, paymentTypeOptions)
                } ?: "",
            )
            TransactionRow(
                label = stringResource(Res.string.feature_offline_transaction_type),
                value = transaction.transactionType ?: "",
            )
            TransactionRow(
                label = stringResource(Res.string.feature_offline_transaction_amount),
                value = transaction.transactionAmount ?: "",
            )
            TransactionRow(
                label = stringResource(Res.string.feature_offline_transaction_date),
                value = transaction.transactionDate ?: "",
            )

            if (transaction.errorMessage != null) {
                Text(
                    text = transaction.errorMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun TransactionRow(
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
            fontWeight = FontWeight.Bold,
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
        Text(text = message, modifier = Modifier.padding(vertical = 8.dp))
        Button(onClick = onRefresh) {
            Text(stringResource(Res.string.feature_offline_retry))
        }
    }
}

fun getPaymentTypeName(
    paymentId: Int,
    paymentTypeOptions: List<PaymentTypeOptionEntity>?,
): String? {
    return paymentTypeOptions
        ?.firstOrNull { it.id == paymentId }
        ?.name
}

class SyncSavingsAccountTransactionUiStateProvider :
    PreviewParameterProvider<SyncSavingsAccountTransactionUiState> {

    private val sampleSavingsAccountTransactions = List(5) { index ->
        SavingsAccountTransactionRequestEntity(
            savingAccountId = index,
            transactionDate = "2023-07-${15 + index}",
            transactionAmount = "${100 + index * 10}",
            paymentTypeId = index.toLong().toString(),
            transactionType = if (index % 2 == 0) "deposit" else "withdrawal",
            accountNumber = "ACC-$index",
            checkNumber = "CHK-$index",
            routingCode = "RTG-$index",
            receiptNumber = "RCP-$index",
            bankNumber = "BNK-$index",
        )
    }
    private val samplePaymentTypeOptions = List(3) { index ->
        PaymentTypeOptionEntity(
            id = index,
            name = "Payment Type $index",
            description = "Description for Payment Type $index",
            isCashPayment = index % 2 == 0,
            position = index,
        )
    }

    override val values = sequenceOf(
        SyncSavingsAccountTransactionUiState.Loading,
        SyncSavingsAccountTransactionUiState.ShowError(Res.string.feature_offline_failed_to_load_savingaccounttransaction),
        SyncSavingsAccountTransactionUiState.ShowEmptySavingsAccountTransactions(Res.string.feature_offline_no_transaction_to_sync),
        SyncSavingsAccountTransactionUiState.ShowSavingsAccountTransactions(
            sampleSavingsAccountTransactions.toMutableList(),
            samplePaymentTypeOptions,
        ),
    )
}

@Composable
@Preview
private fun SyncSavingsAccountTransactionScreenPreview(
    @PreviewParameter(SyncSavingsAccountTransactionUiStateProvider::class)
    state: SyncSavingsAccountTransactionUiState,
) {
    SyncSavingsAccountTransactionScreen(
        uiState = state,
        onBackPressed = {},
        refreshState = false,
        onRefresh = {},
        syncSavingsAccountTransactions = {},
        userStatus = true,
        isOnline = true,
    )
}
