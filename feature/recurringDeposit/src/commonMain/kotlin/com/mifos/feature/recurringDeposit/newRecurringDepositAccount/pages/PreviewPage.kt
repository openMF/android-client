/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages

import kpt.feature.recurringdeposit.generated.resources.Res
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_account_deposit_frequency
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_active_charge
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_adjust_advance_payments
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_allow_withdrawals
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_apply_penal_interest
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_back
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_calculation_days_in_year
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_charges_page
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_deposit_frequency_same_as_meeting
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_deposit_period
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_description
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_empty_date
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_end_date
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_external_id
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_field_officer
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_grouping_by_amount
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_in_multiples_of
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_interest_calculation
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_interest_compounding_period
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_interest_page
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_interest_posting_period
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_interest_rate_chart
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_is_mandatory_deposit
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_lock_in_period
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_maximum_deposit_term
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_minimum_deposit_term
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_name
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_next
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_no
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_no_interest_chart
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_penal_interest_percentage
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_period
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_product_name
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_recurring_deposit_amount
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_details
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_settings
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_terms
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_submitted_on
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_valid_from_date
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_view
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_yes
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
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosDefaultListingComponentFromStringResources
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountAction
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountState
import org.jetbrains.compose.resources.stringResource
import kpt.core.base.designsystem.KptTheme
import kpt.core.base.designsystem.theme.LocalKptSpacing

@Composable
fun PreviewPage(
    state: RecurringAccountState,
    modifier: Modifier = Modifier,
    onAction: (RecurringAccountAction) -> Unit,
) {
    Column(
        Modifier.fillMaxSize().padding(bottom = LocalKptSpacing.current.md),
    ) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
        ) {
            Text(
                text = stringResource(Res.string.feature_recurring_deposit_step_details),
                style = MifosTypography.labelLarge,
            )
            DetailsCard(state)

            Text(
                text = stringResource(Res.string.feature_recurring_deposit_step_terms),
                style = MifosTypography.labelLarge,
            )

            TermsCard(state)

            Text(
                text = stringResource(Res.string.feature_recurring_deposit_step_settings),
                style = MifosTypography.labelLarge,
            )
            SettingCard(state)

            Text(
                text = stringResource(Res.string.feature_recurring_deposit_interest_page),
                style = MifosTypography.labelLarge,
            )

            InterestCard(state, onAction)

            Text(
                text = stringResource(Res.string.feature_recurring_deposit_charges_page),
                style = MifosTypography.labelLarge,
            )

            MifosRowWithTextAndButton(
                onBtnClick = {
                    onAction(
                        RecurringAccountAction.ShowListOfChargesDialog,
                    )
                },
                btnText = stringResource(Res.string.feature_recurring_deposit_view),
                text = state.addedCharges.size.toString() + " " + stringResource(Res.string.feature_recurring_deposit_active_charge),
                btnEnabled = state.addedCharges.isNotEmpty(),
            )
        }
        Spacer(Modifier.height(LocalKptSpacing.current.md))
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_recurring_deposit_back),
            secondBtnText = stringResource(Res.string.feature_recurring_deposit_next),
            onFirstBtnClick = {
                onAction(RecurringAccountAction.OnBackPress)
            },
            onSecondBtnClick = {
                onAction(RecurringAccountAction.OnSubmitRecurringAccount)
            },
        )
    }
}

@Composable
fun InterestCard(
    state: RecurringAccountState,
    onAction: (RecurringAccountAction) -> Unit,
) {
    Column {
        MifosDefaultListingComponentFromStringResources(
            data = mapOf(
                Res.string.feature_recurring_deposit_name to state.template.accountChart?.name.orEmpty(),
                Res.string.feature_recurring_deposit_valid_from_date to (
                    state.template.accountChart?.fromDate?.let {
                        DateHelper.getDateAsString(it)
                    } ?: stringResource(Res.string.feature_recurring_deposit_empty_date)
                    ),
                Res.string.feature_recurring_deposit_end_date to (
                    state.template.accountChart?.endDate?.let {
                        DateHelper.getDateAsString(it)
                    } ?: stringResource(Res.string.feature_recurring_deposit_empty_date)
                    ),
                Res.string.feature_recurring_deposit_description to state.template.accountChart?.description.orEmpty(),
                Res.string.feature_recurring_deposit_grouping_by_amount to if (state.template.accountChart?.isPrimaryGroupingByAmount == true) {
                    stringResource(
                        Res.string.feature_recurring_deposit_yes,
                    )
                } else {
                    stringResource(Res.string.feature_recurring_deposit_no)
                },
            ),
            verticalArrangement = Arrangement.spacedBy(LocalKptSpacing.current.sm),
        )

        Spacer(Modifier.height(LocalKptSpacing.current.sm))

        MifosRowWithTextAndButton(
            onBtnClick = {
                onAction(RecurringAccountAction.OnShowRateChartDialog)
            },
            btnText = stringResource(Res.string.feature_recurring_deposit_view),
            text = if (state.isRateChartEmpty) {
                stringResource(Res.string.feature_recurring_deposit_interest_rate_chart)
            } else {
                stringResource(
                    Res.string.feature_recurring_deposit_no_interest_chart,
                )
            },
            btnEnabled = state.isRateChartEmpty,
        )
    }
}

