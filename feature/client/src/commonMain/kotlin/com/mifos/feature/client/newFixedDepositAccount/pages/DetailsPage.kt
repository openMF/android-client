/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.newFixedDepositAccount.pages

import androidclient.feature.client.generated.resources.Field_officer
import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.btn_back

import androidclient.feature.client.generated.resources.feature_client_next
import androidclient.feature.client.generated.resources.step_charges
import androidclient.feature.client.generated.resources.step_details
import androidclient.feature.client.generated.resources.step_interest
import androidclient.feature.client.generated.resources.one_year_fixed_deposit
import androidclient.feature.client.generated.resources.submission_on


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
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountAction
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountState
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DetailsPage(
    state : NewFixedDepositAccountState,
    onAction: (NewFixedDepositAccountAction) -> Unit,
    modifier: Modifier = Modifier,

    ) {
    val submissionDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds().minus(86_400_000L)
            }
        }

    )
    if (state.showSubmissionDatePick) {
        DatePickerDialog(
            onDismissRequest = {
                onAction(NewFixedDepositAccountAction.OnSubmissionDatePick(state = false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(NewFixedDepositAccountAction.OnSubmissionDatePick(state = false))
                        submissionDatePickerState.selectedDateMillis?.let {
                            onAction(
                                NewFixedDepositAccountAction.OnSubmissionDateChange(
                                    DateHelper.getDateAsStringFromLong(it),
                                ),
                            )
                        }
                    },
                ) { Text("Hello World") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(NewFixedDepositAccountAction.OnSubmissionDatePick(state = false))
                    },
                ) { Text("jljks") }
            },
        ) {
            DatePicker(state = submissionDatePickerState)
        }

    }
    Column (modifier = modifier.fillMaxSize()){
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {

            MifosTextFieldDropdown(
                value = if (state.fixedDepositProductSelected == -1) {
                    ""
                } else {
                    state.fixedDepositProductOptions[state.fixedDepositProductSelected].name
                },
                label = stringResource(Res.string.one_year_fixed_deposit),
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewFixedDepositAccountAction.OnProductNameChange(index))
                },
                options = state.fixedDepositProductOptions.map {
                    it.name
                },



            )
            MifosDatePickerTextField(
                value = state.submissionDate,
                label = stringResource(Res.string.submission_on),
                openDatePicker = {
                    onAction(NewFixedDepositAccountAction.OnSubmissionDatePick(true))
                },
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosTextFieldDropdown(
                value = if (state.fieldOfficerIndex == -1) {
                    ""
                } else {
                    state.fieldOfficerOptions[state.fieldOfficerIndex].displayName
                },
                label = stringResource(Res.string.Field_officer),
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewFixedDepositAccountAction.OnFieldOfficerChange(index))
                },
                options = state.fieldOfficerOptions.map {
                    it.displayName
                }

            )
            MifosOutlinedTextField(
                value = state.externalId,
                onValueChange = {
                    onAction(NewFixedDepositAccountAction.OnExternalIdChange(it))
                },
                label = stringResource(Res.string.step_details),
                config = MifosTextFieldConfig(
                    isError = state.externalIdError != null,
                    errorText = if (state.externalIdError != null) stringResource(state.externalIdError) else null,
                ),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.btn_back),
            secondBtnText = stringResource(Res.string.feature_client_next),
            onFirstBtnClick = { onAction(NewFixedDepositAccountAction.NavigateBack) },
            onSecondBtnClick = { onAction(NewFixedDepositAccountAction.OnDetailsSubmit) },
            isSecondButtonEnabled = state.isDetailsNextEnabled,
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )

    }

}
