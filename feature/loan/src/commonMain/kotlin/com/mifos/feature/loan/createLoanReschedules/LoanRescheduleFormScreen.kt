/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.createLoanReschedules

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_adjust_interest
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_cancel
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_change_repayment_date
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_comments
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_extend_repayment
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_failure_title
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_from_date
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_installment_rescheduled_to
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_interest_grace
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_introduce_grace_periods
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_new_interest_rate
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_new_repayments
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_ok
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_principal_grace
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_reason
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_submit
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_submitted_on
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_waive_penalties
import androidclient.feature.loan.generated.resources.feature_loan_reschedules_new_title
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextButton
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun LoanRescheduleFormScreenRoute(
    navController: NavController,
    viewModel: LoanRescheduleFormViewModel = koinViewModel(),
    navigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanRescheduleFormEvent.NavigateBack -> navigateBack()
        }
    }

    RescheduleFormScreen(
        navController = navController,
        state = state,
        onAction = viewModel::trySendAction,
    )
}

@Composable
internal fun RescheduleFormScreen(
    navController: NavController,
    state: LoanRescheduleFormUiState,
    onAction: (LoanRescheduleFormAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MifosBreadcrumbNavBar(navController = navController)
        Box(modifier = Modifier.weight(1f)) {
            RescheduleFormContent(state = state, onAction = onAction)
        }
    }

    RescheduleFormDialogContent(
        dialogState = state.dialogState,
        onAction = onAction,
    )
}

@Composable
internal fun RescheduleFormContent(
    state: LoanRescheduleFormUiState,
    onAction: (LoanRescheduleFormAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = KptTheme.spacing.md)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.feature_loan_reschedules_new_title),
            style = MifosTypography.labelLarge,
            modifier = Modifier.padding(bottom = KptTheme.spacing.sm),
        )

        RescheduleDatePickerField(
            value = state.rescheduleFromDate,
            label = stringResource(Res.string.feature_loan_reschedule_from_date),
            showDialog = state.activeDatePicker == ActiveDatePicker.RESCHEDULE_FROM,
            onOpenDialog = { onAction(LoanRescheduleFormAction.ShowDatePicker(ActiveDatePicker.RESCHEDULE_FROM)) },
            onCloseDialog = { onAction(LoanRescheduleFormAction.HideDatePicker) },
            onDateSelected = { onAction(LoanRescheduleFormAction.OnRescheduleFromDateChange(it)) },
        )

        MifosTextFieldDropdown(
            value = state.selectedReasonName,
            onValueChanged = { },
            label = stringResource(Res.string.feature_loan_reschedule_reason),
            options = state.reasons.map { it.name },
            modifier = Modifier
                .fillMaxWidth(),
            onOptionSelected = { index, text ->
                state.reasons.getOrNull(index)?.id?.let {
                    onAction(LoanRescheduleFormAction.OnRescheduleReasonChange(it, text))
                }
            },
            errorMessage = state.reasonIdError?.let { stringResource(it) },
        )

        RescheduleDatePickerField(
            value = state.submittedOnDate,
            label = stringResource(Res.string.feature_loan_reschedule_submitted_on),
            showDialog = state.activeDatePicker == ActiveDatePicker.SUBMITTED_ON,
            onOpenDialog = { onAction(LoanRescheduleFormAction.ShowDatePicker(ActiveDatePicker.SUBMITTED_ON)) },
            onCloseDialog = { onAction(LoanRescheduleFormAction.HideDatePicker) },
            onDateSelected = { onAction(LoanRescheduleFormAction.OnSubmittedOnDateChange(it)) },
        )

        MifosOutlinedTextField(
            value = state.comments,
            onValueChange = { onAction(LoanRescheduleFormAction.OnCommentsChange(it)) },
            label = stringResource(Res.string.feature_loan_reschedule_comments),
        )

        RescheduleOptionItem(
            label = stringResource(Res.string.feature_loan_reschedule_change_repayment_date),
            selected = state.changeRepaymentDateSelected,
            onToggle = { onAction(LoanRescheduleFormAction.ToggleChangeRepaymentDate(it)) },
        ) {
            RescheduleDatePickerField(
                value = state.adjustedDueDate,
                label = stringResource(Res.string.feature_loan_reschedule_installment_rescheduled_to),
                showDialog = state.activeDatePicker == ActiveDatePicker.ADJUSTED_DUE_DATE,
                onOpenDialog = { onAction(LoanRescheduleFormAction.ShowDatePicker(ActiveDatePicker.ADJUSTED_DUE_DATE)) },
                onCloseDialog = { onAction(LoanRescheduleFormAction.HideDatePicker) },
                onDateSelected = { onAction(LoanRescheduleFormAction.OnAdjustedDueDateChange(it)) },
            )
        }

        RescheduleOptionItem(
            label = stringResource(Res.string.feature_loan_reschedule_introduce_grace_periods),
            selected = state.introduceGracePeriodsSelected,
            onToggle = { onAction(LoanRescheduleFormAction.ToggleIntroduceGracePeriods(it)) },
        ) {
            Column(
                modifier = Modifier.padding(top = KptTheme.spacing.xs).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
            ) {
                MifosOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.graceOnPrincipal,
                    onValueChange = { onAction(LoanRescheduleFormAction.OnGraceOnPrincipalChange(it)) },
                    label = stringResource(Res.string.feature_loan_reschedule_principal_grace),
                    keyboardType = KeyboardType.Number,
                )
                MifosOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.graceOnInterest,
                    onValueChange = { onAction(LoanRescheduleFormAction.OnGraceOnInterestChange(it)) },
                    label = stringResource(Res.string.feature_loan_reschedule_interest_grace),
                    keyboardType = KeyboardType.Number,
                )
            }
        }

        RescheduleOptionItem(
            label = stringResource(Res.string.feature_loan_reschedule_extend_repayment),
            selected = state.extendRepaymentPeriodSelected,
            onToggle = { onAction(LoanRescheduleFormAction.ToggleExtendRepaymentPeriod(it)) },
        ) {
            MifosOutlinedTextField(
                value = state.extraTerms,
                onValueChange = { onAction(LoanRescheduleFormAction.OnExtraTermsChange(it)) },
                label = stringResource(Res.string.feature_loan_reschedule_new_repayments),
                keyboardType = KeyboardType.Number,
            )
        }

        RescheduleOptionItem(
            label = stringResource(Res.string.feature_loan_reschedule_adjust_interest),
            selected = state.adjustInterestRateSelected,
            onToggle = { onAction(LoanRescheduleFormAction.ToggleAdjustInterestRate(it)) },
        ) {
            MifosOutlinedTextField(
                value = state.newInterestRate,
                onValueChange = { onAction(LoanRescheduleFormAction.OnNewInterestRateChange(it)) },
                label = stringResource(Res.string.feature_loan_reschedule_new_interest_rate),
                keyboardType = KeyboardType.Decimal,
            )
        }

        RescheduleOptionItem(
            label = stringResource(Res.string.feature_loan_reschedule_waive_penalties),
            selected = state.waivePenaltiesSelected,
            onToggle = { onAction(LoanRescheduleFormAction.ToggleWaivePenalties(it)) },
        )

        MifosTextButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = KptTheme.spacing.md),
            text = { Text(stringResource(Res.string.feature_loan_reschedule_submit)) },
            enabled = state.isSubmitEnabled,
            onClick = { onAction(LoanRescheduleFormAction.OnSubmitClicked) },
        )
    }
}

