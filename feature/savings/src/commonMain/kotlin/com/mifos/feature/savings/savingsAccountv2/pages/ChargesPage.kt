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
import androidx.compose.material3.MaterialTheme
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

@Composable
fun ChargesPage(
    state: SavingsAccountState,
    onAction: (SavingsAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = Modifier.fillMaxSize().padding(bottom = DesignToken.padding.large)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                stringResource(Res.string.step_charges),
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
                        onAction(SavingsAccountAction.ShowAddChargeDialog)
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
                        text = stringResource(Res.string.step_charges_add_new),
                        color = MaterialTheme.colorScheme.primary,
                        style = MifosTypography.labelLargeEmphasized,
                    )
                }
            }

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosRowWithTextAndButton(
                onBtnClick = {
                    onAction(SavingsAccountAction.ShowCharges)
                },
                btnText = stringResource(Res.string.step_charges_view),
                text = state.addedCharges.size.toString() + " " + stringResource(Res.string.step_charges_active) + " " + stringResource(Res.string.step_charges),
                btnEnabled = state.addedCharges.isNotEmpty(),
            )
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
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )
    }
}
