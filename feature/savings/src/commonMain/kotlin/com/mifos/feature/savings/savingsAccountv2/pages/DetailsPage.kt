/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountv2.pages

import androidclient.feature.savings.generated.resources.Res
import androidclient.feature.savings.generated.resources.feature_savings_back
import androidclient.feature.savings.generated.resources.feature_savings_cancel
import androidclient.feature.savings.generated.resources.feature_savings_external_id
import androidclient.feature.savings.generated.resources.feature_savings_field_officer
import androidclient.feature.savings.generated.resources.feature_savings_next
import androidclient.feature.savings.generated.resources.feature_savings_product_name
import androidclient.feature.savings.generated.resources.feature_savings_select
import androidclient.feature.savings.generated.resources.feature_savings_submission_date
import androidclient.feature.savings.generated.resources.step_details
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.savings.savingsAccountv2.SavingsAccountAction
import com.mifos.feature.savings.savingsAccountv2.SavingsAccountState
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsPage(
    state: SavingsAccountState,
    onAction: (SavingsAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val submissionDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds().minus(86_400_000L)
            }
        },
    )

    if (state.showSubmissionDatePick) {
        DatePickerDialog(
            onDismissRequest = {
                onAction(SavingsAccountAction.OnSubmissionDatePick(state = false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(SavingsAccountAction.OnSubmissionDatePick(state = false))
                        submissionDatePickerState.selectedDateMillis?.let {
                            onAction(
                                SavingsAccountAction.OnSubmissionDateChange(
                                    DateHelper.getDateAsStringFromLong(it),
                                ),
                            )
                        }
                    },
                ) { Text(stringResource(Res.string.feature_savings_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(SavingsAccountAction.OnSubmissionDatePick(state = false))
                    },
                ) { Text(stringResource(Res.string.feature_savings_cancel)) }
            },
        ) {
            DatePicker(state = submissionDatePickerState)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.step_details),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTextFieldDropdown(
                value = if (state.savingsProductSelected == -1) {
                    ""
                } else {
                    state.savingProductOptions[state.savingsProductSelected].name
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(SavingsAccountAction.OnProductNameChange(index))
                },
                options = state.savingProductOptions.map {
                    it.name
                },
                label = stringResource(Res.string.feature_savings_product_name),
            )
            MifosDatePickerTextField(
                value = state.submissionDate,
                label = stringResource(Res.string.feature_savings_submission_date),
                openDatePicker = {
                    onAction(SavingsAccountAction.OnSubmissionDatePick(true))
                },
            )

            Spacer(Modifier.height(DesignToken.padding.large))
            MifosTextFieldDropdown(
                value = if (state.fieldOfficerIndex == -1) {
                    ""
                } else {
                    state.fieldOfficerOptions[state.fieldOfficerIndex].displayName
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(SavingsAccountAction.OnFieldOfficerChange(index))
                },
                options = state.fieldOfficerOptions.map {
                    it.displayName
                },
                label = stringResource(Res.string.feature_savings_field_officer),
            )

            MifosOutlinedTextField(
                value = state.externalId,
                onValueChange = {
                    onAction(SavingsAccountAction.OnExternalIdChange(it))
                },
                label = stringResource(Res.string.feature_savings_external_id),
                config = MifosTextFieldConfig(
                    isError = state.externalIdError != null,
                    errorText = if (state.externalIdError != null) stringResource(state.externalIdError) else null,
                ),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_savings_back),
            secondBtnText = stringResource(Res.string.feature_savings_next),
            onFirstBtnClick = { onAction(SavingsAccountAction.NavigateBack) },
            onSecondBtnClick = { onAction(SavingsAccountAction.OnDetailsSubmit) },
            isSecondButtonEnabled = state.isDetailsNextEnabled,
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )
    }
}
