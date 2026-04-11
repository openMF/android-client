/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepaymentSchedule

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_balance_of_loan
import androidclient.feature.loan.generated.resources.feature_loan_complete_count
import androidclient.feature.loan.generated.resources.feature_loan_date
import androidclient.feature.loan.generated.resources.feature_loan_days
import androidclient.feature.loan.generated.resources.feature_loan_due_short
import androidclient.feature.loan.generated.resources.feature_loan_fees_short
import androidclient.feature.loan.generated.resources.feature_loan_in_advance
import androidclient.feature.loan.generated.resources.feature_loan_installment_totals
import androidclient.feature.loan.generated.resources.feature_loan_interest_short
import androidclient.feature.loan.generated.resources.feature_loan_late
import androidclient.feature.loan.generated.resources.feature_loan_loan_amount_and_balance
import androidclient.feature.loan.generated.resources.feature_loan_loan_repayment_schedule
import androidclient.feature.loan.generated.resources.feature_loan_number
import androidclient.feature.loan.generated.resources.feature_loan_outstanding
import androidclient.feature.loan.generated.resources.feature_loan_overdue_count
import androidclient.feature.loan.generated.resources.feature_loan_paid_date
import androidclient.feature.loan.generated.resources.feature_loan_paid_short
import androidclient.feature.loan.generated.resources.feature_loan_penalties_short
import androidclient.feature.loan.generated.resources.feature_loan_pending_count
import androidclient.feature.loan.generated.resources.feature_loan_principal_due
import androidclient.feature.loan.generated.resources.feature_loan_total
import androidclient.feature.loan.generated.resources.feature_loan_total_cost_of_loan
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTableRow
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.model.objects.account.loan.RepaymentScheduleRowData
import com.mifos.core.model.objects.account.loan.RepaymentScheduleTableData
import com.mifos.core.model.objects.account.loan.RepaymentScheduleTotalsData
import com.mifos.core.ui.components.MifosProgressIndicator
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun LoanRepaymentScheduleScreen(
    viewModel: LoanRepaymentScheduleViewModel = koinViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.loanRepaymentScheduleUiState.collectAsStateWithLifecycle()

    LoanRepaymentScheduleScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = viewModel::loadLoanRepaymentSchedule,
    )
}

@Composable
internal fun LoanRepaymentScheduleScreen(
    uiState: LoanRepaymentScheduleUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        title = stringResource(Res.string.feature_loan_loan_repayment_schedule),
        snackbarHostState = snackbarHostState,
        onBackPressed = navigateBack,
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (uiState) {
                is LoanRepaymentScheduleUiState.ShowProgressbar -> MifosProgressIndicator()
                is LoanRepaymentScheduleUiState.ShowFetchingError -> MifosSweetError(
                    message = uiState.message,
                    onclick = onRetry,
                )
                is LoanRepaymentScheduleUiState.ShowLoanRepaymentSchedule -> LoanRepaymentScheduleContent(
                    tableData = uiState.tableData,
                )
            }
        }
    }
}

