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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.DataTableRepository
import com.mifos.core.objects.navigation.DataTableNavigationArg
import com.mifos.core.objects.noncore.DataTable
import com.mifos.feature.data_table.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class DataTableViewModel @Inject constructor(
    private val repository: DataTableRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val arg =
        savedStateHandle.getStateFlow(Constants.DATA_TABLE_NAV_DATA, initialValue = "")
    val args = Gson().fromJson(arg.value, DataTableNavigationArg::class.java)

    private val _dataTableUiState =
        MutableStateFlow<DataTableUiState>(DataTableUiState.ShowProgressbar)
    val dataTableUiState: StateFlow<DataTableUiState> get() = _dataTableUiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    fun refresh(tableName: String?) {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            loadDataTable(tableName)
            _isRefreshing.value = false
        }
    }

    fun loadDataTable(tableName: String?) {
        _dataTableUiState.value = DataTableUiState.ShowProgressbar
        repository.getDataTable(tableName).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).subscribe(object : Subscriber<List<DataTable>>() {
                override fun onCompleted() {}

                override fun onError(e: Throwable) {
                    _dataTableUiState.value =
                        DataTableUiState.ShowError(R.string.feature_data_table_failed_to_fetch_data_table)
                }

                override fun onNext(dataTables: List<DataTable>) {
                    if (dataTables.isNotEmpty()) {
                        _dataTableUiState.value = DataTableUiState.ShowDataTables(dataTables)
                    } else {
                        _dataTableUiState.value = DataTableUiState.ShowEmptyDataTables
                    }
                }
            })
    }
}
