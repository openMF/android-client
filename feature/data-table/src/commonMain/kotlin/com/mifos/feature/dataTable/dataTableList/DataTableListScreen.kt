/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTableList

import androidclient.feature.data_table.generated.resources.Res
import androidclient.feature.data_table.generated.resources.feature_data_table_associated_datatables
import androidclient.feature.data_table.generated.resources.feature_data_table_dismiss
import androidclient.feature.data_table.generated.resources.feature_data_table_save
import androidclient.feature.data_table.generated.resources.feature_data_table_select_date
import androidclient.feature.data_table.generated.resources.feature_data_table_something_went_wrong
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.noncore.DataTableEntity
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun DataTableListScreen(
    onBackPressed: () -> Unit,
    clientCreated: (ClientPayloadEntity, Boolean) -> Unit,
    viewModel: DataTableListViewModel = koinViewModel(),
) {
    val dataTables = viewModel.arg.dataTableList
    val requestType = viewModel.arg.requestType
    val formWidgetsList = viewModel.arg.formWidget
    val payload = viewModel.arg.payload
    val uiState by viewModel.dataTableListUiState.collectAsStateWithLifecycle()
    val userStatus by viewModel.userStatus.collectAsStateWithLifecycle()
    val dataTableList by viewModel.dataTableList.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.initArgs(dataTables, requestType, formWidgetsList, payload)
    }

    DataTableListScreen(
        uiState = uiState,
        dataTableList = dataTableList ?: listOf(),
        onBackPressed = onBackPressed,
        clientCreated = { client -> clientCreated(client, userStatus) },
        onSaveClicked = { viewModel.processDataTable() },
    )
}

@Composable
fun DataTableListScreen(
    uiState: DataTableListUiState,
    dataTableList: List<DataTableEntity>,
    onBackPressed: () -> Unit,
    clientCreated: (ClientPayloadEntity) -> Unit,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackBarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        title = stringResource(Res.string.feature_data_table_associated_datatables),
        onBackPressed = onBackPressed,
        snackbarHostState = snackBarHostState,
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            DataTableListContent(
                dataTableList = dataTableList,
                onSaveClicked = onSaveClicked,
                modifier = Modifier.fillMaxWidth(),
            )

            when (uiState) {
                is DataTableListUiState.ShowMessage -> {
                    val message = when {
                        uiState.message != null -> stringResource(uiState.message)
                        else -> stringResource(Res.string.feature_data_table_something_went_wrong)
                    }
                    LaunchedEffect(message) {
                        snackBarHostState.showSnackbar(message = message)
                    }
                }

                is DataTableListUiState.Loading -> MifosProgressIndicator()
                is DataTableListUiState.Success -> {
                    uiState.client?.let { client ->
                        clientCreated(client)
                    } ?: run {
                        val message = when {
                            uiState.message != null -> stringResource(uiState.message)
                            else -> stringResource(Res.string.feature_data_table_something_went_wrong)
                        }
                        LaunchedEffect(key1 = message) {
                            snackBarHostState.showSnackbar(message)
                        }
                        onBackPressed()
                    }
                }
            }
        }
    }
}

