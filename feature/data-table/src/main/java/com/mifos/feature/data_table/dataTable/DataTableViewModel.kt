package com.mifos.feature.data_table.dataTable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.DataTableRepository
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
class DataTableViewModel @Inject constructor(private val repository: DataTableRepository) :
    ViewModel() {

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

