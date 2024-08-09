package com.mifos.feature.loan.loan_account_summary

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.core.objects.accounts.loan.Status
import com.mifos.core.objects.accounts.loan.Summary
import com.mifos.feature.loan.R

/**
 * Created by Pronay Sarker on 01/07/2024 (5:50 AM)
 */


@Composable
fun LoanAccountSummaryScreen(
    viewModel: LoanAccountSummaryViewModel = hiltViewModel(),
    loanAccountNumber: Int,
    navigateBack: () -> Unit,
    onMoreInfoClicked: () -> Unit,
    onTransactionsClicked: (loadId: Int) -> Unit,
    onRepaymentScheduleClicked: (loanId: Int) -> Unit,
    onDocumentsClicked: () -> Unit,
    onChargesClicked: () -> Unit,
    approveLoan: (loanWithAssociations: LoanWithAssociations) -> Unit,
    disburseLoan: () -> Unit,
    onRepaymentClick: (loanWithAssociations: LoanWithAssociations) -> Unit
) {
    val uiState by viewModel.loanAccountSummaryUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadLoanById(loanAccountNumber)
    }

    LoanAccountSummaryScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = { viewModel.loadLoanById(loanAccountNumber) },
        onMoreInfoClicked = onMoreInfoClicked,
        onTransactionsClicked = { onTransactionsClicked.invoke(loanAccountNumber) },
        onRepaymentScheduleClicked = { onRepaymentScheduleClicked.invoke(loanAccountNumber) },
        onDocumentsClicked = onDocumentsClicked,
        onChargesClicked = onChargesClicked,
        approveLoan = approveLoan,
        disburseLoan = disburseLoan,
        makeRepayment = onRepaymentClick
    )
}

