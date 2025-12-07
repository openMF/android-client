/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createShareAccount.pages

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_share_account_back
import androidclient.feature.client.generated.resources.feature_share_account_detail_date_cancel
import androidclient.feature.client.generated.resources.feature_share_account_detail_date_select
import androidclient.feature.client.generated.resources.feature_share_account_next
import androidclient.feature.client.generated.resources.feature_share_account_terms
import androidclient.feature.client.generated.resources.feature_share_account_terms_allow_dividends
import androidclient.feature.client.generated.resources.feature_share_account_terms_application_date
import androidclient.feature.client.generated.resources.feature_share_account_terms_currency
import androidclient.feature.client.generated.resources.feature_share_account_terms_current_price
import androidclient.feature.client.generated.resources.feature_share_account_terms_default_savings_account
import androidclient.feature.client.generated.resources.feature_share_account_terms_frequency
import androidclient.feature.client.generated.resources.feature_share_account_terms_lock_in_period
import androidclient.feature.client.generated.resources.feature_share_account_terms_min_active_period
import androidclient.feature.client.generated.resources.feature_share_account_terms_total_shares
import androidclient.feature.client.generated.resources.feature_share_account_terms_type
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosCheckBox
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.client.createShareAccount.CreateShareAccountAction
import com.mifos.feature.client.createShareAccount.CreateShareAccountState
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun TermsPage(
    state: CreateShareAccountState,
    onAction: (CreateShareAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val applicationDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    if (state.showApplicationDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                onAction(CreateShareAccountAction.OnOpenApplicationDatePicker(state = false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(CreateShareAccountAction.OnOpenApplicationDatePicker(state = false))
                        applicationDatePickerState.selectedDateMillis?.let {
                            onAction(
                                CreateShareAccountAction.OnApplicationDateChange(
                                    DateHelper.getDateAsStringFromLong(it),
                                ),
                            )
                        }
                    },
                ) { Text(stringResource(Res.string.feature_share_account_detail_date_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(CreateShareAccountAction.OnOpenApplicationDatePicker(state = false))
                    },
                ) { Text(stringResource(Res.string.feature_share_account_detail_date_cancel)) }
            },
        ) {
            DatePicker(state = applicationDatePickerState)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(bottom = DesignToken.padding.large)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.feature_share_account_terms),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTextFieldDropdown(
                value = state.currency.orEmpty(),
                onValueChanged = {},
                onOptionSelected = { _, _ -> },
                options = emptyList(),
                label = stringResource(Res.string.feature_share_account_terms_currency),
                enabled = false,
            )
            MifosOutlinedTextField(
                value = state.currentPrice.orEmpty(),
                onValueChange = {},
                label = stringResource(Res.string.feature_share_account_terms_current_price),
                config = MifosTextFieldConfig(
                    enabled = false,
                ),
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.totalShares,
                onValueChange = {
                    onAction(CreateShareAccountAction.OnTotalSharesChange(it))
                },
                label = stringResource(Res.string.feature_share_account_terms_total_shares) + "*",
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                    isError = state.totalSharesError != null,
                    errorText = state.totalSharesError?.let { stringResource(it) },
                ),
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTextFieldDropdown(
                value = if (state.savingsAccountIdx == null) {
                    ""
                } else {
                    state.savingsAccountOptions.getOrNull(state.savingsAccountIdx)?.accountNo.orEmpty()
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(CreateShareAccountAction.OnSavingsAccountChange(index))
                },
                options = state.savingsAccountOptions.map {
                    it.accountNo + (it.savingsProductName?.let { name -> " - $name" }.orEmpty())
                },
                label = stringResource(Res.string.feature_share_account_terms_default_savings_account) + "*",
                errorMessage = state.savingsAccountError?.let { stringResource(it) },
            )

            MifosDatePickerTextField(
                value = state.applicationDate,
                label = stringResource(Res.string.feature_share_account_terms_application_date),
                openDatePicker = {
                    onAction(CreateShareAccountAction.OnOpenApplicationDatePicker(true))
                },
                errorMessage = state.applicationDateError?.let { stringResource(it) },
            )

            MifosCheckBox(
                text = stringResource(Res.string.feature_share_account_terms_allow_dividends),
                checked = state.isDividendAllowed,
                onCheckChanged = {
                    onAction(CreateShareAccountAction.OnIsDividendAllowedClicked)
                },
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            Text(
                text = stringResource(Res.string.feature_share_account_terms_min_active_period),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.minActivePeriodFreq,
                onValueChange = {
                    onAction(CreateShareAccountAction.OnMinActiveFreqChange(it))
                },
                label = stringResource(Res.string.feature_share_account_terms_frequency),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                    isError = state.minActivePeriodFreqError != null,
                    errorText = state.minActivePeriodFreqError?.let { stringResource(it) },
                ),
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTextFieldDropdown(
                value = if (state.minActivePeriodFreqTypeIdx == null) {
                    ""
                } else {
                    state.minimumActivePeriodFrequencyTypeOptions.getOrNull(state.minActivePeriodFreqTypeIdx)?.value.orEmpty()
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(CreateShareAccountAction.OnMinActiveFreqTypeChange(index))
                },
                options = state.minimumActivePeriodFrequencyTypeOptions.map {
                    it.value
                },
                enabled = state.minActivePeriodFreq.isNotBlank(),
                label = stringResource(Res.string.feature_share_account_terms_type),
                errorMessage = state.minActivePeriodFreqTypeError?.let { stringResource(it) },
            )

            Text(
                text = stringResource(Res.string.feature_share_account_terms_lock_in_period),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.lockInPeriodFreq,
                onValueChange = {
                    onAction(CreateShareAccountAction.OnLockInFreqChange(it))
                },
                label = stringResource(Res.string.feature_share_account_terms_frequency),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                    isError = state.lockInPeriodFreqError != null,
                    errorText = state.lockInPeriodFreqError?.let { stringResource(it) },
                ),
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTextFieldDropdown(
                value = if (state.lockInPeriodFreqTypeIdx == null) {
                    ""
                } else {
                    state.lockInPeriodFrequencyTypeOptions.getOrNull(state.lockInPeriodFreqTypeIdx)?.value.orEmpty()
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(CreateShareAccountAction.OnLockInFreqTypeChange(index))
                },
                options = state.lockInPeriodFrequencyTypeOptions.map {
                    it.value
                },
                enabled = state.lockInPeriodFreq.isNotBlank(),
                label = stringResource(Res.string.feature_share_account_terms_type),
                errorMessage = state.lockInPeriodFreqTypeError?.let { stringResource(it) },
            )
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_share_account_back),
            secondBtnText = stringResource(Res.string.feature_share_account_next),
            onFirstBtnClick = {
                onAction(CreateShareAccountAction.PreviousStep)
            },
            onSecondBtnClick = {
                onAction(CreateShareAccountAction.OnTermsNext)
            },
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )
    }
}
