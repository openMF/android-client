/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.loan.loanAccount

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.data.LoansPayload
import com.mifos.core.dbobjects.noncore.DataTable
import com.mifos.core.dbobjects.templates.loans.LoanTemplate
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.objects.organisations.LoanProducts
import com.mifos.feature.loan.R
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LoanAccountScreen(
    onBackPressed: () -> Unit,
    dataTable: (List<DataTable>, LoansPayload) -> Unit,
    viewModel: LoanAccountViewModel = hiltViewModel(),
) {
    val state by viewModel.loanAccountUiState.collectAsStateWithLifecycle()
    val loanAccountTemplateState by viewModel.loanAccountTemplateUiState.collectAsStateWithLifecycle()
    val clientId by viewModel.clientId.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadAllLoans()
    }

    LoanAccountScreen(
        clientId = clientId,
        state = state,
        loanAccountTemplateState = loanAccountTemplateState,
        onBackPressed = onBackPressed,
        onRetry = {
            viewModel.loadAllLoans()
        },
        onLoanProductSelected = { productId ->
            viewModel.loadLoanAccountTemplate(clientId, productId)
        },
        createLoanAccount = { loansPayload ->
            viewModel.createLoansAccount(loansPayload)
        },
        dataTable = dataTable,
        fetchTemplate = { productId ->
            viewModel.loadLoanAccountTemplate(clientId, productId)
        },
    )
}

@Composable
fun LoanAccountScreen(
    clientId: Int,
    state: LoanAccountUiState,
    loanAccountTemplateState: LoanTemplate,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    onLoanProductSelected: (Int) -> Unit,
    createLoanAccount: (LoansPayload) -> Unit,
    dataTable: (List<DataTable>, LoansPayload) -> Unit,
    fetchTemplate: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_loan_application),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues),
        ) {
            when (state) {
                is LoanAccountUiState.AllLoan -> {
                    LoanAccountContent(
                        clientsId = clientId,
                        productLoans = state.productLoans,
                        loanTemplate = loanAccountTemplateState,
                        onLoanProductSelected = onLoanProductSelected,
                        createLoanAccount = createLoanAccount,
                        dataTable = dataTable,
                    )
                    state.productLoans[0].id?.let { fetchTemplate(it) }
                }

                is LoanAccountUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {
                    onRetry()
                }

                is LoanAccountUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        MifosCircularProgress()
                    }
                }

                is LoanAccountUiState.LoanAccountCreatedSuccessfully -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(id = R.string.feature_loan_account_created_successfully),
                        Toast.LENGTH_SHORT,
                    ).show()
                    onBackPressed()
                }
            }
        }
    }
}

