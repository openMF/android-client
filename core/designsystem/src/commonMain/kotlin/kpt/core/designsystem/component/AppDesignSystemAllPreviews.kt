/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import kpt.core.base.designsystem.theme.KptTheme
import kpt.core.designsystem.icon.AppIcons
import kpt.core.designsystem.theme.AppTheme
import kpt.core.designsystem.theme.DesignToken
import kpt.core.designsystem.utility.TabContent
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter

@Preview
@Composable
private fun AppDialogBoxWithMessage() {
    AppTheme {
        AppDialogBox(
            title = "Delete Item",
            showDialogState = true,
            confirmButtonText = "Yes",
            dismissButtonText = "No",
            onConfirm = {},
            onDismiss = {},
            message = "Are you sure you want to delete this item?",
        )
    }
}

@Preview
@Composable
private fun AppDialogBoxWithComposableMessage() {
    AppTheme {
        AppDialogBox(
            title = "Info",
            showDialogState = true,
            confirmButtonText = "Okay",
            dismissButtonText = "Cancel",
            onConfirm = {},
            onDismiss = {},
            message = {
                Column {
                    Text("This is a custom composable inside the dialog.")
                    Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
                    Text("You can place anything here, like a list or input field.")
                }
            },
        )
    }
}

@Preview
@Composable
private fun AppRadioButtonDialog() {
    AppTheme {
        AppRadioButtonDialog(
            title = "Choose Option",
            selectedItem = "Option 2",
            items = arrayOf("Option 1", "Option 2", "Option 3", "Option 4"),
            selectItem = { _, _ -> },
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
private fun AppCustomDialog() {
    AppTheme {
        AppCustomDialog(
            onDismiss = {},
            content = {
                Card(
                    modifier = Modifier.padding(KptTheme.spacing.md),
                ) {
                    Column(modifier = Modifier.padding(KptTheme.spacing.md)) {
                        Text("This is a custom dialog.")
                        Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
                        Text("You can fully customize this content.")
                    }
                }
            },
        )
    }
}

@Preview
@Composable
private fun AppAndroidClientIcon() {
    AppTheme {
        AppAndroidClientIcon(
            imageVector = rememberVectorPainter(Icons.Default.Android),
            modifier = Modifier.padding(KptTheme.spacing.md),
        )
    }
}

@Preview
@Composable
private fun AppBasicDialog(
    @PreviewParameter(BasicDialogStatePreviewProvider::class)
    state: BasicDialogState,
) {
    AppTheme {
        AppBasicDialog(
            visibilityState = state,
            onConfirm = {},
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
private fun AppBasicDialog() {
    AppTheme {
        AppBasicDialog(
            visibilityState = BasicDialogState.Shown(
                title = "An error has occurred.",
                message = "Username or password is incorrect. Try again.",
            ),
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
private fun AppBottomSheet() {
    AppTheme {
        AppBottomSheet(
            content = {
                Box(
                    Modifier.height(DesignToken.sizes.imageDp100),
                ) {
                }
            },
            onDismiss = {},
        )
    }
}

@Preview
@Composable
private fun AppButton() {
    AppTheme {
        AppButton(
            onClick = {},
        ) {
            Text("Default Button")
        }
    }
}

@Preview
@Composable
private fun AppButtonWithText() {
    AppTheme {
        AppButton(
            onClick = {},
            text = { Text("Text Button") },
        )
    }
}

@Preview
@Composable
private fun AppOutlinedButton() {
    AppTheme {
        AppOutlinedButton(
            onClick = {},
        ) {
            Text("Outlined Button")
        }
    }
}

@Preview
@Composable
private fun AppTextButton() {
    AppTheme {
        AppTextButton(
            onClick = {},
        ) {
            Text("Text Button")
        }
    }
}

@Preview
@Composable
private fun AppTextButtonWithIcon() {
    AppTheme {
        AppTextButton(
            text = { Text("With Icon") },
            onClick = {},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                )
            },
        )
    }
}

@Preview
@Composable
private fun AppCard() {
    AppTheme {
        AppCard {
            Column(
                modifier = Modifier.padding(KptTheme.spacing.md),
            ) {
                Text("Simple Card")
                Text("This is the card content.")
            }
        }
    }
}

@Preview
@Composable
private fun AppErrorContent() {
    AppTheme {
        AppErrorContent(
            message = "Something went wrong. Please try again later.",
        )
    }
}

@Preview
@Composable
private fun AppErrorContentWithRefresh() {
    AppTheme {
        AppErrorContent(
            message = "Failed to load data.",
            isRefreshEnabled = true,
            refreshButtonText = "Retry",
            onRefresh = { /* Handle refresh */ },
        )
    }
}

@Preview
@Composable
private fun AppLoadingDialog() {
    AppTheme {
        AppLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )
    }
}

@Preview
@Composable
private fun AppMenuDropDownItem() {
    AppTheme {
        DropdownMenu(
            expanded = true,
            onDismissRequest = {},
        ) {
            AppMenuDropDownItem(
                option = "Profile",
                onClick = {},
            )
            AppMenuDropDownItem(
                option = "Settings",
                onClick = {},
            )
            AppMenuDropDownItem(
                option = "Logout",
                onClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewOtpTextField() {
    AppTheme {
        AppOtpTextField(
            onOtpTextCorrectlyEntered = {},
            realOtp = "1234",
            otpCount = 4,
        )
    }
}

@Preview
@Composable
private fun AppOutlinedTextField_ValuePreview() {
    var text by remember { mutableStateOf(TextFieldValue("Hello")) }
    AppTheme {
        AppOutlinedTextField(
            value = text,
            onValueChanged = { text = it },
            label = "Username",
            icon = Icons.Default.Person,
        )
    }
}

@Preview
@Composable
private fun AppOutlinedTextField_PasswordPreview() {
    var text by remember { mutableStateOf("secret123") }
    var passwordVisible by remember { mutableStateOf(false) }

    AppTheme {
        AppOutlinedTextField(
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
private fun AppOutlinedTextField_ErrorPreview() {
    var text by remember { mutableStateOf("") }
    AppTheme {
        AppOutlinedTextField(
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
private fun AppDatePickerTextFieldPreview() {
    AppTheme {
        AppDatePickerTextField(
            value = "2025-08-18",
            label = "Date of Birth",
            openDatePicker = { /* open picker */ },
        )
    }
}

@Preview
@Composable
private fun AppPasswordField_preview_withInput_hidePassword() {
    AppTheme {
        AppPasswordField(
            label = "Label",
            value = "Password",
            onValueChange = {},
            initialShowPassword = false,
            hint = "Hint",
        )
    }
}

@Preview
@Composable
private fun AppPasswordField_preview_withInput_showPassword() {
    AppTheme {
        AppPasswordField(
            label = "Label",
            value = "Password",
            onValueChange = {},
            initialShowPassword = true,
            hint = "Hint",
        )
    }
}

@Preview
@Composable
private fun AppPasswordField_preview_withoutInput_hidePassword() {
    AppTheme {
        AppPasswordField(
            label = "Label",
            value = "",
            onValueChange = {},
            initialShowPassword = false,
            hint = "Hint",
        )
    }
}

@Preview
@Composable
private fun AppPasswordField_preview_withoutInput_showPassword() {
    AppTheme {
        AppPasswordField(
            label = "Label",
            value = "",
            onValueChange = {},
            initialShowPassword = true,
            hint = "Hint",
        )
    }
}

@Preview
@Composable
private fun AppSweetErrorPreview() {
    AppTheme {
        AppSweetError(
            message = "Something went wrong. Please check your network.",
            buttonText = "Retry",
            onclick = {},
        )
    }
}

@Preview
@Composable
private fun AppSweetError_NoRetryPreview() {
    AppTheme {
        AppSweetError(
            message = "Failed to fetch data.",
            isRetryEnabled = false,
        )
    }
}

@Preview
@Composable
private fun AppPaginationSweetErrorPreview() {
    AppTheme {
        AppPaginationSweetError(
            onclick = {},
        )
    }
}

@Preview
@Composable
private fun AppTabPreview() {
    AppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            modifier = Modifier.padding(KptTheme.spacing.md),
        ) {
            AppTab(
                text = "Selected Tab",
                selected = true,
                onClick = {},
            )
            AppTab(
                text = "Unselected Tab",
                selected = false,
                onClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun AppTabRowPreview() {
    val pagerState = rememberPagerState { 2 }
    val tabContents = listOf(
        TabContent("Home") { Text("Home Content") },
        TabContent("Profile") { Text("Profile Content") },
    )

    AppTheme {
        AppTabRow(
            tabContents = tabContents,
            pagerState = pagerState,
        )
    }
}

@Preview
@Composable
private fun AppTextFieldDropdownPreview() {
    val options = listOf("Option 1", "Option 2", "Option 3")
    var selectedValue by remember { mutableStateOf("") }

    AppTheme {
        AppTextFieldDropdown(
            value = selectedValue,
            onValueChanged = { selectedValue = it },
            onOptionSelected = { _, option -> selectedValue = option },
            options = options,
            label = "Select Option",
        )
    }
}

@Preview
@Composable
private fun AppTopBarPreview() {
    AppTheme {
        AppTopBar(
            topBarTitle = "Sample Title",
            backPress = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AppTopAppBar_preview() {
    AppTheme {
        AppTopAppBar(
            title = "Title",
            scrollBehavior = TopAppBarDefaults
                .exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState(),
                ),
            navigationIcon = NavigationIcon(
                navigationIcon = AppIcons.Back,
                navigationIconContentDescription = "Back",
                onNavigationIconClick = { },
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AppTopAppBarOverflow_preview() {
    AppTheme {
        AppTopAppBar(
            title = "Title that is too long for the top line",
            scrollBehavior = TopAppBarDefaults
                .exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState(),
                ),
            navigationIcon = NavigationIcon(
                navigationIcon = AppIcons.Close,
                navigationIconContentDescription = "Close",
                onNavigationIconClick = { },
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AppTopAppBarOverflowCutoff_preview() {
    AppTheme {
        AppTopAppBar(
            title = "Title that is too long for the top line and the bottom line",
            scrollBehavior = TopAppBarDefaults
                .exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState(),
                ),
            navigationIcon = NavigationIcon(
                navigationIcon = AppIcons.Close,
                navigationIconContentDescription = "Close",
                onNavigationIconClick = { },
            ),
        )
    }
}
