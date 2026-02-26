/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.mifos.core.common.utils.DateHelper.format
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * A reusable date picker dialog component that wraps Material3's DatePickerDialog
 * with consistent styling and behavior across the app.
 *
 * @param show Whether to show the dialog
 * @param state The DatePickerState to manage the selected date
 * @param onDismiss Callback when the dialog is dismissed
 * @param onConfirm Callback when a date is confirmed, receives the selected date in milliseconds
 * @param confirmText Text for the confirm button
 * @param dismissText Text for the dismiss button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosDatePickerDialog(
    show: Boolean,
    state: DatePickerState,
    onDismiss: () -> Unit,
    onConfirm: (Long?) -> Unit,
    confirmText: String,
    dismissText: String,
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
                    Text(confirmText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            },
        ) {
            DatePicker(state = state)
        }
    }
}

/**
 * Creates a SelectableDates object that restricts date selection to a specific range.
 *
 * @param minDate The minimum selectable date
 * @param maxDate The maximum selectable date (defaults to current date)
 * @return SelectableDates object for use with DatePickerState
 */
@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
fun createSelectableDatesRange(
    minDate: LocalDate,
    maxDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
) = object : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val selectedDate = Instant.fromEpochMilliseconds(utcTimeMillis)
            .toLocalDateTime(TimeZone.UTC)
            .date
        return selectedDate in minDate..maxDate
    }

    override fun isSelectableYear(year: Int): Boolean = year in minDate.year..maxDate.year
}

/**
 * Creates a SelectableDates object that only allows dates from a minimum date onwards.
 *
 * @param minDate The minimum selectable date
 * @return SelectableDates object for use with DatePickerState
 */
@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
fun createSelectableDatesFrom(
    minDate: LocalDate,
) = createSelectableDatesRange(minDate)

/**
 * Initializes a DatePickerState with a specific date or current date as fallback.
 *
 * @param currentDate The date to initialize with (in milliseconds), null for current date
 * @param datePickerState The DatePickerState to initialize
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
fun initializeDatePicker(
    currentDate: Long?,
    datePickerState: DatePickerState,
) {
    datePickerState.selectedDateMillis = currentDate
        ?: Clock.System.now().toEpochMilliseconds()
}

/**
 * Formats a date from milliseconds to a readable string format.
 *
 * @param millis The date in milliseconds, null returns empty string
 * @param format The date format pattern (default: "dd-MM-yyyy")
 * @return Formatted date string
 */
@OptIn(ExperimentalTime::class)
fun formatDateFromMillis(
    millis: Long?,
    format: String = "dd-MM-yyyy",
): String {
    if (millis == null) return ""
    val localDate = Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(TimeZone.UTC)
        .date
    return localDate.format(format)
}
