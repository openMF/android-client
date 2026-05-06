/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.mifos.feature.loan.closeLoanAccount

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_close_cancel
import androidclient.feature.loan.generated.resources.feature_loan_close_closed_on
import androidclient.feature.loan.generated.resources.feature_loan_close_disbursed_date_value
import androidclient.feature.loan.generated.resources.feature_loan_close_note
import androidclient.feature.loan.generated.resources.feature_loan_close_ok
import androidclient.feature.loan.generated.resources.feature_loan_close_submit
import androidclient.feature.loan.generated.resources.feature_loan_close_success
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_close_loan_title
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.LoadingDialogState
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosLoadingDialog
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

/**
 * Screen that lets a field officer close an active loan account.
 *
 * This screen allows users to close an active loan by selecting a closure date and
 * providing an optional note.
 *
 * @param onBackPressed invoked when the user taps back/cancel without closing the loan.
 * @param onCloseSuccess invoked after a successful close so the caller can refresh the profile.
 * @param viewModel The [CloseLoanViewModel] that manages the state for this screen.
 */
@Composable
internal fun CloseLoanScreen(
    onBackPressed: () -> Unit,
    onCloseSuccess: () -> Unit,
    viewModel: CloseLoanViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(Res.string.feature_loan_close_success)
    val isSubmitting = state.dialogState is CloseLoanState.DialogState.Submitting

    // Block system back gesture while submission is in flight.
    BackHandler(enabled = isSubmitting) {}

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            CloseLoanEvent.CloseSuccess -> {
                snackbarHostState.showSnackbar(successMessage)
                onCloseSuccess()
            }
            CloseLoanEvent.NavigateBack -> {
                onBackPressed()
            }
        }
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_loan_profile_item_close_loan_title),
        onBackPressed = { if (!isSubmitting) onBackPressed() },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                state.isTemplateLoading -> MifosProgressIndicator()
                state.loadError != null -> MifosErrorComponent(
                    isNetworkConnected = true,
                    message = stringResource(state.loadError!!),
                    isRetryEnabled = true,
                    onRetry = { viewModel.trySendAction(CloseLoanAction.OnRetryLoadTemplate) },
                )
                else -> CloseLoanContent(
                    state = state,
                    onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
                )
            }
        }

        CloseLoanDialogs(
            state = state,
            onDismissError = remember(viewModel) {
                { viewModel.trySendAction(CloseLoanAction.OnDismissError) }
            },
        )
    }
}

/**
 * The main form content for the Close Loan Account screen.
 *
 * @param state The current UI state.
 * @param onAction Callback to handle user actions.
 * @param modifier Modifier for the layout.
 */
@Composable
private fun CloseLoanContent(
    state: CloseLoanState,
    onAction: (CloseLoanAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSubmitting = state.dialogState is CloseLoanState.DialogState.Submitting
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.closedOnDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val floor = state.disbursementDateMillis
                return floor == null || utcTimeMillis >= floor
            }
        },
    )

    if (state.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onAction(CloseLoanAction.OnHideDatePicker) },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onAction(CloseLoanAction.OnDateChange(millis))
                        } ?: onAction(CloseLoanAction.OnHideDatePicker)
                    },
                ) { Text(stringResource(Res.string.feature_loan_close_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { onAction(CloseLoanAction.OnHideDatePicker) }) {
                    Text(stringResource(Res.string.feature_loan_close_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(KptTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

        MifosDatePickerTextField(
            value = state.closedOnDateMillis?.let(DateHelper::getDateAsStringFromLong).orEmpty(),
            label = stringResource(Res.string.feature_loan_close_closed_on),
            modifier = Modifier.fillMaxWidth(),
            openDatePicker = { onAction(CloseLoanAction.OnShowDatePicker) },
            errorMessage = null,
        )

        state.disbursementDateMillis?.let { millis ->
            Text(
                text = stringResource(
                    Res.string.feature_loan_close_disbursed_date_value,
                    DateHelper.getDateAsStringFromLong(millis),
                ),
                style = KptTheme.typography.bodySmall,
                color = KptTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
            )
        }

        MifosOutlinedTextField(
            value = state.note,
            onValueChange = { onAction(CloseLoanAction.OnNoteChange(it)) },
            label = stringResource(Res.string.feature_loan_close_note),
            error = null,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            MifosButton(
                modifier = Modifier.weight(1f),
                enabled = !isSubmitting,
                onClick = { onAction(CloseLoanAction.NavigateBack) },
            ) {
                Text(text = stringResource(Res.string.feature_loan_close_cancel))
            }

            MifosButton(
                modifier = Modifier.weight(1f),
                enabled = state.isSubmitEnabled,
                onClick = { onAction(CloseLoanAction.OnSubmit) },
            ) {
                Text(text = stringResource(Res.string.feature_loan_close_submit))
            }
        }
    }
}

/**
 * Manages the display of overlay dialogs like the loading spinner and error dialog.
 *
 * @param state The current UI state.
 * @param onDismissError Callback to dismiss the error dialog.
 */
@Composable
private fun CloseLoanDialogs(
    state: CloseLoanState,
    onDismissError: () -> Unit,
) {
    when (val dialog = state.dialogState) {
        CloseLoanState.DialogState.Submitting -> MifosLoadingDialog(LoadingDialogState.Shown)
        is CloseLoanState.DialogState.Error -> AlertDialog(
            onDismissRequest = onDismissError,
            confirmButton = {
                TextButton(onClick = onDismissError) {
                    Text(stringResource(Res.string.feature_loan_close_ok))
                }
            },
            text = { Text(stringResource(dialog.message)) },
        )
        null -> Unit
    }
}
