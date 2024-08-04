package com.mifos.feature.loan.loan_repayment

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.objects.PaymentTypeOption
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.feature.loan.R
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Pronay Sarker on 16/07/2024 (2:14 PM)
 */
@Composable
fun LoanRepaymentScreen(
    loanId: Int,
    clientName: String,
    loanAccountNumber: String,
    amountInArrears: Double?,
    loanProductName: String,
    navigateBack: () -> Unit
) {
    val viewmodel: LoanRepaymentViewModel = hiltViewModel()
    val uiState by viewmodel.loanRepaymentUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewmodel.loanId = loanId
        viewmodel.clientName = clientName
        viewmodel.loanAccountNumber = loanAccountNumber
        viewmodel.amountInArrears = amountInArrears
        viewmodel.loanProductName = loanProductName
        viewmodel.checkDatabaseLoanRepaymentByLoanId()
    }

    LoanRepaymentScreen(
        loanId = viewmodel.loanId,
        clientName = viewmodel.clientName,
        loanProductName = viewmodel.loanProductName,
        amountInArrears = viewmodel.amountInArrears,
        loanAccountNumber = viewmodel.loanAccountNumber,
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = { viewmodel.checkDatabaseLoanRepaymentByLoanId() },
        submitPayment = {
            viewmodel.submitPayment(it)
        },
        onLoanRepaymentDoesNotExistInDatabase = {
            viewmodel.loanLoanRepaymentTemplate()
        }
    )
}

