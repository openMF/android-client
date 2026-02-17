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
import androidclient.feature.loan.generated.resources.feature_loan_balance
import androidclient.feature.loan.generated.resources.feature_loan_complete
import androidclient.feature.loan.generated.resources.feature_loan_date
import androidclient.feature.loan.generated.resources.feature_loan_loan_fees
import androidclient.feature.loan.generated.resources.feature_loan_loan_interest
import androidclient.feature.loan.generated.resources.feature_loan_loan_penalty
import androidclient.feature.loan.generated.resources.feature_loan_loan_principal
import androidclient.feature.loan.generated.resources.feature_loan_loan_repayment_schedule
import androidclient.feature.loan.generated.resources.feature_loan_overdue
import androidclient.feature.loan.generated.resources.feature_loan_pending
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
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

    LaunchedEffect(key1 = Unit) {
        viewModel.loadLoanRepaySchedule()
    }

    LoanRepaymentScheduleScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = {
            viewModel.viewModelScope.launch {
                viewModel.loadLoanRepaySchedule()
            }
        },
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

    // Define fixed column widths for all 13 columns
    // Days, Date, Paid Date, Balance, Principal, Interest, Fees, Penalties, Due, Paid, In Advance, Late, Outstanding
    val columnWidths = listOf(
        50.dp,
        90.dp,
        90.dp,
        110.dp,
        100.dp,
        90.dp,
        80.dp,
        90.dp,
        90.dp,
        90.dp,
        100.dp,
        80.dp,
        110.dp,
    )

    fun formatCurrency(amount: Double?): String {
        if (amount == null) return ""
        if (currencyCode.isNullOrBlank()) return amount.toString()

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
        Box(
            modifier = Modifier.weight(1f),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                stickyHeader {
                    MifosTableRow(
                        cells = listOf(
                            { HeaderCell(text = "Days") },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_date)) },
                            { HeaderCell(text = "Paid Date") },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_balance)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_loan_principal)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_loan_interest)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_loan_fees)) },
                            { HeaderCell(text = stringResource(Res.string.feature_loan_loan_penalty)) },
                            { HeaderCell(text = "Due") },
                            { HeaderCell(text = "Paid") },
                            { HeaderCell(text = "In Advance") },
                            { HeaderCell(text = "Late") },
                            { HeaderCell(text = "Outstanding") },
                        ),
                        columnWidths = columnWidths,
                        scrollState = scrollState,
                        isHeader = true,
                        backgroundColor = lerp(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.primary,
                            0.08f,
                        ),
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                // Data Rows with unique keys for performance
                items(
                    items = periods,
                    key = { period ->
                        // Use dueDate as unique key, fallback to hashCode if null
                        period.dueDate?.joinToString("-") ?: period.hashCode()
                    },
                ) { period ->
                    MifosTableRow(
                        cells = listOf(
                            { DataCell(text = period.daysInPeriod?.toString() ?: "", align = TextAlign.Left) },
                            { DataCell(text = formatDate(period.dueDate), align = TextAlign.Left) },
                            { DataCell(text = formatDate(period.obligationsMetOnDate), align = TextAlign.Left) },
                            { DataCell(text = formatCurrency(period.principalLoanBalanceOutstanding), align = TextAlign.Right) },
                            { DataCell(text = formatCurrency(period.principalDue), align = TextAlign.Right) },
                            { DataCell(text = formatCurrency(period.interestDue), align = TextAlign.Right) },
                            { DataCell(text = formatCurrency(period.feeChargesDue), align = TextAlign.Right) },
                            { DataCell(text = formatCurrency(period.penaltyChargesDue), align = TextAlign.Right) },
                            { DataCell(text = formatCurrency(period.totalDueForPeriod), align = TextAlign.Right) },
                            { DataCell(text = formatCurrency(period.totalPaidForPeriod), align = TextAlign.Right) },
                            { DataCell(text = formatCurrency(period.totalPaidInAdvanceForPeriod), align = TextAlign.Right) },
                            { DataCell(text = formatCurrency(period.totalPaidLateForPeriod), align = TextAlign.Right) },
                            { DataCell(text = formatCurrency(period.totalOutstandingForPeriod), align = TextAlign.Right) },
                        ),
                        columnWidths = columnWidths,
                        scrollState = scrollState,
                        isHeader = false,
                    )
                }
            }

            // Bottom Summary Bar
            BottomBarLoanRepaymentSchedule(
                totalPaid = RepaymentSchedule.getNumberOfRepaymentsComplete(periods).toString(),
                totalOverdue = RepaymentSchedule.getNumberOfRepaymentsOverDue(periods).toString(),
                tvTotalUpcoming = RepaymentSchedule.getNumberOfRepaymentsPending(periods).toString(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant),
            )
        }
    }
}

@Composable
private fun HeaderCell(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun DataCell(
    text: String,
    align: TextAlign = TextAlign.Center,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = align,
        modifier = Modifier.fillMaxWidth(),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
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
