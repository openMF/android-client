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
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_charges_page
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_next
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_next_button
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_charges
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_settings
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_submitted_on
import androidclient.feature.recurringdeposit.generated.resources.recurring_step_charges_active
import androidclient.feature.recurringdeposit.generated.resources.recurring_step_charges_add_new
import androidclient.feature.recurringdeposit.generated.resources.recurring_step_charges_submit
import androidclient.feature.recurringdeposit.generated.resources.recurring_step_charges_view
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountAction
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChargesPage(
    state : RecurringAccountState,
    onAction : (RecurringAccountAction) -> Unit,

) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                stringResource(Res.string.feature_recurring_deposit_charges_page),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Row(
                    modifier = Modifier.clickable {
                        onAction(RecurringAccountAction.RecurringAccountChargesAction.ShowAddChargeDialog)
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = MifosIcons.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(DesignToken.sizes.iconSmall),
                    )

                    Text(
                        text = stringResource(Res.string.recurring_step_charges_add_new),
                        color = MaterialTheme.colorScheme.primary,
                        style = MifosTypography.labelLargeEmphasized,
                    )
                }
            }

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosRowWithTextAndButton(
                onBtnClick = {
                    onAction(RecurringAccountAction.RecurringAccountChargesAction.ShowCharge)
                },
                btnText = stringResource(Res.string.recurring_step_charges_view),
                text = state.addedCharges.size.toString() + " " + stringResource(Res.string.recurring_step_charges_active) + " " + stringResource(
                    Res.string.feature_recurring_deposit_step_charges),
                btnEnabled = state.addedCharges.isNotEmpty(),
            )
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_recurring_deposit_back),
            secondBtnText = stringResource(Res.string.recurring_step_charges_submit),
            onFirstBtnClick = {
                onAction(RecurringAccountAction.OnBackPress)
            },
            onSecondBtnClick = {
                onAction(RecurringAccountAction.Finish)
            },
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )


    }
}
