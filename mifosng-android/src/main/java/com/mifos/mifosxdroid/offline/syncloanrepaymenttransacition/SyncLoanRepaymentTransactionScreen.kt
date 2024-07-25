package com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
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
import androidx.compose.ui.text.style.TextAlign
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
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.mifosxdroid.R
import com.mifos.utils.Network
import com.mifos.utils.PrefManager.userStatus
import com.mifos.utils.Utils.getPaymentTypeName

@Composable
fun SyncLoanRepaymentTransactionScreenRoute(
    viewModel: SyncLoanRepaymentTransactionViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.syncLoanRepaymentTransactionUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()

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
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SyncLoanRepaymentTransactionScreen(
    uiState: SyncLoanRepaymentTransactionUiState,
    onBackPressed: () -> Unit,
    refreshState: Boolean,
    onRefresh: () -> Unit,
    syncLoanRepaymentTransactions: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val pullRefreshState =
        rememberPullRefreshState(refreshing = refreshState, onRefresh = onRefresh)

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.sync_loanrepayment),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                when (userStatus) {
                    false -> checkNetworkConnectionAndSync(context, syncLoanRepaymentTransactions)
                    true -> TODO("Implement OfflineModeDialog()")
                }
            }) {
                Icon(
                    MifosIcons.sync,
                    contentDescription = stringResource(id = R.string.sync_loanrepayment)
                )
            }
        },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                when (uiState) {
                    is SyncLoanRepaymentTransactionUiState.ShowProgressbar -> {
                        MifosCircularProgress()
                    }

                    is SyncLoanRepaymentTransactionUiState.ShowError -> {
                        ErrorStateScreen(uiState.message.toString(), onRefresh)
                    }

                    is SyncLoanRepaymentTransactionUiState.ShowLoanRepaymentTransactions -> {
                        LoanRepaymentTransactionsList(
                            uiState.loanRepaymentRequests,
                            uiState.paymentTypeOptions
                        )
                    }

                    is SyncLoanRepaymentTransactionUiState.ShowEmptyLoanRepayments -> {
                        EmptyLoanRepaymentsScreen(uiState.message)
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
fun LoanRepaymentTransactionsList(
    loanRepaymentRequests: List<LoanRepaymentRequest>,
    paymentTypeOptions: List<PaymentTypeOption>
) {
    LazyColumn {
        items(loanRepaymentRequests) { request ->
            LoanRepaymentTransactionItem(request, paymentTypeOptions)
        }
    }
}

@Composable
fun LoanRepaymentTransactionItem(
    request: LoanRepaymentRequest,
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
            TransactionRow(stringResource(R.string.loan_id), request.loanId.toString())
            TransactionRow(stringResource(R.string.account_number), request.accountNumber ?: "")
            TransactionRow(
                stringResource(R.string.payment_type),
                request.paymentTypeId?.let { getPaymentTypeName(it.toInt(), paymentTypeOptions) }
                    ?: ""
            )
            TransactionRow(
                stringResource(R.string.transaction_amount),
                request.transactionAmount ?: ""
            )
            TransactionRow(
                stringResource(R.string.loan_transaction_date),
                request.transactionDate ?: ""
            )

            if (request.errorMessage != null) {
                Text(
                    text = request.errorMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
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
        Text(text = message, modifier = Modifier.padding(vertical = 6.dp))
        Button(onClick = onRefresh) {
            Text(stringResource(id = R.string.click_to_refresh))
        }
    }
}

@Composable
fun EmptyLoanRepaymentsScreen(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AssignmentTurnedIn,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun checkNetworkConnectionAndSync(
    context: Context,
    syncLoanRepaymentTransactions: () -> Unit
) {
    if (Network.isOnline(context)) {
        syncLoanRepaymentTransactions()
    } else {
        Toast.makeText(
            context,
            context.getString(R.string.error_not_connected_internet),
            Toast.LENGTH_SHORT
        ).show()
    }
}

class SyncLoanRepaymentTransactionUiStateProvider :
    PreviewParameterProvider<SyncLoanRepaymentTransactionUiState> {
    override val values = sequenceOf(
        SyncLoanRepaymentTransactionUiState.ShowProgressbar,
        SyncLoanRepaymentTransactionUiState.ShowError(R.string.failed_to_load_loanrepayment),
        SyncLoanRepaymentTransactionUiState.ShowEmptyLoanRepayments("No loan repayments to sync"),
        SyncLoanRepaymentTransactionUiState.ShowLoanRepaymentTransactions(
            sampleLoanRepaymentRequests,
            samplePaymentTypeOptions
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun SyncLoanRepaymentTransactionScreenPreview(
    @PreviewParameter(SyncLoanRepaymentTransactionUiStateProvider::class) uiState: SyncLoanRepaymentTransactionUiState
) {
    SyncLoanRepaymentTransactionScreen(
        uiState = uiState,
        onBackPressed = {},
        refreshState = false,
        onRefresh = {},
        syncLoanRepaymentTransactions = {}
    )
}

// Sample data for previews
val sampleLoanRepaymentRequests = List(5) { index ->
    LoanRepaymentRequest(
        loanId = index,
        accountNumber = "LOAN-$index",
        paymentTypeId = index.toString(),
        transactionAmount = "${1000 + index * 100}",
        transactionDate = "2023-07-${15 + index}",
        errorMessage = if (index % 2 == 0) null else "Error in transaction"
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

// Individual preview for LoanRepaymentTransactionItem
@Preview(showBackground = true)
@Composable
fun LoanRepaymentTransactionItemPreview() {
    LoanRepaymentTransactionItem(
        request = sampleLoanRepaymentRequests[0],
        paymentTypeOptions = samplePaymentTypeOptions
    )
}