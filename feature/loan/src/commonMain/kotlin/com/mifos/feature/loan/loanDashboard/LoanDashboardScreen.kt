/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDashboard

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_account_approve
import androidclient.feature.loan.generated.resources.feature_loan_account_credit_balance_refund
import androidclient.feature.loan.generated.resources.feature_loan_account_disburse
import androidclient.feature.loan.generated.resources.feature_loan_account_make_repayment
import androidclient.feature.loan.generated.resources.feature_loan_account_reject
import androidclient.feature.loan.generated.resources.feature_loan_account_undo_approval
import androidclient.feature.loan.generated.resources.feature_loan_amount
import androidclient.feature.loan.generated.resources.feature_loan_approved_amount
import androidclient.feature.loan.generated.resources.feature_loan_dashboard
import androidclient.feature.loan.generated.resources.feature_loan_disbursed_amount
import androidclient.feature.loan.generated.resources.feature_loan_due_date
import androidclient.feature.loan.generated.resources.feature_loan_expected_next_payment
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan
import androidclient.feature.loan.generated.resources.feature_loan_interest
import androidclient.feature.loan.generated.resources.feature_loan_interest_charged
import androidclient.feature.loan.generated.resources.feature_loan_next_payment
import androidclient.feature.loan.generated.resources.feature_loan_outstanding
import androidclient.feature.loan.generated.resources.feature_loan_payment_schedule
import androidclient.feature.loan.generated.resources.feature_loan_period
import androidclient.feature.loan.generated.resources.feature_loan_principal
import androidclient.feature.loan.generated.resources.feature_loan_projected_payment_schedule
import androidclient.feature.loan.generated.resources.feature_loan_recent_transactions
import androidclient.feature.loan.generated.resources.feature_loan_remaining_balance
import androidclient.feature.loan.generated.resources.feature_loan_repaid
import androidclient.feature.loan.generated.resources.feature_loan_repayment_progress
import androidclient.feature.loan.generated.resources.feature_loan_requested_amount
import androidclient.feature.loan.generated.resources.feature_loan_rescheduled_amount
import androidclient.feature.loan.generated.resources.feature_loan_status_active
import androidclient.feature.loan.generated.resources.feature_loan_status_closed_met
import androidclient.feature.loan.generated.resources.feature_loan_status_closed_rescheduled
import androidclient.feature.loan.generated.resources.feature_loan_status_closed_written_off
import androidclient.feature.loan.generated.resources.feature_loan_status_overpaid
import androidclient.feature.loan.generated.resources.feature_loan_status_pending_approval
import androidclient.feature.loan.generated.resources.feature_loan_status_rejected
import androidclient.feature.loan.generated.resources.feature_loan_status_waiting_for_disbursal
import androidclient.feature.loan.generated.resources.feature_loan_status_withdrawn_by_applicant
import androidclient.feature.loan.generated.resources.feature_loan_surplus_balance
import androidclient.feature.loan.generated.resources.feature_loan_timeline
import androidclient.feature.loan.generated.resources.feature_loan_total_interest
import androidclient.feature.loan.generated.resources.feature_loan_total_outstanding
import androidclient.feature.loan.generated.resources.feature_loan_total_payment_received
import androidclient.feature.loan.generated.resources.feature_loan_total_repaid
import androidclient.feature.loan.generated.resources.feature_loan_transaction_charge_payment
import androidclient.feature.loan.generated.resources.feature_loan_transaction_disbursement
import androidclient.feature.loan.generated.resources.feature_loan_transaction_repayment
import androidclient.feature.loan.generated.resources.feature_loan_view_all
import androidclient.feature.loan.generated.resources.feature_loan_written_off_amount
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.designsystem.component.BarSegment
import com.mifos.core.designsystem.component.ChartLegendItem
import com.mifos.core.designsystem.component.DonutSegment
import com.mifos.core.designsystem.component.LegendToggleItem
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosDonutGraph
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.StackedBarChart
import com.mifos.core.designsystem.component.StackedBarItem
import com.mifos.core.designsystem.component.calculateNiceMax
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import com.mifos.room.entities.accounts.loans.LoanStatusEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun LoanDashboardScreen(
    onNavigateBack: () -> Unit,
    navigateToTransactions: (Int) -> Unit,
    navigateToRejectLoan: (Int) -> Unit = {},
    navigateToApproveLoan: (Int) -> Unit = {},
    navigateToUndoApproval: (Int) -> Unit = {},
    navigateToDisburseLoan: (Int) -> Unit = {},
    navigateToMakeRepayment: (Int) -> Unit = {},
    navigateToCreditBalanceRefund: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: LoanDashboardViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanDashboardEvent.NavigateBack -> onNavigateBack()
            is LoanDashboardEvent.NavigateToTransactions -> navigateToTransactions(event.loanId)
            is LoanDashboardEvent.NavigateToApproveLoan -> navigateToApproveLoan(event.loanId)
            is LoanDashboardEvent.NavigateToCreditBalanceRefund -> navigateToCreditBalanceRefund(
                event.loanId,
            )

            is LoanDashboardEvent.NavigateToDisburseLoan -> navigateToDisburseLoan(event.loanId)
            is LoanDashboardEvent.NavigateToMakeRepayment -> navigateToMakeRepayment(event.loanId)
            is LoanDashboardEvent.NavigateToRejectLoan -> navigateToRejectLoan(event.loanId)
            is LoanDashboardEvent.NavigateToUndoApproval -> navigateToUndoApproval(event.loanId)
        }
    }

    LoanDashboardScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun LoanDashboardScreenContent(
    state: LoanDashboardState,
    onAction: (LoanDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        title = stringResource(Res.string.feature_loan_dashboard),
        onBackPressed = { onAction(LoanDashboardAction.NavigateBack) },
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(KptTheme.colorScheme.background)
                .padding(it),
        ) {
            when (state.viewState) {
                LoanDashboardState.ViewState.Loading -> {
                    MifosProgressIndicator()
                }

                LoanDashboardState.ViewState.Empty -> {
                    MifosEmptyUi(text = stringResource(Res.string.feature_loan_failed_to_load_loan))
                }

                is LoanDashboardState.ViewState.Error -> {
                    val displayMessage = stringResource(state.viewState.message)
                    MifosSweetError(
                        message = displayMessage,
                        onclick = { onAction(LoanDashboardAction.OnRetry) },
                    )
                }

                LoanDashboardState.ViewState.Success -> {
                    val currencyCode = state.loanDetails.currency.code
                    val maxDigits = state.loanDetails.currency.decimalPlaces

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(DesignToken.padding.large),
                        verticalArrangement = Arrangement.spacedBy(DesignToken.padding.medium),
                    ) {
                        item {
                            LoanHeroSummaryCard(
                                productName = state.loanDetails.loanProductName,
                                accountNumber = state.loanDetails.accountNo,
                                status = state.loanStatus,
                                heroLabel = state.heroLabel?.let { label ->
                                    stringResource(label)
                                } ?: "",
                                heroValue = state.heroValue,
                            )
                        }

                        if (
                            state.loanStatus == LoanStatus.ACTIVE ||
                            state.loanStatus == LoanStatus.WAITING_FOR_DISBURSAL
                        ) {
                            item {
                                NextRepaymentCard(
                                    dueDate = state.nextRepaymentDueDate,
                                    amount = state.nextRepaymentAmount,
                                    isWaitingForDisbursal = state.loanStatus == LoanStatus.WAITING_FOR_DISBURSAL,
                                )
                            }
                        }

                        if (
                            state.loanStatus == LoanStatus.ACTIVE ||
                            state.loanStatus == LoanStatus.OVERPAID ||
                            state.loanStatus == LoanStatus.CLOSED_OBLIGATIONS_MET ||
                            state.loanStatus == LoanStatus.CLOSED_WRITTEN_OFF ||
                            state.loanStatus == LoanStatus.CLOSED_RESCHEDULED
                        ) {
                            item {
                                RecentTransactionsSection(
                                    transactions = state.recentTransactions,
                                    onViewAll = {
                                        onAction(LoanDashboardAction.NavigateToTransactions(state.loanDetails.id))
                                    },
                                )

                                Spacer(modifier = Modifier.height(DesignToken.padding.medium))

                                LoanSummaryGridSection(
                                    principalAmount = CurrencyFormatter.format(
                                        state.loanDetails.summary.principalDisbursed ?: 0.0,
                                        currencyCode,
                                        maxDigits,
                                    ),
                                    interestAmount = CurrencyFormatter.format(
                                        state.loanDetails.summary.interestCharged ?: 0.0,
                                        currencyCode,
                                        maxDigits,
                                    ),
                                    repaidAmount = CurrencyFormatter.format(
                                        state.loanDetails.summary.totalRepayment ?: 0.0,
                                        currencyCode,
                                        maxDigits,
                                    ),
                                    outstandingAmount = CurrencyFormatter.format(
                                        state.loanDetails.summary.totalOutstanding ?: 0.0,
                                        currencyCode,
                                        maxDigits,
                                    ),
                                )

                                state.repaymentProgressData?.let {
                                    Spacer(modifier = Modifier.height(DesignToken.padding.medium))
                                    RepaymentProgressCard(
                                        data = state.repaymentProgressData,
                                    )
                                }
                            }
                        }

                        if (
                            state.loanStatus == LoanStatus.ACTIVE ||
                            state.loanStatus == LoanStatus.OVERPAID ||
                            state.loanStatus == LoanStatus.CLOSED_OBLIGATIONS_MET ||
                            state.loanStatus == LoanStatus.CLOSED_WRITTEN_OFF ||
                            state.loanStatus == LoanStatus.CLOSED_RESCHEDULED ||
                            state.loanStatus == LoanStatus.WAITING_FOR_DISBURSAL
                        ) {
                            item {
                                LoanRepaymentGraphSection(
                                    periods = state.periodsGraphValues,
                                    isPrincipalVisible = state.isGraphPrincipalVisible,
                                    isInterestVisible = state.isGraphInterestVisible,
                                    onTogglePrincipal = {
                                        onAction(LoanDashboardAction.TogglePrincipalVisibility)
                                    },
                                    onToggleInterest = {
                                        onAction(LoanDashboardAction.ToggleInterestVisibility)
                                    },
                                    isWaitingForDisbursal = state.loanStatus == LoanStatus.WAITING_FOR_DISBURSAL,
                                )
                            }
                        }

                        item {
                            LoanTimelineCard(steps = state.timelineSteps)
                        }

                        state.loanStatus?.let {
                            item {
                                LoanStatusActionButtons(
                                    loanStatus = state.loanStatus,
                                    loanId = state.loanDetails.id,
                                    onAction = onAction,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun LoanHeroSummaryCard(
    productName: String,
    accountNumber: String,
    status: LoanStatus?,
    heroLabel: String,
    heroValue: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                BorderStroke(DesignToken.strokes.dpPoint5, KptTheme.colorScheme.outlineVariant),
                DesignToken.shapes.medium,
            )
            .padding(DesignToken.padding.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = productName,
                style = KptTheme.typography.labelMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )

            status?.let {
                LoanStatusBadge(status = it)
            }
        }

        Text(
            text = "#$accountNumber",
            style = MifosTypography.titleMediumEmphasized,
            fontWeight = FontWeight.Medium,
            color = KptTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(DesignToken.padding.medium))

        Text(
            text = heroLabel,
            style = KptTheme.typography.labelMedium,
            color = KptTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )

        Text(
            text = heroValue,
            style = KptTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = KptTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun LoanStatusBadge(
    status: LoanStatus,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when (status) {
        LoanStatus.ACTIVE ->
            AppColors.loanActiveStatus

        LoanStatus.CLOSED_OBLIGATIONS_MET ->
            AppColors.loanClosedObligationsMetStatus

        LoanStatus.PENDING_APPROVAL, LoanStatus.WAITING_FOR_DISBURSAL ->
            AppColors.loanPendingStatus

        LoanStatus.OVERPAID ->
            AppColors.loanOverpaidStatus

        LoanStatus.CLOSED_WRITTEN_OFF ->
            AppColors.loanClosedWrittenOffStatus

        LoanStatus.CLOSED_RESCHEDULED ->
            AppColors.loanClosedRescheduled

        LoanStatus.REJECTED, LoanStatus.WITHDRAWN_BY_APPLICANT ->
            AppColors.loanRejectedStatus
    }

    val statusText = stringResource(status.toBadgeLabelResource())

    Badge(
        containerColor = backgroundColor,
        modifier = modifier,
    ) {
        Text(
            text = statusText,
            style = KptTheme.typography.labelMedium,
            color = AppColors.customWhite,
        )
    }
}

private fun LoanStatus.toBadgeLabelResource(): StringResource =
    when (this) {
        LoanStatus.PENDING_APPROVAL -> Res.string.feature_loan_status_pending_approval
        LoanStatus.WAITING_FOR_DISBURSAL -> Res.string.feature_loan_status_waiting_for_disbursal
        LoanStatus.ACTIVE -> Res.string.feature_loan_status_active
        LoanStatus.OVERPAID -> Res.string.feature_loan_status_overpaid
        LoanStatus.CLOSED_OBLIGATIONS_MET -> Res.string.feature_loan_status_closed_met
        LoanStatus.CLOSED_WRITTEN_OFF -> Res.string.feature_loan_status_closed_written_off
        LoanStatus.CLOSED_RESCHEDULED -> Res.string.feature_loan_status_closed_rescheduled
        LoanStatus.REJECTED -> Res.string.feature_loan_status_rejected
        LoanStatus.WITHDRAWN_BY_APPLICANT -> Res.string.feature_loan_status_withdrawn_by_applicant
    }

@Composable
internal fun NextRepaymentCard(
    dueDate: String,
    amount: String,
    isWaitingForDisbursal: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = if (isWaitingForDisbursal) {
                stringResource(Res.string.feature_loan_expected_next_payment)
            } else {
                stringResource(Res.string.feature_loan_next_payment)
            },
            style = MifosTypography.titleMediumEmphasized,
            color = KptTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(DesignToken.padding.extraSmall))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(DesignToken.strokes.dpPoint5, KptTheme.colorScheme.outlineVariant),
                    DesignToken.shapes.medium,
                )
                .padding(DesignToken.padding.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = MifosIcons.CurrencyExchange,
                    modifier = Modifier
                        .size(DesignToken.sizes.iconExtraLarge)
                        .background(
                            color = KptTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = DesignToken.shapes.circle,
                        )
                        .padding(DesignToken.padding.small),
                    tint = KptTheme.colorScheme.primary,
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(DesignToken.padding.medium))

                Column {
                    Text(
                        text = stringResource(Res.string.feature_loan_due_date),
                        style = KptTheme.typography.titleSmall,
                        color = KptTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(KptTheme.spacing.xs))
                    Text(
                        text = dueDate,
                        style = KptTheme.typography.labelMedium,
                        color = KptTheme.colorScheme.onSurface,
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(Res.string.feature_loan_amount),
                    style = KptTheme.typography.titleSmall,
                    color = KptTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(KptTheme.spacing.xs))
                Text(
                    text = amount,
                    style = KptTheme.typography.labelMedium,
                    color = KptTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
internal fun RecentTransactionsSection(
    transactions: List<RecentTransaction>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (transactions.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.feature_loan_recent_transactions),
                style = MifosTypography.titleMediumEmphasized,
                color = KptTheme.colorScheme.onBackground,
            )

            Text(
                text = stringResource(Res.string.feature_loan_view_all),
                style = KptTheme.typography.labelMedium,
                color = KptTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { onViewAll() }
                    .padding(vertical = DesignToken.padding.extraExtraSmall),
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.padding.extraSmall))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(DesignToken.strokes.dpPoint5, KptTheme.colorScheme.outlineVariant),
                    DesignToken.shapes.medium,
                )
                .padding(DesignToken.padding.medium),
            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraSmall),
        ) {
            transactions.forEachIndexed { index, transaction ->
                TransactionItemCard(transaction = transaction)
                if (index != transactions.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = DesignToken.padding.small),
                        thickness = DesignToken.strokes.dpPoint5,
                        color = KptTheme.colorScheme.outlineVariant.copy(.6f),
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionItemCard(
    transaction: RecentTransaction,
    modifier: Modifier = Modifier,
) {
    val icon = when (transaction.isIncreasingDebt) {
        true -> MifosIcons.TrendingUp
        false -> MifosIcons.TrendingDown
        else -> MifosIcons.SyncAlt
    }
    val iconBgColor = when (transaction.isIncreasingDebt) {
        true -> AppColors.customEnable.copy(.15f)
        false -> KptTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        else -> KptTheme.colorScheme.surfaceVariant
    }
    val iconTintColor = when (transaction.isIncreasingDebt) {
        true -> AppColors.customEnable
        false -> KptTheme.colorScheme.primary
        else -> KptTheme.colorScheme.onSurfaceVariant
    }
    val amountColor = when (transaction.isIncreasingDebt) {
        true -> AppColors.customEnable
        false -> KptTheme.colorScheme.onSurface
        else -> KptTheme.colorScheme.onSurfaceVariant
    }
    val amountPrefix = when (transaction.isIncreasingDebt) {
        true -> "+"
        false -> "-"
        else -> ""
    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                modifier = Modifier
                    .size(DesignToken.sizes.iconExtraLarge)
                    .background(
                        color = iconBgColor,
                        shape = DesignToken.shapes.circle,
                    )
                    .padding(DesignToken.padding.small),
                tint = iconTintColor,
                contentDescription = null,
            )

            Spacer(modifier = Modifier.width(DesignToken.padding.medium))

            Column {
                Text(
                    text = stringResource(transaction.type),
                    style = KptTheme.typography.titleSmall,
                    color = KptTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.xs))

                Text(
                    text = transaction.date,
                    style = KptTheme.typography.labelMedium,
                    color = KptTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Text(
            text = "$amountPrefix${transaction.amount}",
            style = KptTheme.typography.titleSmall,
            color = amountColor,
        )
    }
}

@Composable
internal fun LoanSummaryGridSection(
    principalAmount: String,
    interestAmount: String,
    repaidAmount: String,
    outstandingAmount: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.medium),
        ) {
            PrincipalCard(principalAmount, Modifier.weight(1f))
            InterestCard(interestAmount, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.medium),
        ) {
            RepaidCard(repaidAmount, Modifier.weight(1f))
            OutstandingCard(outstandingAmount, Modifier.weight(1f))
        }
    }
}

@Composable
private fun PrincipalCard(amount: String, modifier: Modifier) {
    SummaryMetricCard(
        icon = MifosIcons.AttachMoney,
        iconBgColor = KptTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        iconTintColor = KptTheme.colorScheme.primary,
        title = stringResource(Res.string.feature_loan_principal),
        amount = amount,
        subtitle = stringResource(Res.string.feature_loan_disbursed_amount),
        modifier = modifier,
    )
}

@Composable
private fun InterestCard(amount: String, modifier: Modifier) {
    SummaryMetricCard(
        icon = MifosIcons.Percent,
        iconBgColor = AppColors.customYellow.copy(alpha = 0.3f),
        iconTintColor = AppColors.customYellow,
        title = stringResource(Res.string.feature_loan_interest_charged),
        amount = amount,
        subtitle = stringResource(Res.string.feature_loan_total_interest),
        modifier = modifier,
    )
}

@Composable
private fun RepaidCard(amount: String, modifier: Modifier) {
    SummaryMetricCard(
        icon = MifosIcons.Check,
        iconBgColor = AppColors.customEnable.copy(alpha = 0.15f),
        iconTintColor = AppColors.customEnable,
        title = stringResource(Res.string.feature_loan_repaid),
        amount = amount,
        subtitle = stringResource(Res.string.feature_loan_total_payment_received),
        modifier = modifier,
    )
}

@Composable
private fun OutstandingCard(amount: String, modifier: Modifier) {
    SummaryMetricCard(
        icon = MifosIcons.AccountBalanceWallet,
        iconBgColor = KptTheme.colorScheme.errorContainer,
        iconTintColor = KptTheme.colorScheme.error,
        title = stringResource(Res.string.feature_loan_outstanding),
        amount = amount,
        subtitle = stringResource(Res.string.feature_loan_remaining_balance),
        modifier = modifier,
    )
}

@Composable
private fun SummaryMetricCard(
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    title: String,
    amount: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                BorderStroke(
                    DesignToken.strokes.dpPoint5,
                    KptTheme.colorScheme.outlineVariant,
                ),
                DesignToken.shapes.medium,
            )
            .padding(DesignToken.padding.medium),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                modifier = Modifier
                    .size(DesignToken.sizes.iconAverage)
                    .background(
                        color = iconBgColor,
                        shape = DesignToken.shapes.circle,
                    )
                    .padding(DesignToken.padding.extraSmall),
                tint = iconTintColor,
                contentDescription = null,
            )

            Spacer(modifier = Modifier.width(DesignToken.padding.extraExtraSmall))

            Text(
                text = title.uppercase(),
                style = KptTheme.typography.labelMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.padding.small))

        Text(
            text = amount,
            style = MifosTypography.titleMediumEmphasized,
            color = KptTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(DesignToken.padding.extraSmall))

        Text(
            text = subtitle,
            style = KptTheme.typography.labelMedium,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun RepaymentProgressCard(
    data: RepaymentProgressData,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(Res.string.feature_loan_repayment_progress),
        style = MifosTypography.titleMediumEmphasized,
        color = KptTheme.colorScheme.onBackground,
    )

    Spacer(modifier = Modifier.height(DesignToken.padding.extraSmall))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                BorderStroke(DesignToken.strokes.dpPoint5, KptTheme.colorScheme.outlineVariant),
                DesignToken.shapes.medium,
            )
            .padding(DesignToken.padding.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        MifosDonutGraph(
            segments = listOf(
                DonutSegment(
                    label = stringResource(data.primaryLabel),
                    valueLabel = data.primaryPercentText,
                    value = data.primaryPercent,
                    color = AppColors.chartPrimary,
                ),
                DonutSegment(
                    label = stringResource(data.secondaryLabel),
                    valueLabel = data.secondaryPercentText,
                    value = data.secondaryPercent,
                    color = AppColors.chartSecondary,
                ),
            ),
            backgroundColor = AppColors.chartSecondary,
            showLegends = true,
        )
    }
}

