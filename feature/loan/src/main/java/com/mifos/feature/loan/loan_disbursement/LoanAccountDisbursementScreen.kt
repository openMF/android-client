package com.mifos.feature.loan.loan_disbursement

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Network
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.objects.PaymentTypeOption
import com.mifos.core.objects.accounts.loan.LoanDisbursement
import com.mifos.core.objects.templates.loans.LoanTransactionTemplate
import com.mifos.feature.loan.R
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Pronay Sarker on 08/07/2024 (8:04 PM)
 */

@Composable
fun LoanAccountDisbursementScreen(
    loanId: Int,
    navigateBack: () -> Unit,
) {
    val viewmodel: LoanAccountDisbursementViewModel = hiltViewModel()
    val uiState by viewmodel.loanAccountDisbursementUiState.collectAsStateWithLifecycle()

    LaunchedEffect(loanId) {
        viewmodel.loanId = loanId
    }

    LaunchedEffect(key1 = Unit) {
        viewmodel.loadLoanTemplate()
    }

    LoanAccountDisbursementScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = { viewmodel.loadLoanTemplate() },
        onDisburseLoan = {
            viewmodel.disburseLoan(it)
        }
    )
}

@Composable
fun LoanAccountDisbursementScreen(
    uiState: LoanAccountDisbursementUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
    onDisburseLoan: (loanDisbursement: LoanDisbursement) -> Unit
) {
    val SnackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current

    MifosScaffold(
        snackbarHostState = SnackbarHostState,
        title = stringResource(id = R.string.feature_loan_disburse_loan),
        icon = MifosIcons.arrowBack,
        onBackPressed = navigateBack
    ) {
        Box(
            modifier = Modifier
                .padding(it)
        ) {
            when (uiState) {
                is LoanAccountDisbursementUiState.ShowDisburseLoanSuccessfully -> {
                    Toast.makeText(
                        context,
                        stringResource(id = R.string.feature_loan_loan_disburse_successfully),
                        Toast.LENGTH_LONG
                    ).show()
                    navigateBack.invoke()
                }

                is LoanAccountDisbursementUiState.ShowError -> {
                    MifosSweetError(
                        message = uiState.message,
                        onclick = onRetry
                    )
                }

                is LoanAccountDisbursementUiState.ShowLoanTransactionTemplate -> {
                    LoanAccountDisbursementContent(
                        initialAmount = uiState.loanTransactionTemplate.amount.toString(),
                        paymentTypeOptions = uiState.loanTransactionTemplate.paymentTypeOptions,
                        onDisburseLoan = onDisburseLoan
                    )
                }

                LoanAccountDisbursementUiState.ShowProgressbar -> MifosCircularProgress()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanAccountDisbursementContent(
    initialAmount: String,
    paymentTypeOptions: List<PaymentTypeOption>,
    onDisburseLoan: (loanDisbursement: LoanDisbursement) -> Unit
) {
    var disbursementDate by rememberSaveable {
        mutableLongStateOf(System.currentTimeMillis())
    }
    var note by rememberSaveable {
        mutableStateOf("")
    }
    var amount by rememberSaveable {
        mutableStateOf(initialAmount)
    }
    var selectedPaymentType by rememberSaveable {
        mutableStateOf("")
    }
    var showDatePickerDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var paymentTypeId by rememberSaveable {
        mutableIntStateOf(0)
    }
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = disbursementDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )
    val scrollState = rememberScrollState()

    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePickerDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            disbursementDate = it
                        }
                        showDatePickerDialog = false
                    }
                ) { Text(stringResource(id = R.string.feature_loan_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false
                    }
                ) { Text(stringResource(id = R.string.feature_loan_cancel)) }
            }
        )
        {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                disbursementDate
            ),
            label = R.string.feature_loan_approval_disbursement_date,
            openDatePicker = {
                showDatePickerDialog = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = stringResource(id = R.string.feature_loan_loan_amount_disbursed),
            error = null,
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = selectedPaymentType,
            onValueChanged = { selectedPaymentType = it },
            onOptionSelected = { index, value ->
                selectedPaymentType = value
                paymentTypeId = paymentTypeOptions[index].id
            },
            label = R.string.feature_loan_payment_type,
            options = paymentTypeOptions.map { it.name },
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = stringResource(id = R.string.feature_loan_disbursement_note),
            error = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
            ),
            onClick = {
                if (Network.isOnline(context)) {
                    if (isFieldValid(amount = amount, context = context)) {
                        val date = SimpleDateFormat(
                            "dd MMMM yyyy",
                            Locale.getDefault()
                        ).format(
                            disbursementDate
                        )
                        val loanDisbursement = LoanDisbursement(
                            note = note,
                            paymentId = paymentTypeId,
                            actualDisbursementDate = date,
                            transactionAmount = amount.toDouble()
                        )

                        onDisburseLoan.invoke(loanDisbursement)
                    }
                } else {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.feature_loan_error_network_not_available),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            Text(text = stringResource(id = R.string.feature_loan_submit))
        }
    }
}

fun isFieldValid(amount: String, context: Context): Boolean {
    return when {
        amount.isEmpty() -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.feature_loan_error_amount_can_not_be_empty),
                Toast.LENGTH_SHORT
            ).show()

            false
        }

        !isAmountValid(amount) -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.feature_loan_error_invalid_amount),
                Toast.LENGTH_SHORT
            ).show()

            false
        }

        else -> {
            true
        }
    }
}

fun isAmountValid(amount: String): Boolean {
    return amount.toDoubleOrNull() != null
}

class LoanAccountDisbursementScreenPreviewProvider :
    PreviewParameterProvider<LoanAccountDisbursementUiState> {
    override val values: Sequence<LoanAccountDisbursementUiState>
        get() = sequenceOf(
            LoanAccountDisbursementUiState.ShowProgressbar,
            LoanAccountDisbursementUiState.ShowError("An error occurred"),
            LoanAccountDisbursementUiState.ShowDisburseLoanSuccessfully(null),
            LoanAccountDisbursementUiState.ShowLoanTransactionTemplate(LoanTransactionTemplate())
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewLoanAccountDisbursementScreen(
    @PreviewParameter(LoanAccountDisbursementScreenPreviewProvider::class) loanAccountDisbursementUiState: LoanAccountDisbursementUiState
) {
    LoanAccountDisbursementScreen(
        uiState = loanAccountDisbursementUiState,
        navigateBack = { },
        onRetry = { },
        onDisburseLoan = { }
    )
}