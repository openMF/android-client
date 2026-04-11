/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepayment

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_account_number
import androidclient.feature.loan.generated.resources.feature_loan_bank_number
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_cheque_number
import androidclient.feature.loan.generated.resources.feature_loan_dialog_action_ok
import androidclient.feature.loan.generated.resources.feature_loan_dialog_action_pay_now
import androidclient.feature.loan.generated.resources.feature_loan_dialog_message_sync_transaction
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan_repayment
import androidclient.feature.loan.generated.resources.feature_loan_fees
import androidclient.feature.loan.generated.resources.feature_loan_interest
import androidclient.feature.loan.generated.resources.feature_loan_loan_amount_due
import androidclient.feature.loan.generated.resources.feature_loan_loan_in_arrears
import androidclient.feature.loan.generated.resources.feature_loan_loan_repayment
import androidclient.feature.loan.generated.resources.feature_loan_no_penalties_found
import androidclient.feature.loan.generated.resources.feature_loan_note
import androidclient.feature.loan.generated.resources.feature_loan_payment_success_title
import androidclient.feature.loan.generated.resources.feature_loan_payment_success_transaction_label
import androidclient.feature.loan.generated.resources.feature_loan_payment_type
import androidclient.feature.loan.generated.resources.feature_loan_penalties
import androidclient.feature.loan.generated.resources.feature_loan_principal
import androidclient.feature.loan.generated.resources.feature_loan_receipt_number
import androidclient.feature.loan.generated.resources.feature_loan_repayment_date
import androidclient.feature.loan.generated.resources.feature_loan_review_payment
import androidclient.feature.loan.generated.resources.feature_loan_routing_code
import androidclient.feature.loan.generated.resources.feature_loan_select_date
import androidclient.feature.loan.generated.resources.feature_loan_show_payment_details
import androidclient.feature.loan.generated.resources.feature_loan_sync_previous_transaction
import androidclient.feature.loan.generated.resources.feature_loan_total
import androidclient.feature.loan.generated.resources.feature_loan_transaction_amount
import androidclient.feature.loan.generated.resources.feature_loan_transaction_breakdown
import androidclient.feature.loan.generated.resources.feature_loan_waive_penalties
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.util.EventsEffect
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun LoanRepaymentScreen(
    navigateBack: () -> Unit,
    viewModel: LoanRepaymentViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanRepaymentEvent.NavigateBack -> navigateBack()
        }
    }

    LoanRepaymentScreen(
        state = state,
        onAction = remember(viewModel) { viewModel::trySendAction },
    )
}

@Composable
internal fun LoanRepaymentScreen(
    state: LoanRepaymentState,
    onAction: (LoanRepaymentAction) -> Unit,
) {
    MifosScaffold(
        onBackPressed = { onAction(LoanRepaymentAction.CancelClicked) },
        title = stringResource(Res.string.feature_loan_loan_repayment),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (state.loanRepaymentTemplate != null) {
                LoanRepaymentContent(
                    state = state,
                    onAction = onAction,
                )
            }

            LoanRepaymentDialogs(state = state, onAction = onAction)

            if (state.isLoading) {
                MifosProgressIndicatorOverlay()
            }
        }
    }
}

