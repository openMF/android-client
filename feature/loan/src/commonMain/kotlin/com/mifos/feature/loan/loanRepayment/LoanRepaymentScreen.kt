/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepayment

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_account_number
import androidclient.feature.loan.generated.resources.feature_loan_additional_payment
import androidclient.feature.loan.generated.resources.feature_loan_amount
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_dialog_action_ok
import androidclient.feature.loan.generated.resources.feature_loan_dialog_action_pay_now
import androidclient.feature.loan.generated.resources.feature_loan_dialog_message_sync_transaction
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan_repayment
import androidclient.feature.loan.generated.resources.feature_loan_loan_amount_due
import androidclient.feature.loan.generated.resources.feature_loan_loan_fees
import androidclient.feature.loan.generated.resources.feature_loan_loan_in_arrears
import androidclient.feature.loan.generated.resources.feature_loan_loan_repayment
import androidclient.feature.loan.generated.resources.feature_loan_payment_success_message
import androidclient.feature.loan.generated.resources.feature_loan_payment_type
import androidclient.feature.loan.generated.resources.feature_loan_repayment_date
import androidclient.feature.loan.generated.resources.feature_loan_review_payment
import androidclient.feature.loan.generated.resources.feature_loan_select_date
import androidclient.feature.loan.generated.resources.feature_loan_sync_previous_transaction
import androidclient.feature.loan.generated.resources.feature_loan_total
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LoanRepaymentScreen(
    navigateBack: () -> Unit,
    viewmodel: LoanRepaymentViewModel = koinViewModel(),
) {
    val uiState by viewmodel.loanRepaymentUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
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
        },
    )
}

