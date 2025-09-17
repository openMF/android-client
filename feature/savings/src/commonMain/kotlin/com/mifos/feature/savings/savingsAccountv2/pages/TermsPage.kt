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
import androidclient.feature.savings.generated.resources.step_terms_currency_required
import androidclient.feature.savings.generated.resources.step_terms_days_in_year_required
import androidclient.feature.savings.generated.resources.step_terms_decimal_places
import androidclient.feature.savings.generated.resources.step_terms_decimal_places_error
import androidclient.feature.savings.generated.resources.step_terms_enforce_min_balance
import androidclient.feature.savings.generated.resources.step_terms_frequency
import androidclient.feature.savings.generated.resources.step_terms_interest_calc_required
import androidclient.feature.savings.generated.resources.step_terms_interest_comp_period_required
import androidclient.feature.savings.generated.resources.step_terms_interest_posting_period_required
import androidclient.feature.savings.generated.resources.step_terms_is_allowed_overdraft
import androidclient.feature.savings.generated.resources.step_terms_lock_in_period
import androidclient.feature.savings.generated.resources.step_terms_lock_in_period_freq_error
import androidclient.feature.savings.generated.resources.step_terms_lock_in_type_required
import androidclient.feature.savings.generated.resources.step_terms_min_balance_required
import androidclient.feature.savings.generated.resources.step_terms_min_monthly_balance_error
import androidclient.feature.savings.generated.resources.step_terms_min_opening_balance
import androidclient.feature.savings.generated.resources.step_terms_min_opening_balance_error
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun TermsPage(
    state: SavingsAccountState,
    onAction: (SavingsAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(
        state.currencyIndex,
        state.frequency,
        state.monthlyMinimumBalance,
        state.minimumOpeningBalance,
        state.decimalPlaces,
        state.interestCalcIndex,
        state.interestCompPeriodIndex,
        state.interestPostingPeriodIndex,
        state.daysInYearIndex,
        state.freqTypeIndex,
        state.isCheckedMinimumBalance,
    ) {
        validateAllFields(state, onAction)
    }

    Column(modifier = Modifier.fillMaxSize()) {
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
                errorMessage = state.interestCompPeriodError,
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
                errorMessage = state.interestPostingPeriodError,
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
                errorMessage = state.interestCalcError,
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
                errorMessage = state.daysInYearError,
            )
            MifosOutlinedTextField(
                value = state.minimumOpeningBalance,
                onValueChange = { onAction(SavingsAccountAction.OnMinimumOpeningBalanceChange(it)) },
                label = stringResource(Res.string.step_terms_min_opening_balance),
                config = MifosTextFieldConfig(
                    isError = state.minimumOpeningBalanceError != null,
                    errorText = state.minimumOpeningBalanceError,
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
                    isError = state.frequencyError != null,
                    errorText = state.frequencyError,
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
                errorMessage = state.freqTypeError,
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
                    isError = state.monthlyMinimumBalanceError != null,
                    errorText = state.monthlyMinimumBalanceError,
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
                handleNext(
                    state,
                    onAction,
                    scope,
                )
            },
            isSecondButtonEnabled = state.isTermsNextEnabled,
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )
    }
}

private fun handleNext(
    state: SavingsAccountState,
    onAction: (SavingsAccountAction) -> Unit,
    scope: CoroutineScope,
) {
    scope.launch {
        val isValid = validateAllFields(state, onAction)
        if (isValid) {
            onAction(SavingsAccountAction.NextStep)
        }
    }
}

