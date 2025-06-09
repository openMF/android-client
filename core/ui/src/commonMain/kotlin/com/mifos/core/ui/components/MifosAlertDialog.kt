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

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.mifos.core.designsystem.component.MifosTextButton
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.util.DevicePreview

@Composable
fun MifosAlertDialog(
    dialogTitle: String,
    dialogText: String,
    dismissText: String? = "Cancel",
    confirmationText: String = "Ok",
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    AlertDialog(
        icon = {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null)
            }
        },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            MifosTextButton(
                text = { Text(text = confirmationText) },
                onClick = onConfirmation,
            )
        },
        dismissButton = {
            if (dismissText != null) {
                MifosTextButton(
                    text = { Text(text = dismissText) },
                    onClick = onDismissRequest,
                )
            }
        },
    )
}

@DevicePreview
@Composable
private fun MifosAlertDialogPreview(
    modifier: Modifier = Modifier,
) {
    MifosTheme {
        MifosAlertDialog(
            dialogTitle = "Dialog Title",
            dialogText = "Dialog Text",
            dismissText = "Dismiss",
            confirmationText = "Confirm",
            onDismissRequest = {},
            onConfirmation = {},
            modifier = modifier,
        )
    }
}
