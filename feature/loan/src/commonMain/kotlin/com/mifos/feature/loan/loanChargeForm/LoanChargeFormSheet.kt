/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.loan.loanChargeForm

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_amount
import androidclient.feature.loan.generated.resources.feature_loan_charge_calculation
import androidclient.feature.loan.generated.resources.feature_loan_charge_cancel
import androidclient.feature.loan.generated.resources.feature_loan_charge_dialog
import androidclient.feature.loan.generated.resources.feature_loan_charge_due_date
import androidclient.feature.loan.generated.resources.feature_loan_charge_failed_to_create_loan_charge
import androidclient.feature.loan.generated.resources.feature_loan_charge_failed_to_load_charge_types
import androidclient.feature.loan.generated.resources.feature_loan_charge_name
import androidclient.feature.loan.generated.resources.feature_loan_charge_no_charge_types_available
import androidclient.feature.loan.generated.resources.feature_loan_charge_select
import androidclient.feature.loan.generated.resources.feature_loan_charge_submit
import androidclient.feature.loan.generated.resources.feature_loan_charge_time
import androidclient.feature.loan.generated.resources.feature_loan_message_field_required
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosProgressIndicator
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun LoanChargeForm(
    loanId: Int,
    isVisible: Boolean,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit,
    onError: (String) -> Unit,
    viewModel: LoanChargeSheetViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(isVisible) {
        if (isVisible) {
            viewModel.trySendAction(LoanChargeFormAction.LoadCharges(loanId))
        }
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is LoanChargeFormEvent.ChargeCreationSuccess -> {
                    viewModel.trySendAction(LoanChargeFormAction.Reset)
                    onSuccess()
                }

                is LoanChargeFormEvent.ChargeCreationFailed -> {
                    onError(getString(Res.string.feature_loan_charge_failed_to_create_loan_charge))
                }

                is LoanChargeFormEvent.FailedToLoadChargeTypes -> {
                    onError(getString(Res.string.feature_loan_charge_failed_to_load_charge_types))
                }
            }
        }
    }

    LoanChargeFormSheet(
        state = state,
        loanId = loanId,
        isVisible = isVisible,
        onAction = { action -> viewModel.trySendAction(action) },
        onDismiss = {
            viewModel.trySendAction(LoanChargeFormAction.Reset)
            onDismiss()
        },
    )
}

