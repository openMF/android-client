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
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_approved_amount
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_currency
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_disbursed_amount
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_disbursement_date
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_loan_officer
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_loan_purpose
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_proposed_amount
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosTableRow
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosListingColumnItem
import com.mifos.core.ui.components.MifosListingComponentOutline
import com.mifos.core.ui.components.MifosListingRowItem
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

    LoanAccountGeneralScreen(
        state = state,
        onAction = viewModel::trySendAction,
        navController = navController,
        modifier = modifier,
    )
}

@Composable
internal fun LoanAccountGeneralScreen(
    state: LoanAccountGeneralState,
    onAction: (LoanAccountGeneralAction) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        MifosBreadcrumbNavBar(navController = navController)

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

            null -> {
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
}

@Composable
private fun PerformanceHistoryCard(
    state: LoanAccountGeneralState,
    modifier: Modifier = Modifier,
) {
    MifosListingComponentOutline(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.medium),
        ) {
            MifosListingRowItem(
                key = stringResource(Res.string.feature_loan_general_number_of_repayments),
                value = state.numberOfRepayments,
                valueStyle = MifosTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
            )
            MifosListingRowItem(
                key = stringResource(Res.string.feature_loan_general_maturity_date),
                value = state.maturityDate,
                valueStyle = MifosTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

@Composable
private fun LoanSummaryTable(
    state: LoanAccountGeneralState,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val textColor = KptTheme.colorScheme.onSurface
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
    val amountColors = listOf(textColor, KptTheme.colorScheme.primary, KptTheme.colorScheme.primary, KptTheme.colorScheme.primary, textColor, textColor)

    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
        ) {
            MifosTableRow(
                cells = headers.map { label ->
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
                                text = label,
                                style = KptTheme.typography.titleSmall,
                                softWrap = true,
                            )
                        }
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

            state.summaryRows.forEachIndexed { index, row ->
                val amounts = listOf(row.original, row.paid, row.waived, row.writtenOff, row.outstanding, row.overDue)

                MifosTableRow(
                    cells = buildList {
                        add {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        vertical = DesignToken.padding.small,
                                        horizontal = DesignToken.padding.extraSmall,
                                    ),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Text(
                                    text = row.component,
                                    style = KptTheme.typography.bodySmall,
                                    color = textColor,
                                )
                            }
                        }
                        amounts.forEachIndexed { i, amount ->
                            add {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(
                                            vertical = DesignToken.padding.small,
                                            horizontal = DesignToken.padding.extraSmall,
                                        ),
                                    contentAlignment = Alignment.CenterStart,
                                ) {
                                    Text(
                                        text = amount,
                                        style = KptTheme.typography.bodySmall,
                                        color = amountColors[i],
                                    )
                                }
                            }
                        }
                    },
                    widths = colWidths,
                    backgroundColor = if (index % 2 != 0) {
                        lerp(KptTheme.colorScheme.surface, KptTheme.colorScheme.primary, 0.08f)
                    } else {
                        KptTheme.colorScheme.surface
                    },
                    edgeOffset = DesignToken.padding.medium,
                )
            }

            val totals = listOf(state.totalOriginal, state.totalPaid, state.totalWaived, state.totalWrittenOff, state.totalOutstanding, state.totalOverDue)

            MifosTableRow(
                cells = buildList {
                    add {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    vertical = DesignToken.padding.small,
                                    horizontal = DesignToken.padding.extraSmall,
                                ),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = totalLabel,
                                style = KptTheme.typography.titleSmall,
                                color = textColor,
                            )
                        }
                    }
                    totals.forEachIndexed { i, total ->
                        add {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        vertical = DesignToken.padding.small,
                                        horizontal = DesignToken.padding.extraSmall,
                                    ),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Text(
                                    text = total,
                                    style = KptTheme.typography.titleSmall,
                                    color = amountColors[i],
                                )
                            }
                        }
                    }
                },
                widths = colWidths,
                backgroundColor = lerp(KptTheme.colorScheme.surface, KptTheme.colorScheme.primary, 0.08f),
                edgeOffset = DesignToken.padding.medium,
                cornerShape = DesignToken.shapes.bottomMedium,
                showBottomBorder = false,
            )
        }
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
        MifosListingComponentOutline {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.medium),
            ) {
                MifosListingColumnItem(
                    key = stringResource(Res.string.feature_loan_general_detail_disbursement_date),
                    value = state.disbursementDate,
                )
                MifosListingColumnItem(
                    key = stringResource(Res.string.feature_loan_general_detail_loan_purpose),
                    value = state.loanPurpose,
                )
                MifosListingColumnItem(
                    key = stringResource(Res.string.feature_loan_general_detail_loan_officer),
                    value = state.loanOfficer,
                )
                MifosListingColumnItem(
                    key = stringResource(Res.string.feature_loan_general_detail_currency),
                    value = state.currency,
                )
            }
        }

        MifosListingComponentOutline {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.medium),
            ) {
                MifosListingRowItem(
                    key = stringResource(Res.string.feature_loan_general_detail_proposed_amount),
                    value = state.proposedAmount,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.feature_loan_general_detail_approved_amount),
                    value = state.approvedAmount,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.feature_loan_general_detail_disbursed_amount),
                    value = state.disbursedAmount,
                    keyStyle = MifosTypography.labelMediumEmphasized.copy(color = KptTheme.colorScheme.primary),
                    valueColor = KptTheme.colorScheme.primary,
                )
            }
        }
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
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "PERFORMANCE HISTORY",
                style = KptTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
            )
            Spacer(Modifier.height(DesignToken.spacing.medium))
            PerformanceHistoryCard(state = previewState, modifier = Modifier.padding(horizontal = KptTheme.spacing.md))
            Spacer(Modifier.height(DesignToken.spacing.largeIncreased))
            Text(
                text = "LOAN SUMMARY",
                style = KptTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
            )
            Spacer(Modifier.height(DesignToken.spacing.medium))
            LoanSummaryTable(state = previewState)
            Spacer(Modifier.height(DesignToken.spacing.largeIncreased))
            Text(
                text = "LOAN DETAILS",
                style = KptTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
            )
            Spacer(Modifier.height(DesignToken.spacing.medium))
            LoanDetailsSection(state = previewState, modifier = Modifier.padding(horizontal = KptTheme.spacing.md))
            Spacer(Modifier.height(KptTheme.spacing.xl))
        }
    }
}
