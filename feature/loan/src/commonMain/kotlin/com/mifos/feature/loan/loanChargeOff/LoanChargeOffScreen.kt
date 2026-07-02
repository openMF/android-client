/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.loan.loanChargeOff

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.cancel
import androidclient.feature.loan.generated.resources.feature_loan_charge_off
import androidclient.feature.loan.generated.resources.feature_loan_charge_off_external_id
import androidclient.feature.loan.generated.resources.feature_loan_charge_off_no_reasons_available
import androidclient.feature.loan.generated.resources.feature_loan_charge_off_note
import androidclient.feature.loan.generated.resources.feature_loan_charge_off_please_select_reason
import androidclient.feature.loan.generated.resources.feature_loan_charge_off_reason
import androidclient.feature.loan.generated.resources.feature_loan_charge_off_submit
import androidclient.feature.loan.generated.resources.feature_loan_charge_off_transaction_date
import androidclient.feature.loan.generated.resources.ok
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun LoanChargeOffScreen(
    onNavigateBack: () -> Unit,
    onChargeOffSuccess: (loanId: Int) -> Unit,
    viewModel: LoanChargeOffViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanChargeOffEvent.NavigateBack -> onNavigateBack()
            LoanChargeOffEvent.ChargeOffSuccess -> onChargeOffSuccess(state.loanId)
        }
    }

    LoanChargeOffScreenContent(
        state = state,
        onAction = remember(viewModel) { viewModel::trySendAction },
    )

    LoanChargeOffDialog(
        dialogMessage = state.dialogMessage,
        onDismissDialog = { viewModel.trySendAction(LoanChargeOffAction.DismissDialog) },
    )
}

@Composable
private fun LoanChargeOffScreenContent(
    state: LoanChargeOffState,
    onAction: (LoanChargeOffAction) -> Unit,
) {
    MifosScaffold(
        title = stringResource(Res.string.feature_loan_charge_off),
        onBackPressed = { onAction(LoanChargeOffAction.NavigateBack) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            when (state.viewState) {
                is LoanChargeOffState.ViewState.Loading -> {
                    MifosProgressIndicator()
                }

                is LoanChargeOffState.ViewState.Error -> {
                    MifosSweetError(
                        message = stringResource(state.viewState.message),
                        onclick = { onAction(LoanChargeOffAction.OnRetry) },
                    )
                }

                is LoanChargeOffState.ViewState.Success -> {
                    LoanChargeOffForm(
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
private fun LoanChargeOffForm(
    state: LoanChargeOffState,
    onAction: (LoanChargeOffAction) -> Unit,
) {
    val scrollState = rememberScrollState()
    val dueDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.transactionDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val currentMillis = Clock.System.now().toEpochMilliseconds()
                val startOfTodayUtc = currentMillis - (currentMillis % 86_400_000L)
                return utcTimeMillis >= startOfTodayUtc
            }
        },
    )

    LaunchedEffect(state.transactionDate) {
        dueDatePickerState.selectedDateMillis = state.transactionDate
    }

    if (state.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onAction(LoanChargeOffAction.HideDatePicker) },
            confirmButton = {
                TextButton(
                    onClick = {
                        dueDatePickerState.selectedDateMillis?.let {
                            onAction(LoanChargeOffAction.TransactionDateChanged(it))
                        }
                        onAction(LoanChargeOffAction.HideDatePicker)
                    },
                ) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { onAction(LoanChargeOffAction.HideDatePicker) },
                ) { Text(stringResource(Res.string.cancel)) }
            },
        ) {
            DatePicker(state = dueDatePickerState)
        }
    }

    val viewState = state.viewState
    val reasonOptions = if (viewState is LoanChargeOffState.ViewState.Success) {
        viewState.reasonOptions
    } else {
        emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(KptTheme.spacing.md),
    ) {
        MifosTextFieldDropdown(
            value = state.selectedReason?.name ?: "",
            onValueChanged = { },
            label = stringResource(Res.string.feature_loan_charge_off_reason) + "*",
            readOnly = true,
            onOptionSelected = { index, _ -> onAction(LoanChargeOffAction.ReasonSelected(index)) },
            options = reasonOptions.map { it.name },
            errorMessage = if (state.isReasonError) {
                stringResource(Res.string.feature_loan_charge_off_please_select_reason)
            } else if (reasonOptions.isEmpty()) {
                stringResource(Res.string.feature_loan_charge_off_no_reasons_available)
            } else {
                null
            },
        )

        MifosDatePickerTextField(
            value = state.transactionDateText,
            label = stringResource(Res.string.feature_loan_charge_off_transaction_date) + "*",
            openDatePicker = { onAction(LoanChargeOffAction.ShowDatePicker) },
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        MifosOutlinedTextField(
            value = state.externalId,
            onValueChange = { onAction(LoanChargeOffAction.ExternalIdChanged(it)) },
            label = stringResource(Res.string.feature_loan_charge_off_external_id),
            keyboardType = KeyboardType.Text,
        )

        MifosOutlinedTextField(
            value = state.note,
            onValueChange = { onAction(LoanChargeOffAction.NoteChanged(it)) },
            label = stringResource(Res.string.feature_loan_charge_off_note),
            maxLines = 4,
            singleLine = false,
            keyboardType = KeyboardType.Text,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        MifosButton(
            onClick = { onAction(LoanChargeOffAction.Submit) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSubmitting && state.selectedReason != null,
        ) {
            Text(stringResource(Res.string.feature_loan_charge_off_submit))
        }
    }
}

@Composable
private fun LoanChargeOffDialog(
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
