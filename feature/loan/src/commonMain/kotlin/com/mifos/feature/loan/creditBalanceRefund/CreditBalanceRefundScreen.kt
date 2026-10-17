/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.model.objects.account.loan.creditBalanceRefund.CreditBalanceRefundInput
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun CreditBalanceRefundScreen(
    navigateBack: () -> Unit,
    navController: NavController,
    onRefreshParent: () -> Unit,
    viewModel: CreditBalanceRefundViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is CreditBalanceRefundEvent.NavigateBack -> navigateBack()
            is CreditBalanceRefundEvent.NavigateBackWithRefresh -> {
                onRefreshParent()
                navigateBack()
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
                        state = state,
                        onAction = viewModel::trySendAction,
                    )
                } else if (state.viewState == null) {
                    MifosProgressIndicator()
                }

                when (val viewState = state.viewState) {
                    is CreditBalanceRefundState.ViewState.Loading -> {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = KptTheme.colorScheme.background,
                        ) {
                            MifosProgressIndicator()
                        }
                    }

                    is CreditBalanceRefundState.ViewState.Success -> {
                        SuccessBottomSheet(
                            transactionId = viewState.transactionId,
                            onDismiss = {
                                viewModel.trySendAction(CreditBalanceRefundAction.OnDismissBottomSheet)
                            },
                        )
                    }

                    is CreditBalanceRefundState.ViewState.Error -> {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = KptTheme.colorScheme.background,
                        ) {
                            MifosSweetError(
                                message = viewState.messageRes?.let { stringResource(it) } ?: "",
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
    state: CreditBalanceRefundState,
    onAction: (CreditBalanceRefundAction) -> Unit,
) {
    var transactionAmount by rememberSaveable { mutableStateOf(if (state.overpaidAmount > 0) state.overpaidAmount.toString() else "") }
    var externalId by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }

    val formattedOverpaidAmount = if (state.currencyCode != null) {
        CurrencyFormatter.format(state.overpaidAmount, state.currencyCode, state.decimalPlaces)
    } else {
        state.overpaidAmount.toString()
    }

    val amountDouble = transactionAmount.toDoubleOrNull()
    val amountErrorRes = when {
        transactionAmount.isBlank() -> Res.string.feature_loan_credit_balance_refund_error_amount_required
        amountDouble == null -> Res.string.feature_loan_credit_balance_refund_error_amount_invalid
        amountDouble <= 0 -> Res.string.feature_loan_credit_balance_refund_error_amount_non_positive
        amountDouble > state.overpaidAmount -> Res.string.feature_loan_credit_balance_refund_error_amount_exceeds_overpaid
        else -> null
    }

    val amountError = amountErrorRes?.let {
        if (it == Res.string.feature_loan_credit_balance_refund_error_amount_exceeds_overpaid) {
            stringResource(it, formattedOverpaidAmount)
        } else {
            stringResource(it)
        }
    }

    val isFormValid = state.transactionDate.isNotBlank() && amountErrorRes == null

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
                text = state.clientName ?: stringResource(Res.string.feature_loan_profile_label_client_name_placeholder),
                style = KptTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
            Text(
                text = stringResource(Res.string.feature_loan_credit_balance_refund_loan_account_number),
                style = KptTheme.typography.labelMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = state.loanAccountNumber, style = KptTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

        MifosOutlinedTextField(
            value = state.transactionDate,
            label = stringResource(Res.string.feature_loan_credit_balance_refund_transaction_date) + " *",
            readOnly = true,
            enabled = false,
            onValueChange = { },
        )

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
            onFirstBtnClick = { onAction(CreditBalanceRefundAction.NavigateBack) },
            onSecondBtnClick = {
                onAction(
                    CreditBalanceRefundAction.OnSubmitRefund(
                        CreditBalanceRefundInput(
                            transactionDate = state.transactionDate,
                            transactionAmount = transactionAmount.toDoubleOrNull() ?: 0.0,
                            dateFormat = "dd MMMM yyyy",
                            locale = "en",
                            externalId = externalId.ifBlank { null },
                            note = note.ifBlank { null },
                        ),
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