@Composable
fun SettingCard(state: RecurringAccountState) {
    MifosDefaultListingComponentFromStringResources(
        data = mapOf(
            Res.string.feature_recurring_deposit_is_mandatory_deposit to if (state.recurringDepositAccountSettings.isMandatory) {
                stringResource(
                    Res.string.feature_recurring_deposit_yes,
                )
            } else {
                stringResource(Res.string.feature_recurring_deposit_no)
            },
            Res.string.feature_recurring_deposit_adjust_advance_payments to if (state.recurringDepositAccountSettings.adjustAdvancePayments) {
                stringResource(
                    Res.string.feature_recurring_deposit_yes,
                )
            } else {
                stringResource(Res.string.feature_recurring_deposit_no)
            },
            Res.string.feature_recurring_deposit_allow_withdrawals to if (state.recurringDepositAccountSettings.allowWithdrawals) {
                stringResource(
                    Res.string.feature_recurring_deposit_yes,
                )
            } else {
                stringResource(Res.string.feature_recurring_deposit_no)
            },

            Res.string.feature_recurring_deposit_lock_in_period to state.recurringDepositAccountSettings.lockInPeriod.frequency + " " + if (state.recurringDepositAccountSettings.lockInPeriod.frequencyTypeIndex != -1) {
                (
                    state.template.lockinPeriodFrequencyTypeOptions?.getOrNull(
                        state.recurringDepositAccountSettings.lockInPeriod.frequencyTypeIndex,
                    )?.value.orEmpty()
                    )
            } else {
                ""
            },

            Res.string.feature_recurring_deposit_recurring_deposit_amount to state.template.currency?.displaySymbol.orEmpty() + " " + state.recurringDepositAccountSettings.recurringDepositDetails.depositAmount,

            Res.string.feature_recurring_deposit_deposit_period to state.recurringDepositAccountSettings.depositPeriod.period + " " + (
                state.template.periodFrequencyTypeOptions?.getOrNull(
                    state.recurringDepositAccountSettings.depositPeriod.periodType,
                )?.value.orEmpty()
                ),

            if (!state.recurringDepositAccountSettings.depositPeriod.depositFrequencySameAsGroupCenterMeeting) {
                Res.string.feature_recurring_account_deposit_frequency to state.recurringDepositAccountSettings.recurringFrequency + " " + (
                    state.template.periodFrequencyTypeOptions?.getOrNull(
                        state.recurringDepositAccountSettings.recurringFrequencyTypeIndex,
                    )?.value.orEmpty()
                    )
            } else {
                Res.string.feature_recurring_deposit_deposit_frequency_same_as_meeting to if (state.recurringDepositAccountSettings.depositPeriod.depositFrequencySameAsGroupCenterMeeting) {
                    stringResource(
                        Res.string.feature_recurring_deposit_yes,
                    )
                } else {
                    stringResource(Res.string.feature_recurring_deposit_no)
                }
            },

            Res.string.feature_recurring_deposit_minimum_deposit_term to state.recurringDepositAccountSettings.minimumDepositTerm.frequency + " " + if (state.recurringDepositAccountSettings.minimumDepositTerm.frequencyTypeIndex != -1) {
                (
                    state.template.periodFrequencyTypeOptions?.getOrNull(
                        state.recurringDepositAccountSettings.minimumDepositTerm.frequencyTypeIndex,
                    )?.value.orEmpty()
                    )
            } else {
                ""
            },

            Res.string.feature_recurring_deposit_in_multiples_of to state.recurringDepositAccountSettings.minimumDepositTerm.frequencyAfterInMultiplesOf + " " + if (state.recurringDepositAccountSettings.minimumDepositTerm.frequencyTypeIndex != -1) {
                (
                    state.template.periodFrequencyTypeOptions?.getOrNull(
                        state.recurringDepositAccountSettings.minimumDepositTerm.frequencyTypeIndexAfterInMultiplesOf,
                    )?.value.orEmpty()
                    )
            } else {
                ""
            },

            Res.string.feature_recurring_deposit_maximum_deposit_term to state.recurringDepositAccountSettings.maxDepositTerm.frequency + " " + if (state.recurringDepositAccountSettings.maxDepositTerm.frequencyTypeIndex != -1) {
                state.template.periodFrequencyTypeOptions?.getOrNull(
                    state.recurringDepositAccountSettings.maxDepositTerm.frequencyTypeIndex,
                )?.value.orEmpty()
            } else {
                ""
            },

            Res.string.feature_recurring_deposit_apply_penal_interest to if (state.recurringDepositAccountSettings.preMatureClosure.applyPenalInterest) {
                stringResource(
                    Res.string.feature_recurring_deposit_yes,
                )
            } else {
                stringResource(Res.string.feature_recurring_deposit_no)
            },

            Res.string.feature_recurring_deposit_penal_interest_percentage to state.recurringDepositAccountSettings.preMatureClosure.penalInterest,

            Res.string.feature_recurring_deposit_period to if (state.recurringDepositAccountSettings.preMatureClosure.interestPeriodIndex != -1) {
                state.template.preClosurePenalInterestOnTypeOptions?.getOrNull(
                    state.recurringDepositAccountSettings.preMatureClosure.interestPeriodIndex,
                )?.value.orEmpty()
            } else {
                ""
            },
            Res.string.feature_recurring_deposit_period to state.template.currency?.displaySymbol.orEmpty() + " " + state.recurringDepositAccountSettings.preMatureClosure.minimumBalanceForInterestCalculation,
        ),
        verticalArrangement = Arrangement.spacedBy(LocalKptSpacing.current.sm),
    )
}

