package com.mifos.mifosxdroid.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun LogoutDialog(
    showDialogState: Boolean,
    onDismiss: () -> Unit,
    title: Int,
    confirmButtonText: Int,
    onConfirm: () -> Unit,
    dismissButtonText: Int,
    message: Int? = null,
) {
    if (showDialogState) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(id = title)) },
            text = {
                if (message != null) {
                    Text(text = stringResource(id = message))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                    },
                ) {
                    Text(stringResource(id = confirmButtonText))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(id = dismissButtonText))
                }
            },
        )
    }
}
