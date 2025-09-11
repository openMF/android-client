/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.report.reportDetail

import androidclient.feature.report.generated.resources.Res
import androidclient.feature.report.generated.resources.feature_report_currency
import androidclient.feature.report.generated.resources.feature_report_details
import androidclient.feature.report.generated.resources.feature_report_failed_to_load_report_details
import androidclient.feature.report.generated.resources.feature_report_fund
import androidclient.feature.report.generated.resources.feature_report_gl_account
import androidclient.feature.report.generated.resources.feature_report_ic_report_item
import androidclient.feature.report.generated.resources.feature_report_loan_officer
import androidclient.feature.report.generated.resources.feature_report_loan_purpose
import androidclient.feature.report.generated.resources.feature_report_obligation_date
import androidclient.feature.report.generated.resources.feature_report_office
import androidclient.feature.report.generated.resources.feature_report_par_type
import androidclient.feature.report.generated.resources.feature_report_product
import androidclient.feature.report.generated.resources.feature_report_run_report
import androidclient.feature.report.generated.resources.feature_report_saving_account
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Constants
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.model.objects.runreport.DataRow
import com.mifos.core.model.objects.runreport.client.ClientReportTypeItem
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.feature.report.report.ReportScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ReportDetailScreen(
    onBackPressed: () -> Unit,
    viewModel: ReportDetailViewModel = koinViewModel(),
) {
    val reportItem = viewModel.reportItem
    val state by viewModel.reportDetailUiState.collectAsStateWithLifecycle()
    val reportParameterList by viewModel.reportParameterList.collectAsStateWithLifecycle()
    val reportDetail by viewModel.reportDetail.collectAsStateWithLifecycle()
    val reportUiState by viewModel.reportUiState.collectAsStateWithLifecycle()
    val reportOffices by viewModel.reportOffices.collectAsStateWithLifecycle()
    val reportProducts by viewModel.reportProducts.collectAsStateWithLifecycle()
    val runReportDetail by viewModel.runReport.collectAsStateWithLifecycle()
    var runReportEnable by remember { mutableStateOf(false) }
    var showReportScreen by remember { mutableStateOf(false) }

    LaunchedEffect(runReportDetail) {
        runReportDetail?.let {
            if (runReportEnable) {
                showReportScreen = true
            }
        }
    }

    var officeId by rememberSaveable { mutableIntStateOf(0) }
    var currencyId by rememberSaveable { mutableStateOf("") }

    var officeList by rememberSaveable { mutableStateOf(emptyList<DataRow>()) }
    var loanPurposeList by rememberSaveable { mutableStateOf(emptyList<DataRow>()) }
    var fundList by rememberSaveable { mutableStateOf(emptyList<DataRow>()) }
    var currencyList by rememberSaveable { mutableStateOf(emptyList<DataRow>()) }
    var parCalculatorList by rememberSaveable { mutableStateOf(emptyList<DataRow>()) }
    var savingsAccountDepositList by rememberSaveable { mutableStateOf(emptyList<DataRow>()) }
    var glAccountList by rememberSaveable { mutableStateOf(emptyList<DataRow>()) }
    var obligationDateList by rememberSaveable { mutableStateOf(emptyList<DataRow>()) }

    LaunchedEffect(reportDetail) {
        when (reportDetail.second) {
            Constants.LOAN_OFFICER_ID_SELECT -> {
                viewModel.fetchOffices(reportDetail.second, officeId, true)
            }

            Constants.LOAN_PRODUCT_ID_SELECT -> {
                viewModel.fetchProduct(reportDetail.second, currencyId, true)
            }

            Constants.LOAN_PURPOSE_ID_SELECT -> {
                loanPurposeList = reportDetail.first
            }

            Constants.FUND_ID_SELECT -> {
                fundList = reportDetail.first
            }

            Constants.CURRENCY_ID_SELECT -> {
                currencyId = reportDetail.first.first().row.first() ?: ""
                currencyList = reportDetail.first
                viewModel.fetchProduct(Constants.LOAN_PRODUCT_ID_SELECT, currencyId, true)
            }

            Constants.OFFICE_ID_SELECT -> {
                officeList = reportDetail.first
                officeId = reportDetail.first.first().row.first()?.toInt() ?: -1
                viewModel.fetchOffices(Constants.LOAN_OFFICER_ID_SELECT, officeId, true)
            }

            Constants.PAR_TYPE_SELECT -> {
                parCalculatorList = reportDetail.first
            }

            Constants.SAVINGS_ACCOUNT_SUB_STATUS -> {
                savingsAccountDepositList = reportDetail.first
            }

            Constants.SELECT_GL_ACCOUNT_NO -> {
                glAccountList = reportDetail.first
            }

            Constants.OBLIG_DATE_TYPE_SELECT -> {
                obligationDateList = reportDetail.first
            }
        }
    }

    LaunchedEffect(Unit) {
        val reportName = "'" + reportItem.reportName + "'"
        viewModel.fetchFullParameterList(reportName, true)
    }

    LaunchedEffect(reportParameterList) {
        reportParameterList.forEach {
            if (it.row.first() != null) {
                viewModel.fetchParameterDetails(it.row.first() ?: "", true)
            }
        }
    }

    if (showReportScreen && runReportDetail != null) {
        ReportScreen(
            state = reportUiState,
            report = runReportDetail!!,
            onBackPressed = onBackPressed,
            exportReport = {
                viewModel.exportCsv(runReportDetail!!)
            },
        )
    } else {
        ReportDetailScreen(
            reportItem = reportItem,
            state = state,
            onBackPressed = onBackPressed,
            onRetry = {},
            officeList = officeList,
            loanPurposeList = loanPurposeList,
            fundList = fundList,
            currencyList = currencyList,
            parCalculatorList = parCalculatorList,
            savingsAccountDepositList = savingsAccountDepositList,
            glAccountList = glAccountList,
            obligationDateList = obligationDateList,
            reportOffices = reportOffices,
            reportProducts = reportProducts,
            runReport = { mapQuery ->
                runReportEnable = true
                reportItem.reportName?.let {
                    viewModel.fetchRunReportWithQuery(it, mapQuery)
                }
            },
        )
    }
}

