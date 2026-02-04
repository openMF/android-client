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

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_break_down
import androidclient.feature.loan.generated.resources.feature_loan_loan_transactions
import androidclient.feature.loan.generated.resources.feature_loan_no_transactions
import androidclient.feature.loan.generated.resources.feature_loan_table_header_amount
import androidclient.feature.loan.generated.resources.feature_loan_table_header_external_id
import androidclient.feature.loan.generated.resources.feature_loan_table_header_fees
import androidclient.feature.loan.generated.resources.feature_loan_table_header_interest
import androidclient.feature.loan.generated.resources.feature_loan_table_header_loan_balance
import androidclient.feature.loan.generated.resources.feature_loan_table_header_number
import androidclient.feature.loan.generated.resources.feature_loan_table_header_office
import androidclient.feature.loan.generated.resources.feature_loan_table_header_penalties
import androidclient.feature.loan.generated.resources.feature_loan_table_header_principal
import androidclient.feature.loan.generated.resources.feature_loan_table_header_transaction_date
import androidclient.feature.loan.generated.resources.feature_loan_table_header_transaction_id
import androidclient.feature.loan.generated.resources.feature_loan_table_header_transaction_type
import androidclient.feature.loan.generated.resources.feature_loan_transaction_action_undo
import androidclient.feature.loan.generated.resources.feature_loan_transaction_action_view_details
import androidclient.feature.loan.generated.resources.feature_loan_transaction_action_view_journal_entries
import androidclient.feature.loan.generated.resources.feature_loan_transaction_action_view_receipts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTableRow
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.model.objects.account.loan.Transaction
import com.mifos.core.model.objects.account.loan.Type
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.components.MifosProgressIndicator
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LoanTransactionsScreen(
    navigateBack: () -> Unit,
    viewModel: LoanTransactionsViewModel = koinViewModel(),
) {
    val uiState by viewModel.loanTransactionsUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadLoanTransaction()
    }

    LoanTransactionsScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = {
            viewModel.viewModelScope.launch {
                viewModel.loadLoanTransaction()
            }
        },
        onDismissBottomSheet = { viewModel.dismissBottomSheet() },
        onTransactionActionClick = { action, id ->
            viewModel.onActionSelected(
                action,
                id,
            )
        },
        onRowAction = { row -> viewModel.onRowAction(row) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoanTransactionsScreen(
    uiState: LoanTransactionsUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
    onDismissBottomSheet: () -> Unit,
    onTransactionActionClick: (TransactionAction, Int) -> Unit,
    onRowAction: (LoanTransactionsUiState.LoanTransactionsTableData.TransactionRowData) -> Unit = {},
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        title = stringResource(Res.string.feature_loan_loan_transactions),
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
                    if (uiState.transactionsTableData == null || uiState.transactionsTableData.transactions.isEmpty()) {
                        MifosEmptyUi(text = stringResource(Res.string.feature_loan_no_transactions))
                    } else {
                        LoanTransactionsTableContent(
                            tableData = uiState.transactionsTableData,
                            onRowAction = onRowAction,
                        )
                    }

                    if (uiState.isBottomSheetOpen) {
                        TransactionActionsBottomSheet(
                            transactionType = uiState.selectedRow?.transactionType
                                ?: TransactionType.UNKNOWN,
                            manuallyReversed = uiState.selectedRow?.manuallyReversed ?: false,
                            onDismissRequest = onDismissBottomSheet,
                            onAction = { action ->
                                uiState.selectedRow?.id?.let { id ->
                                    onTransactionActionClick(action, id.toInt())
                                }
                            },
                        )
                    }
                }

                LoanTransactionsUiState.ShowProgressBar -> {
                    MifosProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun LoanTransactionsTableContent(
    tableData: LoanTransactionsUiState.LoanTransactionsTableData,
    onRowAction: (LoanTransactionsUiState.LoanTransactionsTableData.TransactionRowData) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    val smallWidth = DesignToken.sizes.tableCellWidthSmall
    val mediumWidth = DesignToken.sizes.tableCellWidthMedium
    val largeWidth = DesignToken.sizes.tableCellWidthLarge

    val columnWidths =
        listOf(
            // #
            smallWidth,
            // id
            smallWidth,
            // office
            mediumWidth,
            // externalId
            mediumWidth,
            // transaction date
            largeWidth,
            // transaction type
            largeWidth,
            // amount
            mediumWidth,
            // principal
            mediumWidth,
            // interest
            mediumWidth,
            // fees
            mediumWidth,
            // penalties
            mediumWidth,
            // loan balance
            mediumWidth,
        )

    val headersRow1 = listOf(
        Res.string.feature_loan_table_header_number,
        Res.string.feature_loan_table_header_transaction_id,
        Res.string.feature_loan_table_header_office,
        Res.string.feature_loan_table_header_external_id,
        Res.string.feature_loan_table_header_transaction_date,
        Res.string.feature_loan_table_header_transaction_type,
        // Breakdown (will span Amount, Principal, Interest, Fees, Penalties)
        Res.string.feature_loan_break_down,
        Res.string.feature_loan_table_header_loan_balance,
    )

    val breakdownSpanWidth = mediumWidth * 5
    val headerWidthsRow1 = listOf(
        // #
        smallWidth,
        // id
        smallWidth,
        // office
        mediumWidth,
        // externalId
        mediumWidth,
        // transaction date
        largeWidth,
        // transaction type
        largeWidth,
        // breakdown (span)
        breakdownSpanWidth,
        // loan balance
        mediumWidth,
    )

    // Second header row: show Amount, Principal, Interest, Fees, Penalties under the Breakdown area
    val headersRow2 = listOf(
        "",
        "",
        "",
        "",
        "",
        "",
        stringResource(Res.string.feature_loan_table_header_amount),
        stringResource(Res.string.feature_loan_table_header_principal),
        stringResource(Res.string.feature_loan_table_header_interest),
        stringResource(Res.string.feature_loan_table_header_fees),
        stringResource(Res.string.feature_loan_table_header_penalties),
        "",
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(vertical = DesignToken.padding.medium),
    ) {
        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
            ) {
                MifosTableRow(
                    cells = headersRow1.mapIndexed { index, headerId ->
                        {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        vertical = DesignToken.padding.small,
                                        horizontal = DesignToken.padding.extraSmall,
                                    ),
                            ) {
                                Text(
                                    text = stringResource(headerId),
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.fillMaxSize(),
                                    textAlign = if (index == 6) TextAlign.Center else TextAlign.Left,
                                )
                            }
                        }
                    },
                    widths = headerWidthsRow1,
                    backgroundColor = lerp(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.primary,
                        0.3f,
                    ),
                    edgeOffset = DesignToken.padding.medium,
                    cornerShape = DesignToken.shapes.topMedium,
                )

                MifosTableRow(
                    cells = headersRow2.map { headerTitle ->
                        {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (headerTitle.isNotBlank()) {
                                            lerp(
                                                MaterialTheme.colorScheme.surface,
                                                MaterialTheme.colorScheme.primary,
                                                0.08f,
                                            )
                                        } else {
                                            MaterialTheme.colorScheme.background
                                        },
                                    )
                                    .padding(
                                        vertical = DesignToken.padding.small,
                                        horizontal = DesignToken.padding.extraSmall,
                                    ),
                            ) {
                                Text(
                                    text = headerTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                        }
                    },
                    widths = columnWidths,
                    backgroundColor = Color.Transparent,
                    edgeOffset = DesignToken.padding.medium,
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
            ) {
                tableData.transactions.forEach { row ->
                    TransactionRow(
                        row = row,
                        widths = columnWidths,
                        onRowAction = onRowAction,
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    row: LoanTransactionsUiState.LoanTransactionsTableData.TransactionRowData,
    widths: List<Dp>,
    onRowAction: (LoanTransactionsUiState.LoanTransactionsTableData.TransactionRowData) -> Unit = {},
) {
    val textValues = listOf(
        row.number,
        row.id,
        row.office,
        row.externalId,
        row.transactionDate,
        row.transactionType.value,
        row.amount,
        row.principal,
        row.interest,
        row.fees,
        row.penalties,
        row.loanBalance,
    )

    val commonTextStyle = MaterialTheme.typography.bodySmall
    val commonModifier = Modifier.fillMaxSize()
    val commonTextAlign = TextAlign.Left
    val textColor =
        if (row.manuallyReversed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
    val textDecoration = if (row.manuallyReversed) TextDecoration.LineThrough else null

    val cells: List<@Composable () -> Unit> = textValues.mapIndexed { index, value ->
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (index in 6..10) {
                            lerp(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.primary,
                                0.08f,
                            )
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                    )
                    .padding(
                        vertical = DesignToken.padding.small,
                        horizontal = DesignToken.padding.extraSmall,
                    ),
            ) {
                Text(
                    text = value,
                    style = commonTextStyle,
                    modifier = commonModifier,
                    textAlign = commonTextAlign,
                    color = textColor,
                    textDecoration = textDecoration,
                )
            }
        }
    }

    MifosTableRow(
        cells = cells,
        widths = widths,
        backgroundColor = Color.Transparent,
        onClick = { onRowAction(row) },
        edgeOffset = DesignToken.padding.medium,
    )
}

@Composable
private fun TransactionActionsBottomSheet(
    transactionType: TransactionType,
    manuallyReversed: Boolean,
    onDismissRequest: () -> Unit,
    onAction: (TransactionAction) -> Unit,
) {
    val actions = if (manuallyReversed) {
        listOf(TransactionAction.VIEW_JOURNAL_ENTRIES)
    } else {
        when (transactionType) {
            TransactionType.ACCRUAL -> listOf(
                TransactionAction.UNDO_TRANSACTION,
                TransactionAction.VIEW_RECEIPTS,
                TransactionAction.VIEW_JOURNAL_ENTRIES,
            )

            TransactionType.DISBURSEMENT -> listOf(
                TransactionAction.VIEW_TRANSACTION,
                TransactionAction.VIEW_JOURNAL_ENTRIES,
            )

            TransactionType.REPAYMENT -> listOf(
                TransactionAction.UNDO_TRANSACTION,
                TransactionAction.VIEW_RECEIPTS,
                TransactionAction.VIEW_JOURNAL_ENTRIES,
                TransactionAction.VIEW_TRANSACTION,
            )

            TransactionType.UNKNOWN -> emptyList()
        }
    }

    fun getIconForAction(action: TransactionAction) = when (action) {
        TransactionAction.VIEW_TRANSACTION -> MifosIcons.Visibility
        TransactionAction.UNDO_TRANSACTION -> MifosIcons.Undo
        TransactionAction.VIEW_RECEIPTS -> MifosIcons.Assignment
        TransactionAction.VIEW_JOURNAL_ENTRIES -> MifosIcons.Dashboard
    }

    @Composable
    fun getTextForAction(action: TransactionAction) = when (action) {
        TransactionAction.VIEW_TRANSACTION -> stringResource(Res.string.feature_loan_transaction_action_view_details)
        TransactionAction.UNDO_TRANSACTION -> stringResource(Res.string.feature_loan_transaction_action_undo)
        TransactionAction.VIEW_RECEIPTS -> stringResource(Res.string.feature_loan_transaction_action_view_receipts)
        TransactionAction.VIEW_JOURNAL_ENTRIES -> stringResource(Res.string.feature_loan_transaction_action_view_journal_entries)
    }

    MifosBottomSheet(
        onDismiss = onDismissRequest,
        content = {
            Column(
                modifier = Modifier.padding(DesignToken.padding.large),
            ) {
                actions.forEach { action ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAction(action)
                            }
                            .padding(DesignToken.padding.small),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = getIconForAction(action),
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(DesignToken.padding.medium))
                        Text(text = getTextForAction(action))
                    }
                }
            }
        },
    )
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
                transactionsTableData = LoanTransactionsUiState.LoanTransactionsTableData(
                    transactions = List(10) { index ->
                        LoanTransactionsUiState.LoanTransactionsTableData.TransactionRowData(
                            number = (index + 1).toString(),
                            id = transaction.id?.toString() ?: "-",
                            office = transaction.officeName ?: "-",
                            externalId = "-",
                            transactionDate = DateHelper.getDateAsString(transaction.date),
                            transactionType = TransactionType.DISBURSEMENT,
                            amount = transaction.amount?.toString() ?: "-",
                            principal = transaction.principalPortion?.toString() ?: "-",
                            interest = transaction.interestPortion?.toString() ?: "-",
                            fees = transaction.feeChargesPortion?.toString() ?: "-",
                            penalties = transaction.penaltyChargesPortion?.toString() ?: "-",
                            loanBalance = "-",
                        )
                    },
                ),
            ),
        )
}

@Composable
@Preview
private fun PreviewLoanTransactions(
    @PreviewParameter(LoanTransactionsPreviewProvider::class) loanTransactionsUiState: LoanTransactionsUiState,
) {
    LoanTransactionsScreen(
        uiState = loanTransactionsUiState,
        navigateBack = {},
        onRetry = {},
        onDismissBottomSheet = {},
        onTransactionActionClick = { _, _ -> },
    )
}
