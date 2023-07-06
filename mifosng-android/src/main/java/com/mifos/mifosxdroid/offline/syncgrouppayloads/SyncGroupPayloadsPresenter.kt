package com.mifos.mifosxdroid.offline.syncgrouppayloads

import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.group.GroupPayload
import com.mifos.objects.response.SaveResponse
import com.mifos.utils.MFErrorParser.errorMessage
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 19/07/16.
 */
class SyncGroupPayloadsPresenter @Inject constructor(val mDataManagerGroups: DataManagerGroups) :
    BasePresenter<SyncGroupPayloadsMvpView>() {
    var mSubscriptions: CompositeSubscription = CompositeSubscription()

    override fun attachView(mvpView: SyncGroupPayloadsMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    fun loanDatabaseGroupPayload() {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerGroups.allDatabaseGroupPayload
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<GroupPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showError(R.string.failed_to_load_grouppayload)
                }

                override fun onNext(groupPayloads: List<GroupPayload>) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showGroups(groupPayloads)
                }
            })
        )
    }

    fun syncGroupPayload(groupPayload: GroupPayload?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerGroups.createGroup(groupPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SaveResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showGroupSyncFailed(errorMessage(e)!!)
                }

                override fun onNext(group: SaveResponse) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showGroupSyncResponse()
                }
            })
        )
    }

    fun deleteAndUpdateGroupPayload(id: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerGroups.deleteAndUpdateGroupPayloads(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<GroupPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showError(R.string.failed_to_update_list)
                }

                override fun onNext(groupPayloads: List<GroupPayload>) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showPayloadDeletedAndUpdatePayloads(groupPayloads)
                }
            })
        )
    }

    fun updateGroupPayload(groupPayload: GroupPayload?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerGroups.updateGroupPayload(groupPayload!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GroupPayload>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_load_grouppayload)
                    }

                    override fun onNext(groupPayload: GroupPayload) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showGroupPayloadUpdated(groupPayload)
                    }
                })
        )
    }
}