@Composable
fun LoanAccountSummaryScreen(
    uiState: LoanAccountSummaryUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
    onMoreInfoClicked: () -> Unit,
    onTransactionsClicked: () -> Unit,
    onRepaymentScheduleClicked: () -> Unit,
    onDocumentsClicked: () -> Unit,
    onChargesClicked: () -> Unit,
    approveLoan: (loanWithAssociations: LoanWithAssociations) -> Unit,
    disburseLoan: () -> Unit,
    makeRepayment: (loanWithAssociations: LoanWithAssociations) -> Unit
) {
    val snackbarHostState = remember {
        androidx.compose.material3.SnackbarHostState()
    }
    var openDropdown by rememberSaveable {
        mutableStateOf(false)
    }

    MifosScaffold(icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_loan_loan_account_summary),
        onBackPressed = navigateBack,
        snackbarHostState = snackbarHostState,
        actions = {
            IconButton(onClick = { openDropdown = !openDropdown }) {
                Icon(
                    imageVector = MifosIcons.moreVert, contentDescription = null
                )
            }
            if (openDropdown) {
                DropdownMenu(
                    expanded = openDropdown,
                    onDismissRequest = { openDropdown = false }
                ) {
                    MifosMenuDropDownItem(
                        option = Constants.DATA_TABLE_LOAN_NAME,
                        onClick = {
                            openDropdown = false
                            onMoreInfoClicked.invoke()
                        }
                    )
                    MifosMenuDropDownItem(
                        option = stringResource(id = R.string.feature_loan_transactions),
                        onClick = {
                            openDropdown = false
                            onTransactionsClicked.invoke()
                        }
                    )
                    MifosMenuDropDownItem(
                        option = stringResource(id = R.string.feature_loan_repayment_schedule),
                        onClick = {
                            openDropdown = false
                            onRepaymentScheduleClicked.invoke()
                        }
                    )
                    MifosMenuDropDownItem(
                        option = stringResource(id = R.string.feature_loan_documents),
                        onClick = {
                            openDropdown = false
                            onDocumentsClicked.invoke()
                        }
                    )
                    MifosMenuDropDownItem(
                        option = stringResource(id = R.string.feature_loan_loan_charges),
                        onClick = {
                            openDropdown = false
                            onChargesClicked.invoke()
                        }
                    )
                }
            }
        }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (uiState) {
                is LoanAccountSummaryUiState.ShowFetchingError -> {
                    MifosSweetError(
                        message = uiState.message, onclick = onRetry
                    )
                }

                is LoanAccountSummaryUiState.ShowLoanById -> {
                    val loanWithAssociations = uiState.loanWithAssociations
                    LoanAccountSummaryContent(
                        loanWithAssociations = loanWithAssociations,
                        makeRepayment = { makeRepayment.invoke(loanWithAssociations) },
                        approveLoan = { approveLoan.invoke(loanWithAssociations) },
                        disburseLoan = disburseLoan
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
fun LoanAccountSummaryContent(
    loanWithAssociations: LoanWithAssociations,
    makeRepayment: () -> Unit,
    approveLoan: () -> Unit,
    disburseLoan: () -> Unit,
) {
    val context = LocalContext.current
    val inflateLoanSummary = getInflateLoanSummaryValue(status = loanWithAssociations.status)
    val scrollState = rememberScrollState()

    fun getActualDisbursementDateInStringFormat(): String {
        try {
            return loanWithAssociations.timeline.actualDisbursementDate?.let {
                DateHelper.getDateAsString(it as List<Int>)
            } ?: ""
        } catch (exception: IndexOutOfBoundsException) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.feature_loan_loan_rejected_message),
                Toast.LENGTH_SHORT
            ).show()
            return ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier
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
                        }
                    )
                })

            LoanSummaryFarApartTextItem(
                title = loanWithAssociations.loanProductName,
                value = "#" + loanWithAssociations.accountNo
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
        )

        LoanSummaryFarApartTextItem(
            title = stringResource(id = R.string.feature_loan_loan_amount_disbursed),
            value = if (inflateLoanSummary) loanWithAssociations.summary.principalDisbursed?.toString()
                ?: "" else ""
        )

        LoanSummaryFarApartTextItem(
            title = stringResource(id = R.string.feature_loan_disbursed_date),
            value = if (inflateLoanSummary) getActualDisbursementDateInStringFormat() else ""
        )

        LoanSummaryFarApartTextItem(
            title = stringResource(id = R.string.feature_loan_loan_in_arrears),
            value = if (inflateLoanSummary) loanWithAssociations.summary.totalOverdue?.toString()
                ?: "" else ""
        )

        LoanSummaryFarApartTextItem(
            title = stringResource(id = R.string.feature_loan_staff),
            value = loanWithAssociations.loanOfficerName
        )

        Spacer(modifier = Modifier.height(8.dp))

        LoanSummaryDataTable(
            loanSummary = loanWithAssociations.summary,
            inflateLoanSummary = inflateLoanSummary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            enabled = getButtonActiveStatus(loanWithAssociations.status),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .height(45.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
            ),
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
                    { Log.d("LoanAccountSummary", "TRANSACTION ACTION NOT SET") }
                }

                else -> {
                    { Log.d("LoanAccountSummary", "TRANSACTION ACTION NOT SET") }
                }
            }
        ) {
            Text(
                color = MaterialTheme.colorScheme.background,
                text = getButtonText(context, loanWithAssociations.status)
            )
        }
    }
}

