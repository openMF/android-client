/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createShareAccount.pages

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_share_account_back
import androidclient.feature.client.generated.resources.feature_share_account_charge_active_charge
import androidclient.feature.client.generated.resources.feature_share_account_charge_view
import androidclient.feature.client.generated.resources.feature_share_account_charges
import androidclient.feature.client.generated.resources.feature_share_account_detail_external_id
import androidclient.feature.client.generated.resources.feature_share_account_detail_product_name
import androidclient.feature.client.generated.resources.feature_share_account_detail_submission_date
import androidclient.feature.client.generated.resources.feature_share_account_details
import androidclient.feature.client.generated.resources.feature_share_account_submit
import androidclient.feature.client.generated.resources.feature_share_account_terms
import androidclient.feature.client.generated.resources.feature_share_account_terms_allow_dividends
import androidclient.feature.client.generated.resources.feature_share_account_terms_application_date
import androidclient.feature.client.generated.resources.feature_share_account_terms_currency
import androidclient.feature.client.generated.resources.feature_share_account_terms_current_price
import androidclient.feature.client.generated.resources.feature_share_account_terms_default_savings_account
import androidclient.feature.client.generated.resources.feature_share_account_terms_lock_in_period
import androidclient.feature.client.generated.resources.feature_share_account_terms_min_active_period
import androidclient.feature.client.generated.resources.feature_share_account_terms_total_shares
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
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosDefaultListingComponentFromStringResources
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.client.createShareAccount.CreateShareAccountAction
import com.mifos.feature.client.createShareAccount.CreateShareAccountState
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme

@Composable
fun PreviewPage(
    state: CreateShareAccountState,
    modifier: Modifier = Modifier,
    onAction: (CreateShareAccountAction) -> Unit,
) {
    Column(
        Modifier.fillMaxSize().padding(bottom = KptTheme.spacing.sm),
    ) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.largeIncreased),
        ) {
            Text(
                text = stringResource(Res.string.feature_share_account_details),
                style = MifosTypography.labelLarge,
            )
            DetailsCard(state)

            Text(
                text = stringResource(Res.string.feature_share_account_terms),
                style = MifosTypography.labelLarge,
            )

            TermsCard(state)

            Text(
                text = stringResource(Res.string.feature_share_account_charges),
                style = MifosTypography.labelLarge,
            )

            MifosRowWithTextAndButton(
                onBtnClick = {
                    onAction(
                        CreateShareAccountAction.ShowListOfChargesDialog,
                    )
                },
                btnText = stringResource(Res.string.feature_share_account_charge_view),
                text = state.addedCharges.size.toString() + " " + stringResource(Res.string.feature_share_account_charge_active_charge),
                btnEnabled = state.addedCharges.isNotEmpty(),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_share_account_back),
            secondBtnText = stringResource(Res.string.feature_share_account_submit),
            onFirstBtnClick = {
                onAction(CreateShareAccountAction.PreviousStep)
            },
            onSecondBtnClick = {
                onAction(CreateShareAccountAction.SubmitShareAccount)
            },
        )
    }
}

@Composable
private fun TermsCard(
    state: CreateShareAccountState,
) {
    MifosDefaultListingComponentFromStringResources(
        data = mapOf(
            Res.string.feature_share_account_terms_currency to state.currency.orEmpty(),
            Res.string.feature_share_account_terms_current_price to state.currentPrice.orEmpty(),
            Res.string.feature_share_account_terms_total_shares to state.totalShares,
            Res.string.feature_share_account_terms_default_savings_account to state.savingsAccountOptions[state.savingsAccountIdx!!].savingsProductName.orEmpty(),
            Res.string.feature_share_account_terms_application_date to state.applicationDate,
            Res.string.feature_share_account_terms_allow_dividends to if (state.isDividendAllowed) "Yes" else "No",
            Res.string.feature_share_account_terms_min_active_period to if (state.minActivePeriodFreqTypeIdx != null) state.minActivePeriodFreq + " " + state.minimumActivePeriodFrequencyTypeOptions[state.minActivePeriodFreqTypeIdx].value else "",
            Res.string.feature_share_account_terms_lock_in_period to if (state.lockInPeriodFreqTypeIdx != null) state.lockInPeriodFreq + " " + state.lockInPeriodFrequencyTypeOptions[state.lockInPeriodFreqTypeIdx].value else "",
        ),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
    )
}

@Composable
private fun DetailsCard(
    state: CreateShareAccountState,
) {
    MifosDefaultListingComponentFromStringResources(
        data = mapOf(
            Res.string.feature_share_account_detail_product_name to state.productOption[state.shareProductIndex!!].name,
            Res.string.feature_share_account_detail_submission_date to state.submissionDate,
            Res.string.feature_share_account_detail_external_id to state.externalId.orEmpty(),
        ),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
    )
}
