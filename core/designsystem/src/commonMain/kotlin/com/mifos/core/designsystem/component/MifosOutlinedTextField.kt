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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MifosOutlinedTextField(
    value: TextFieldValue,
    onValueChanged: (TextFieldValue) -> Unit,
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
        onValueChange = onValueChanged,
        label = {
            Text(
                text = label,
                style = MifosTypography.bodySmall,
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        leadingIcon = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                )
            }
        } else {
            null
        },
        trailingIcon = trailingIcon,
        maxLines = maxLines,
        singleLine = singleLine,
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
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    )
}

@Composable
fun MifosOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
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
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isPasswordToggleDisplayed: Boolean = keyboardType == KeyboardType.Password,
    clearIcon: ImageVector = Icons.Default.Clear,
    showClearIcon: Boolean = false,
    onClickClearIcon: () -> Unit = {},
    onPasswordToggleClick: (Boolean) -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
        unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
    ),
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
                style = MaterialTheme.typography.bodySmall,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingIcon = if (leadingIcon != null) {
            {
                Icon(imageVector = leadingIcon, contentDescription = "leadingIcon")
            }
        } else {
            null
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
        shape = shape,
        colors = colors,
    )
}

@Composable
fun MifosOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier.fillMaxWidth().clip(DesignToken.shapes.medium),
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
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
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
                )
            }
        } else {
            null
        },
        shape = DesignToken.shapes.medium,
        trailingIcon = trailingIcon,
        maxLines = maxLines,
        singleLine = singleLine,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            lineHeight = 24.sp,
            letterSpacing = 0.5f.sp,
            fontWeight = FontWeight.Normal,
        ),
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
    onClickClearIcon: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = showClearIcon,
        modifier = modifier,
    ) {
        IconButton(
            onClick = onClickClearIcon,
            modifier = Modifier.semantics {
                contentDescription = "clearIcon"
            },
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
    enabled: Boolean = true,
    value: String,
    modifier: Modifier = Modifier
        .fillMaxSize()
        .clip(DesignToken.shapes.medium),
    label: String? = null,
    openDatePicker: () -> Unit,
) {
    OutlinedTextField(
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        value = value,
        onValueChange = { },
        label = { Text(text = label?.let { label } ?: "") },
        readOnly = true,
        modifier = modifier,
        shape = DesignToken.shapes.medium,
        maxLines = 1,
        textStyle = LocalDensity.current.run {
            TextStyle(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                lineHeight = 24.sp,
                letterSpacing = 0.5f.sp,
                fontWeight = FontWeight.Normal,
            )
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        trailingIcon = {
            IconButton(onClick = { openDatePicker() }) {
                Icon(imageVector = Icons.Default.CalendarMonth, null)
            }
        },
    )
}

@Composable
fun MifosOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
        unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
        errorBorderColor = MaterialTheme.colorScheme.error,
    ),
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    config: MifosTextFieldConfig = MifosTextFieldConfig(),
    onClickClearIcon: () -> Unit = { onValueChange("") },
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val showIcon by rememberUpdatedState(value.isNotEmpty())

    OutlinedTextField(
        shape = shape,
        colors = colors,
        value = value,
        label = { Text(text = label) },
        onValueChange = onValueChange,
        textStyle = textStyle,
        modifier = modifier.fillMaxWidth(),
        enabled = config.enabled,
        readOnly = config.readOnly,
        visualTransformation = config.visualTransformation,
        keyboardOptions = config.keyboardOptions,
        keyboardActions = config.keyboardActions,
        interactionSource = interactionSource,
        singleLine = config.singleLine,
        maxLines = config.maxLines,
        minLines = config.minLines,
        leadingIcon = config.leadingIcon,
        isError = config.isError,
        trailingIcon = @Composable {
            AnimatedContent(
                targetState = config.showClearIcon && isFocused && showIcon,
            ) {
                if (it) {
                    ClearIconButton(
                        showClearIcon = true,
                        clearIcon = config.clearIcon,
                        onClickClearIcon = onClickClearIcon,
                    )
                } else {
                    config.trailingIcon?.invoke()
                }
            }
        },
        supportingText = config.errorText?.let {
            {
                Text(
                    modifier = Modifier.testTag("errorTag"),
                    text = it,
                    style = MifosTypography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
    )
}

data class MifosTextFieldConfig(
    val enabled: Boolean = true,
    val showClearIcon: Boolean = true,
    val readOnly: Boolean = false,
    val clearIcon: ImageVector = MifosIcons.Close,
    val isError: Boolean = false,
    val errorText: String? = null,
    val visualTransformation: VisualTransformation = VisualTransformation.None,
    val keyboardActions: KeyboardActions = KeyboardActions.Default,
    val singleLine: Boolean = true,
    val maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    val minLines: Int = 1,
    val keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    val trailingIcon: @Composable (() -> Unit)? = null,
    val leadingIcon: @Composable (() -> Unit)? = null,
)

@Preview
@Composable
private fun MifosOutlinedTextField_ValuePreview() {
    var text by remember { mutableStateOf(TextFieldValue("Hello")) }
    MifosTheme {
        MifosOutlinedTextField(
            value = text,
            onValueChanged = { text = it },
            label = "Username",
            icon = Icons.Default.Person,
        )
    }
}

@Preview
@Composable
private fun MifosOutlinedTextField_PasswordPreview() {
    var text by remember { mutableStateOf("secret123") }
    var passwordVisible by remember { mutableStateOf(false) }

    MifosTheme {
        MifosOutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = "Password",
            keyboardType = KeyboardType.Password,
            isPasswordVisible = passwordVisible,
            onPasswordToggleClick = { passwordVisible = it },
            showClearIcon = true,
            onClickClearIcon = { text = "" },
        )
    }
}

@Preview
@Composable
private fun MifosOutlinedTextField_ErrorPreview() {
    var text by remember { mutableStateOf("") }
    MifosTheme {
        MifosOutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = "Email",
            error = "Invalid email address",
            icon = Icons.Default.Email,
        )
    }
}

@Preview
@Composable
private fun MifosDatePickerTextFieldPreview() {
    MifosTheme {
        MifosDatePickerTextField(
            value = "2025-08-18",
            label = "Date of Birth",
            openDatePicker = { /* open picker */ },
        )
    }
}