@Composable
fun LoanSummaryDataTable(loanSummary: Summary, inflateLoanSummary: Boolean) {
    // dataTable should be empty if [inflateLoanSummary] is false
    val summary = if (inflateLoanSummary) loanSummary else null

    DataTableRow(
        summaryColumnTitle = stringResource(id = R.string.feature_loan_summary),
        loanColumnValue = stringResource(id = R.string.feature_loan),
        amountColumnValue = stringResource(id = R.string.feature_loan_amount_paid),
        balanceColumnValue = stringResource(id = R.string.feature_loan_balance),
        isHeader = true,
        color = BluePrimary.copy(alpha = .3f)
    )

    DataTableRow(
        summaryColumnTitle = stringResource(id = R.string.feature_loan_loan_principal),
        loanColumnValue = summary?.principalDisbursed?.toString() ?: "",
        amountColumnValue = summary?.principalPaid?.toString() ?: "",
        balanceColumnValue = summary?.principalOutstanding?.toString() ?: ""
    )

    DataTableRow(
        summaryColumnTitle = stringResource(id = R.string.feature_loan_loan_interest),
        loanColumnValue = summary?.interestCharged?.toString() ?: "",
        amountColumnValue = summary?.interestPaid?.toString() ?: "",
        balanceColumnValue = summary?.interestOutstanding?.toString() ?: "",
        color = BluePrimary.copy(alpha = .1f)
    )

    DataTableRow(
        summaryColumnTitle = stringResource(id = R.string.feature_loan_loan_fees),
        loanColumnValue = summary?.feeChargesCharged?.toString() ?: "",
        amountColumnValue = summary?.feeChargesPaid?.toString() ?: "",
        balanceColumnValue = summary?.feeChargesOutstanding?.toString() ?: ""
    )

    DataTableRow(
        summaryColumnTitle = stringResource(id = R.string.feature_loan_loan_penalty),
        loanColumnValue = summary?.penaltyChargesCharged?.toString() ?: "",
        amountColumnValue = summary?.penaltyChargesPaid?.toString() ?: "",
        balanceColumnValue = summary?.penaltyChargesOutstanding?.toString() ?: "",
        color = BluePrimary.copy(alpha = .1f)
    )

    DataTableRow(
        summaryColumnTitle = stringResource(id = R.string.feature_loan_total),
        loanColumnValue = summary?.totalExpectedRepayment?.toString() ?: "",
        amountColumnValue = summary?.totalRepayment?.toString() ?: "",
        balanceColumnValue = summary?.totalOutstanding?.toString() ?: ""
    )
}

@Composable
fun LoanSummaryFarApartTextItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = title,
            color = Black,
        )

        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = value,
            color = DarkGray,
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
    color: Color = White
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
            textAlign = TextAlign.End
        )
    }
}

private fun getButtonText(context: Context, status: Status): String {
    return when {
        status.active == true || status.closedObligationsMet == true -> {
            context.resources.getString(R.string.feature_loan_make_Repayment)
        }

        status.pendingApproval == true -> {
            context.resources.getString(R.string.feature_loan_approve_loan)
        }

        status.waitingForDisbursal == true -> {
            context.resources.getString(R.string.feature_loan_disburse_loan)
        }

        else -> {
            context.resources.getString(R.string.feature_loan_closed)
        }
    }
}

private fun getButtonActiveStatus(status: Status): Boolean {
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
fun getInflateLoanSummaryValue(status: Status): Boolean {
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

class LoanAccountSummaryPreviewProvider : PreviewParameterProvider<LoanAccountSummaryUiState> {
    private val demoSummary = Summary(
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
        overdueSinceDate = listOf(2024, 6, 1)
    )

    override val values: Sequence<LoanAccountSummaryUiState>
        get() = sequenceOf(
            LoanAccountSummaryUiState.ShowProgressbar,
            LoanAccountSummaryUiState.ShowFetchingError("Could not fetch summary"),
            LoanAccountSummaryUiState.ShowLoanById(
                LoanWithAssociations(
                    accountNo = "90927493938",
                    status = Status(
                        closedObligationsMet = true
                    ),
                    clientName = "Pronay sarker",
                    loanOfficerName = "MR. Ching chong",
                    loanProductName = "Group Loan",
                    summary = demoSummary
                )
            ),
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewLoanAccountSummary(
    @PreviewParameter(LoanAccountSummaryPreviewProvider::class) loanAccountSummaryUiState: LoanAccountSummaryUiState
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
        makeRepayment = { }
    )
}