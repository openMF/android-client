@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.loan.group_loan_account

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
import com.mifos.core.data.GroupLoanPayload
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.objects.organisation.LoanProducts
import com.mifos.core.objects.templates.loans.GroupLoanTemplate
import com.mifos.feature.loan.R
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun GroupLoanAccountScreen(
    groupId: Int,
    onBackPressed: () -> Unit
) {
    val viewModel: GroupLoanAccountViewModel = hiltViewModel()
    val state by viewModel.groupLoanAccountUiState.collectAsStateWithLifecycle()
    val loanProducts by viewModel.loanProducts.collectAsStateWithLifecycle()

    LaunchedEffect(loanProducts) {
        if (loanProducts.isNotEmpty()) {
            loanProducts[0].id?.let { viewModel.loadGroupLoansAccountTemplate(groupId, it) }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAllLoans()
    }

    GroupLoanAccountScreen(
        groupId = groupId,
        state = state,
        onBackPressed = onBackPressed,
        loanProducts = loanProducts,
        onRetry = {
            viewModel.loadAllLoans()
        },
        onLoanProductSelected = { productId ->
            viewModel.loadGroupLoansAccountTemplate(groupId, productId)
        },
        createLoanAccount = { loansPayload ->
            viewModel.createGroupLoanAccount(loansPayload)
        }
    )
}

@Composable
fun GroupLoanAccountScreen(
    groupId: Int,
    state: GroupLoanAccountUiState,
    loanProducts: List<LoanProducts>,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    onLoanProductSelected: (Int) -> Unit,
    createLoanAccount: (GroupLoanPayload) -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_loan_application),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            when (state) {
                is GroupLoanAccountUiState.GroupLoanAccountTemplate -> {
                    GroupLoanAccountContent(
                        groupId = groupId,
                        productLoans = loanProducts,
                        loanTemplate = state.groupLoanTemplate,
                        onLoanProductSelected = onLoanProductSelected,
                        createLoanAccount = createLoanAccount,
                    )
                }

                is GroupLoanAccountUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {
                    onRetry()
                }

                is GroupLoanAccountUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        MifosCircularProgress()
                    }
                }

                is GroupLoanAccountUiState.GroupLoanAccountCreatedSuccessfully -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(id = R.string.feature_loan_account_created_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    onBackPressed()
                }
            }
        }
    }
}

