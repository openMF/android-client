package com.mifos.feature.data_table.dataTable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.DataTableRepository
import com.mifos.core.domain.use_cases.GetDataTableUseCase
import com.mifos.core.objects.noncore.DataTable
import com.mifos.feature.data_table.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val getDataTableUseCase: GetDataTableUseCase
) : ViewModel() {

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

    fun loadDataTable(tableName: String?) = viewModelScope.launch(Dispatchers.IO) {
        getDataTableUseCase(tableName).collect { result ->
            when (result) {
                is Resource.Error -> _dataTableUiState.value =
                    DataTableUiState.ShowError(R.string.feature_data_table_failed_to_fetch_datatable)

                is Resource.Loading -> _dataTableUiState.value = DataTableUiState.ShowProgressbar

                is Resource.Success -> {
                    val dataTables = result.data ?: emptyList()
                    if (dataTables.isNotEmpty()) {
                        _dataTableUiState.value = DataTableUiState.ShowDataTables(dataTables)
                    } else {
                        _dataTableUiState.value = DataTableUiState.ShowEmptyDataTables
                    }
                }
            }
        }
    }

}

