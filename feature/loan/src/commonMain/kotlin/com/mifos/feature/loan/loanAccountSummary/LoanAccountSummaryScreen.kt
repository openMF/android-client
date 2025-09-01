/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountSummary

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan
import androidclient.feature.loan.generated.resources.feature_loan_amount_paid
import androidclient.feature.loan.generated.resources.feature_loan_approve_loan
import androidclient.feature.loan.generated.resources.feature_loan_balance
import androidclient.feature.loan.generated.resources.feature_loan_closed
import androidclient.feature.loan.generated.resources.feature_loan_disburse_loan
import androidclient.feature.loan.generated.resources.feature_loan_disbursed_date
import androidclient.feature.loan.generated.resources.feature_loan_documents
import androidclient.feature.loan.generated.resources.feature_loan_loan_account_summary
import androidclient.feature.loan.generated.resources.feature_loan_loan_amount_disbursed
import androidclient.feature.loan.generated.resources.feature_loan_loan_charges
import androidclient.feature.loan.generated.resources.feature_loan_loan_fees
import androidclient.feature.loan.generated.resources.feature_loan_loan_in_arrears
import androidclient.feature.loan.generated.resources.feature_loan_loan_interest
import androidclient.feature.loan.generated.resources.feature_loan_loan_penalty
import androidclient.feature.loan.generated.resources.feature_loan_loan_principal
import androidclient.feature.loan.generated.resources.feature_loan_loan_rejected_message
import androidclient.feature.loan.generated.resources.feature_loan_make_Repayment
import androidclient.feature.loan.generated.resources.feature_loan_repayment_schedule
import androidclient.feature.loan.generated.resources.feature_loan_staff
import androidclient.feature.loan.generated.resources.feature_loan_summary
import androidclient.feature.loan.generated.resources.feature_loan_total
import androidclient.feature.loan.generated.resources.feature_loan_transactions
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.room.entities.accounts.loans.LoanStatusEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.accounts.loans.LoansAccountSummaryEntity
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LoanAccountSummaryScreen(
    navigateBack: () -> Unit,
    onMoreInfoClicked: (String, loanId: Int) -> Unit,
    onTransactionsClicked: (loadId: Int) -> Unit,
    onRepaymentScheduleClicked: (loanId: Int) -> Unit,
    onDocumentsClicked: (loanId: Int) -> Unit,
    onChargesClicked: (loanId: Int) -> Unit,
    approveLoan: (loadId: Int, loanWithAssociations: LoanWithAssociationsEntity) -> Unit,
    disburseLoan: (loanId: Int) -> Unit,
    onRepaymentClick: (loanWithAssociations: LoanWithAssociationsEntity) -> Unit,
    viewModel: LoanAccountSummaryViewModel = koinViewModel(),
) {
    val uiState by viewModel.loanAccountSummaryUiState.collectAsStateWithLifecycle()
    val loanAccountNumber = viewModel.loanAccountNumber

    LaunchedEffect(key1 = Unit) {
        viewModel.loadLoanById()
    }

    LoanAccountSummaryScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = { viewModel.loadLoanById() },
        onMoreInfoClicked = {
            onMoreInfoClicked.invoke(
                Constants.DATA_TABLE_NAME_LOANS,
                loanAccountNumber,
            )
        },
        onTransactionsClicked = { onTransactionsClicked.invoke(loanAccountNumber) },
        onRepaymentScheduleClicked = { onRepaymentScheduleClicked.invoke(loanAccountNumber) },
        onDocumentsClicked = { onDocumentsClicked(loanAccountNumber) },
        onChargesClicked = { onChargesClicked(loanAccountNumber) },
        approveLoan = { approveLoan(loanAccountNumber, it) },
        disburseLoan = { disburseLoan(loanAccountNumber) },
        makeRepayment = onRepaymentClick,
    )
}

