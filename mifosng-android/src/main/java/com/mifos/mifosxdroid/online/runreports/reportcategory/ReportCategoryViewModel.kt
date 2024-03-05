package com.mifos.mifosxdroid.online.runreports.reportcategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.runreports.client.ClientReportTypeItem
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
@HiltViewModel
class ReportCategoryViewModel @Inject constructor(private val repository: ReportCategoryRepository) :
    ViewModel() {

    private val _reportCategoryUiState = MutableLiveData<ReportCategoryUiState>()

    val reportCategoryUiState: LiveData<ReportCategoryUiState>
        get() = _reportCategoryUiState

    fun fetchCategories(
        reportCategory: String?,
        genericResultSet: Boolean,
        parameterType: Boolean
    ) {
        _reportCategoryUiState.value = ReportCategoryUiState.ShowProgressbar
        repository.getReportCategories(
            reportCategory, genericResultSet,
            parameterType
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<ClientReportTypeItem>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _reportCategoryUiState.value = errorMessage?.let {
                                ReportCategoryUiState.ShowError(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(clientReportTypeItems: List<ClientReportTypeItem>) {
                    _reportCategoryUiState.value =
                        ReportCategoryUiState.ShowReportCategories(clientReportTypeItems)
                }
            })

    }

    /**
     * Method to filter unique ClientReportTypeItems from the given list
     *
     * @param list List of ClientReportTypeItems retrieved from server
     * @return List of ClientReportTypeItems with unique items based on their Id.
     */
    private fun filterUniques(list: List<ClientReportTypeItem>): ArrayList<ClientReportTypeItem> {
        val map: MutableMap<Int?, ClientReportTypeItem> = LinkedHashMap()
        for (item in list) {
            map[item.report_id] = item
        }
        val uniques = ArrayList<ClientReportTypeItem>()
        uniques.addAll(map.values)
        return uniques
    }
}