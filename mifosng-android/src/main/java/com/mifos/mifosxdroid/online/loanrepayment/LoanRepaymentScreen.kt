package com.mifos.mifosxdroid.online.loanrepayment

import android.util.Log
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
import androidx.compose.material.AlertDialog
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
import com.google.gson.Gson
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
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.online.loanaccountapproval.LoanAccountApprovalScreen
import com.mifos.mifosxdroid.online.loanaccountapproval.LoanAccountApprovalUiState
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Pronay Sarker on 16/07/2024 (2:14 PM)
 */
@Composable
fun LoanRepaymentScreen(
    navigateBack: () -> Unit
) {
    val viewmodel: LoanRepaymentViewModel = hiltViewModel()
    val uiState by viewmodel.loanRepaymentUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewmodel.checkDatabaseLoanRepaymentByLoanId()
    }

    LoanRepaymentScreen(
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
        title = stringResource(id = R.string.loan_repayment),
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
    clientName: String,
    loanProductName: String,
    amountInArrears: Double?,
    loanAccountNumber: String,
    loanRepaymentTemplate: LoanRepaymentTemplate,
    navigateBack: () -> Unit,
    submitPayment: (request: LoanRepaymentRequest) -> Unit
) {
    var showDatePickerDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var paymentType by rememberSaveable {
        mutableStateOf("")
    }
    var amount by rememberSaveable {
        mutableStateOf("0")
    }
    var additionalPayment by rememberSaveable {
        mutableStateOf("0")
    }
    var fees by rememberSaveable {
        mutableStateOf("0")
    }
    var paymentTypeId by rememberSaveable {
        mutableIntStateOf(0)
    }

    fun calculateTotal(): Double {
        return try {
            fees.toDouble() + amount.toDouble() + additionalPayment.toDouble()
        } catch (nfe: NumberFormatException) {
            0.0
        }
    }

    val total by rememberSaveable {
        mutableStateOf(calculateTotal().toString())
    }
    var repaymentDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
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
    var showLoanRepaymentConfirmationDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showLoanRepaymentConfirmationDialog) {
        ShowLoanRepaymentConfirmationDialog(
            dialogValue = showLoanRepaymentConfirmationDialog,
            onDismiss = { showLoanRepaymentConfirmationDialog = false },
            onConfirm = {
                if (Network.isOnline(context)) {
                    val request = LoanRepaymentRequest()

                    request.accountNumber = loanAccountNumber
                    request.paymentTypeId = paymentTypeId.toString()
                    request.dateFormat = "dd MM yyyy"
                    request.locale = "en"
                    request.transactionAmount = calculateTotal().toString()
                    request.transactionDate = SimpleDateFormat(
                        "dd MMMM yyyy",
                        Locale.getDefault()
                    ).format(
                        repaymentDate
                    )

                    submitPayment.invoke(request)

                    val builtRequest = Gson().toJson(request)
                    Log.i("LOG_TAG", builtRequest)
                } else {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.error_not_connected_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            loanAccountNumber = loanAccountNumber,
            repaymentDate = repaymentDate.toString(),
            paymentType = paymentType,
            amount = amount,
            additionalPayment = additionalPayment,
            fees = fees,
            total = total
        )
    }

    if (showDatePickerDialog) {
        /* Had this TODO in the fragment -
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
                ) { Text(stringResource(id = R.string.select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false
                    }
                ) { Text(stringResource(id = R.string.cancel)) }
            }
        )
        {
            DatePicker(state = datePickerState)
        }
    }

    fun isAllFieldsValid(): Boolean {
        return when {
            amount.isNotEmpty() && additionalPayment.isNotEmpty() && fees.isNotEmpty() && paymentType.isNotEmpty() -> {
                true
            }

            else -> {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.loan_repayment_make_sure_every_field_has_a_value),
                    Toast.LENGTH_SHORT
                ).show()

                false
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            text = clientName
        )

        HorizontalDivider(modifier = Modifier.padding(top = 10.dp))

        FarApartTextItem(
            title = loanRepaymentTemplate.type?.toString() ?: "Loan type",
            value = "57"
        )
        FarApartTextItem(
            title = stringResource(id = R.string.loan_in_arrears),
            value = amountInArrears?.toString() ?: ""
        )
        FarApartTextItem(
            title = stringResource(id = R.string.loan_amount_due),
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
            ), label = R.string.repayment_date
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
            label = R.string.payment_type,
            options = if (loanRepaymentTemplate.paymentTypeOptions != null) loanRepaymentTemplate.paymentTypeOptions!!.map { it.name } else listOf(),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = amount,
            onValueChange = {
                amount = it
                if (amount.isEmpty()) {
                    amount = "0"
                }
            },
            label = stringResource(id = R.string.amount),
            error = null,
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = additionalPayment,
            onValueChange = {
                additionalPayment = it
                if (additionalPayment.isEmpty()) {
                    additionalPayment = "0"
                }
            },
            label = stringResource(id = R.string.additional_payment),
            error = null,
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = fees,
            onValueChange = {
                fees = it
                if (fees.isEmpty()) {
                    fees = "0"
                }
            },
            label = stringResource(id = R.string.loan_fees),
            error = null,
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = calculateTotal().toString(),
            onValueChange = { },
            label = stringResource(id = R.string.total),
            error = null,

        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier
                    .heightIn(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                ),
                onClick = { navigateBack.invoke() }) {
                Text(text = stringResource(id = R.string.cancel))
            }

            Button(
                modifier = Modifier
                    .heightIn(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                ),
                onClick = {
                    if (isAllFieldsValid()) {
                        showLoanRepaymentConfirmationDialog = true
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.review_payment))
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
    dialogValue: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    loanAccountNumber: String,
    repaymentDate: String,
    paymentType: String,
    amount: String,
    additionalPayment: String,
    fees: String,
    total: String
) {
    val showDialog by rememberSaveable {
        mutableStateOf(dialogValue)
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(
                    onClick = { onConfirm() }) {
                    Text(text = stringResource(id = R.string.dialog_action_pay_now))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismiss() }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            title = { Text(text = stringResource(id = R.string.review_payment)) },
            text = {
                Column {
                    Text(text = stringResource(id = R.string.account_number) + " : " + loanAccountNumber)
                    Text(text = stringResource(id = R.string.repayment_date) + " : " + repaymentDate)
                    Text(text = stringResource(id = R.string.payment_type) + " : " + paymentType)
                    Text(text = stringResource(id = R.string.amount) + " : " + amount)
                    Text(text = stringResource(id = R.string.additional_payment) + " : " + additionalPayment)
                    Text(text = stringResource(id = R.string.loan_fees) + " : " + fees)
                    Text(text = stringResource(id = R.string.total) + " : " + total)
                }
            }
        )
    }
}


class LoanRepaymentScreenPreviewProvider :
    PreviewParameterProvider<LoanRepaymentUiState> {
    override val values: Sequence<LoanRepaymentUiState>
        get() = sequenceOf(
            LoanRepaymentUiState.ShowProgressbar,
//            LoanRepaymentUiState.ShowLoanRepayTemplate,
//            LoanRepaymentUiState.ShowLoanRepaymentExistInDatabase,
//            LoanRepaymentUiState.ShowError,
//            LoanRepaymentUiState.ShowPaymentSubmittedSuccessfully
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewLoanAccountApprovalScreen(
    @PreviewParameter(LoanRepaymentScreenPreviewProvider::class) loanAccountApprovalUiState: LoanAccountApprovalUiState
) {
    LoanAccountApprovalScreen(
        uiState = loanAccountApprovalUiState,
        loanWithAssociations = LoanWithAssociations(),
        navigateBack = { }) {
    }
}
