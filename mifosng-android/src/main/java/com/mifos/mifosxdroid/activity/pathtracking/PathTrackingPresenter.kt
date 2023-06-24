package com.mifos.mifosxdroid.activity.pathtracking

import com.mifos.api.datamanager.DataManagerDataTable
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.user.UserLocation
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 24/01/17.
 */
class PathTrackingPresenter @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) :
    BasePresenter<PathTrackingMvpView>() {
    private val subscriptions: CompositeSubscription = CompositeSubscription()

    override fun attachView(mvpView: PathTrackingMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        subscriptions.clear()
    }

    fun loadPathTracking(userId: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        subscriptions.add(
            dataManagerDataTable.getUserPathTracking(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<UserLocation>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showError()
                        mvpView?.showProgressbar(false)
                    }

                    override fun onNext(userLocations: List<UserLocation>) {
                        mvpView?.showProgressbar(false)
                        if (userLocations.isNotEmpty()) {
                            mvpView?.showPathTracking(userLocations)
                        } else {
                            mvpView?.showEmptyPathTracking()
                        }
                    }
                })
        )
    }
}