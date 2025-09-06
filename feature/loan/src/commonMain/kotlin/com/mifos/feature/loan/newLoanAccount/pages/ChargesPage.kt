/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.newLoanAccount.pages

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.add_new
import androidclient.feature.loan.generated.resources.back
import androidclient.feature.loan.generated.resources.charges
import androidclient.feature.loan.generated.resources.next
import androidclient.feature.loan.generated.resources.view
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
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountAction
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChargesPage(
    state: NewLoanAccountState,
    onAction: (NewLoanAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.charges),
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
                        onAction(NewLoanAccountAction.ShowAddChargeDialog)
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
                        text = stringResource(Res.string.add_new),
                        color = MaterialTheme.colorScheme.primary,
                        style = MifosTypography.labelLargeEmphasized,
                    )
                }
            }

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosRowWithTextAndButton(
                onBtnClick = {
                    onAction(NewLoanAccountAction.ShowCharges)
                },
                btnText = stringResource(Res.string.view),
                text = state.addedCharges.size.toString() + " " + stringResource(Res.string.charges),
                btnEnabled = state.addedCharges.isNotEmpty(),
            )

            if (state.loanTemplate?.overdueCharges?.isNotEmpty() ?: false) {
                Spacer(Modifier.height(DesignToken.padding.large))

                MifosRowWithTextAndButton(
                    onBtnClick = {
                        onAction(NewLoanAccountAction.ShowOverDueCharges)
                    },
                    btnText = stringResource(Res.string.view),
                    text = state.loanTemplate.overdueCharges.size.toString() + " " + stringResource(Res.string.charges),
                    btnEnabled = state.addedCharges.isNotEmpty(),
                )
            }
        }

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.back),
            secondBtnText = stringResource(Res.string.next),
            onFirstBtnClick = {
                onAction(NewLoanAccountAction.PreviousStep)
            },
            onSecondBtnClick = {
                onAction(NewLoanAccountAction.NextStep)
            },
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )
    }
}