@Composable
internal fun LoanTimelineCard(
    steps: List<TimelineStep>,
    modifier: Modifier = Modifier,
) {
    if (steps.isEmpty()) return

    Text(
        text = stringResource(Res.string.feature_loan_timeline),
        style = MifosTypography.titleMediumEmphasized,
        color = KptTheme.colorScheme.onBackground,
    )

    Spacer(modifier = Modifier.height(DesignToken.padding.extraSmall))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                BorderStroke(DesignToken.strokes.dpPoint5, KptTheme.colorScheme.outlineVariant),
                DesignToken.shapes.medium,
            )
            .padding(DesignToken.padding.medium),
    ) {
        steps.forEachIndexed { index, step ->
            TimelineStepRow(
                step = step,
                isLast = index == steps.size - 1,
            )
        }
    }
}

@Composable
private fun TimelineStepRow(
    step: TimelineStep,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        val strokeWidth = DesignToken.strokes.dp2
        Box(
            modifier = Modifier
                .width(DesignToken.sizes.iconMedium)
                .fillMaxHeight()
                .drawBehind {
                    if (!isLast) {
                        drawLine(
                            color = AppColors.borderColor.copy(alpha = 0.5f),
                            start = Offset(size.width / 2, size.width),
                            end = Offset(size.width / 2, size.height),
                            strokeWidth = strokeWidth.toPx(),
                        )
                    }
                },
            contentAlignment = Alignment.TopCenter,
        ) {
            TimelineIndicator(state = step.state)
        }

        Spacer(modifier = Modifier.width(DesignToken.padding.medium))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) DesignToken.padding.none else DesignToken.padding.largeIncreasedExtra),
        ) {
            Text(
                text = stringResource(step.title),
                style = KptTheme.typography.bodyMedium,
                color = if (step.state == TimelineStepState.CURRENT) KptTheme.colorScheme.primary else KptTheme.colorScheme.onSurface,
            )

            if (step.date.isNotEmpty()) {
                Spacer(modifier = Modifier.height(KptTheme.spacing.xs))
                Text(
                    text = step.date,
                    style = KptTheme.typography.bodySmall,
                    color = KptTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun TimelineIndicator(
    state: TimelineStepState,
    modifier: Modifier = Modifier,
) {
    when (state) {
        TimelineStepState.COMPLETED -> {
            Icon(
                imageVector = MifosIcons.Check,
                modifier = modifier
                    .size(DesignToken.sizes.iconMedium)
                    .background(
                        color = AppColors.customEnable,
                        shape = DesignToken.shapes.circle,
                    )
                    .padding(DesignToken.padding.extraSmall),
                tint = Color.White,
                contentDescription = null,
            )
        }

        TimelineStepState.CURRENT -> {
            Icon(
                imageVector = MifosIcons.Adjust,
                modifier = modifier
                    .size(DesignToken.sizes.iconMedium)
                    .background(
                        color = KptTheme.colorScheme.primary,
                        shape = DesignToken.shapes.circle,
                    )
                    .padding(DesignToken.padding.extraSmall),
                tint = Color.White,
                contentDescription = null,
            )
        }

        TimelineStepState.ERROR -> {
            Icon(
                imageVector = MifosIcons.Close,
                modifier = modifier
                    .size(DesignToken.sizes.iconMedium)
                    .background(
                        color = AppColors.lightRed,
                        shape = DesignToken.shapes.circle,
                    )
                    .padding(DesignToken.padding.extraSmall),
                tint = Color.White,
                contentDescription = null,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun LoanRepaymentGraphSection(
    periods: List<PeriodGraphValue>,
    isPrincipalVisible: Boolean,
    isInterestVisible: Boolean,
    onTogglePrincipal: () -> Unit,
    onToggleInterest: () -> Unit,
    modifier: Modifier = Modifier,
    isWaitingForDisbursal: Boolean = false,
) {
    if (periods.isEmpty()) return

    val visibleMax = periods.maxOfOrNull { period ->
        val p = if (isPrincipalVisible) period.principal.toFloat() else 0f
        val i = if (isInterestVisible) period.interest.toFloat() else 0f
        p + i
    } ?: 0f

    val absoluteMax = periods.maxOfOrNull { (it.principal + it.interest).toFloat() } ?: 100f

    val safeMax = if (visibleMax <= 0f) absoluteMax else visibleMax
    val graphMaxY = calculateNiceMax(safeMax)

    val legends = listOf(
        ChartLegendItem(
            title = Res.string.feature_loan_principal,
            color = AppColors.chartPrimary,
            isVisible = isPrincipalVisible,
            onToggle = onTogglePrincipal,
        ),
        ChartLegendItem(
            title = Res.string.feature_loan_interest,
            color = AppColors.chartSecondary,
            isVisible = isInterestVisible,
            onToggle = onToggleInterest,
        ),
    )

    val principalName = stringResource(Res.string.feature_loan_principal)
    val interestName = stringResource(Res.string.feature_loan_interest)
    val periodLabel = stringResource(Res.string.feature_loan_period)

    val bars = periods.mapIndexed { index, period ->
        val segments = mutableListOf<BarSegment>()

        if (period.principal > 0) {
            val pValue = if (isPrincipalVisible) period.principal.toFloat() else 0f
            segments.add(BarSegment(principalName, pValue, AppColors.chartPrimary))
        }
        if (period.interest > 0) {
            val iValue = if (isInterestVisible) period.interest.toFloat() else 0f
            segments.add(BarSegment(interestName, iValue, AppColors.chartSecondary))
        }

        StackedBarItem(
            xLabel = "$periodLabel ${index + 1}",
            segments = segments,
        )
    }

    Text(
        text = if (isWaitingForDisbursal) {
            stringResource(Res.string.feature_loan_projected_payment_schedule)
        } else {
            stringResource(Res.string.feature_loan_payment_schedule)
        },
        style = MifosTypography.titleMediumEmphasized,
        color = KptTheme.colorScheme.onBackground,
    )

    Spacer(modifier = Modifier.height(DesignToken.padding.extraSmall))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                BorderStroke(DesignToken.strokes.dpPoint5, KptTheme.colorScheme.outlineVariant),
                DesignToken.shapes.medium,
            )
            .padding(DesignToken.padding.medium),
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.small),
            maxItemsInEachRow = 3,
        ) {
            legends.forEach { legend ->
                LegendToggleItem(legend = legend)
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.padding.large))

        StackedBarChart(bars = bars, graphMaxY = graphMaxY)
    }
}

@Composable
private fun LoanStatusActionButtons(
    loanStatus: LoanStatus,
    loanId: Int,
    onAction: (LoanDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (loanStatus) {
        LoanStatus.PENDING_APPROVAL -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    DesignToken.padding.medium,
                ),
            ) {
                MifosOutlinedButton(
                    text = {
                        Text(text = stringResource(Res.string.feature_loan_account_reject))
                    },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onAction(
                            LoanDashboardAction.RejectLoan(loanId),
                        )
                    },
                )
                MifosButton(
                    text = {
                        Text(text = stringResource(Res.string.feature_loan_account_approve))
                    },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onAction(
                            LoanDashboardAction.ApproveLoan(loanId),
                        )
                    },
                )
            }
        }

        LoanStatus.WAITING_FOR_DISBURSAL -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    DesignToken.padding.medium,
                ),
            ) {
                MifosOutlinedButton(
                    text = {
                        Text(text = stringResource(Res.string.feature_loan_account_undo_approval))
                    },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onAction(
                            LoanDashboardAction.UndoApproval(loanId),
                        )
                    },
                )
                MifosButton(
                    text = {
                        Text(text = stringResource(Res.string.feature_loan_account_disburse))
                    },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onAction(
                            LoanDashboardAction.DisburseLoan(loanId),
                        )
                    },
                )
            }
        }

        LoanStatus.ACTIVE -> {
            MifosButton(
                text = {
                    Text(text = stringResource(Res.string.feature_loan_account_make_repayment))
                },
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onAction(LoanDashboardAction.MakeRepayment(loanId))
                },
            )
        }

        LoanStatus.OVERPAID -> {
            MifosButton(
                text = {
                    Text(text = stringResource(Res.string.feature_loan_account_credit_balance_refund))
                },
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onAction(LoanDashboardAction.CreditBalanceRefund(loanId))
                },
            )
        }

        else -> {}
    }
}

