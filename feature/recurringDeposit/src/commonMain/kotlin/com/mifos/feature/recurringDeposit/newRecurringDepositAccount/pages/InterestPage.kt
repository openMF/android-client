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
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_back
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_description
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_empty_date
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_end_date
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_grouping_by_amount
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_interest_rate_chart
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_name
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_next
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_no
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_no_interest_chart
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_rate_chart
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_interest
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_valid_from_date
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_view
import kpt.feature.recurringdeposit.generated.resources.feature_recurring_deposit_yes
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountAction
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountState
import org.jetbrains.compose.resources.stringResource
import kpt.core.base.designsystem.KptTheme
import kpt.core.base.designsystem.theme.LocalKptSpacing

@Composable
fun InterestPage(
    state: RecurringAccountState,
    modifier: Modifier = Modifier,
    onAction: (RecurringAccountAction) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(bottom = LocalKptSpacing.current.md)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.feature_recurring_deposit_step_interest),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(LocalKptSpacing.current.md))

            MifosDefaultListingComponentFromStringResources(
                data = mapOf(
                    Res.string.feature_recurring_deposit_name to state.template.accountChart?.name.orEmpty(),
                    Res.string.feature_recurring_deposit_valid_from_date to (
                        state.template.accountChart?.fromDate
                            ?.let { DateHelper.getDateAsString(it) }
                            ?: stringResource(Res.string.feature_recurring_deposit_empty_date)
                        ),
                    Res.string.feature_recurring_deposit_end_date to (
                        state.template.accountChart?.endDate
                            ?.let { DateHelper.getDateAsString(it) }
                            ?: stringResource(Res.string.feature_recurring_deposit_empty_date)
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

            Spacer(Modifier.height(LocalKptSpacing.current.md))

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
            Spacer(Modifier.height(LocalKptSpacing.current.md))
        }

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_recurring_deposit_back),
            secondBtnText = stringResource(Res.string.feature_recurring_deposit_next),
            onFirstBtnClick = { onAction(RecurringAccountAction.OnBackPress) },
            onSecondBtnClick = { onAction(RecurringAccountAction.OnNextPress) },
        )
    }
}

@Composable
fun RateChart(
    state: RecurringAccountState,
    onAction: (RecurringAccountAction) -> Unit,
) {
    MifosBottomSheet(
        onDismiss = {
            onAction(RecurringAccountAction.OnDismissDialog)
        },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(LocalKptSpacing.current.md),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
            ) {
                Text(
                    text = stringResource(Res.string.feature_recurring_deposit_rate_chart),
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
                    firstBtnText = stringResource(Res.string.feature_recurring_deposit_back),
                    secondBtnText = stringResource(Res.string.feature_recurring_deposit_next),
                    onFirstBtnClick = {
                        onAction(RecurringAccountAction.OnDismissDialog)
                    },
                    onSecondBtnClick = {
                        onAction(RecurringAccountAction.OnNextPress)
                    },
                )
            }
        },
    )
}