@Composable
private fun LoanAccountContent(
    clientsId: Int,
    productLoans: List<LoanProducts>,
    loanTemplate: LoanTemplate,
    onLoanProductSelected: (Int) -> Unit,
    createLoanAccount: (LoansPayload) -> Unit,
    dataTable: (List<DataTable>, LoansPayload) -> Unit,
) {
    var selectedLoanProduct by rememberSaveable { mutableStateOf("") }
    var selectedLoanProductId by rememberSaveable { mutableIntStateOf(0) }
    var selectedLoanPurpose by rememberSaveable { mutableStateOf("") }
    var selectedLoanPurposeId by rememberSaveable { mutableIntStateOf(0) }
    var selectedLoanOfficer by rememberSaveable { mutableStateOf("") }
    var selectedLoanOfficerId by rememberSaveable { mutableIntStateOf(0) }
    var selectedFund by rememberSaveable { mutableStateOf("") }
    var selectedFundId by rememberSaveable { mutableIntStateOf(0) }

    var showSubmissionDatePicker by rememberSaveable { mutableStateOf(false) }
    var submissionDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    val submissionDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = submissionDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        },
    )

    var showDisbursementDatePicker by rememberSaveable { mutableStateOf(false) }
    var disbursementDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    val disbursementDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = disbursementDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        },
    )

    var externalId by rememberSaveable { mutableStateOf("") }
    var principalAmount by rememberSaveable { mutableStateOf("10000.0") }
    var numberOfRepayment by rememberSaveable { mutableStateOf("10") }
    var nominal by rememberSaveable { mutableStateOf("5.0") }
    var repaidEvery by rememberSaveable { mutableStateOf("2") }
    var repaidEveryType by rememberSaveable { mutableStateOf("") }
    var repaidEveryTypeFrequency by rememberSaveable { mutableIntStateOf(0) }
    var loanTerms by rememberSaveable { mutableStateOf("20") }
    var loanTermsType by rememberSaveable { mutableStateOf("") }
    var loanTermsTypeFrequency by rememberSaveable { mutableIntStateOf(0) }

    var selectedLinkSavings by rememberSaveable { mutableStateOf("") }
    var selectedLinkSavingsId by rememberSaveable { mutableIntStateOf(0) }
    var selectedAmortization by rememberSaveable { mutableStateOf("") }
    var selectedAmortizationId by rememberSaveable { mutableIntStateOf(0) }
    var selectedInterestCalculationPeriod by rememberSaveable { mutableStateOf("") }
    var selectedInterestCalculationPeriodId by rememberSaveable { mutableIntStateOf(0) }
    var selectedRepaymentStrategy by rememberSaveable { mutableStateOf("") }
    var selectedRepaymentStrategyId by rememberSaveable { mutableIntStateOf(0) }
    var selectedInterestTypeMethod by rememberSaveable { mutableStateOf("") }
    var selectedInterestTypeMethodId by rememberSaveable { mutableIntStateOf(0) }
    var selectedCalculateExactDaysIn by rememberSaveable { mutableStateOf(false) }

    if (showSubmissionDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showSubmissionDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSubmissionDatePicker = false
                        submissionDatePickerState.selectedDateMillis?.let {
                            submissionDate = it
                        }
                    },
                ) { Text(stringResource(id = R.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSubmissionDatePicker = false
                    },
                ) { Text(stringResource(id = R.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = submissionDatePickerState)
        }
    }

    if (showDisbursementDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDisbursementDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDisbursementDatePicker = false
                        disbursementDatePickerState.selectedDateMillis?.let {
                            disbursementDate = it
                        }
                    },
                ) { Text(stringResource(id = R.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSubmissionDatePicker = false
                    },
                ) { Text(stringResource(id = R.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = disbursementDatePickerState)
        }
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        MifosTextFieldDropdown(
            value = selectedLoanProduct,
            onValueChanged = { selectedLoanProduct = it },
            onOptionSelected = { index, value ->
                selectedLoanProduct = value
                productLoans[index].id?.let {
                    selectedLoanProductId = it
                    onLoanProductSelected(it)
                }
            },
            label = R.string.feature_loan_product,
            options = productLoans.map { it.name.toString() },
            readOnly = true,
        )

        MifosTextFieldDropdown(
            value = selectedLoanPurpose,
            onValueChanged = { selectedLoanPurpose = it },
            onOptionSelected = { index, value ->
                selectedLoanPurpose = value
                loanTemplate.loanPurposeOptions[index].id?.let { selectedLoanPurposeId = it }
            },
            label = R.string.feature_loan_purpose,
            options = loanTemplate.loanPurposeOptions.map { it.name.toString() },
            readOnly = true,
        )

        MifosTextFieldDropdown(
            value = selectedLoanOfficer,
            onValueChanged = { selectedLoanOfficer = it },
            onOptionSelected = { index, value ->
                selectedLoanOfficer = value
                loanTemplate.loanOfficerOptions[index].id?.let {
                    selectedLoanOfficerId = it
                }
            },
            label = R.string.feature_loan_officer,
            options = loanTemplate.loanOfficerOptions.map { it.displayName.toString() },
            readOnly = true,
        )

        MifosTextFieldDropdown(
            value = selectedFund,
            onValueChanged = { selectedFund = it },
            onOptionSelected = { index, value ->
                selectedFund = value
                loanTemplate.fundOptions[index].id?.let {
                    selectedFundId = it
                }
            },
            label = R.string.feature_loan_fund,
            options = loanTemplate.fundOptions.map { it.name.toString() },
            readOnly = true,
        )

        MifosDatePickerTextField(
            value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                submissionDate,
            ),
            label = R.string.feature_loan_submission_date,
            openDatePicker = {
                showSubmissionDatePicker = true
            },
        )

        MifosDatePickerTextField(
            value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                disbursementDate,
            ),
            label = R.string.feature_loan_disbursed_date,
            openDatePicker = {
                showDisbursementDatePicker = true
            },
        )

        MifosOutlinedTextField(
            value = externalId,
            label = stringResource(id = R.string.feature_loan_external_id),
            onValueChange = {
                externalId = it
            },
            error = null,
            keyboardType = KeyboardType.Text,
        )

        MifosTextFieldDropdown(
            value = selectedLinkSavings,
            onValueChanged = { selectedLinkSavings = it },
            onOptionSelected = { index, value ->
                selectedLinkSavings = value
                loanTemplate.accountLinkingOptions[index].id?.let {
                    selectedLinkSavingsId = it
                }
            },
            label = R.string.feature_loan_link_savings,
            options = loanTemplate.accountLinkingOptions.map { it.productName.toString() },
            readOnly = true,
        )

        MifosOutlinedTextField(
            value = principalAmount,
            label = stringResource(id = R.string.feature_loan_principal),
            onValueChange = {
                principalAmount = it
            },
            error = null,
            keyboardType = KeyboardType.Number,
        )

        MifosOutlinedTextField(
            value = numberOfRepayment,
            label = stringResource(id = R.string.feature_loan_number_of_repayments),
            onValueChange = {
                numberOfRepayment = it
            },
            error = null,
            keyboardType = KeyboardType.Number,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            MifosOutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                value = nominal,
                label = stringResource(id = R.string.feature_loan_nominal),
                onValueChange = {
                    nominal = it
                },
                error = null,
                keyboardType = KeyboardType.Number,
            )
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.feature_loan_per_month),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            MifosOutlinedTextField(
                modifier = Modifier
                    .weight(2f)
                    .padding(start = 16.dp),
                value = repaidEvery,
                label = stringResource(id = R.string.feature_loan_repaid_every),
                onValueChange = {
                    repaidEvery = it
                },
                error = null,
                keyboardType = KeyboardType.Number,
            )
            MifosTextFieldDropdown(
                modifier = Modifier
                    .width(164.dp)
                    .padding(start = 8.dp, end = 16.dp),
                value = repaidEveryType,
                onValueChanged = { repaidEveryType = it },
                onOptionSelected = { index, value ->
                    repaidEveryType = value
                    loanTemplate.repaymentFrequencyDaysOfWeekTypeOptions[index].id?.let {
                        repaidEveryTypeFrequency = it
                    }
                },
                label = R.string.feature_loan_term,
                options = loanTemplate.repaymentFrequencyDaysOfWeekTypeOptions.map { it.value.toString() },
                readOnly = true,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            MifosOutlinedTextField(
                modifier = Modifier
                    .weight(2f)
                    .padding(start = 16.dp),
                value = loanTerms,
                label = stringResource(id = R.string.feature_loan_loan_terms),
                onValueChange = {
                    loanTerms = it
                },
                error = null,
                keyboardType = KeyboardType.Number,
            )
            MifosTextFieldDropdown(
                modifier = Modifier
                    .width(164.dp)
                    .padding(start = 8.dp, end = 16.dp),
                value = loanTermsType,
                onValueChanged = { loanTermsType = it },
                onOptionSelected = { index, value ->
                    loanTermsType = value
                    loanTemplate.termFrequencyTypeOptions[index].id?.let {
                        loanTermsTypeFrequency = it
                    }
                },
                label = R.string.feature_loan_term,
                options = loanTemplate.termFrequencyTypeOptions.map { it.value.toString() },
                readOnly = true,
            )
        }

        MifosTextFieldDropdown(
            value = selectedAmortization,
            onValueChanged = { selectedAmortization = it },
            onOptionSelected = { index, value ->
                selectedAmortization = value
                loanTemplate.amortizationTypeOptions[index].id?.let {
                    selectedAmortizationId = it
                }
            },
            label = R.string.feature_loan_amortization,
            options = loanTemplate.amortizationTypeOptions.map { it.value.toString() },
            readOnly = true,
        )

        MifosTextFieldDropdown(
            value = selectedInterestCalculationPeriod,
            onValueChanged = { selectedInterestCalculationPeriod = it },
            onOptionSelected = { index, value ->
                selectedInterestCalculationPeriod = value
                loanTemplate.interestCalculationPeriodTypeOptions[index].id?.let {
                    selectedInterestCalculationPeriodId = it
                }
            },
            label = R.string.feature_loan_interest_calculation_period,
            options = loanTemplate.interestCalculationPeriodTypeOptions.map { it.value.toString() },
            readOnly = true,
        )

        MifosTextFieldDropdown(
            value = selectedRepaymentStrategy,
            onValueChanged = { selectedRepaymentStrategy = it },
            onOptionSelected = { index, value ->
                selectedRepaymentStrategy = value
                loanTemplate.transactionProcessingStrategyOptions[index].id?.let {
                    selectedRepaymentStrategyId = it
                }
            },
            label = R.string.feature_loan_repayment_strategy,
            options = loanTemplate.transactionProcessingStrategyOptions.map { it.name.toString() },
            readOnly = true,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = selectedCalculateExactDaysIn,
                onCheckedChange = {
                    selectedCalculateExactDaysIn = selectedCalculateExactDaysIn.not()
                },
            )
            Text(text = stringResource(id = R.string.feature_loan_calculate_interest_for_exact_days_in))
        }

        MifosTextFieldDropdown(
            value = selectedInterestTypeMethod,
            onValueChanged = { selectedInterestTypeMethod = it },
            onOptionSelected = { index, value ->
                selectedInterestTypeMethod = value
                loanTemplate.interestTypeOptions[index].id?.let {
                    selectedInterestTypeMethodId = it
                }
            },
            label = R.string.feature_loan_interest_type_method,
            options = loanTemplate.interestTypeOptions.map { it.value.toString() },
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val loadPayload = LoansPayload().apply {
                    allowPartialPeriodInterestCalcualtion = selectedCalculateExactDaysIn
                    amortizationType = selectedAmortizationId
                    clientId = clientsId
                    dateFormat = "dd MMMM yyyy"
                    expectedDisbursementDate =
                        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                            disbursementDate,
                        )
                    interestCalculationPeriodType = selectedInterestCalculationPeriodId
                    loanType = "individual"
                    locale = "en"
                    numberOfRepayments = numberOfRepayment.toInt()
                    principal = principalAmount.toDouble()
                    productId = selectedLoanProductId
                    repaymentEvery = repaidEvery.toInt()
                    submittedOnDate =
                        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(submissionDate)
                    loanPurposeId = selectedLoanPurposeId
                    loanTermFrequency = loanTerms.toInt()
                    loanTermFrequencyType = loanTermsTypeFrequency
                    repaymentFrequencyType = loanTermsTypeFrequency
                    repaymentFrequencyDayOfWeekType = repaidEveryTypeFrequency
                    repaymentFrequencyNthDayType = repaidEveryTypeFrequency
                    transactionProcessingStrategyId = selectedRepaymentStrategyId
                    fundId = selectedFundId
                    interestType = selectedInterestTypeMethodId
                    loanOfficerId = selectedLoanOfficerId
                    linkAccountId = selectedLinkSavingsId
                    interestRatePerPeriod = nominal.toDouble()
                }
                if (loanTemplate.dataTables.size > 0) {
                    dataTable(loanTemplate.dataTables, loadPayload)
                } else {
                    createLoanAccount(loadPayload)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(44.dp)
                .padding(start = 16.dp, end = 16.dp),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
            ),
        ) {
            Text(text = stringResource(id = R.string.feature_loan_submit), fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private class LoanAccountUiStateProvider : PreviewParameterProvider<LoanAccountUiState> {

    override val values: Sequence<LoanAccountUiState>
        get() = sequenceOf(
            LoanAccountUiState.AllLoan(sampleLoanList),
            LoanAccountUiState.Error(R.string.feature_loan_application),
            LoanAccountUiState.Loading,
            LoanAccountUiState.LoanAccountCreatedSuccessfully,
        )
}

@Preview(showBackground = true)
@Composable
private fun LoanAccountScreenPreview(
    @PreviewParameter(LoanAccountUiStateProvider::class) state: LoanAccountUiState,
) {
    LoanAccountScreen(
        clientId = 1,
        state = state,
        loanAccountTemplateState = LoanTemplate(),
        onBackPressed = {},
        onRetry = {},
        onLoanProductSelected = {},
        createLoanAccount = {},
        dataTable = { _, _ -> },
        fetchTemplate = {},
    )
}

val sampleLoanList = List(10) {
    LoanProducts(name = "Loan $it", id = it)
}
