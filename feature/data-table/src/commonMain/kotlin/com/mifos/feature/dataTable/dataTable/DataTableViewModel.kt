/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTable

import androidclient.feature.data_table.generated.resources.Res
import androidclient.feature.data_table.generated.resources.feature_data_table_something_went_wrong
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.DataTableRepository
import com.mifos.core.model.objects.nav.DataTableNavigationArg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class DataTableViewModel(
    private val repository: DataTableRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val arg =
        savedStateHandle.getStateFlow(Constants.DATA_TABLE_NAV_DATA, initialValue = "")
    val args = Json.decodeFromString<DataTableNavigationArg>(arg.value)

    private val _dataTableUiState =
        MutableStateFlow<DataTableUiState>(DataTableUiState.ShowProgressbar)
    val dataTableUiState: StateFlow<DataTableUiState> get() = _dataTableUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch {
            loadDataTable(args.tableName)
        }
    }

    fun refresh(tableName: String?) {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            loadDataTable(tableName)
            _isRefreshing.value = false
        }
    }

    suspend fun loadDataTable(tableName: String?) {
        repository.getDataTable(tableName)
            .collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        _dataTableUiState.value =
                            DataTableUiState.ShowError(Res.string.feature_data_table_something_went_wrong)
                    }

                    DataState.Loading ->
                        _dataTableUiState.value =
                            DataTableUiState.ShowProgressbar

                    is DataState.Success -> {
                        val result = dataState.data
                        _dataTableUiState.value = if (result.isEmpty()) {
                            DataTableUiState.ShowEmptyDataTables
                        } else {
                            DataTableUiState.ShowDataTables(result)
                        }
                    }
                }
            }
    }
}