private class LoanDashboardPreviewProvider : PreviewParameterProvider<LoanDashboardState> {

    private val demoLoanDetails = LoanWithAssociationsEntity(
        id = 12345,
        accountNo = "000000001",
        clientName = "John Doe",
        loanProductName = "Agriculture Loan",
        loanOfficerName = "Jane Smith",
        principal = 10000.0,
        approvedPrincipal = 10000.0,
        status = LoanStatusEntity(active = true),
    )

    private val demoTransactions = listOf(
        RecentTransaction(
            type = Res.string.feature_loan_transaction_disbursement,
            amount = "10,000.00",
            date = "Mar 01, 2026",
            isIncreasingDebt = true,
        ),
        RecentTransaction(
            type = Res.string.feature_loan_transaction_repayment,
            amount = "550.00",
            date = "Apr 01, 2026",
            isIncreasingDebt = false,
        ),
        RecentTransaction(
            type = Res.string.feature_loan_transaction_charge_payment,
            amount = "50.00",
            date = "May 01, 2026",
            isIncreasingDebt = false,
        ),
    )

    private val demoGraphValues = listOf(
        PeriodGraphValue(principal = 500.0, interest = 50.0),
        PeriodGraphValue(principal = 500.0, interest = 45.0),
        PeriodGraphValue(principal = 500.0, interest = 40.0),
        PeriodGraphValue(principal = 500.0, interest = 35.0),
        PeriodGraphValue(principal = 500.0, interest = 50.0),
        PeriodGraphValue(principal = 500.0, interest = 45.0),
        PeriodGraphValue(principal = 500.0, interest = 40.0),
        PeriodGraphValue(principal = 500.0, interest = 35.0),
        PeriodGraphValue(principal = 500.0, interest = 50.0),
        PeriodGraphValue(principal = 500.0, interest = 45.0),
        PeriodGraphValue(principal = 500.0, interest = 40.0),
        PeriodGraphValue(principal = 500.0, interest = 35.0),
    )