@Composable
fun DataTableListContent(
    dataTableList: List<DataTableEntity>,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState),
    ) {
        for (table in dataTableList) {
            Text(
                text = table.registeredTableName ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            TableColumnHeader(table = table)
        }

        Button(
            onClick = { onSaveClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Text(text = stringResource(Res.string.feature_data_table_save), color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun TableColumnHeader(
    table: DataTableEntity,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        table.columnHeaderData.filter { it.columnPrimaryKey != null }.forEach { columnHeader ->
            when (columnHeader.columnDisplayType) {
                BaseFormWidget.SCHEMA_KEY_STRING, BaseFormWidget.SCHEMA_KEY_TEXT -> {
                    MifosOutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = columnHeader.dataTableColumnName ?: "",
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                BaseFormWidget.SCHEMA_KEY_INT, BaseFormWidget.SCHEMA_KEY_DECIMAL -> {
                    MifosOutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = columnHeader.dataTableColumnName ?: "",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                BaseFormWidget.SCHEMA_KEY_CODELOOKUP, BaseFormWidget.SCHEMA_KEY_CODEVALUE -> {
                    var selectedValue by remember { mutableStateOf("") }
                    val columnValueStrings = columnHeader.columnValues.map { it.value.orEmpty() }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        MifosTextFieldDropdown(
                            value = selectedValue,
                            onValueChanged = { selectedValue = it },
                            label = columnHeader.dataTableColumnName,
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            options = columnValueStrings,
                            onOptionSelected = { _, item -> selectedValue = item },
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                BaseFormWidget.SCHEMA_KEY_DATE -> {
                    var showDatePicker by rememberSaveable { mutableStateOf(false) }
                    var selectedDate by rememberSaveable {
                        mutableLongStateOf(
                            Clock.System.now().toEpochMilliseconds(),
                        )
                    }
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = selectedDate,
                        selectableDates = object : SelectableDates {
                            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
                            }
                        },
                    )

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = {
                                showDatePicker = false
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDatePicker = false
                                        datePickerState.selectedDateMillis?.let {
                                            selectedDate = it
                                        }
                                    },
                                ) { Text(stringResource(Res.string.feature_data_table_select_date)) }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showDatePicker = false
                                    },
                                ) { Text(stringResource(Res.string.feature_data_table_dismiss)) }
                            },
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    MifosDatePickerTextField(
                        value = DateHelper.getDateAsStringFromLong(selectedDate),
                        label = columnHeader.dataTableColumnName ?: "",
                        openDatePicker = {
                            showDatePicker = true
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                BaseFormWidget.SCHEMA_KEY_BOOL -> {
                    var checked by remember { mutableStateOf(false) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp),
                    ) {
                        Text(
                            text = columnHeader.dataTableColumnName ?: "",
                            modifier = Modifier.weight(1f),
                        )

                        Switch(
                            checked = checked,
                            onCheckedChange = { checked = it },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DataTableListScreenPreview() {
    DataTableListScreen(
        uiState = DataTableListUiState.Success(),
        dataTableList = listOf(),
        onBackPressed = { },
        clientCreated = { },
        onSaveClicked = { },
    )
}

// private fun createFormWidgetList(): MutableList<List<FormWidget>> {
//    return dataTables?.map { createForm(it) }?.toMutableList() ?: mutableListOf()
// }
//
// private fun createForm(table: DataTable): List<FormWidget> {
//    return table.columnHeaderData
//        .filterNot { it.columnPrimaryKey == true }
//        .map { createFormWidget(it) }
// }
//
// private fun createFormWidget(columnHeader: ColumnHeader): FormWidget {
//    return when (columnHeader.columnDisplayType) {
//        FormWidget.SCHEMA_KEY_STRING, FormWidget.SCHEMA_KEY_TEXT -> FormEditText(
//            activity,
//            columnHeader.dataTableColumnName
//        )
//
//        FormWidget.SCHEMA_KEY_INT -> FormNumericEditText(
//            activity,
//            columnHeader.dataTableColumnName
//        ).apply { returnType = FormWidget.SCHEMA_KEY_INT }
//
//        FormWidget.SCHEMA_KEY_DECIMAL -> FormNumericEditText(
//            activity,
//            columnHeader.dataTableColumnName
//        ).apply { returnType = FormWidget.SCHEMA_KEY_DECIMAL }
//
//        FormWidget.SCHEMA_KEY_CODELOOKUP, FormWidget.SCHEMA_KEY_CODEVALUE -> createFormSpinner(
//            columnHeader
//        )
//
//        FormWidget.SCHEMA_KEY_DATE -> FormEditText(
//            activity,
//            columnHeader.dataTableColumnName
//        ).apply { setIsDateField(true, requireActivity().supportFragmentManager) }
//
//        FormWidget.SCHEMA_KEY_BOOL -> FormToggleButton(
//            activity,
//            columnHeader.dataTableColumnName
//        )
//
//        else -> FormEditText(activity, columnHeader.dataTableColumnName)
//    }
// }
//
// private fun createFormSpinner(columnHeader: ColumnHeader): FormSpinner {
//    val columnValueStrings = columnHeader.columnValues.mapNotNull { it.value }
//    val columnValueIds = columnHeader.columnValues.mapNotNull { it.id }
//    return FormSpinner(
//        activity,
//        columnHeader.dataTableColumnName,
//        columnValueStrings,
//        columnValueIds
//    ).apply {
//        returnType = FormWidget.SCHEMA_KEY_CODEVALUE
//    }
// }
//
// private fun showClientCreatedSuccessfully(client: Client) {
//    requireActivity().supportFragmentManager.popBackStack()
//    requireActivity().supportFragmentManager.popBackStack()
//    Toast.makeText(
//        activity, getString(R.string.client) +
//                MifosResponseHandler.response, Toast.LENGTH_SHORT
//    ).show()
//    if (PrefManager.userStatus == Constants.USER_ONLINE) {
//        val clientActivityIntent = Intent(activity, ClientActivity::class.kotlin)
//        clientActivityIntent.putExtra(Constants.CLIENT_ID, client.clientId)
//        startActivity(clientActivityIntent)
//    }
// }
