/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountGeneral

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_general_maturity_date
import androidclient.feature.loan.generated.resources.feature_loan_general_number_of_repayments
import androidclient.feature.loan.generated.resources.feature_loan_general_section_loan_details
import androidclient.feature.loan.generated.resources.feature_loan_general_section_loan_summary
import androidclient.feature.loan.generated.resources.feature_loan_general_section_performance_history
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_col_component
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_col_original
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_col_outstanding
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_col_overdue
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_col_paid
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_col_waived
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_col_written_off
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_row_total
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosTableRow
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosDefaultListingComponentFromStringResources
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun LoanAccountGeneralScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: LoanAccountGeneralViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val onAction = remember(viewModel) { { action: LoanAccountGeneralAction -> viewModel.trySendAction(action) } }

    LoanAccountGeneralContent(
        state = state,
        navController = navController,
        modifier = modifier,
    )

    LoanAccountGeneralDialogs(
        state = state,
        onAction = onAction,
    )
}

@Composable
private fun LoanAccountGeneralDialogs(
    state: LoanAccountGeneralState,
    onAction: (LoanAccountGeneralAction) -> Unit,
) {
    when (state.dialogState) {
        is LoanAccountGeneralState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = { onAction(LoanAccountGeneralAction.OnRetry) },
            )
        }

        LoanAccountGeneralState.DialogState.Loading -> MifosProgressIndicator()

        null -> Unit
    }
}

@Composable
private fun LoanAccountGeneralContent(
    state: LoanAccountGeneralState,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    if (state.dialogState == null) {
        Column(modifier = modifier.fillMaxSize()) {
            MifosBreadcrumbNavBar(navController = navController)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = stringResource(Res.string.feature_loan_general_section_performance_history),
                    style = KptTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
                )

                Spacer(Modifier.height(DesignToken.spacing.medium))

                PerformanceHistoryCard(
                    state = state,
                    modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
                )

                Spacer(Modifier.height(DesignToken.spacing.largeIncreased))

                Text(
                    text = stringResource(Res.string.feature_loan_general_section_loan_summary),
                    style = KptTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
                )

                Spacer(Modifier.height(DesignToken.spacing.medium))

                LoanSummaryTable(state = state)

                Spacer(Modifier.height(DesignToken.spacing.largeIncreased))

                Text(
                    text = stringResource(Res.string.feature_loan_general_section_loan_details),
                    style = KptTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
                )

                Spacer(Modifier.height(DesignToken.spacing.medium))

                LoanDetailsSection(
                    state = state,
                    modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
                )

                Spacer(Modifier.height(KptTheme.spacing.xl))
            }
        }
    }
}

@Composable
private fun PerformanceHistoryCard(
    state: LoanAccountGeneralState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(KptTheme.shapes.medium)
            .background(KptTheme.colorScheme.primary)
            .padding(KptTheme.spacing.lg),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PerformanceHistoryRow(
                label = stringResource(Res.string.feature_loan_general_number_of_repayments),
                value = state.numberOfRepayments,
            )
            PerformanceHistoryRow(
                label = stringResource(Res.string.feature_loan_general_maturity_date),
                value = state.maturityDate,
            )
        }
    }
}

@Composable
private fun PerformanceHistoryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        color = AppColors.customWhite,
        fontStyle = KptTheme.typography.labelMedium.fontStyle,
    ),
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = textStyle)
        Text(text = value, style = textStyle)
    }
}