@Composable
private fun LoanRepaymentDialogs(
    state: LoanRepaymentState,
    onAction: (LoanRepaymentAction) -> Unit,
) {
    when (val dialogState = state.dialogState) {
        is LoanRepaymentState.DialogState.Error -> {
            MifosSweetError(message = stringResource(dialogState.message)) {
                onAction(LoanRepaymentAction.RetryClicked)
            }
        }

        LoanRepaymentState.DialogState.SyncPreviousTransaction -> {
            AlertDialog(
                onDismissRequest = { },
                confirmButton = {
                    TextButton(onClick = { onAction(LoanRepaymentAction.SyncDialogDismissed) }) {
                        Text(text = stringResource(Res.string.feature_loan_dialog_action_ok))
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.feature_loan_sync_previous_transaction),
                        style = KptTheme.typography.titleLarge,
                    )
                },
                text = {
                    Text(
                        text = stringResource(
                            Res.string.feature_loan_dialog_message_sync_transaction,
                        ),
                    )
                },
            )
        }

        is LoanRepaymentState.DialogState.PaymentSuccess -> {
            SuccessBottomSheet(
                response = dialogState.response,
                onDismiss = { onAction(LoanRepaymentAction.DismissSuccessDialog) },
            )
        }

        null -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun LoanRepaymentContent(
    state: LoanRepaymentState,
    onAction: (LoanRepaymentAction) -> Unit,
) {
    val scrollState = rememberScrollState()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.repaymentDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    if (state.showConfirmationSheet) {
        ConfirmationBottomSheet(
            state = state,
            onDismiss = { onAction(LoanRepaymentAction.DismissConfirmationSheet) },
            onConfirm = { onAction(LoanRepaymentAction.ConfirmPayment) },
        )
    }

    if (state.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onAction(LoanRepaymentAction.DismissDatePicker) },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onAction(LoanRepaymentAction.RepaymentDateChanged(it))
                        }
                        onAction(LoanRepaymentAction.DismissDatePicker)
                    },
                ) { Text(stringResource(Res.string.feature_loan_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { onAction(LoanRepaymentAction.DismissDatePicker) },
                ) { Text(stringResource(Res.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = DesignToken.padding.medium)
            .verticalScroll(scrollState),
    ) {
        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        Text(
            style = KptTheme.typography.bodyLarge,
            color = KptTheme.colorScheme.onBackground,
            text = state.clientName,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        FarApartTextItem(title = state.loanProductName, value = state.loanId.toString())
        FarApartTextItem(
            title = stringResource(Res.string.feature_loan_loan_in_arrears),
            value = formatCurrency(state.amountInArrears, state.currencyCode, state.decimalPlaces),
        )
        FarApartTextItem(
            title = stringResource(Res.string.feature_loan_loan_amount_due),
            value = formatCurrency(
                state.loanRepaymentTemplate?.amount,
                state.currencyCode,
                state.decimalPlaces,
            ),
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        TransactionBreakdownSection(state = state)

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        MifosDatePickerTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DesignToken.sizes.inputHeight),
            value = DateHelper.getDateAsStringFromLong(state.repaymentDate),
            label = stringResource(Res.string.feature_loan_repayment_date),
        ) {
            onAction(LoanRepaymentAction.ShowDatePicker)
        }

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        MifosOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DesignToken.sizes.inputHeight),
            value = state.transactionAmount,
            onValueChange = { onAction(LoanRepaymentAction.TransactionAmountChanged(it)) },
            label = stringResource(Res.string.feature_loan_transaction_amount),
            error = null,
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        MifosTextFieldDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DesignToken.sizes.inputHeight),
            value = state.paymentType,
            onValueChanged = {},
            onOptionSelected = { index, value ->
                onAction(LoanRepaymentAction.PaymentTypeChanged(index, value))
            },
            label = stringResource(Res.string.feature_loan_payment_type),
            options = state.paymentTypeOptions.map { it.name },
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        WaivePenaltiesToggle(state = state, onAction = onAction)

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        ShowPaymentDetailsToggle(state = state, onAction = onAction)

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        MifosOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DesignToken.sizes.inputHeight * 2),
            value = state.note,
            onValueChange = { onAction(LoanRepaymentAction.NoteChanged(it)) },
            label = stringResource(Res.string.feature_loan_note),
            error = null,
            singleLine = false,
            maxLines = 4,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.extraLarge))

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_loan_cancel),
            secondBtnText = stringResource(Res.string.feature_loan_review_payment),
            onFirstBtnClick = { onAction(LoanRepaymentAction.CancelClicked) },
            onSecondBtnClick = { onAction(LoanRepaymentAction.ReviewPaymentClicked) },
            isSecondButtonEnabled = state.isFormValid,
            isButtonIconVisible = false,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))
    }
}

