/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.loan.chargeform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kpt.core.base.designsystem.theme.KptTheme
import kpt.core.base.ui.effects.EventsEffect
import kpt.core.common.utils.DateHelper
import kpt.core.designsystem.component.AppButton
import kpt.core.designsystem.component.AppDatePickerTextField
import kpt.core.designsystem.component.AppOutlinedTextField
import kpt.core.designsystem.component.AppScaffold
import kpt.core.designsystem.component.AppSweetError
import kpt.core.designsystem.component.AppTextFieldDropdown
import kpt.core.designsystem.theme.AppTypography
import kpt.core.designsystem.theme.DesignToken
import kpt.core.ui.components.AppProgressIndicator
import kpt.core.ui.components.AppStatusDialog
import kpt.core.ui.components.ResultStatus
import kpt.feature.loan.generated.resources.Res
import kpt.feature.loan.generated.resources.feature_loan_add_charge
import kpt.feature.loan.generated.resources.feature_loan_cancel
import kpt.feature.loan.generated.resources.feature_loan_charge_amount
import kpt.feature.loan.generated.resources.feature_loan_charge_calculation
import kpt.feature.loan.generated.resources.feature_loan_charge_created_failure_title
import kpt.feature.loan.generated.resources.feature_loan_charge_created_message
import kpt.feature.loan.generated.resources.feature_loan_charge_created_title
import kpt.feature.loan.generated.resources.feature_loan_charge_disbursement
import kpt.feature.loan.generated.resources.feature_loan_charge_due_date
import kpt.feature.loan.generated.resources.feature_loan_charge_flat
import kpt.feature.loan.generated.resources.feature_loan_charge_installment
import kpt.feature.loan.generated.resources.feature_loan_charge_name
import kpt.feature.loan.generated.resources.feature_loan_charge_none_available
import kpt.feature.loan.generated.resources.feature_loan_charge_required
import kpt.feature.loan.generated.resources.feature_loan_charge_specified_due_date
import kpt.feature.loan.generated.resources.feature_loan_charge_time
import kpt.feature.loan.generated.resources.feature_loan_charge_types_failed
import kpt.feature.loan.generated.resources.feature_loan_dialog_continue
import kpt.feature.loan.generated.resources.feature_loan_select_date
import kpt.feature.loan.generated.resources.feature_loan_submit
import kpt.feature.loan.generated.resources.feature_loan_transfer_amount_invalid
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Carried over from `6b66e8a43:feature/loan/loanChargeForm/LoanChargeFormSheet.kt` — the charge-type
 * dropdown, the read-only charge-time and calculation fields it fills in, the amount, and a due
 * date shown only for a specified-due-date charge.
 *
 * ## It is a SCREEN, where the original was a sheet inside the charges list
 *
 * The list owned a `showLoanChargeForm` boolean and composed the sheet itself, then reported the
 * outcome by firing snackbars from the sheet's `onSuccess`/`onError` callbacks. Here it is its own
 * destination like every other write in this feature, and the list refreshes when the officer
 * comes back because its stream re-reads — no callback, no boolean, no state across a sheet
 * boundary.
 *
 * ## The due date appears only when the chosen charge HAS one
 *
 * A disbursement or installment-fee charge has no due date of its own, and sending one makes
 * Fineract reject the request. The original handled this in the payload (a conditional
 * `if (chargeTime == SPECIFIED_DUE_DATE)`) but drew the field regardless — so an officer could
 * pick a date that was then silently discarded. The field follows the payload now.
 */
@Composable
internal fun LoanChargeFormScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoanChargeFormViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanChargeFormEvent.NavigateBack -> onBackPressed()
        }
    }

    LoanChargeFormContent(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        modifier = modifier,
    )
}

