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
import androidclient.feature.loan.generated.resources.back
import androidclient.feature.loan.generated.resources.next
import androidclient.feature.loan.generated.resources.repayment_schedule
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.model.objects.account.loan.Period
import com.mifos.core.ui.components.MifosDefaultListingComponentFromStringResources
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.loan.component.RepaymentPeriodCard
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountAction
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountState
import org.jetbrains.compose.resources.stringResource

@Composable
fun SchedulePage(
    state: NewLoanAccountState,
    onAction: (NewLoanAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        Modifier.fillMaxSize().padding(bottom = DesignToken.padding.large),
    ) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.repayment_schedule),
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            if (!state.repaymentSchedules.isEmpty()) {
                MifosDefaultListingComponentFromStringResources(
                    data = state.repaymentSchedules,
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                    borderColor = MaterialTheme.colorScheme.surfaceDim,
                    verticalArrangement = Arrangement.spacedBy(DesignToken.padding.small),
                )
            }

            Spacer(Modifier.height(DesignToken.padding.large))

            RepaymentScheduleList(
                periods = state.loanWithAssociationsEntity.repaymentSchedule.periods.orEmpty()
                    .filter { it.period != null },
                currencyCode = state.loanWithAssociationsEntity.currency.code,
                maxDigits = state.loanWithAssociationsEntity.currency.decimalPlaces,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
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
        )
    }
}

@Composable
fun RepaymentScheduleList(
    periods: List<Period>,
    currencyCode: String?,
    maxDigits: Int?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.medium),
    ) {
        periods.forEach { period ->
            RepaymentPeriodCard(
                period = period,
                currencyCode = currencyCode,
                maxDigits = maxDigits,
            )
        }
    }
}
