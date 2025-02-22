package com.mifos.mifosxdroid.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mifos.mifosxdroid.R

@Composable
fun LogoutDialog(
    showDialogState: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    message: Int? = null,
) {
    if (showDialogState) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(id = R.string.dialog_message_logout_confirm)) },
            text = {
                if (message != null) {
                    Text(text = stringResource(id = R.string.dialog_message_logout_confirmation))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                    },
                ) {
                    Text(stringResource(id = R.string.dialog_action_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(id = R.string.dialog_action_back))
                }
            },
        )
    }
}
