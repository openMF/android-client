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

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.feature_client_charge_cancel
import androidclient.feature.client.generated.resources.feature_client_charge_select
import androidclient.feature.client.generated.resources.feature_client_external_id
import androidclient.feature.client.generated.resources.feature_client_next
import androidclient.feature.client.generated.resources.field_officer
import androidclient.feature.client.generated.resources.one_year_fixed_deposit
import androidclient.feature.client.generated.resources.step_details
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
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountAction
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountState
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DetailsPage(
    state: NewFixedDepositAccountState,
    modifier: Modifier = Modifier,
    onAction: (NewFixedDepositAccountAction) -> Unit,
) {
    val submissionDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds().minus(86_400_000L)
            }
        },

    )
    if (state.fixedDepositAccountDetail.showSubmissionDatePick) {
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
                ) { Text(stringResource(Res.string.feature_client_charge_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(NewFixedDepositAccountAction.OnSubmissionDatePick(state = false))
                    },
                ) { Text(stringResource(Res.string.feature_client_charge_cancel)) }
            },
        ) {
            DatePicker(state = submissionDatePickerState)
        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(bottom = DesignToken.padding.large)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.step_details),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosTextFieldDropdown(
                value = if (state.fixedDepositAccountDetail.productSelected == -1) {
                    ""
                } else {
                    state.template.productOptions?.get(state.fixedDepositAccountDetail.productSelected)?.name
                        ?: ""
                },
                label = stringResource(Res.string.one_year_fixed_deposit) + "*",
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewFixedDepositAccountAction.OnProductNameChange(index))
                },
                options = state.template.productOptions?.map {
                    it.name ?: ""
                } ?: emptyList(),
                errorMessage = state.fixedDepositAccountDetail.productError?.let { stringResource(it) },
            )

            if (!state.template.fieldOfficerOptions.isNullOrEmpty()) {
                MifosDatePickerTextField(
                    value = state.fixedDepositAccountDetail.submissionDate,
                    label = stringResource(Res.string.submission_on) + "*",
                    openDatePicker = {
                        onAction(NewFixedDepositAccountAction.OnSubmissionDatePick(true))
                    },
                )
                Spacer(Modifier.height(DesignToken.padding.large))
                MifosTextFieldDropdown(
                    value = if (state.fixedDepositAccountDetail.fieldOfficerIndex == -1) {
                        ""
                    } else {
                        state.template.fieldOfficerOptions?.get(state.fixedDepositAccountDetail.fieldOfficerIndex)?.displayName
                            ?: ""
                    },
                    label = stringResource(Res.string.field_officer),
                    onValueChanged = {},
                    onOptionSelected = { index, value ->
                        onAction(NewFixedDepositAccountAction.OnFieldOfficerChange(index))
                    },
                    options = state.template.fieldOfficerOptions?.map {
                        it.displayName ?: ""
                    } ?: emptyList(),

                )
                MifosOutlinedTextField(
                    value = state.fixedDepositAccountDetail.externalId,
                    onValueChange = {
                        onAction(NewFixedDepositAccountAction.OnExternalIdChange(it))
                    },
                    label = stringResource(Res.string.feature_client_external_id),
                    config = MifosTextFieldConfig(
                        isError = state.fixedDepositAccountDetail.externalIdError != null,
                        errorText = if (state.fixedDepositAccountDetail.externalIdError != null) {
                            stringResource(
                                state.fixedDepositAccountDetail.externalIdError,
                            )
                        } else {
                            null
                        },
                    ),
                )
                Spacer(Modifier.height(DesignToken.padding.large))
            }
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.btn_back),
            secondBtnText = stringResource(Res.string.feature_client_next),
            onFirstBtnClick = { onAction(NewFixedDepositAccountAction.NavigateBack) },
            onSecondBtnClick = { onAction(NewFixedDepositAccountAction.OnDetailNext) },
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )
    }
}
