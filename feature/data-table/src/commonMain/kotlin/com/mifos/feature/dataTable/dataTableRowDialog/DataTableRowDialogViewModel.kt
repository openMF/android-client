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

import androidclient.feature.data_table.generated.resources.Res
import androidclient.feature.data_table.generated.resources.feature_data_table_failed_to_add_data_table
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.AddDataTableEntryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class DataTableRowDialogViewModel(
    private val addDataTableEntryUseCase: AddDataTableEntryUseCase,
) : ViewModel() {

    private val _dataTableRowDialogUiState =
        MutableStateFlow<DataTableRowDialogUiState>(DataTableRowDialogUiState.Initial)
    val dataTableRowDialogUiState = _dataTableRowDialogUiState.asStateFlow()

    fun addDataTableEntry(
        table: String,
        entityId: Int,
        payload: Map<String, String>,
    ) {
        viewModelScope.launch {
            val result = addDataTableEntryUseCase(table, entityId, payload)
            _dataTableRowDialogUiState.value = when (result) {
                is DataState.Success -> DataTableRowDialogUiState.DataTableEntrySuccessfully
                is DataState.Error -> DataTableRowDialogUiState.Error(
                    getString(Res.string.feature_data_table_failed_to_add_data_table),
                )
                is DataState.Loading -> DataTableRowDialogUiState.Loading
            }
        }
    }
}