/** Stateless half — rendered in tests and previews without Koin. */
@Composable
internal fun LoanChargeFormContent(
    state: LoanChargeFormState,
    onAction: (LoanChargeFormAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        title = stringResource(Res.string.feature_loan_add_charge),
        onBackPressed = { onAction(LoanChargeFormAction.NavigateBack) },
        modifier = modifier.testTag(LoanChargeFormTestTags.SCREEN),
    ) { padding ->
        when {
            state.isLoadingTypes -> AppProgressIndicator(
                modifier = Modifier.fillMaxSize().padding(padding)
                    .testTag(LoanChargeFormTestTags.LOADING),
            )

            state.typesError -> AppSweetError(
                message = stringResource(Res.string.feature_loan_charge_types_failed),
                onclick = { onAction(LoanChargeFormAction.OnRetry) },
                modifier = Modifier.fillMaxSize().padding(padding)
                    .testTag(LoanChargeFormTestTags.TYPES_ERROR),
            )

            // The tenant allows no chargeable type this form can handle. Stated rather than left
            // as an empty dropdown over a live submit button.
            state.chargeTypes.isEmpty() -> Text(
                text = stringResource(Res.string.feature_loan_charge_none_available),
                style = AppTypography.bodyMedium,
                modifier = Modifier.fillMaxSize().padding(padding).padding(KptTheme.spacing.md)
                    .testTag(LoanChargeFormTestTags.NO_TYPES),
            )

            else -> ChargeForm(
                state = state,
                onAction = onAction,
                modifier = Modifier.fillMaxSize().padding(padding),
            )
        }
    }

    when (val dialog = state.dialogState) {
        null -> Unit
        LoanChargeFormState.DialogState.Submitted -> AppStatusDialog(
            status = ResultStatus.SUCCESS,
            btnText = stringResource(Res.string.feature_loan_dialog_continue),
            // Back to the list, which re-reads and shows the new charge.
            onConfirm = { onAction(LoanChargeFormAction.NavigateBack) },
            onDismissRequest = { onAction(LoanChargeFormAction.NavigateBack) },
            successTitle = stringResource(Res.string.feature_loan_charge_created_title),
            successMessage = stringResource(Res.string.feature_loan_charge_created_message),
            failureTitle = stringResource(Res.string.feature_loan_charge_created_failure_title),
            failureMessage = "",
            modifier = Modifier.fillMaxSize().testTag(LoanChargeFormTestTags.STATUS_DIALOG),
        )

        is LoanChargeFormState.DialogState.Failed -> AppStatusDialog(
            status = ResultStatus.FAILURE,
            btnText = stringResource(Res.string.feature_loan_dialog_continue),
            // Dismiss, keeping the form filled in.
            onConfirm = { onAction(LoanChargeFormAction.CloseDialog) },
            onDismissRequest = { onAction(LoanChargeFormAction.CloseDialog) },
            successTitle = stringResource(Res.string.feature_loan_charge_created_title),
            successMessage = stringResource(Res.string.feature_loan_charge_created_message),
            failureTitle = stringResource(Res.string.feature_loan_charge_created_failure_title),
            failureMessage = dialog.msg,
            modifier = Modifier.fillMaxSize().testTag(LoanChargeFormTestTags.STATUS_DIALOG),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChargeForm(
    state: LoanChargeFormState,
    onAction: (LoanChargeFormAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = KptTheme.spacing.md)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(KptTheme.spacing.md))

        AppTextFieldDropdown(
            value = state.selectedCharge?.name.orEmpty(),
            onValueChanged = {},
            // The chosen CHARGE is dispatched. The original dispatched the index and looked it up
            // in the ViewModel — workable here because the list does not re-sort, but the option
            // is what the payload needs.
            onOptionSelected = { index, _ ->
                state.chargeTypes.getOrNull(index)
                    ?.let { onAction(LoanChargeFormAction.OnChargeSelected(it)) }
            },
            options = state.chargeTypes.map { it.name },
            label = stringResource(Res.string.feature_loan_charge_name),
            readOnly = true,
            errorMessage = if (state.chargeError) {
                stringResource(Res.string.feature_loan_charge_required)
            } else {
                null
            },
            modifier = Modifier.fillMaxWidth().testTag(LoanChargeFormTestTags.CHARGE_TYPE),
        )

        // The two fields the chosen charge DETERMINES — read-only, as the original had them, so
        // the officer can see what the charge will do before adding it.
        state.selectedCharge?.let { charge ->
            Spacer(Modifier.height(KptTheme.spacing.md))
            AppOutlinedTextField(
                value = stringResource(charge.time.labelRes()),
                onValueChange = {},
                label = stringResource(Res.string.feature_loan_charge_time),
                readOnly = true,
                modifier = Modifier.fillMaxWidth().testTag(LoanChargeFormTestTags.CHARGE_TIME),
            )

            Spacer(Modifier.height(KptTheme.spacing.md))
            AppOutlinedTextField(
                value = stringResource(charge.calculation.labelRes()),
                onValueChange = {},
                label = stringResource(Res.string.feature_loan_charge_calculation),
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
                    .testTag(LoanChargeFormTestTags.CHARGE_CALCULATION),
            )
        }

        Spacer(Modifier.height(KptTheme.spacing.md))

        AppOutlinedTextField(
            value = state.amount,
            onValueChange = { onAction(LoanChargeFormAction.OnAmountChanged(it)) },
            label = stringResource(Res.string.feature_loan_charge_amount),
            keyboardType = KeyboardType.Number,
            isError = state.amountError,
            errorText = stringResource(Res.string.feature_loan_transfer_amount_invalid)
                .takeIf { state.amountError },
            modifier = Modifier.fillMaxWidth().testTag(LoanChargeFormTestTags.AMOUNT),
        )

        // Only for the one charge kind that HAS a due date — see this file's header.
        if (state.needsDueDate) {
            Spacer(Modifier.height(KptTheme.spacing.md))

            AppDatePickerTextField(
                value = DateHelper.getDateAsStringFromLong(state.dueDate),
                label = stringResource(Res.string.feature_loan_charge_due_date),
                openDatePicker = { onAction(LoanChargeFormAction.UpdateDatePicker(true)) },
                modifier = Modifier.fillMaxWidth().testTag(LoanChargeFormTestTags.DUE_DATE),
            )

            if (state.showDatePicker) {
                val pickerState = rememberDatePickerState(initialSelectedDateMillis = state.dueDate)
                DatePickerDialog(
                    onDismissRequest = { onAction(LoanChargeFormAction.UpdateDatePicker(false)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                pickerState.selectedDateMillis?.let {
                                    onAction(LoanChargeFormAction.OnDueDateChanged(it))
                                }
                                onAction(LoanChargeFormAction.UpdateDatePicker(false))
                            },
                        ) { Text(stringResource(Res.string.feature_loan_select_date)) }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { onAction(LoanChargeFormAction.UpdateDatePicker(false)) },
                        ) { Text(stringResource(Res.string.feature_loan_cancel)) }
                    },
                ) {
                    // Deliberately UNRESTRICTED, unlike the approval / repayment / disbursement
                    // pickers. A charge's due date is when it falls due, which is normally in the
                    // FUTURE — the opposite of a date recording something that already happened.
                    DatePicker(state = pickerState)
                }
            }
        }

        Spacer(Modifier.height(KptTheme.spacing.md))

        AppButton(
            onClick = { onAction(LoanChargeFormAction.OnSubmit) },
            enabled = !state.isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(DesignToken.spacing.dp44)
                .testTag(LoanChargeFormTestTags.SUBMIT),
        ) {
            Text(text = stringResource(Res.string.feature_loan_submit))
        }

        Spacer(Modifier.height(KptTheme.spacing.md))
    }
}