@OptIn(ExperimentalTime::class)
@Composable
internal fun LoanChargeFormSheet(
    state: LoanChargeFormState,
    loanId: Int,
    isVisible: Boolean,
    onAction: (LoanChargeFormAction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dueDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.dueDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val currentMillis = Clock.System.now().toEpochMilliseconds()
                val startOfTodayUtc = currentMillis - (currentMillis % 86_400_000L)

                return utcTimeMillis >= startOfTodayUtc
            }
        },
    )

    LaunchedEffect(state.dueDate) {
        dueDatePickerState.selectedDateMillis = state.dueDate
    }

    val chargeTypes =
        if (state.chargeTypesState is LoanChargeFormState.ChargeTypesState.Success) {
            state.chargeTypesState.chargeTypes
        } else {
            emptyList()
        }
    val isLoading =
        state.chargeTypesState is LoanChargeFormState.ChargeTypesState.Loading || state.isCreatingCharge

    if (state.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                onAction(LoanChargeFormAction.HideDatePicker)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dueDatePickerState.selectedDateMillis?.let {
                            onAction(LoanChargeFormAction.DueDateChanged(it))
                        }
                        onAction(LoanChargeFormAction.HideDatePicker)
                    },
                ) { Text(stringResource(Res.string.feature_loan_charge_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(LoanChargeFormAction.HideDatePicker)
                    },
                ) { Text(stringResource(Res.string.feature_loan_charge_cancel)) }
            },
        ) {
            DatePicker(state = dueDatePickerState)
        }
    }

    if (isVisible) {
        MifosBottomSheet(
            onDismiss = onDismiss,
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = DesignToken.padding.large)
                    .padding(bottom = DesignToken.padding.largeIncreasedExtra),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = DesignToken.padding.large),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.feature_loan_charge_dialog),
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    )
                    IconButton(onClick = { onDismiss() }) {
                        Icon(
                            imageVector = MifosIcons.Close,
                            contentDescription = "",
                            modifier = Modifier
                                .width(DesignToken.sizes.iconLarge)
                                .height(DesignToken.sizes.iconLarge),
                        )
                    }
                }

                if (isLoading) {
                    MifosProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = DesignToken.padding.extraLarge),
                    )
                } else {
                    MifosTextFieldDropdown(
                        value = state.name,
                        onValueChanged = {
                            /* Handled via selection */
                        },
                        label = stringResource(Res.string.feature_loan_charge_name) + "*",
                        readOnly = true,
                        onOptionSelected = { index, _ ->
                            onAction(LoanChargeFormAction.ChargeSelected(index))
                        },
                        options = chargeTypes.map { it.name },
                        errorMessage = if (chargeTypes.isEmpty()) {
                            stringResource(Res.string.feature_loan_charge_no_charge_types_available)
                        } else {
                            null
                        },
                    )

                    MifosOutlinedTextField(
                        value = state.amount,
                        onValueChange = { value ->
                            onAction(LoanChargeFormAction.AmountChanged(value))
                        },
                        label = stringResource(Res.string.feature_loan_amount) + "*",
                        error = if (state.amountError) {
                            stringResource(Res.string.feature_loan_message_field_required)
                        } else {
                            null
                        },
                        trailingIcon = {
                            if (state.amountError) {
                                Icon(
                                    imageVector = MifosIcons.Error,
                                    contentDescription = null,
                                )
                            }
                        },
                        keyboardType = KeyboardType.Decimal,
                    )

                    Spacer(modifier = Modifier.height(DesignToken.spacing.large))

                    val chargeTimeRes = state.chargeTime?.labelRes
                    MifosOutlinedTextField(
                        value = if (chargeTimeRes != null) stringResource(chargeTimeRes) else "",
                        onValueChange = {
                            /* Read only */
                        },
                        readOnly = true,
                        label = stringResource(Res.string.feature_loan_charge_time),
                        error = null,
                    )

                    Spacer(modifier = Modifier.height(DesignToken.spacing.large))

                    val calculationRes = state.chargeCalculation?.labelRes
                    MifosOutlinedTextField(
                        value = if (calculationRes != null) stringResource(calculationRes) else "",
                        onValueChange = {
                            /* Read only */
                        },
                        readOnly = true,
                        label = stringResource(Res.string.feature_loan_charge_calculation),
                        error = null,
                    )

                    if (state.chargeTime == ChargeTimeType.SPECIFIED_DUE_DATE) {
                        Spacer(modifier = Modifier.height(DesignToken.spacing.large))
                        MifosDatePickerTextField(
                            value = DateHelper.getDateAsStringFromLong(state.dueDate),
                            label = stringResource(Res.string.feature_loan_charge_due_date),
                            openDatePicker = { onAction(LoanChargeFormAction.ShowDatePicker) },
                        )
                    }

                    Spacer(modifier = Modifier.height(DesignToken.spacing.large))

                    MifosButton(
                        onClick = {
                            onAction(LoanChargeFormAction.Submit(loanId))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.selectedChargeId != null && state.amount.isNotEmpty() && !state.amountError && !state.isCreatingCharge,
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_loan_charge_submit),
                        )
                    }
                }
            }
        }
    }
}

private class LoanChargeFormStateProvider : PreviewParameterProvider<LoanChargeFormState> {
    override val values: Sequence<LoanChargeFormState>
        get() = sequenceOf(
            LoanChargeFormState(
                chargeTypesState = LoanChargeFormState.ChargeTypesState.Success(sampleChargeList),
                selectedChargeId = 1,
                name = "Sample Charge",
                amount = "150.0",
                chargeTime = ChargeTimeType.SPECIFIED_DUE_DATE,
                chargeCalculation = ChargeCalculationType.FLAT,
            ),
            LoanChargeFormState(
                chargeTypesState = LoanChargeFormState.ChargeTypesState.Loading,
            ),
        )
}

@Preview
@Composable
private fun LoanChargeDialogScreenPreview(
    @PreviewParameter(LoanChargeFormStateProvider::class) state: LoanChargeFormState,
) {
    LoanChargeFormSheet(
        state = state,
        loanId = 1,
        isVisible = true,
        onAction = {},
        onDismiss = {},
    )
}

val sampleChargeList = List(10) {
    ChargeTypes(
        id = it,
        name = "name $it",
        chargeTimeType = ChargeTimeType.SPECIFIED_DUE_DATE,
        chargeCalculationType = ChargeCalculationType.FLAT,
        amount = 10.0 * it,
    )
}
