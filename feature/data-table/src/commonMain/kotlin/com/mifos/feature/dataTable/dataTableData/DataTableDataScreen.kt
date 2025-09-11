/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTableData

import androidclient.feature.data_table.generated.resources.Res
import androidclient.feature.data_table.generated.resources.feature_data_table_client_id
import androidclient.feature.data_table.generated.resources.feature_data_table_data_id
import androidclient.feature.data_table.generated.resources.feature_data_table_data_table_created_successfully
import androidclient.feature.data_table.generated.resources.feature_data_table_delete_data_table
import androidclient.feature.data_table.generated.resources.feature_data_table_failed_to_delete_data_table
import androidclient.feature.data_table.generated.resources.feature_data_table_no_data_table_details_to_show
import androidclient.feature.data_table.generated.resources.feature_data_table_select_options
import androidclient.feature.data_table.generated.resources.feature_data_table_title
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.feature.dataTable.dataTableRowDialog.DataTableRowDialogScreen
import com.mifos.room.entities.noncore.DataTableEntity
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DataTableDataScreen(
    viewModel: DataTableDataViewModel = koinViewModel(),
    onBackPressed: () -> Unit,
) {
    val dataTable = viewModel.arg.dataTable
    val entityId = viewModel.arg.entityId
    val table = viewModel.arg.tableName
    val state by viewModel.dataTableDataUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    DataTableDataScreen(
        dataTable = dataTable,
        entityId = entityId,
        state = state,
        onBackPressed = onBackPressed,
        onRetry = {
            viewModel.loadDataTableInfo(table, entityId)
        },
        onRefresh = {
            viewModel.refreshDataTableData(table, entityId)
        },
        refreshState = isRefreshing,
        deleteDataTable = {
            viewModel.deleteDataTableEntry(table, entityId, it)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataTableDataScreen(
    dataTable: DataTableEntity,
    entityId: Int,
    state: DataTableDataUiState,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    refreshState: Boolean,
    deleteDataTable: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()
    var showOptionDialog by rememberSaveable { mutableStateOf(false) }
    var deleteDataTableId by rememberSaveable { mutableIntStateOf(0) }
    var showAddDataTableRowDialog by rememberSaveable { mutableStateOf(false) }

    if (showAddDataTableRowDialog) {
        DataTableRowDialogScreen(
            snackbarHostState = snackbarHostState,
            dataTable = dataTable,
            entityId = entityId,
            onDismiss = {
                showAddDataTableRowDialog = false
            },
            onSuccess = {
                showAddDataTableRowDialog = false
                onRetry()
            },
        )
    }

    if (showOptionDialog) {
        SelectOptionsDialog(
            onDismissRequest = { showOptionDialog = false },
            deleteDataTable = {
                deleteDataTable(deleteDataTableId)
                showOptionDialog = false
                onRetry()
            },
        )
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_data_table_title),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(
                onClick = {
                    showAddDataTableRowDialog = true
                },
            ) {
                Icon(imageVector = MifosIcons.Add, contentDescription = null)
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues)) {
            PullToRefreshBox(
                state = pullRefreshState,
                isRefreshing = refreshState,
                onRefresh = onRefresh,
            ) {
                when (state) {
                    is DataTableDataUiState.DataTableDeletedSuccessfully -> {
                        val errorMessage = stringResource(
                            (Res.string.feature_data_table_data_table_created_successfully),
                        )
                        scope.launch {
                            snackbarHostState.showSnackbar(message = errorMessage)
                        }
                        onBackPressed()
                    }

                    is DataTableDataUiState.DataTableInfo -> {
                        DataTableDataContent(
                            jsonElements = state.jsonElements,
                            onDataClicked = {
                                showOptionDialog = true
                                deleteDataTableId = it
                            },
                        )
                    }

                    is DataTableDataUiState.Error -> MifosSweetError(message = stringResource(state.message)) {
                        onRetry()
                    }

                    is DataTableDataUiState.Loading -> MifosProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun DataTableDataContent(
    jsonElements: JsonArray,
    onDataClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val jsonElementIterator: Iterator<JsonElement> = jsonElements.iterator()
    var dataList by rememberSaveable { mutableStateOf<List<DataTableDataItem>>(emptyList()) }

    if (jsonElements.size == 0) {
        MifosEmptyUi(
            text = stringResource(Res.string.feature_data_table_no_data_table_details_to_show),
            modifier = modifier,
        )
    } else {
        while (jsonElementIterator.hasNext()) {
            val dataTableDataItem = DataTableDataItem()
            val jsonElement: JsonElement = jsonElementIterator.next()

            if (jsonElement.jsonObject.containsKey("client_id")) {
                dataTableDataItem.clientId = jsonElement.jsonObject["client_id"].toString()
            }
            if (jsonElement.jsonObject.containsKey("id")) {
                dataTableDataItem.id = jsonElement.jsonObject["id"].toString()
            }
            dataList = dataList + dataTableDataItem
        }

        LazyColumn(modifier = modifier) {
            items(dataList) { dataItem ->
                DataTableDataCardItem(
                    dataItem = dataItem,
                    onDataClicked = onDataClicked,
                )
            }
        }
    }
}

@Composable
fun DataTableDataCardItem(
    dataItem: DataTableDataItem,
    onDataClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier
            .padding(8.dp)
            .clickable {
                onDataClicked(dataItem.clientId?.toInt() ?: dataItem.id?.toInt() ?: 0)
            },
        colors = CardDefaults.cardColors(White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(Res.string.feature_data_table_client_id),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = Black,
                        ),
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        text = dataItem.clientId ?: "-",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = DarkGray,
                        ),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(Res.string.feature_data_table_data_id),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = Black,
                        ),
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = dataItem.id ?: "-",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = DarkGray,
                        ),
                    )
                }
            }
        }
    }
}

data class DataTableDataItem(
    var id: String? = null,
    var clientId: String? = null,
)

class DataTableDataUiStateProvider : PreviewParameterProvider<DataTableDataUiState> {

    override val values: Sequence<DataTableDataUiState>
        get() = sequenceOf(
            DataTableDataUiState.Loading,
            DataTableDataUiState.Error(Res.string.feature_data_table_failed_to_delete_data_table),
            DataTableDataUiState.DataTableInfo(Json.parseToJsonElement(values.toString()).jsonArray),
        )
}

@Composable
fun SelectOptionsDialog(
    onDismissRequest: () -> Unit,
    deleteDataTable: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Card(
            colors = CardDefaults.cardColors(White),
            shape = RoundedCornerShape(20.dp),
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier
                    .padding(30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.feature_data_table_select_options),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { deleteDataTable() },
                ) {
                    Text(
                        text = stringResource(Res.string.feature_data_table_delete_data_table),
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DataTableDataScreenPreview(
    @PreviewParameter(DataTableDataUiStateProvider::class) state: DataTableDataUiState,
) {
    DataTableDataScreen(
        dataTable = DataTableEntity(),
        entityId = 1,
        state = state,
        onBackPressed = {},
        onRetry = {},
        onRefresh = {},
        refreshState = false,
        deleteDataTable = {},
    )
}
