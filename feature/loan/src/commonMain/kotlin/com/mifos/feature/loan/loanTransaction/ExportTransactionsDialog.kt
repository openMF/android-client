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
import androidclient.feature.loan.generated.resources.feature_loan_select
import androidclient.feature.loan.generated.resources.feature_loan_to_date
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosCustomDialog
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedButton
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
internal fun ExportTransactionsDialog(
    onDismiss: () -> Unit,
    onGenerateReport: (fromDate: Long, toDate: Long) -> Unit,
) {
    var showFromDatePicker by rememberSaveable { mutableStateOf(false) }
    var showToDatePicker by rememberSaveable { mutableStateOf(false) }
    var fromDate: Long? by rememberSaveable { mutableStateOf(null) }
    var toDate: Long? by rememberSaveable { mutableStateOf(null) }

    val currentFromDate = fromDate
    val currentToDate = toDate
    val isValidDateRange = currentFromDate != null && currentToDate != null && currentToDate >= currentFromDate
    val isInvalidDateRange = currentFromDate != null && currentToDate != null && currentToDate < currentFromDate

    val fromDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    val toDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    if (showFromDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showFromDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFromDatePicker = false
                        fromDatePickerState.selectedDateMillis?.let {
                            fromDate = it
                        }
                    },
                ) { Text(stringResource(Res.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showFromDatePicker = false },
                ) { Text(stringResource(Res.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = fromDatePickerState)
        }
    }

    if (showToDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showToDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showToDatePicker = false
                        toDatePickerState.selectedDateMillis?.let {
                            toDate = it
                        }
                    },
                ) { Text(stringResource(Res.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showToDatePicker = false },
                ) { Text(stringResource(Res.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = toDatePickerState)
        }
    }

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

                MifosDatePickerTextField(
                    value = fromDate?.let { DateHelper.getDateAsStringFromLong(it) }.orEmpty(),
                    label = stringResource(Res.string.feature_loan_from_date),
                    openDatePicker = {
                        fromDatePickerState.selectedDateMillis =
                            fromDate ?: Clock.System.now().toEpochMilliseconds()
                        showFromDatePicker = true
                    },
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

                MifosDatePickerTextField(
                    value = toDate?.let { DateHelper.getDateAsStringFromLong(it) }.orEmpty(),
                    label = stringResource(Res.string.feature_loan_to_date),
                    errorMessage = if (isInvalidDateRange) {
                        stringResource(Res.string.feature_loan_invalid_date_range)
                    } else {
                        null
                    },
                    openDatePicker = {
                        toDatePickerState.selectedDateMillis =
                            toDate ?: Clock.System.now().toEpochMilliseconds()
                        showToDatePicker = true
                    },
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
