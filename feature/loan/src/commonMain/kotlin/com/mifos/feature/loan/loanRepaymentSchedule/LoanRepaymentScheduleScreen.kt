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
import androidclient.feature.loan.generated.resources.feature_loan_amount_paid
import androidclient.feature.loan.generated.resources.feature_loan_balance
import androidclient.feature.loan.generated.resources.feature_loan_complete
import androidclient.feature.loan.generated.resources.feature_loan_date
import androidclient.feature.loan.generated.resources.feature_loan_days
import androidclient.feature.loan.generated.resources.feature_loan_in_advance
import androidclient.feature.loan.generated.resources.feature_loan_late
import androidclient.feature.loan.generated.resources.feature_loan_loan_amount_due
import androidclient.feature.loan.generated.resources.feature_loan_loan_fees
import androidclient.feature.loan.generated.resources.feature_loan_loan_interest
import androidclient.feature.loan.generated.resources.feature_loan_loan_penalty
import androidclient.feature.loan.generated.resources.feature_loan_loan_principal
import androidclient.feature.loan.generated.resources.feature_loan_loan_repayment_schedule
import androidclient.feature.loan.generated.resources.feature_loan_outstanding
import androidclient.feature.loan.generated.resources.feature_loan_overdue
import androidclient.feature.loan.generated.resources.feature_loan_paid_date
import androidclient.feature.loan.generated.resources.feature_loan_pending
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    val smallWidth = DesignToken.sizes.tableCellWidthSmall
    val mediumWidth = DesignToken.sizes.tableCellWidthMedium
    val largeWidth = DesignToken.sizes.tableCellWidthLarge

    val columnWidths = listOf(
        smallWidth,   // Days
        largeWidth,   // Date
        largeWidth,   // Paid Date
        mediumWidth,  // Balance
        mediumWidth,  // Principal
        mediumWidth,  // Interest
        mediumWidth,  // Fees
        mediumWidth,  // Penalties
        mediumWidth,  // Due
        mediumWidth,  // Paid
        mediumWidth,  // In Advance
        mediumWidth,  // Late
        mediumWidth,  // Outstanding
    )

    val headers = listOf(
        stringResource(Res.string.feature_loan_days),
        stringResource(Res.string.feature_loan_date),
        stringResource(Res.string.feature_loan_paid_date),
        stringResource(Res.string.feature_loan_balance),
        stringResource(Res.string.feature_loan_loan_principal),
        stringResource(Res.string.feature_loan_loan_interest),
        stringResource(Res.string.feature_loan_loan_fees),
        stringResource(Res.string.feature_loan_loan_penalty),
        stringResource(Res.string.feature_loan_loan_amount_due),
        stringResource(Res.string.feature_loan_amount_paid),
        stringResource(Res.string.feature_loan_in_advance),
        stringResource(Res.string.feature_loan_late),
        stringResource(Res.string.feature_loan_outstanding),
    )

    fun formatCurrency(amount: Double?): String {
        if (amount == null) return ""
        return CurrencyFormatter.format(
            balance = amount,
            currencyCode = currencyCode.orEmpty(),
            maximumFractionDigits = decimalPlaces ?: 2,
        )
    }

    fun formatDate(date: List<Int>?): String {
        return date?.let { DateHelper.getDateAsString(it) } ?: ""
    }

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
                        cells = headers.map { headerText ->
                            {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            vertical = KptTheme.spacing.sm,
                                            horizontal = KptTheme.spacing.xs,
                                        ),
                                ) {
                                    Text(
                                        text = headerText,
                                        style = KptTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Left,
                                    )
                                }
                            }
                        },
                        widths = columnWidths,
                        backgroundColor = lerp(
                            KptTheme.colorScheme.surface,
                            KptTheme.colorScheme.primary,
                            0.3f,
                        ),
                        edgeOffset = DesignToken.padding.medium,
                        cornerShape = DesignToken.shapes.topMedium,
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                ) {
                    periods.forEach { period ->
                        RepaymentScheduleRow(
                            period = period,
                            widths = columnWidths,
                            formatCurrency = ::formatCurrency,
                            formatDate = ::formatDate,
                        )
                    }
                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = KptTheme.colorScheme.outlineVariant,
        )
        BottomBarLoanRepaymentSchedule(
            totalPaid = RepaymentSchedule.getNumberOfRepaymentsComplete(periods).toString(),
            totalOverdue = RepaymentSchedule.getNumberOfRepaymentsOverDue(periods).toString(),
            tvTotalUpcoming = RepaymentSchedule.getNumberOfRepaymentsPending(periods).toString(),
            modifier = Modifier.background(color = KptTheme.colorScheme.surfaceVariant),
        )
    }
}

@Composable
private fun RepaymentScheduleRow(
    period: Period,
    widths: List<Dp>,
    formatCurrency: (Double?) -> String,
    formatDate: (List<Int>?) -> String,
) {
    val textValues = listOf(
        period.daysInPeriod?.toString() ?: "",
        formatDate(period.dueDate),
        formatDate(period.obligationsMetOnDate),
        formatCurrency(period.principalLoanBalanceOutstanding),
        formatCurrency(period.principalDue),
        formatCurrency(period.interestDue),
        formatCurrency(period.feeChargesDue),
        formatCurrency(period.penaltyChargesDue),
        formatCurrency(period.totalDueForPeriod),
        formatCurrency(period.totalPaidForPeriod),
        formatCurrency(period.totalPaidInAdvanceForPeriod),
        formatCurrency(period.totalPaidLateForPeriod),
        formatCurrency(period.totalOutstandingForPeriod),
    )

    val cells: List<@Composable () -> Unit> = textValues.map { value ->
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KptTheme.colorScheme.surface)
                    .padding(
                        vertical = KptTheme.spacing.sm,
                        horizontal = KptTheme.spacing.xs,
                    ),
            ) {
                Text(
                    text = value,
                    style = KptTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    color = KptTheme.colorScheme.onBackground,
                )
            }
        }
    }

    MifosTableRow(
        cells = cells,
        widths = widths,
        backgroundColor = KptTheme.colorScheme.surface,
        edgeOffset = DesignToken.padding.medium,
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
                style = KptTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = KptTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
            )

            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.feature_loan_pending) + ": $tvTotalUpcoming",
                style = KptTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = KptTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )

            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.feature_loan_overdue) + ": $totalOverdue",
                style = KptTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = KptTheme.colorScheme.onSurface,
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
