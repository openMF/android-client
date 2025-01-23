/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.theme.onPrimaryLight
import com.mifos.core.designsystem.theme.primaryDark
import com.mifos.core.designsystem.theme.primaryLight
import com.mifos.core.designsystem.theme.secondaryLight

@Composable
fun MifosOutlinedTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    icon: ImageVector? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    error: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        leadingIcon = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSystemInDarkTheme()) onPrimaryLight else secondaryLight,
                )
            }
        } else {
            null
        },
        trailingIcon = trailingIcon,
        maxLines = maxLines,
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isSystemInDarkTheme()) primaryDark else primaryLight,
        ),
        textStyle = LocalDensity.current.run {
            TextStyle(fontSize = 18.sp)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        visualTransformation = visualTransformation,
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
    )
}

@Composable
fun MifosOutlinedTextField(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    maxLines: Int = 1,
    isError: Boolean = false,
    errorText: String? = null,
    singleLine: Boolean = true,
    placeholder: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    message: String? = null,
    isPasswordVisible: Boolean = false,
    errorTextTag: String = label.plus("Error"),
    keyboardType: KeyboardType = KeyboardType.Text,
    textStyle: TextStyle = LocalTextStyle.current,
    isPasswordToggleDisplayed: Boolean = keyboardType == KeyboardType.Password,
    clearIcon: ImageVector = Icons.Default.Clear,
    showClearIcon: Boolean = false,
    onClickClearIcon: () -> Unit = {},
    onPasswordToggleClick: (Boolean) -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onValueChange: (String) -> Unit,
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .testTag(label)
            .fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingIcon = {
            leadingIcon?.let { Icon(imageVector = it, contentDescription = "leadingIcon") }
        },
        trailingIcon = @Composable {
            if (isPasswordToggleDisplayed) {
                PasswordToggleIcon(
                    isPasswordVisible = isPasswordVisible,
                    onPasswordToggleClick = onPasswordToggleClick,
                )
            } else if (showClearIcon && isFocused) {
                ClearIconButton(
                    showClearIcon = true,
                    clearIcon = clearIcon,
                    onClickClearIcon = onClickClearIcon,
                )
            } else {
                trailingIcon?.invoke()
            }
        },
        placeholder = {
            placeholder?.let { Text(it) }
        },
        prefix = prefix,
        suffix = suffix,
        supportingText = {
            if (errorText != null) {
                Text(
                    modifier = Modifier.testTag(errorTextTag),
                    text = errorText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            } else if (message != null) {
                Text(
                    modifier = Modifier.testTag(errorTextTag),
                    text = message,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        isError = isError,
        singleLine = singleLine,
        visualTransformation = if (!isPasswordVisible && isPasswordToggleDisplayed) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            keyboardType = keyboardType,
        ),
        maxLines = maxLines,
        interactionSource = interactionSource,
    )
}

@Composable
fun MifosOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
    maxLines: Int = 1,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    icon: ImageVector? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        readOnly = readOnly,
        enabled = enabled,
        leadingIcon = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSystemInDarkTheme()) onPrimaryLight else secondaryLight,
                )
            }
        } else {
            null
        },
        trailingIcon = trailingIcon,
        maxLines = maxLines,
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isSystemInDarkTheme()) primaryDark else primaryLight,
            focusedLabelColor = if (isSystemInDarkTheme()) primaryDark else primaryLight,
            cursorColor = if (isSystemInDarkTheme()) primaryDark else primaryLight,
        ),
        textStyle = LocalDensity.current.run {
            TextStyle(fontSize = 18.sp)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        isError = error != null,
        supportingText = if (error != null) {
            {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        } else {
            null
        },
    )
}

@Composable
private fun PasswordToggleIcon(
    isPasswordVisible: Boolean,
    modifier: Modifier = Modifier,
    onPasswordToggleClick: (Boolean) -> Unit,
) {
    IconButton(
        onClick = {
            onPasswordToggleClick(!isPasswordVisible)
        },
        modifier = modifier,
    ) {
        Icon(
            imageVector = if (isPasswordVisible) {
                Icons.Filled.VisibilityOff
            } else {
                Icons.Filled.Visibility
            },
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = if (isPasswordVisible) {
                "VisibilityOff"
            } else {
                "VisibilityOn"
            },
        )
    }
}

@Composable
private fun ClearIconButton(
    showClearIcon: Boolean,
    clearIcon: ImageVector,
    modifier: Modifier = Modifier,
    onClickClearIcon: () -> Unit,
) {
    AnimatedVisibility(
        visible = showClearIcon,
    ) {
        IconButton(
            onClick = onClickClearIcon,
            modifier = modifier,
        ) {
            Icon(
                imageVector = clearIcon,
                contentDescription = "trailingIcon",
            )
        }
    }
}

@Composable
fun MifosDatePickerTextField(
    value: String,
    modifier: Modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
    label: String? = null,
    labelString: String? = null,
    openDatePicker: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = { Text(text = labelString ?: label?.let { label } ?: "") },
        readOnly = true,
        modifier = modifier,
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isSystemInDarkTheme()) primaryDark else primaryLight,
        ),
        textStyle = LocalDensity.current.run {
            TextStyle(fontSize = 18.sp)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        trailingIcon = {
            IconButton(onClick = { openDatePicker() }) {
                Icon(imageVector = Icons.Default.CalendarToday, null)
            }
        },
    )
}