@Composable
private fun LoanRepaymentScheduleContent(
    tableData: RepaymentScheduleTableData,
) {
    val scrollState = rememberScrollState()

    val smallWidth = DesignToken.sizes.tableCellWidthSmall
    val mediumWidth = DesignToken.sizes.tableCellWidthMedium
    val largeWidth = DesignToken.sizes.tableCellWidthLarge
    val divider = DesignToken.strokes.dpPoint5
    val columnWidths = listOf(
        smallWidth, smallWidth,
        largeWidth, largeWidth,
        largeWidth,
        mediumWidth, mediumWidth, mediumWidth, mediumWidth, mediumWidth,
        mediumWidth, mediumWidth, mediumWidth, mediumWidth,
    )

    val groupHeaders = listOf(
        "" to smallWidth,
        "" to smallWidth,
        "" to largeWidth,
        "" to largeWidth,
        stringResource(Res.string.feature_loan_loan_amount_and_balance) to (largeWidth + mediumWidth + divider),
        stringResource(Res.string.feature_loan_total_cost_of_loan) to (mediumWidth * 3 + divider * 2),
        stringResource(Res.string.feature_loan_installment_totals) to (mediumWidth * 5 + divider * 4),
    )

    val columnHeaders = listOf(
        stringResource(Res.string.feature_loan_number),
        stringResource(Res.string.feature_loan_days),
        stringResource(Res.string.feature_loan_date),
        stringResource(Res.string.feature_loan_paid_date),
        stringResource(Res.string.feature_loan_balance_of_loan),
        stringResource(Res.string.feature_loan_principal_due),
        stringResource(Res.string.feature_loan_interest_short),
        stringResource(Res.string.feature_loan_fees_short),
        stringResource(Res.string.feature_loan_penalties_short),
        stringResource(Res.string.feature_loan_due_short),
        stringResource(Res.string.feature_loan_paid_short),
        stringResource(Res.string.feature_loan_in_advance),
        stringResource(Res.string.feature_loan_late),
        stringResource(Res.string.feature_loan_outstanding),
    )

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = DesignToken.padding.medium),
        ) {
            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                ) {
                    MifosTableRow(
                        cells = groupHeaders.mapIndexed { index, (text, _) ->
                            {
                                TableHeaderCell(
                                    text = text,
                                    backgroundColor = if (text.isNotBlank()) {
                                        lerp(KptTheme.colorScheme.surface, KptTheme.colorScheme.primary, 0.3f)
                                    } else {
                                        KptTheme.colorScheme.background
                                    },
                                    textAlign = if (index in 4..6) TextAlign.Center else TextAlign.Left,
                                    style = KptTheme.typography.titleSmall,
                                )
                            }
                        },
                        widths = groupHeaders.map { it.second },
                        backgroundColor = KptTheme.colorScheme.background,
                        edgeOffset = DesignToken.padding.medium,
                        cornerShape = DesignToken.shapes.topMedium,
                    )

                    MifosTableRow(
                        cells = columnHeaders.map { text ->
                            {
                                TableHeaderCell(
                                    text = text,
                                    backgroundColor = Color.Transparent,
                                    textAlign = TextAlign.Left,
                                    style = KptTheme.typography.titleSmall,
                                )
                            }
                        },
                        widths = columnWidths,
                        backgroundColor = lerp(KptTheme.colorScheme.surface, KptTheme.colorScheme.primary, 0.08f),
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
                    tableData.disbursementRow?.let { disbursement ->
                        ScheduleTableRow(
                            values = disbursement.toDisplayValues(),
                            widths = columnWidths,
                            backgroundColor = lerp(KptTheme.colorScheme.surface, KptTheme.colorScheme.primary, 0.05f),
                            fontWeight = FontWeight.SemiBold,
                            textStyle = KptTheme.typography.bodySmall,
                        )
                    }

                    tableData.rows.forEach { row ->
                        ScheduleTableRow(
                            values = row.toDisplayValues(),
                            widths = columnWidths,
                            backgroundColor = KptTheme.colorScheme.surface,
                            fontWeight = FontWeight.Normal,
                            textStyle = KptTheme.typography.bodySmall,
                        )
                    }

                    ScheduleTableRow(
                        values = tableData.totals.toDisplayValues(
                            totalLabel = stringResource(Res.string.feature_loan_total),
                        ),
                        widths = columnWidths,
                        backgroundColor = lerp(KptTheme.colorScheme.surface, KptTheme.colorScheme.primary, 0.15f),
                        fontWeight = FontWeight.Bold,
                        textStyle = KptTheme.typography.bodyMedium,
                    )
                }
            }
        }

        HorizontalDivider(color = KptTheme.colorScheme.outlineVariant)
        BottomBarLoanRepaymentSchedule(
            completeCount = tableData.completeCount.toString(),
            overdueCount = tableData.overdueCount.toString(),
            pendingCount = tableData.pendingCount.toString(),
            modifier = Modifier.background(color = KptTheme.colorScheme.surfaceVariant),
        )
    }
}