@Composable
private fun TransactionBreakdownSection(state: LoanRepaymentState) {
    Text(
        text = stringResource(Res.string.feature_loan_transaction_breakdown),
        style = KptTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = KptTheme.colorScheme.onBackground,
    )

    Spacer(modifier = Modifier.height(DesignToken.spacing.small))

    FarApartTextItem(
        title = stringResource(Res.string.feature_loan_principal),
        value = formatCurrency(state.principalPortion, state.currencyCode, state.decimalPlaces),
    )
    FarApartTextItem(
        title = stringResource(Res.string.feature_loan_interest),
        value = formatCurrency(state.interestPortion, state.currencyCode, state.decimalPlaces),
    )
    FarApartTextItem(
        title = stringResource(Res.string.feature_loan_fees),
        value = formatCurrency(state.feeChargesPortion, state.currencyCode, state.decimalPlaces),
    )
    FarApartTextItem(
        title = stringResource(Res.string.feature_loan_penalties),
        value = formatCurrency(
            state.penaltyChargesPortion,
            state.currencyCode,
            state.decimalPlaces,
        ),
    )
}

@Composable
private fun WaivePenaltiesToggle(
    state: LoanRepaymentState,
    onAction: (LoanRepaymentAction) -> Unit,
) {
    val hasPenalties = (state.loanRepaymentTemplate?.penaltyChargesPortion ?: 0.0) > 0.0

    if (hasPenalties) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.feature_loan_waive_penalties),
                style = KptTheme.typography.bodyLarge,
            )
            Switch(
                checked = state.waivePenalties,
                onCheckedChange = { onAction(LoanRepaymentAction.WaivePenaltiesToggled(it)) },
            )
        }
    } else {
        Text(
            text = stringResource(Res.string.feature_loan_no_penalties_found),
            style = KptTheme.typography.bodyMedium,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ShowPaymentDetailsToggle(
    state: LoanRepaymentState,
    onAction: (LoanRepaymentAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.feature_loan_show_payment_details),
            style = KptTheme.typography.bodyLarge,
        )
        Switch(
            checked = state.showPaymentDetails,
            onCheckedChange = { onAction(LoanRepaymentAction.ShowPaymentDetailsToggled(it)) },
        )
    }

    AnimatedVisibility(
        visible = state.showPaymentDetails,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        Column {
            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            MifosOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = DesignToken.sizes.inputHeight),
                value = state.accountNumber,
                onValueChange = { onAction(LoanRepaymentAction.AccountNumberChanged(it)) },
                label = stringResource(Res.string.feature_loan_account_number),
                error = null,
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            MifosOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = DesignToken.sizes.inputHeight),
                value = state.chequeNumber,
                onValueChange = { onAction(LoanRepaymentAction.ChequeNumberChanged(it)) },
                label = stringResource(Res.string.feature_loan_cheque_number),
                error = null,
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            MifosOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = DesignToken.sizes.inputHeight),
                value = state.routingCode,
                onValueChange = { onAction(LoanRepaymentAction.RoutingCodeChanged(it)) },
                label = stringResource(Res.string.feature_loan_routing_code),
                error = null,
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            MifosOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = DesignToken.sizes.inputHeight),
                value = state.receiptNumber,
                onValueChange = { onAction(LoanRepaymentAction.ReceiptNumberChanged(it)) },
                label = stringResource(Res.string.feature_loan_receipt_number),
                error = null,
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            MifosOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = DesignToken.sizes.inputHeight),
                value = state.bankNumber,
                onValueChange = { onAction(LoanRepaymentAction.BankNumberChanged(it)) },
                label = stringResource(Res.string.feature_loan_bank_number),
                error = null,
            )
        }
    }
}