/** The three charge-time kinds' labels. */
internal fun ChargeTimeKind.labelRes() = when (this) {
    ChargeTimeKind.DISBURSEMENT -> Res.string.feature_loan_charge_disbursement
    ChargeTimeKind.INSTALLMENT_FEE -> Res.string.feature_loan_charge_installment
    ChargeTimeKind.SPECIFIED_DUE_DATE -> Res.string.feature_loan_charge_specified_due_date
}

internal fun ChargeCalculationKind.labelRes() = when (this) {
    ChargeCalculationKind.FLAT -> Res.string.feature_loan_charge_flat
}

/** Stable handles for UI tests and Maestro flows — see RULE-KMP-COMPOSE-UITEST-001. */
internal object LoanChargeFormTestTags {
    const val SCREEN = "loanChargeForm:screen"
    const val LOADING = "loanChargeForm:loading"
    const val TYPES_ERROR = "loanChargeForm:typesError"
    const val NO_TYPES = "loanChargeForm:noTypes"
    const val CHARGE_TYPE = "loanChargeForm:chargeType"
    const val CHARGE_TIME = "loanChargeForm:chargeTime"
    const val CHARGE_CALCULATION = "loanChargeForm:chargeCalculation"
    const val AMOUNT = "loanChargeForm:amount"
    const val DUE_DATE = "loanChargeForm:dueDate"
    const val SUBMIT = "loanChargeForm:submit"
    const val STATUS_DIALOG = "loanChargeForm:statusDialog"
}
