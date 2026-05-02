/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanTransaction

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_break_down
import androidclient.feature.loan.generated.resources.feature_loan_export_transactions
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun LoanTransactionsScreen(
    navigateBack: () -> Unit,
    viewModel: LoanTransactionsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanTransactionsEvent.NavigateBack -> navigateBack()
        }
    }

    LoanTransactionsScreen(
        state = state,
        onAction = remember(viewModel) { viewModel::trySendAction },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoanTransactionsScreen(
    state: LoanTransactionsState,
    onAction: (LoanTransactionsAction) -> Unit = {},
) {
    MifosScaffold(
        title = stringResource(Res.string.feature_loan_loan_transactions),
        onBackPressed = { onAction(LoanTransactionsAction.NavigateBack) },
        actions = {
            IconButton(
                onClick = { onAction(LoanTransactionsAction.ExportClicked) },
                enabled = state.uiState is LoanTransactionsUiState.Success,
            ) {
                Icon(
                    imageVector = MifosIcons.FileUpload,
                    contentDescription = stringResource(Res.string.feature_loan_export_transactions),
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            when (val uiState = state.uiState) {
                is LoanTransactionsUiState.Loading -> {
                    MifosProgressIndicator()
                }

                is LoanTransactionsUiState.Error -> {
                    MifosSweetError(
                        message = uiState.message,
                        onclick = { onAction(LoanTransactionsAction.Retry) },
                    )
                }

                is LoanTransactionsUiState.Success -> {
                    val tableData = uiState.tableData
                    if (tableData.transactions.isEmpty()) {
                        MifosEmptyUi(
                            text = stringResource(Res.string.feature_loan_no_transactions),
                        )
                    } else {
                        LoanTransactionsTableContent(
                            tableData = tableData,
                            onRowAction = { row ->
                                onAction(LoanTransactionsAction.RowSelected(row))
                            },
                        )
                    }

                    if (state.selectedRow != null) {
                        TransactionActionsBottomSheet(
                            transactionType = state.selectedRow.transactionType,
                            manuallyReversed = state.selectedRow.manuallyReversed,
                            onDismissRequest = {
                                onAction(LoanTransactionsAction.DismissBottomSheet)
                            },
                            onAction = { action ->
                                onAction(
                                    LoanTransactionsAction.TransactionActionClicked(
                                        action = action,
                                        id = state.selectedRow.id.toInt(),
                                    ),
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    if (state.exportDialogState.isVisible) {
        ExportTransactionsDialog(
            state = state.exportDialogState,
            onAction = onAction,
        )
    }
}

@Composable
private fun LoanTransactionsTableContent(
    tableData: LoanTransactionsTableData,
    onRowAction: (TransactionRowData) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    val smallWidth = DesignToken.sizes.tableCellWidthSmall
    val mediumWidth = DesignToken.sizes.tableCellWidthMedium
    val largeWidth = DesignToken.sizes.tableCellWidthLarge

    val columnWidths =
        listOf(
            smallWidth,
            smallWidth,
            mediumWidth,
            mediumWidth,
            largeWidth,
            largeWidth,
            mediumWidth,
            mediumWidth,
            mediumWidth,
            mediumWidth,
            mediumWidth,
            mediumWidth,
        )

    val headersRow1 = listOf(
        Res.string.feature_loan_table_header_number,
        Res.string.feature_loan_table_header_transaction_id,
        Res.string.feature_loan_table_header_office,
        Res.string.feature_loan_table_header_external_id,
        Res.string.feature_loan_table_header_transaction_date,
        Res.string.feature_loan_table_header_transaction_type,
        Res.string.feature_loan_break_down,
        Res.string.feature_loan_table_header_loan_balance,
    )

    val breakdownSpanWidth = mediumWidth * 5
    val headerWidthsRow1 = listOf(
        smallWidth,
        smallWidth,
        mediumWidth,
        mediumWidth,
        largeWidth,
        largeWidth,
        breakdownSpanWidth,
        mediumWidth,
    )

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
                                        vertical = KptTheme.spacing.sm,
                                        horizontal = KptTheme.spacing.xs,
                                    ),
                            ) {
                                Text(
                                    text = stringResource(headerId),
                                    style = KptTheme.typography.titleSmall,
                                    modifier = Modifier.fillMaxSize(),
                                    textAlign = if (index == 6) TextAlign.Center else TextAlign.Left,
                                )
                            }
                        }
                    },
                    widths = headerWidthsRow1,
                    backgroundColor = lerp(
                        KptTheme.colorScheme.surface,
                        KptTheme.colorScheme.primary,
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
                                                KptTheme.colorScheme.surface,
                                                KptTheme.colorScheme.primary,
                                                0.08f,
                                            )
                                        } else {
                                            KptTheme.colorScheme.background
                                        },
                                    )
                                    .padding(
                                        vertical = KptTheme.spacing.sm,
                                        horizontal = KptTheme.spacing.xs,
                                    ),
                            ) {
                                Text(
                                    text = headerTitle,
                                    style = KptTheme.typography.titleSmall,
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
    row: TransactionRowData,
    widths: List<Dp>,
    onRowAction: (TransactionRowData) -> Unit = {},
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

    val commonTextStyle = KptTheme.typography.bodySmall
    val commonModifier = Modifier.fillMaxSize()
    val commonTextAlign = TextAlign.Left
    val textColor =
        if (row.manuallyReversed) KptTheme.colorScheme.error else KptTheme.colorScheme.onBackground
    val textDecoration = if (row.manuallyReversed) TextDecoration.LineThrough else null

    val cells: List<@Composable () -> Unit> = textValues.mapIndexed { index, value ->
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (index in 6..10) {
                            lerp(
                                KptTheme.colorScheme.surface,
                                KptTheme.colorScheme.primary,
                                0.08f,
                            )
                        } else {
                            KptTheme.colorScheme.surface
                        },
                    )
                    .padding(
                        vertical = KptTheme.spacing.sm,
                        horizontal = KptTheme.spacing.xs,
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
                modifier = Modifier.padding(KptTheme.spacing.md),
            ) {
                actions.forEach { action ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAction(action)
                            }
                            .padding(KptTheme.spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = getIconForAction(action),
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(KptTheme.spacing.md))
                        Text(text = getTextForAction(action))
                    }
                }
            }
        },
    )
}

private class LoanTransactionsPreviewProvider : PreviewParameterProvider<LoanTransactionsState> {
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

    override val values: Sequence<LoanTransactionsState>
        get() = sequenceOf(
            LoanTransactionsState(uiState = LoanTransactionsUiState.Error("Network error")),
            LoanTransactionsState(uiState = LoanTransactionsUiState.Loading),
            LoanTransactionsState(
                uiState = LoanTransactionsUiState.Success(
                    LoanTransactionsTableData(
                        transactions = List(10) { index ->
                            TransactionRowData(
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
            ),
        )
}

@Composable
@Preview
private fun PreviewLoanTransactions(
    @PreviewParameter(LoanTransactionsPreviewProvider::class) state: LoanTransactionsState,
) {
    LoanTransactionsScreen(
        state = state,
    )
}