@Composable
fun LoanRepaymentScreen(
    loanId: Int,
    clientName: String,
    loanProductName: String,
    amountInArrears: Double?,
    loanAccountNumber: String,
    uiState: LoanRepaymentUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
    submitPayment: (request: LoanRepaymentRequest) -> Unit,
    onLoanRepaymentDoesNotExistInDatabase: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        onBackPressed = navigateBack,
        title = stringResource(id = R.string.feature_loan_loan_repayment),
        icon = MifosIcons.arrowBack,
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            when (uiState) {
                is LoanRepaymentUiState.ShowError -> {
                    MifosSweetError(message = context.resources.getString(uiState.message)) {
                        onRetry()
                    }
                }

                is LoanRepaymentUiState.ShowLoanRepayTemplate -> {
                    LoanRepaymentContent(
                        loanId = loanId,
                        loanAccountNumber = loanAccountNumber,
                        clientName = clientName,
                        loanProductName = loanProductName,
                        amountInArrears = amountInArrears,
                        loanRepaymentTemplate = uiState.loanRepaymentTemplate,
                        navigateBack = navigateBack,
                        submitPayment = submitPayment
                    )
                }

                LoanRepaymentUiState.ShowLoanRepaymentDoesNotExistInDatabase -> {
                    onLoanRepaymentDoesNotExistInDatabase.invoke()
                }

                LoanRepaymentUiState.ShowLoanRepaymentExistInDatabase -> {
                    AlertDialog(onDismissRequest = { }, confirmButton = {
                        TextButton(onClick = { navigateBack.invoke() }) {
                            Text(text = stringResource(id = R.string.feature_loan_dialog_action_ok))
                        }
                    },
                        title = {
                            Text(
                                text = stringResource(id = R.string.feature_loan_sync_previous_transaction),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        text = { Text(text = stringResource(id = R.string.feature_loan_dialog_message_sync_transaction)) })
                }

                is LoanRepaymentUiState.ShowPaymentSubmittedSuccessfully -> {
                    if (uiState.loanRepaymentResponse != null) {
                        Toast.makeText(
                            context,
                            "Payment Successful, Transaction ID = " + uiState.loanRepaymentResponse.resourceId,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    navigateBack.invoke()
                }

                LoanRepaymentUiState.ShowProgressbar -> {
                    MifosCircularProgress()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanRepaymentContent(
    loanId: Int,
    clientName: String,
    loanProductName: String,
    amountInArrears: Double?,
    loanAccountNumber: String,
    loanRepaymentTemplate: LoanRepaymentTemplate,
    navigateBack: () -> Unit,
    submitPayment: (request: LoanRepaymentRequest) -> Unit
) {
    var paymentType by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var additionalPayment by rememberSaveable { mutableStateOf("") }
    var fees by rememberSaveable { mutableStateOf("") }
    var paymentTypeId by rememberSaveable { mutableIntStateOf(0) }

    var repaymentDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = repaymentDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showConfirmationDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showConfirmationDialog) {
        ShowLoanRepaymentConfirmationDialog(
            onDismiss = { showConfirmationDialog = false },
            loanAccountNumber = loanAccountNumber,
            paymentTypeId = paymentTypeId.toString(),
            repaymentDate = repaymentDate,
            paymentType = paymentType,
            amount = amount,
            additionalPayment = additionalPayment,
            fees = fees,
            total = calculateTotal(
                fees = fees,
                amount = amount,
                additionalPayment = additionalPayment
            ).toString(),
            context = context,
            submitPayment = submitPayment,
        )
    }

    if (showDatePickerDialog) {
        /* Had this TODO in the fragment (keeping as it is, implement todo if needed)
             Add Validation to make sure :
            2. Date Entered is not greater than Date Today i.e Date is not in future
         */
        DatePickerDialog(
            onDismissRequest = {
                showDatePickerDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            repaymentDate = it
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
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            text = clientName
        )

        HorizontalDivider(modifier = Modifier.padding(top = 10.dp))

        FarApartTextItem(title = loanProductName, value = loanId.toString())
        FarApartTextItem(
            title = stringResource(id = R.string.feature_loan_loan_in_arrears),
            value = amountInArrears?.toString() ?: ""
        )
        FarApartTextItem(
            title = stringResource(id = R.string.feature_loan_loan_amount_due),
            value = loanRepaymentTemplate.amount?.toString() ?: ""
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

        MifosDatePickerTextField(
            modifier = Modifier.fillMaxWidth(),
            value = SimpleDateFormat(
                "dd MMMM yyyy",
                Locale.getDefault()
            ).format(
                repaymentDate
            ), label = R.string.feature_loan_repayment_date
        ) {
            showDatePickerDialog = true
        }

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            modifier = Modifier.fillMaxWidth(),
            value = paymentType,
            onValueChanged = { paymentType = it },
            onOptionSelected = { index, value ->
                paymentType = value
                paymentTypeId = loanRepaymentTemplate.paymentTypeOptions?.get(index)?.id ?: 0
            },
            label = R.string.feature_loan_payment_type,
            options = if (loanRepaymentTemplate.paymentTypeOptions != null) loanRepaymentTemplate.paymentTypeOptions!!.map { it.name } else listOf(),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = amount,
            onValueChange = {
                amount = it
            },
            label = stringResource(id = R.string.feature_loan_amount),
            error = null,
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = additionalPayment,
            onValueChange = {
                additionalPayment = it
            },
            label = stringResource(id = R.string.feature_loan_additional_payment),
            error = null,
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = fees,
            onValueChange = {
                fees = it
            },
            label = stringResource(id = R.string.feature_loan_loan_fees),
            error = null,
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = calculateTotal(
                fees = fees,
                amount = amount,
                additionalPayment = additionalPayment
            ).toString(),
            onValueChange = { },
            label = stringResource(id = R.string.feature_loan_total),
            error = null,
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier
                    .heightIn(46.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                ),
                onClick = { navigateBack.invoke() }) {
                Text(text = stringResource(id = R.string.feature_loan_cancel))
            }

            Button(
                modifier = Modifier
                    .heightIn(46.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                ),
                onClick = {
                    if (isAllFieldsValid(
                            amount = amount,
                            additionalPayment = additionalPayment,
                            fees = fees,
                            paymentType = paymentType,
                            context = context
                        )
                    ) {
                        showConfirmationDialog = true
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.feature_loan_review_payment))
            }
        }
    }
}

@Composable
private fun FarApartTextItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
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
fun ShowLoanRepaymentConfirmationDialog(
    onDismiss: () -> Unit,
    loanAccountNumber: String,
    paymentTypeId: String,
    repaymentDate: Long,
    paymentType: String,
    amount: String,
    additionalPayment: String,
    fees: String,
    total: String,
    context: Context,
    submitPayment: (request: LoanRepaymentRequest) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    if (Network.isOnline(context)) {
                        val request = LoanRepaymentRequest()

                        request.accountNumber = loanAccountNumber
                        request.paymentTypeId = paymentTypeId
                        request.dateFormat = "dd MM yyyy"
                        request.locale = "en"
                        request.transactionAmount = total
                        request.transactionDate = SimpleDateFormat(
                            "dd MMMM yyyy",
                            Locale.getDefault()
                        ).format(
                            repaymentDate
                        )

                        submitPayment.invoke(request)
                    } else {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.feature_loan_error_not_connected_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                Text(text = stringResource(id = R.string.feature_loan_dialog_action_pay_now))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }) {
                Text(text = stringResource(id = R.string.feature_loan_cancel))
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.feature_loan_review_payment),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                Text(text = stringResource(id = R.string.feature_loan_account_number) + " : " + loanAccountNumber)
                Text(
                    text = stringResource(id = R.string.feature_loan_repayment_date) + " : " + SimpleDateFormat(
                        "dd MMMM yyyy",
                        Locale.getDefault()
                    ).format(repaymentDate)
                )
                Text(text = stringResource(id = R.string.feature_loan_payment_type) + " : " + paymentType)
                Text(text = stringResource(id = R.string.feature_loan_amount) + " : " + amount)
                Text(text = stringResource(id = R.string.feature_loan_additional_payment) + " : " + additionalPayment)
                Text(text = stringResource(id = R.string.feature_loan_loan_fees) + " : " + fees)
                Text(text = stringResource(id = R.string.feature_loan_total) + " : " + total)
            }
        }
    )
}

/**
 * Calculating the Total of the  Amount, Additional Payment and Fee
 * @return Total of the Amount + Additional Payment + Fees
 */
fun calculateTotal(
    fees: String,
    amount: String,
    additionalPayment: String
): Double {
    fun setValue(value: String): Double {
        if (value.isEmpty()) {
            return 0.0
        }
        return try {
            value.toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }

    val feesValue = setValue(fees)
    val amountValue = setValue(amount)
    val additionalPaymentValue = setValue(additionalPayment)

    return feesValue + amountValue + additionalPaymentValue
}

fun isAllFieldsValid(
    amount: String,
    additionalPayment: String,
    fees: String,
    paymentType: String,
    context: Context
): Boolean {
    return when {
        amount.isNotEmpty() && additionalPayment.isNotEmpty() && fees.isNotEmpty() && paymentType.isNotEmpty() -> {
            true
        }

        else -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.feature_loan_repayment_make_sure_every_field_has_a_value),
                Toast.LENGTH_SHORT
            ).show()

            false
        }
    }
}