private suspend fun validateAllFields(
    state: SavingsAccountState,
    onAction: (SavingsAccountAction) -> Unit,
): Boolean {
    var isValid = true
    if (state.currencyIndex == -1) {
        onAction(SavingsAccountAction.SetCurrencyError(getString(Res.string.step_terms_currency_required)))
        isValid = false
    } else {
        onAction(SavingsAccountAction.SetCurrencyError(null))
    }

    val decimalPlaces = state.decimalPlaces.toIntOrNull()
    if (decimalPlaces == null || decimalPlaces < 0 || decimalPlaces > 6 || state.decimalPlaces.length != 1) {
        onAction(SavingsAccountAction.SetDecimalPlacesError(getString(Res.string.step_terms_decimal_places_error)))
        isValid = false
    } else {
        onAction(SavingsAccountAction.SetDecimalPlacesError(null))
    }

    if (state.interestCompPeriodIndex == -1) {
        onAction(SavingsAccountAction.SetInterestCompPeriodError(getString(Res.string.step_terms_interest_comp_period_required)))
        isValid = false
    } else {
        onAction(SavingsAccountAction.SetInterestCompPeriodError(null))
    }

    if (state.interestPostingPeriodIndex == -1) {
        onAction(SavingsAccountAction.SetInterestPostingPeriodError(getString(Res.string.step_terms_interest_posting_period_required)))
        isValid = false
    } else {
        onAction(SavingsAccountAction.SetInterestPostingPeriodError(null))
    }

    if (state.interestCalcIndex == -1) {
        onAction(SavingsAccountAction.SetInterestCalcError(getString(Res.string.step_terms_interest_calc_required)))
        isValid = false
    } else {
        onAction(SavingsAccountAction.SetInterestCalcError(null))
    }

    if (state.daysInYearIndex == -1) {
        onAction(SavingsAccountAction.SetDaysInYearError(getString(Res.string.step_terms_days_in_year_required)))
        isValid = false
    } else {
        onAction(SavingsAccountAction.SetDaysInYearError(null))
    }

    if (state.minimumOpeningBalance.isNotEmpty()) {
        val minimumOpeningBalance = state.minimumOpeningBalance.toDoubleOrNull()
        if (minimumOpeningBalance == null || minimumOpeningBalance < 0) {
            onAction(SavingsAccountAction.OnMinimumOpeningBalanceError(getString(Res.string.step_terms_min_opening_balance_error)))
            isValid = false
        } else {
            onAction(SavingsAccountAction.OnMinimumOpeningBalanceError(null))
        }
    } else {
        onAction(SavingsAccountAction.OnMinimumOpeningBalanceError(null))
    }

    if (state.frequency.isNotEmpty()) {
        val frequency = state.frequency.toIntOrNull()
        if (frequency == null || frequency < 0) {
            onAction(SavingsAccountAction.SetFrequencyError(getString(Res.string.step_terms_lock_in_period_freq_error)))
            isValid = false
        } else {
            onAction(SavingsAccountAction.SetFrequencyError(null))
        }

        if (state.freqTypeIndex == -1) {
            onAction(SavingsAccountAction.SetFreqTypeError(getString(Res.string.step_terms_lock_in_type_required)))
            isValid = false
        } else {
            onAction(SavingsAccountAction.SetFreqTypeError(null))
        }
    } else {
        onAction(SavingsAccountAction.SetFrequencyError(null))
        onAction(SavingsAccountAction.SetFreqTypeError(null))
    }

    if (state.isCheckedMinimumBalance) {
        if (state.monthlyMinimumBalance.isEmpty()) {
            onAction(SavingsAccountAction.OnMonthlyMinimumBalanceError(getString(Res.string.step_terms_min_balance_required)))
            isValid = false
        } else {
            val minimumBalance = state.monthlyMinimumBalance.toDoubleOrNull()
            if (minimumBalance == null || minimumBalance < 0) {
                onAction(SavingsAccountAction.OnMonthlyMinimumBalanceError(getString(Res.string.step_terms_min_monthly_balance_error)))
                isValid = false
            } else {
                onAction(SavingsAccountAction.OnMonthlyMinimumBalanceError(null))
            }
        }
    } else {
        onAction(SavingsAccountAction.OnMonthlyMinimumBalanceError(null))
    }
    return isValid
}
