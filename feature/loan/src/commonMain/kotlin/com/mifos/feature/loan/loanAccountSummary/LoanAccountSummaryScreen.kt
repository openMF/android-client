/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountSummary

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan
import androidclient.feature.loan.generated.resources.feature_loan_amount_paid
import androidclient.feature.loan.generated.resources.feature_loan_approve_loan
import androidclient.feature.loan.generated.resources.feature_loan_arrears
import androidclient.feature.loan.generated.resources.feature_loan_balance
import androidclient.feature.loan.generated.resources.feature_loan_closed
import androidclient.feature.loan.generated.resources.feature_loan_copy
import androidclient.feature.loan.generated.resources.feature_loan_date
import androidclient.feature.loan.generated.resources.feature_loan_disburse_loan
import androidclient.feature.loan.generated.resources.feature_loan_disbursed_date
import androidclient.feature.loan.generated.resources.feature_loan_documents
import androidclient.feature.loan.generated.resources.feature_loan_info
import androidclient.feature.loan.generated.resources.feature_loan_loan_account_summary
import androidclient.feature.loan.generated.resources.feature_loan_loan_amount_disbursed
import androidclient.feature.loan.generated.resources.feature_loan_loan_charges
import androidclient.feature.loan.generated.resources.feature_loan_loan_fees
import androidclient.feature.loan.generated.resources.feature_loan_loan_id
import androidclient.feature.loan.generated.resources.feature_loan_loan_id_copied
import androidclient.feature.loan.generated.resources.feature_loan_loan_in_arrears
import androidclient.feature.loan.generated.resources.feature_loan_loan_interest
import androidclient.feature.loan.generated.resources.feature_loan_loan_overview
import androidclient.feature.loan.generated.resources.feature_loan_loan_penalty
import androidclient.feature.loan.generated.resources.feature_loan_loan_principal
import androidclient.feature.loan.generated.resources.feature_loan_make_Repayment
import androidclient.feature.loan.generated.resources.feature_loan_outstanding_balance
import androidclient.feature.loan.generated.resources.feature_loan_repayment_schedule
import androidclient.feature.loan.generated.resources.feature_loan_staff
import androidclient.feature.loan.generated.resources.feature_loan_summary
import androidclient.feature.loan.generated.resources.feature_loan_total_loan
import androidclient.feature.loan.generated.resources.feature_loan_transactions
import androidclient.feature.loan.generated.resources.feature_loan_transfer_funds
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mifos.core.common.utils.Constants
import com.mifos.core.designsystem.component.MifosCard
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import com.mifos.room.entities.accounts.loans.LoanStatusEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.accounts.loans.LoansAccountSummaryEntity
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun LoanAccountSummaryScreenRoute(
    onNavigateBack: () -> Unit,
    onMoreInfoClicked: (String, loanId: Int) -> Unit,
    onTransactionsClicked: (loadId: Int) -> Unit,
    onRepaymentScheduleClicked: (loanId: Int) -> Unit,
    onDocumentsClicked: (loanId: Int) -> Unit,
    onChargesClicked: (loanId: Int) -> Unit,
    approveLoan: (loadId: Int, loanWithAssociations: LoanWithAssociationsEntity) -> Unit,
    disburseLoan: (loanId: Int) -> Unit,
    onRepaymentClick: (loanWithAssociations: LoanWithAssociationsEntity) -> Unit,
    navController: NavController,
    viewModel: LoanAccountSummaryViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val loanIdCopiedMessage = stringResource(Res.string.feature_loan_loan_id_copied)

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanAccountSummaryEvent.NavigateBack -> onNavigateBack()
            is LoanAccountSummaryEvent.NavigateToMoreInfo -> {
                onMoreInfoClicked(Constants.DATA_TABLE_NAME_LOANS, event.loanId)
            }

            is LoanAccountSummaryEvent.NavigateToTransactions -> {
                onTransactionsClicked(event.loanId)
            }

            is LoanAccountSummaryEvent.NavigateToRepaymentSchedule -> {
                onRepaymentScheduleClicked(event.loanId)
            }

            is LoanAccountSummaryEvent.NavigateToDocuments -> {
                onDocumentsClicked(event.loanId)
            }

            is LoanAccountSummaryEvent.NavigateToCharges -> {
                onChargesClicked(event.loanId)
            }

            is LoanAccountSummaryEvent.NavigateToApproveLoan -> {
                approveLoan(event.loanId, event.loanWithAssociations)
            }

            is LoanAccountSummaryEvent.NavigateToDisburseLoan -> {
                disburseLoan(event.loanId)
            }

            is LoanAccountSummaryEvent.NavigateToMakeRepayment -> {
                onRepaymentClick(event.loanWithAssociations)
            }

            is LoanAccountSummaryEvent.NavigateToLoanTransfer -> {}
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { state.showLoanIdCopiedMessage }
            .collect { showMessage ->
                if (showMessage) {
                    snackbarHostState.showSnackbar(message = loanIdCopiedMessage)
                    viewModel.trySendAction(LoanAccountSummaryAction.OnMessageShown)
                }
            }
    }

    LoanAccountSummaryScreen(
        state = state,
        onAction = viewModel::trySendAction,
        navController = navController,
        snackbarHostState = snackbarHostState,
    )

    LoanAccountSummaryDialog(
        state.dialogState,
        onAction = viewModel::trySendAction,
    )
}

