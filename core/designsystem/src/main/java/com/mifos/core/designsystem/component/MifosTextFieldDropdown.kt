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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark

@Composable
fun MifosTextFieldDropdown(
    value: String,
    onValueChanged: (String) -> Unit,
    onOptionSelected: (Int, String) -> Unit,
    label: Int,
    options: List<String>,
    readOnly: Boolean = false
) {
    var isExpended by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpended,
        onExpandedChange = { isExpended = it }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChanged(it) },
            label = { Text(text = stringResource(id = label)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
                .menuAnchor(),
            maxLines = 1,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
            ),
            textStyle = LocalDensity.current.run {
                TextStyle(fontSize = 18.sp)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(isExpended)
            },
            readOnly = readOnly
        )

        ExposedDropdownMenu(
            expanded = isExpended,
            onDismissRequest = { isExpended = false }
        ) {
            options.forEachIndexed { index, value ->
                DropdownMenuItem(
                    text = { Text(text = value) },
                    onClick = {
                        isExpended = false
                        onOptionSelected(index, value)
                    }
                )
            }
        }
    }
}