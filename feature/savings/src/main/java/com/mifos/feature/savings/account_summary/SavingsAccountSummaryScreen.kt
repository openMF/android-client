package com.mifos.feature.savings.account_summary

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.objects.accounts.savings.Currency
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.objects.accounts.savings.Status
import com.mifos.core.objects.accounts.savings.Summary
import com.mifos.core.objects.accounts.savings.Transaction
import com.mifos.core.objects.accounts.savings.TransactionType
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.feature.savings.R

/**
 * Created by Pronay Sarker on 10/07/2024 (6:21 PM)
 */

@Composable
fun SavingsAccountSummaryScreen(
    navigateBack: () -> Unit,
    loadMoreSavingsAccountInfo: (accountNumber: Int) -> Unit,
    loadDocuments: (accountNumber: Int) -> Unit,
    onDepositClick: (savingsAccountWithAssociations: SavingsAccountWithAssociations, savingsAccountType: DepositType?) -> Unit,
    onWithdrawButtonClicked: (savingsAccountWithAssociations: SavingsAccountWithAssociations, savingsAccountType: DepositType?) -> Unit,
    approveSavings: (savingsAccountType: DepositType?, savingsAccountNumber: Int) -> Unit,
    activateSavings: (savingsAccountType: DepositType?, savingsAccountNumber: Int) -> Unit
) {
    val viewmodel: SavingsAccountSummaryViewModel = hiltViewModel()
    val uiState by viewmodel.savingsAccountSummaryUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewmodel.loadSavingAccount(viewmodel.savingsAccountType?.endpoint)
    }

    SavingsAccountSummaryScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = { viewmodel.loadSavingAccount(viewmodel.savingsAccountType?.endpoint) },
        loadMoreSavingsAccountInfo = { loadMoreSavingsAccountInfo.invoke(viewmodel.accountId) },
        loadDocuments = { loadDocuments.invoke(viewmodel.accountId) },
        onDepositButtonClicked = { savingsAccountWithAssociations: SavingsAccountWithAssociations ->
            onDepositClick.invoke(savingsAccountWithAssociations, viewmodel.savingsAccountType)
        },
        onWithdrawButtonClicked = { savingsAccountWithAssociations: SavingsAccountWithAssociations ->
            onWithdrawButtonClicked.invoke(
                savingsAccountWithAssociations,
                viewmodel.savingsAccountType
            )
        },
        approveSavings = {
            approveSavings.invoke(viewmodel.savingsAccountType, viewmodel.accountId)
        },
        activateSavings = {
            activateSavings.invoke(viewmodel.savingsAccountType, viewmodel.accountId)
        }
    )
}

@Composable
fun SavingsAccountSummaryScreen(
    uiState: SavingsAccountSummaryUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
    loadMoreSavingsAccountInfo: () -> Unit,
    loadDocuments: () -> Unit,
    onDepositButtonClicked: (savingsAccountWithAssociations: SavingsAccountWithAssociations) -> Unit,
    onWithdrawButtonClicked: (savingsAccountWithAssociations: SavingsAccountWithAssociations) -> Unit,
    approveSavings: () -> Unit,
    activateSavings: () -> Unit
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    var showDropdown by rememberSaveable {
        mutableStateOf(false)
    }

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        onBackPressed = navigateBack,
        title = stringResource(id = R.string.feature_savings_savingsAccountSummary),
        icon = MifosIcons.arrowBack,
        fontsizeInSp = 22,
        actions = {
            IconButton(onClick = { showDropdown = !showDropdown }) {
                Icon(imageVector = MifosIcons.moreVert, contentDescription = "")
            }

            if (showDropdown) {
                DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
                    MifosMenuDropDownItem(option = stringResource(id = R.string.feature_savings_more_savings_account_info)) {
                        showDropdown = false
                        loadMoreSavingsAccountInfo.invoke()
                    }
                    MifosMenuDropDownItem(option = stringResource(id = R.string.feature_savings_documents)) {
                        showDropdown = false
                        loadDocuments.invoke()
                    }
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
        ) {
            when (uiState) {
                is SavingsAccountSummaryUiState.ShowFetchingError -> {
                    MifosSweetError(message = stringResource(id = uiState.message)) {
                        onRetry()
                    }
                }

                SavingsAccountSummaryUiState.ShowProgressbar -> {
                    MifosCircularProgress()
                }

                is SavingsAccountSummaryUiState.ShowSavingAccount -> {
                    SavingsAccountSummaryContent(
                        savingsAccountWithAssociations = uiState.savingsAccountWithAssociations,
                        onDepositButtonClicked = onDepositButtonClicked,
                        onWithdrawButtonClicked = onWithdrawButtonClicked,
                        approveSavings = approveSavings,
                        activateSavings = activateSavings
                    )
                }
            }
        }
    }
}

