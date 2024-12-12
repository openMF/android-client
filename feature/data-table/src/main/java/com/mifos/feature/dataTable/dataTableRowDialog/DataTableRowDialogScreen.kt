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

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.noncore.DataTable
import com.mifos.feature.data_table.R

@Composable
fun DataTableRowDialogScreen(
    dataTable: DataTable,
    entityId: Int,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: DataTableRowDialogViewModel = hiltViewModel(),
) {
    val state by viewModel.dataTableRowDialogUiState.collectAsStateWithLifecycle()

    DataTableRowDialogScreen(
        dataTable = dataTable,
        state = state,
        onDismiss = onDismiss,
        onSuccess = onSuccess,
        onRetry = { },
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
    dataTable: DataTable,
    state: DataTableRowDialogUiState,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    onRetry: () -> Unit,
    onCreate: (HashMap<String, String>) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                        Toast.makeText(
                            LocalContext.current,
                            stringResource(id = R.string.feature_data_table_added_data_table_successfully),
                            Toast.LENGTH_SHORT,
                        ).show()
                        onSuccess()
                    }

                    is DataTableRowDialogUiState.Error -> MifosSweetError(
                        message = stringResource(
                            id = state.message,
                        ),
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
                                    text = stringResource(id = R.string.feature_data_table_add_data_table),
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    color = BluePrimary,
                                )
                                IconButton(onClick = { onDismiss() }) {
                                    Icon(
                                        imageVector = MifosIcons.close,
                                        contentDescription = "",
                                        tint = colorResource(android.R.color.darker_gray),
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

                    is DataTableRowDialogUiState.Loading -> MifosCircularProgress()
                }
            }
        }
    }
}

@Composable
fun DataTableRowDialogContent(
    table: DataTable,
    onCreate: (HashMap<String, String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Log.d("DataTable", table.toString())
    Log.d("DataTable", onCreate.toString())

    // TODO dataTable is now returning null for columnHeaderData, so we will correct this and then construct a form to implement.

    Button(
        onClick = {
            // TODO call onCreate with using addDataTableInput
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonColors(
            containerColor = BluePrimary,
            contentColor = White,
            disabledContainerColor = BluePrimary,
            disabledContentColor = Color.Gray,
        ),
    ) {
        Text(text = stringResource(id = R.string.feature_data_table_submit))
    }
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

class DataTableRowDialogUiStateProvider : PreviewParameterProvider<DataTableRowDialogUiState> {

    override val values: Sequence<DataTableRowDialogUiState>
        get() = sequenceOf(
            DataTableRowDialogUiState.Initial,
            DataTableRowDialogUiState.Loading,
            DataTableRowDialogUiState.Error(R.string.feature_data_table_failed_to_add_data_table),
            DataTableRowDialogUiState.DataTableEntrySuccessfully,
        )
}

@Preview(showBackground = true)
@Composable
private fun DataTableRowDialogScreenPreview(
    @PreviewParameter(DataTableRowDialogUiStateProvider::class) state: DataTableRowDialogUiState,
) {
    DataTableRowDialogScreen(
        dataTable = DataTable(),
        state = state,
        onDismiss = {},
        onSuccess = {},
        onRetry = {},
        onCreate = {},
    )
}
