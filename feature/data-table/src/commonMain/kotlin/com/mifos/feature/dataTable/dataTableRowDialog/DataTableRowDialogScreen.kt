/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTableRowDialog

import androidclient.feature.data_table.generated.resources.Res
import androidclient.feature.data_table.generated.resources.feature_data_table_add_data_table
import androidclient.feature.data_table.generated.resources.feature_data_table_added_data_table_successfully
import androidclient.feature.data_table.generated.resources.feature_data_table_submit
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.room.entities.noncore.DataTableEntity
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DataTableRowDialogScreen(
    dataTable: DataTableEntity,
    snackbarHostState: SnackbarHostState,
    entityId: Int,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: DataTableRowDialogViewModel = koinViewModel(),
) {
    val state by viewModel.dataTableRowDialogUiState.collectAsStateWithLifecycle()

    DataTableRowDialogScreen(
        dataTable = dataTable,
        state = state,
        onDismiss = onDismiss,
        onSuccess = onSuccess,
        onRetry = { },
        snackbarHostState = snackbarHostState,
        onCreate = {
            dataTable.registeredTableName?.let { tableName ->
                viewModel.addDataTableEntry(
                    table = tableName,
                    payload = it,
                    entityId = entityId,
                )
            }
        },
    )
}

@Composable
fun DataTableRowDialogScreen(
    dataTable: DataTableEntity,
    state: DataTableRowDialogUiState,
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    onRetry: () -> Unit,
    onCreate: (HashMap<String, String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                when (state) {
                    is DataTableRowDialogUiState.DataTableEntrySuccessfully -> {
                        val message = stringResource(Res.string.feature_data_table_added_data_table_successfully)
                        scope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                        onSuccess.invoke()
                    }

                    is DataTableRowDialogUiState.Error -> MifosSweetError(
                        message = state.message,
                    ) {
                        onRetry()
                    }

                    is DataTableRowDialogUiState.Initial -> {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(Res.string.feature_data_table_add_data_table),
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    color = Color.Blue,
                                )
                                IconButton(onClick = { onDismiss() }) {
                                    Icon(
                                        imageVector = MifosIcons.Close,
                                        contentDescription = "",
                                        modifier = Modifier
                                            .width(30.dp)
                                            .height(30.dp),
                                    )
                                }
                            }
                            DataTableRowDialogContent(
                                table = dataTable,
                                onCreate = onCreate,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    is DataTableRowDialogUiState.Loading -> MifosProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun DataTableRowDialogContent(
    table: DataTableEntity,
    onCreate: (HashMap<String, String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO dataTable is now returning null for columnHeaderData, so we will correct this and then construct a form to implement.

    Button(
        onClick = {
            // TODO call onCreate with using addDataTableInput
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
    ) {
        Text(text = stringResource(Res.string.feature_data_table_submit))
    }
}

class DataTableRowDialogUiStateProvider : PreviewParameterProvider<DataTableRowDialogUiState> {

    override val values: Sequence<DataTableRowDialogUiState>
        get() = sequenceOf(
            DataTableRowDialogUiState.Initial,
            DataTableRowDialogUiState.Loading,
            DataTableRowDialogUiState.Error("Something went wrong"),
            DataTableRowDialogUiState.DataTableEntrySuccessfully,
        )
}

@Preview
@Composable
private fun DataTableRowDialogScreenPreview(
    @PreviewParameter(DataTableRowDialogUiStateProvider::class) state: DataTableRowDialogUiState,
) {
    DataTableRowDialogScreen(
        dataTable = DataTableEntity(),
        state = state,
        onDismiss = {},
        onSuccess = {},
        snackbarHostState = remember { SnackbarHostState() },
        onRetry = {},
        onCreate = {},
    )
}

// private fun createForm(table: DataTable?) {
//    val formWidgets: MutableList<FormWidget> = ArrayList()
//    if (table != null) {
//        for (columnHeader in table.columnHeaderData) {
//            if (!columnHeader.columnPrimaryKey!!) {
//                if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_STRING || columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_TEXT) {
//                    val formEditText = FormEditText(
//                        activity, columnHeader
//                            .dataTableColumnName
//                    )
//                    formWidgets.add(formEditText)
//                    binding.llDataTableEntryForm.addView(formEditText.view)
//                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_INT) {
//                    val formNumericEditText =
//                        FormNumericEditText(activity, columnHeader.dataTableColumnName)
//                    formNumericEditText.returnType = FormWidget.SCHEMA_KEY_INT
//                    formWidgets.add(formNumericEditText)
//                    binding.llDataTableEntryForm.addView(formNumericEditText.view)
//                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_DECIMAL) {
//                    val formNumericEditText =
//                        FormNumericEditText(activity, columnHeader.dataTableColumnName)
//                    formNumericEditText.returnType = FormWidget.SCHEMA_KEY_DECIMAL
//                    formWidgets.add(formNumericEditText)
//                    binding.llDataTableEntryForm.addView(formNumericEditText.view)
//                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_CODELOOKUP || columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_CODEVALUE) {
//                    if (columnHeader.columnValues.isNotEmpty()) {
//                        val columnValueStrings: MutableList<String> = ArrayList()
//                        val columnValueIds: MutableList<Int> = ArrayList()
//                        for (columnValue in columnHeader.columnValues) {
//                            columnValue.value?.let { columnValueStrings.add(it) }
//                            columnValue.id?.let { columnValueIds.add(it) }
//                        }
//                        val formSpinner = FormSpinner(
//                            activity, columnHeader
//                                .dataTableColumnName, columnValueStrings, columnValueIds
//                        )
//                        formSpinner.returnType = FormWidget.SCHEMA_KEY_CODEVALUE
//                        formWidgets.add(formSpinner)
//                        binding.llDataTableEntryForm.addView(formSpinner.view)
//                    }
//                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_DATE) {
//                    val formEditText = FormEditText(
//                        activity, columnHeader
//                            .dataTableColumnName
//                    )
//                    formEditText.setIsDateField(true, requireActivity().supportFragmentManager)
//                    formWidgets.add(formEditText)
//                    binding.llDataTableEntryForm.addView(formEditText.view)
//                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_BOOL) {
//                    val formToggleButton = FormToggleButton(
//                        activity,
//                        columnHeader.dataTableColumnName
//                    )
//                    formWidgets.add(formToggleButton)
//                    binding.llDataTableEntryForm.addView(formToggleButton.view)
//                }
//            }
//        }
//    }
//    listFormWidgets.addAll(formWidgets)
// }

// private fun addDataTableInput(): HashMap<String, String> {
//    val formWidgets: List<FormWidget> = listFormWidgets
//    val payload = HashMap<String, String>()
//    payload[Constants.DATE_FORMAT] = "dd-mm-YYYY"
//    payload[Constants.LOCALE] = "en"
//    for (formWidget in formWidgets) {
//        when (formWidget.returnType) {
//            FormWidget.SCHEMA_KEY_INT -> payload[formWidget.propertyName] =
//                (if (formWidget.value
//                    == ""
//                ) "0" else formWidget.value).toInt().toString()
//
//            FormWidget.SCHEMA_KEY_DECIMAL -> payload[formWidget.propertyName] =
//                (if (formWidget.value == "") "0.0" else formWidget.value).toDouble().toString()
//
//            FormWidget.SCHEMA_KEY_CODEVALUE -> {
//                val formSpinner = formWidget as FormSpinner
//                payload[formWidget.propertyName] =
//                    formSpinner.getIdOfSelectedItem(formWidget.value).toString()
//            }
//
//            else -> payload[formWidget.propertyName] = formWidget.value
//        }
//    }
//    return payload
// }
//
