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
import androidclient.feature.client.generated.resources.action_view
import androidclient.feature.client.generated.resources.client_identifier_btn_view
import androidclient.feature.client.generated.resources.feature_client_external_id
import androidclient.feature.client.generated.resources.feature_fixed_days_in_year
import androidclient.feature.client.generated.resources.feature_fixed_deposit_deposit_amount
import androidclient.feature.client.generated.resources.feature_fixed_deposit_deposit_period
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_description
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_empty_date
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_end_date
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_grouping_by_amount
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_interest_rate_chart
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_name
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_no
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_no_interest_chart
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_valid_from_date
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_yes
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_apply_penal_interest
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_interest_transfer
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_investing_account
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_linked_saving_account_field
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_lock_in_period
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_maturity_instructions
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_maximum_deposit_term
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_minimum_deposit_term
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_penal_interest
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_period
import androidclient.feature.client.generated.resources.feature_fixed_deposit_setting_thereafter_in_multiples
import androidclient.feature.client.generated.resources.feature_fixed_interest_calculated_using
import androidclient.feature.client.generated.resources.feature_fixed_interest_compounding_period
import androidclient.feature.client.generated.resources.feature_fixed_interest_posting_period
import androidclient.feature.client.generated.resources.feature_share_account_back
import androidclient.feature.client.generated.resources.feature_share_account_charge_active_charge
import androidclient.feature.client.generated.resources.feature_share_account_submit
import androidclient.feature.client.generated.resources.field_officer
import androidclient.feature.client.generated.resources.one_year_fixed_deposit
import androidclient.feature.client.generated.resources.step_charges
import androidclient.feature.client.generated.resources.step_details
import androidclient.feature.client.generated.resources.step_interest
import androidclient.feature.client.generated.resources.step_settings
import androidclient.feature.client.generated.resources.step_terms
import androidclient.feature.client.generated.resources.submission_on
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosDefaultListingComponentFromStringResources
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountAction
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountState
import org.jetbrains.compose.resources.stringResource

@Composable
fun PreviewPage(
    state: NewFixedDepositAccountState,
    modifier: Modifier = Modifier,
    onAction: (NewFixedDepositAccountAction) -> Unit,
) {
    Column(
        Modifier.fillMaxSize().padding(bottom = DesignToken.padding.large),
    ) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = stringResource(Res.string.step_details),
                style = MifosTypography.labelLarge,
            )
            DetailsCard(state)

            Text(
                text = stringResource(Res.string.step_terms),
                style = MifosTypography.labelLarge,
            )

            TermsCard(state)

            Text(
                text = stringResource(Res.string.step_settings),
                style = MifosTypography.labelLarge,
            )

            SettingCard(state)

            Text(
                text = stringResource(Res.string.step_interest),
                style = MifosTypography.labelLarge,
            )

            InterestRate(state, onAction)

            Text(
                text = stringResource(Res.string.step_charges),
                style = MifosTypography.labelLarge,
            )

            MifosRowWithTextAndButton(
                onBtnClick = {
                    onAction(
                        NewFixedDepositAccountAction.NewFixedDepositAccountChargesAction.ShowListOfChargesDialog,
                    )
                },
                btnText = stringResource(Res.string.client_identifier_btn_view),
                text = "${state.fixedDepositAccountCharges.addedCharges.size} ${stringResource(Res.string.feature_share_account_charge_active_charge)}",
                btnEnabled = state.fixedDepositAccountCharges.addedCharges.isNotEmpty(),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_share_account_back),
            secondBtnText = stringResource(Res.string.feature_share_account_submit),
            onFirstBtnClick = {
                onAction(NewFixedDepositAccountAction.PreviousStep)
            },
            onSecondBtnClick = {
                onAction(NewFixedDepositAccountAction.OnSubmitFixedAccount)
            },
        )
    }
}

