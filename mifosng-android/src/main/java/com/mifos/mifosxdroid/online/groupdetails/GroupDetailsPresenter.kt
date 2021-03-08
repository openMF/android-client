package com.mifos.mifosxdroid.online.groupdetails

import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.group.GroupWithAssociations
import com.mifos.objects.zipmodels.GroupAndGroupAccounts
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 07/06/16.
 */
class GroupDetailsPresenter @Inject constructor(private val mDataManagerGroups: DataManagerGroups) : BasePresenter<GroupDetailsMvpView?>() {
    private val mSubscriptions: CompositeSubscription
    override fun attachView(mvpView: GroupDetailsMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.clear()
    }

    fun loadGroupDetailsAndAccounts(groupId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(Observable.combineLatest(
                mDataManagerGroups.getGroup(groupId),
                mDataManagerGroups.getGroupAccounts(groupId)
        ) { group, groupAccounts -> GroupAndGroupAccounts(group, groupAccounts) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GroupAndGroupAccounts?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(R.string.failed_to_fetch_group_and_account)
                    }

                    override fun onNext(groupAndGroupAccounts: GroupAndGroupAccounts?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showGroup(groupAndGroupAccounts?.group)
                        mvpView!!.showGroupAccounts(groupAndGroupAccounts?.groupAccounts)
                    }
                })
        )
    }

    fun loadGroupAssociateClients(groupId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerGroups.getGroupWithAssociations(groupId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GroupWithAssociations?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(R.string.failed_to_load_client)
                    }

                    override fun onNext(groupWithAssociations: GroupWithAssociations?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showGroupClients(groupWithAssociations?.clientMembers)
                    }
                })
        )
    }

    init {
        mSubscriptions = CompositeSubscription()
    }
}