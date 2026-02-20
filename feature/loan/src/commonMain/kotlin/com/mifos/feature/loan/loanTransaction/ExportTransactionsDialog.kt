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
import androidclient.feature.loan.generated.resources.feature_loan_close
import androidclient.feature.loan.generated.resources.feature_loan_export_transactions
import androidclient.feature.loan.generated.resources.feature_loan_from_date
import androidclient.feature.loan.generated.resources.feature_loan_generate_report
import androidclient.feature.loan.generated.resources.feature_loan_invalid_date_range
import androidclient.feature.loan.generated.resources.feature_loan_select
import androidclient.feature.loan.generated.resources.feature_loan_to_date
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mifos.core.common.utils.DateHelper.format
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosCustomDialog
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.icon.MifosIcons
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
    val isInvalidDateRange = fromDate != null && toDate != null && toDate!! < fromDate!!
    val isValidDateRange = fromDate != null && toDate != null && toDate!! >= fromDate!!

    val fromDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = createSelectableDatesFrom(LocalDate.parse("2000-01-01")),
    )

    val toDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = createSelectableDatesFrom(LocalDate.parse("2000-01-01")),
    )

    MifosDatePickerDialog(
        show = showFromDatePicker,
        state = fromDatePickerState,
        onDismiss = { showFromDatePicker = false },
        onConfirm = { selectedMillis ->
            selectedMillis?.let { fromDate = it }
        },
    )

    MifosDatePickerDialog(
        show = showToDatePicker,
        state = toDatePickerState,
        onDismiss = { showToDatePicker = false },
        onConfirm = { selectedMillis ->
            selectedMillis?.let { toDate = it }
        },
    )

    MifosCustomDialog(
        onDismiss = onDismiss,
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.95f),
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_loan_export_transactions),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = MifosIcons.Cancel,
                                contentDescription = stringResource(Res.string.feature_loan_close),
                                tint = MaterialTheme.colorScheme.outline,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    MifosDatePickerTextField(
                        value = formatDateFromMillis(fromDate),
                        label = stringResource(Res.string.feature_loan_from_date),
                        openDatePicker = {
                            initializeDatePicker(fromDate, fromDatePickerState)
                            showFromDatePicker = true
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MifosDatePickerTextField(
                        value = formatDateFromMillis(toDate),
                        label = stringResource(Res.string.feature_loan_to_date),
                        errorMessage = if (isInvalidDateRange) {
                            stringResource(Res.string.feature_loan_invalid_date_range)
                        } else {
                            null
                        },
                        openDatePicker = {
                            initializeDatePicker(toDate, toDatePickerState)
                            showToDatePicker = true
                        },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        MifosButton(
                            onClick = {
                                onGenerateReport(fromDate!!, toDate!!)
                            },
                            enabled = isValidDateRange,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = stringResource(Res.string.feature_loan_generate_report),
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun formatDateFromMillis(millis: Long?): String {
    if (millis == null) return ""
    val localDate = Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(TimeZone.UTC)
        .date
    return localDate.format("dd-MM-yyyy")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
private fun initializeDatePicker(
    currentDate: Long?,
    datePickerState: DatePickerState,
) {
    datePickerState.selectedDateMillis = currentDate
        ?: Clock.System.now().toEpochMilliseconds()
}

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
private fun createSelectableDatesFrom(
    minDate: LocalDate,
    maxDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
) = object : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val selectedDate = Instant.fromEpochMilliseconds(utcTimeMillis)
            .toLocalDateTime(TimeZone.UTC)
            .date
        return selectedDate in minDate..maxDate
    }

    override fun isSelectableYear(year: Int): Boolean = year in minDate.year..maxDate.year
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MifosDatePickerDialog(
    show: Boolean,
    state: DatePickerState,
    onDismiss: () -> Unit,
    onConfirm: (Long?) -> Unit,
) {
    if (show) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(state.selectedDateMillis)
                        onDismiss()
                    },
                ) {
                    Text(stringResource(Res.string.feature_loan_select))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.feature_loan_cancel))
                }
            },
        ) {
            DatePicker(state = state)
        }
    }
}
