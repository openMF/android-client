/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createShareAccount.pages

import kpt.feature.client.generated.resources.Res
import kpt.feature.client.generated.resources.feature_share_account_back
import kpt.feature.client.generated.resources.feature_share_account_charge
import kpt.feature.client.generated.resources.feature_share_account_charge_active_charge
import kpt.feature.client.generated.resources.feature_share_account_charge_add_new
import kpt.feature.client.generated.resources.feature_share_account_charge_view
import kpt.feature.client.generated.resources.feature_share_account_next
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
import com.mifos.feature.client.createShareAccount.CreateShareAccountAction
import com.mifos.feature.client.createShareAccount.CreateShareAccountState
import org.jetbrains.compose.resources.stringResource
import kpt.core.base.designsystem.KptTheme
import kpt.core.base.designsystem.theme.LocalKptColors
import kpt.core.base.designsystem.theme.LocalKptSpacing

@Composable
fun ChargesPage(
    state: CreateShareAccountState,
    modifier: Modifier = Modifier,
    onAction: (CreateShareAccountAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(bottom = LocalKptSpacing.current.md)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                stringResource(Res.string.feature_share_account_charge),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(LocalKptSpacing.current.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Row(
                    modifier = Modifier.clickable {
                        onAction(
                            CreateShareAccountAction.ShowAddChargeDialog,
                        )
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = MifosIcons.Add,
                        contentDescription = null,
                        tint = LocalKptColors.current.primary,
                        modifier = Modifier.size(DesignToken.sizes.iconSmall),
                    )

                    Text(
                        text = stringResource(Res.string.feature_share_account_charge_add_new),
                        color = LocalKptColors.current.primary,
                        style = MifosTypography.labelLargeEmphasized,
                    )
                }
            }

            Spacer(Modifier.height(LocalKptSpacing.current.md))

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
            Spacer(Modifier.height(LocalKptSpacing.current.md))
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_share_account_back),
            secondBtnText = stringResource(Res.string.feature_share_account_next),
            onFirstBtnClick = {
                onAction(CreateShareAccountAction.PreviousStep)
            },
            onSecondBtnClick = {
                onAction(CreateShareAccountAction.NextStep)
            },
            modifier = Modifier.padding(top = LocalKptSpacing.current.sm),
        )
    }
}
