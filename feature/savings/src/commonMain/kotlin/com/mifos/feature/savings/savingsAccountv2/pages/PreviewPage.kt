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
import androidclient.feature.savings.generated.resources.feature_savings_charges_active_count
import androidclient.feature.savings.generated.resources.feature_savings_currency
import androidclient.feature.savings.generated.resources.feature_savings_days_in_year
import androidclient.feature.savings.generated.resources.feature_savings_external_id
import androidclient.feature.savings.generated.resources.feature_savings_field_officer
import androidclient.feature.savings.generated.resources.feature_savings_interest_calc
import androidclient.feature.savings.generated.resources.feature_savings_interest_comp
import androidclient.feature.savings.generated.resources.feature_savings_interest_p_period
import androidclient.feature.savings.generated.resources.feature_savings_no
import androidclient.feature.savings.generated.resources.feature_savings_product_name
import androidclient.feature.savings.generated.resources.feature_savings_submission_date
import androidclient.feature.savings.generated.resources.feature_savings_submit
import androidclient.feature.savings.generated.resources.feature_savings_yes
import androidclient.feature.savings.generated.resources.step_charges
import androidclient.feature.savings.generated.resources.step_charges_view
import androidclient.feature.savings.generated.resources.step_details
import androidclient.feature.savings.generated.resources.step_terms
import androidclient.feature.savings.generated.resources.step_terms_apply_withdrawal_fee
import androidclient.feature.savings.generated.resources.step_terms_decimal_places
import androidclient.feature.savings.generated.resources.step_terms_is_allowed_overdraft
import androidclient.feature.savings.generated.resources.step_terms_lock_in_period
import androidclient.feature.savings.generated.resources.step_terms_min_opening_balance
import androidclient.feature.savings.generated.resources.step_terms_minimum_balance
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosGeneralCard
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.savings.savingsAccountv2.SavingsAccountAction
import com.mifos.feature.savings.savingsAccountv2.SavingsAccountState
import org.jetbrains.compose.resources.stringResource
import kotlin.collections.mapOf

@Composable
fun PreviewPage(
    state: SavingsAccountState,
    modifier: Modifier = Modifier,
    onAction: (SavingsAccountAction) -> Unit,
) {
    val previewDetailsMap = mapOf(
        stringResource(Res.string.feature_savings_product_name) to
            state.savingProductOptions.getOrNull(state.savingsProductSelected)?.name.orEmpty(),
        stringResource(Res.string.feature_savings_field_officer) to
            state.fieldOfficerOptions.getOrNull(state.fieldOfficerIndex)?.displayName.orEmpty(),
        stringResource(Res.string.feature_savings_submission_date) to state.submissionDate,
        stringResource(Res.string.feature_savings_external_id) to state.externalId,
    )

    val termsDetailsMap = mapOf(
        stringResource(Res.string.feature_savings_currency) to (
            state.savingsProductTemplate?.currencyOptions?.getOrNull(
                state.currencyIndex,
            )?.name.orEmpty()
            ),
        stringResource(Res.string.step_terms_decimal_places) to state.decimalPlaces,
        stringResource(Res.string.feature_savings_interest_comp) to (
            state.savingsProductTemplate?.interestCompoundingPeriodTypeOptions?.getOrNull(
                state.interestCompPeriodIndex,
            )?.value.orEmpty()
            ),
        stringResource(Res.string.feature_savings_interest_p_period) to (
            state.savingsProductTemplate?.interestPostingPeriodTypeOptions?.getOrNull(
                state.interestPostingPeriodIndex,
            )?.value.orEmpty()
            ),
        stringResource(Res.string.feature_savings_interest_calc) to (
            state.savingsProductTemplate?.interestCalculationTypeOptions?.getOrNull(
                state.interestCalcIndex,
            )?.value.orEmpty()
            ),

        stringResource(Res.string.feature_savings_days_in_year) to (
            state.savingsProductTemplate?.interestCalculationDaysInYearTypeOptions?.getOrNull(
                state.daysInYearIndex,
            )?.value.orEmpty()
            ),
        stringResource(Res.string.step_terms_apply_withdrawal_fee)
            to (
                state.isCheckedApplyWithdrawalFee.let {
                    if (it) {
                        stringResource(Res.string.feature_savings_yes)
                    } else {
                        stringResource(Res.string.feature_savings_no)
                    }
                }
                ),
        stringResource(Res.string.step_terms_is_allowed_overdraft) to
            (
                state.isCheckedOverdraftAllowed.let {
                    if (it) {
                        stringResource(Res.string.feature_savings_yes)
                    } else {
                        stringResource(Res.string.feature_savings_no)
                    }
                }
                ),
        stringResource(Res.string.step_terms_lock_in_period) to
            if (state.freqTypeIndex == -1 || state.frequency.toIntOrNull() == null) {
                ""
            } else {
                state.savingsProductTemplate?.lockinPeriodFrequencyTypeOptions
                    ?.getOrNull(state.freqTypeIndex)?.value
                    ?.let { "${state.frequency} $it" }.orEmpty()
            },
        stringResource(Res.string.step_terms_minimum_balance) to state.monthlyMinimumBalance,
        stringResource(Res.string.step_terms_min_opening_balance) to state.minimumOpeningBalance,

    )

    Column(modifier = Modifier.fillMaxSize().padding(bottom = DesignToken.padding.large)) {
        LazyColumn(modifier = modifier.weight(1f)) {
            item {
                Text(
                    stringResource(Res.string.step_details),
                    style = MifosTypography.labelLargeEmphasized,
                )
                Spacer(Modifier.height(DesignToken.padding.large))
            }

            item {
                MifosGeneralCard(
                    contentMap = previewDetailsMap,
                )
                Spacer(Modifier.height(DesignToken.padding.large))
            }

            item {
                Text(
                    stringResource(Res.string.step_terms),
                    style = MifosTypography.labelLargeEmphasized,
                )
                Spacer(Modifier.height(DesignToken.padding.large))
            }

            item {
                MifosGeneralCard(
                    contentMap = termsDetailsMap,
                )
                Spacer(Modifier.height(DesignToken.padding.large))
            }

            item {
                Text(
                    stringResource(Res.string.step_charges),
                    style = MifosTypography.labelLargeEmphasized,
                )
                Spacer(Modifier.height(DesignToken.padding.large))
            }

            item {
                MifosRowWithTextAndButton(
                    onBtnClick = {
                        onAction(SavingsAccountAction.ShowCharges)
                    },
                    btnText = stringResource(Res.string.step_charges_view),
                    text = stringResource(
                        Res.string.feature_savings_charges_active_count,
                        state.addedCharges.size,
                    ),
                    btnEnabled = state.addedCharges.isNotEmpty(),
                )
                Spacer(Modifier.height(DesignToken.padding.large))
            }
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_savings_back),
            secondBtnText = stringResource(Res.string.feature_savings_submit),
            onFirstBtnClick = {
                onAction(SavingsAccountAction.PreviousStep)
            },
            onSecondBtnClick = {
                onAction(SavingsAccountAction.SubmitSavingsApplication)
            },
        )
    }
}
