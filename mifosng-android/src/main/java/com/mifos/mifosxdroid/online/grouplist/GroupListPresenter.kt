package com.mifos.mifosxdroid.online.grouplist

import com.mifos.api.DataManager
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.GroupWithAssociations
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 06/06/16.
 */
class GroupListPresenter @Inject constructor(private val mDataManager: DataManager) : BasePresenter<GroupListMvpView?>() {
    private var mSubscription: Subscription? = null
    override fun attachView(mvpView: GroupListMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        if (mSubscription != null) mSubscription!!.unsubscribe()
    }

    fun loadGroupByCenter(id: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        if (mSubscription != null) mSubscription!!.unsubscribe()
        mSubscription = mDataManager.getGroupsByCenter(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<CenterWithAssociations?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError("Failed to load GroupList")
                    }

                    override fun onNext(centerWithAssociations: CenterWithAssociations?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showGroupList(centerWithAssociations)
                    }
                })
    }

    fun loadGroups(groupid: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        if (mSubscription != null) mSubscription!!.unsubscribe()
        mSubscription = mDataManager.getGroups(groupid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GroupWithAssociations?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError("Failed to load Groups")
                    }

                    override fun onNext(groupWithAssociations: GroupWithAssociations?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showGroups(groupWithAssociations)
                    }
                })
    }

}