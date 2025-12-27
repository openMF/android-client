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
import androidclient.feature.savings.generated.resources.feature_savings_currency
import androidclient.feature.savings.generated.resources.feature_savings_days_in_year
import androidclient.feature.savings.generated.resources.feature_savings_interest_calc
import androidclient.feature.savings.generated.resources.feature_savings_interest_comp
import androidclient.feature.savings.generated.resources.feature_savings_interest_p_period
import androidclient.feature.savings.generated.resources.feature_savings_next
import androidclient.feature.savings.generated.resources.step_terms
import androidclient.feature.savings.generated.resources.step_terms_apply_withdrawal_fee
import androidclient.feature.savings.generated.resources.step_terms_decimal_places
import androidclient.feature.savings.generated.resources.step_terms_enforce_min_balance
import androidclient.feature.savings.generated.resources.step_terms_frequency
import androidclient.feature.savings.generated.resources.step_terms_is_allowed_overdraft
import androidclient.feature.savings.generated.resources.step_terms_lock_in_period
import androidclient.feature.savings.generated.resources.step_terms_min_opening_balance
import androidclient.feature.savings.generated.resources.step_terms_minimum_balance
import androidclient.feature.savings.generated.resources.step_terms_monthly_min_balance
import androidclient.feature.savings.generated.resources.step_terms_overdraft
import androidclient.feature.savings.generated.resources.step_terms_type
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.savings.savingsAccountv2.SavingsAccountAction
import com.mifos.feature.savings.savingsAccountv2.SavingsAccountState
import org.jetbrains.compose.resources.stringResource

