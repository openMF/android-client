/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDisbursement

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_approval_disbursement_date
import androidclient.feature.loan.generated.resources.feature_loan_available_disbursement_amount
import androidclient.feature.loan.generated.resources.feature_loan_bank_number
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_cheque_number
import androidclient.feature.loan.generated.resources.feature_loan_disburse_loan
import androidclient.feature.loan.generated.resources.feature_loan_disbursement_note
import androidclient.feature.loan.generated.resources.feature_loan_external_id
import androidclient.feature.loan.generated.resources.feature_loan_loan_amount_disbursed
import androidclient.feature.loan.generated.resources.feature_loan_payment_type
import androidclient.feature.loan.generated.resources.feature_loan_receipt_number
import androidclient.feature.loan.generated.resources.feature_loan_routing_code
import androidclient.feature.loan.generated.resources.feature_loan_select_date
import androidclient.feature.loan.generated.resources.feature_loan_show_account_number
import androidclient.feature.loan.generated.resources.feature_loan_show_payment_details
import androidclient.feature.loan.generated.resources.feature_loan_submission_failed
import androidclient.feature.loan.generated.resources.feature_loan_submit
import androidclient.feature.loan.generated.resources.feature_loan_unknown_error
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.EventsEffect
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.savings.SavingAccountCurrencyEntity
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock

@Composable
internal fun LoanAccountDisbursementScreenRoute(
    navController: NavController,
    navigateBack: () -> Unit,
    navigateToLoanProfile: (Int) -> Unit,
    viewModel: LoanAccountDisbursementViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanDisbursementEvent.NavigateBack -> navigateBack()
            is LoanDisbursementEvent.NavigateToLoanProfile -> {
                navigateToLoanProfile(event.loanId)
            }
        }
    }

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        title = stringResource(Res.string.feature_loan_disburse_loan),
        onBackPressed = navigateBack,
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (state.dialogState == null) {
                state.template?.let {
                    LoanAccountDisbursementContent(
                        state = state,
                        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
                        onCancel = navigateBack,
                    )
                }
            }

            LoanAccountDisbursementDialogs(
                dialogState = state.dialogState,
                networkConnection = state.networkConnection,
                onRetry = remember(viewModel) { { viewModel.trySendAction(LoanDisbursementAction.OnRetry) } },
                onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
            )
        }
    }
}

