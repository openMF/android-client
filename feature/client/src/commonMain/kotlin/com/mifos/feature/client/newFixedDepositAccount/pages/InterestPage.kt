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
import androidclient.feature.client.generated.resources.action_view
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.feature_client_next
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_description
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_empty_date
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_end_date
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_grouping_by_amount
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_interest_rate_chart
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_name
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_no
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_no_interest_chart
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_rate_chart
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_valid_from_date
import androidclient.feature.client.generated.resources.feature_fixed_deposit_interest_yes
import androidclient.feature.client.generated.resources.step_interest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosActionsChargeListingComponent
import com.mifos.core.ui.components.MifosDefaultListingComponentFromStringResources
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountAction
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountState
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme

@Composable
fun InterestPage(
    state: NewFixedDepositAccountState,
    modifier: Modifier = Modifier,
    onAction: (NewFixedDepositAccountAction) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(bottom = KptTheme.spacing.md)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.step_interest),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosDefaultListingComponentFromStringResources(
                data = mapOf(
                    Res.string.feature_fixed_deposit_interest_name to state.template.accountChart?.name.orEmpty(),
                    Res.string.feature_fixed_deposit_interest_valid_from_date to (
                        state.template.accountChart?.fromDate
                            ?.let { DateHelper.getDateAsString(it) }
                            ?: stringResource(Res.string.feature_fixed_deposit_interest_empty_date)
                        ),
                    Res.string.feature_fixed_deposit_interest_end_date to (
                        state.template.accountChart?.endDate
                            ?.let { DateHelper.getDateAsString(it) }
                            ?: stringResource(Res.string.feature_fixed_deposit_interest_empty_date)
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
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            )

            Spacer(Modifier.height(KptTheme.spacing.md))

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
            Spacer(Modifier.height(KptTheme.spacing.md))
        }

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.btn_back),
            secondBtnText = stringResource(Res.string.feature_client_next),
            onFirstBtnClick = { onAction(NewFixedDepositAccountAction.PreviousStep) },
            onSecondBtnClick = { onAction(NewFixedDepositAccountAction.OnNextPress) },
        )
    }
}

@Composable
fun FixedDepositRateChart(
    state: NewFixedDepositAccountState,
    onAction: (NewFixedDepositAccountAction) -> Unit,
) {
    MifosBottomSheet(
        onDismiss = {
            onAction(NewFixedDepositAccountAction.OnDismissDialog)
        },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(KptTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
            ) {
                Text(
                    text = stringResource(Res.string.feature_fixed_deposit_interest_rate_chart),
                    style = MifosTypography.titleMediumEmphasized,
                )

                state.template.accountChart?.chartSlabs?.forEachIndexed { index, slab ->

                    /** here amountRangeTo implement later because currently API not support amountRangeTo*/
                    MifosActionsChargeListingComponent(
                        chargeTitle = CurrencyFormatter.format(
                            balance = slab.amountRangeFrom,
                            currencyCode = slab.currency?.code,
                            maximumFractionDigits = slab.currency?.decimalPlaces,
                        ) + " - ",
                        type = slab.description ?: "",
                        collectedOn = "@" + slab.annualInterestRate.toString() + "%",
                        date = slab.fromPeriod.toString() + " - " + slab.periodType?.value,
                        onActionClicked = {},
                        isExpanded = false,
                        onExpandToggle = {},
                    )
                }

                MifosTwoButtonRow(
                    firstBtnText = stringResource(Res.string.btn_back),
                    secondBtnText = stringResource(Res.string.feature_client_next),
                    onFirstBtnClick = {
                        onAction(NewFixedDepositAccountAction.OnDismissDialog)
                    },
                    onSecondBtnClick = {
                        onAction(NewFixedDepositAccountAction.OnNextPress)
                    },
                )
            }
        },
    )
}
