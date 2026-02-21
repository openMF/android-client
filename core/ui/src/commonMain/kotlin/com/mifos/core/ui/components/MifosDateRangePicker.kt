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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * A reusable date range picker component that handles from/to date selection
 * with validation and consistent UI.
 *
 * @param fromDate The selected from date in milliseconds (null if not selected)
 * @param toDate The selected to date in milliseconds (null if not selected)
 * @param onFromDateSelected Callback when from date is selected
 * @param onToDateSelected Callback when to date is selected
 * @param fromDateLabel Label for the from date field
 * @param toDateLabel Label for the to date field
 * @param minSelectableDate Minimum selectable date (defaults to 2000-01-01)
 * @param invalidDateRangeMessage Error message for invalid date range
 * @param modifier Modifier for the component
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun MifosDateRangePicker(
    fromDate: Long?,
    toDate: Long?,
    onFromDateSelected: (Long) -> Unit,
    onToDateSelected: (Long) -> Unit,
    fromDateLabel: String = "From Date",
    toDateLabel: String = "To Date",
    minSelectableDate: LocalDate = LocalDate.parse("2000-01-01"),
    invalidDateRangeMessage: String = "To date must be after from date",
    modifier: Modifier = Modifier,
) {
    var showFromDatePicker by rememberSaveable { mutableStateOf(false) }
    var showToDatePicker by rememberSaveable { mutableStateOf(false) }

    val isInvalidDateRange = fromDate != null && toDate != null && toDate < fromDate

    val fromDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = createSelectableDatesFrom(minSelectableDate),
    )

    val toDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = createSelectableDatesFrom(minSelectableDate),
    )

    // Date picker dialogs
    MifosDatePickerDialog(
        show = showFromDatePicker,
        state = fromDatePickerState,
        onDismiss = { showFromDatePicker = false },
        onConfirm = { selectedMillis ->
            selectedMillis?.let { onFromDateSelected(it) }
        },
    )

    MifosDatePickerDialog(
        show = showToDatePicker,
        state = toDatePickerState,
        onDismiss = { showToDatePicker = false },
        onConfirm = { selectedMillis ->
            selectedMillis?.let { onToDateSelected(it) }
        },
    )

    Column(modifier = modifier) {
        MifosDatePickerTextField(
            value = formatDateFromMillis(fromDate),
            label = fromDateLabel,
            openDatePicker = {
                initializeDatePicker(fromDate, fromDatePickerState)
                showFromDatePicker = true
            },
        )

        Spacer(modifier = Modifier.height(8.dp))

        MifosDatePickerTextField(
            value = formatDateFromMillis(toDate),
            label = toDateLabel,
            errorMessage = if (isInvalidDateRange) invalidDateRangeMessage else null,
            openDatePicker = {
                initializeDatePicker(toDate, toDatePickerState)
                showToDatePicker = true
            },
        )
    }
}
