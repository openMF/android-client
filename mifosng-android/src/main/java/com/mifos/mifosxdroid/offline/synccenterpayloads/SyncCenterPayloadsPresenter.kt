package com.mifos.mifosxdroid.offline.synccenterpayloads

import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.response.SaveResponse
import com.mifos.services.data.CenterPayload
import com.mifos.utils.MFErrorParser.errorMessage
import rx.Observer
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by mayankjindal on 04/07/17.
 */
class SyncCenterPayloadsPresenter @Inject constructor(private val mDataManagerCenter: DataManagerCenter) :
    BasePresenter<SyncCenterPayloadsMvpView>() {
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()

    override fun attachView(mvpView: SyncCenterPayloadsMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    fun loadDatabaseCenterPayload() {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerCenter.allDatabaseCenterPayload
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<CenterPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showError(R.string.failed_to_load_centerpayload)
                }

                override fun onNext(centerPayloads: List<CenterPayload>) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showCenters(centerPayloads)
                }
            })
        )
    }

    fun syncCenterPayload(centerPayload: CenterPayload?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerCenter.createCenter(centerPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<SaveResponse?> {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showCenterSyncFailed(errorMessage(e)!!)
                }

                override fun onNext(center: SaveResponse?) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showCenterSyncResponse()
                }
            })
        )
    }

    fun deleteAndUpdateCenterPayload(id: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerCenter.deleteAndUpdateCenterPayloads(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<List<CenterPayload>> {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showError(R.string.failed_to_update_list)
                }

                override fun onNext(centerPayloads: List<CenterPayload>) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showPayloadDeletedAndUpdatePayloads(centerPayloads)
                }
            })
        )
    }

    fun updateCenterPayload(centerPayload: CenterPayload?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerCenter.updateCenterPayload(centerPayload!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<CenterPayload>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_update_list)
                    }

                    override fun onNext(centerPayload: CenterPayload) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showCenterPayloadUpdated(centerPayload)
                    }
                })
        )
    }
}