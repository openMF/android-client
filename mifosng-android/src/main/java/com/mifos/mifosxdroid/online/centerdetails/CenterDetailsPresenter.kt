package com.mifos.mifosxdroid.online.centerdetails

import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.api.datamanager.DataManagerRunReport
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.group.CenterInfo
import com.mifos.objects.group.CenterWithAssociations
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 05/02/17.
 */
class CenterDetailsPresenter @Inject constructor(private val dataManagerCenter: DataManagerCenter,
                                                 private val dataManagerRunReport: DataManagerRunReport) : BasePresenter<CenterDetailsMvpView?>() {
    private val subscriptions: CompositeSubscription
    override fun attachView(mvpView: CenterDetailsMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        subscriptions.clear()
    }

    fun loadCentersGroupAndMeeting(centerId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        subscriptions.add(dataManagerCenter.getCentersGroupAndMeeting(centerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<CenterWithAssociations?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showErrorMessage(R.string.failed_to_fetch_Group_and_meeting)
                    }

                    override fun onNext(centerWithAssociations: CenterWithAssociations?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showMeetingDetails(centerWithAssociations)
                        mvpView!!.showCenterDetails(centerWithAssociations)
                    }
                }))
    }

    fun loadSummaryInfo(centerId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        subscriptions.add(dataManagerRunReport.getCenterSummarInfo(centerId, false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<CenterInfo?>?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showErrorMessage(R.string.failed_to_fetch_center_info)
                    }

                    override fun onNext(centerInfos: List<CenterInfo?>?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showSummaryInfo(centerInfos)
                    }
                }))
    }

    init {
        subscriptions = CompositeSubscription()
    }
}