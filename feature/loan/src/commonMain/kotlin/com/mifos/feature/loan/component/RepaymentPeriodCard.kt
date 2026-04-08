/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.component

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.due
import androidclient.feature.loan.generated.resources.installment
import androidclient.feature.loan.generated.resources.paid
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosCard
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.model.objects.account.loan.Period
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun RepaymentPeriodCard(
    period: Period,
    currencyCode: String?,
    maxDigits: Int?,
    modifier: Modifier = Modifier,
) {
    val isPaid = period.complete == true
    val dueDate = DateHelper.getDateAsString(period.dueDate!!)
    val amount = CurrencyFormatter.format(
        period.totalDueForPeriod ?: 0.0,
        currencyCode ?: "N/A",
        maxDigits ?: 0,
    )

    MifosCard(
        modifier = modifier
            .fillMaxWidth()
            .border(
                DesignToken.spacing.dp1,
                KptTheme.colorScheme.secondaryContainer,
                DesignToken.shapes.medium,
            ),
        shape = DesignToken.shapes.medium,
        elevation = KptTheme.elevation.level0,
        colors = CardDefaults.cardColors(
            KptTheme.colorScheme.surface,
        ),
        borderStroke = BorderStroke(DesignToken.spacing.dp1, KptTheme.colorScheme.secondaryContainer),
    ) {
        Row(
            modifier = Modifier
                .padding(KptTheme.spacing.md)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(DesignToken.sizes.iconExtraLarge)
                    .clip(CircleShape)
                    .background(KptTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = period.period?.toString() ?: "-",
                    color = KptTheme.colorScheme.onPrimary,
                    style = MifosTypography.labelMedium,
                )
            }

            Spacer(modifier = Modifier.width(DesignToken.spacing.medium))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(
                        Res.string.installment,
                        period.period?.let {
                            "$it${
                                if (it % 100 in 11..13) {
                                    "th"
                                } else {
                                    when (it) {
                                        1 -> "st"
                                        2 -> "nd"
                                        3 -> "rd"
                                        else -> "th"
                                    }
                                }
                            }"
                        } ?: "-",
                    ),
                    color = KptTheme.colorScheme.outline,
                    style = MifosTypography.labelMediumEmphasized,
                )

                Text(
                    text = dueDate,
                    style = MifosTypography.labelLargeEmphasized,
                    color = KptTheme.colorScheme.onSurface,
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.wrapContentWidth(),
            ) {
                Text(
                    text = if (isPaid) {
                        stringResource(Res.string.paid)
                    } else {
                        stringResource(Res.string.due)
                    },
                    style = MifosTypography.labelSmall.copy(
                        color = if (isPaid) AppColors.customEnable else KptTheme.colorScheme.error,
                    ),
                )
                Text(
                    text = amount,
                    style = MifosTypography.titleSmallEmphasized,
                    color = if (isPaid) AppColors.customEnable else KptTheme.colorScheme.error,
                )
            }
        }
    }
}
