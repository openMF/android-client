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
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_export_to_pdf
import androidclient.feature.loan.generated.resources.feature_loan_from_date
import androidclient.feature.loan.generated.resources.feature_loan_generate_report
import androidclient.feature.loan.generated.resources.feature_loan_invalid_date_range
import androidclient.feature.loan.generated.resources.feature_loan_loan_transactions
import androidclient.feature.loan.generated.resources.feature_loan_no_transactions
import androidclient.feature.loan.generated.resources.feature_loan_select
import androidclient.feature.loan.generated.resources.feature_loan_table_header_amount
import androidclient.feature.loan.generated.resources.feature_loan_table_header_external_id
import androidclient.feature.loan.generated.resources.feature_loan_table_header_fees
import androidclient.feature.loan.generated.resources.feature_loan_table_header_interest
import androidclient.feature.loan.generated.resources.feature_loan_table_header_loan_balance
import androidclient.feature.loan.generated.resources.feature_loan_table_header_number
import androidclient.feature.loan.generated.resources.feature_loan_table_header_office
import androidclient.feature.loan.generated.resources.feature_loan_table_header_penalties
import androidclient.feature.loan.generated.resources.feature_loan_table_header_principal_due
import androidclient.feature.loan.generated.resources.feature_loan_table_header_transaction_date
import androidclient.feature.loan.generated.resources.feature_loan_table_header_transaction_id
import androidclient.feature.loan.generated.resources.feature_loan_table_header_transaction_type
import androidclient.feature.loan.generated.resources.feature_loan_to_date
import androidclient.feature.loan.generated.resources.feature_loan_transaction_action_undo
import androidclient.feature.loan.generated.resources.feature_loan_transaction_action_view_details
import androidclient.feature.loan.generated.resources.feature_loan_transaction_action_view_journal_entries
import androidclient.feature.loan.generated.resources.feature_loan_transaction_action_view_receipts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosCustomDialog
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedButton
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
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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

    LoanTransactionsScreenContent(
        state = state,
        onAction = remember(viewModel) { viewModel::trySendAction },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoanTransactionsScreenContent(
    state: LoanTransactionsState,
    onAction: (LoanTransactionsAction) -> Unit,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        title = stringResource(Res.string.feature_loan_loan_transactions),
        onBackPressed = { onAction(LoanTransactionsAction.NavigateBack) },
        actions = {
            if (state.viewState is LoanTransactionsState.ViewState.Success && state.viewState.transactionsTableData.transactions.isNotEmpty()) {
                IconButton(
                    onClick = { onAction(LoanTransactionsAction.ExportClicked) },
                ) {
                    Icon(
                        imageVector = MifosIcons.FileUpload,
                        contentDescription = stringResource(Res.string.feature_loan_export_to_pdf),
                    )
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (val viewState = state.viewState) {
                is LoanTransactionsState.ViewState.Error -> {
                    MifosSweetError(
                        message = viewState.message,
                        onclick = { onAction(LoanTransactionsAction.OnRetry) },
                    )
                }

                is LoanTransactionsState.ViewState.Success -> {
                    if (viewState.transactionsTableData.transactions.isEmpty()) {
                        MifosEmptyUi(text = stringResource(Res.string.feature_loan_no_transactions))
                    } else {
                        LoanTransactionsTableContent(
                            tableData = viewState.transactionsTableData,
                            onAction = onAction,
                        )
                    }

                    if (state.isBottomSheetOpen) {
                        TransactionActionsBottomSheet(
                            transactionType = state.selectedRow?.transactionType
                                ?: TransactionType.UNKNOWN,
                            manuallyReversed = state.selectedRow?.manuallyReversed ?: false,
                            onDismissRequest = { onAction(LoanTransactionsAction.DismissBottomSheet) },
                            onAction = { action ->
                                state.selectedRow?.id?.toIntOrNull()?.let { id ->
                                    onAction(
                                        LoanTransactionsAction.TransactionActionSelected(
                                            action,
                                            id,
                                        ),
                                    )
                                }
                            },
                        )
                    }
                }

                LoanTransactionsState.ViewState.Loading -> {
                    MifosProgressIndicator()
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
internal fun ExportTransactionsDialog(
    state: ExportDialogState,
    onAction: (LoanTransactionsAction) -> Unit,
) {
    val nowMillis = remember { Clock.System.now().toEpochMilliseconds() }
    val minToDate = state.fromDate

    val pastOnlySelectable = remember(nowMillis) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= nowMillis
            }
        }
    }

    val toDateSelectable = remember(nowMillis, minToDate) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= nowMillis &&
                        (minToDate == null || utcTimeMillis >= minToDate)
            }
        }
    }

    val fromDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.fromDate ?: nowMillis,
        selectableDates = pastOnlySelectable,
    )

    val toDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.toDate ?: nowMillis,
        selectableDates = toDateSelectable,
    )

    if (state.showFromDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onAction(LoanTransactionsAction.DismissFromDatePicker) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(LoanTransactionsAction.DismissFromDatePicker)
                        fromDatePickerState.selectedDateMillis?.let {
                            onAction(LoanTransactionsAction.FromDateSelected(it))
                        }
                    },
                ) { Text(stringResource(Res.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { onAction(LoanTransactionsAction.DismissFromDatePicker) },
                ) { Text(stringResource(Res.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = fromDatePickerState)
        }
    }

    if (state.showToDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onAction(LoanTransactionsAction.DismissToDatePicker) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(LoanTransactionsAction.DismissToDatePicker)
                        toDatePickerState.selectedDateMillis?.let {
                            onAction(LoanTransactionsAction.ToDateSelected(it))
                        }
                    },
                ) { Text(stringResource(Res.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { onAction(LoanTransactionsAction.DismissToDatePicker) },
                ) { Text(stringResource(Res.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = toDatePickerState)
        }
    }

    MifosCustomDialog(
        onDismiss = { onAction(LoanTransactionsAction.DismissExportDialog) },
    ) {
        Surface(
            shape = KptTheme.shapes.medium,
            color = KptTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.95f),
        ) {
            Column(modifier = Modifier.padding(KptTheme.spacing.lg)) {
                Text(
                    text = stringResource(Res.string.feature_loan_export_to_pdf),
                    style = KptTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                MifosDatePickerTextField(
                    value = state.fromDate?.let {
                        DateHelper.getDateAsStringFromLong(it)
                    }.orEmpty(),
                    label = stringResource(Res.string.feature_loan_from_date),
                    openDatePicker = {
                        onAction(LoanTransactionsAction.OpenFromDatePicker)
                    },
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

                MifosDatePickerTextField(
                    value = state.toDate?.let {
                        DateHelper.getDateAsStringFromLong(it)
                    }.orEmpty(),
                    label = stringResource(Res.string.feature_loan_to_date),
                    errorMessage = if (state.isInvalidDateRange) {
                        stringResource(Res.string.feature_loan_invalid_date_range)
                    } else {
                        null
                    },
                    openDatePicker = {
                        onAction(LoanTransactionsAction.OpenToDatePicker)
                    },
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MifosOutlinedButton(
                        onClick = { onAction(LoanTransactionsAction.DismissExportDialog) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_loan_cancel),
                            style = KptTheme.typography.labelLarge,
                            maxLines = 1,
                        )
                    }

                    Spacer(modifier = Modifier.width(KptTheme.spacing.md))

                    MifosButton(
                        onClick = {
                            onAction(LoanTransactionsAction.GenerateReportClicked)
                        },
                        enabled = state.isValidDateRange,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_loan_generate_report),
                            style = KptTheme.typography.labelLarge,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoanTransactionsTableContent(
    tableData: LoanTransactionsState.LoanTransactionsTableData,
    onAction: (LoanTransactionsAction) -> Unit,
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
        stringResource(Res.string.feature_loan_table_header_principal_due),
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
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    row: LoanTransactionsState.TransactionRowData,
    widths: List<Dp>,
    onAction: (LoanTransactionsAction) -> Unit,
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
        onClick = { onAction(LoanTransactionsAction.RowSelected(row)) },
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
                        Spacer(modifier = Modifier.width(DesignToken.padding.medium))
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
            LoanTransactionsState(viewState = LoanTransactionsState.ViewState.Error("")),
            LoanTransactionsState(viewState = LoanTransactionsState.ViewState.Loading),
            LoanTransactionsState(
                viewState = LoanTransactionsState.ViewState.Success(
                    transactionsTableData = LoanTransactionsState.LoanTransactionsTableData(
                        transactions = List(10) { index ->
                            LoanTransactionsState.TransactionRowData(
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
    LoanTransactionsScreenContent(
        state = state,
        onAction = {},
    )
}
