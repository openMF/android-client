/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.loan.loanDisburse

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.cancel
import androidclient.feature.loan.generated.resources.feature_loan_disburse
import androidclient.feature.loan.generated.resources.feature_loan_disburse_account_number
import androidclient.feature.loan.generated.resources.feature_loan_disburse_available_amount
import androidclient.feature.loan.generated.resources.feature_loan_disburse_bank_number
import androidclient.feature.loan.generated.resources.feature_loan_disburse_cheque_number
import androidclient.feature.loan.generated.resources.feature_loan_disburse_date
import androidclient.feature.loan.generated.resources.feature_loan_disburse_external_id
import androidclient.feature.loan.generated.resources.feature_loan_disburse_note
import androidclient.feature.loan.generated.resources.feature_loan_disburse_receipt_number
import androidclient.feature.loan.generated.resources.feature_loan_disburse_routing_code
import androidclient.feature.loan.generated.resources.feature_loan_disburse_show_payment_details
import androidclient.feature.loan.generated.resources.feature_loan_disburse_transaction_amount
import androidclient.feature.loan.generated.resources.feature_loan_payment_type
import androidclient.feature.loan.generated.resources.feature_loan_submit
import androidclient.feature.loan.generated.resources.ok
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import kotlin.math.max
import kotlin.time.ExperimentalTime

@Composable
internal fun LoanDisburseScreen(
    onNavigateBack: () -> Unit,
    onDisburseSuccess: (loanId: Int) -> Unit,
    viewModel: LoanDisburseViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanDisburseEvent.NavigateBack -> onNavigateBack()
            LoanDisburseEvent.DisburseSuccess -> onDisburseSuccess(state.loanId)
        }
    }

    LoanDisburseScreenContent(
        state = state,
        onAction = remember(viewModel) { viewModel::trySendAction },
    )

    LoanDisburseDialog(
        dialogMessage = state.dialogMessage,
        onDismissDialog = { viewModel.trySendAction(LoanDisburseAction.DismissDialog) },
    )
}

