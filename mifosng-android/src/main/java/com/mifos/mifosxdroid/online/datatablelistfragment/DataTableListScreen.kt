package com.mifos.mifosxdroid.online.datatablelistfragment

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.icon.MifosIcons.ArrowDropDown
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.noncore.DataTable
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.formwidgets.FormWidget
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DataTableListScreen(
    viewModel: DataTableListViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    clientCreated: (Client) -> Unit
) {
    val uiState by viewModel.dataTableListUiState.collectAsStateWithLifecycle()
    val dataTableList by viewModel.dataTableList.collectAsStateWithLifecycle()

    DataTableListScreen(
        uiState = uiState,
        dataTableList = dataTableList ?: listOf(),
        onBackPressed = onBackPressed,
        clientCreated = clientCreated,
        onSaveClicked = { viewModel.processDataTable() }
    )
}

@Composable
fun DataTableListScreen(
    uiState: DataTableListUiState,
    dataTableList: List<DataTable>,
    onBackPressed: () -> Unit,
    clientCreated: (Client) -> Unit,
    onSaveClicked: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.associated_datatables),
        onBackPressed = onBackPressed,
        snackbarHostState = snackBarHostState
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            DataTableListContent(
                dataTableList = dataTableList,
                onSaveClicked = onSaveClicked
            )

            when (uiState) {
                is DataTableListUiState.ShowMessage -> {
                    val message = when {
                        uiState.messageResId != null -> stringResource(id = uiState.messageResId)
                        uiState.message != null -> uiState.message
                        else -> stringResource(id = R.string.something_went_wrong)
                    }
                    LaunchedEffect(key1 = message) {
                        snackBarHostState.showSnackbar(message = message)
                    }
                }

                is DataTableListUiState.Loading -> MifosCircularProgress()
                is DataTableListUiState.Success -> {
                    uiState.client?.let { client ->
                        clientCreated(client)
                    } ?: run {
                        val message = when {
                            uiState.messageResId != null -> stringResource(id = uiState.messageResId)
                            else -> stringResource(id = R.string.something_went_wrong)
                        }
                        LaunchedEffect(key1 = message) {
                            snackBarHostState.showSnackbar(message = message)
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
    dataTableList: List<DataTable>,
    onSaveClicked: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
    ) {
        for (table in dataTableList) {
            Text(
                text = table.registeredTableName ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TableColumnHeader(table = table)
        }

        Button(
            onClick = { onSaveClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = stringResource(id = R.string.save), color = Color.White)
        }
    }
}

@Composable
fun TableColumnHeader(
    table: DataTable,
) {
    val context = LocalContext.current
    Column {
        table.columnHeaderData.filter { it.columnPrimaryKey != null }.forEach { columnHeader ->
            when (columnHeader.columnDisplayType) {
                FormWidget.SCHEMA_KEY_STRING, FormWidget.SCHEMA_KEY_TEXT -> {
                    MifosOutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = columnHeader.dataTableColumnName ?: "",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                FormWidget.SCHEMA_KEY_INT, FormWidget.SCHEMA_KEY_DECIMAL -> {
                    MifosOutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = columnHeader.dataTableColumnName ?: "",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                FormWidget.SCHEMA_KEY_CODELOOKUP, FormWidget.SCHEMA_KEY_CODEVALUE -> {
                    var selectedValue by remember { mutableStateOf("") }
                    val columnValueStrings =
                        columnHeader.columnValues.map { it.value.orEmpty() }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        MifosTextFieldDropdown(
                            value = selectedValue,
                            onValueChanged = { selectedValue = it },
                            labelString = columnHeader.dataTableColumnName,
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            options = columnValueStrings,
                            onOptionSelected = { _, item -> selectedValue = item }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                FormWidget.SCHEMA_KEY_DATE -> {
                    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    var selectedDate by remember {
                        mutableStateOf(
                            LocalDate.now().format(dateFormatter)
                        )
                    }

                    fun openDatePicker() {
                        val datePickerDialog = DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val newDate = LocalDate.of(year, month + 1, dayOfMonth)
                                selectedDate = newDate.format(dateFormatter)
                            },
                            LocalDate.now().year,
                            LocalDate.now().monthValue - 1,
                            LocalDate.now().dayOfMonth
                        )
                        datePickerDialog.show()
                    }

                    MifosDatePickerTextField(
                        value = selectedDate,
                        labelString = columnHeader.dataTableColumnName ?: "",
                        openDatePicker = ::openDatePicker
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                FormWidget.SCHEMA_KEY_BOOL -> {
                    var checked by remember { mutableStateOf(false) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = columnHeader.dataTableColumnName ?: "",
                            modifier = Modifier.weight(1f)
                        )

                        Switch(
                            checked = checked,
                            onCheckedChange = { checked = it }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DataTableListScreenPreview() {
    DataTableListScreen(
        uiState = DataTableListUiState.Success(),
        dataTableList = listOf(),
        onBackPressed = { },
        clientCreated = { },
        onSaveClicked = { }
    )
}