@Composable
fun TermsPage(
    state: SavingsAccountState,
    modifier: Modifier = Modifier,
    onAction: (SavingsAccountAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(bottom = DesignToken.padding.large)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                stringResource(Res.string.step_terms),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosTextFieldDropdown(
                value = if (state.currencyIndex == -1) {
                    ""
                } else {
                    state.savingsProductTemplate?.currencyOptions?.get(state.currencyIndex)?.name ?: ""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(SavingsAccountAction.OnCurrencyChange(index))
                },
                options = state.savingsProductTemplate?.currencyOptions?.map { currency ->
                    currency.name ?: ""
                } ?: emptyList(),
                label = stringResource(Res.string.feature_savings_currency),
                errorMessage = state.currencyError,
            )
            MifosOutlinedTextField(
                value = state.decimalPlaces,
                onValueChange = {
                    onAction(SavingsAccountAction.OnDecimalPlacesChange(it))
                },
                label = stringResource(Res.string.step_terms_decimal_places),
                config = MifosTextFieldConfig(
                    isError = state.decimalPlacesError != null,
                    errorText = state.decimalPlacesError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                ),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosTextFieldDropdown(
                value = if (state.interestCompPeriodIndex == -1) {
                    ""
                } else {
                    state.savingsProductTemplate?.interestCompoundingPeriodTypeOptions?.get(state.interestCompPeriodIndex)?.value
                        ?: ""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(SavingsAccountAction.OnInterestCompPeriodChange(index))
                },
                options = state.savingsProductTemplate?.interestCompoundingPeriodTypeOptions?.map { interestType ->
                    interestType.value ?: ""
                } ?: emptyList(),
                label = stringResource(Res.string.feature_savings_interest_comp),
            )
            MifosTextFieldDropdown(
                value = if (state.interestPostingPeriodIndex == -1) {
                    ""
                } else {
                    state.savingsProductTemplate?.interestPostingPeriodTypeOptions?.get(state.interestPostingPeriodIndex)?.value
                        ?: ""
                },
                onValueChanged = { },
                onOptionSelected = { index, value ->
                    onAction(SavingsAccountAction.OnInterestPostingPeriodChange(index))
                },
                options = state.savingsProductTemplate?.interestPostingPeriodTypeOptions?.map { postingPeriod ->
                    postingPeriod.value ?: ""
                } ?: emptyList(),
                label = stringResource(Res.string.feature_savings_interest_p_period),
            )
            MifosTextFieldDropdown(
                value = if (state.interestCalcIndex == -1) {
                    ""
                } else {
                    state.savingsProductTemplate?.interestCalculationTypeOptions?.get(state.interestCalcIndex)?.value
                        ?: ""
                },
                onValueChanged = { },
                onOptionSelected = { index, value ->
                    onAction(SavingsAccountAction.OnInterestCalcChange(index))
                },
                options = state.savingsProductTemplate?.interestCalculationTypeOptions?.map { calcType ->
                    calcType.value ?: ""
                } ?: emptyList(),
                label = stringResource(Res.string.feature_savings_interest_calc),
            )
            MifosTextFieldDropdown(
                value = if (state.daysInYearIndex == -1) {
                    ""
                } else {
                    state.savingsProductTemplate?.interestCalculationDaysInYearTypeOptions?.get(state.daysInYearIndex)?.value
                        ?: ""
                },
                onValueChanged = { },
                onOptionSelected = { index, value ->
                    onAction(SavingsAccountAction.OnDaysInYearChange(index))
                },
                options = state.savingsProductTemplate?.interestCalculationDaysInYearTypeOptions?.map { daysInYearType ->
                    daysInYearType.value ?: ""
                } ?: emptyList(),
                label = stringResource(Res.string.feature_savings_days_in_year),
            )
            MifosOutlinedTextField(
                value = state.minimumOpeningBalance,
                onValueChange = { onAction(SavingsAccountAction.OnMinimumOpeningBalanceChange(it)) },
                label = stringResource(Res.string.step_terms_min_opening_balance),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                ),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Row(
                Modifier.fillMaxWidth()
                    .clickable {
                        onAction(SavingsAccountAction.OnApplyWithdrawalFeeChange(!state.isCheckedApplyWithdrawalFee))
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = state.isCheckedApplyWithdrawalFee,
                    onCheckedChange = {
                        onAction(SavingsAccountAction.OnApplyWithdrawalFeeChange(it))
                    },
                )
                Text(
                    text = stringResource(Res.string.step_terms_apply_withdrawal_fee),
                    style = MifosTypography.labelLarge,
                )
            }
            Spacer(Modifier.height(DesignToken.padding.large))
            Text(
                stringResource(Res.string.step_terms_lock_in_period),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosOutlinedTextField(
                value = state.frequency,
                onValueChange = { onAction(SavingsAccountAction.OnFrequencyChange(it)) },
                label = stringResource(Res.string.step_terms_frequency),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                ),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosTextFieldDropdown(
                value = if (state.freqTypeIndex == -1) {
                    ""
                } else {
                    state.savingsProductTemplate?.lockinPeriodFrequencyTypeOptions?.get(state.freqTypeIndex)?.value
                        ?: ""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(SavingsAccountAction.OnFreqTypeChange(index))
                },
                options = state.savingsProductTemplate?.lockinPeriodFrequencyTypeOptions?.map { freqType ->
                    freqType.value ?: ""
                } ?: emptyList(),
                label = stringResource(Res.string.step_terms_type),
                enabled = state.frequency.isNotEmpty(),
            )
            Text(
                stringResource(Res.string.step_terms_overdraft),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Row(
                Modifier.fillMaxWidth()
                    .clickable {
                        onAction(SavingsAccountAction.OnOverDraftAllowedChange(!state.isCheckedOverdraftAllowed))
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = state.isCheckedOverdraftAllowed,
                    onCheckedChange = {
                        onAction(SavingsAccountAction.OnOverDraftAllowedChange(it))
                    },
                )
                Text(
                    text = stringResource(Res.string.step_terms_is_allowed_overdraft),
                    style = MifosTypography.labelLarge,
                )
            }
            Spacer(Modifier.height(DesignToken.padding.large))
            Text(
                stringResource(Res.string.step_terms_monthly_min_balance),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Row(
                Modifier.fillMaxWidth()
                    .clickable {
                        onAction(SavingsAccountAction.OnMinimumBalanceChange(!state.isCheckedMinimumBalance))
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = state.isCheckedMinimumBalance,
                    onCheckedChange = {
                        onAction(SavingsAccountAction.OnMinimumBalanceChange(it))
                    },
                )
                Text(
                    text = stringResource(Res.string.step_terms_enforce_min_balance),
                    style = MifosTypography.labelLarge,
                )
            }
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosOutlinedTextField(
                value = state.monthlyMinimumBalance,
                onValueChange = { onAction(SavingsAccountAction.OnMonthlyMinimumBalanceChange(it)) },
                label = stringResource(Res.string.step_terms_minimum_balance),
                config = MifosTextFieldConfig(
                    enabled = state.isCheckedMinimumBalance,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                ),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_savings_back),
            secondBtnText = stringResource(Res.string.feature_savings_next),
            onFirstBtnClick = {
                onAction(SavingsAccountAction.PreviousStep)
            },
            onSecondBtnClick = {
                onAction(SavingsAccountAction.OnTermSubmit)
            },
        )
    }
}
