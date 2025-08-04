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
import androidclient.feature.data_table.generated.resources.feature_data_table_failed_to_delete_data_table
import androidclient.feature.data_table.generated.resources.feature_data_table_failed_to_load_data_table_details
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.DeleteDataTableEntryUseCase
import com.mifos.core.domain.useCases.GetDataTableInfoUseCase
import com.mifos.room.entities.navigation.DataTableDataNavigationArg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray

class DataTableDataViewModel(
    private val getDataTableInfoUseCase: GetDataTableInfoUseCase,
    private val deleteDataTableEntryUseCase: DeleteDataTableEntryUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args =
        savedStateHandle.getStateFlow(key = Constants.DATA_TABLE_DATA_NAV_DATA, initialValue = "")
    val arg: DataTableDataNavigationArg =
        Json.decodeFromString<DataTableDataNavigationArg>(args.value)

    private val _dataTableDataUiState =
        MutableStateFlow<DataTableDataUiState>(DataTableDataUiState.Loading)
    val dataTableDataUiState = _dataTableDataUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        loadDataTableInfo(arg.tableName, arg.entityId)
    }

    fun refreshDataTableData(table: String, entityId: Int) {
        _isRefreshing.value = true
        loadDataTableInfo(table, entityId)
        _isRefreshing.value = false
    }

    fun loadDataTableInfo(table: String, entityId: Int) =
        viewModelScope.launch {
            getDataTableInfoUseCase(table, entityId)
                .collect { result ->
                    when (result) {
                        is DataState.Error -> {
                            _dataTableDataUiState.value =
                                DataTableDataUiState.Error(
                                    Res.string.feature_data_table_failed_to_load_data_table_details,
                                )
                        }

                        DataState.Loading -> {
                            _dataTableDataUiState.value =
                                DataTableDataUiState.Loading
                        }

                        is DataState.Success -> {
                            _dataTableDataUiState.value =
                                DataTableDataUiState.DataTableInfo(Json.parseToJsonElement(result.data.toString()).jsonArray)
                        }
                    }
                }
        }

    fun deleteDataTableEntry(table: String, entity: Int, rowId: Int) =
        viewModelScope.launch {
            deleteDataTableEntryUseCase(table, entity, rowId).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _dataTableDataUiState.value =
                            DataTableDataUiState.Error(
                                Res.string.feature_data_table_failed_to_delete_data_table,
                            )

                    is DataState.Loading ->
                        _dataTableDataUiState.value =
                            DataTableDataUiState.Loading

                    is DataState.Success ->
                        _dataTableDataUiState.value =
                            DataTableDataUiState.DataTableDeletedSuccessfully
                }
            }
        }
}