    override val values: Sequence<LoanDashboardState>
        get() = sequenceOf(
            LoanDashboardState(
                viewState = LoanDashboardState.ViewState.Loading,
            ),

            LoanDashboardState(
                viewState = LoanDashboardState.ViewState.Error(Res.string.feature_loan_failed_to_load_loan),
            ),

            LoanDashboardState(
                viewState = LoanDashboardState.ViewState.Empty,
            ),

            LoanDashboardState(
                viewState = LoanDashboardState.ViewState.Success,
                loanStatus = LoanStatus.PENDING_APPROVAL,
                heroLabel = Res.string.feature_loan_requested_amount,
                heroValue = "$10,000.00",
                nextRepaymentDueDate = "",
                nextRepaymentAmount = "",
                loanDetails = demoLoanDetails.copy(
                    status = LoanStatusEntity(pendingApproval = true),
                ),
                recentTransactions = emptyList(),
                periodsGraphValues = emptyList(),
            ),

            LoanDashboardState(
                viewState = LoanDashboardState.ViewState.Success,
                loanStatus = LoanStatus.WAITING_FOR_DISBURSAL,
                heroLabel = Res.string.feature_loan_approved_amount,
                heroValue = "$10,000.00",
                nextRepaymentDueDate = "01 Apr 2026",
                nextRepaymentAmount = "$550.00",
                loanDetails = demoLoanDetails.copy(
                    status = LoanStatusEntity(waitingForDisbursal = true),
                ),
                recentTransactions = emptyList(),
                periodsGraphValues = demoGraphValues,
            ),

            LoanDashboardState(
                viewState = LoanDashboardState.ViewState.Success,
                loanStatus = LoanStatus.ACTIVE,
                heroLabel = Res.string.feature_loan_total_outstanding,
                heroValue = "$5,000.00",
                nextRepaymentDueDate = "15 Mar 2026",
                nextRepaymentAmount = "$550.00",
                loanDetails = demoLoanDetails,
                recentTransactions = demoTransactions,
                periodsGraphValues = demoGraphValues,
                isGraphInterestVisible = true,
                isGraphPrincipalVisible = true,
            ),

            LoanDashboardState(
                viewState = LoanDashboardState.ViewState.Success,
                loanStatus = LoanStatus.OVERPAID,
                heroLabel = Res.string.feature_loan_surplus_balance,
                heroValue = "$150.00",
                nextRepaymentDueDate = "",
                nextRepaymentAmount = "",
                loanDetails = demoLoanDetails.copy(
                    status = LoanStatusEntity(overpaid = true),
                    totalOverpaid = 150.0,
                ),
                recentTransactions = demoTransactions,
                periodsGraphValues = emptyList(),
            ),

            LoanDashboardState(
                viewState = LoanDashboardState.ViewState.Success,
                loanStatus = LoanStatus.CLOSED_OBLIGATIONS_MET,
                heroLabel = Res.string.feature_loan_total_repaid,
                heroValue = "$10,000.00",
                nextRepaymentDueDate = "",
                nextRepaymentAmount = "",
                loanDetails = demoLoanDetails.copy(
                    status = LoanStatusEntity(closedObligationsMet = true),
                ),
                recentTransactions = demoTransactions,
                periodsGraphValues = demoGraphValues,
            ),

            LoanDashboardState(
                viewState = LoanDashboardState.ViewState.Success,
                loanStatus = LoanStatus.CLOSED_WRITTEN_OFF,
                heroLabel = Res.string.feature_loan_written_off_amount,
                heroValue = "$3,500.00",
                nextRepaymentDueDate = "",
                nextRepaymentAmount = "",
                loanDetails = demoLoanDetails.copy(
                    status = LoanStatusEntity(closedWrittenOff = true),
                ),
                recentTransactions = demoTransactions,
                periodsGraphValues = demoGraphValues,
            ),

            LoanDashboardState(
                viewState = LoanDashboardState.ViewState.Success,
                loanStatus = LoanStatus.CLOSED_RESCHEDULED,
                heroLabel = Res.string.feature_loan_rescheduled_amount,
                heroValue = "$4,000.00",
                nextRepaymentDueDate = "",
                nextRepaymentAmount = "",
                loanDetails = demoLoanDetails.copy(
                    status = LoanStatusEntity(closedRescheduled = true),
                ),
                recentTransactions = demoTransactions,
                periodsGraphValues = demoGraphValues,
            ),

            LoanDashboardState(
                viewState = LoanDashboardState.ViewState.Success,
                loanStatus = LoanStatus.REJECTED,
                heroLabel = Res.string.feature_loan_requested_amount,
                heroValue = "$10,000.00",
                nextRepaymentDueDate = "",
                nextRepaymentAmount = "",
                loanDetails = demoLoanDetails.copy(
                    status = LoanStatusEntity(),
                ),
                recentTransactions = emptyList(),
                periodsGraphValues = emptyList(),
            ),
        )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
private fun PreviewLoanDashboardScreen(
    @PreviewParameter(LoanDashboardPreviewProvider::class) state: LoanDashboardState,
) {
    MifosTheme {
        LoanDashboardScreenContent(
            state = state,
            onAction = { },
        )
    }
}
