/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.assignLoanOfficer

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.cancel
import androidclient.feature.loan.generated.resources.feature_loan_assign_officer_assignment_date
import androidclient.feature.loan.generated.resources.feature_loan_assign_officer_select_officer
import androidclient.feature.loan.generated.resources.feature_loan_assign_officer_title
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_submit
import androidclient.feature.loan.generated.resources.ok
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock

@Composable
internal fun AssignLoanOfficerScreen(
    navigateBack: () -> Unit,
    onAssignLoanOfficerSuccess: (loanId: Int) -> Unit,
    viewModel: AssignLoanOfficerViewModel = koinViewModel(),
) {
    val uiState by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            AssignLoanOfficerEvent.NavigateBack -> navigateBack()
            AssignLoanOfficerEvent.AssignLoanOfficerSuccess -> onAssignLoanOfficerSuccess(uiState.loanId)
        }
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_loan_assign_officer_title),
        onBackPressed = { viewModel.trySendAction(AssignLoanOfficerAction.NavigateBack) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (uiState.viewState) {
                AssignLoanOfficerState.ViewState.Loading -> MifosProgressIndicator()

                is AssignLoanOfficerState.ViewState.Error -> {
                    MifosSweetError(
                        message = stringResource((uiState.viewState as AssignLoanOfficerState.ViewState.Error).message),
                        isRetryEnabled = true,
                        onclick = { viewModel.trySendAction(AssignLoanOfficerAction.Retry) },
                    )
                }

                is AssignLoanOfficerState.ViewState.Success -> {
                    AssignLoanOfficerForm(
                        state = uiState,
                        onAction = remember(viewModel) { viewModel::trySendAction },
                    )
                }
            }
        }
    }

    AssignLoanOfficerDialog(
        dialogMessage = uiState.dialogMessage,
        onDismissDialog = { viewModel.trySendAction(AssignLoanOfficerAction.DismissDialog) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignLoanOfficerForm(
    state: AssignLoanOfficerState,
    onAction: (AssignLoanOfficerAction) -> Unit,
) {
    val scrollState = rememberScrollState()
    val dueDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.assignmentDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val currentMillis = Clock.System.now().toEpochMilliseconds()
                val startOfTodayUtc = currentMillis - (currentMillis % 86_400_000L)
                return utcTimeMillis <= startOfTodayUtc
            }
        },
    )

    LaunchedEffect(state.assignmentDateMillis) {
        dueDatePickerState.selectedDateMillis = state.assignmentDateMillis
    }

    if (state.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onAction(AssignLoanOfficerAction.HideDatePicker) },
            confirmButton = {
                TextButton(
                    onClick = {
                        dueDatePickerState.selectedDateMillis?.let {
                            onAction(AssignLoanOfficerAction.UpdateAssignmentDate(it))
                        }
                        onAction(AssignLoanOfficerAction.HideDatePicker)
                    },
                ) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { onAction(AssignLoanOfficerAction.HideDatePicker) },
                ) { Text(stringResource(Res.string.cancel)) }
            },
        ) {
            DatePicker(state = dueDatePickerState)
        }
    }

    val officers = (state.viewState as? AssignLoanOfficerState.ViewState.Success)?.officers ?: emptyList()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(KptTheme.spacing.md),
        ) {
            MifosTextFieldDropdown(
                value = state.loanOfficerOptions.getOrNull(state.selectedOfficerIndex)?.displayName.orEmpty(),
                onValueChanged = {},
                label = stringResource(Res.string.feature_loan_assign_officer_select_officer) + "*",
                readOnly = true,
                onOptionSelected = { index, _ ->
                    onAction(AssignLoanOfficerAction.SelectOfficer(index))
                },
                options = officers.map { it.displayName ?: "${it.firstname} ${it.lastname}" },
                errorMessage = state.officerError?.let { stringResource(it) },
            )

            Spacer(Modifier.height(DesignToken.padding.medium))

            MifosDatePickerTextField(
                value = state.assignmentDateText,
                label = stringResource(Res.string.feature_loan_assign_officer_assignment_date) + "*",
                openDatePicker = { onAction(AssignLoanOfficerAction.ShowDatePicker) },
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTwoButtonRow(
                firstBtnText = stringResource(Res.string.feature_loan_cancel),
                secondBtnText = stringResource(Res.string.feature_loan_submit),
                onFirstBtnClick = { if (!state.submitInProgress) onAction(AssignLoanOfficerAction.NavigateBack) },
                onSecondBtnClick = { onAction(AssignLoanOfficerAction.Submit) },
                isButtonIconVisible = false,
                isSecondButtonEnabled = state.canSubmit && !state.submitInProgress,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (state.submitInProgress) {
            MifosProgressIndicatorOverlay()
        }
    }
}

@Composable
private fun AssignLoanOfficerDialog(
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