@Composable
internal fun LoanAccountSummaryScreen(
    state: LoanAccountSummaryState,
    onAction: (LoanAccountSummaryAction) -> Unit,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
) {
    MifosScaffold(
        title = stringResource(Res.string.feature_loan_loan_account_summary),
        onBackPressed = { onAction(LoanAccountSummaryAction.NavigateBack) },
        snackbarHostState = snackbarHostState,
        actions = {
            IconButton(onClick = { onAction(LoanAccountSummaryAction.ToggleDropdown) }) {
                Icon(
                    imageVector = MifosIcons.MoreVert,
                    contentDescription = "More options",
                )
            }

            LoanSummaryDropdown(
                state = state,
                onAction = onAction,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(KptTheme.colorScheme.background),
        ) {
            MifosBreadcrumbNavBar(navController)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                state.loanWithAssociations?.let { loanWithAssociations ->
                    LoanAccountSummaryContent(
                        state = state,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoanAccountSummaryContent(
    state: LoanAccountSummaryState,
    onAction: (LoanAccountSummaryAction) -> Unit,
) {
    val loanWithAssociations = state.loanWithAssociations ?: return
    val actualDisbursementDate = state.actualDisbursementDate
    val scrollState = rememberScrollState()
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = DesignToken.padding.medium)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(DesignToken.padding.medium),
    ) {
        MifosCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(KptTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.mediumSmall),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = loanWithAssociations.clientName,
                    style = KptTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val statusDescription = when {
                        loanWithAssociations.status.active == true -> "Active"
                        loanWithAssociations.status.pendingApproval == true -> "Pending Approval"
                        loanWithAssociations.status.waitingForDisbursal == true -> "Waiting for Disbursal"
                        else -> "Closed"
                    }
                    Canvas(
                        modifier = Modifier
                            .size(DesignToken.sizes.iconMedium)
                            .semantics {
                                contentDescription = "Loan status: $statusDescription"
                            },
                        onDraw = {
                            drawCircle(
                                color = when {
                                    loanWithAssociations.status.active == true -> {
                                        AppColors.loanIndicatorActive
                                    }

                                    loanWithAssociations.status.pendingApproval == true -> {
                                        AppColors.loanIndicatorPending
                                    }

                                    loanWithAssociations.status.waitingForDisbursal == true -> {
                                        AppColors.loanIndicatorWaitingForDisbursal
                                    }

                                    else -> {
                                        AppColors.loanIndicatorOther
                                    }
                                },
                            )
                        },
                    )
                    Spacer(modifier = Modifier.width(DesignToken.spacing.mediumSmall))
                    Text(
                        text = loanWithAssociations.loanProductName,
                        style = MifosTypography.bodyLarge,
                        color = KptTheme.colorScheme.onSurface,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.feature_loan_loan_id) + loanWithAssociations.accountNo,
                        color = KptTheme.colorScheme.onSurfaceVariant,
                        style = MifosTypography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.width(KptTheme.spacing.xs))
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(loanWithAssociations.accountNo))
                            onAction(LoanAccountSummaryAction.OnLoanIdCopied)
                        },
                        modifier = Modifier.size(DesignToken.sizes.iconSmall),
                    ) {
                        Icon(
                            imageVector = MifosIcons.Copy,
                            contentDescription = stringResource(Res.string.feature_loan_copy),
                            modifier = Modifier.size(DesignToken.sizes.iconSmall),
                            tint = KptTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Row {
            InfoCard(
                titleText = stringResource(Res.string.feature_loan_total_loan),
                infoText = state.totalLoanFormat,
                modifier = Modifier.fillMaxWidth(0.5f),
            )
            Spacer(modifier = Modifier.width(DesignToken.spacing.medium))
            InfoCard(
                titleText = stringResource(Res.string.feature_loan_amount_paid),
                infoText = state.loanAmountPaid,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        InfoCard(
            titleText = stringResource(Res.string.feature_loan_outstanding_balance),
            infoText = state.outstandingAmount,
            modifier = Modifier.fillMaxWidth(),
        )

        MifosCard {
            Column(
                modifier = Modifier.padding(KptTheme.spacing.md),
            ) {
                Text(
                    text = stringResource(Res.string.feature_loan_loan_overview),
                    style = MifosTypography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = KptTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(DesignToken.spacing.medium))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = MifosIcons.Currency,
                        contentDescription = stringResource(Res.string.feature_loan_info),
                        modifier = Modifier.size(DesignToken.sizes.iconAverage),
                    )
                    LoanSummaryFarApartTextItem(
                        title = stringResource(Res.string.feature_loan_loan_amount_disbursed),
                        value = if (state.inflateLoanSummary) {
                            state.principalDisbursed
                        } else {
                            ""
                        },
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = MifosIcons.Calendar,
                        contentDescription = stringResource(Res.string.feature_loan_date),
                        modifier = Modifier.size(DesignToken.sizes.iconAverage),
                    )
                    LoanSummaryFarApartTextItem(
                        title = stringResource(Res.string.feature_loan_disbursed_date),
                        value = if (state.inflateLoanSummary) actualDisbursementDate else "",
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = MifosIcons.KeyboardArrowDown,
                        contentDescription = stringResource(Res.string.feature_loan_arrears),
                        modifier = Modifier.size(DesignToken.sizes.iconAverage),
                    )
                    LoanSummaryFarApartTextItem(
                        title = stringResource(Res.string.feature_loan_loan_in_arrears),
                        value = if (state.inflateLoanSummary) {
                            state.overdueAmount
                        } else {
                            ""
                        },
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = MifosIcons.Person,
                        contentDescription = stringResource(Res.string.feature_loan_info),
                        modifier = Modifier.size(DesignToken.sizes.iconAverage),
                    )
                    LoanSummaryFarApartTextItem(
                        title = stringResource(Res.string.feature_loan_staff),
                        value = loanWithAssociations.loanOfficerName,
                    )
                }
            }
        }

        LoanSummaryDataTable(state = state)

        val primaryAction = loanWithAssociations.status.getPrimaryAction()
        val buttonText = when (primaryAction) {
            LoanPrimaryAction.MAKE_REPAYMENT -> stringResource(Res.string.feature_loan_make_Repayment)
            LoanPrimaryAction.APPROVE_LOAN -> stringResource(Res.string.feature_loan_approve_loan)
            LoanPrimaryAction.DISBURSE_LOAN -> stringResource(Res.string.feature_loan_disburse_loan)
            LoanPrimaryAction.OVERPAID -> stringResource(Res.string.feature_loan_transfer_funds)
            LoanPrimaryAction.CLOSED -> stringResource(Res.string.feature_loan_closed)
        }

        Button(
            enabled = loanWithAssociations.status.isButtonActive(),
            modifier = Modifier
                .fillMaxWidth()
                .height(DesignToken.sizes.buttonHeightMedium),
            shape = KptTheme.shapes.small,
            onClick = {
                when (primaryAction) {
                    LoanPrimaryAction.MAKE_REPAYMENT -> onAction(LoanAccountSummaryAction.OnMakeRepayment)
                    LoanPrimaryAction.APPROVE_LOAN -> onAction(LoanAccountSummaryAction.OnApproveLoan)
                    LoanPrimaryAction.DISBURSE_LOAN -> onAction(LoanAccountSummaryAction.OnDisburseLoan)
                    LoanPrimaryAction.OVERPAID -> onAction(LoanAccountSummaryAction.NavigateToLoanTransfer)
                    LoanPrimaryAction.CLOSED -> { }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = KptTheme.colorScheme.primary),
        ) {
            Text(
                color = KptTheme.colorScheme.onPrimary,
                text = buttonText,
            )
        }
        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))
    }
}

@Composable
private fun LoanSummaryDataTable(
    state: LoanAccountSummaryState,
) {
    MifosCard {
        DataTableRow(
            summaryColumnTitle = stringResource(Res.string.feature_loan_summary),
            loanColumnValue = stringResource(Res.string.feature_loan),
            amountColumnValue = stringResource(Res.string.feature_loan_amount_paid),
            balanceColumnValue = stringResource(Res.string.feature_loan_balance),
            isHeader = true,
            color = KptTheme.colorScheme.primary.copy(
                alpha = 0.3f,
            ),
        )

        DataTableRow(
            summaryColumnTitle = stringResource(Res.string.feature_loan_loan_principal),
            loanColumnValue = state.principalDisbursed,
            amountColumnValue = state.principalPaid,
            balanceColumnValue = state.principalOutStanding,
        )

        HorizontalDivider(thickness = 0.5.dp)

        DataTableRow(
            summaryColumnTitle = stringResource(Res.string.feature_loan_loan_interest),
            loanColumnValue = state.interestCharged,
            amountColumnValue = state.interestPaid,
            balanceColumnValue = state.interestOutstanding,
        )

        HorizontalDivider(thickness = 0.5.dp)

        DataTableRow(
            summaryColumnTitle = stringResource(Res.string.feature_loan_loan_fees),
            loanColumnValue = state.feeChargesCharged,
            amountColumnValue = state.feeChargesPaid,
            balanceColumnValue = state.feeChargesOutstanding,
        )

        HorizontalDivider(thickness = 0.5.dp)

        DataTableRow(
            summaryColumnTitle = stringResource(Res.string.feature_loan_loan_penalty),
            loanColumnValue = state.penaltyChargesCharged,
            amountColumnValue = state.penaltyChargesPaid,
            balanceColumnValue = state.penaltyChargesOutstanding,
        )
    }
}

@Composable
private fun LoanSummaryFarApartTextItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(KptTheme.spacing.sm),
    ) {
        Text(
            style = KptTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            text = "$title:",
            color = KptTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.width(KptTheme.spacing.xs))

        Text(
            style = KptTheme.typography.bodyMedium,
            text = value,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InfoCard(
    titleText: String,
    infoText: String,
    modifier: Modifier,
) {
    MifosCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(KptTheme.spacing.md),
        ) {
            Text(
                text = titleText,
                style = MifosTypography.bodyLarge,
                color = KptTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))
            Text(
                text = infoText,
                style = MifosTypography.headlineSmallEmphasized,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DataTableRow(
    summaryColumnTitle: String,
    loanColumnValue: String,
    amountColumnValue: String,
    balanceColumnValue: String,
    isHeader: Boolean = false,
    color: Color = KptTheme.colorScheme.surface,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = summaryColumnTitle,
            modifier = Modifier
                .weight(1f)
                .padding(KptTheme.spacing.sm),
            style = KptTheme.typography.bodyMedium,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            color = KptTheme.colorScheme.onSurface,
        )

        Text(
            text = loanColumnValue,
            modifier = Modifier
                .weight(1f)
                .padding(KptTheme.spacing.sm),
            style = KptTheme.typography.bodyMedium,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.End,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = amountColumnValue,
            modifier = Modifier
                .weight(1f)
                .padding(KptTheme.spacing.sm),
            style = KptTheme.typography.bodyMedium,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.End,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = balanceColumnValue,
            modifier = Modifier
                .weight(1f)
                .padding(KptTheme.spacing.sm),
            style = KptTheme.typography.bodyMedium,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.End,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LoanAccountSummaryDialog(
    dialogState: LoanAccountSummaryState.DialogState?,
    onAction: (LoanAccountSummaryAction) -> Unit,
) {
    when (dialogState) {
        is LoanAccountSummaryState.DialogState.Error -> {
            MifosSweetError(
                message = dialogState.message,
                onclick = { onAction(LoanAccountSummaryAction.OnRetry) },
            )
        }

        LoanAccountSummaryState.DialogState.Loading -> {
            MifosProgressIndicator()
        }

        null -> Unit
    }
}

@Composable
private fun LoanSummaryDropdown(
    state: LoanAccountSummaryState,
    onAction: (LoanAccountSummaryAction) -> Unit,
) {
    DropdownMenu(
        expanded = state.openDropdown,
        onDismissRequest = { onAction(LoanAccountSummaryAction.ToggleDropdown) },
    ) {
        MifosMenuDropDownItem(
            option = Constants.DATA_TABLE_LOAN_NAME,
            onClick = {
                onAction(LoanAccountSummaryAction.DropdownAction(LoanSummaryDropDownAction.OnMoreInfoClick))
            },
        )
        MifosMenuDropDownItem(
            option = stringResource(Res.string.feature_loan_transactions),
            onClick = {
                onAction(LoanAccountSummaryAction.DropdownAction(LoanSummaryDropDownAction.OnTransactionsClick))
            },
        )
        MifosMenuDropDownItem(
            option = stringResource(Res.string.feature_loan_repayment_schedule),
            onClick = {
                onAction(LoanAccountSummaryAction.DropdownAction(LoanSummaryDropDownAction.OnRepaymentScheduleClick))
            },
        )
        MifosMenuDropDownItem(
            option = stringResource(Res.string.feature_loan_documents),
            onClick = {
                onAction(LoanAccountSummaryAction.DropdownAction(LoanSummaryDropDownAction.OnDocumentsClick))
            },
        )
        MifosMenuDropDownItem(
            option = stringResource(Res.string.feature_loan_loan_charges),
            onClick = {
                onAction(LoanAccountSummaryAction.DropdownAction(LoanSummaryDropDownAction.OnChargesClick))
            },
        )
    }
}

private fun LoanStatusEntity.isButtonActive(): Boolean {
    return active == true || pendingApproval == true || waitingForDisbursal == true || overpaid == true
}

private class LoanAccountSummaryPreviewProvider :
    PreviewParameterProvider<LoanAccountSummaryState> {
    private val demoSummary = LoansAccountSummaryEntity(
        loanId = 12345,
        principalDisbursed = 10000.0,
        principalPaid = 4000.0,
        principalWrittenOff = 0.0,
        principalOutstanding = 6000.0,
        principalOverdue = 500.0,
        interestCharged = 700.0,
        interestPaid = 300.0,
        interestWaived = 0.0,
        interestWrittenOff = 0.0,
        interestOutstanding = 200.0,
        interestOverdue = 50.0,
        feeChargesCharged = 200.0,
        feeChargesDueAtDisbursementCharged = 50.0,
        feeChargesPaid = 150.0,
        feeChargesWaived = 0.0,
        feeChargesWrittenOff = 0.0,
        feeChargesOutstanding = 50.0,
        feeChargesOverdue = 20.0,
        penaltyChargesCharged = 100.0,
        penaltyChargesPaid = 50.0,
        penaltyChargesWaived = 0.0,
        penaltyChargesWrittenOff = 0.0,
        penaltyChargesOutstanding = 50.0,
        penaltyChargesOverdue = 10.0,
        totalExpectedRepayment = 10700.0,
        totalRepayment = 4450.0,
        totalExpectedCostOfLoan = 750.0,
        totalCostOfLoan = 300.0,
        totalOutstanding = 6250.0,
        totalOverdue = 580.0,
        overdueSinceDate = listOf(2024, 6, 1),
    )

    override val values: Sequence<LoanAccountSummaryState>
        get() = sequenceOf(
            LoanAccountSummaryState(
                dialogState = LoanAccountSummaryState.DialogState.Loading,
            ),
            LoanAccountSummaryState(
                dialogState = LoanAccountSummaryState.DialogState.Error("Could not fetch summary"),
            ),
            LoanAccountSummaryState(
                loanWithAssociations = LoanWithAssociationsEntity(
                    accountNo = "90927493938",
                    status = LoanStatusEntity(
                        closedObligationsMet = true,
                    ),
                    clientName = "Pronay sarker",
                    loanOfficerName = "MR. Ching",
                    loanProductName = "Group Loan",
                    summary = demoSummary,
                ),
                dialogState = null,
            ),
            LoanAccountSummaryState(
                loanWithAssociations = LoanWithAssociationsEntity(
                    accountNo = "12345678901",
                    status = LoanStatusEntity(
                        active = true,
                    ),
                    clientName = "John Doe",
                    loanOfficerName = "Jane Smith",
                    loanProductName = "Personal Loan",
                    summary = demoSummary,
                ),
                dialogState = null,
                inflateLoanSummary = true,
                actualDisbursementDate = "01 June 2024",
                totalLoanFormat = "$10,700.00",
                loanAmountPaid = "$4,450.00",
                outstandingAmount = "$6,250.00",
                overdueAmount = "$580.00",
                principalDisbursed = "$10,000.00",
                principalPaid = "$4,000.00",
                principalOutStanding = "$6,000.00",
                interestCharged = "$700.00",
                interestPaid = "$300.00",
                interestOutstanding = "$200.00",
                feeChargesCharged = "$200.00",
                feeChargesPaid = "$150.00",
                feeChargesOutstanding = "$50.00",
                penaltyChargesCharged = "$100.00",
                penaltyChargesPaid = "$50.00",
                penaltyChargesOutstanding = "$50.00",
            ),
        )
}

@Composable
@Preview
private fun PreviewLoanAccountSummary(
    @PreviewParameter(LoanAccountSummaryPreviewProvider::class) state: LoanAccountSummaryState,
) {
    MifosTheme {
        LoanAccountSummaryScreen(
            state = state,
            onAction = { },
            navController = rememberNavController(),
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
