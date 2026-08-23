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

package com.mifos.feature.loan.loanReject

import kpt.feature.loan.generated.resources.Res
import kpt.feature.loan.generated.resources.cancel
import kpt.feature.loan.generated.resources.feature_loan_note_optional
import kpt.feature.loan.generated.resources.feature_loan_reject
import kpt.feature.loan.generated.resources.feature_loan_reject_date
import kpt.feature.loan.generated.resources.feature_loan_reject_submit
import kpt.feature.loan.generated.resources.ok
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kpt.core.base.designsystem.KptTheme
import kpt.core.base.designsystem.theme.LocalKptSpacing
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun LoanRejectScreen(
    onNavigateBack: () -> Unit,
    onRejectSuccess: (loanId: Int) -> Unit,
    viewModel: LoanRejectViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanRejectEvent.NavigateBack -> onNavigateBack()
            LoanRejectEvent.RejectSuccess -> onRejectSuccess(state.loanId)
        }
    }

    LoanRejectScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
    )

    LoanRejectDialog(
        dialogMessage = state.dialogMessage,
        onDismissDialog = { viewModel.trySendAction(LoanRejectAction.DismissDialog) },
    )
}

@Composable
private fun LoanRejectScreenContent(
    state: LoanRejectState,
    onAction: (LoanRejectAction) -> Unit,
) {
    MifosScaffold(
        title = stringResource(Res.string.feature_loan_reject),
        onBackPressed = { onAction(LoanRejectAction.NavigateBack) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            LoanRejectForm(state = state, onAction = onAction)
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun LoanRejectForm(
    state: LoanRejectState,
    onAction: (LoanRejectAction) -> Unit,
) {
    val scrollState = rememberScrollState()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.rejectedOnDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val currentMillis = Clock.System.now().toEpochMilliseconds()
                val startOfTodayUtc = currentMillis - (currentMillis % 86_400_000L)
                return utcTimeMillis <= startOfTodayUtc
            }
        },
    )

    LaunchedEffect(state.rejectedOnDate) {
        datePickerState.selectedDateMillis = state.rejectedOnDate
    }

    if (state.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onAction(LoanRejectAction.HideDatePicker) },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onAction(LoanRejectAction.RejectedOnDateChanged(it))
                        }
                        onAction(LoanRejectAction.HideDatePicker)
                    },
                ) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { onAction(LoanRejectAction.HideDatePicker) }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(LocalKptSpacing.current.md),
        ) {
            MifosDatePickerTextField(
                value = state.rejectedOnDateText,
                label = stringResource(Res.string.feature_loan_reject_date) + "*",
                openDatePicker = { onAction(LoanRejectAction.ShowDatePicker) },
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            MifosOutlinedTextField(
                value = state.note,
                onValueChange = { onAction(LoanRejectAction.NoteChanged(it)) },
                label = stringResource(Res.string.feature_loan_note_optional),
                maxLines = 4,
                singleLine = false,
                keyboardType = KeyboardType.Text,
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            MifosButton(
                onClick = { onAction(LoanRejectAction.Submit) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting,
            ) {
                Text(stringResource(Res.string.feature_loan_reject_submit))
            }
        }

        if (state.isSubmitting) {
            MifosProgressIndicatorOverlay()
        }
    }
}

@Composable
private fun LoanRejectDialog(
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