@Composable
private fun LoanDisburseScreenContent(
    state: LoanDisburseState,
    onAction: (LoanDisburseAction) -> Unit,
) {
    MifosScaffold(
        title = stringResource(Res.string.feature_loan_disburse),
        onBackPressed = { onAction(LoanDisburseAction.NavigateBack) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            when (state.viewState) {
                is LoanDisburseState.ViewState.Loading -> {
                    MifosProgressIndicator()
                }

                is LoanDisburseState.ViewState.Error -> {
                    MifosSweetError(
                        message = stringResource(state.viewState.message),
                        onclick = { onAction(LoanDisburseAction.OnRetry) },
                    )
                }

                is LoanDisburseState.ViewState.Success -> {
                    LoanDisburseForm(
                        state = state,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun LoanDisburseForm(
    state: LoanDisburseState,
    onAction: (LoanDisburseAction) -> Unit,
) {
    val scrollState = rememberScrollState()
    val initialDate = max(state.disbursedDate, state.minDisbursementDate)

    val selectableDates = remember(state.minDisbursementDate) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= state.minDisbursementDate
            }
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate,
        selectableDates = selectableDates,
    )

    LaunchedEffect(state.disbursedDate) {
        val constrainedDate = max(state.disbursedDate, state.minDisbursementDate)
        datePickerState.selectedDateMillis = constrainedDate
    }

    if (state.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onAction(LoanDisburseAction.HideDatePicker) },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onAction(LoanDisburseAction.DisbursedDateChanged(it))
                        }
                        onAction(LoanDisburseAction.HideDatePicker)
                    },
                ) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { onAction(LoanDisburseAction.HideDatePicker) },
                ) { Text(stringResource(Res.string.cancel)) }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(KptTheme.spacing.md),
        ) {
            MifosDatePickerTextField(
                value = state.disbursedDateText,
                label = stringResource(Res.string.feature_loan_disburse_date) + "*",
                openDatePicker = { onAction(LoanDisburseAction.ShowDatePicker) },
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            MifosOutlinedTextField(
                value = state.transactionAmount.toString(),
                onValueChange = { value ->
                    value.toDoubleOrNull()?.let { amount ->
                        onAction(LoanDisburseAction.TransactionAmountChanged(amount))
                    }
                },
                label = stringResource(Res.string.feature_loan_disburse_transaction_amount) + "*",
                keyboardType = KeyboardType.Decimal,
                prefix = {
                    Text(
                        text = (state.viewState as? LoanDisburseState.ViewState.Success)
                            ?.template?.currency?.code ?: "",
                        modifier = Modifier.padding(start = DesignToken.spacing.small),
                    )
                },
                errorText = state.transactionAmountError?.let { stringResource(it) },
                isError = state.transactionAmountError != null,
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            MifosOutlinedTextField(
                value = state.availableAmount,
                onValueChange = { },
                label = stringResource(Res.string.feature_loan_disburse_available_amount),
                readOnly = true,
                keyboardType = KeyboardType.Number,
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            MifosOutlinedTextField(
                value = state.externalId,
                onValueChange = { onAction(LoanDisburseAction.ExternalIdChanged(it)) },
                label = stringResource(Res.string.feature_loan_disburse_external_id),
                keyboardType = KeyboardType.Text,
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            MifosTextFieldDropdown(
                value = state.selectedPaymentType?.name ?: "",
                onValueChanged = { },
                label = stringResource(Res.string.feature_loan_payment_type),
                readOnly = true,
                onOptionSelected = { index, _ ->
                    onAction(
                        LoanDisburseAction.PaymentTypeSelected(
                            index,
                        ),
                    )
                },
                options = state.paymentTypes.map { it.name ?: "" },
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            LoanDisbursePaymentDetailsToggle(
                isExpanded = state.showPaymentDetails,
                onToggle = { onAction(LoanDisburseAction.TogglePaymentDetails) },
            )

            AnimatedVisibility(visible = state.showPaymentDetails) {
                LoanDisbursePaymentDetailsSection(
                    state = state,
                    onAction = onAction,
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            MifosOutlinedTextField(
                value = state.note,
                onValueChange = { onAction(LoanDisburseAction.NoteChanged(it)) },
                label = stringResource(Res.string.feature_loan_disburse_note),
                keyboardType = KeyboardType.Text,
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            MifosButton(
                onClick = { onAction(LoanDisburseAction.Submit) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting && state.transactionAmountError == null,
            ) {
                Text(stringResource(Res.string.feature_loan_submit))
            }
        }

        if (state.isSubmitting) {
            MifosProgressIndicatorOverlay()
        }
    }
}

@Composable
private fun LoanDisbursePaymentDetailsToggle(
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DesignToken.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.feature_loan_disburse_show_payment_details),
            modifier = Modifier
                .weight(1f)
                .padding(start = DesignToken.spacing.small),
        )
        Switch(
            checked = isExpanded,
            onCheckedChange = { onToggle() },
        )
    }
}

@Composable
private fun LoanDisbursePaymentDetailsSection(
    state: LoanDisburseState,
    onAction: (LoanDisburseAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = DesignToken.spacing.medium),
    ) {
        MifosOutlinedTextField(
            value = state.accountNumber,
            onValueChange = { onAction(LoanDisburseAction.AccountNumberChanged(it)) },
            label = stringResource(Res.string.feature_loan_disburse_account_number),
            keyboardType = KeyboardType.Text,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.small))

        MifosOutlinedTextField(
            value = state.chequeNumber,
            onValueChange = { onAction(LoanDisburseAction.ChequeNumberChanged(it)) },
            label = stringResource(Res.string.feature_loan_disburse_cheque_number),
            keyboardType = KeyboardType.Text,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.small))

        MifosOutlinedTextField(
            value = state.routingCode,
            onValueChange = { onAction(LoanDisburseAction.RoutingCodeChanged(it)) },
            label = stringResource(Res.string.feature_loan_disburse_routing_code),
            keyboardType = KeyboardType.Text,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.small))

        MifosOutlinedTextField(
            value = state.receiptNumber,
            onValueChange = { onAction(LoanDisburseAction.ReceiptNumberChanged(it)) },
            label = stringResource(Res.string.feature_loan_disburse_receipt_number),
            keyboardType = KeyboardType.Text,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.small))

        MifosOutlinedTextField(
            value = state.bankNumber,
            onValueChange = { onAction(LoanDisburseAction.BankNumberChanged(it)) },
            label = stringResource(Res.string.feature_loan_disburse_bank_number),
            keyboardType = KeyboardType.Text,
        )
    }
}

@Composable
private fun LoanDisburseDialog(
    dialogMessage: StringResource?,
    onDismissDialog: () -> Unit,
) {
    if (dialogMessage != null) {
        MifosAlertDialog(
            onConfirmation = onDismissDialog,
            dialogText = stringResource(dialogMessage),
            onDismissRequest = {},
            dismissText = null,
        )
    }
}