@Composable
internal fun LoanAccountSummaryScreen(
    uiState: LoanAccountSummaryUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
    onMoreInfoClicked: () -> Unit,
    onTransactionsClicked: () -> Unit,
    onRepaymentScheduleClicked: () -> Unit,
    onDocumentsClicked: () -> Unit,
    onChargesClicked: () -> Unit,
    approveLoan: (loanWithAssociations: LoanWithAssociationsEntity) -> Unit,
    disburseLoan: () -> Unit,
    makeRepayment: (loanWithAssociations: LoanWithAssociationsEntity) -> Unit,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    var openDropdown by rememberSaveable {
        mutableStateOf(false)
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_loan_loan_account_summary),
        onBackPressed = navigateBack,
        snackbarHostState = snackbarHostState,
        actions = {
            IconButton(onClick = { openDropdown = !openDropdown }) {
                Icon(
                    imageVector = MifosIcons.MoreVert,
                    contentDescription = null,
                )
            }
            if (openDropdown) {
                DropdownMenu(
                    expanded = openDropdown,
                    onDismissRequest = { openDropdown = false },
                ) {
                    MifosMenuDropDownItem(
                        option = Constants.DATA_TABLE_LOAN_NAME,
                        onClick = {
                            openDropdown = false
                            onMoreInfoClicked.invoke()
                        },
                    )
                    MifosMenuDropDownItem(
                        option = stringResource(Res.string.feature_loan_transactions),
                        onClick = {
                            openDropdown = false
                            onTransactionsClicked.invoke()
                        },
                    )
                    MifosMenuDropDownItem(
                        option = stringResource(Res.string.feature_loan_repayment_schedule),
                        onClick = {
                            openDropdown = false
                            onRepaymentScheduleClicked.invoke()
                        },
                    )
                    MifosMenuDropDownItem(
                        option = stringResource(Res.string.feature_loan_documents),
                        onClick = {
                            openDropdown = false
                            onDocumentsClicked.invoke()
                        },
                    )
                    MifosMenuDropDownItem(
                        option = stringResource(Res.string.feature_loan_loan_charges),
                        onClick = {
                            openDropdown = false
                            onChargesClicked.invoke()
                        },
                    )
                }
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            when (uiState) {
                is LoanAccountSummaryUiState.ShowFetchingError -> {
                    MifosSweetError(
                        message = uiState.message,
                        onclick = onRetry,
                    )
                }

                is LoanAccountSummaryUiState.ShowLoanById -> {
                    val loanWithAssociations = uiState.loanWithAssociations
                    LoanAccountSummaryContent(
                        loanWithAssociations = loanWithAssociations,
                        makeRepayment = { makeRepayment.invoke(loanWithAssociations) },
                        approveLoan = { approveLoan.invoke(loanWithAssociations) },
                        disburseLoan = disburseLoan,
                        snackbarHostState = snackbarHostState,
                    )
                }

                LoanAccountSummaryUiState.ShowProgressbar -> {
                    MifosCircularProgress()
                }
            }
        }
    }
}

