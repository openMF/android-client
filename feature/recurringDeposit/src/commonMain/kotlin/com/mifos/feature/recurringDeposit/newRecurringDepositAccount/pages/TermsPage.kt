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
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_calculation_days_in_year
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_interest_calculation
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_interest_compounding_period
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_interest_posting_period
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_next
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_terms
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountAction
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountState
import org.jetbrains.compose.resources.stringResource

@Composable
fun TermsPage(
    state: RecurringAccountState,
    modifier: Modifier = Modifier,
    onAction: (RecurringAccountAction) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(bottom = DesignToken.padding.large)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.feature_recurring_deposit_step_terms),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosTextFieldDropdown(
                value = if (state.recurringDepositAccountInterestChart.interestCompoundingPeriodType == -1) {
                    ""
                } else {
                    state.template.interestCompoundingPeriodTypeOptions?.get(state.recurringDepositAccountInterestChart.interestCompoundingPeriodType)?.value
                        ?: ""
                },
                onValueChanged = { },
                onOptionSelected = { index, value ->
                    onAction(
                        RecurringAccountAction.RecurringAccountTermAction.OnInterestCompoundingPeriodType(
                            index,
                        ),
                    )
                },
                options = state.template.interestCompoundingPeriodTypeOptions?.map {
                    it.value ?: ""
                } ?: emptyList(),
                label = stringResource(Res.string.feature_recurring_deposit_interest_compounding_period),
            )
            MifosTextFieldDropdown(
                value = if (state.recurringDepositAccountInterestChart.interestPostingPeriodType == -1) {
                    ""
                } else {
                    state.template.interestPostingPeriodTypeOptions?.get(state.recurringDepositAccountInterestChart.interestPostingPeriodType)?.value
                        ?: ""
                },
                onValueChanged = { },
                onOptionSelected = { index, value ->
                    onAction(
                        RecurringAccountAction.RecurringAccountTermAction.OnInterestPostingPeriodType(
                            index,
                        ),
                    )
                },
                options = state.template.interestPostingPeriodTypeOptions?.map {
                    it.value ?: ""
                } ?: emptyList(),
                label = stringResource(Res.string.feature_recurring_deposit_interest_posting_period),
            )
            MifosTextFieldDropdown(
                value = if (state.recurringDepositAccountInterestChart.interestCalculationType == -1) {
                    ""
                } else {
                    state.template.interestCalculationTypeOptions?.get(state.recurringDepositAccountInterestChart.interestCalculationType)?.value
                        ?: ""
                },
                onValueChanged = { },
                onOptionSelected = { index, value ->
                    onAction(
                        RecurringAccountAction.RecurringAccountTermAction.OnInterestCalculationType(
                            index,
                        ),
                    )
                },
                options = state.template.interestCalculationTypeOptions?.map {
                    it.value ?: ""
                } ?: emptyList(),
                label = stringResource(Res.string.feature_recurring_deposit_interest_calculation),
            )
            MifosTextFieldDropdown(
                value = if (state.recurringDepositAccountInterestChart.interestCalculationDaysInYearType == -1) {
                    ""
                } else {
                    state.template.interestCalculationDaysInYearTypeOptions?.get(state.recurringDepositAccountInterestChart.interestCalculationDaysInYearType)?.value
                        ?: ""
                },
                onValueChanged = { },
                onOptionSelected = { index, value ->
                    onAction(
                        RecurringAccountAction.RecurringAccountTermAction.OnInterestCalculationDaysInYearType(
                            index,
                        ),
                    )
                },
                options = state.template.interestCalculationDaysInYearTypeOptions?.map {
                    it.value ?: ""
                } ?: emptyList(),
                label = stringResource(Res.string.feature_recurring_deposit_calculation_days_in_year),
            )
        }

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_recurring_deposit_back),
            secondBtnText = stringResource(Res.string.feature_recurring_deposit_next),
            onFirstBtnClick = { onAction(RecurringAccountAction.OnBackPress) },
            onSecondBtnClick = { onAction(RecurringAccountAction.OnNextPress) },
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )
    }
}
