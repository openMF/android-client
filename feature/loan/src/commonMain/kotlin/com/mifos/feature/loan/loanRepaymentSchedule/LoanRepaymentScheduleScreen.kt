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
import androidclient.feature.loan.generated.resources.feature_loan_account_number
import androidclient.feature.loan.generated.resources.feature_loan_amount_and_balance
import androidclient.feature.loan.generated.resources.feature_loan_client_name_label
import androidclient.feature.loan.generated.resources.feature_loan_disbursed_date
import androidclient.feature.loan.generated.resources.feature_loan_export_pdf_error
import androidclient.feature.loan.generated.resources.feature_loan_export_pdf_error_title
import androidclient.feature.loan.generated.resources.feature_loan_export_to_pdf
import androidclient.feature.loan.generated.resources.feature_loan_installment_totals
import androidclient.feature.loan.generated.resources.feature_loan_loan_repayment_schedule
import androidclient.feature.loan.generated.resources.feature_loan_paid_label
import androidclient.feature.loan.generated.resources.feature_loan_period_details
import androidclient.feature.loan.generated.resources.feature_loan_repayment_schedule_pdf_title
import androidclient.feature.loan.generated.resources.feature_loan_table_header_date
import androidclient.feature.loan.generated.resources.feature_loan_table_header_days
import androidclient.feature.loan.generated.resources.feature_loan_table_header_due
import androidclient.feature.loan.generated.resources.feature_loan_table_header_fees
import androidclient.feature.loan.generated.resources.feature_loan_table_header_in_advance
import androidclient.feature.loan.generated.resources.feature_loan_table_header_interest
import androidclient.feature.loan.generated.resources.feature_loan_table_header_late
import androidclient.feature.loan.generated.resources.feature_loan_table_header_loan_balance
import androidclient.feature.loan.generated.resources.feature_loan_table_header_number
import androidclient.feature.loan.generated.resources.feature_loan_table_header_outstanding
import androidclient.feature.loan.generated.resources.feature_loan_table_header_paid
import androidclient.feature.loan.generated.resources.feature_loan_table_header_paid_date
import androidclient.feature.loan.generated.resources.feature_loan_table_header_penalties
import androidclient.feature.loan.generated.resources.feature_loan_table_header_principal_due
import androidclient.feature.loan.generated.resources.feature_loan_table_total
import androidclient.feature.loan.generated.resources.feature_loan_total_cost_of_loan
import androidclient.feature.loan.generated.resources.feature_loan_total_label
import androidclient.feature.loan.generated.resources.principal_paid_off
import androidclient.feature.loan.generated.resources.total_installments
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DataState
import com.mifos.core.designsystem.component.BasicDialogState
import com.mifos.core.designsystem.component.MifosBasicDialog
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTableRow
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import com.mifos.core.ui.components.MifosDetailsCard
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.pdf.Orientation
import com.mifos.core.ui.util.pdf.PageConfig
import com.mifos.core.ui.util.pdf.PageSize
import com.mifos.core.ui.util.pdf.rememberPdfGenerator
import com.mifos.feature.loan.loanRepaymentSchedule.pdf.RepaymentScheduleHtmlGenerator
import com.mifos.feature.loan.loanRepaymentSchedule.pdf.RepaymentSchedulePdfStrings
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun LoanRepaymentScheduleScreen(
    viewModel: LoanRepaymentScheduleViewModel = koinViewModel(),
    navigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val pdfGenerator = rememberPdfGenerator()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                LoanRepaymentScheduleEvent.NavigateBack -> navigateBack()
                LoanRepaymentScheduleEvent.ExportPdf -> {
                    state.repaymentScheduleTableData?.let { data ->
                        coroutineScope.launch {
                            try {
                                val pdfStrings = createPdfStrings()
                                val htmlGenerator = RepaymentScheduleHtmlGenerator(data, pdfStrings)
                                val htmlContent = htmlGenerator.generateHtml()
                                val fileName = "repayment_schedule_${data.accountNo}"
                                val pageConfig = PageConfig(
                                    size = PageSize.A4,
                                    orientation = Orientation.LANDSCAPE,
                                    marginMm = 8,
                                )
                                pdfGenerator.generateAndSharePdf(htmlContent, fileName, pageConfig)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                viewModel.trySendAction(
                                    LoanRepaymentScheduleAction.PdfExportError(
                                        title = getString(Res.string.feature_loan_export_pdf_error_title),
                                        message = getString(Res.string.feature_loan_export_pdf_error),
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    LoanRepaymentScheduleScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
    )

    LoanRepaymentScheduleDialogs(
        dialogState = state.dialogState,
        onAction = viewModel::trySendAction,
    )
}

@Composable
internal fun LoanRepaymentScheduleScreenContent(
    state: LoanRepaymentScheduleState,
    onAction: (LoanRepaymentScheduleAction) -> Unit,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_loan_loan_repayment_schedule),
        snackbarHostState = snackbarHostState,
        onBackPressed = { onAction(LoanRepaymentScheduleAction.OnNavigateBack) },
        actions = {
            IconButton(onClick = { onAction(LoanRepaymentScheduleAction.ExportToPdf) }) {
                Icon(
                    imageVector = MifosIcons.Export,
                    contentDescription = stringResource(Res.string.feature_loan_export_to_pdf),
                )
            }
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (state.dataState) {
                is DataState.Error -> {
                    MifosSweetError(
                        message = state.dataState.message,
                        onclick = { onAction(LoanRepaymentScheduleAction.Retry) },
                    )
                }

                is DataState.Success<LoanWithAssociations> -> {
                    state.repaymentScheduleTableData?.let { data ->
                        LoanRepaymentScheduleContent(
                            tableData = data,
                            basicDetails = state.basicDetails,
                        )
                    }
                }

                DataState.Loading -> {
                    MifosProgressIndicator()
                }
            }
        }
    }
}

@Composable
internal fun LoanRepaymentScheduleDialogs(
    dialogState: LoanRepaymentScheduleState.DialogState?,
    onAction: (LoanRepaymentScheduleAction) -> Unit,
) {
    when (dialogState) {
        is LoanRepaymentScheduleState.DialogState.Error -> {
            MifosBasicDialog(
                visibilityState = BasicDialogState.Shown(
                    message = dialogState.message,
                    title = dialogState.title,
                ),
                onDismissRequest = { onAction(LoanRepaymentScheduleAction.DismissErrorDialog) },
            )
        }
        null -> Unit
    }
}

private suspend fun createPdfStrings(): RepaymentSchedulePdfStrings {
    return RepaymentSchedulePdfStrings(
        title = getString(Res.string.feature_loan_repayment_schedule_pdf_title),
        clientNameLabel = getString(Res.string.feature_loan_client_name_label),
        accountNumberLabel = getString(Res.string.feature_loan_account_number),
        productNameLabel = getString(Res.string.feature_loan_loan_repayment_schedule),
        disbursementDateLabel = getString(Res.string.feature_loan_disbursed_date),
        installmentsLabel = getString(Res.string.total_installments),
        paidLabel = getString(Res.string.feature_loan_paid_label),
        totalLabel = getString(Res.string.feature_loan_total_label),
        principalPaidLabel = getString(Res.string.principal_paid_off),
        periodDetailsHeader = getString(Res.string.feature_loan_period_details),
        loanAmountBalanceHeader = getString(Res.string.feature_loan_amount_and_balance),
        totalCostLoanHeader = getString(Res.string.feature_loan_total_cost_of_loan),
        installmentTotalsHeader = getString(Res.string.feature_loan_installment_totals),
        hNo = getString(Res.string.feature_loan_table_header_number),
        hDays = getString(Res.string.feature_loan_table_header_days),
        hDate = getString(Res.string.feature_loan_table_header_date),
        hPaidDate = getString(Res.string.feature_loan_table_header_paid_date),
        hBalance = getString(Res.string.feature_loan_table_header_loan_balance),
        hPrincipal = getString(Res.string.feature_loan_table_header_principal_due),
        hInterest = getString(Res.string.feature_loan_table_header_interest),
        hFees = getString(Res.string.feature_loan_table_header_fees),
        hPenalties = getString(Res.string.feature_loan_table_header_penalties),
        hDue = getString(Res.string.feature_loan_table_header_due),
        hPaid = getString(Res.string.feature_loan_table_header_paid),
        hInAdvance = getString(Res.string.feature_loan_table_header_in_advance),
        hLate = getString(Res.string.feature_loan_table_header_late),
        hOutstanding = getString(Res.string.feature_loan_table_header_outstanding),
    )
}

@Composable
private fun LoanRepaymentScheduleContent(
    tableData: LoanRepaymentScheduleState.RepaymentScheduleTableData,
    basicDetails: Map<String, String?>,
) {
    val scrollState = rememberScrollState()

    val smallWidth = DesignToken.sizes.tableCellWidthSmall
    val mediumWidth = DesignToken.sizes.tableCellWidthMedium
    val largeWidth = DesignToken.sizes.tableCellWidthLarge

    val columnWidths = remember(smallWidth, mediumWidth, largeWidth) {
        listOf(
            smallWidth,
            smallWidth,
            mediumWidth,
            largeWidth,
            mediumWidth,
            mediumWidth,
            mediumWidth,
            mediumWidth,
            mediumWidth,
            mediumWidth,
            mediumWidth,
            mediumWidth,
            mediumWidth,
            mediumWidth,
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = DesignToken.padding.large),
        horizontalAlignment = Alignment.Start,
    ) {
        item {
            MifosDetailsCard(
                details = basicDetails,
                modifier = Modifier.padding(horizontal = DesignToken.padding.medium),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
        }

        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
            ) {
                RepaymentTableHeader(columnWidths)
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
            ) {
                DisbursementRow(tableData, columnWidths)

                tableData.periods.forEach { period ->
                    RepaymentPeriodRow(period, columnWidths)
                }

                RepaymentTableFooter(tableData.totals, columnWidths)
            }
        }
    }
}

@Composable
private fun RepaymentTableHeader(widths: List<Dp>) {
    val headers = listOf(
        Res.string.feature_loan_table_header_number,
        Res.string.feature_loan_table_header_days,
        Res.string.feature_loan_table_header_date,
        Res.string.feature_loan_table_header_paid_date,
        Res.string.feature_loan_table_header_loan_balance,
        Res.string.feature_loan_table_header_principal_due,
        Res.string.feature_loan_table_header_interest,
        Res.string.feature_loan_table_header_fees,
        Res.string.feature_loan_table_header_penalties,
        Res.string.feature_loan_table_header_due,
        Res.string.feature_loan_table_header_paid,
        Res.string.feature_loan_table_header_in_advance,
        Res.string.feature_loan_table_header_late,
        Res.string.feature_loan_table_header_outstanding,
    )

    MifosTableRow(
        cells = headers.map { headerRes ->
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = DesignToken.padding.small,
                            horizontal = DesignToken.padding.extraSmall,
                        ),
                ) {
                    Text(
                        text = stringResource(headerRes),
                        style = MifosTypography.labelMediumEmphasized,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
        widths = widths,
        backgroundColor = MaterialTheme.colorScheme.primary,
        edgeOffset = DesignToken.spacing.medium,
        cornerShape = DesignToken.shapes.topMedium,
    )
}

@Composable
private fun DisbursementRow(
    data: LoanRepaymentScheduleState.RepaymentScheduleTableData,
    widths: List<Dp>,
) {
    val cells: List<@Composable () -> Unit> = List(14) { index ->
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = DesignToken.padding.small,
                        horizontal = DesignToken.padding.extraSmall,
                    ),
            ) {
                val text = when (index) {
                    2 -> data.disbursementDate
                    4 -> data.loanAmount
                    else -> ""
                }
                Text(text = text, style = MifosTypography.labelMedium)
            }
        }
    }
    MifosTableRow(
        cells = cells,
        widths = widths,
        backgroundColor = Color.Transparent,
        edgeOffset = DesignToken.spacing.medium,
    )
}

