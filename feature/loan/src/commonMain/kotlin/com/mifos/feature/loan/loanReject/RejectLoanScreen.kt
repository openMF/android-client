/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanReject

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_reject_discard_confirm
import androidclient.feature.loan.generated.resources.feature_loan_reject_discard_message
import androidclient.feature.loan.generated.resources.feature_loan_reject_discard_title
import androidclient.feature.loan.generated.resources.feature_loan_reject_note_hint
import androidclient.feature.loan.generated.resources.feature_loan_reject_title
import androidclient.feature.loan.generated.resources.feature_loan_rejected_on_label
import androidclient.feature.loan.generated.resources.feature_loan_select_date
import androidclient.feature.loan.generated.resources.feature_loan_submit
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper.toDisplayDate
import com.mifos.core.common.utils.DateHelper.toEpochMillis
import com.mifos.core.common.utils.DateHelper.toLocalDate
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosDialogBox
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun RejectLoanScreen(
    navigateBack: () -> Unit,
    onRejectSuccess: () -> Unit,
    viewModel: RejectLoanViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            RejectLoanEvent.NavigateBack -> navigateBack()
            RejectLoanEvent.RejectSuccess -> onRejectSuccess()
            is RejectLoanEvent.SubmissionError -> {
                snackbarHostState.showSnackbar(message = event.message)
            }
        }
    }

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        title = stringResource(Res.string.feature_loan_reject_title),
        onBackPressed = { viewModel.trySendAction(RejectLoanAction.CancelClicked) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            RejectLoanContent(
                state = state,
                onAction = viewModel::trySendAction,
            )

            MifosDialogBox(
                title = stringResource(Res.string.feature_loan_reject_discard_title),
                showDialogState = state.showDiscardDialog,
                confirmButtonText = stringResource(Res.string.feature_loan_reject_discard_confirm),
                dismissButtonText = stringResource(Res.string.feature_loan_cancel),
                onConfirm = { viewModel.trySendAction(RejectLoanAction.DiscardConfirmed) },
                onDismiss = { viewModel.trySendAction(RejectLoanAction.DiscardDismissed) },
                message = stringResource(Res.string.feature_loan_reject_discard_message),
            )

            if (state.isLoading) {
                MifosProgressIndicatorOverlay()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RejectLoanContent(
    state: RejectLoanViewState,
    onAction: (RejectLoanAction) -> Unit,
) {
    val rejectedOnLabel = stringResource(Res.string.feature_loan_rejected_on_label)
    val noteLabel = stringResource(Res.string.feature_loan_reject_note_hint)
    val cancelLabel = stringResource(Res.string.feature_loan_cancel)
    val submitLabel = stringResource(Res.string.feature_loan_submit)

    var showDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.rejectedOnDate.toEpochMillis(),
    )

    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onAction(
                                RejectLoanAction.RejectedOnDateChanged(
                                    millis.toLocalDate(),
                                ),
                            )
                        }
                        showDatePickerDialog = false
                    },
                ) {
                    Text(stringResource(Res.string.feature_loan_select_date))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePickerDialog = false },
                ) {
                    Text(stringResource(Res.string.feature_loan_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = KptTheme.spacing.md, vertical = KptTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        MifosDatePickerTextField(
            value = state.rejectedOnDate.toDisplayDate(),
            label = rejectedOnLabel,
            errorMessage = state.rejectedOnDateError,
            openDatePicker = { showDatePickerDialog = true },
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = rejectedOnLabel
                    state.rejectedOnDateError?.let { error(it) }
                },
        )

        MifosOutlinedTextField(
            value = state.note,
            onValueChange = { onAction(RejectLoanAction.NoteChanged(it)) },
            label = noteLabel,
            config = MifosTextFieldConfig(
                enabled = !state.isLoading,
                singleLine = false,
                minLines = 3,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                showClearIcon = false,
            ),
            modifier = Modifier.semantics {
                contentDescription = noteLabel
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
        ) {
            MifosOutlinedButton(
                text = { Text(text = cancelLabel) },
                onClick = { onAction(RejectLoanAction.CancelClicked) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        contentDescription = cancelLabel
                        if (state.isLoading) disabled()
                    },
            )

            MifosButton(
                text = { Text(text = submitLabel) },
                onClick = { onAction(RejectLoanAction.SubmitClicked) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        contentDescription = submitLabel
                        if (state.isLoading) disabled()
                    },
            )
        }
    }
}