@Composable
internal fun LoanRepaymentScreen(
    loanId: Int,
    clientName: String,
    loanProductName: String,
    amountInArrears: Double?,
    loanAccountNumber: String,
    uiState: LoanRepaymentUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
    submitPayment: (request: LoanRepaymentRequestEntity) -> Unit,
    onLoanRepaymentDoesNotExistInDatabase: () -> Unit,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        onBackPressed = navigateBack,
        title = stringResource(Res.string.feature_loan_loan_repayment),
    ) {
        Box(
            modifier = Modifier.padding(it),
        ) {
            when (uiState) {
                is LoanRepaymentUiState.ShowError -> {
                    MifosSweetError(message = stringResource(uiState.message)) {
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
                        submitPayment = submitPayment,
                    )
                }

                LoanRepaymentUiState.ShowLoanRepaymentDoesNotExistInDatabase -> {
                    onLoanRepaymentDoesNotExistInDatabase.invoke()
                }

                LoanRepaymentUiState.ShowLoanRepaymentExistInDatabase -> {
                    AlertDialog(
                        onDismissRequest = { },
                        confirmButton = {
                            TextButton(onClick = { navigateBack.invoke() }) {
                                Text(text = stringResource(Res.string.feature_loan_dialog_action_ok))
                            }
                        },
                        title = {
                            Text(
                                text = stringResource(Res.string.feature_loan_sync_previous_transaction),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        },
                        text = { Text(text = stringResource(Res.string.feature_loan_dialog_message_sync_transaction)) },
                    )
                }

                is LoanRepaymentUiState.ShowPaymentSubmittedSuccessfully -> {
                    if (uiState.loanRepaymentResponse != null) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = getString(Res.string.feature_loan_payment_success_message) + uiState.loanRepaymentResponse.resourceId,
                            )
                            navigateBack.invoke()
                        }
                    }
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
private fun LoanRepaymentContent(
    loanId: Int,
    clientName: String,
    loanProductName: String,
    amountInArrears: Double?,
    loanAccountNumber: String,
    loanRepaymentTemplate: LoanRepaymentTemplateEntity,
    navigateBack: () -> Unit,
    submitPayment: (request: LoanRepaymentRequestEntity) -> Unit,
) {
    var paymentType by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var additionalPayment by rememberSaveable { mutableStateOf("") }
    var fees by rememberSaveable { mutableStateOf("") }
    var paymentTypeId by rememberSaveable { mutableIntStateOf(0) }

    var repaymentDate by rememberSaveable { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }
    var showDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = repaymentDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )
    val scrollState = rememberScrollState()
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
                additionalPayment = additionalPayment,
            ).toString(),
            submitPayment = submitPayment,
        )
    }

    if (showDatePickerDialog) {
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
                    },
                ) { Text(stringResource(Res.string.feature_loan_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false
                    },
                ) { Text(stringResource(Res.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            text = clientName,
        )

        HorizontalDivider(modifier = Modifier.padding(top = 10.dp))

        FarApartTextItem(title = loanProductName, value = loanId.toString())
        FarApartTextItem(
            title = stringResource(Res.string.feature_loan_loan_in_arrears),
            value = amountInArrears?.toString() ?: "",
        )
        FarApartTextItem(
            title = stringResource(Res.string.feature_loan_loan_amount_due),
            value = loanRepaymentTemplate.amount?.toString() ?: "",
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

        MifosDatePickerTextField(
            modifier = Modifier.fillMaxWidth(),
            value = DateHelper.getDateAsStringFromLong(
                repaymentDate,
            ),
            label = stringResource(Res.string.feature_loan_repayment_date),
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
            label = stringResource(Res.string.feature_loan_payment_type),
            options = if (loanRepaymentTemplate.paymentTypeOptions != null) loanRepaymentTemplate.paymentTypeOptions!!.map { it.name } else listOf(),
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = amount,
            onValueChange = {
                amount = it
            },
            label = stringResource(Res.string.feature_loan_amount),
            error = null,
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = additionalPayment,
            onValueChange = {
                additionalPayment = it
            },
            label = stringResource(Res.string.feature_loan_additional_payment),
            error = null,
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = fees,
            onValueChange = {
                fees = it
            },
            label = stringResource(Res.string.feature_loan_loan_fees),
            error = null,
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = calculateTotal(
                fees = fees,
                amount = amount,
                additionalPayment = additionalPayment,
            ).toString(),
            onValueChange = { },
            label = stringResource(Res.string.feature_loan_total),
            error = null,
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(
                modifier = Modifier
                    .heightIn(46.dp),
                onClick = { navigateBack.invoke() },
            ) {
                Text(text = stringResource(Res.string.feature_loan_cancel))
            }

            Button(
                modifier = Modifier
                    .heightIn(46.dp),
                onClick = {
                    if (isAllFieldsValid(
                            amount = amount,
                            additionalPayment = additionalPayment,
                            fees = fees,
                            paymentType = paymentType,
                        )
                    ) {
                        showConfirmationDialog = true
                    }
                },
            ) {
                Text(text = stringResource(Res.string.feature_loan_review_payment))
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
        horizontalArrangement = Arrangement.SpaceBetween,
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
private fun ShowLoanRepaymentConfirmationDialog(
    onDismiss: () -> Unit,
    loanAccountNumber: String,
    paymentTypeId: String,
    repaymentDate: Long,
    paymentType: String,
    amount: String,
    additionalPayment: String,
    fees: String,
    total: String,
    submitPayment: (request: LoanRepaymentRequestEntity) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    val request = LoanRepaymentRequestEntity(
                        accountNumber = loanAccountNumber,
                        paymentTypeId = paymentTypeId,
                        dateFormat = "dd-MM-yyyy",
                        locale = "en",
                        transactionAmount = total,
                        transactionDate = DateHelper.getDateAsStringFromLong(
                            repaymentDate,
                        ),
                    )
                    submitPayment.invoke(request)
                },
            ) {
                Text(text = stringResource(Res.string.feature_loan_dialog_action_pay_now))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() },
            ) {
                Text(text = stringResource(Res.string.feature_loan_cancel))
            }
        },
        title = {
            Text(
                text = stringResource(Res.string.feature_loan_review_payment),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column {
                Text(text = stringResource(Res.string.feature_loan_account_number) + " : " + loanAccountNumber)
                Text(
                    text = stringResource(Res.string.feature_loan_repayment_date) + " : " +
                        DateHelper.getDateAsStringFromLong(repaymentDate),
                )
                Text(text = stringResource(Res.string.feature_loan_payment_type) + " : " + paymentType)
                Text(text = stringResource(Res.string.feature_loan_amount) + " : " + amount)
                Text(text = stringResource(Res.string.feature_loan_additional_payment) + " : " + additionalPayment)
                Text(text = stringResource(Res.string.feature_loan_loan_fees) + " : " + fees)
                Text(text = stringResource(Res.string.feature_loan_total) + " : " + total)
            }
        },
    )
}

/**
 * Calculating the Total of the  Amount, Additional Payment and Fee
 * @return Total of the Amount + Additional Payment + Fees
 */
private fun calculateTotal(
    fees: String,
    amount: String,
    additionalPayment: String,
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

private fun isAllFieldsValid(
    amount: String,
    additionalPayment: String,
    fees: String,
    paymentType: String,
): Boolean {
    return when {
        amount.isNotEmpty() && additionalPayment.isNotEmpty() && fees.isNotEmpty() && paymentType.isNotEmpty() -> {
            true
        }

        else -> {
            false
        }
    }
}

private class LoanRepaymentScreenPreviewProvider :
    PreviewParameterProvider<LoanRepaymentUiState> {

    private val samplePaymentTypeOptions = mutableListOf(
        PaymentTypeOptionEntity(
            id = 1,
            name = "Cash",
            description = "Cash payment",
            isCashPayment = true,
            position = 1,
        ),
    )

    private val sampleLoanRepaymentTemplate = LoanRepaymentTemplateEntity(
        loanId = 101,
        date = mutableListOf(2024, 7, 15),
        amount = 1000.0,
        principalPortion = 800.0,
        interestPortion = 150.0,
        feeChargesPortion = 30.0,
        penaltyChargesPortion = 20.0,
        paymentTypeOptions = samplePaymentTypeOptions,
    )

    override val values: Sequence<LoanRepaymentUiState>
        get() = sequenceOf(
            LoanRepaymentUiState.ShowLoanRepaymentExistInDatabase,
            LoanRepaymentUiState.ShowLoanRepayTemplate(sampleLoanRepaymentTemplate),
            LoanRepaymentUiState.ShowError(Res.string.feature_loan_failed_to_load_loan_repayment),
            LoanRepaymentUiState.ShowLoanRepaymentDoesNotExistInDatabase,
            LoanRepaymentUiState.ShowProgressbar,
            LoanRepaymentUiState.ShowPaymentSubmittedSuccessfully(LoanRepaymentResponseEntity()),
        )
}

@Composable
@Preview
private fun PreviewLoanRepaymentScreen(
    @PreviewParameter(LoanRepaymentScreenPreviewProvider::class) loanRepaymentUiState: LoanRepaymentUiState,
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
        onLoanRepaymentDoesNotExistInDatabase = {},
    )
}
