/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages

import androidclient.feature.recurringdeposit.generated.resources.Res
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_back
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_cancel
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_external_id
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_field_officer
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_next
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_product_name
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_select
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_submitted_on
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosProgressIndicatorMini
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountAction
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountState
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailsPage(
    state: RecurringAccountState,
    onAction: (RecurringAccountAction) -> Unit,
) {
    val submissionDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds().minus(86_400_000L)
            }
        },
    )

    if (state.recurringDepositAccountDetail.showSubmissionDatePick) {
        DatePickerDialog(
            onDismissRequest = {
                onAction(RecurringAccountAction.RecurringAccountDetailsAction.OnSubmissionDatePick(state = false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(RecurringAccountAction.RecurringAccountDetailsAction.OnSubmissionDatePick(state = false))
                        submissionDatePickerState.selectedDateMillis?.let {
                            onAction(
                                RecurringAccountAction.RecurringAccountDetailsAction.OnSubmissionDateChange(
                                    DateHelper.getDateAsStringFromLong(it),
                                ),
                            )
                        }
                    },
                ) { Text(stringResource(Res.string.feature_recurring_deposit_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(RecurringAccountAction.RecurringAccountDetailsAction.OnSubmissionDatePick(state = false))
                    },
                ) { Text(stringResource(Res.string.feature_recurring_deposit_cancel)) }
            },
        ) {
            DatePicker(state = submissionDatePickerState)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        MifosTextFieldDropdown(
            value = if (state.recurringDepositAccountDetail.loanProductSelected == -1) {
                ""
            } else {
                state.template.productOptions?.get(state.recurringDepositAccountDetail.loanProductSelected)?.name ?: ""
            },
            onValueChanged = {},
            onOptionSelected = { index, value ->
                onAction(RecurringAccountAction.RecurringAccountDetailsAction.OnProductNameChange(index))
            },
            options = state.template.productOptions?.map {
                it.name ?: ""
            } ?: emptyList(),
            label = stringResource(Res.string.feature_recurring_deposit_product_name),
        )

        if (!state.template.fieldOfficerOptions.isNullOrEmpty()) {
            MifosDatePickerTextField(
                value = state.recurringDepositAccountDetail.submissionDate,
                label = stringResource(Res.string.feature_recurring_deposit_submitted_on),
                openDatePicker = {
                    onAction(RecurringAccountAction.RecurringAccountDetailsAction.OnSubmissionDatePick(true))
                },
            )

            Spacer(Modifier.height(DesignToken.padding.large))
            MifosTextFieldDropdown(
                value = if (state.recurringDepositAccountDetail.fieldOfficerIndex == -1) {
                    ""
                } else {
                    state.template.fieldOfficerOptions
                        ?.get(state.recurringDepositAccountDetail.fieldOfficerIndex)?.displayName ?: ""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(RecurringAccountAction.RecurringAccountDetailsAction.OnFieldOfficerChange(index))
                },
                options = state.template.fieldOfficerOptions?.map {
                    it.displayName ?: ""
                } ?: emptyList(),
                label = stringResource(Res.string.feature_recurring_deposit_field_officer),
            )

            MifosOutlinedTextField(
                value = state.recurringDepositAccountDetail.externalId,
                onValueChange = {
                    onAction(RecurringAccountAction.RecurringAccountDetailsAction.OnExternalIdChange(it))
                },
                label = stringResource(Res.string.feature_recurring_deposit_external_id),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                    ),
                ),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
        }

        if (state.recurringDepositAccountDetail.isMiniLoaderActive) {
            MifosProgressIndicatorMini()
        }

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_recurring_deposit_back),
            secondBtnText = stringResource(Res.string.feature_recurring_deposit_next),
            onFirstBtnClick = { onAction(RecurringAccountAction.NavigateBack) },
            onSecondBtnClick = { onAction(RecurringAccountAction.OnNextPress) },
            isSecondButtonEnabled = state.recurringDepositAccountDetail.isDetailButtonEnabled,
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )
    }
}
