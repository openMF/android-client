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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
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

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(stringResource(Res.string.feature_recurring_deposit_step_settings), fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(settingsState.isMandatory, onCheckedChange = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.ToggleMandatoryDeposit) })
                Text(stringResource(Res.string.feature_recurring_deposit_is_mandatory_deposit))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                MifosCheckBox(
                    text = stringResource(Res.string.feature_recurring_deposit_adjust_advance_payments),
                    checked = settingsState.adjustAdvancePayments,
                    onCheckChanged = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.ToggleAdvancePaymentsTowardsFutureInstallments) },
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                MifosCheckBox(
                    text = stringResource(Res.string.feature_recurring_deposit_allow_withdrawals),
                    checked = settingsState.allowWithdrawals,
                    onCheckChanged = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.ToggleAllowWithdrawals) },
                )
            }
        }

        Text(stringResource(Res.string.feature_recurring_deposit_lock_in_period), fontWeight = FontWeight.Bold)
        MifosOutlinedTextField(
            value = settingsState.lockInPeriod.frequency,
            onValueChange = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.SetLockInPeriod(it)) },
            label = stringResource(Res.string.feature_recurring_deposit_frequency),
            config = MifosTextFieldConfig(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            ),
            modifier = Modifier.fillMaxWidth(),
        )
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
        Text(stringResource(Res.string.feature_recurring_deposit_recurring_deposit_details), fontWeight = FontWeight.Bold)
        MifosOutlinedTextField(
            value = settingsState.recurringDepositDetails.depositAmount,
            onValueChange = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.SetRecurringDepositAmount(it)) },
            label = stringResource(Res.string.feature_recurring_deposit_recurring_deposit_amount),
            config = MifosTextFieldConfig(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                prefix = {
                    Text(state.template.currency?.displaySymbol.orEmpty())
                },
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(stringResource(Res.string.feature_recurring_deposit_deposit_period), fontWeight = FontWeight.Bold)
        MifosOutlinedTextField(
            value = settingsState.depositPeriod.period,
            onValueChange = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.SetDepositPeriod(it)) },
            label = stringResource(Res.string.feature_recurring_deposit_deposit_period),
            config = MifosTextFieldConfig(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            ),
            modifier = Modifier.fillMaxWidth(),
        )
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
                onAction(RecurringAccountAction.RecurringAccountSettingsAction.SetDepositPeriodType(id))
            },
            label = stringResource(Res.string.feature_recurring_deposit_type),
            modifier = Modifier.fillMaxWidth(),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            MifosCheckBox(
                text = stringResource(Res.string.feature_recurring_deposit_deposit_frequency_same_as_meeting),
                checked = settingsState.depositPeriod.depositFrequencySameAsGroupCenterMeeting,
                onCheckChanged = {
                    onAction(RecurringAccountAction.RecurringAccountSettingsAction.ToggleDepositFrequencySameAsGroupCenterMeeting)
                },
            )
        }
        Text(stringResource(Res.string.feature_recurring_deposit_minimum_deposit_term), fontWeight = FontWeight.Bold)
        MifosOutlinedTextField(
            value = settingsState.minimumDepositTerm.frequency,
            onValueChange = {
                onAction(RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreq(it))
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
                onAction(RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreqType(id))
            },
            label = stringResource(Res.string.feature_recurring_deposit_type),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(stringResource(Res.string.feature_recurring_deposit_in_multiples_of), fontWeight = FontWeight.Bold)
        MifosOutlinedTextField(
            value = settingsState.minimumDepositTerm.frequencyAfterInMultiplesOf,
            onValueChange = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreqAfterInMultiOf(it)) },
            label = stringResource(Res.string.feature_recurring_deposit_frequency),
            config = MifosTextFieldConfig(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            ),
            modifier = Modifier.fillMaxWidth(),
        )
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
                onAction(RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreqTypeAfterInMultiOf(id))
            },
            label = stringResource(Res.string.feature_recurring_deposit_type),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(stringResource(Res.string.feature_recurring_deposit_maximum_deposit_term), fontWeight = FontWeight.Bold)
        MifosOutlinedTextField(
            value = settingsState.maxDepositTerm.frequency,
            onValueChange = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.SetMaxDepositTermFreq(it)) },
            label = stringResource(Res.string.feature_recurring_deposit_frequency),
            config = MifosTextFieldConfig(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            ),
            modifier = Modifier.fillMaxWidth(),
        )
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
                onAction(RecurringAccountAction.RecurringAccountSettingsAction.SetMaxDepositTermFreqType(id))
            },
            label = stringResource(Res.string.feature_recurring_deposit_type),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(stringResource(Res.string.feature_recurring_deposit_for_pre_mature_closure), fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                settingsState.preMatureClosure.applyPenalInterest,
                onCheckedChange = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.TogglePreMatureClosureApplyPenalInterest) },
            )
            Text(stringResource(Res.string.feature_recurring_deposit_apply_penal_interest))
        }
        AnimatedVisibility(
            visible = settingsState.preMatureClosure.applyPenalInterest,
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                MifosOutlinedTextField(
                    value = settingsState.preMatureClosure.penalInterest,
                    onValueChange = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.SetPreMatureClosurePenalInterest(it)) },
                    label = stringResource(Res.string.feature_recurring_deposit_penal_interest_percentage),
                    config = MifosTextFieldConfig(
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
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
                        onAction(RecurringAccountAction.RecurringAccountSettingsAction.SetPreMatureClosureInterestPeriodIndex(id))
                    },
                    label = stringResource(Res.string.feature_recurring_deposit_period),
                    modifier = Modifier.fillMaxWidth(),
                )
                MifosOutlinedTextField(
                    value = settingsState.preMatureClosure.minimumBalanceForInterestCalculation,
                    onValueChange = { onAction(RecurringAccountAction.RecurringAccountSettingsAction.SetPreMatureClosureMinimumBalanceForInterestCalculation(it)) },
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
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_recurring_deposit_back),
            secondBtnText = stringResource(Res.string.feature_recurring_deposit_next),
            onFirstBtnClick = { onAction(RecurringAccountAction.OnBackPress) },
            onSecondBtnClick = { onAction(RecurringAccountAction.OnNextPress) },
            isButtonIconVisible = true,
            isSecondButtonEnabled = settingsState.isSettingsNextEnabled,
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