private fun RepaymentScheduleRowData.toDisplayValues() = listOf(
    number, days, date, paidDate, balance,
    principal, interest, fees, penalties,
    due, paid, inAdvance, late, outstanding,
)

private fun RepaymentScheduleTotalsData.toDisplayValues(totalLabel: String) = listOf(
    "", "", totalLabel, "", "",
    principal, interest, fees, penalties,
    due, paid, inAdvance, late, outstanding,
)

@Composable
private fun TableHeaderCell(
    text: String,
    backgroundColor: Color,
    textAlign: TextAlign,
    style: TextStyle,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = KptTheme.spacing.sm, horizontal = KptTheme.spacing.xs),
    ) {
        Text(
            text = text,
            style = style,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = textAlign,
        )
    }
}

@Composable
private fun ScheduleTableRow(
    values: List<String>,
    widths: List<Dp>,
    backgroundColor: Color,
    fontWeight: FontWeight,
    textStyle: TextStyle,
) {
    MifosTableRow(
        cells = values.map { value ->
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(vertical = KptTheme.spacing.sm, horizontal = KptTheme.spacing.xs),
                ) {
                    Text(
                        text = value,
                        style = textStyle,
                        fontWeight = fontWeight,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Left,
                        color = KptTheme.colorScheme.onBackground,
                    )
                }
            }
        },
        widths = widths,
        backgroundColor = backgroundColor,
        edgeOffset = DesignToken.padding.medium,
    )
}

@Composable
private fun BottomBarLoanRepaymentSchedule(
    completeCount: String,
    overdueCount: String,
    pendingCount: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = DesignToken.padding.medium, vertical = DesignToken.padding.small),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(Res.string.feature_loan_complete_count, completeCount),
            style = KptTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = KptTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(Res.string.feature_loan_pending_count, pendingCount),
            style = KptTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = KptTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(Res.string.feature_loan_overdue_count, overdueCount),
            style = KptTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = KptTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
        )
    }
}

private class LoanRepaymentSchedulePreviewProvider :
    PreviewParameterProvider<LoanRepaymentScheduleUiState> {

    override val values: Sequence<LoanRepaymentScheduleUiState>
        get() = sequenceOf(
            LoanRepaymentScheduleUiState.ShowFetchingError("Error fetching loan repayment schedule"),
            LoanRepaymentScheduleUiState.ShowProgressbar,
            LoanRepaymentScheduleUiState.ShowLoanRepaymentSchedule(
                tableData = RepaymentScheduleTableData(
                    disbursementRow = null,
                    rows = listOf(
                        RepaymentScheduleRowData(
                            number = "1", days = "30", date = "1 Jun 2024", paidDate = "1 Jun 2024",
                            balance = "9,000.00", principal = "1,000.00", interest = "50.00",
                            fees = "10.00", penalties = "0.00", due = "1,060.00",
                            paid = "1,060.00", inAdvance = "0.00", late = "0.00", outstanding = "0.00",
                        ),
                        RepaymentScheduleRowData(
                            number = "2", days = "30", date = "1 Jul 2024", paidDate = "",
                            balance = "8,000.00", principal = "1,000.00", interest = "45.00",
                            fees = "10.00", penalties = "5.00", due = "1,060.00",
                            paid = "500.00", inAdvance = "0.00", late = "0.00", outstanding = "560.00",
                        ),
                    ),
                    totals = RepaymentScheduleTotalsData(
                        principal = "2,000.00", interest = "95.00", fees = "20.00",
                        penalties = "5.00", due = "2,120.00", paid = "1,560.00",
                        inAdvance = "0.00", late = "0.00", outstanding = "560.00",
                    ),
                    completeCount = 1,
                    overdueCount = 1,
                    pendingCount = 0,
                ),
            ),
        )
}

@Composable
@Preview
private fun PreviewLoanRepaymentSchedule(
    @PreviewParameter(LoanRepaymentSchedulePreviewProvider::class) uiState: LoanRepaymentScheduleUiState,
) {
    LoanRepaymentScheduleScreen(
        uiState = uiState,
        navigateBack = {},
        onRetry = {},
    )
}