@Composable
fun DetailsCard(state: NewFixedDepositAccountState) {
    MifosDefaultListingComponentFromStringResources(
        data = mapOf(
            Res.string.one_year_fixed_deposit to state.template.productOptions?.get(
                state.fixedDepositAccountDetail.productSelected,
            )?.name.orEmpty(),
            Res.string.submission_on to state.fixedDepositAccountDetail.submissionDate,
            Res.string.field_officer to (
                if (state.fixedDepositAccountDetail.fieldOfficerIndex == -1) {
                    ""
                } else {
                    state.template.fieldOfficerOptions?.get(state.fixedDepositAccountDetail.fieldOfficerIndex)?.displayName.orEmpty()
                }
                ),
            Res.string.feature_client_external_id to state.fixedDepositAccountDetail.externalId,
        ),
        verticalArrangement = Arrangement.spacedBy(DesignToken.padding.small),
    )
}

@Composable
fun TermsCard(state: NewFixedDepositAccountState) {
    MifosDefaultListingComponentFromStringResources(
        data = mapOf(
            Res.string.feature_fixed_deposit_deposit_amount to state.template.currency?.displaySymbol.orEmpty() + " " + state.fixedDepositAccountTerms.depositAmount,
            Res.string.feature_fixed_deposit_deposit_period to state.fixedDepositAccountTerms.depositPeriod + " " + if (state.fixedDepositAccountTerms.depositPeriodTypeIndex != -1) {
                state.template.periodFrequencyTypeOptions?.getOrNull(state.fixedDepositAccountTerms.depositPeriodTypeIndex)?.value.orEmpty()
            } else {
                ""
            },
            Res.string.feature_fixed_interest_compounding_period to if (state.fixedDepositAccountTerms.interestPostingPeriodTypeIndex != -1) {
                state.template.interestPostingPeriodTypeOptions?.getOrNull(state.fixedDepositAccountTerms.interestPostingPeriodTypeIndex)?.value.orEmpty()
            } else {
                ""
            },
            Res.string.feature_fixed_interest_calculated_using to if (state.fixedDepositAccountTerms.interestCalculationTypeIndex != -1) {
                state.template.interestCalculationTypeOptions?.getOrNull(state.fixedDepositAccountTerms.interestCalculationTypeIndex)?.value.orEmpty()
            } else {
                ""
            },
            Res.string.feature_fixed_interest_posting_period to if (state.fixedDepositAccountTerms.interestPostingPeriodTypeIndex != -1) {
                state.template.interestPostingPeriodTypeOptions?.getOrNull(state.fixedDepositAccountTerms.interestPostingPeriodTypeIndex)?.value.orEmpty()
            } else {
                ""
            },
            Res.string.feature_fixed_days_in_year to if (state.fixedDepositAccountTerms.interestCalculationDaysInYearTypeIndex != -1) {
                state.template.interestCalculationDaysInYearTypeOptions?.getOrNull(state.fixedDepositAccountTerms.interestCalculationDaysInYearTypeIndex)?.value.orEmpty()
            } else {
                ""
            },
        ),
        verticalArrangement = Arrangement.spacedBy(DesignToken.padding.small),
    )
}

