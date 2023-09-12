package com.mifos.mifosxdroid.online.datatabledata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import org.apache.fineract.client.models.DeleteDataTablesDatatableAppTableIdDatatableIdResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class DataTableDataViewModel @Inject constructor(private val repository: DataTableDataRepository) :
    ViewModel() {

    private val _dataTableDataUiState = MutableLiveData<DataTableDataUiState>()

    val dataTableDataUiState: LiveData<DataTableDataUiState>
        get() = _dataTableDataUiState

    fun loadDataTableInfo(table: String?, entityId: Int) {
        _dataTableDataUiState.value = DataTableDataUiState.ShowProgressbar
        repository.getDataTableInfo(table, entityId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<JsonArray>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _dataTableDataUiState.value =
                        DataTableDataUiState.ShowFetchingError(R.string.failed_to_fetch_datatable_info)
                }

                override fun onNext(jsonElements: JsonArray) {
                    if (jsonElements.size() == 0) {
                        _dataTableDataUiState.value = DataTableDataUiState.ShowEmptyDataTable
                    } else {
                        _dataTableDataUiState.value =
                            DataTableDataUiState.ShowDataTableInfo(jsonElements)
                    }
                }
            })
    }

    fun deleteDataTableEntry(table: String?, entity: Int, rowId: Int) {
        _dataTableDataUiState.value = DataTableDataUiState.ShowProgressbar
        repository.deleteDataTableEntry(table, entity, rowId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<DeleteDataTablesDatatableAppTableIdDatatableIdResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _dataTableDataUiState.value =
                        DataTableDataUiState.ShowFetchingErrorString(e.message.toString())
                }

                override fun onNext(genericResponse: DeleteDataTablesDatatableAppTableIdDatatableIdResponse) {
                    _dataTableDataUiState.value =
                        DataTableDataUiState.ShowDataTableDeletedSuccessfully
                }
            })

    }
}