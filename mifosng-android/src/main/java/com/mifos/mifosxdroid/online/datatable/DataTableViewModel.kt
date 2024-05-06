package com.mifos.mifosxdroid.online.datatable

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.noncore.DataTable
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _dataTableUiState = MutableLiveData<DataTableUiState>()

    val dataTableUiState: LiveData<DataTableUiState>
        get() = _dataTableUiState

    fun loadDataTable(tableName: String?) {
        _dataTableUiState.value = DataTableUiState.ShowProgressbar
        _dataTableUiState.value = DataTableUiState.ShowResetVisibility
        repository.getDataTable(tableName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<DataTable>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _dataTableUiState.value =
                        DataTableUiState.ShowError(R.string.failed_to_fetch_datatable)
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