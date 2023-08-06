package com.mifos.mifosxdroid.online.runreports.reportcategory

import com.mifos.api.datamanager.DataManagerRunReport
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.runreports.client.ClientReportTypeItem
import com.mifos.utils.MFErrorParser
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Tarun on 03-08-17.
 */
class ReportCategoryPresenter @Inject constructor(private val dataManager: DataManagerRunReport) :
    BasePresenter<ReportCategoryMvpView>() {
    private val subscription: CompositeSubscription?

    init {
        subscription = CompositeSubscription()
    }

    override fun attachView(mvpView: ReportCategoryMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        subscription?.unsubscribe()
    }

    fun fetchCategories(
        reportCategory: String?,
        genericResultSet: Boolean,
        parameterType: Boolean
    ) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        subscription?.add(dataManager.getReportCategories(
            reportCategory, genericResultSet,
            parameterType
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<ClientReportTypeItem>>() {
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

                override fun onNext(clientReportTypeItems: List<ClientReportTypeItem>) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showReportCategories(filterUniques(clientReportTypeItems))
                }
            })
        )
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