@Composable
internal fun ReportDetailScreen(
    reportItem: ClientReportTypeItem,
    state: ReportDetailUiState,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    officeList: List<DataRow>,
    loanPurposeList: List<DataRow>,
    fundList: List<DataRow>,
    currencyList: List<DataRow>,
    parCalculatorList: List<DataRow>,
    savingsAccountDepositList: List<DataRow>,
    glAccountList: List<DataRow>,
    obligationDateList: List<DataRow>,
    reportOffices: List<DataRow>,
    reportProducts: List<DataRow>,
    modifier: Modifier = Modifier,
    runReport: (MutableMap<String, String>) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val runReportDetail = remember { mutableStateMapOf<String, String>() }

    MifosScaffold(
        modifier = modifier,
        title = stringResource(Res.string.feature_report_details),
        onBackPressed = onBackPressed,
        actions = {
            TextButton(
                onClick = { runReport(runReportDetail) },
            ) {
                Text(text = stringResource(Res.string.feature_report_run_report))
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (state) {
                is ReportDetailUiState.Error -> MifosSweetError(message = stringResource(state.message)) {
                    onRetry()
                }

                is ReportDetailUiState.Loading -> MifosProgressIndicator()

                ReportDetailUiState.ParameterDetailsSuccess -> {
                    RunReportContent(
                        reportItem = reportItem,
                        officeList = officeList,
                        loanPurposeList = loanPurposeList,
                        fundList = fundList,
                        currencyList = currencyList,
                        parCalculatorList = parCalculatorList,
                        savingsAccountDepositList = savingsAccountDepositList,
                        glAccountList = glAccountList,
                        obligationDateList = obligationDateList,
                        reportOffices = reportOffices,
                        reportProducts = reportProducts,
                        runReportDetail = runReportDetail,
                    )
                }
            }
        }
    }
}

@Composable
private fun RunReportContent(
    reportItem: ClientReportTypeItem,
    officeList: List<DataRow>,
    loanPurposeList: List<DataRow>,
    fundList: List<DataRow>,
    currencyList: List<DataRow>,
    parCalculatorList: List<DataRow>,
    savingsAccountDepositList: List<DataRow>,
    glAccountList: List<DataRow>,
    obligationDateList: List<DataRow>,
    reportOffices: List<DataRow>,
    reportProducts: List<DataRow>,
    runReportDetail: SnapshotStateMap<String, String>,
    modifier: Modifier = Modifier,
) {
    val firstOffice = officeList.firstOrNull()?.row?.getOrNull(1) ?: ""
    val firstOfficeId = officeList.firstOrNull()?.row?.getOrNull(0) ?: ""
    val firstOfficer = reportOffices.firstOrNull()?.row?.getOrNull(1) ?: ""
    val firstOfficerId = reportOffices.firstOrNull()?.row?.getOrNull(0) ?: ""

    var selectedOffice by rememberSaveable { mutableStateOf(firstOffice) }
    var selectedLoanPurpose by rememberSaveable { mutableStateOf("") }
    var selectedLoanOfficer by rememberSaveable { mutableStateOf(firstOfficer) }
    var selectedProducts by rememberSaveable { mutableStateOf("") }
    var selectedFund by rememberSaveable { mutableStateOf("") }
    var selectedCurrency by rememberSaveable { mutableStateOf("") }
    var selectedParCalculator by rememberSaveable { mutableStateOf("") }
    var selectedSavingsAccountDeposit by rememberSaveable { mutableStateOf("") }
    var selectedGlAccount by rememberSaveable { mutableStateOf("") }
    var selectedObligationDate by rememberSaveable { mutableStateOf("") }

    var selectedOfficeId by rememberSaveable { mutableStateOf(firstOfficeId) }
    var selectedLoanPurposeId by rememberSaveable { mutableStateOf("") }
    var selectedLoanOfficerId by rememberSaveable { mutableStateOf(firstOfficerId) }
    var selectedProductsId by rememberSaveable { mutableStateOf("") }
    var selectedFundId by rememberSaveable { mutableStateOf("") }
    var selectedCurrencyId by rememberSaveable { mutableStateOf("") }
    var selectedParCalculatorId by rememberSaveable { mutableStateOf("") }
    var selectedSavingsAccountDepositId by rememberSaveable { mutableStateOf("") }
    var selectedGlAccountId by rememberSaveable { mutableStateOf("") }
    var selectedObligationDateId by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(
        selectedOffice,
        selectedLoanPurpose,
        selectedLoanOfficer,
        selectedProducts,
        selectedFund,
        selectedCurrency,
        selectedParCalculator,
        selectedSavingsAccountDeposit,
        selectedGlAccount,
        selectedObligationDate,
    ) {
        if (selectedOffice.isNotEmpty() && selectedOffice.isNotEmpty()) {
            runReportDetail[Constants.R_OFFICE_ID] = selectedOfficeId
        }

        if (selectedLoanPurpose.isNotEmpty()) {
            runReportDetail[Constants.R_LOAN_PURPOSE_ID] = selectedLoanPurposeId
        }

        if (selectedLoanOfficer.isNotEmpty() && selectedLoanOfficer.isNotEmpty()) {
            runReportDetail[Constants.R_LOAN_OFFICER_ID] = selectedLoanOfficerId
        }

        if (selectedProducts.isNotEmpty()) {
            runReportDetail[Constants.R_LOAN_PRODUCT_ID] = selectedProductsId
        }

        if (selectedFund.isNotEmpty()) {
            runReportDetail[Constants.R_FUND_ID] = selectedFundId
        }

        if (selectedCurrency.isNotEmpty()) {
            runReportDetail[Constants.R_CURRENCY_ID] = selectedCurrencyId
        }

        if (selectedParCalculator.isNotEmpty()) {
            runReportDetail[Constants.R_PAR_TYPE] = selectedParCalculatorId
        }

        if (selectedSavingsAccountDeposit.isNotEmpty()) {
            runReportDetail[Constants.R_SUB_STATUS] =
                selectedSavingsAccountDepositId
        }

        if (selectedGlAccount.isNotEmpty()) {
            runReportDetail[Constants.R_ACCOUNT] = selectedGlAccountId
        }

        if (selectedObligationDate.isNotEmpty()) {
            runReportDetail[Constants.R_OBLIG_DATE_TYPE] = selectedObligationDateId
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        OutlinedCard(
            modifier = modifier
                .padding(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        16.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Blue, CircleShape),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.feature_report_ic_report_item),
                        contentDescription = null,
                        tint = Black,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                ) {
                    reportItem.reportName?.let {
                        Text(
                            text = it,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                            ),
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = reportItem.reportType.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                            ),
                        )
                        Text(
                            text = reportItem.reportCategory.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                            ),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (officeList.isNotEmpty()) {
            MifosTextFieldDropdown(
                value = selectedOffice,
                onValueChanged = {
                    selectedOffice = it
                },
                onOptionSelected = { index, value ->
                    selectedOffice = value
                    selectedOfficeId = officeList.getOrNull(index)?.row?.getOrNull(0) ?: ""
                },
                label = stringResource(Res.string.feature_report_office),
                options = officeList.mapNotNull { it.row.getOrNull(1) },
                readOnly = true,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (loanPurposeList.isNotEmpty()) {
            MifosTextFieldDropdown(
                value = selectedLoanPurpose,
                onValueChanged = {
                    selectedLoanPurpose = it
                },
                onOptionSelected = { index, value ->
                    selectedLoanPurpose = value
                    selectedLoanPurposeId = loanPurposeList.getOrNull(index)?.row?.getOrNull(0) ?: ""
                },
                label = stringResource(Res.string.feature_report_loan_purpose),
                options = loanPurposeList.mapNotNull { it.row.getOrNull(1) },
                readOnly = true,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (reportOffices.isNotEmpty()) {
            MifosTextFieldDropdown(
                value = selectedLoanOfficer,
                onValueChanged = {
                    selectedLoanOfficer = it
                },
                onOptionSelected = { index, value ->
                    selectedLoanOfficer = value
                    selectedLoanOfficerId = reportOffices.getOrNull(index)?.row?.getOrNull(0) ?: ""
                },
                label = stringResource(Res.string.feature_report_loan_officer),
                options = reportOffices.mapNotNull { it.row.getOrNull(1) },
                readOnly = true,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (reportProducts.isNotEmpty()) {
            MifosTextFieldDropdown(
                value = selectedProducts,
                onValueChanged = {
                    selectedProducts = it
                },
                onOptionSelected = { index, value ->
                    selectedProducts = value
                    selectedProductsId = reportProducts.getOrNull(index)?.row?.getOrNull(0) ?: ""
                },
                label = stringResource(Res.string.feature_report_product),
                options = reportProducts.mapNotNull { it.row.getOrNull(1) },
                readOnly = true,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (fundList.isNotEmpty()) {
            MifosTextFieldDropdown(
                value = selectedFund,
                onValueChanged = {
                    selectedFund = it
                },
                onOptionSelected = { index, value ->
                    selectedFund = value
                    selectedFundId = fundList[index].row.first() ?: ""
                },
                label = stringResource(Res.string.feature_report_fund),
                options = fundList.mapNotNull { it.row.getOrNull(1) },
                readOnly = true,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (currencyList.isNotEmpty()) {
            MifosTextFieldDropdown(
                value = selectedCurrency,
                onValueChanged = {
                    selectedCurrency = it
                },
                onOptionSelected = { index, value ->
                    selectedCurrency = value
                    selectedCurrencyId = currencyList[index].row.first() ?: ""
                },
                label = stringResource(Res.string.feature_report_currency),
                options = currencyList.mapNotNull { it.row.getOrNull(1) },
                readOnly = true,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (parCalculatorList.isNotEmpty()) {
            MifosTextFieldDropdown(
                value = selectedParCalculator,
                onValueChanged = {
                    selectedParCalculator = it
                },
                onOptionSelected = { index, value ->
                    selectedParCalculator = value
                    selectedParCalculatorId = parCalculatorList[index].row.first() ?: ""
                },
                label = stringResource(Res.string.feature_report_par_type),
                options = parCalculatorList.mapNotNull { it.row.getOrNull(1) },
                readOnly = true,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (savingsAccountDepositList.isNotEmpty()) {
            MifosTextFieldDropdown(
                value = selectedSavingsAccountDeposit,
                onValueChanged = {
                    selectedSavingsAccountDeposit = it
                },
                onOptionSelected = { index, value ->
                    selectedSavingsAccountDeposit = value
                    selectedSavingsAccountDepositId = savingsAccountDepositList[index].row.first() ?: ""
                },
                label = stringResource(Res.string.feature_report_saving_account),
                options = savingsAccountDepositList.mapNotNull { it.row.getOrNull(1) },
                readOnly = true,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (glAccountList.isNotEmpty()) {
            MifosTextFieldDropdown(
                value = selectedGlAccount,
                onValueChanged = {
                    selectedGlAccount = it
                },
                onOptionSelected = { index, value ->
                    selectedGlAccount = value
                    selectedGlAccountId = glAccountList[index].row.first() ?: ""
                },
                label = stringResource(Res.string.feature_report_gl_account),
                options = glAccountList.mapNotNull { it.row.getOrNull(1) },
                readOnly = true,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (obligationDateList.isNotEmpty()) {
            MifosTextFieldDropdown(
                value = selectedObligationDate,
                onValueChanged = {
                    selectedObligationDate = it
                },
                onOptionSelected = { index, value ->
                    selectedObligationDate = value
                    selectedObligationDateId = obligationDateList[index].row.first() ?: ""
                },
                label = stringResource(Res.string.feature_report_obligation_date),
                options = obligationDateList.mapNotNull { it.row.getOrNull(1) },
                readOnly = true,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private class ReportDetailUiStateProvider : PreviewParameterProvider<ReportDetailUiState> {

    override val values: Sequence<ReportDetailUiState>
        get() = sequenceOf(
            ReportDetailUiState.Error(Res.string.feature_report_failed_to_load_report_details),
            ReportDetailUiState.Loading,
            ReportDetailUiState.ParameterDetailsSuccess,
        )
}

@Preview
@Composable
private fun ReportDetailScreenPreview(
    @PreviewParameter(ReportDetailUiStateProvider::class) state: ReportDetailUiState,
) {
    ReportDetailScreen(
        reportItem = ClientReportTypeItem(),
        state = state,
        onBackPressed = {},
        onRetry = {},
        officeList = emptyList(),
        loanPurposeList = emptyList(),
        fundList = emptyList(),
        currencyList = emptyList(),
        parCalculatorList = emptyList(),
        savingsAccountDepositList = emptyList(),
        glAccountList = emptyList(),
        obligationDateList = emptyList(),
        reportOffices = emptyList(),
        reportProducts = emptyList(),
        runReport = {},
    )
}