@Composable
private fun RepaymentPeriodRow(
    period: LoanRepaymentScheduleState.RepaymentScheduleTableData.PeriodData,
    widths: List<Dp>,
) {
    val periodValues = listOf(
        period.number,
        period.days,
        period.dueDate,
        period.paidDate,
        period.balanceOfLoan,
        period.principalDue,
        period.interest,
        period.fees,
        period.penalties,
        period.due,
        period.paid,
        period.inAdvance,
        period.late,
        period.outstanding,
    )

    MifosTableRow(
        cells = periodValues.map { value ->
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = DesignToken.padding.small,
                            horizontal = DesignToken.padding.extraSmall,
                        ),
                ) {
                    Text(text = value, style = MifosTypography.labelMedium)
                }
            }
        },
        widths = widths,
        backgroundColor = Color.Transparent,
        edgeOffset = DesignToken.spacing.medium,
    )
}

@Composable
private fun RepaymentTableFooter(
    totals: LoanRepaymentScheduleState.RepaymentScheduleTableData.TotalsData,
    widths: List<Dp>,
) {
    val footerValues = listOf(
        stringResource(Res.string.feature_loan_table_total),
        "",
        "",
        "",
        "",
        totals.principalDue,
        totals.interest,
        totals.fees,
        totals.penalties,
        totals.due,
        totals.paid,
        totals.inAdvance,
        totals.late,
        totals.outstanding,
    )

    MifosTableRow(
        cells = footerValues.map { value ->
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = DesignToken.padding.small,
                            horizontal = DesignToken.padding.extraSmall,
                        ),
                ) {
                    Text(
                        text = value,
                        style = MifosTypography.labelMediumEmphasized,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
        widths = widths,
        backgroundColor = MaterialTheme.colorScheme.primary,
        edgeOffset = DesignToken.spacing.medium,
        cornerShape = DesignToken.shapes.bottomMedium,
    )
}

@Preview
@Composable
private fun PreviewLoanRepaymentSchedule() {
    MifosTheme {
        LoanRepaymentScheduleScreenContent(
            state = LoanRepaymentScheduleState(),
            onAction = {},
        )
    }
}
