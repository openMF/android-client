package com.mifos.mifosxdroid.online.runreports.reportdetail

import com.mifos.api.datamanager.DataManagerRunReport
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.runreports.DataRow
import com.mifos.objects.runreports.FullParameterListResponse
import com.mifos.utils.MFErrorParser
import org.json.JSONObject
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Tarun on 04-08-17.
 */
class ReportDetailPresenter @Inject constructor(private val dataManager: DataManagerRunReport) :
    BasePresenter<ReportDetailMvpView>() {
    private val subscription: CompositeSubscription?

    init {
        subscription = CompositeSubscription()
    }

    override fun attachView(mvpView: ReportDetailMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        subscription?.unsubscribe()
    }

    fun fetchFullParameterList(reportName: String?, parameterType: Boolean) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        subscription?.add(
            dataManager.getReportFullParameterList(reportName, parameterType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<FullParameterListResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        try {
                            if (e is HttpException) {
                                val errorMessage = e.response()?.errorBody()
                                    ?.string()

                                // Default User message is null in these queries most of the times.
                                // Hence, show Developer message.
                                mvpView?.showError(
                                    MFErrorParser.parseError(errorMessage)
                                        .errors[0].developerMessage
                                )
                            }
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler.handleError(e)
                        }
                    }

                    override fun onNext(response: FullParameterListResponse) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showFullParameterResponse(response)
                    }
                })
        )
    }

    fun fetchParameterDetails(parameterName: String?, parameterType: Boolean) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        subscription?.add(
            dataManager.getReportParameterDetails(parameterName, parameterType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<FullParameterListResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        try {
                            if (e is HttpException) {
                                val errorMessage = e.response()?.errorBody()
                                    ?.string()
                                mvpView?.showError(
                                    MFErrorParser.parseError(errorMessage)
                                        .errors[0].developerMessage
                                )
                            }
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler.handleError(e)
                        }
                    }

                    override fun onNext(response: FullParameterListResponse) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showParameterDetails(response, parameterName!!)
                    }
                })
        )
    }

    fun fetchOffices(parameterName: String?, officeId: Int, parameterType: Boolean) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        subscription?.add(
            dataManager.getRunReportOffices(parameterName, officeId, parameterType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<FullParameterListResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        try {
                            if (e is HttpException) {
                                val errorMessage = e.response()?.errorBody()
                                    ?.string()
                                mvpView?.showError(
                                    MFErrorParser.parseError(errorMessage)
                                        .errors[0].developerMessage
                                )
                            }
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler.handleError(e)
                        }
                    }

                    override fun onNext(response: FullParameterListResponse) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showOffices(response, parameterName!!)
                    }
                })
        )
    }

    fun fetchProduct(parameterName: String?, currencyId: String?, parameterType: Boolean) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        subscription?.add(
            dataManager.getRunReportProduct(parameterName, currencyId, parameterType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<FullParameterListResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        try {
                            if (e is HttpException) {
                                val errorMessage = e.response()?.errorBody()
                                    ?.string()
                                mvpView?.showError(
                                    MFErrorParser.parseError(errorMessage)
                                        .errors[0].developerMessage
                                )
                            }
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler.handleError(e)
                        }
                    }

                    override fun onNext(response: FullParameterListResponse) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showProduct(response, parameterName!!)
                    }
                })
        )
    }

    fun fetchRunReportWithQuery(reportName: String?, options: MutableMap<String?, String?>) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        subscription?.add(
            dataManager.getRunReportWithQuery(reportName, options)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<FullParameterListResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        try {
                            if (e is HttpException) {
                                val errorMessage = e.response()?.errorBody()
                                    ?.string()
                                mvpView?.showError(
                                    JSONObject(errorMessage)
                                        .getString("developerMessage")
                                )
                            }
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler.handleError(e)
                        }
                    }

                    override fun onNext(response: FullParameterListResponse) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showRunReport(response)
                    }
                })
        )
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