/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncLoanRepaymentTransaction

import androidclient.feature.offline.generated.resources.Res
import androidclient.feature.offline.generated.resources.feature_offline_account_number
import androidclient.feature.offline.generated.resources.feature_offline_click_to_refresh
import androidclient.feature.offline.generated.resources.feature_offline_error_not_connected_internet
import androidclient.feature.offline.generated.resources.feature_offline_failed_to_load_loanrepayment
import androidclient.feature.offline.generated.resources.feature_offline_loan_id
import androidclient.feature.offline.generated.resources.feature_offline_loan_transaction_date
import androidclient.feature.offline.generated.resources.feature_offline_payment_type
import androidclient.feature.offline.generated.resources.feature_offline_sync_loanrepayment
import androidclient.feature.offline.generated.resources.feature_offline_transaction_amount
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.feature.offline.syncSavingsAccountTransaction.getPaymentTypeName
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SyncLoanRepaymentTransactionScreenRoute(
    viewModel: SyncLoanRepaymentTransactionViewModel = koinViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.syncLoanRepaymentTransactionUiState.collectAsStateWithLifecycle()
    val userStatus by viewModel.userStatus.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isOnline by viewModel.isNetworkAvailable.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadDatabaseLoanRepaymentTransactions()
        viewModel.loanPaymentTypeOption()
    }

    SyncLoanRepaymentTransactionScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        refreshState = refreshState,
        onRefresh = {
            viewModel.refreshTransactions()
        },
        syncLoanRepaymentTransactions = {
            viewModel.syncGroupPayload()
        },
        userStatus = userStatus,
        isOnline = isOnline,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SyncLoanRepaymentTransactionScreen(
    uiState: SyncLoanRepaymentTransactionUiState,
    onBackPressed: () -> Unit,
    refreshState: Boolean,
    onRefresh: () -> Unit,
    syncLoanRepaymentTransactions: () -> Unit,
    userStatus: Boolean,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()
    val offlineMessage = stringResource(Res.string.feature_offline_error_not_connected_internet)
    val scope = rememberCoroutineScope()

    MifosScaffold(
        modifier = modifier,
        title = stringResource(Res.string.feature_offline_sync_loanrepayment),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(
                onClick = {
                    when (userStatus) {
                        false -> when (isOnline) {
                            true -> syncLoanRepaymentTransactions()
                            false -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(offlineMessage)
                                }
                            }
                        }

                        true -> TODO("Implement OfflineModeDialog()")
                    }
                },
            ) {
                Icon(
                    MifosIcons.Sync,
                    contentDescription = stringResource(Res.string.feature_offline_sync_loanrepayment),
                )
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            PullToRefreshBox(
                modifier = Modifier.fillMaxSize(),
                isRefreshing = refreshState,
                onRefresh = onRefresh,
                state = pullToRefreshState,
                contentAlignment = Alignment.TopCenter,
            ) {
                when (uiState) {
                    is SyncLoanRepaymentTransactionUiState.ShowProgressbar -> {
                        MifosCircularProgress()
                    }

                    is SyncLoanRepaymentTransactionUiState.ShowError -> {
                        ErrorStateScreen(uiState.message.toString(), onRefresh = onRefresh)
                    }

                    is SyncLoanRepaymentTransactionUiState.ShowLoanRepaymentTransactions -> {
                        LoanRepaymentTransactionsList(
                            uiState.loanRepaymentRequests,
                            uiState.paymentTypeOptions,
                        )
                    }

                    is SyncLoanRepaymentTransactionUiState.ShowEmptyLoanRepayments -> {
                        EmptyLoanRepaymentsScreen(uiState.message)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoanRepaymentTransactionsList(
    loanRepaymentRequests: List<LoanRepaymentRequestEntity>,
    paymentTypeOptions: List<PaymentTypeOptionEntity>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(loanRepaymentRequests) { request ->
            LoanRepaymentTransactionItem(request, paymentTypeOptions)
        }
    }
}

@Composable
private fun LoanRepaymentTransactionItem(
    request: LoanRepaymentRequestEntity,
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
                stringResource(Res.string.feature_offline_loan_id),
                request.loanId.toString(),
            )
            TransactionRow(
                stringResource(Res.string.feature_offline_account_number),
                request.accountNumber ?: "",
            )
            TransactionRow(
                stringResource(Res.string.feature_offline_payment_type),
                request.paymentTypeId?.let { getPaymentTypeName(it.toInt(), paymentTypeOptions) }
                    ?: "",
            )
            TransactionRow(
                stringResource(Res.string.feature_offline_transaction_amount),
                request.transactionAmount ?: "",
            )
            TransactionRow(
                stringResource(Res.string.feature_offline_loan_transaction_date),
                request.transactionDate ?: "",
            )

            if (request.errorMessage != null) {
                Text(
                    text = request.errorMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
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
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ErrorStateScreen(
    message: String,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit,
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
        Text(text = message, modifier = Modifier.padding(vertical = 6.dp))
        Button(onClick = onRefresh) {
            Text(stringResource(Res.string.feature_offline_click_to_refresh))
        }
    }
}

@Composable
private fun EmptyLoanRepaymentsScreen(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = MifosIcons.AssignmentTurnedIn,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private class SyncLoanRepaymentTransactionUiStateProvider :
    PreviewParameterProvider<SyncLoanRepaymentTransactionUiState> {
    override val values = sequenceOf(
        SyncLoanRepaymentTransactionUiState.ShowProgressbar,
        SyncLoanRepaymentTransactionUiState.ShowError(Res.string.feature_offline_failed_to_load_loanrepayment),
        SyncLoanRepaymentTransactionUiState.ShowEmptyLoanRepayments("No loan repayments to sync"),
        SyncLoanRepaymentTransactionUiState.ShowLoanRepaymentTransactions(
            sampleLoanRepaymentRequests,
            samplePaymentTypeOptions,
        ),
    )
}

private val sampleLoanRepaymentRequests = List(5) { index ->
    LoanRepaymentRequestEntity(
        loanId = index,
        accountNumber = "LOAN-$index",
        paymentTypeId = index.toString(),
        transactionAmount = "${1000 + index * 100}",
        transactionDate = "2023-07-${15 + index}",
        errorMessage = if (index % 2 == 0) null else "Error in transaction",
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

@Composable
@Preview
private fun SyncLoanRepaymentTransactionScreenPreview(
    @PreviewParameter(SyncLoanRepaymentTransactionUiStateProvider::class) uiState: SyncLoanRepaymentTransactionUiState,
) {
    SyncLoanRepaymentTransactionScreen(
        uiState = uiState,
        onBackPressed = {},
        refreshState = false,
        onRefresh = {},
        syncLoanRepaymentTransactions = {},
        userStatus = true,
        isOnline = true,
    )
}
