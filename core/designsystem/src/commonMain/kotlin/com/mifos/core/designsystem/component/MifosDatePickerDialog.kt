/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.designsystem.component

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosDatePickerDialog(
    datePickerState: DatePickerState,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            MifosTextButton(
                onClick = onConfirm,
                text = { Text(confirmText) },
            )
        },
        dismissButton = {
            MifosOutlinedButton(
                onClick = onDismissRequest,
                text = { Text(dismissText) },
            )
        },
    ) {
        DatePicker(state = datePickerState)
    }
}
