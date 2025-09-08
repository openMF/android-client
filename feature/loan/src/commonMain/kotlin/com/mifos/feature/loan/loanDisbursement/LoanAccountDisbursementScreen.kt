/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDisbursement

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_approval_disbursement_date
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_disburse_loan
import androidclient.feature.loan.generated.resources.feature_loan_disbursement_note
import androidclient.feature.loan.generated.resources.feature_loan_loan_amount_disbursed
import androidclient.feature.loan.generated.resources.feature_loan_loan_disburse_successfully
import androidclient.feature.loan.generated.resources.feature_loan_payment_type
import androidclient.feature.loan.generated.resources.feature_loan_select_date
import androidclient.feature.loan.generated.resources.feature_loan_submit
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.mifos.core.model.objects.account.loan.LoanDisbursement
import com.mifos.core.network.GenericResponse
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LoanAccountDisbursementScreen(
    navigateBack: () -> Unit,
    viewmodel: LoanAccountDisbursementViewModel = koinViewModel(),
) {
    val uiState by viewmodel.loanAccountDisbursementUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewmodel.loadLoanTemplate()
    }

    LoanAccountDisbursementScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = { viewmodel.loadLoanTemplate() },
        onDisburseLoan = {
            viewmodel.disburseLoan(it)
        },
    )
}

@Composable
internal fun LoanAccountDisbursementScreen(
    uiState: LoanAccountDisbursementUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
    onDisburseLoan: (loanDisbursement: LoanDisbursement) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        title = stringResource(Res.string.feature_loan_disburse_loan),
        onBackPressed = navigateBack,
    ) {
        Box(
            modifier = Modifier
                .padding(it),
        ) {
            when (uiState) {
                is LoanAccountDisbursementUiState.ShowDisburseLoanSuccessfully -> {
                    val message = stringResource(Res.string.feature_loan_loan_disburse_successfully)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = message,
                        )
                    }
                    navigateBack.invoke()
                }

                is LoanAccountDisbursementUiState.ShowError -> {
                    MifosSweetError(
                        message = uiState.message,
                        onclick = onRetry,
                    )
                }

                is LoanAccountDisbursementUiState.ShowLoanTransactionTemplate -> {
                    LoanAccountDisbursementContent(
                        initialAmount = uiState.loanTransactionTemplate.amount.toString(),
                        paymentTypeOptions = uiState.loanTransactionTemplate.paymentTypeOptions,
                        onDisburseLoan = onDisburseLoan,
                    )
                }

                LoanAccountDisbursementUiState.ShowProgressbar -> MifosCircularProgress()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoanAccountDisbursementContent(
    initialAmount: String,
    paymentTypeOptions: List<PaymentTypeOptionEntity>,
    onDisburseLoan: (loanDisbursement: LoanDisbursement) -> Unit,
) {
    var disbursementDate by rememberSaveable {
        mutableLongStateOf(Clock.System.now().toEpochMilliseconds())
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
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = disbursementDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
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
            .verticalScroll(scrollState),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = DateHelper.getDateAsStringFromLong(
                disbursementDate,
            ),
            label = stringResource(Res.string.feature_loan_approval_disbursement_date),
            openDatePicker = {
                showDatePickerDialog = true
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = stringResource(Res.string.feature_loan_loan_amount_disbursed),
            error = null,
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = selectedPaymentType,
            onValueChanged = { selectedPaymentType = it },
            onOptionSelected = { index, value ->
                selectedPaymentType = value
                paymentTypeId = paymentTypeOptions[index].id
            },
            label = stringResource(Res.string.feature_loan_payment_type),
            options = paymentTypeOptions.map { it.name },
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = stringResource(Res.string.feature_loan_disbursement_note),
            error = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(44.dp),
            onClick = {
                if (isFieldValid(amount = amount)) {
                    val date = DateHelper.getDateAsStringFromLong(
                        disbursementDate,
                    )
                    val loanDisbursement = LoanDisbursement(
                        note = note,
                        paymentId = paymentTypeId,
                        actualDisbursementDate = date,
                        transactionAmount = amount.toDouble(),
                    )

                    onDisburseLoan.invoke(loanDisbursement)
                }
            },
        ) {
            Text(text = stringResource(Res.string.feature_loan_submit))
        }
    }
}

private fun isFieldValid(amount: String): Boolean {
    return when {
        amount.isEmpty() -> {
//            Toast.makeText(
//                context,
//                context.resources.getString(R.string.feature_loan_error_amount_can_not_be_empty),
//                Toast.LENGTH_SHORT,
//            ).show()

            false
        }

        !isAmountValid(amount) -> {
//            Toast.makeText(
//                context,
//                context.resources.getString(R.string.feature_loan_error_invalid_amount),
//                Toast.LENGTH_SHORT,
//            ).show()

            false
        }

        else -> {
            true
        }
    }
}

private fun isAmountValid(amount: String): Boolean {
    return amount.toDoubleOrNull() != null
}

private class LoanAccountDisbursementScreenPreviewProvider :
    PreviewParameterProvider<LoanAccountDisbursementUiState> {
    override val values: Sequence<LoanAccountDisbursementUiState>
        get() = sequenceOf(
            LoanAccountDisbursementUiState.ShowProgressbar,
            LoanAccountDisbursementUiState.ShowError("An error occurred"),
            LoanAccountDisbursementUiState.ShowDisburseLoanSuccessfully(GenericResponse()),
            LoanAccountDisbursementUiState.ShowLoanTransactionTemplate(LoanTransactionTemplate()),
        )
}

@Composable
@Preview
private fun PreviewLoanAccountDisbursementScreen(
    @PreviewParameter(LoanAccountDisbursementScreenPreviewProvider::class) loanAccountDisbursementUiState: LoanAccountDisbursementUiState,
) {
    LoanAccountDisbursementScreen(
        uiState = loanAccountDisbursementUiState,
        navigateBack = { },
        onRetry = { },
        onDisburseLoan = { },
    )
}