@Composable
fun GroupLoanAccountContent(
    groupId: Int,
    productLoans: List<LoanProducts>,
    loanTemplate: GroupLoanTemplate,
    onLoanProductSelected: (Int) -> Unit,
    createLoanAccount: (GroupLoanPayload) -> Unit
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
        }
    )

    var showDisbursementDatePicker by rememberSaveable { mutableStateOf(false) }
    var disbursementDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    val disbursementDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = disbursementDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
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
                    }
                ) { Text(stringResource(id = R.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSubmissionDatePicker = false
                    }
                ) { Text(stringResource(id = R.string.feature_loan_cancel)) }
            }
        )
        {
            DatePicker(state = submissionDatePickerState)
        }
    }

    if (showDisbursementDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDisbursementDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDisbursementDatePicker = false
                        disbursementDatePickerState.selectedDateMillis?.let {
                            disbursementDate = it
                        }
                    }
                ) { Text(stringResource(id = R.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSubmissionDatePicker = false
                    }
                ) { Text(stringResource(id = R.string.feature_loan_cancel)) }
            }
        )
        {
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
            readOnly = true
        )

        loanTemplate.loanPurposeOptions?.let { loanPurposeOptions ->
            MifosTextFieldDropdown(
                value = selectedLoanPurpose,
                onValueChanged = { selectedLoanPurpose = it },
                onOptionSelected = { index, value ->
                    selectedLoanPurpose = value
                    loanPurposeOptions[index].id?.let {
                        selectedLoanPurposeId = it
                    }
                },
                label = R.string.feature_loan_purpose,
                options = loanPurposeOptions.map { it.name.toString() },
                readOnly = true
            )
        }

        loanTemplate.loanOfficerOptions?.let { loanOfficerOptions ->
            MifosTextFieldDropdown(
                value = selectedLoanOfficer,
                onValueChanged = { selectedLoanOfficer = it },
                onOptionSelected = { index, value ->
                    selectedLoanOfficer = value
                    loanOfficerOptions[index].id?.let {
                        selectedLoanOfficerId = it
                    }
                },
                label = R.string.feature_loan_officer,
                options = loanOfficerOptions.map { it.displayName.toString() },
                readOnly = true
            )
        }

        loanTemplate.fundOptions?.let { fundOptions ->
            MifosTextFieldDropdown(
                value = selectedFund,
                onValueChanged = { selectedFund = it },
                onOptionSelected = { index, value ->
                    selectedFund = value
                    fundOptions[index].id?.let {
                        selectedFundId = it
                    }
                },
                label = R.string.feature_loan_fund,
                options = fundOptions.map { it.name.toString() },
                readOnly = true
            )
        }

        MifosDatePickerTextField(
            value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                submissionDate
            ),
            label = R.string.feature_loan_submission_date,
            openDatePicker = {
                showSubmissionDatePicker = true
            }
        )

        MifosDatePickerTextField(
            value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                disbursementDate
            ),
            label = R.string.feature_loan_disbursed_date,
            openDatePicker = {
                showDisbursementDatePicker = true
            }
        )

        MifosOutlinedTextField(
            value = externalId,
            label = stringResource(id = R.string.feature_loan_external_id),
            onValueChange = {
                externalId = it
            },
            error = null,
            keyboardType = KeyboardType.Text
        )

        MifosOutlinedTextField(
            value = principalAmount,
            label = stringResource(id = R.string.feature_loan_principal),
            onValueChange = {
                principalAmount = it
            },
            error = null,
            keyboardType = KeyboardType.Number
        )

        MifosOutlinedTextField(
            value = numberOfRepayment,
            label = stringResource(id = R.string.feature_loan_number_of_repayments),
            onValueChange = {
                numberOfRepayment = it
            },
            error = null,
            keyboardType = KeyboardType.Number
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
                keyboardType = KeyboardType.Number
            )
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.feature_loan_per_month)
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
                keyboardType = KeyboardType.Number
            )

            loanTemplate.repaymentFrequencyDaysOfWeekTypeOptions?.let { repaymentFrequencyDaysOfWeekTypeOptions ->
                MifosTextFieldDropdown(
                    modifier = Modifier
                        .width(164.dp)
                        .padding(start = 8.dp, end = 16.dp),
                    value = repaidEveryType,
                    onValueChanged = { repaidEveryType = it },
                    onOptionSelected = { index, value ->
                        repaidEveryType = value
                        repaymentFrequencyDaysOfWeekTypeOptions[index].id?.let {
                            repaidEveryTypeFrequency = it
                        }
                    },
                    label = R.string.feature_loan_term,
                    options = repaymentFrequencyDaysOfWeekTypeOptions.map { it.value.toString() },
                    readOnly = true
                )
            }
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
                keyboardType = KeyboardType.Number
            )

            loanTemplate.termFrequencyTypeOptions?.let { termFrequencyTypeOptions ->
                MifosTextFieldDropdown(
                    modifier = Modifier
                        .width(164.dp)
                        .padding(start = 8.dp, end = 16.dp),
                    value = loanTermsType,
                    onValueChanged = { loanTermsType = it },
                    onOptionSelected = { index, value ->
                        loanTermsType = value
                        termFrequencyTypeOptions[index].id?.let {
                            loanTermsTypeFrequency = it
                        }
                    },
                    label = R.string.feature_loan_term,
                    options = termFrequencyTypeOptions.map { it.value.toString() },
                    readOnly = true
                )
            }
        }

        loanTemplate.amortizationTypeOptions?.let { amortizationTypeOptions ->
            MifosTextFieldDropdown(
                value = selectedAmortization,
                onValueChanged = { selectedAmortization = it },
                onOptionSelected = { index, value ->
                    selectedAmortization = value
                    amortizationTypeOptions[index].id?.let {
                        selectedAmortizationId = it
                    }
                },
                label = R.string.feature_loan_amortization,
                options = amortizationTypeOptions.map { it.value.toString() },
                readOnly = true
            )
        }

        loanTemplate.interestCalculationPeriodTypeOptions?.let { interestCalculationPeriodTypeOptions ->
            MifosTextFieldDropdown(
                value = selectedInterestCalculationPeriod,
                onValueChanged = { selectedInterestCalculationPeriod = it },
                onOptionSelected = { index, value ->
                    selectedInterestCalculationPeriod = value
                    interestCalculationPeriodTypeOptions[index].id?.let {
                        selectedInterestCalculationPeriodId = it
                    }
                },
                label = R.string.feature_loan_interest_calculation_period,
                options = interestCalculationPeriodTypeOptions.map { it.value.toString() },
                readOnly = true
            )
        }

        loanTemplate.transactionProcessingStrategyOptions?.let { transactionProcessingStrategyOptions ->
            MifosTextFieldDropdown(
                value = selectedRepaymentStrategy,
                onValueChanged = { selectedRepaymentStrategy = it },
                onOptionSelected = { index, value ->
                    selectedRepaymentStrategy = value
                    transactionProcessingStrategyOptions[index].id?.let {
                        selectedRepaymentStrategyId = it
                    }
                },
                label = R.string.feature_loan_repayment_strategy,
                options = transactionProcessingStrategyOptions.map { it.name.toString() },
                readOnly = true
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = selectedCalculateExactDaysIn,
                onCheckedChange = {
                    selectedCalculateExactDaysIn = selectedCalculateExactDaysIn.not()
                })
            Text(text = stringResource(id = R.string.feature_loan_calculate_interest_for_exact_days_in))
        }

        loanTemplate.interestTypeOptions?.let { interestTypeOptions ->
            MifosTextFieldDropdown(
                value = selectedInterestTypeMethod,
                onValueChanged = { selectedInterestTypeMethod = it },
                onOptionSelected = { index, value ->
                    selectedInterestTypeMethod = value
                    interestTypeOptions[index].id?.let {
                        selectedInterestTypeMethodId = it
                    }
                },
                label = R.string.feature_loan_interest_type_method,
                options = interestTypeOptions.map { it.value.toString() },
                readOnly = true
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val loadPayload = GroupLoanPayload().apply {
                    isAllowPartialPeriodInterestCalcualtion = selectedCalculateExactDaysIn
                    amortizationType = selectedAmortizationId
                    this.groupId = groupId
                    dateFormat = "dd MMMM yyyy"
                    expectedDisbursementDate =
                        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                            disbursementDate
                        )
                    interestCalculationPeriodType = selectedInterestCalculationPeriodId
                    loanType = "individual"
                    locale = "en"
                    numberOfRepayments = numberOfRepayment
                    principal = principalAmount
                    productId = selectedLoanProductId
                    repaymentEvery = repaidEvery
                    submittedOnDate =
                        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(submissionDate)
                    loanPurposeId = selectedLoanPurposeId
                    loanTermFrequency = loanTerms.toInt()
                    loanTermFrequencyType = loanTermsTypeFrequency
                    repaymentFrequencyType = loanTermsTypeFrequency
                    repaymentFrequencyDayOfWeekType = repaidEveryTypeFrequency
                    repaymentFrequencyNthDayType = repaidEveryTypeFrequency
                    transactionProcessingStrategyId = selectedRepaymentStrategyId
                    interestType = selectedInterestTypeMethodId
                    interestRatePerPeriod = nominal.toDouble()
                }
                createLoanAccount(loadPayload)

            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(44.dp)
                .padding(start = 16.dp, end = 16.dp),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
            )
        ) {
            Text(text = stringResource(id = R.string.feature_loan_submit), fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

class GroupLoanAccountUiStateProvider : PreviewParameterProvider<GroupLoanAccountUiState> {

    override val values: Sequence<GroupLoanAccountUiState>
        get() = sequenceOf(
            GroupLoanAccountUiState.Error(R.string.feature_loan_failed_to_create_loan_account),
            GroupLoanAccountUiState.Loading,
            GroupLoanAccountUiState.GroupLoanAccountCreatedSuccessfully
        )

}

@Preview(showBackground = true)
@Composable
private fun GroupLoanAccountScreenPreview(
    @PreviewParameter(GroupLoanAccountUiStateProvider::class) state: GroupLoanAccountUiState
) {
    GroupLoanAccountScreen(
        groupId = 0,
        state = state,
        loanProducts = emptyList(),
        onBackPressed = {},
        onRetry = {},
        onLoanProductSelected = {},
        createLoanAccount = {}
    )
}

val sampleLoanProducts = List(10) {
    LoanProducts(name = "mifos $it")
}