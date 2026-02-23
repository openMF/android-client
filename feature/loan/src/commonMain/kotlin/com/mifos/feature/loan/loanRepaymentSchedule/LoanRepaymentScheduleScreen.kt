/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepaymentSchedule

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.due
import androidclient.feature.loan.generated.resources.feature_loan_balance
import androidclient.feature.loan.generated.resources.feature_loan_complete
import androidclient.feature.loan.generated.resources.feature_loan_date
import androidclient.feature.loan.generated.resources.feature_loan_days
import androidclient.feature.loan.generated.resources.feature_loan_in_advance
import androidclient.feature.loan.generated.resources.feature_loan_late
import androidclient.feature.loan.generated.resources.feature_loan_loan_fees
import androidclient.feature.loan.generated.resources.feature_loan_loan_interest
import androidclient.feature.loan.generated.resources.feature_loan_loan_penalty
import androidclient.feature.loan.generated.resources.feature_loan_loan_principal
import androidclient.feature.loan.generated.resources.feature_loan_loan_repayment_schedule
import androidclient.feature.loan.generated.resources.feature_loan_outstanding
import androidclient.feature.loan.generated.resources.feature_loan_overdue
import androidclient.feature.loan.generated.resources.feature_loan_paid_date
import androidclient.feature.loan.generated.resources.feature_loan_pending
import androidclient.feature.loan.generated.resources.paid
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTableRow
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.model.objects.account.loan.Period
import com.mifos.core.model.objects.account.loan.RepaymentSchedule
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LoanRepaymentScheduleScreen(
    viewModel: LoanRepaymentScheduleViewModel = koinViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.loanRepaymentScheduleUiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.loanId) {
        viewModel.loadLoanRepaySchedule()
    }

    LoanRepaymentScheduleScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = viewModel::retryLoadSchedule,
    )
}

@Composable
internal fun LoanRepaymentScheduleScreen(
    uiState: LoanRepaymentScheduleUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_loan_loan_repayment_schedule),
        snackbarHostState = snackbarHostState,
        onBackPressed = navigateBack,
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (uiState) {
                is LoanRepaymentScheduleUiState.ShowFetchingError -> {
                    MifosSweetError(
                        message = uiState.message,
                        onclick = onRetry,
                    )
                }

                is LoanRepaymentScheduleUiState.ShowLoanRepaySchedule -> {
                    LoanRepaymentScheduleContent(
                        loanWithAssociations = uiState.loanWithAssociations,
                    )
                }

                LoanRepaymentScheduleUiState.ShowProgressbar -> {
                    MifosProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun LoanRepaymentScheduleContent(
    loanWithAssociations: LoanWithAssociationsEntity,
) {
    val periods = loanWithAssociations.repaymentSchedule.getListOfActualPeriods()
    val currencyCode = loanWithAssociations.currency.code
    val decimalPlaces = loanWithAssociations.currency.decimalPlaces
    val scrollState = rememberScrollState()

    val columnWidths = listOf(
        DesignToken.sizes.tableCellWidthSmall,
        DesignToken.sizes.tableCellWidthLarge,
        DesignToken.sizes.tableCellWidthLarge,
        DesignToken.sizes.tableCellWidthMedium,
        DesignToken.sizes.tableCellWidthMedium,
        DesignToken.sizes.tableCellWidthMedium,
        DesignToken.sizes.tableCellWidthMedium,
        DesignToken.sizes.tableCellWidthMedium,
        DesignToken.sizes.tableCellWidthMedium,
        DesignToken.sizes.tableCellWidthMedium,
        DesignToken.sizes.tableCellWidthMedium,
        DesignToken.sizes.tableCellWidthMedium,
        DesignToken.sizes.tableCellWidthMedium,
    )

    fun formatCurrency(amount: Double?): String {
        if (amount == null) return ""
        if (currencyCode.isNullOrBlank()) {
            // Fallback: format with decimal places without currency symbol
            val places = decimalPlaces ?: 2
            val multiplier = when (places) {
                0 -> 1.0
                1 -> 10.0
                2 -> 100.0
                3 -> 1000.0
                else -> 10000.0
            }
            val rounded = kotlin.math.round(amount * multiplier) / multiplier
            return rounded.toString()
        }

        return CurrencyFormatter.format(
            balance = amount,
            currencyCode = currencyCode,
            maximumFractionDigits = decimalPlaces,
        )
    }

    fun formatDate(date: List<Int>?): String {
        return date?.let { DateHelper.getDateAsString(it) } ?: ""
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            stickyHeader {
                // Follow the same pattern as LoanTransactionsScreen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                ) {
                    MifosTableRow(
                        cells = listOf(
                            { HeaderCell(text = stringResource(Res.string.feature_loan_days)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_date)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_paid_date)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_balance)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_loan_principal)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_loan_interest)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_loan_fees)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_loan_penalty)) },
                            { HeaderCell(text = stringResource(Res.string.due)) },
                            { HeaderCell(text = stringResource(Res.string.paid)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_in_advance)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_late)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_outstanding)) },
                        ),
                        widths = columnWidths,
                        backgroundColor = lerp(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.primary,
                            0.08f,
                        ),
                        edgeOffset = DesignToken.padding.medium,
                        cornerShape = DesignToken.shapes.topMedium,
                    )
                }
            }

            itemsIndexed(
                items = periods,
                key = { index, period ->
                    // Using index + dueDate for unique keys, handling potential null dates
                    "$index-${period.dueDate?.joinToString("-") ?: "null"}"
                },
            ) { index, period ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                ) {
                    MifosTableRow(
                        cells = listOf(
                            { DataCell(text = period.daysInPeriod?.toString() ?: "", align = TextAlign.Center) },
                            { DataCell(text = formatDate(period.dueDate), align = TextAlign.Start) },
                            { DataCell(text = formatDate(period.obligationsMetOnDate), align = TextAlign.Start) },
                            { DataCell(text = formatCurrency(period.principalLoanBalanceOutstanding), align = TextAlign.End) },
                            { DataCell(text = formatCurrency(period.principalDue), align = TextAlign.End) },
                            { DataCell(text = formatCurrency(period.interestDue), align = TextAlign.End) },
                            { DataCell(text = formatCurrency(period.feeChargesDue), align = TextAlign.End) },
                            { DataCell(text = formatCurrency(period.penaltyChargesDue), align = TextAlign.End) },
                            { DataCell(text = formatCurrency(period.totalDueForPeriod), align = TextAlign.End) },
                            { DataCell(text = formatCurrency(period.totalPaidForPeriod), align = TextAlign.End) },
                            { DataCell(text = formatCurrency(period.totalPaidInAdvanceForPeriod), align = TextAlign.End) },
                            { DataCell(text = formatCurrency(period.totalPaidLateForPeriod), align = TextAlign.End) },
                            { DataCell(text = formatCurrency(period.totalOutstandingForPeriod), align = TextAlign.End) },
                        ),
                        widths = columnWidths,
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        edgeOffset = DesignToken.padding.medium,
                        showTopBorder = false,
                        showBottomBorder = index < periods.lastIndex, // Only show bottom border if not last row
                    )
                }
            }
        }

        // Bottom Summary Bar - explicit layout instead of overlapping
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        BottomBarLoanRepaymentSchedule(
            totalPaid = RepaymentSchedule.getNumberOfRepaymentsComplete(periods).toString(),
            totalOverdue = RepaymentSchedule.getNumberOfRepaymentsOverDue(periods).toString(),
            tvTotalUpcoming = RepaymentSchedule.getNumberOfRepaymentsPending(periods).toString(),
            modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant),
        )
    }
}

