/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import kpt.core.base.designsystem.KptTheme
import kpt.core.base.designsystem.theme.LocalKptColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosSearchableDropdown(
    value: String,
    onValueChanged: (String) -> Unit,
    onOptionSelected: (Int, String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier
        .clip(DesignToken.shapes.medium)
        .fillMaxWidth(),
    label: String? = null,
    enabled: Boolean = true,
    errorMessage: String? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled && options.isNotEmpty(),
        onExpandedChange = {
            if (enabled) {
                expanded = !expanded
            }
        },
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChanged(it)
                expanded = true
            },
            enabled = enabled,
            readOnly = false,
            isError = errorMessage != null,
            supportingText = {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = LocalKptColors.current.error,
                    )
                }
            },
            label = {
                label?.let {
                    Text(text = it)
                }
            },
            modifier = modifier.menuAnchor(
                type = ExposedDropdownMenuAnchorType.PrimaryEditable,
                enabled = enabled,
            ),
            shape = DesignToken.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LocalKptColors.current.secondaryContainer,
                unfocusedBorderColor = LocalKptColors.current.secondaryContainer,
            ),
            maxLines = 1,
            textStyle = MifosTypography.bodyLarge,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
            ),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
        )

        ExposedDropdownMenu(
            expanded = expanded && options.isNotEmpty(),
            onDismissRequest = {
                expanded = false
            },
        ) {
            options.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        Text(text = item)
                    },
                    onClick = {
                        expanded = false
                        onValueChanged(item)
                        onOptionSelected(index, item)
                    },
                )
            }
        }
    }
}