class LoanRepaymentScreenPreviewProvider :
    PreviewParameterProvider<LoanRepaymentUiState> {

    private val samplePaymentTypeOptions = mutableListOf(
        PaymentTypeOption(
            id = 1,
            name = "Cash",
            description = "Cash payment",
            isCashPayment = true,
            position = 1
        )
    )

    private val sampleLoanRepaymentTemplate = LoanRepaymentTemplate(
        loanId = 101,
        date = mutableListOf(2024, 7, 15),
        amount = 1000.0,
        principalPortion = 800.0,
        interestPortion = 150.0,
        feeChargesPortion = 30.0,
        penaltyChargesPortion = 20.0,
        paymentTypeOptions = samplePaymentTypeOptions
    )

    override val values: Sequence<LoanRepaymentUiState>
        get() = sequenceOf(
            LoanRepaymentUiState.ShowLoanRepaymentExistInDatabase,
            LoanRepaymentUiState.ShowLoanRepayTemplate(sampleLoanRepaymentTemplate),
            LoanRepaymentUiState.ShowError(R.string.feature_loan_failed_to_load_loan_repayment),
            LoanRepaymentUiState.ShowLoanRepaymentDoesNotExistInDatabase,
            LoanRepaymentUiState.ShowProgressbar,
            LoanRepaymentUiState.ShowPaymentSubmittedSuccessfully(LoanRepaymentResponse()),
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewLoanRepaymentScreen(
    @PreviewParameter(LoanRepaymentScreenPreviewProvider::class) loanRepaymentUiState: LoanRepaymentUiState
) {
    LoanRepaymentScreen(
        loanId = 2,
        clientName = "Ben Kiko",
        loanProductName = "Product name",
        amountInArrears = 23.333,
        loanAccountNumber = 25.toString(),
        uiState = loanRepaymentUiState,
        navigateBack = {},
        onRetry = {},
        submitPayment = {},
        onLoanRepaymentDoesNotExistInDatabase = {}
    )
}