@Composable
private fun HeaderCell(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = DesignToken.padding.small,
                horizontal = DesignToken.padding.extraSmall,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DataCell(
    text: String,
    align: TextAlign = TextAlign.Center,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = DesignToken.padding.small,
                horizontal = DesignToken.padding.extraSmall,
            ),
        contentAlignment = when (align) {
            TextAlign.Start -> Alignment.CenterStart
            TextAlign.End -> Alignment.CenterEnd
            else -> Alignment.Center
        },
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = align,
        )
    }
}

@Composable
private fun BottomBarLoanRepaymentSchedule(
    totalPaid: String,
    totalOverdue: String,
    tvTotalUpcoming: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = DesignToken.padding.medium,
                    vertical = DesignToken.padding.small,
                ),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.feature_loan_complete) + ": $totalPaid",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
            )

            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.feature_loan_pending) + ": $tvTotalUpcoming",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )

            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.feature_loan_overdue) + ": $totalOverdue",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
            )
        }
    }
}

private class LoanRepaymentSchedulePreviewProvider :
    PreviewParameterProvider<LoanRepaymentScheduleUiState> {

    val loanWithAssociations = LoanWithAssociationsEntity(
        repaymentSchedule = RepaymentSchedule(
            periods = listOf(
                Period(
                    complete = true,
                    daysInPeriod = 30,
                    dueDate = listOf(2024, 6, 1),
                    obligationsMetOnDate = listOf(2024, 6, 1),
                    principalLoanBalanceOutstanding = 9000.0,
                    principalDue = 1000.0,
                    interestDue = 50.0,
                    feeChargesDue = 10.0,
                    penaltyChargesDue = 0.0,
                    totalDueForPeriod = 1060.0,
                    totalPaidForPeriod = 1060.0,
                    totalPaidInAdvanceForPeriod = 0.0,
                    totalPaidLateForPeriod = 0.0,
                    totalOutstandingForPeriod = 0.0,
                ),
                Period(
                    complete = false,
                    daysInPeriod = 30,
                    dueDate = listOf(2024, 7, 1),
                    principalLoanBalanceOutstanding = 8000.0,
                    principalDue = 1000.0,
                    interestDue = 45.0,
                    feeChargesDue = 10.0,
                    penaltyChargesDue = 5.0,
                    totalDueForPeriod = 1060.0,
                    totalPaidForPeriod = 500.0,
                    totalPaidInAdvanceForPeriod = 0.0,
                    totalPaidLateForPeriod = 0.0,
                    totalOutstandingForPeriod = 560.0,
                    totalOverdue = 560.0,
                ),
            ),
        ),
    )

    override val values: Sequence<LoanRepaymentScheduleUiState>
        get() = sequenceOf(
            LoanRepaymentScheduleUiState.ShowFetchingError("Error fetching loan repayment schedule"),
            LoanRepaymentScheduleUiState.ShowProgressbar,
            LoanRepaymentScheduleUiState.ShowLoanRepaySchedule(loanWithAssociations),
        )
}

@Composable
@Preview
private fun PreviewLoanRepaymentSchedule(
    @PreviewParameter(LoanRepaymentSchedulePreviewProvider::class) loanRepaymentScheduleUiState: LoanRepaymentScheduleUiState,
) {
    LoanRepaymentScheduleScreen(
        uiState = loanRepaymentScheduleUiState,
        navigateBack = { },
        onRetry = {},
    )
}
