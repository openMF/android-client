package com.mifos.mifosxdroid.dialogfragments.datatablerowdialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.GenericResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
@HiltViewModel
class DataTableRowDialogViewModel @Inject constructor(private val repository: DataTableRowDialogRepository) :
    ViewModel() {

    private val _dataTableRowDialogUiState = MutableLiveData<DataTableRowDialogUiState>()

    val dataTableRowDialogUiState: LiveData<DataTableRowDialogUiState>
        get() = _dataTableRowDialogUiState

    fun addDataTableEntry(table: String?, entityId: Int, payload: Map<String, String>) {
        _dataTableRowDialogUiState.value = DataTableRowDialogUiState.ShowProgressbar
        repository.addDataTableEntry(table, entityId, payload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _dataTableRowDialogUiState.value =
                        DataTableRowDialogUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: GenericResponse) {
                    _dataTableRowDialogUiState.value =
                        DataTableRowDialogUiState.ShowDataTableEntrySuccessfully(genericResponse)
                }
            })

    }
}