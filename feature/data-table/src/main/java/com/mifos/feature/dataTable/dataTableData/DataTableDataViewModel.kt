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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.useCases.DeleteDataTableEntryUseCase
import com.mifos.core.domain.useCases.GetDataTableInfoUseCase
import com.mifos.core.objects.navigation.DataTableDataNavigationArg
import com.mifos.feature.data_table.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataTableDataViewModel @Inject constructor(
    private val getDataTableInfoUseCase: GetDataTableInfoUseCase,
    private val deleteDataTableEntryUseCase: DeleteDataTableEntryUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args =
        savedStateHandle.getStateFlow(key = Constants.DATA_TABLE_DATA_NAV_DATA, initialValue = "")
    val arg: DataTableDataNavigationArg =
        Gson().fromJson(args.value, DataTableDataNavigationArg::class.java)

    private val _dataTableDataUiState =
        MutableStateFlow<DataTableDataUiState>(DataTableDataUiState.Loading)
    val dataTableDataUiState = _dataTableDataUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshDataTableData(table: String, entityId: Int) {
        _isRefreshing.value = true
        loadDataTableInfo(table = table, entityId = entityId)
        _isRefreshing.value = false
    }

    fun loadDataTableInfo(table: String, entityId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            getDataTableInfoUseCase(table, entityId).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _dataTableDataUiState.value =
                            DataTableDataUiState.Error(R.string.feature_data_table_failed_to_load_data_table_details)

                    is Resource.Loading ->
                        _dataTableDataUiState.value =
                            DataTableDataUiState.Loading

                    is Resource.Success ->
                        _dataTableDataUiState.value =
                            DataTableDataUiState.DataTableInfo(result.data ?: JsonArray())
                }
            }
        }

    fun deleteDataTableEntry(table: String, entity: Int, rowId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            deleteDataTableEntryUseCase(table, entity, rowId).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _dataTableDataUiState.value =
                            DataTableDataUiState.Error(R.string.feature_data_table_failed_to_delete_data_table)

                    is Resource.Loading ->
                        _dataTableDataUiState.value =
                            DataTableDataUiState.Loading

                    is Resource.Success ->
                        _dataTableDataUiState.value =
                            DataTableDataUiState.DataTableDeletedSuccessfully
                }
            }
        }
}
