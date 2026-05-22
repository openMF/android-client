/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import template.core.base.designsystem.theme.KptTheme
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
    val payload = viewModel.arg.payload
    val uiState by viewModel.dataTableListUiState.collectAsStateWithLifecycle()
    val userStatus by viewModel.userStatus.collectAsStateWithLifecycle()
    val dataTableList by viewModel.dataTableList.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.initArgs(dataTables, requestType, payload)
    }

    DataTableListScreen(
        uiState = uiState,
        dataTableList = dataTableList ?: listOf(),
        onBackPressed = onBackPressed,
        clientCreated = { client -> clientCreated(client, userStatus) },
        onSaveClicked = { viewModel.processDataTable() },
        onFieldChanged = viewModel::updateFieldValue,
    )
}

@Composable
fun DataTableListScreen(
    uiState: DataTableListUiState,
    dataTableList: List<DataTableEntity>,
    onBackPressed: () -> Unit,
    clientCreated: (ClientPayloadEntity) -> Unit,
    onSaveClicked: () -> Unit,
    onFieldChanged: (tableIndex: Int, columnName: String, value: Any) -> Unit,
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
                onFieldChanged = onFieldChanged,
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
                        // Initial Success() emitted by initArgs has no message and no client —
                        // it's the "ready to edit" signal, not a completion event.
                        if (uiState.message != null) {
                            val message = stringResource(uiState.message)
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
}

@Composable
fun DataTableListContent(
    dataTableList: List<DataTableEntity>,
    onSaveClicked: () -> Unit,
    onFieldChanged: (tableIndex: Int, columnName: String, value: Any) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState),
    ) {
        dataTableList.forEachIndexed { index, table ->
            Text(
                text = table.registeredTableName ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            TableColumnHeader(
                table = table,
                onFieldChanged = { columnName, value ->
                    onFieldChanged(index, columnName, value)
                },
            )
        }

        Button(
            onClick = { onSaveClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
            colors = ButtonDefaults.buttonColors(containerColor = KptTheme.colorScheme.primary),
        ) {
            Text(
                text = stringResource(Res.string.feature_data_table_save),
                color = KptTheme.colorScheme.onPrimary,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun TableColumnHeader(
    table: DataTableEntity,
    onFieldChanged: (columnName: String, value: Any) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // GAP-DT-004: filter to user-editable columns. `columnPrimaryKey == false`
        // skips Fineract system columns (e.g. loan_id, created_at, updated_at)
        // and `null` placeholders. The legacy filter `!= null` mistakenly kept
        // the primary-key column itself.
        table.columnHeaderData
            .filter { it.columnPrimaryKey == false }
            .forEach { columnHeader ->
                val name = columnHeader.dataTableColumnName ?: return@forEach
                when (columnHeader.columnDisplayType) {
                    DataTableColumnType.STRING, DataTableColumnType.TEXT -> {
                        // GAP-DT-006: stateful input — lift value to ViewModel via onFieldChanged.
                        var value by rememberSaveable(name) { mutableStateOf("") }
                        MifosOutlinedTextField(
                            value = value,
                            onValueChange = {
                                value = it
                                onFieldChanged(name, it)
                            },
                            label = name,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(modifier = Modifier.height(KptTheme.spacing.md))
                    }

                    DataTableColumnType.INTEGER, DataTableColumnType.DECIMAL,
                    DataTableColumnType.FLOAT,
                    -> {
                        var value by rememberSaveable(name) { mutableStateOf("") }
                        MifosOutlinedTextField(
                            value = value,
                            onValueChange = {
                                value = it
                                onFieldChanged(name, it)
                            },
                            label = name,
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = KptTheme.spacing.sm),
                        )

                        Spacer(modifier = Modifier.height(KptTheme.spacing.md))
                    }

                    DataTableColumnType.CODELOOKUP, DataTableColumnType.CODEVALUE -> {
                        var selectedValue by rememberSaveable(name) { mutableStateOf("") }
                        val columnValueStrings = columnHeader.columnValues.map { it.value.orEmpty() }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = KptTheme.spacing.sm),
                        ) {
                            MifosTextFieldDropdown(
                                value = selectedValue,
                                onValueChanged = {
                                    selectedValue = it
                                    onFieldChanged(name, it)
                                },
                                label = name,
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                options = columnValueStrings,
                                onOptionSelected = { _, item ->
                                    selectedValue = item
                                    onFieldChanged(name, item)
                                },
                            )
                        }

                        Spacer(modifier = Modifier.height(KptTheme.spacing.md))
                    }

                    DataTableColumnType.DATE, DataTableColumnType.DATETIME -> {
                        var showDatePicker by rememberSaveable(name) { mutableStateOf(false) }
                        var selectedDate by rememberSaveable(name) {
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
                                                onFieldChanged(
                                                    name,
                                                    DateHelper.getDateAsStringFromLong(it),
                                                )
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
                            label = name,
                            openDatePicker = {
                                showDatePicker = true
                            },
                        )
                        Spacer(modifier = Modifier.height(KptTheme.spacing.md))
                    }

                    DataTableColumnType.BOOLEAN -> {
                        var checked by rememberSaveable(name) { mutableStateOf(false) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = KptTheme.spacing.sm),
                        ) {
                            Text(
                                text = name,
                                modifier = Modifier.weight(1f),
                            )

                            Switch(
                                checked = checked,
                                onCheckedChange = {
                                    checked = it
                                    onFieldChanged(name, it)
                                },
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
        onFieldChanged = { _, _, _ -> },
    )
}
