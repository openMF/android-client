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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties

@Composable
fun MifosTextFieldDropdown(
    value: String,
    onValueChanged: (String) -> Unit,
    onOptionSelected: (Int, String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier,
    label: Int? = null,
    labelString: String? = null,
    readOnly: Boolean = false,
) {
    var isExpended by remember { mutableStateOf(false) }
    val height = (LocalConfiguration.current.screenHeightDp / 2).dp
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val width = with(LocalDensity.current) { textFieldSize.width.toDp() }

    ExposedDropdownMenuBox(
        expanded = isExpended,
        onExpandedChange = { isExpended = it },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChanged(it) },
            label = { Text(text = labelString ?: label?.let { stringResource(id = label) } ?: "") },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This is used to assign to the DropDown the same width
                    textFieldSize = coordinates.size.toSize()
                }
                .menuAnchor(),
            maxLines = 1,
            textStyle = LocalDensity.current.run {
                TextStyle(fontSize = 18.sp)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(isExpended)
            },
            readOnly = readOnly,
        )

        DropdownMenu(
            expanded = isExpended,
            onDismissRequest = { isExpended = false },
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                excludeFromSystemGesture = true,
                clippingEnabled = true,
            ),
            modifier = Modifier
                .width(width = width)
                .heightIn(max = height),
        ) {
            options.forEachIndexed { index, value ->
                DropdownMenuItem(
                    text = { Text(text = value) },
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        isExpended = false
                        onOptionSelected(index, value)
                    },
                )
            }
        }
    }
}
