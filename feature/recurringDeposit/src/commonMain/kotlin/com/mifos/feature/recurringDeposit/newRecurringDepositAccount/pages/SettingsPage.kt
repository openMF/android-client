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
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_adjust_advance_payments
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_allow_withdrawals
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_apply_penal_interest
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_back
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_deposit_frequency_same_as_meeting
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_deposit_period
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_for_pre_mature_closure
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_frequency
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_in_multiples_of
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_is_mandatory_deposit
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_lock_in_period
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_maximum_deposit_term
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_minimum_balance_for_interest
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_minimum_deposit_term
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_next
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_penal_interest_percentage
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_period
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_recurring_deposit_amount
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_recurring_deposit_details
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_settings
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_type
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosCheckBox
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountAction
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(
    state: RecurringAccountState,
    onAction: (RecurringAccountAction) -> Unit,
) {
    val settingsState = state.recurringDepositAccountSettings

    Column(Modifier.fillMaxSize().padding(bottom = DesignToken.padding.large)) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.feature_recurring_deposit_step_settings),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosCheckBox(
                text = stringResource(Res.string.feature_recurring_deposit_is_mandatory_deposit),
                checked = settingsState.isMandatory,
                onCheckChanged = {
                    onAction(RecurringAccountAction.RecurringAccountSettingsAction.ToggleMandatoryDeposit)
                },
            )

            Spacer(Modifier.height(DesignToken.padding.small))

            MifosCheckBox(
                text = stringResource(Res.string.feature_recurring_deposit_adjust_advance_payments),
                checked = settingsState.adjustAdvancePayments,
                onCheckChanged = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.ToggleAdvancePaymentsTowardsFutureInstallments) },
            )

            Spacer(Modifier.height(DesignToken.padding.small))

            MifosCheckBox(
                text = stringResource(Res.string.feature_recurring_deposit_allow_withdrawals),
                checked = settingsState.allowWithdrawals,
                onCheckChanged = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.ToggleAllowWithdrawals) },
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            Text(
                stringResource(Res.string.feature_recurring_deposit_lock_in_period),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = settingsState.lockInPeriod.frequency,
                onValueChange = {
                    onAction(
                        RecurringAccountAction.RecurringAccountSettingsAction.SetLockInPeriod(
                            it,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_recurring_deposit_frequency),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(DesignToken.padding.small))
            MifosTextFieldDropdown(
                value = if (settingsState.lockInPeriod.frequencyTypeIndex != -1) {
                    state.template.lockinPeriodFrequencyTypeOptions
                        ?.getOrNull(settingsState.lockInPeriod.frequencyTypeIndex)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.lockinPeriodFrequencyTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { id, name ->
                    onAction(
                        RecurringAccountAction.RecurringAccountSettingsAction.SetLockInPeriodType(id),
                    )
                },
                label = stringResource(Res.string.feature_recurring_deposit_type),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Text(
                stringResource(Res.string.feature_recurring_deposit_recurring_deposit_details),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosOutlinedTextField(
                value = settingsState.recurringDepositDetails.depositAmount,
                onValueChange = {
                    onAction(
                        RecurringAccountAction.RecurringAccountSettingsAction.SetRecurringDepositAmount(
                            it,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_recurring_deposit_recurring_deposit_amount) + "*",
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    prefix = {
                        Text(state.template.currency?.displaySymbol.orEmpty())
                    },
                    isError = state.recurringDepositAccountSettings.depositAmountError != null,
                    errorText = state.recurringDepositAccountSettings.depositAmountError?.let { stringResource(it) },
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Text(
                stringResource(Res.string.feature_recurring_deposit_deposit_period),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosOutlinedTextField(
                value = settingsState.depositPeriod.period,
                onValueChange = {
                    onAction(
                        RecurringAccountAction.RecurringAccountSettingsAction.SetDepositPeriod(
                            it,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_recurring_deposit_deposit_period) + "*",
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    isError = state.recurringDepositAccountSettings.depositPeriodError != null,
                    errorText = state.recurringDepositAccountSettings.depositPeriodError?.let { stringResource(it) },
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(DesignToken.padding.small))
            MifosTextFieldDropdown(
                value = if (settingsState.depositPeriod.periodType != -1) {
                    state.template.periodFrequencyTypeOptions
                        ?.getOrNull(settingsState.depositPeriod.periodType)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.periodFrequencyTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { id, name ->
                    onAction(
                        RecurringAccountAction.RecurringAccountSettingsAction.SetDepositPeriodType(
                            id,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_recurring_deposit_type) + "*",
                modifier = Modifier.fillMaxWidth(),
                errorMessage = state.recurringDepositAccountSettings.depositPeriodTypeError?.let { stringResource(it) },
            )
            MifosCheckBox(
                text = stringResource(Res.string.feature_recurring_deposit_deposit_frequency_same_as_meeting),
                checked = settingsState.depositPeriod.depositFrequencySameAsGroupCenterMeeting,
                onCheckChanged = {
                    onAction(RecurringAccountAction.RecurringAccountSettingsAction.ToggleDepositFrequencySameAsGroupCenterMeeting)
                },
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Text(
                stringResource(Res.string.feature_recurring_deposit_minimum_deposit_term),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosOutlinedTextField(
                value = settingsState.minimumDepositTerm.frequency,
                onValueChange = {
                    onAction(
                        RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreq(
                            it,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_recurring_deposit_frequency),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(DesignToken.padding.small))
            MifosTextFieldDropdown(
                value = if (settingsState.minimumDepositTerm.frequencyTypeIndex != -1) {
                    state.template.periodFrequencyTypeOptions?.getOrNull(settingsState.minimumDepositTerm.frequencyTypeIndex)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.periodFrequencyTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { id, name ->
                    onAction(
                        RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreqType(
                            id,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_recurring_deposit_type),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Text(
                stringResource(Res.string.feature_recurring_deposit_in_multiples_of),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosOutlinedTextField(
                value = settingsState.minimumDepositTerm.frequencyAfterInMultiplesOf,
                onValueChange = {
                    onAction(
                        RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreqAfterInMultiOf(
                            it,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_recurring_deposit_frequency),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(DesignToken.padding.small))
            MifosTextFieldDropdown(
                value = if (settingsState.minimumDepositTerm.frequencyTypeIndexAfterInMultiplesOf != -1) {
                    state.template.periodFrequencyTypeOptions?.getOrNull(settingsState.minimumDepositTerm.frequencyTypeIndexAfterInMultiplesOf)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.periodFrequencyTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { id, name ->
                    onAction(
                        RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreqTypeAfterInMultiOf(
                            id,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_recurring_deposit_type),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Text(
                stringResource(Res.string.feature_recurring_deposit_maximum_deposit_term),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosOutlinedTextField(
                value = settingsState.maxDepositTerm.frequency,
                onValueChange = {
                    onAction(
                        RecurringAccountAction.RecurringAccountSettingsAction.SetMaxDepositTermFreq(
                            it,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_recurring_deposit_frequency),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(DesignToken.padding.small))
            MifosTextFieldDropdown(
                value = if (settingsState.maxDepositTerm.frequencyTypeIndex != -1) {
                    state.template.periodFrequencyTypeOptions?.getOrNull(settingsState.maxDepositTerm.frequencyTypeIndex)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.periodFrequencyTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { id, name ->
                    onAction(
                        RecurringAccountAction.RecurringAccountSettingsAction.SetMaxDepositTermFreqType(
                            id,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_recurring_deposit_type),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Text(
                stringResource(Res.string.feature_recurring_deposit_for_pre_mature_closure),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.small))
            MifosCheckBox(
                text = stringResource(Res.string.feature_recurring_deposit_apply_penal_interest),
                checked = settingsState.preMatureClosure.applyPenalInterest,
                onCheckChanged = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.TogglePreMatureClosureApplyPenalInterest) },
            )

            AnimatedVisibility(
                visible = settingsState.preMatureClosure.applyPenalInterest,
            ) {
                Column {
                    MifosOutlinedTextField(
                        value = settingsState.preMatureClosure.penalInterest,
                        onValueChange = {
                            onAction(
                                RecurringAccountAction.RecurringAccountSettingsAction.SetPreMatureClosurePenalInterest(
                                    it,
                                ),
                            )
                        },
                        label = stringResource(Res.string.feature_recurring_deposit_penal_interest_percentage),
                        config = MifosTextFieldConfig(
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                            ),
                        ),
                    )

                    Spacer(Modifier.height(DesignToken.padding.large))

                    MifosTextFieldDropdown(
                        value = if (settingsState.preMatureClosure.interestPeriodIndex != -1) {
                            state.template.preClosurePenalInterestOnTypeOptions
                                ?.getOrNull(settingsState.preMatureClosure.interestPeriodIndex)?.value.orEmpty()
                        } else {
                            ""
                        },
                        options = state.template.preClosurePenalInterestOnTypeOptions?.map {
                            it.value.orEmpty()
                        } ?: emptyList(),
                        onValueChanged = {},
                        onOptionSelected = { id, name ->
                            onAction(
                                RecurringAccountAction.RecurringAccountSettingsAction.SetPreMatureClosureInterestPeriodIndex(
                                    id,
                                ),
                            )
                        },
                        label = stringResource(Res.string.feature_recurring_deposit_period),
                    )
                    MifosOutlinedTextField(
                        value = settingsState.preMatureClosure.minimumBalanceForInterestCalculation,
                        onValueChange = {
                            onAction(
                                RecurringAccountAction.RecurringAccountSettingsAction.SetPreMatureClosureMinimumBalanceForInterestCalculation(
                                    it,
                                ),
                            )
                        },
                        label = stringResource(Res.string.feature_recurring_deposit_minimum_balance_for_interest),
                        config = MifosTextFieldConfig(
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                            ),
                            prefix = {
                                Text(state.template.currency?.displaySymbol.orEmpty())
                            },
                        ),
                    )
                    Spacer(Modifier.height(DesignToken.padding.large))
                }
            }
        }

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_recurring_deposit_back),
            secondBtnText = stringResource(Res.string.feature_recurring_deposit_next),
            onFirstBtnClick = { onAction(RecurringAccountAction.OnBackPress) },
            onSecondBtnClick = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.OnSettingNext) },
        )
    }
}

@Preview
@Composable
private fun SettingPagePreview() {
    SettingPage(
        state = RecurringAccountState(),
        onAction = {},
    )
}