@Composable
private fun LoanAccountSummaryContent(
    loanWithAssociations: LoanWithAssociationsEntity,
    makeRepayment: () -> Unit,
    approveLoan: () -> Unit,
    disburseLoan: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val inflateLoanSummary = getInflateLoanSummaryValue(status = loanWithAssociations.status)
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val message = stringResource(Res.string.feature_loan_loan_rejected_message)
    fun getActualDisbursementDateInStringFormat(): String {
        try {
            return loanWithAssociations.timeline.actualDisbursementDate?.let {
                DateHelper.getDateAsString(it as List<Int>)
            } ?: ""
        } catch (exception: IndexOutOfBoundsException) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                )
            }
            return ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            text = loanWithAssociations.clientName,
            style = MaterialTheme.typography.bodyLarge,
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Canvas(
                modifier = Modifier
                    .size(22.dp)
                    .padding(top = 4.dp, end = 4.dp),
                contentDescription = "",
                onDraw = {
                    drawRect(
                        color = when {
                            loanWithAssociations.status.active == true -> {
                                Color.Green
                            }

                            loanWithAssociations.status.pendingApproval == true -> {
                                Color.Yellow
                            }

                            loanWithAssociations.status.waitingForDisbursal == true -> {
                                Color.Blue
                            }

                            else -> {
                                Color.Black
                            }
                        },
                    )
                },
            )

            LoanSummaryFarApartTextItem(
                title = loanWithAssociations.loanProductName,
                value = "#" + loanWithAssociations.accountNo,
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
        )

        LoanSummaryFarApartTextItem(
            title = stringResource(Res.string.feature_loan_loan_amount_disbursed),
            value = if (inflateLoanSummary) {
                loanWithAssociations.summary.principalDisbursed?.toString()
                    ?: ""
            } else {
                ""
            },
        )

        LoanSummaryFarApartTextItem(
            title = stringResource(Res.string.feature_loan_disbursed_date),
            value = if (inflateLoanSummary) getActualDisbursementDateInStringFormat() else "",
        )

        LoanSummaryFarApartTextItem(
            title = stringResource(Res.string.feature_loan_loan_in_arrears),
            value = if (inflateLoanSummary) {
                loanWithAssociations.summary.totalOverdue?.toString()
                    ?: ""
            } else {
                ""
            },
        )

        LoanSummaryFarApartTextItem(
            title = stringResource(Res.string.feature_loan_staff),
            value = loanWithAssociations.loanOfficerName,
        )

        Spacer(modifier = Modifier.height(8.dp))

        LoanSummaryDataTable(
            loanSummary = loanWithAssociations.summary,
            inflateLoanSummary = inflateLoanSummary,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            enabled = getButtonActiveStatus(loanWithAssociations.status),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .height(45.dp),
            onClick = when {
                loanWithAssociations.status.active == true -> {
                    { makeRepayment.invoke() }
                }

                loanWithAssociations.status.pendingApproval == true -> {
                    { approveLoan.invoke() }
                }

                loanWithAssociations.status.waitingForDisbursal == true -> {
                    { disburseLoan.invoke() }
                }

                loanWithAssociations.status.closedObligationsMet == true -> {
                    { Logger.e("LoanAccountSummary") { "TRANSACTION ACTION NOT SET" } }
                }

                else -> {
                    { Logger.e("LoanAccountSummary") { "TRANSACTION ACTION NOT SET" } }
                }
            },
        ) {
            Text(
                color = MaterialTheme.colorScheme.background,
                text = getButtonText(loanWithAssociations.status),
            )
        }
    }
}

@Composable
private fun LoanSummaryDataTable(loanSummary: LoansAccountSummaryEntity, inflateLoanSummary: Boolean) {
    // dataTable should be empty if [inflateLoanSummary] is false
    val summary = if (inflateLoanSummary) loanSummary else null
    Column {
        DataTableRow(
            summaryColumnTitle = stringResource(Res.string.feature_loan_summary),
            loanColumnValue = stringResource(Res.string.feature_loan),
            amountColumnValue = stringResource(Res.string.feature_loan_amount_paid),
            balanceColumnValue = stringResource(Res.string.feature_loan_balance),
            isHeader = true,
            color = Color.Blue.copy(alpha = .3f),
        )

        DataTableRow(
            summaryColumnTitle = stringResource(Res.string.feature_loan_loan_principal),
            loanColumnValue = summary?.principalDisbursed?.toString() ?: "",
            amountColumnValue = summary?.principalPaid?.toString() ?: "",
            balanceColumnValue = summary?.principalOutstanding?.toString() ?: "",
        )

        DataTableRow(
            summaryColumnTitle = stringResource(Res.string.feature_loan_loan_interest),
            loanColumnValue = summary?.interestCharged?.toString() ?: "",
            amountColumnValue = summary?.interestPaid?.toString() ?: "",
            balanceColumnValue = summary?.interestOutstanding?.toString() ?: "",
            color = Color.Blue.copy(alpha = .3f),
        )

        DataTableRow(
            summaryColumnTitle = stringResource(Res.string.feature_loan_loan_fees),
            loanColumnValue = summary?.feeChargesCharged?.toString() ?: "",
            amountColumnValue = summary?.feeChargesPaid?.toString() ?: "",
            balanceColumnValue = summary?.feeChargesOutstanding?.toString() ?: "",
        )

        DataTableRow(
            summaryColumnTitle = stringResource(Res.string.feature_loan_loan_penalty),
            loanColumnValue = summary?.penaltyChargesCharged?.toString() ?: "",
            amountColumnValue = summary?.penaltyChargesPaid?.toString() ?: "",
            balanceColumnValue = summary?.penaltyChargesOutstanding?.toString() ?: "",
            color = Color.Blue.copy(alpha = .3f),
        )

        DataTableRow(
            summaryColumnTitle = stringResource(Res.string.feature_loan_total),
            loanColumnValue = summary?.totalExpectedRepayment?.toString() ?: "",
            amountColumnValue = summary?.totalRepayment?.toString() ?: "",
            balanceColumnValue = summary?.totalOutstanding?.toString() ?: "",
        )
    }
}

