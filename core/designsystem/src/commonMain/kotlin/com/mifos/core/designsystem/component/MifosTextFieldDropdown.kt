/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.core.designsystem.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.theme.primaryDark
import com.mifos.core.designsystem.theme.primaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosTextFieldDropdown(
    value: String,
    onValueChanged: (String) -> Unit,
    onOptionSelected: (Int, String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp),
    label: Int? = null,
    labelString: String? = null,
    readOnly: Boolean = false,
) {
    var isExpended by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpended,
        onExpandedChange = { isExpended = it },
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChanged(it) },
            label = {
                if (labelString != null) {
                    Text(text = labelString)
                }
            },
            modifier = modifier.menuAnchor(),
            maxLines = 1,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isSystemInDarkTheme()) primaryLight else primaryDark,
                focusedLabelColor = if (isSystemInDarkTheme()) primaryDark else primaryLight,
            ),
            textStyle = LocalDensity.current.run {
                TextStyle(fontSize = 18.sp)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(isExpended)
            },
            readOnly = readOnly,
        )

        ExposedDropdownMenu(
            expanded = isExpended,
            onDismissRequest = { isExpended = false },
        ) {
            options.forEachIndexed { index, value ->
                DropdownMenuItem(
                    text = { Text(text = value) },
                    onClick = {
                        isExpended = false
                        onOptionSelected(index, value)
                    },
                )
            }
        }
    }
}
