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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.AddDataTableEntryUseCase
import com.mifos.feature.data_table.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataTableRowDialogViewModel @Inject constructor(
    private val addDataTableEntryUseCase: AddDataTableEntryUseCase,
) : ViewModel() {

    private val _dataTableRowDialogUiState =
        MutableStateFlow<DataTableRowDialogUiState>(DataTableRowDialogUiState.Initial)
    val dataTableRowDialogUiState = _dataTableRowDialogUiState.asStateFlow()

    fun addDataTableEntry(table: String, entityId: Int, payload: Map<String, String>) =
        viewModelScope.launch(Dispatchers.IO) {
            addDataTableEntryUseCase(table, entityId, payload).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _dataTableRowDialogUiState.value =
                            DataTableRowDialogUiState.Error(
                                R.string.feature_data_table_failed_to_add_data_table,
                            )

                    is Resource.Loading ->
                        _dataTableRowDialogUiState.value =
                            DataTableRowDialogUiState.Loading

                    is Resource.Success ->
                        _dataTableRowDialogUiState.value =
                            DataTableRowDialogUiState.DataTableEntrySuccessfully
                }
            }
        }
}
