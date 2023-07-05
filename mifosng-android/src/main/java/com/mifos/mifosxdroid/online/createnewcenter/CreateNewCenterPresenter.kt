package com.mifos.mifosxdroid.online.createnewcenter

import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.organisation.Office
import com.mifos.objects.response.SaveResponse
import com.mifos.services.data.CenterPayload
import com.mifos.utils.MFErrorParser
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 06/06/16.
 */
class CreateNewCenterPresenter @Inject constructor(private val dataManagerCenter: DataManagerCenter) : BasePresenter<CreateNewCenterMvpView?>() {
    private val subscriptions: CompositeSubscription = CompositeSubscription()
    override fun attachView(mvpView: CreateNewCenterMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        subscriptions.unsubscribe()
    }

    fun loadOffices() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        subscriptions.add(dataManagerCenter.offices
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Office?>?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(R.string.failed_to_fetch_offices)
                    }

                    override fun onNext(offices: List<Office?>?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showOffices(offices)
                    }
                }))
    }

    fun createCenter(centerPayload: CenterPayload) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        subscriptions.add(dataManagerCenter.createCenter(centerPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SaveResponse?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(saveResponse: SaveResponse?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.centerCreatedSuccessfully(saveResponse)
                    }
                }))
    }

}