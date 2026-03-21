/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.creditBalanceRefund

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_cancel
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_client_name
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_error_amount_exceeds_overpaid
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_error_amount_invalid
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_error_amount_non_positive
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_error_amount_required
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_external_id
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_loan_account_number
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_note
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_ok
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_submit
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_success_message
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_success_title
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_success_transaction_id
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_title
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_transaction_amount
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_transaction_date
import androidclient.feature.loan.generated.resources.feature_loan_profile_label_client_name_placeholder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.room.entities.accounts.loans.CreditBalanceRefundRequest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock

/**
 * Main screen composable for Credit Balance Refund feature.
 * Observes the ViewModel state and dynamically renders overlays (loading/errors)
 * on top of the refund form.
 */
@Composable
internal fun CreditBalanceRefundScreen(
    navigateBack: () -> Unit,
    navController: NavController,
    onRefreshParent: () -> Unit,
    viewModel: CreditBalanceRefundViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is CreditBalanceRefundEvent.NavigateBack -> {
                    navigateBack()
                }
                is CreditBalanceRefundEvent.NavigateBackWithRefresh -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh_loan_details", true)
                    onRefreshParent()
                    navigateBack()
                }
            }
        }
    }

    MifosScaffold(
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            MifosBreadcrumbNavBar(navController)

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.clientName != null || !state.networkAvailable) {
                    CreditBalanceRefundContent(
                        clientName = state.clientName,
                        loanAccountNumber = state.loanAccountNumber,
                        overpaidAmount = state.overpaidAmount,
                        currencyCode = state.currencyCode,
                        decimalPlaces = state.decimalPlaces,
                        onSubmit = { request ->
                            viewModel.trySendAction(CreditBalanceRefundAction.OnSubmitRefund(request))
                        },
                        onCancel = {
                            viewModel.trySendAction(CreditBalanceRefundAction.NavigateBack)
                        },
                    )
                } else if (state.dialogState == null) {
                    MifosProgressIndicator()
                }

                when (val dialogState = state.dialogState) {
                    is CreditBalanceRefundState.DialogState.Loading -> {
                        androidx.compose.material3.Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = template.core.base.designsystem.theme.KptTheme.colorScheme.background,
                        ) {
                            MifosProgressIndicator()
                        }
                    }

                    is CreditBalanceRefundState.DialogState.Success -> {
                        SuccessBottomSheet(
                            transactionId = dialogState.transactionId,
                            onDismiss = {
                                viewModel.trySendAction(CreditBalanceRefundAction.OnDismissDialog)
                            },
                        )
                    }

                    is CreditBalanceRefundState.DialogState.Error -> {
                        androidx.compose.material3.Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = template.core.base.designsystem.theme.KptTheme.colorScheme.background,
                        ) {
                            MifosSweetError(
                                message = dialogState.messageRes?.let { stringResource(it) } ?: dialogState.message ?: "",
                                onclick = {
                                    viewModel.trySendAction(CreditBalanceRefundAction.OnRetry)
                                },
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreditBalanceRefundContent(
    clientName: String?,
    loanAccountNumber: String,
    overpaidAmount: Double,
    currencyCode: String?,
    decimalPlaces: Int?,
    onSubmit: (CreditBalanceRefundRequest) -> Unit,
    onCancel: () -> Unit,
) {
    var transactionDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var transactionDateStr by rememberSaveable { mutableStateOf("") }
    var transactionAmount by rememberSaveable { mutableStateOf(if (overpaidAmount > 0) overpaidAmount.toString() else "") }
    var externalId by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }

    var showDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    val formattedOverpaidAmount = if (currencyCode != null) {
        CurrencyFormatter.format(overpaidAmount, currencyCode, decimalPlaces)
    } else {
        overpaidAmount.toString()
    }

    val amountDouble = transactionAmount.toDoubleOrNull()
    val amountErrorRes = when {
        transactionAmount.isBlank() -> Res.string.feature_loan_credit_balance_refund_error_amount_required
        amountDouble == null -> Res.string.feature_loan_credit_balance_refund_error_amount_invalid
        amountDouble <= 0 -> Res.string.feature_loan_credit_balance_refund_error_amount_non_positive
        amountDouble > overpaidAmount -> Res.string.feature_loan_credit_balance_refund_error_amount_exceeds_overpaid
        else -> null
    }

    val amountError = amountErrorRes?.let {
        if (it == Res.string.feature_loan_credit_balance_refund_error_amount_exceeds_overpaid) {
            stringResource(it, formattedOverpaidAmount)
        } else {
            stringResource(it)
        }
    }

    val isFormValid = transactionDateStr.isNotBlank() && amountErrorRes == null

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = transactionDateMillis ?: Clock.System.now()
            .toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val dateStr = DateHelper.getDateMonthYearStringFromLong(utcTimeMillis)
                val todayStr = DateHelper.getDateMonthYearStringFromLong(Clock.System.now().toEpochMilliseconds())
                return dateStr == todayStr
            }
        },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(KptTheme.spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.feature_loan_credit_balance_refund_title),
            style = MifosTypography.labelLargeEmphasized,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        Column {
            Text(
                text = stringResource(Res.string.feature_loan_credit_balance_refund_client_name),
                style = KptTheme.typography.labelMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = clientName ?: stringResource(Res.string.feature_loan_profile_label_client_name_placeholder),
                style = KptTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
            Text(
                text = stringResource(Res.string.feature_loan_credit_balance_refund_loan_account_number),
                style = KptTheme.typography.labelMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = loanAccountNumber, style = KptTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

        MifosDatePickerTextField(
            value = transactionDateStr,
            label = stringResource(Res.string.feature_loan_credit_balance_refund_transaction_date) + " *",
            openDatePicker = { showDatePickerDialog = true },
        )

        if (showDatePickerDialog) {
            DatePickerDialog(
                onDismissRequest = { showDatePickerDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                transactionDateMillis = it
                                transactionDateStr = DateHelper.getDateMonthYearStringFromLong(it)
                            }
                            showDatePickerDialog = false
                        },
                    ) { Text(stringResource(Res.string.feature_loan_credit_balance_refund_ok)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePickerDialog = false }) {
                        Text(stringResource(Res.string.feature_loan_credit_balance_refund_cancel))
                    }
                },
            ) { DatePicker(state = datePickerState) }
        }

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        MifosOutlinedTextField(
            value = transactionAmount,
            onValueChange = { transactionAmount = it },
            label = stringResource(Res.string.feature_loan_credit_balance_refund_transaction_amount) + " *",
            message = "Max: $formattedOverpaidAmount",
            errorText = amountError,
            isError = amountError != null,
            keyboardType = KeyboardType.Decimal,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        MifosOutlinedTextField(
            value = externalId,
            onValueChange = { externalId = it },
            label = stringResource(Res.string.feature_loan_credit_balance_refund_external_id),
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        MifosOutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = stringResource(Res.string.feature_loan_credit_balance_refund_note),
            maxLines = 5,
            singleLine = false,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.xl))

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_loan_credit_balance_refund_cancel),
            secondBtnText = stringResource(Res.string.feature_loan_credit_balance_refund_submit),
            onFirstBtnClick = onCancel,
            onSecondBtnClick = {
                onSubmit(
                    CreditBalanceRefundRequest(
                        transactionDate = transactionDateStr,
                        transactionAmount = transactionAmount.toDoubleOrNull() ?: 0.0,
                        dateFormat = "dd MMMM yyyy",
                        locale = "en",
                        externalId = externalId.ifBlank { null },
                        note = note.ifBlank { null },
                    ),
                )
            },
            isSecondButtonEnabled = isFormValid,
        )
    }
}

@Composable
private fun SuccessBottomSheet(transactionId: String, onDismiss: () -> Unit) {
    MifosBottomSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(KptTheme.spacing.lg).navigationBarsPadding(),
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
                text = stringResource(Res.string.feature_loan_credit_balance_refund_success_title),
                style = KptTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(Res.string.feature_loan_credit_balance_refund_success_message),
                style = KptTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            if (transactionId.isNotBlank() && transactionId != "null") {
                Text(
                    text = stringResource(
                        Res.string.feature_loan_credit_balance_refund_success_transaction_id,
                        transactionId,
                    ),
                    style = KptTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(KptTheme.spacing.lg))
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(DesignToken.sizes.buttonHeight)) {
                Text(stringResource(Res.string.feature_loan_credit_balance_refund_ok))
            }
        }
    }
}