@Composable
private fun FarApartTextItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = KptTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            style = KptTheme.typography.bodyLarge,
            text = title,
            color = Black,
        )

        Text(
            style = KptTheme.typography.bodyLarge,
            text = value,
            color = DarkGray,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ConfirmationBottomSheet(
    state: LoanRepaymentState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    MifosBottomSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .padding(horizontal = KptTheme.spacing.md)
                .padding(bottom = KptTheme.spacing.lg)
                .navigationBarsPadding(),
        ) {
            Text(
                text = stringResource(Res.string.feature_loan_review_payment),
                style = KptTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = KptTheme.spacing.md),
            )

            ReviewItem(
                stringResource(Res.string.feature_loan_account_number),
                state.loanAccountNumber,
            )
            ReviewItem(
                stringResource(Res.string.feature_loan_repayment_date),
                DateHelper.getDateAsStringFromLong(state.repaymentDate),
            )
            ReviewItem(
                stringResource(Res.string.feature_loan_payment_type),
                state.paymentType,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = KptTheme.spacing.sm))

            ReviewItem(
                stringResource(Res.string.feature_loan_principal),
                formatCurrency(state.principalPortion, state.currencyCode, state.decimalPlaces),
            )
            ReviewItem(
                stringResource(Res.string.feature_loan_interest),
                formatCurrency(state.interestPortion, state.currencyCode, state.decimalPlaces),
            )
            ReviewItem(
                stringResource(Res.string.feature_loan_fees),
                formatCurrency(state.feeChargesPortion, state.currencyCode, state.decimalPlaces),
            )
            ReviewItem(
                stringResource(Res.string.feature_loan_penalties),
                formatCurrency(
                    state.penaltyChargesPortion,
                    state.currencyCode,
                    state.decimalPlaces,
                ),
            )

            if (state.note.isNotBlank()) {
                ReviewItem(stringResource(Res.string.feature_loan_note), state.note)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = KptTheme.spacing.sm))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = KptTheme.spacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.feature_loan_total),
                    style = KptTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = formatCurrency(
                        state.transactionAmount.toDoubleOrNull(),
                        state.currencyCode,
                        state.decimalPlaces,
                    ),
                    style = KptTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = KptTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

            MifosTwoButtonRow(
                firstBtnText = stringResource(Res.string.feature_loan_cancel),
                secondBtnText = stringResource(Res.string.feature_loan_dialog_action_pay_now),
                onFirstBtnClick = onDismiss,
                onSecondBtnClick = onConfirm,
                isButtonIconVisible = false,
            )
        }
    }
}

@Composable
private fun ReviewItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = KptTheme.spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = KptTheme.typography.bodyMedium,
            color = KptTheme.colorScheme.surfaceVariant,
        )
        Text(
            text = value,
            style = KptTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun SuccessBottomSheet(
    response: LoanRepaymentResponseEntity,
    onDismiss: () -> Unit,
) {
    MifosBottomSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.lg)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = MifosIcons.ApproveAccount,
                contentDescription = null,
                tint = AppColors.customEnable,
                modifier = Modifier.size(DesignToken.sizes.avatarLarge),
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            Text(
                text = stringResource(Res.string.feature_loan_payment_success_title),
                style = KptTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

            Text(
                text = stringResource(
                    Res.string.feature_loan_payment_success_transaction_label,
                    response.resourceId.toString(),
                ),
                style = KptTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignToken.sizes.buttonHeight),
            ) {
                Text(stringResource(Res.string.feature_loan_dialog_action_ok))
            }
        }
    }
}

private fun formatCurrency(amount: Double?, code: String?, decimalPlaces: Int?): String {
    return LoanRepaymentViewModel.formatCurrency(amount, code, decimalPlaces)
}

private class LoanRepaymentScreenPreviewProvider :
    PreviewParameterProvider<LoanRepaymentState> {

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

    @OptIn(ExperimentalTime::class)
    override val values: Sequence<LoanRepaymentState>
        get() = sequenceOf(
            LoanRepaymentState(
                clientName = "Ben Kiko",
                loanId = 2,
                loanProductName = "Product name",
                amountInArrears = 23.333,
                loanAccountNumber = "25",
                loanRepaymentTemplate = sampleLoanRepaymentTemplate,
                repaymentDate = Clock.System.now().toEpochMilliseconds(),
                isLoading = false,
            ),
            LoanRepaymentState(
                isLoading = true,
            ),
            LoanRepaymentState(
                isLoading = false,
                dialogState = LoanRepaymentState.DialogState.Error(
                    Res.string.feature_loan_failed_to_load_loan_repayment,
                ),
            ),
        )
}

@Composable
@Preview
private fun PreviewLoanRepaymentScreen(
    @PreviewParameter(LoanRepaymentScreenPreviewProvider::class) state: LoanRepaymentState,
) {
    LoanRepaymentScreen(
        state = state,
        onAction = {},
    )
}
