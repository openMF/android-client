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
import androidclient.feature.client.generated.resources.feature_client_next
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_apply_penal_interest
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_frequency
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_interest_transfer
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_investing_account
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_linked_saving_account
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_linked_saving_account_field
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_lock_in_period
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_maturity_instructions
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_maximum_deposit_term
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_minimum_deposit_term
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_penal_interest
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_period
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_pre_mature_closure
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_thereafter_in_multiples
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_type
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosCheckBox
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountAction
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountState
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme

@Composable
fun SettingPage(
    state: NewFixedDepositAccountState,
    modifier: Modifier = Modifier,
    onAction: (NewFixedDepositAccountAction) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(bottom = KptTheme.spacing.md)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                stringResource(Res.string.feature_fixed_deposit_setting_lock_in_period),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = state.lockInPeriodFrequency,
                onValueChange = {
                    onAction(NewFixedDepositAccountAction.OnLockInPeriodFrequencyChange(it))
                },
                label = stringResource(Res.string.feature_fixed_deposit_setting_frequency),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                ),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
            MifosTextFieldDropdown(
                value = if (state.lockInPeriodTypeIndex != -1) {
                    state.template.lockinPeriodFrequencyTypeOptions?.get(state.lockInPeriodTypeIndex)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.lockinPeriodFrequencyTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { index, name ->
                    onAction(
                        NewFixedDepositAccountAction.OnLockInPeriodTypeIndexChange(index),
                    )
                },
                label = stringResource(Res.string.feature_fixed_deposit_setting_type),
            )
            Spacer(Modifier.height(KptTheme.spacing.sm))

            Text(
                stringResource(Res.string.feature_fixed_deposit_setting_minimum_deposit_term),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = state.minimumDispositTermFrequency,
                onValueChange = {
                    onAction(
                        NewFixedDepositAccountAction.OnMinimumDepositTermFrequencyChange(it),
                    )
                },
                label = stringResource(Res.string.feature_fixed_deposit_setting_thereafter_in_multiples),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                ),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
            MifosTextFieldDropdown(
                value = if (state.minimumDispositTermTypeIndex != -1) {
                    state.template.periodFrequencyTypeOptions?.get(state.minimumDispositTermTypeIndex)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.periodFrequencyTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { index, name ->
                    onAction(NewFixedDepositAccountAction.OnMinimumDepositTermTypeIndexChange(index))
                },
                label = stringResource(Res.string.feature_fixed_deposit_setting_type),
            )

            Text(
                stringResource(Res.string.feature_fixed_deposit_setting_thereafter_in_multiples),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = state.multiplesFrequency,
                onValueChange = {
                    onAction(NewFixedDepositAccountAction.OnMultiplesFrequencyChange(it))
                },
                label = stringResource(Res.string.feature_fixed_deposit_setting_frequency),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                ),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
            MifosTextFieldDropdown(
                value = if (state.multiplesTypeIndex != -1) {
                    state.template.periodFrequencyTypeOptions?.get(state.multiplesTypeIndex)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.periodFrequencyTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { id, name ->
                    onAction(
                        NewFixedDepositAccountAction.OnMultiplesTypeIndexChange(id),
                    )
                },
                label = stringResource(Res.string.feature_fixed_deposit_setting_type),
            )

            Text(
                stringResource(Res.string.feature_fixed_deposit_setting_maximum_deposit_term),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = state.maximumDispositFrequency,
                onValueChange = {
                    onAction(NewFixedDepositAccountAction.OnMaximumDepositFrequencyChange(it))
                },
                label = stringResource(Res.string.feature_fixed_deposit_setting_frequency),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
            MifosTextFieldDropdown(
                value = if (state.maximumDispositTypeIndex != -1) {
                    state.template.periodFrequencyTypeOptions?.get(state.maximumDispositTypeIndex)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.periodFrequencyTypeOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { index, name ->
                    onAction(
                        NewFixedDepositAccountAction.OnMaximumDepositTypeIndexChange(index),
                    )
                },
                label = stringResource(Res.string.feature_fixed_deposit_setting_type),
            )
            Spacer(Modifier.height(KptTheme.spacing.sm))

            Text(
                stringResource(Res.string.feature_fixed_deposit_setting_interest_transfer),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
            MifosCheckBox(
                text = stringResource(Res.string.feature_fixed_deposit_setting_linked_saving_account),
                checked = state.transferLinkedSavingAccountInterest,
                onCheckChanged = {
                    onAction(
                        NewFixedDepositAccountAction.OnTransferLinkedSavingsAccountInterestChange(it),
                    )
                },
            )
            AnimatedVisibility(state.transferLinkedSavingAccountInterest) {
                MifosTextFieldDropdown(
                    value = if (state.linkedSavingAccountIndex != -1) {
                        state.template.savingsAccounts?.get(state.linkedSavingAccountIndex)?.accountNo.orEmpty()
                    } else {
                        ""
                    },
                    options = state.template.savingsAccounts?.map {
                        it.accountNo
                    } ?: emptyList(),
                    onValueChanged = {},
                    onOptionSelected = { index, name ->
                        onAction(
                            NewFixedDepositAccountAction.OnLinkedSavingAccount(index),
                        )
                    },
                    label = stringResource(Res.string.feature_fixed_deposit_setting_linked_saving_account_field),
                    errorMessage = state.linkedSavingAccountError?.let { stringResource(it) },
                )
            }
            Spacer(Modifier.height(KptTheme.spacing.sm))

            Text(
                stringResource(Res.string.feature_fixed_deposit_setting_maturity_instructions),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
            MifosTextFieldDropdown(
                value = if (state.maturityInstructionsIndex != -1) {
                    state.template.maturityInstructionOptions?.get(state.maturityInstructionsIndex)?.value.orEmpty()
                } else {
                    ""
                },
                options = state.template.maturityInstructionOptions?.map {
                    it.value.orEmpty()
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { index, name ->
                    onAction(
                        NewFixedDepositAccountAction.OnMaturityInstructionIndexChange(index),
                    )
                },
                label = stringResource(Res.string.feature_fixed_deposit_setting_maturity_instructions),
            )

            MifosTextFieldDropdown(
                value = if (state.investingAccountIndex != -1) {
                    state.template.savingsAccounts?.get(state.investingAccountIndex)?.accountNo.orEmpty()
                } else {
                    ""
                },
                options = state.template.savingsAccounts?.map {
                    it.accountNo
                } ?: emptyList(),
                onValueChanged = {},
                onOptionSelected = { index, name ->
                    onAction(
                        NewFixedDepositAccountAction.OnInvestingAccountChange(index),
                    )
                },
                label = stringResource(Res.string.feature_fixed_deposit_setting_investing_account),
                errorMessage = state.investingAccountError?.let { stringResource(it) },
            )
            Spacer(Modifier.height(KptTheme.spacing.sm))

            Text(
                stringResource(Res.string.feature_fixed_deposit_setting_pre_mature_closure),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
            MifosCheckBox(
                text = stringResource(Res.string.feature_fixed_deposit_setting_apply_penal_interest),
                checked = state.applyPenalInterest,
                onCheckChanged = {
                    onAction(
                        NewFixedDepositAccountAction.OnApplyPenalInterestChange(it),
                    )
                },
            )

            AnimatedVisibility(
                visible = state.applyPenalInterest,
            ) {
                Column {
                    MifosOutlinedTextField(
                        value = state.penalInterest,
                        onValueChange = {
                            onAction(
                                NewFixedDepositAccountAction.OnPenalInterestChange(it),
                            )
                        },
                        label = stringResource(Res.string.feature_fixed_deposit_setting_penal_interest),
                        config = MifosTextFieldConfig(
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                            ),
                        ),
                    )

                    Spacer(Modifier.height(KptTheme.spacing.md))

                    MifosTextFieldDropdown(
                        value = if (state.periodIndex != -1) {
                            state.template.periodFrequencyTypeOptions?.get(state.periodIndex)?.value.orEmpty()
                        } else {
                            ""
                        },
                        options = state.template.periodFrequencyTypeOptions?.map {
                            it.value.orEmpty()
                        } ?: emptyList(),
                        onValueChanged = {},
                        onOptionSelected = { index, name ->
                            onAction(
                                NewFixedDepositAccountAction.OnPeriodIndexChange(index),
                            )
                        },
                        label = stringResource(Res.string.feature_fixed_deposit_setting_period),
                    )
                }
            }
        }

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.btn_back),
            secondBtnText = stringResource(Res.string.feature_client_next),
            onFirstBtnClick = { onAction(NewFixedDepositAccountAction.PreviousStep) },
            onSecondBtnClick = { onAction(NewFixedDepositAccountAction.OnSettingNext) },
        )
    }
}