@Composable
private fun LoanAccountDisbursementDialogs(
    dialogState: LoanDisbursementState.DialogState?,
    networkConnection: Boolean,
    onRetry: () -> Unit,
    onAction: (LoanDisbursementAction) -> Unit,
) {
    when (val dialog = dialogState) {
        is LoanDisbursementState.DialogState.Loading -> {
            MifosProgressIndicator()
        }
        is LoanDisbursementState.DialogState.FetchingError -> {
            MifosErrorComponent(
                isNetworkConnected = networkConnection,
                message = stringResource(dialog.messageRes),
                isRetryEnabled = true,
                onRetry = onRetry,
            )
        }

        is LoanDisbursementState.DialogState.ActionError -> {
            val displayMessage = dialog.backendMessage ?: dialog.messageRes?.let { stringResource(it) } ?: stringResource(Res.string.feature_loan_unknown_error)

            MifosStatusDialog(
                status = ResultStatus.FAILURE,
                btnText = stringResource(Res.string.feature_loan_cancel),
                onConfirm = { onAction(LoanDisbursementAction.DismissDialog) },
                onDismissRequest = { onAction(LoanDisbursementAction.DismissDialog) },
                successTitle = "",
                successMessage = "",
                failureTitle = stringResource(Res.string.feature_loan_submission_failed),
                failureMessage = displayMessage,
                showAsDialog = true,
            )
        }
        null -> { }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoanAccountDisbursementContent(
    state: LoanDisbursementState,
    onAction: (LoanDisbursementAction) -> Unit,
    onCancel: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var showDatePickerDialog by rememberSaveable { mutableStateOf(false) }

    val currencySymbol = state.template?.currency?.displaySymbol ?: ""
    val availableAmountRaw = state.template?.availableDisbursementAmountWithOverApplied
        ?: state.template?.amount
        ?: 0.0

    val isSubmitEnabled = state.amount.isNotBlank() &&
        state.amount.toDoubleOrNull() != null

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.disbursementDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onAction(LoanDisbursementAction.UpdateDate(it))
                        }
                        showDatePickerDialog = false
                    },
                ) { Text(stringResource(Res.string.feature_loan_select_date)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text(stringResource(Res.string.feature_loan_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = KptTheme.spacing.md)
                .verticalScroll(scrollState),
        ) {
            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            MifosDatePickerTextField(
                value = DateHelper.getDateAsStringFromLong(state.disbursementDate),
                label = stringResource(Res.string.feature_loan_approval_disbursement_date),
                openDatePicker = { showDatePickerDialog = true },
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = state.amount,
                onValueChange = { onAction(LoanDisbursementAction.UpdateAmount(it)) },
                label = stringResource(Res.string.feature_loan_loan_amount_disbursed),
                config = MifosTextFieldConfig(
                    prefix = {
                        Text(
                            text = "$currencySymbol ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    isError = state.amountError != null,
                    errorText = state.amountError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                ),
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = "$currencySymbol $availableAmountRaw",
                onValueChange = { },
                label = stringResource(Res.string.feature_loan_available_disbursement_amount),
                error = null,
                readOnly = true,
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = state.externalId,
                onValueChange = { onAction(LoanDisbursementAction.UpdateExternalId(it)) },
                label = stringResource(Res.string.feature_loan_external_id),
                error = null,
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            val paymentOptions = state.template?.paymentTypeOptions ?: emptyList()
            MifosTextFieldDropdown(
                value = state.selectedPaymentType?.name ?: "",
                onValueChanged = { },
                onOptionSelected = { index, _ ->
                    onAction(LoanDisbursementAction.UpdatePaymentType(paymentOptions[index]))
                },
                label = stringResource(Res.string.feature_loan_payment_type),
                options = paymentOptions.map { it.name },
                readOnly = true,
            )
            if (state.paymentTypeError != null) {
                Text(
                    text = stringResource(state.paymentTypeError),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = KptTheme.spacing.sm, top = KptTheme.spacing.xs),
                )
            }

            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.feature_loan_show_payment_details),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Switch(
                    checked = state.showPaymentDetails,
                    onCheckedChange = { onAction(LoanDisbursementAction.TogglePaymentDetails(it)) },
                )
            }

            AnimatedVisibility(visible = state.showPaymentDetails) {
                Column {
                    Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

                    MifosOutlinedTextField(
                        value = state.accountNumber,
                        onValueChange = { onAction(LoanDisbursementAction.UpdateAccountNumber(it)) },
                        label = stringResource(Res.string.feature_loan_show_account_number),
                        error = null,
                    )

                    Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

                    MifosOutlinedTextField(
                        value = state.checkNumber,
                        onValueChange = { onAction(LoanDisbursementAction.UpdateCheckNumber(it)) },
                        label = stringResource(Res.string.feature_loan_cheque_number),
                        error = null,
                    )

                    Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

                    MifosOutlinedTextField(
                        value = state.routingCode,
                        onValueChange = { onAction(LoanDisbursementAction.UpdateRoutingCode(it)) },
                        label = stringResource(Res.string.feature_loan_routing_code),
                        error = null,
                    )

                    Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

                    MifosOutlinedTextField(
                        value = state.receiptNumber,
                        onValueChange = { onAction(LoanDisbursementAction.UpdateReceiptNumber(it)) },
                        label = stringResource(Res.string.feature_loan_receipt_number),
                        error = null,
                    )

                    Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

                    MifosOutlinedTextField(
                        value = state.bankNumber,
                        onValueChange = { onAction(LoanDisbursementAction.UpdateBankNumber(it)) },
                        label = stringResource(Res.string.feature_loan_bank_number),
                        error = null,
                    )
                }
            }

            Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

            MifosOutlinedTextField(
                value = state.note,
                onValueChange = { onAction(LoanDisbursementAction.UpdateNote(it)) },
                label = stringResource(Res.string.feature_loan_disbursement_note),
                error = null,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
        ) {
            MifosTwoButtonRow(
                firstBtnText = stringResource(Res.string.feature_loan_cancel),
                secondBtnText = stringResource(Res.string.feature_loan_submit),
                onFirstBtnClick = onCancel,
                onSecondBtnClick = { onAction(LoanDisbursementAction.Submit) },
                isButtonIconVisible = false,
                isSecondButtonEnabled = isSubmitEnabled,
            )
        }
    }
}

private class LoanDisbursementPreviewProvider : PreviewParameterProvider<LoanDisbursementState> {

    val mockTemplate = LoanTransactionTemplate(
        amount = 5000.0,
        availableDisbursementAmountWithOverApplied = 5000.0,
        currency = SavingAccountCurrencyEntity(
            code = "USD",
            displaySymbol = "$",
            decimalPlaces = 2,
        ),
        paymentTypeOptions = listOf(
            PaymentTypeOptionEntity(id = 1, name = "Cash"),
            PaymentTypeOptionEntity(id = 2, name = "Bank Transfer"),
        ),
    )

    override val values: Sequence<LoanDisbursementState>
        get() = sequenceOf(
            LoanDisbursementState(
                template = mockTemplate,
                amount = "5000.0",
                dialogState = null,
            ),
            LoanDisbursementState(
                template = mockTemplate,
                amount = "5000.0",
                showPaymentDetails = true,
                accountNumber = "123456789",
                bankNumber = "BOA-001",
                dialogState = null,
            ),
            LoanDisbursementState(
                dialogState = LoanDisbursementState.DialogState.Loading,
            ),
            LoanDisbursementState(
                dialogState = LoanDisbursementState.DialogState.FetchingError(Res.string.feature_loan_unknown_error),
                networkConnection = true,
            ),
        )
}

@Composable
@Preview(showBackground = true)
private fun LoanAccountDisbursementScreenPreview(
    @PreviewParameter(LoanDisbursementPreviewProvider::class) state: LoanDisbursementState,
) {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.dialogState == null && state.template != null) {
                LoanAccountDisbursementContent(
                    state = state,
                    onAction = {},
                    onCancel = {},
                )
            }

            LoanAccountDisbursementDialogs(
                dialogState = state.dialogState,
                networkConnection = state.networkConnection,
                onRetry = {},
                onAction = {},
            )
        }
    }
}
