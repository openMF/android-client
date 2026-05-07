/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountv2.pages

import androidclient.feature.savings.generated.resources.Res
import androidclient.feature.savings.generated.resources.feature_savings_back
import androidclient.feature.savings.generated.resources.feature_savings_next
import androidclient.feature.savings.generated.resources.step_charges
import androidclient.feature.savings.generated.resources.step_charges_active
import androidclient.feature.savings.generated.resources.step_charges_add_new
import androidclient.feature.savings.generated.resources.step_charges_view
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.savings.savingsAccountv2.SavingsAccountAction
import com.mifos.feature.savings.savingsAccountv2.SavingsAccountState
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme

@Composable
fun ChargesPage(
    state: SavingsAccountState,
    modifier: Modifier = Modifier,
    onAction: (SavingsAccountAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = KptTheme.spacing.md),
    ) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                stringResource(Res.string.step_charges),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Row(
                    modifier = Modifier.clickable {
                        onAction(SavingsAccountAction.ShowAddChargeDialog)
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = MifosIcons.Add,
                        contentDescription = null,
                        tint = KptTheme.colorScheme.primary,
                        modifier = Modifier.size(DesignToken.sizes.iconSmall),
                    )

                    Text(
                        text = stringResource(Res.string.step_charges_add_new),
                        color = KptTheme.colorScheme.primary,
                        style = MifosTypography.labelLargeEmphasized,
                    )
                }
            }

            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosRowWithTextAndButton(
                onBtnClick = {
                    onAction(SavingsAccountAction.ShowCharges)
                },
                btnText = stringResource(Res.string.step_charges_view),
                text = state.addedCharges.size.toString() + " " + stringResource(Res.string.step_charges_active) + " " + stringResource(Res.string.step_charges),
                btnEnabled = state.addedCharges.isNotEmpty(),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_savings_back),
            secondBtnText = stringResource(Res.string.feature_savings_next),
            onFirstBtnClick = {
                onAction(SavingsAccountAction.PreviousStep)
            },
            onSecondBtnClick = {
                onAction(SavingsAccountAction.NextStep)
            },
        )
    }
}
