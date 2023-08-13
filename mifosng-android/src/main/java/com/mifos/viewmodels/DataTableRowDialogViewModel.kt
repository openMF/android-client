package com.mifos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.api.GenericResponse
import com.mifos.repositories.DataTableRowDialogRepository
import com.mifos.states.DataTableRowDialogUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class DataTableRowDialogViewModel @Inject constructor(private val repository: DataTableRowDialogRepository) :
    ViewModel() {

    private val _dataTableRowDialogUiState = MutableLiveData<DataTableRowDialogUiState>()

    val dataTableRowDialogUiState: LiveData<DataTableRowDialogUiState>
        get() = _dataTableRowDialogUiState

    fun addDataTableEntry(table: String?, entityId: Int, payload: Map<String, String>) {
//        checkViewAttached()
//        mvpView?.showProgressbar(true)
        _dataTableRowDialogUiState.value = DataTableRowDialogUiState.ShowProgressbar
        repository.addDataTableEntry(table, entityId, payload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {
//                    mvpView?.showProgressbar(false)
                }

                override fun onError(e: Throwable) {
//                    mvpView?.showProgressbar(false)
//                    mvpView?.showError(MFErrorParser.errorMessage(e)!!)
                    _dataTableRowDialogUiState.value =
                        DataTableRowDialogUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: GenericResponse) {
//                    mvpView?.showProgressbar(false)
//                    mvpView?.showDataTableEntrySuccessfully(genericResponse)
                    _dataTableRowDialogUiState.value =
                        DataTableRowDialogUiState.ShowDataTableEntrySuccessfully(genericResponse)
                }
            })

    }
}