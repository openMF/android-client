/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanTransaction

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.account.loan.Transaction
import com.mifos.core.objects.account.loan.Type
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.feature.loan.R
import com.mifos.room.entities.accounts.loans.LoanWithAssociations

/**
 * Created by Pronay Sarker on 04/07/2024 (11:31 AM)
 */

@Composable
internal fun LoanTransactionsScreen(
    navigateBack: () -> Unit,
    viewModel: LoanTransactionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.loanTransactionsUiState.collectAsStateWithLifecycle()
    val loanId by viewModel.loanId.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        Log.d("debugPrint", "LoanId: $loanId")
        viewModel.loadLoanTransaction(loanId)
    }

    LoanTransactionsScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = { viewModel.loadLoanTransaction(loanId) },
    )
}

@Composable
internal fun LoanTransactionsScreen(
    uiState: LoanTransactionsUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    MifosScaffold(
        snackbarHostState = snackbarHostState,
        title = stringResource(id = R.string.feature_loan_loan_transactions),
        icon = MifosIcons.arrowBack,
        onBackPressed = navigateBack,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            when (uiState) {
                is LoanTransactionsUiState.ShowFetchingError -> {
                    MifosSweetError(
                        message = uiState.message,
                        onclick = onRetry,
                    )
                }

                is LoanTransactionsUiState.ShowLoanTransaction -> {
                    if (uiState.loanWithAssociations.transactions.isEmpty()) {
                        MifosEmptyUi(text = stringResource(id = R.string.feature_loan_no_transactions))
                    } else {
                        LoanTransactionsContent(uiState.loanWithAssociations.transactions)
                    }
                }

                LoanTransactionsUiState.ShowProgressBar -> {
                    MifosCircularProgress()
                }
            }
        }
    }
}

@Composable
private fun LoanTransactionsContent(
    transactions: List<Transaction>,
) {
    LazyColumn {
        items(transactions) { transaction ->
            LoanTransactionsItemRow(transaction = transaction)
        }
    }
}

@Composable
private fun LoanTransactionsItemRow(transaction: Transaction) {
    val density = LocalDensity.current
    var showDetails by rememberSaveable {
        mutableStateOf(false)
    }
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(0.dp),
            onClick = { showDetails = !showDetails },
            colors = CardDefaults.cardColors(
                containerColor = White,
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (!showDetails) MifosIcons.arrowDown else MifosIcons.arrowUp,
                        contentDescription = "",
                    )

                    Text(
                        modifier = Modifier
                            .weight(3f)
                            .padding(start = 8.dp),
                        text = DateHelper.getDateAsString(transaction.date),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        modifier = Modifier
                            .weight(3.3f)
                            .padding(start = 8.dp),
                        text = transaction.type?.value.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        modifier = Modifier
                            .weight(2.7f)
                            .padding(start = 8.dp),
                        text = transaction.amount.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showDetails,
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top,
            ) + fadeIn(
                initialAlpha = 0.3f,
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut(),
        ) {
            LoanTransactionsItemDetailsCard(transaction = transaction)
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
private fun LoanTransactionsItemDetailsCard(
    transaction: Transaction,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFe7eb9a),
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(id = R.string.feature_loan_id),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = transaction.id.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(id = R.string.feature_loan_office),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = transaction.officeName.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(id = R.string.feature_loan_break_down),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            HorizontalDivider(color = Black)

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .background(Color(0xFFdea164)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                ) {
                    Text(
                        modifier = Modifier.weight(2.5f),
                        text = stringResource(id = R.string.feature_loan_principal),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Start,
                    )

                    Text(
                        modifier = Modifier.weight(2.5f),
                        text = stringResource(id = R.string.feature_loan_loan_interest),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        modifier = Modifier.weight(2.5f),
                        text = stringResource(id = R.string.feature_loan_loan_fees),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        modifier = Modifier.weight(2.5f),
                        text = stringResource(id = R.string.feature_loan_loan_penalty),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.End,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
            ) {
                Text(
                    modifier = Modifier.weight(2.5f),
                    text = transaction.principalPortion.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                )

                Text(
                    modifier = Modifier
                        .weight(2.5f)
                        .padding(horizontal = 4.dp),
                    text = transaction.interestPortion.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Text(
                    modifier = Modifier.weight(2.5f),
                    text = transaction.feeChargesPortion.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Text(
                    modifier = Modifier
                        .weight(2.5f)
                        .padding(start = 4.dp),
                    text = transaction.penaltyChargesPortion.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

private class LoanTransactionsPreviewProvider : PreviewParameterProvider<LoanTransactionsUiState> {
    val transaction =
        Transaction(
            id = 23,
            officeName = "Main office",
            date = listOf(2024, 6, 1),
            principalPortion = 121.2,
            penaltyChargesPortion = 32323.232,
            overpaymentPortion = 23232.23,
            feeChargesPortion = 323.3,
            interestPortion = 232.3,
            type = Type(
                value = "Repayment",
            ),
        )

    override val values: Sequence<LoanTransactionsUiState>
        get() = sequenceOf(
            LoanTransactionsUiState.ShowFetchingError(""),
            LoanTransactionsUiState.ShowProgressBar,
            LoanTransactionsUiState.ShowLoanTransaction(
                LoanWithAssociations(
                    transactions = listOf(
                        transaction,
                        transaction,
                        transaction,
                        transaction,
                        transaction,
                        transaction,
                        transaction,
                        transaction,
                        transaction,
                        transaction,
                    ),
                ),
            ),
        )
}

@Composable
@Preview(showSystemUi = true)
private fun PreviewLoanTransactions(
    @PreviewParameter(LoanTransactionsPreviewProvider::class) loanTransactionsUiState: LoanTransactionsUiState,
) {
    LoanTransactionsScreen(
        uiState = loanTransactionsUiState,
        navigateBack = {},
        onRetry = {},
    )
}
