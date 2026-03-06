/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanTransaction

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_export_transactions
import androidclient.feature.loan.generated.resources.feature_loan_from_date
import androidclient.feature.loan.generated.resources.feature_loan_generate_report
import androidclient.feature.loan.generated.resources.feature_loan_invalid_date_range
import androidclient.feature.loan.generated.resources.feature_loan_to_date
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosCustomDialog
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.ui.components.MifosDateRangePicker
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
internal fun ExportTransactionsDialog(
    onDismiss: () -> Unit,
    onGenerateReport: (fromDate: Long, toDate: Long) -> Unit,
) {
    var fromDate: Long? by rememberSaveable { mutableStateOf(null) }
    var toDate: Long? by rememberSaveable { mutableStateOf(null) }
    val isValidDateRange = fromDate != null && toDate != null && toDate!! >= fromDate!!

    MifosCustomDialog(
        onDismiss = onDismiss,
    ) {
        Surface(
            shape = KptTheme.shapes.medium,
            color = KptTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.95f),
        ) {
            Column(modifier = Modifier.padding(KptTheme.spacing.lg)) {
                Text(
                    text = stringResource(Res.string.feature_loan_export_transactions),
                    style = KptTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                MifosDateRangePicker(
                    fromDate = fromDate,
                    toDate = toDate,
                    onFromDateSelected = { fromDate = it },
                    onToDateSelected = { toDate = it },
                    fromDateLabel = stringResource(Res.string.feature_loan_from_date),
                    toDateLabel = stringResource(Res.string.feature_loan_to_date),
                    minSelectableDate = LocalDate.parse("2000-01-01"),
                    invalidDateRangeMessage = stringResource(Res.string.feature_loan_invalid_date_range),
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MifosOutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_loan_cancel),
                            style = KptTheme.typography.labelLarge,
                            maxLines = 1,
                        )
                    }

                    Spacer(modifier = Modifier.width(KptTheme.spacing.md))

                    MifosButton(
                        onClick = {
                            val from = fromDate ?: return@MifosButton
                            val to = toDate ?: return@MifosButton
                            onGenerateReport(from, to)
                        },
                        enabled = isValidDateRange,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_loan_generate_report),
                            style = KptTheme.typography.labelLarge,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