@Composable
fun SettingCard(state: NewFixedDepositAccountState) {
    MifosDefaultListingComponentFromStringResources(
        data = mapOf(
            Res.string.feature_fixed_deposit_setting_lock_in_period to state.lockInPeriodFrequency + " " + if (state.lockInPeriodTypeIndex != -1) {
                state.template.lockinPeriodFrequencyTypeOptions?.get(state.lockInPeriodTypeIndex)?.value.orEmpty()
            } else {
                ""
            },
            Res.string.feature_fixed_deposit_setting_minimum_deposit_term to state.minimumDispositTermFrequency + " " + if (state.minimumDispositTermTypeIndex != -1) {
                state.template.periodFrequencyTypeOptions?.get(state.minimumDispositTermTypeIndex)?.value.orEmpty()
            } else {
                ""
            },
            Res.string.feature_fixed_deposit_setting_thereafter_in_multiples to state.multiplesFrequency + " " + if (state.multiplesTypeIndex != -1) {
                state.template.periodFrequencyTypeOptions?.get(state.multiplesTypeIndex)?.value.orEmpty()
            } else {
                ""
            },
            Res.string.feature_fixed_deposit_setting_maximum_deposit_term to state.maximumDispositFrequency + " " + if (state.maximumDispositTypeIndex != -1) {
                state.template.periodFrequencyTypeOptions?.get(state.maximumDispositTypeIndex)?.value.orEmpty()
            } else {
                ""
            },
            Res.string.feature_fixed_deposit_setting_interest_transfer to if (state.transferLinkedSavingAccountInterest) {
                stringResource(
                    Res.string.feature_fixed_deposit_interest_yes,
                )
            } else {
                stringResource(Res.string.feature_fixed_deposit_interest_no)
            },
            Res.string.feature_fixed_deposit_setting_linked_saving_account_field to if (state.linkedSavingAccountIndex != -1) {
                state.template.savingsAccounts?.get(state.linkedSavingAccountIndex)?.accountNo.orEmpty()
            } else {
                ""
            },
            Res.string.feature_fixed_deposit_setting_maturity_instructions to if (state.maturityInstructionsIndex != -1) {
                state.template.maturityInstructionOptions?.get(state.maturityInstructionsIndex)?.value.orEmpty()
            } else {
                ""
            },
            Res.string.feature_fixed_deposit_setting_investing_account to if (state.investingAccountIndex != -1) {
                state.template.savingsAccounts?.get(state.investingAccountIndex)?.accountNo.orEmpty()
            } else {
                ""
            },
            Res.string.feature_fixed_deposit_setting_apply_penal_interest to if (state.applyPenalInterest) {
                stringResource(
                    Res.string.feature_fixed_deposit_interest_yes,
                )
            } else {
                stringResource(Res.string.feature_fixed_deposit_interest_no)
            },

            Res.string.feature_fixed_deposit_setting_penal_interest to state.penalInterest,
            Res.string.feature_fixed_deposit_setting_period to if (state.periodIndex != -1) {
                state.template.periodFrequencyTypeOptions?.get(state.periodIndex)?.value.orEmpty()
            } else {
                ""
            },
        ),
        verticalArrangement = Arrangement.spacedBy(DesignToken.padding.small),
    )
}

@Composable
fun InterestRate(
    state: NewFixedDepositAccountState,
    onAction: (NewFixedDepositAccountAction) -> Unit,
) {
    Column {
        MifosDefaultListingComponentFromStringResources(
            data = mapOf(
                Res.string.feature_fixed_deposit_interest_name to state.template.accountChart?.name.orEmpty(),
                Res.string.feature_fixed_deposit_interest_valid_from_date to (
                    state.template.accountChart?.fromDate?.let {
                        DateHelper.getDateAsString(it)
                    } ?: stringResource(Res.string.feature_fixed_deposit_interest_empty_date)
                    ),
                Res.string.feature_fixed_deposit_interest_end_date to (
                    state.template.accountChart?.endDate?.let {
                        DateHelper.getDateAsString(it)
                    } ?: stringResource(Res.string.feature_fixed_deposit_interest_empty_date)
                    ),
                Res.string.feature_fixed_deposit_interest_description to state.template.accountChart?.description.orEmpty(),
                Res.string.feature_fixed_deposit_interest_grouping_by_amount to if (state.template.accountChart?.isPrimaryGroupingByAmount == true) {
                    stringResource(
                        Res.string.feature_fixed_deposit_interest_yes,
                    )
                } else {
                    stringResource(Res.string.feature_fixed_deposit_interest_no)
                },
            ),
            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.small),
        )

        Spacer(Modifier.height(DesignToken.padding.small))

        MifosRowWithTextAndButton(
            onBtnClick = {
                onAction(NewFixedDepositAccountAction.OnShowRateChart)
            },
            btnText = stringResource(Res.string.action_view),
            text = if (state.isRateChartEmpty) {
                stringResource(Res.string.feature_fixed_deposit_interest_interest_rate_chart)
            } else {
                stringResource(Res.string.feature_fixed_deposit_interest_no_interest_chart)
            },
            btnEnabled = state.isRateChartEmpty,
        )
    }
}
