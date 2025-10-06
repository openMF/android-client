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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mifos.core.designsystem.theme.MifosTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MifosDialogBox(
    title: String,
    showDialogState: Boolean,
    confirmButtonText: String,
    dismissButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    if (showDialogState) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            title = { Text(text = title) },
            text = {
                if (message != null) {
                    Text(text = message)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                    },
                ) {
                    Text(text = confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = dismissButtonText)
                }
            },
        )
    }
}

@Composable
fun MifosDialogBox(
    title: String,
    showDialogState: Boolean,
    confirmButtonText: String,
    dismissButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    message: @Composable () -> Unit,
) {
    if (showDialogState) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            title = { Text(text = title) },
            text = message,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                    },
                ) {
                    Text(text = confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = dismissButtonText)
                }
            },
        )
    }
}

@Composable
fun MifosRadioButtonDialog(
    title: String,
    selectedItem: String,
    items: Array<String>,
    selectItem: (item: String, index: Int) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = { onDismissRequest.invoke() },
    ) {
        Card(modifier = modifier) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = title)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp),
                ) {
                    itemsIndexed(items = items) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    onDismissRequest.invoke()
                                    selectItem.invoke(item, index)
                                }
                                .fillMaxWidth(),
                        ) {
                            RadioButton(
                                selected = (item == selectedItem),
                                onClick = {
                                    onDismissRequest.invoke()
                                    selectItem.invoke(item, index)
                                },
                            )
                            Text(
                                text = item,
                                modifier = Modifier.padding(start = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosCustomDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        content = content,
        modifier = modifier,
        properties = properties,
    )
}

@Preview
@Composable
private fun MifosDialogBoxWithMessagePreview() {
    MifosTheme {
        MifosDialogBox(
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
private fun MifosDialogBoxWithComposableMessagePreview() {
    MifosTheme {
        MifosDialogBox(
            title = "Info",
            showDialogState = true,
            confirmButtonText = "Okay",
            dismissButtonText = "Cancel",
            onConfirm = {},
            onDismiss = {},
            message = {
                Column {
                    Text("This is a custom composable inside the dialog.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("You can place anything here, like a list or input field.")
                }
            },
        )
    }
}

@Preview
@Composable
private fun MifosRadioButtonDialogPreview() {
    MifosTheme {
        MifosRadioButtonDialog(
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
private fun MifosCustomDialogPreview() {
    MifosTheme {
        MifosCustomDialog(
            onDismiss = {},
            content = {
                Card(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("This is a custom dialog.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("You can fully customize this content.")
                    }
                }
            },
        )
    }
}
