/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.newFixedDepositAccount.pages

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.feature_client_next
import androidclient.feature.client.generated.resources.feature_fixed_days_in_year
import androidclient.feature.client.generated.resources.feature_fixed_deposit_deposit_amount
import androidclient.feature.client.generated.resources.feature_fixed_deposit_deposit_period
import androidclient.feature.client.generated.resources.feature_fixed_deposit_deposit_period_type
import androidclient.feature.client.generated.resources.feature_fixed_deposit_terms_page
import androidclient.feature.client.generated.resources.feature_fixed_interest_calculated_using
import androidclient.feature.client.generated.resources.feature_fixed_interest_compounding
import androidclient.feature.client.generated.resources.feature_fixed_interest_compounding_period
import androidclient.feature.client.generated.resources.feature_fixed_interest_posting_period
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountAction
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountState
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme

@Composable
fun TermsPage(
    state: NewFixedDepositAccountState,
    modifier: Modifier = Modifier,
    onAction: (NewFixedDepositAccountAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(bottom = KptTheme.spacing.md)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.feature_fixed_deposit_terms_page),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
            MifosOutlinedTextField(
                value = state.fixedDepositAccountTerms.depositAmount,
                onValueChange = {
                    onAction(
                        NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetFixedDepositAmount(
                            it,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_fixed_deposit_deposit_amount) + "*",
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    prefix = {
                        Text(state.template.currency?.displaySymbol.orEmpty())
                    },
                    isError = state.fixedDepositAccountTerms.depositAmountError != null,
                    errorText = state.fixedDepositAccountTerms.depositAmountError?.let {
                        stringResource(
                            it,
                        )
                    },
                ),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
            MifosOutlinedTextField(
                value = state.fixedDepositAccountTerms.depositPeriod,
                onValueChange = {
                    onAction(
                        NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetFixedDepositPeriod(
                            it,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_fixed_deposit_deposit_period) + "*",
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    isError = state.fixedDepositAccountTerms.depositPeriodError != null,
                    errorText = state.fixedDepositAccountTerms.depositPeriodError?.let { stringResource(it) },
                ),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
            MifosTextFieldDropdown(
                value = if (state.fixedDepositAccountTerms.depositPeriodTypeIndex != -1) {
                    state.template.periodFrequencyTypeOptions?.getOrNull(state.fixedDepositAccountTerms.depositPeriodTypeIndex)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.periodFrequencyTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { id, name ->
                    onAction(
                        NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetFixedDepositPeriodType(
                            id,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_fixed_deposit_deposit_period_type) + "*",
                errorMessage = state.fixedDepositAccountTerms.depositPeriodTypeError?.let { stringResource(it) },
            )
            Spacer(Modifier.height(KptTheme.spacing.sm))
            Text(
                text = stringResource(Res.string.feature_fixed_interest_compounding),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(modifier = Modifier.height(KptTheme.spacing.md))
            MifosTextFieldDropdown(
                value = if (state.fixedDepositAccountTerms.interestCompoundingPeriodTypeIndex != -1) {
                    state.template.interestCompoundingPeriodTypeOptions?.getOrNull(state.fixedDepositAccountTerms.interestCompoundingPeriodTypeIndex)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.interestCompoundingPeriodTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { id, name ->
                    onAction(
                        NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetInterestCompoundingPeriod(
                            id,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_fixed_interest_compounding_period),
            )
            MifosTextFieldDropdown(
                value = if (state.fixedDepositAccountTerms.interestPostingPeriodTypeIndex != -1) {
                    state.template.interestPostingPeriodTypeOptions?.getOrNull(state.fixedDepositAccountTerms.interestPostingPeriodTypeIndex)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.interestPostingPeriodTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { id, name ->
                    onAction(
                        NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetInterestPostingPeriod(
                            id,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_fixed_interest_posting_period),
            )
            MifosTextFieldDropdown(
                value = if (state.fixedDepositAccountTerms.interestCalculationTypeIndex != -1) {
                    state.template.interestCalculationTypeOptions?.getOrNull(state.fixedDepositAccountTerms.interestCalculationTypeIndex)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.interestCalculationTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { id, name ->
                    onAction(
                        NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetInterestCalculationType(
                            id,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_fixed_interest_calculated_using),
                modifier = Modifier.fillMaxWidth(),
            )
            MifosTextFieldDropdown(
                value = if (state.fixedDepositAccountTerms.interestCalculationDaysInYearTypeIndex != -1) {
                    state.template.interestCalculationDaysInYearTypeOptions?.getOrNull(state.fixedDepositAccountTerms.interestCalculationDaysInYearTypeIndex)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.interestCalculationDaysInYearTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { id, name ->
                    onAction(
                        NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetInterestCalculationDaysInYearType(
                            id,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_fixed_days_in_year),
            )
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.btn_back),
            secondBtnText = stringResource(Res.string.feature_client_next),
            onFirstBtnClick = { onAction(NewFixedDepositAccountAction.PreviousStep) },
            onSecondBtnClick = { onAction(NewFixedDepositAccountAction.OnTermNext) },
        )
    }
}
