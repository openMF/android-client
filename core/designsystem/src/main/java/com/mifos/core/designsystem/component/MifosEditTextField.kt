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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.White

/**
 * Created by Aditya Gupta on 21/02/24.
 */

@Composable
fun MifosOutlinedTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    icon: ImageVector? = null,
    label: Int,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    error: Int?
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(id = label)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        leadingIcon = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSystemInDarkTheme()) White else DarkGray
                )
            }
        } else null,
        trailingIcon = trailingIcon,
        maxLines = maxLines,
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
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
                    text = stringResource(id = error),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

@Composable
fun MifosOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
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
            )
        },
        leadingIcon = {
            leadingIcon?.let { Icon(imageVector = it, contentDescription = "leadingIcon") }
        },
        trailingIcon = @Composable {
            if (isPasswordToggleDisplayed) {
                PasswordToggleIcon(
                    isPasswordVisible = isPasswordVisible,
                    onPasswordToggleClick = onPasswordToggleClick
                )
            } else if (showClearIcon && isFocused) {
                ClearIconButton(
                    showClearIcon = true,
                    clearIcon = clearIcon,
                    onClickClearIcon = onClickClearIcon
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
    modifier: Modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
    value: String,
    onValueChange: (String) -> Unit,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    icon: ImageVector? = null,
    label: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: Int?
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        leadingIcon = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSystemInDarkTheme()) White else DarkGray
                )
            }
        } else null,
        trailingIcon = trailingIcon,
        maxLines = maxLines,
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
            focusedLabelColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
        ),
        textStyle = LocalDensity.current.run {
            TextStyle(fontSize = 18.sp)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next,keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        isError = error != null,
        supportingText = if (error != null) {
            {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = error),
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            null
        }
    )
}

@Composable
private fun PasswordToggleIcon(
    modifier: Modifier = Modifier,
    isPasswordVisible: Boolean,
    onPasswordToggleClick: (Boolean) -> Unit
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
    modifier: Modifier = Modifier,
    showClearIcon: Boolean,
    clearIcon: ImageVector,
    onClickClearIcon: () -> Unit,
) {
    AnimatedVisibility(
        visible = showClearIcon
    ) {
        IconButton(
            onClick = onClickClearIcon,
            modifier = modifier,
        ) {
            Icon(
                imageVector = clearIcon,
                contentDescription = "trailingIcon"
            )
        }
    }

}

@Composable
fun MifosDatePickerTextField(
    modifier: Modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
    value: String,
    label: Int,
    openDatePicker: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = { Text(text = stringResource(id = label)) },
        readOnly = true,
        modifier = modifier,
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
        ),
        textStyle = LocalDensity.current.run {
            TextStyle(fontSize = 18.sp)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        trailingIcon = {
            IconButton(onClick = { openDatePicker() }) {
                Icon(imageVector = Icons.Default.CalendarToday, null)
            }
        }
    )
}