@Composable
private fun LoanSummaryTable(
    state: LoanAccountGeneralState,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val textColor = KptTheme.colorScheme.onBackground
    val componentWidth = DesignToken.sizes.tableCellWidthLarge
    val amountWidth = DesignToken.sizes.tableCellWidthMedium
    val colWidths = listOf(componentWidth, amountWidth, amountWidth, amountWidth, amountWidth, amountWidth, amountWidth)

    val headers = listOf(
        stringResource(Res.string.feature_loan_general_summary_col_component),
        stringResource(Res.string.feature_loan_general_summary_col_original),
        stringResource(Res.string.feature_loan_general_summary_col_paid),
        stringResource(Res.string.feature_loan_general_summary_col_waived),
        stringResource(Res.string.feature_loan_general_summary_col_written_off),
        stringResource(Res.string.feature_loan_general_summary_col_outstanding),
        stringResource(Res.string.feature_loan_general_summary_col_overdue),
    )

    val totalLabel = stringResource(Res.string.feature_loan_general_summary_row_total)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
    ) {
        MifosTableRow(
            cells = headers.map { label ->
                {
                    LoanSummaryTableCell(
                        text = label,
                        style = KptTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        textColor = textColor,
                    )
                }
            },
            widths = colWidths,
            backgroundColor = lerp(
                KptTheme.colorScheme.surface,
                KptTheme.colorScheme.primary,
                0.3f,
            ),
            edgeOffset = DesignToken.padding.medium,
            cornerShape = DesignToken.shapes.topMedium,
        )

        state.summaryRows.forEach { row ->
            val amounts = listOf(row.original, row.paid, row.waived, row.writtenOff, row.outstanding, row.overDue)

            MifosTableRow(
                cells = buildList {
                    add {
                        LoanSummaryTableCell(
                            text = row.component,
                            style = KptTheme.typography.bodySmall,
                            fontWeight = FontWeight.Normal,
                            textColor = textColor,
                        )
                    }
                    amounts.forEach { amount ->
                        add {
                            LoanSummaryTableCell(
                                text = amount,
                                style = KptTheme.typography.bodySmall,
                                fontWeight = FontWeight.Normal,
                                textColor = textColor,
                            )
                        }
                    }
                },
                widths = colWidths,
                backgroundColor = KptTheme.colorScheme.surface,
                edgeOffset = DesignToken.padding.medium,
            )
        }

        val totals = listOf(state.totalOriginal, state.totalPaid, state.totalWaived, state.totalWrittenOff, state.totalOutstanding, state.totalOverDue)

        MifosTableRow(
            cells = buildList {
                add {
                    LoanSummaryTableCell(
                        text = totalLabel,
                        style = KptTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textColor = textColor,
                    )
                }
                totals.forEach { total ->
                    add {
                        LoanSummaryTableCell(
                            text = total,
                            style = KptTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textColor = textColor,
                        )
                    }
                }
            },
            widths = colWidths,
            backgroundColor = lerp(KptTheme.colorScheme.surface, KptTheme.colorScheme.primary, 0.15f),
            edgeOffset = DesignToken.padding.medium,
            cornerShape = DesignToken.shapes.bottomMedium,
            showBottomBorder = false,
        )
    }
}

@Composable
private fun LoanDetailsSection(
    state: LoanAccountGeneralState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignToken.padding.medium),
    ) {
        state.details.forEach { detailGroup ->
            MifosDefaultListingComponentFromStringResources(data = detailGroup)
        }
    }
}

@Composable
private fun LoanSummaryTableCell(
    text: String,
    style: TextStyle,
    fontWeight: FontWeight,
    textColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = KptTheme.spacing.sm, horizontal = KptTheme.spacing.xs),
    ) {
        Text(
            text = text,
            style = style,
            fontWeight = fontWeight,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Left,
            color = textColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoanAccountGeneralPreview() {
    MifosTheme {
        val previewState = LoanAccountGeneralState(
            numberOfRepayments = "12",
            maturityDate = "17 August 2026",
            summaryRows = listOf(
                LoanAccountGeneralState.SummaryRowState("Principal", "$1,243.00", "$1,243.00", "$0.00", "$0.00", "$0.00", "$0.00"),
                LoanAccountGeneralState.SummaryRowState("Interest", "$946.09", "$946.09", "$0.00", "$0.00", "$0.00", "$0.00"),
                LoanAccountGeneralState.SummaryRowState("Fees", "$20.00", "$20.00", "$0.00", "$0.00", "$0.00", "$0.00"),
                LoanAccountGeneralState.SummaryRowState("Penalties", "$0.00", "$0.00", "$0.00", "$0.00", "$0.00", "$0.00"),
            ),
            totalOriginal = "$2,209.09",
            totalPaid = "$2,209.09",
            totalWaived = "$0.00",
            totalWrittenOff = "$0.00",
            totalOutstanding = "$0.00",
            totalOverDue = "$0.00",
            disbursementDate = "17 August 2025",
            loanPurpose = "Not Available",
            loanOfficer = "Unassigned",
            currency = "US Dollar USD",
            externalId = "Not Available",
            proposedAmount = "$1,243.00",
            approvedAmount = "$1,243.00",
            disbursedAmount = "$1,243.00",
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = KptTheme.spacing.md),
        ) {
            Text(
                text = stringResource(Res.string.feature_loan_general_section_performance_history),
                style = KptTheme.typography.labelLarge,
            )
            Spacer(Modifier.height(DesignToken.spacing.medium))
            PerformanceHistoryCard(state = previewState)

            Spacer(Modifier.height(DesignToken.spacing.largeIncreased))
            Text(
                text = stringResource(Res.string.feature_loan_general_section_loan_summary),
                style = KptTheme.typography.labelLarge,
            )
            Spacer(Modifier.height(DesignToken.spacing.medium))
            LoanSummaryTable(state = previewState)

            Spacer(Modifier.height(DesignToken.spacing.largeIncreased))
            Text(
                text = stringResource(Res.string.feature_loan_general_section_loan_details),
                style = KptTheme.typography.labelLarge,
            )
            Spacer(Modifier.height(DesignToken.spacing.medium))
            LoanDetailsSection(state = previewState)

            Spacer(Modifier.height(KptTheme.spacing.xl))
        }
    }
}
