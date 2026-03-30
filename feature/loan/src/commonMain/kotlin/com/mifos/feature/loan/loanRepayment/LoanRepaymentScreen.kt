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
import androidclient.feature.loan.generated.resources.feature_loan_payment_success_title
import androidclient.feature.loan.generated.resources.feature_loan_payment_success_transaction_label
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
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Constants
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
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
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
    viewmodel: LoanRepaymentViewModel = koinViewModel(),
) {
    val uiState by viewmodel.loanRepaymentUiState.collectAsStateWithLifecycle()
    val loanDetails by viewmodel.loanDetailsState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        if (loanDetails.loanAccountNumber.isNotEmpty()) {
            viewmodel.checkDatabaseLoanRepaymentByLoanId()
        }
    }

    LoanRepaymentScreen(
        loanId = loanDetails.loanId,
        clientName = loanDetails.clientName,
        loanProductName = loanDetails.loanProductName,
        amountInArrears = loanDetails.amountInArrears,
        loanAccountNumber = loanDetails.loanAccountNumber,
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = {
            if (loanDetails.loanAccountNumber.isEmpty()) {
                viewmodel.loadLoanById()
            } else {
                viewmodel.checkDatabaseLoanRepaymentByLoanId()
            }
        },
        submitPayment = {
            viewmodel.submitPayment(it)
        },
        onLoanRepaymentDoesNotExistInDatabase = {
            viewmodel.loadLoanRepaymentTemplate()
        },
        formatCurrency = viewmodel::formatCurrency,
        calculateTotal = viewmodel::calculateTotal,
        isAllFieldsValid = viewmodel::isAllFieldsValid,
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
    formatCurrency: (Double?, String?, Int?) -> String,
    calculateTotal: (String, String, String) -> Double,
    isAllFieldsValid: (String, String, String, String) -> Boolean,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

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
                        formatCurrency = formatCurrency,
                        calculateTotal = calculateTotal,
                        isAllFieldsValid = isAllFieldsValid,
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
                                style = KptTheme.typography.titleLarge,
                            )
                        },
                        text = { Text(text = stringResource(Res.string.feature_loan_dialog_message_sync_transaction)) },
                    )
                }

                is LoanRepaymentUiState.ShowPaymentSubmittedSuccessfully -> {
                    val response = uiState.loanRepaymentResponse
                    if (response != null) {
                        SuccessBottomSheet(
                            response = response,
                            onDismiss = navigateBack,
                        )
                    } else {
                        LaunchedEffect(Unit) { navigateBack() }
                    }
                }

                LoanRepaymentUiState.ShowProgressbar -> {
                    MifosProgressIndicator()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
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
    formatCurrency: (Double?, String?, Int?) -> String,
    calculateTotal: (String, String, String) -> Double,
    isAllFieldsValid: (String, String, String, String) -> Boolean,
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
    var showConfirmationSheet by rememberSaveable {
        mutableStateOf(false)
    }

    val currencyCode = loanRepaymentTemplate.currency?.code
    val decimalPlaces = loanRepaymentTemplate.currency?.decimalPlaces

    if (showConfirmationSheet) {
        ConfirmationBottomSheet(
            onDismiss = { showConfirmationSheet = false },
            loanAccountNumber = loanAccountNumber,
            paymentTypeId = paymentTypeId.toString(),
            repaymentDate = repaymentDate,
            paymentType = paymentType,
            amount = amount,
            additionalPayment = additionalPayment,
            fees = fees,
            total = calculateTotal(fees, amount, additionalPayment).toString(),
            submitPayment = submitPayment,
            currencyCode = currencyCode,
            decimalPlaces = decimalPlaces,
            formatCurrency = formatCurrency,
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
            .padding(horizontal = DesignToken.padding.medium)
            .verticalScroll(scrollState),
    ) {
        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        Text(
            style = KptTheme.typography.bodyLarge,
            color = KptTheme.colorScheme.onBackground,
            text = clientName,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        FarApartTextItem(title = loanProductName, value = loanId.toString())
        FarApartTextItem(
            title = stringResource(Res.string.feature_loan_loan_in_arrears),
            value = formatCurrency(amountInArrears, currencyCode, decimalPlaces),
        )
        FarApartTextItem(
            title = stringResource(Res.string.feature_loan_loan_amount_due),
            value = formatCurrency(loanRepaymentTemplate.amount, currencyCode, decimalPlaces),
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        MifosDatePickerTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DesignToken.sizes.inputHeight),
            value = DateHelper.getDateAsStringFromLong(
                repaymentDate,
            ),
            label = stringResource(Res.string.feature_loan_repayment_date),
        ) {
            showDatePickerDialog = true
        }

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        MifosTextFieldDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DesignToken.sizes.inputHeight),
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

        MifosOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DesignToken.sizes.inputHeight),
            value = amount,
            onValueChange = {
                amount = it
            },
            label = stringResource(Res.string.feature_loan_amount),
            error = null,
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        MifosOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DesignToken.sizes.inputHeight),
            value = additionalPayment,
            onValueChange = {
                additionalPayment = it
            },
            label = stringResource(Res.string.feature_loan_additional_payment),
            error = null,
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        MifosOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DesignToken.sizes.inputHeight),
            value = fees,
            onValueChange = {
                fees = it
            },
            label = stringResource(Res.string.feature_loan_loan_fees),
            error = null,
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        val calculatedTotal = calculateTotal(fees, amount, additionalPayment)

        MifosOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DesignToken.sizes.inputHeight),
            value = formatCurrency(calculatedTotal, currencyCode, decimalPlaces),
            onValueChange = { },
            label = stringResource(Res.string.feature_loan_total),
            error = null,
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.extraLarge))

        val isValid = isAllFieldsValid(amount, additionalPayment, fees, paymentType)
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_loan_cancel),
            secondBtnText = stringResource(Res.string.feature_loan_review_payment),
            onFirstBtnClick = { navigateBack.invoke() },
            onSecondBtnClick = {
                if (isValid) {
                    showConfirmationSheet = true
                }
            },
            isSecondButtonEnabled = isValid,
            isButtonIconVisible = false,
        )
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
    currencyCode: String? = null,
    decimalPlaces: Int? = null,
    formatCurrency: (Double?, String?, Int?) -> String,
) {
    MifosBottomSheet(
        onDismiss = onDismiss,
    ) {
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

            ReviewItem(stringResource(Res.string.feature_loan_account_number), loanAccountNumber)
            ReviewItem(
                stringResource(Res.string.feature_loan_repayment_date),
                DateHelper.getDateAsStringFromLong(repaymentDate),
            )
            ReviewItem(stringResource(Res.string.feature_loan_payment_type), paymentType)
            HorizontalDivider(modifier = Modifier.padding(vertical = KptTheme.spacing.sm))
            ReviewItem(
                stringResource(Res.string.feature_loan_amount),
                formatCurrency(amount.toDoubleOrNull(), currencyCode, decimalPlaces),
            )
            ReviewItem(
                stringResource(Res.string.feature_loan_additional_payment),
                formatCurrency(additionalPayment.toDoubleOrNull(), currencyCode, decimalPlaces),
            )
            ReviewItem(
                stringResource(Res.string.feature_loan_loan_fees),
                formatCurrency(fees.toDoubleOrNull(), currencyCode, decimalPlaces),
            )

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
                    text = formatCurrency(total.toDoubleOrNull(), currencyCode, decimalPlaces),
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
                onSecondBtnClick = {
                    onDismiss()
                    val request = LoanRepaymentRequestEntity(
                        accountNumber = loanAccountNumber,
                        paymentTypeId = paymentTypeId,
                        dateFormat = DateHelper.SHORT_MONTH,
                        locale = Constants.LOCALE_EN,
                        transactionAmount = total,
                        transactionDate = DateHelper.getDateAsStringFromLong(repaymentDate),
                    )
                    submitPayment.invoke(request)
                },
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
        Text(text = label, style = KptTheme.typography.bodyMedium, color = KptTheme.colorScheme.surfaceVariant)
        Text(text = value, style = KptTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SuccessBottomSheet(
    response: LoanRepaymentResponseEntity,
    onDismiss: () -> Unit,
) {
    MifosBottomSheet(
        onDismiss = onDismiss,
    ) {
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
                text = stringResource(Res.string.feature_loan_payment_success_transaction_label, response.resourceId.toString()),
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
            LoanRepaymentUiState.ShowPaymentSubmittedSuccessfully(LoanRepaymentResponseEntity(resourceId = 123)),
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
        formatCurrency = { amount, code, _ -> "$code $amount" },
        calculateTotal = { _, _, _ -> 0.0 },
        isAllFieldsValid = { _, _, _, _ -> true },
    )
}