@Composable
private fun LoanSummaryFarApartTextItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = title,
            color = Color.Black,
        )

        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = value,
            color = Color.DarkGray,
        )
    }
}

@Composable
private fun DataTableRow(
    summaryColumnTitle: String,
    loanColumnValue: String,
    amountColumnValue: String,
    balanceColumnValue: String,
    isHeader: Boolean = false,
    color: Color = Color.White,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color),
    ) {
        Text(
            text = summaryColumnTitle,
            modifier = Modifier
                .weight(2.5f)
                .padding(vertical = 6.dp)
                .padding(start = 2.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        )

        Text(
            text = loanColumnValue,
            modifier = Modifier
                .weight(2.8f)
                .padding(horizontal = 6.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.End,
        )

        Text(
            text = amountColumnValue,
            modifier = Modifier
                .weight(2.7f)
                .padding(end = 6.dp, top = 6.dp, bottom = 6.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.End,
        )

        Text(
            text = balanceColumnValue,
            modifier = Modifier
                .weight(2f)
                .padding(vertical = 6.dp)
                .padding(end = 2.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
fun getButtonText(status: LoanStatusEntity): String {
    return when {
        status.active == true || status.closedObligationsMet == true -> {
            stringResource(Res.string.feature_loan_make_Repayment)
        }

        status.pendingApproval == true -> {
            stringResource(Res.string.feature_loan_approve_loan)
        }

        status.waitingForDisbursal == true -> {
            stringResource(Res.string.feature_loan_disburse_loan)
        }

        else -> {
            stringResource(Res.string.feature_loan_closed)
        }
    }
}

private fun getButtonActiveStatus(status: LoanStatusEntity): Boolean {
    return when {
        status.active == true || status.pendingApproval == true || status.waitingForDisbursal == true -> {
            true
        }

        else -> {
            false
        }
    }
}

@Composable
private fun getInflateLoanSummaryValue(status: LoanStatusEntity): Boolean {
    return when {
        status.active == true || status.closedObligationsMet == true -> {
            true
        }

        status.pendingApproval == true || status.waitingForDisbursal == true -> {
            false
        }

        else -> {
            true
        }
    }
}

private class LoanAccountSummaryPreviewProvider :
    PreviewParameterProvider<LoanAccountSummaryUiState> {
    private val demoSummary = LoansAccountSummaryEntity(
        loanId = 12345,
        principalDisbursed = 10000.0,
        principalOutstanding = 6000.0,
        principalOverdue = 500.0,
        interestCharged = 500.0,
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

    override val values: Sequence<LoanAccountSummaryUiState>
        get() = sequenceOf(
            LoanAccountSummaryUiState.ShowProgressbar,
            LoanAccountSummaryUiState.ShowFetchingError("Could not fetch summary"),
            LoanAccountSummaryUiState.ShowLoanById(
                LoanWithAssociationsEntity(
                    accountNo = "90927493938",
                    status = LoanStatusEntity(
                        closedObligationsMet = true,
                    ),
                    clientName = "Pronay sarker",
                    loanOfficerName = "MR. Ching",
                    loanProductName = "Group Loan",
                    summary = demoSummary,
                ),
            ),
        )
}

@Composable
@Preview
private fun PreviewLoanAccountSummary(
    @PreviewParameter(LoanAccountSummaryPreviewProvider::class) loanAccountSummaryUiState: LoanAccountSummaryUiState,
) {
    LoanAccountSummaryScreen(
        uiState = loanAccountSummaryUiState,
        navigateBack = { },
        onRetry = { },
        onMoreInfoClicked = { },
        onTransactionsClicked = { },
        onRepaymentScheduleClicked = { },
        onDocumentsClicked = { },
        onChargesClicked = { },
        approveLoan = { },
        disburseLoan = { },
        makeRepayment = { },
    )
}
