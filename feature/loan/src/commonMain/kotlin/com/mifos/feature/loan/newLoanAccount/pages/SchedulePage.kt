/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.newLoanAccount.pages

import kpt.feature.loan.generated.resources.Res
import kpt.feature.loan.generated.resources.back
import kpt.feature.loan.generated.resources.feature_loan_loan_repayment_schedule
import kpt.feature.loan.generated.resources.next
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
import com.mifos.core.model.objects.account.loan.Period
import com.mifos.core.ui.components.MifosDefaultListingComponentFromStringResources
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.loan.component.RepaymentPeriodCard
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountAction
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountState
import org.jetbrains.compose.resources.stringResource
import kpt.core.base.designsystem.KptTheme
import kpt.core.base.designsystem.theme.LocalKptColors
import kpt.core.base.designsystem.theme.LocalKptSpacing

@Composable
fun SchedulePage(
    state: NewLoanAccountState,
    modifier: Modifier = Modifier,
    onAction: (NewLoanAccountAction) -> Unit,
) {
    Column(
        Modifier.fillMaxSize().padding(bottom = LocalKptSpacing.current.md),
    ) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.feature_loan_loan_repayment_schedule),
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(LocalKptSpacing.current.md))

            if (!state.repaymentSchedulesSummary.isEmpty()) {
                MifosDefaultListingComponentFromStringResources(
                    data = state.repaymentSchedulesSummary,
                    backgroundColor = LocalKptColors.current.surfaceContainer,
                    borderColor = LocalKptColors.current.surfaceDim,
                    verticalArrangement = Arrangement.spacedBy(LocalKptSpacing.current.sm),
                )
            }

            Spacer(Modifier.height(LocalKptSpacing.current.md))

            RepaymentScheduleList(
                periods = state.repaymentSchedule.periods.orEmpty()
                    .filter { it.period != null },
                currencyCode = state.repaymentSchedule.currency?.code,
                maxDigits = state.repaymentSchedule.currency?.decimalPlaces,
            )
            Spacer(Modifier.height(LocalKptSpacing.current.md))
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
