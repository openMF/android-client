package com.mifos.mifosxdroid.online.collectionsheetindividual

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class NewIndividualCollectionSheetViewModel @Inject constructor(private val repository: NewIndividualCollectionSheetRepository) :
    ViewModel() {

    private val _newIndividualCollectionSheetUiState =
        MutableLiveData<NewIndividualCollectionSheetUiState>()

    val newIndividualCollectionSheetUiState: LiveData<NewIndividualCollectionSheetUiState>
        get() = _newIndividualCollectionSheetUiState

    fun fetchIndividualCollectionSheet(requestCollectionSheetPayload: RequestCollectionSheetPayload?) {
        _newIndividualCollectionSheetUiState.value =
            NewIndividualCollectionSheetUiState.ShowProgressbar

        repository.getIndividualCollectionSheet(requestCollectionSheetPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<IndividualCollectionSheet>() {
                override fun onCompleted() {
                    _newIndividualCollectionSheetUiState.value =
                        NewIndividualCollectionSheetUiState.ShowSuccess
                }

                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        try {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _newIndividualCollectionSheetUiState.value =
                                NewIndividualCollectionSheetUiState.ShowError(errorMessage)
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler.handleError(e)
                        }
                    } else {
                        _newIndividualCollectionSheetUiState.value =
                            NewIndividualCollectionSheetUiState.ShowError(e.localizedMessage)
                    }
                }

                override fun onNext(individualCollectionSheet: IndividualCollectionSheet) {
                    if (individualCollectionSheet.clients?.size!! > 0) {
                        _newIndividualCollectionSheetUiState.value =
                            NewIndividualCollectionSheetUiState.ShowSheet(individualCollectionSheet)
                    } else {
                        _newIndividualCollectionSheetUiState.value =
                            NewIndividualCollectionSheetUiState.ShowNoSheetFound
                    }
                }
            })
    }

    fun fetchOffices() {
        _newIndividualCollectionSheetUiState.value =
            NewIndividualCollectionSheetUiState.ShowProgressbar
        repository.offices()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Office>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _newIndividualCollectionSheetUiState.value =
                                NewIndividualCollectionSheetUiState.ShowError(errorMessage)
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(officeList: List<Office>) {
                    _newIndividualCollectionSheetUiState.value =
                        NewIndividualCollectionSheetUiState.SetOfficeSpinner(officeList)
                }
            })
    }

    fun fetchStaff(officeId: Int) {
        _newIndividualCollectionSheetUiState.value =
            NewIndividualCollectionSheetUiState.ShowProgressbar
        repository.getStaffInOffice(officeId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Staff>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _newIndividualCollectionSheetUiState.value =
                                NewIndividualCollectionSheetUiState.ShowError(errorMessage)
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(staffList: List<Staff>) {
                    _newIndividualCollectionSheetUiState.value =
                        NewIndividualCollectionSheetUiState.SetStaffSpinner(staffList)
                }
            })

    }

    fun filterOffices(offices: List<Office>): List<String> {
        val officesList: MutableList<String> = ArrayList()
        Observable.from(offices)
            .subscribe { office -> office.name?.let { officesList.add(it) } }
        return officesList
    }

    fun filterStaff(staffs: List<Staff>): List<String> {
        val staffList: MutableList<String> = ArrayList()
        Observable.from(staffs)
            .subscribe { staff -> staff.displayName?.let { staffList.add(it) } }
        return staffList
    }

}