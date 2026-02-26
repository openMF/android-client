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

import androidclient.core.ui.generated.resources.Res
import androidclient.core.ui.generated.resources.core_ui_date_picker_cancel
import androidclient.core.ui.generated.resources.core_ui_date_picker_select
import androidclient.core.ui.generated.resources.core_ui_from_date
import androidclient.core.ui.generated.resources.core_ui_invalid_date_range
import androidclient.core.ui.generated.resources.core_ui_to_date
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
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * A reusable date range picker component that handles from/to date selection
 * with validation and consistent UI.
 *
 * The "To Date" picker is automatically constrained to only allow dates on or after
 * the selected "From Date", preventing invalid ranges from being selected in the first place.
 *
 * @param fromDate The selected from date in milliseconds (null if not selected)
 * @param toDate The selected to date in milliseconds (null if not selected)
 * @param onFromDateSelected Callback when from date is selected
 * @param onToDateSelected Callback when to date is selected (only called with valid dates)
 * @param fromDateLabel Label for the from date field
 * @param toDateLabel Label for the to date field
 * @param minSelectableDate Minimum selectable date
 * @param invalidDateRangeMessage Error message shown when to date is before from date
 * @param modifier Modifier for the component
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun MifosDateRangePicker(
    fromDate: Long?,
    toDate: Long?,
    onFromDateSelected: (Long) -> Unit,
    onToDateSelected: (Long) -> Unit,
    fromDateLabel: String = stringResource(Res.string.core_ui_from_date),
    toDateLabel: String = stringResource(Res.string.core_ui_to_date),
    minSelectableDate: LocalDate = LocalDate.parse("2000-01-01"),
    invalidDateRangeMessage: String = stringResource(Res.string.core_ui_invalid_date_range),
    modifier: Modifier = Modifier,
) {
    var showFromDatePicker by rememberSaveable { mutableStateOf(false) }
    var showToDatePicker by rememberSaveable { mutableStateOf(false) }

    val confirmText = stringResource(Res.string.core_ui_date_picker_select)
    val dismissText = stringResource(Res.string.core_ui_date_picker_cancel)

    val isInvalidDateRange = fromDate != null && toDate != null && toDate < fromDate

    // The minimum selectable date for the "To Date" picker is max(minSelectableDate, fromDate).
    // This prevents the user from selecting a To Date that is before the From Date at pick time.
    val toDateMinSelectableDate = if (fromDate != null) {
        val fromLocalDate = Instant.fromEpochMilliseconds(fromDate)
            .toLocalDateTime(TimeZone.UTC)
            .date
        if (fromLocalDate > minSelectableDate) fromLocalDate else minSelectableDate
    } else {
        minSelectableDate
    }

    val fromDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = createSelectableDatesFrom(minSelectableDate),
    )

    val toDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = createSelectableDatesFrom(toDateMinSelectableDate),
    )

    // Date picker dialogs
    MifosDatePickerDialog(
        show = showFromDatePicker,
        state = fromDatePickerState,
        onDismiss = { showFromDatePicker = false },
        onConfirm = { selectedMillis ->
            selectedMillis?.let { onFromDateSelected(it) }
        },
        confirmText = confirmText,
        dismissText = dismissText,
    )

    MifosDatePickerDialog(
        show = showToDatePicker,
        state = toDatePickerState,
        onDismiss = { showToDatePicker = false },
        onConfirm = { selectedMillis ->
            // Only propagate the selection if it represents a valid range.
            // The selectable dates already enforce this at the picker level, but
            // this guard ensures correctness if fromDate changes after toDate is set.
            if (selectedMillis != null && (fromDate == null || selectedMillis >= fromDate)) {
                onToDateSelected(selectedMillis)
            }
        },
        confirmText = confirmText,
        dismissText = dismissText,
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

        Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

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
