package com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.runtime.mutableStateOf
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
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.objects.PaymentTypeOption
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.mifosxdroid.R
import com.mifos.utils.Network
import com.mifos.utils.PrefManager.userStatus
import com.mifos.utils.Utils.getPaymentTypeName

@Composable
fun SyncSavingsAccountTransactionScreenRoute(
    viewModel: SyncSavingsAccountTransactionViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.syncSavingsAccountTransactionUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()

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
        }
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SyncSavingsAccountTransactionScreen(
    uiState: SyncSavingsAccountTransactionUiState,
    onBackPressed: () -> Unit,
    refreshState: Boolean,
    onRefresh: () -> Unit,
    syncSavingsAccountTransactions: () -> Unit,
) {
    val snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }
    val context = LocalContext.current
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshState,
        onRefresh = onRefresh
    )

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.sync_savingsaccounttransactions),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                when (userStatus) {
                    false -> checkNetworkConnectionAndSync(context, syncSavingsAccountTransactions)
                    true -> TODO()//Implement OfflineModeDialog()
                }
            }) {
                Icon(Icons.Default.Sync, contentDescription = "Sync")
            }
        },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                when (uiState) {
                    is SyncSavingsAccountTransactionUiState.Loading -> {
                        MifosCircularProgress()
                    }

                    is SyncSavingsAccountTransactionUiState.ShowEmptySavingsAccountTransactions -> {
                        MifosEmptyUi(
                            text = stringResource(id = R.string.nothing_to_sync),
                            icon = MifosIcons.sync
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
                PullRefreshIndicator(
                    refreshing = refreshState,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
fun SavingsAccountTransactionItem(
    transaction: SavingsAccountTransactionRequest,
    paymentTypeOptions: List<PaymentTypeOption>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            TransactionRow(
                label = stringResource(R.string.savings_account_id),
                value = transaction.savingAccountId.toString()
            )
            TransactionRow(
                label = stringResource(R.string.payment_type),
                value = transaction.paymentTypeId?.toInt()?.let {
                    getPaymentTypeName(it, paymentTypeOptions)
                } ?: ""
            )
            TransactionRow(
                label = stringResource(R.string.transaction_type),
                value = transaction.transactionType ?: ""
            )
            TransactionRow(
                label = stringResource(R.string.transaction_amount),
                value = transaction.transactionAmount ?: ""
            )
            TransactionRow(
                label = stringResource(R.string.transaction_date),
                value = transaction.transactionDate ?: ""
            )

            if (transaction.errorMessage != null) {
                Text(
                    text = transaction.errorMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TransactionRow(label: String, value: String) {
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
            fontWeight = FontWeight.Bold,
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
        Text(text = message, modifier = Modifier.padding(vertical = 8.dp))
        Button(onClick = onRefresh) {
            Text(stringResource(id = com.github.therajanmaurya.sweeterror.R.string.retry))
        }
    }
}

fun checkNetworkConnectionAndSync(
    context: Context,
    syncSavingsAccountTransactions: () -> Unit
) {
    if (Network.isOnline(context)) {
        syncSavingsAccountTransactions()
    } else {
        Toast.makeText(
            context,
            context.resources.getString(R.string.error_not_connected_internet),
            Toast.LENGTH_SHORT
        ).show()
    }
}


class SyncSavingsAccountTransactionUiStateProvider :
    PreviewParameterProvider<SyncSavingsAccountTransactionUiState> {
    override val values = sequenceOf(
        SyncSavingsAccountTransactionUiState.Loading,
        SyncSavingsAccountTransactionUiState.ShowError(R.string.failed_to_load_savingaccounttransaction),
        SyncSavingsAccountTransactionUiState.ShowEmptySavingsAccountTransactions(R.string.no_transaction_to_sync),
        SyncSavingsAccountTransactionUiState.ShowSavingsAccountTransactions(
            sampleSavingsAccountTransactions.toMutableList(),
            samplePaymentTypeOptions
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun SyncSavingsAccountTransactionScreenPreview(
    @PreviewParameter(SyncSavingsAccountTransactionUiStateProvider::class) state: SyncSavingsAccountTransactionUiState
) {
    SyncSavingsAccountTransactionScreen(
        uiState = state,
        onBackPressed = {},
        refreshState = false,
        onRefresh = {},
        syncSavingsAccountTransactions = {}
    )
}

// Sample data for previews
val sampleSavingsAccountTransactions = List(5) { index ->
    SavingsAccountTransactionRequest(
        savingAccountId = index,
        transactionDate = "2023-07-${15 + index}",
        transactionAmount = "${100 + index * 10}",
        paymentTypeId = index.toLong().toString(),
        transactionType = if (index % 2 == 0) "deposit" else "withdrawal",
        accountNumber = "ACC-$index",
        checkNumber = "CHK-$index",
        routingCode = "RTG-$index",
        receiptNumber = "RCP-$index",
        bankNumber = "BNK-$index"
    )
}

val samplePaymentTypeOptions = List(3) { index ->
    PaymentTypeOption(
        id = index,
        name = "Payment Type $index",
        description = "Description for Payment Type $index",
        isCashPayment = index % 2 == 0,
        position = index
    )
}

// Individual preview for SavingsAccountTransactionItem
@Preview(showBackground = true)
@Composable
fun SavingsAccountTransactionItemPreview() {
    SavingsAccountTransactionItem(
        transaction = sampleSavingsAccountTransactions[0],
        paymentTypeOptions = samplePaymentTypeOptions
    )
}