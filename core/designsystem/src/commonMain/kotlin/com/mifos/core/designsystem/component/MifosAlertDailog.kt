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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mifos.core.designsystem.R

@Composable
fun MifosDialogBox(
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

@Composable
fun MifosRadioButtonDialog(
    titleResId: Int,
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
                Text(text = stringResource(id = titleResId))
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

@Composable
fun UpdateEndpointDialogScreen(
    initialBaseURL: String?,
    initialTenant: String?,
    onDismissRequest: () -> Unit,
    handleEndpointUpdate: (baseURL: String, tenant: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var baseURL by rememberSaveable { mutableStateOf(initialBaseURL) }
    var tenant by rememberSaveable { mutableStateOf(initialTenant) }

    Dialog(
        onDismissRequest = { onDismissRequest.invoke() },
    ) {
        Card {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Text(text = stringResource(id = R.string.core_designsystem_pref_base_url_title))
                Spacer(modifier = Modifier.height(8.dp))

                baseURL?.let {
                    OutlinedTextField(
                        value = it,
                        onValueChange = { baseURL = it },
                        label = { Text(text = stringResource(id = R.string.core_designsystem_enter_base_url)) },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                tenant?.let {
                    OutlinedTextField(
                        value = it,
                        onValueChange = { tenant = it },
                        label = { Text(text = stringResource(id = R.string.core_designsystem_enter_tenant)) },
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismissRequest.invoke() },
                    ) {
                        Text(text = stringResource(id = R.string.core_designsystem_cancel))
                    }
                    TextButton(
                        onClick = {
                            if (baseURL != null && tenant != null) {
                                handleEndpointUpdate.invoke(baseURL ?: "", tenant ?: "")
                            }
                        },
                    ) {
                        Text(text = stringResource(id = R.string.core_designsystem_dialog_action_ok))
                    }
                }
            }
        }
    }
}