@Composable
private fun RescheduleFormDialogContent(
    dialogState: LoanRescheduleFormUiState.DialogState?,
    onAction: (LoanRescheduleFormAction) -> Unit,
) {
    when (dialogState) {
        LoanRescheduleFormUiState.DialogState.Loading -> MifosProgressIndicatorOverlay()

        is LoanRescheduleFormUiState.DialogState.Error -> {
            MifosStatusDialog(
                status = ResultStatus.FAILURE,
                btnText = dialogState.confirmBtnRes?.let { stringResource(it) } ?: stringResource(Res.string.feature_loan_reschedule_ok),
                onConfirm = { onAction(LoanRescheduleFormAction.CloseDialog) },
                onDismissRequest = { onAction(LoanRescheduleFormAction.CloseDialog) },
                successTitle = "",
                successMessage = "",
                failureTitle = stringResource(Res.string.feature_loan_reschedule_failure_title),
                failureMessage = dialogState.errorMessage,
                showAsDialog = true,
            )
        }

        null -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RescheduleDatePickerField(
    value: String,
    label: String,
    showDialog: Boolean,
    onOpenDialog: () -> Unit,
    onCloseDialog: () -> Unit,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val datePickerState = rememberDatePickerState()

    MifosDatePickerTextField(
        value = value,
        label = label,
        modifier = modifier.fillMaxWidth(),
        openDatePicker = onOpenDialog,
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = onCloseDialog,
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(ApiDateFormatter.formatForApi(millis))
                        }
                        onCloseDialog()
                    },
                ) { Text(stringResource(Res.string.feature_loan_reschedule_ok)) }
            },
            dismissButton = {
                TextButton(onClick = onCloseDialog) { Text(stringResource(Res.string.feature_loan_reschedule_cancel)) }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun RescheduleOptionItem(
    label: String,
    selected: Boolean,
    onToggle: (Boolean) -> Unit,
    content: @Composable (() -> Unit)? = null,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(KptTheme.shapes.small)
                .toggleable(
                    value = selected,
                    role = Role.Checkbox,
                    onValueChange = onToggle,
                )
                .padding(vertical = KptTheme.spacing.xs),
        ) {
            Checkbox(checked = selected, onCheckedChange = null)
            Text(
                text = label,
                style = KptTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = KptTheme.spacing.sm),
            )
        }
        AnimatedVisibility(visible = selected) {
            if (content != null) {
                Column(modifier = Modifier) {
                    content()
                }
            }
        }
    }
}