@Composable
fun SavingsAccountSummaryContent(
    savingsAccountWithAssociations: SavingsAccountWithAssociations,
    onDepositButtonClicked: (savingsAccountWithAssociations: SavingsAccountWithAssociations) -> Unit,
    onWithdrawButtonClicked: (savingsAccountWithAssociations: SavingsAccountWithAssociations) -> Unit,
    approveSavings: () -> Unit,
    activateSavings: () -> Unit
) {
    val context = LocalContext.current
    val isSavingsButtonVisible by rememberSaveable {
        mutableStateOf(savingsButtonVisibilityStatus(savingsAccountWithAssociations.status))
    }
    val isWithdrawalAndDepositButtonVisible by rememberSaveable {
        mutableStateOf(depositAndWithdrawButtonVisibility(savingsAccountWithAssociations.status))
    }

    Box(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                text = savingsAccountWithAssociations.clientName
                    ?: stringResource(id = R.string.feature_savings_client_name),
                style = MaterialTheme.typography.bodyLarge,
            )

            HorizontalDivider(color = DarkGray)

            FarApartTextItem(
                title = savingsAccountWithAssociations.savingsProductName
                    ?: stringResource(id = R.string.feature_savings_product_name),
                value = savingsAccountWithAssociations.accountNo?.toString() ?: ""
            )

            HorizontalDivider(modifier = Modifier.padding(top = 6.dp), color = DarkGray)

            FarApartTextItem(
                title = stringResource(id = R.string.feature_savings_account_balance),
                value = savingsAccountWithAssociations.summary?.accountBalance?.toString()
                    ?: "0.0"
            )

            FarApartTextItem(
                title = stringResource(id = R.string.feature_savings_total_deposits),
                value = savingsAccountWithAssociations.summary?.totalDeposits?.toString()
                    ?: "0.0"
            )

            FarApartTextItem(
                title = stringResource(id = R.string.feature_savings_total_withdrawals),
                value = savingsAccountWithAssociations.summary?.totalWithdrawals?.toString()
                    ?: "0.0"
            )

            FarApartTextItem(
                title = stringResource(id = R.string.feature_savings_interest_earned),
                value = savingsAccountWithAssociations.summary?.totalInterestEarned?.toString()
                    ?: "0.0"
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = DarkGray
            )

            if (savingsAccountWithAssociations.transactions.isEmpty()) {
                MifosEmptyUi(text = stringResource(id = R.string.feature_savings_no_transactions))
            } else {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(id = R.string.feature_savings_transactions),
                    color = MaterialTheme.colorScheme.onBackground,
                )

                LazyColumn {
                    items(savingsAccountWithAssociations.transactions) { transaction ->
                        TransactionItemRow(transaction = transaction)
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(
                    color = Color.White
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isWithdrawalAndDepositButtonVisible) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
                        ),
                        onClick = { onWithdrawButtonClicked.invoke(savingsAccountWithAssociations) }) {
                        Text(text = stringResource(id = R.string.feature_savings_withdrawal))
                    }

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
                        ),
                        onClick = { onDepositButtonClicked.invoke(savingsAccountWithAssociations) }) {
                        Text(text = stringResource(id = R.string.feature_savings_make_deposit))
                    }
                }


                if (isSavingsButtonVisible) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
                        ),
                        onClick = when {
                            savingsAccountWithAssociations.status?.submittedAndPendingApproval == true -> {
                                { approveSavings.invoke() }
                            }

                            !savingsAccountWithAssociations.status?.active!! -> {
                                { activateSavings.invoke() }
                            }

                            else -> {
                                { /** not reachable , view gone here **/ }
                            }
                        }

                    ) {
                        Text(
                            text = getSavingsButtonText(
                                context = context,
                                status = savingsAccountWithAssociations.status
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItemRow(
    transaction: Transaction
) {
    var showTransactionDetails by rememberSaveable {
        mutableStateOf(false)
    }

    if (showTransactionDetails) {
        SummaryDialogBox(
            onDismissCall = { showTransactionDetails = false },
            transaction = transaction
        )
    }
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { showTransactionDetails = !showTransactionDetails },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateHelper.getDateAsString(transaction.date as List<Int>),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(4f)
                )

                Text(
                    text = transaction.transactionType?.value ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(3f),
                )

                Text(
                    text = transaction.currency?.displaySymbol + " " + transaction.amount?.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(3f),
                    textAlign = TextAlign.End,
                    color = when {
                        transaction.transactionType?.deposit == true -> {
                            Color.Green
                        }

                        transaction.transactionType?.withdrawal == true -> {
                            Color.Red
                        }

                        else -> {
                            Color.Black
                        }
                    }
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun SummaryDialogBox(
    onDismissCall: () -> Unit,
    transaction: Transaction
) {
    AlertDialog(
        onDismissRequest = { onDismissCall.invoke() },
        confirmButton = { },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                DialogBoxRowItem(
                    title = stringResource(id = R.string.feature_savings_transaction_id),
                    value = transaction.id?.toString() ?: ""
                )
                Spacer(modifier = Modifier.height(4.dp))

                DialogBoxRowItem(
                    title = stringResource(id = R.string.feature_savings_date),
                    value = DateHelper.getDateAsString(transaction.date as List<Int>)
                )
                Spacer(modifier = Modifier.height(4.dp))

                DialogBoxRowItem(
                    title = stringResource(id = R.string.feature_savings_transaction_type),
                    value = transaction.transactionType?.value ?: ""
                )
                Spacer(modifier = Modifier.height(4.dp))

                DialogBoxRowItem(
                    title = stringResource(id = R.string.feature_savings_running_balance),
                    value = transaction.runningBalance.toString()
                )
                Spacer(modifier = Modifier.height(4.dp))

                DialogBoxRowItem(
                    title = stringResource(id = R.string.feature_savings_saving_account_id),
                    value = transaction.accountId.toString()
                )
                Spacer(modifier = Modifier.height(4.dp))

                DialogBoxRowItem(
                    title = stringResource(id = R.string.feature_savings_account_number),
                    value = transaction.accountNo ?: ""
                )
                Spacer(modifier = Modifier.height(4.dp))

                DialogBoxRowItem(
                    title = stringResource(id = R.string.feature_savings_currency),
                    value = transaction.currency?.name ?: ""
                )
            }
        })
}

@Composable
private fun DialogBoxRowItem(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = BluePrimary.copy(alpha = .5f),
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(
            modifier = Modifier.weight(5f),
            style = MaterialTheme.typography.bodyMedium,
            text = title,
            color = DarkGray,
        )

        Text(
            modifier = Modifier
                .weight(5f)
                .padding(end = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            text = value,
            color = Black,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun FarApartTextItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = title,
            color = Black,
        )

        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = value,
            color = DarkGray,
        )
    }
}


fun getSavingsButtonText(context: Context, status: Status?): String {
    return when {
        status?.submittedAndPendingApproval == true -> {
            context.resources.getString(R.string.feature_savings_approve_savings)
        }

        !status?.active!! -> {
            context.resources.getString(R.string.feature_savings_activate_savings)
        }

        status?.closed == true -> {
            context.resources.getString(R.string.feature_savings_savings_account_closed)
        }

        else -> {
            "Button not visible"
        }
    }
}

fun savingsButtonVisibilityStatus(status: Status?): Boolean {
    return when {
        status?.submittedAndPendingApproval == true -> {
            true
        }

        !status?.active!! -> {
            true
        }

        status?.closed == true -> {
            true
        }

        else -> {
            false
        }
    }
}

fun depositAndWithdrawButtonVisibility(status: Status?): Boolean {
    return when {
        status?.submittedAndPendingApproval == true -> {
            false
        }

        !status?.active!! -> {
            false
        }

        status?.closed == true -> {
            false
        }

        else -> {
            true
        }
    }
}

class SavingsAccountSummaryScreenPreviewProvider :
    PreviewParameterProvider<SavingsAccountSummaryUiState> {

    val summary = Summary(
        savingsId = 2232,
        currency = null,
        totalDeposits = 4332.333,
        accountBalance = 23232.333,
        totalWithdrawals = 3343.434,
        totalInterestEarned = 234.34
    )

    val transaction = Transaction(
        transactionType = TransactionType(
            value = "Transfer",
        ),
        date = listOf(2, 3, 2022),
        currency = Currency(
            code = null
        ),
    )
    override val values: Sequence<SavingsAccountSummaryUiState>
        get() = sequenceOf(
            SavingsAccountSummaryUiState.ShowProgressbar,
            SavingsAccountSummaryUiState.ShowFetchingError(R.string.feature_savings_failed_to_fetch_savingsaccount),
            SavingsAccountSummaryUiState.ShowSavingAccount(
                SavingsAccountWithAssociations(
                    clientId = 343434343,
                    accountNo = 3830948,
                    clientName = "Pronay",
                    transactions = listOf(
                        transaction,
                        transaction,
                        transaction,
                        transaction
                    ),
                    status = Status(),
                    summary = summary
                )
            ),
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewSavingsAccountSummaryScreen(
    @PreviewParameter(SavingsAccountSummaryScreenPreviewProvider::class) savingsAccountSummaryUiState: SavingsAccountSummaryUiState
) {
    SavingsAccountSummaryScreen(
        uiState = savingsAccountSummaryUiState,
        navigateBack = { },
        onRetry = { },
        loadMoreSavingsAccountInfo = { },
        loadDocuments = { },
        onDepositButtonClicked = { _ -> },
        onWithdrawButtonClicked = { _ -> },
        approveSavings = { }) {
    }
}
