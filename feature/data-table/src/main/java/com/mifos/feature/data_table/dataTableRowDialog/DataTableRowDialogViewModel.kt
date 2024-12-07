package com.mifos.feature.data_table.dataTableRowDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.useCases.AddDataTableEntryUseCase
import com.mifos.feature.data_table.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataTableRowDialogViewModel @Inject constructor(
    private val addDataTableEntryUseCase: AddDataTableEntryUseCase
) : ViewModel() {

    private val _dataTableRowDialogUiState =
        MutableStateFlow<DataTableRowDialogUiState>(DataTableRowDialogUiState.Initial)
    val dataTableRowDialogUiState = _dataTableRowDialogUiState.asStateFlow()


    fun addDataTableEntry(table: String, entityId: Int, payload: Map<String, String>) =
        viewModelScope.launch(Dispatchers.IO) {
            addDataTableEntryUseCase(table, entityId, payload).collect { result ->
                when (result) {
                    is Resource.Error -> _dataTableRowDialogUiState.value =
                        DataTableRowDialogUiState.Error(
                            R.string.feature_data_table_failed_to_add_data_table
                        )

                    is Resource.Loading -> _dataTableRowDialogUiState.value =
                        DataTableRowDialogUiState.Loading

                    is Resource.Success -> _dataTableRowDialogUiState.value =
                        DataTableRowDialogUiState.DataTableEntrySuccessfully
                }
            }
        }
}