package com.mifos.mifosxdroid.online.runreports.reportdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.runreports.DataRow
import com.mifos.core.objects.runreports.FullParameterListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
@HiltViewModel
class ReportDetailViewModel @Inject constructor(private val repository: ReportDetailRepository) :
    ViewModel() {

    private val _reportDetailUiState = MutableLiveData<ReportDetailUiState>()

    val reportDetailUiState: LiveData<ReportDetailUiState>
        get() = _reportDetailUiState

    fun fetchFullParameterList(reportName: String?, parameterType: Boolean) {
        _reportDetailUiState.value = ReportDetailUiState.ShowProgressbar
        repository.getReportFullParameterList(reportName, parameterType)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<FullParameterListResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _reportDetailUiState.value = errorMessage?.let {
                                ReportDetailUiState.ShowError(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(response: FullParameterListResponse) {
                    _reportDetailUiState.value =
                        ReportDetailUiState.ShowFullParameterResponse(response)
                }
            })

    }

    fun fetchParameterDetails(parameterName: String?, parameterType: Boolean) {
        _reportDetailUiState.value = ReportDetailUiState.ShowProgressbar
        repository.getReportParameterDetails(parameterName, parameterType)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<FullParameterListResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _reportDetailUiState.value = errorMessage?.let {
                                ReportDetailUiState.ShowError(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(response: FullParameterListResponse) {
                    _reportDetailUiState.value =
                        parameterName?.let {
                            ReportDetailUiState.ShowParameterDetails(
                                response,
                                it
                            )
                        }
                }
            })

    }

    fun fetchOffices(parameterName: String?, officeId: Int, parameterType: Boolean) {
        _reportDetailUiState.value = ReportDetailUiState.ShowProgressbar
        repository.getRunReportOffices(parameterName, officeId, parameterType)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<FullParameterListResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _reportDetailUiState.value = errorMessage?.let {
                                ReportDetailUiState.ShowError(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(response: FullParameterListResponse) {
                    _reportDetailUiState.value =
                        parameterName?.let { ReportDetailUiState.ShowOffices(response, it) }
                }
            })

    }

    fun fetchProduct(parameterName: String?, currencyId: String?, parameterType: Boolean) {
        _reportDetailUiState.value = ReportDetailUiState.ShowProgressbar
        repository.getRunReportProduct(parameterName, currencyId, parameterType)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<FullParameterListResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _reportDetailUiState.value = errorMessage?.let {
                                ReportDetailUiState.ShowError(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(response: FullParameterListResponse) {
                    _reportDetailUiState.value =
                        parameterName?.let { ReportDetailUiState.ShowProduct(response, it) }
                }
            })

    }

    fun fetchRunReportWithQuery(reportName: String?, options: MutableMap<String?, String?>) {
        _reportDetailUiState.value = ReportDetailUiState.ShowProgressbar
        repository.getRunReportWithQuery(reportName, options)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<FullParameterListResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _reportDetailUiState.value = errorMessage?.let {
                                ReportDetailUiState.ShowError(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(response: FullParameterListResponse) {
                    _reportDetailUiState.value = ReportDetailUiState.ShowRunReport(response)
                }
            })

    }

    /**
     * Method to filter Values of spinners with associated integer codes
     *
     * @param response List of DataRows
     * @param values   List of corresponding String values to be shown in Spinner
     * @return HashMap of String values with the corresponding integer code
     */
    fun filterIntHashMapForSpinner(
        response: List<DataRow>?,
        values: ArrayList<String>
    ): HashMap<String, Int> {
        val map = HashMap<String, Int>()
        values.clear()
        Observable.from(response)
            .subscribe { dataRow ->
                val id = dataRow.row[0].toInt()
                val value = dataRow.row[1]
                values.add(value)
                map[value] = id
            }
        return map
    }

    /**
     * Method to filter out values for the Spinners.
     *
     * @param response List of DataRows
     * @param values   List of the corresponding values
     * @return HashMap of value and currency code pairs.
     */
    fun filterStringHashMapForSpinner(
        response: List<DataRow>?,
        values: ArrayList<String>
    ): HashMap<String, String> {
        val map = HashMap<String, String>()
        values.clear()
        Observable.from(response)
            .subscribe { dataRow ->
                val code = dataRow.row[0]
                val value = dataRow.row[1]
                values.add(value)
                map[value] = code
            }
        return map
    }
}