@Composable
private fun TermsCard(
    state: RecurringAccountState,
) {
    MifosDefaultListingComponentFromStringResources(
        data = mapOf(
            Res.string.feature_recurring_deposit_interest_compounding_period to if (state.recurringDepositAccountInterestChart.interestCompoundingPeriodType != -1) {
                (
                    state.template.interestCompoundingPeriodTypeOptions?.get(
                        state.recurringDepositAccountInterestChart.interestCompoundingPeriodType,
                    )?.value.orEmpty()
                    )
            } else {
                ""
            },
            Res.string.feature_recurring_deposit_interest_posting_period to if (state.recurringDepositAccountInterestChart.interestPostingPeriodType != -1) {
                state.template.interestPostingPeriodTypeOptions?.get(
                    state.recurringDepositAccountInterestChart.interestPostingPeriodType,
                )?.value.orEmpty()
            } else {
                ""
            },
            Res.string.feature_recurring_deposit_interest_calculation to if (state.recurringDepositAccountInterestChart.interestCalculationType != -1) {
                state.template.interestCalculationTypeOptions?.get(
                    state.recurringDepositAccountInterestChart.interestCalculationType,
                )?.value.orEmpty()
            } else {
                ""
            },
            Res.string.feature_recurring_deposit_calculation_days_in_year to if (state.recurringDepositAccountInterestChart.interestCalculationDaysInYearType != -1) {
                state.template.interestCalculationDaysInYearTypeOptions?.get(
                    state.recurringDepositAccountInterestChart.interestCalculationDaysInYearType,
                )?.value.orEmpty()
            } else {
                ""
            },
        ),
        verticalArrangement = Arrangement.spacedBy(LocalKptSpacing.current.sm),
    )
}

@Composable
private fun DetailsCard(
    state: RecurringAccountState,
) {
    MifosDefaultListingComponentFromStringResources(
        data = mapOf(
            Res.string.feature_recurring_deposit_product_name to state.template.productOptions?.get(
                state.recurringDepositAccountDetail.loanProductSelected,
            )?.name.orEmpty(),
            Res.string.feature_recurring_deposit_submitted_on to state.recurringDepositAccountDetail.submissionDate,
            Res.string.feature_recurring_deposit_field_officer to if (state.recurringDepositAccountDetail.fieldOfficerIndex != -1) {
                (
                    state.template.fieldOfficerOptions?.get(
                        state.recurringDepositAccountDetail.fieldOfficerIndex,
                    )?.displayName.orEmpty()
                    )
            } else {
                ""
            },
            Res.string.feature_recurring_deposit_external_id to state.recurringDepositAccountDetail.externalId,
        ),
        verticalArrangement = Arrangement.spacedBy(LocalKptSpacing.current.sm),
    )
}
