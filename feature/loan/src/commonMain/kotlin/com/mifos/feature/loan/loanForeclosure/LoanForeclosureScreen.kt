/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanForeclosure

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_fee_amount
import androidclient.feature.loan.generated.resources.feature_loan_foreclosure
import androidclient.feature.loan.generated.resources.feature_loan_interest
import androidclient.feature.loan.generated.resources.feature_loan_note
import androidclient.feature.loan.generated.resources.feature_loan_penalty_amount
import androidclient.feature.loan.generated.resources.feature_loan_principal
import androidclient.feature.loan.generated.resources.feature_loan_select
import androidclient.feature.loan.generated.resources.feature_loan_transaction_amount_mandatory
import androidclient.feature.loan.generated.resources.feature_loan_transaction_date
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock

@Composable
internal fun LoanForeclosureScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onSubmitted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoanForeclosureViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ForeclosureEvent.NavigateBack -> onNavigateBack()
            ForeclosureEvent.Finish -> onSubmitted()
            is ForeclosureEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
        }
    }

    MifosScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            MifosBreadcrumbNavBar(navController)

            when (state.screenState) {
                is ForeclosureState.ScreenState.Loading -> MifosProgressIndicator()

                is ForeclosureState.ScreenState.Error -> MifosSweetError(
                    message = (state.screenState as ForeclosureState.ScreenState.Error).message,
                    onclick = { viewModel.trySendAction(ForeclosureAction.Retry) },
                )

                is ForeclosureState.ScreenState.Success -> ForeclosureFormContent(
                    state = state,
                    onAction = viewModel::trySendAction,
                )
            }
        }

        if (state.isOverLayLoadingActive) {
            MifosProgressIndicatorOverlay()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForeclosureFormContent(
    state: ForeclosureState,
    onAction: (ForeclosureAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in state.minTransactionDateMillis..state.maxTransactionDateMillis
            }
        },
    )

    if (state.showTransactionDatePick) {
        DatePickerDialog(
            onDismissRequest = { onAction(ForeclosureAction.OnTransactionDatePick(false)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onAction(ForeclosureAction.OnTransactionDateSelected(millis))
                        } ?: onAction(ForeclosureAction.OnTransactionDatePick(false))
                    },
                ) { Text(stringResource(Res.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(onClick = { onAction(ForeclosureAction.OnTransactionDatePick(false)) }) {
                    Text(stringResource(Res.string.feature_loan_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = KptTheme.spacing.md, vertical = KptTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        MifosDatePickerTextField(
            value = state.transactionDate,
            label = stringResource(Res.string.feature_loan_transaction_date),
            openDatePicker = { onAction(ForeclosureAction.OnTransactionDatePick(true)) },
        )

        DisabledTextField(label = stringResource(Res.string.feature_loan_principal), value = state.principalPortion)
        DisabledTextField(label = stringResource(Res.string.feature_loan_interest), value = state.interestPortion)
        DisabledTextField(label = stringResource(Res.string.feature_loan_fee_amount), value = state.feeChargesPortion)
        DisabledTextField(label = stringResource(Res.string.feature_loan_penalty_amount), value = state.penaltyChargesPortion)
        DisabledTextField(label = stringResource(Res.string.feature_loan_transaction_amount_mandatory), value = state.transactionAmount)

        MifosOutlinedTextField(
            value = state.note,
            onValueChange = { onAction(ForeclosureAction.OnNoteChange(it)) },
            label = stringResource(Res.string.feature_loan_note),
        )

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_loan_cancel),
            secondBtnText = stringResource(Res.string.feature_loan_foreclosure),
            onFirstBtnClick = { onAction(ForeclosureAction.NavigateBack) },
            onSecondBtnClick = { onAction(ForeclosureAction.SubmitForeclosure) },
            isSecondButtonEnabled = state.isSubmitEnabled,
            modifier = modifier.padding(top = DesignToken.padding.large),
        )
    }
}

@Composable
private fun DisabledTextField(label: String, value: String) {
    MifosOutlinedTextField(
        value = value,
        onValueChange = {},
        label = label,
        config = MifosTextFieldConfig(
            readOnly = true,
            enabled = false,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}
