package com.mifos.mifosxdroid.online.groupslist

import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.client.Page
import com.mifos.objects.group.Group
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 7/6/16.
 */
class GroupsListPresenter @Inject constructor(private val mDataManagerGroups: DataManagerGroups) : BasePresenter<GroupsListMvpView>() {
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()
    private var mDbGroupList: List<Group>
    private var mSyncGroupList: List<Group>
    private val limit = 100
    private var loadmore = false
    private var mRestApiGroupSyncStatus = false
    private var mDatabaseGroupSyncStatus = false
    override fun attachView(mvpView: GroupsListMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    fun loadGroups(loadmore: Boolean, offset: Int) {
        this.loadmore = loadmore
        loadGroups(true, offset, limit)
    }

    /**
     * Showing Groups List in View, If loadmore is true call showLoadMoreGroups(...) and else
     * call showGroupsList(...).
     */
    private fun showClientList(clients: List<Group>) {
        if (loadmore) {
            mvpView?.showLoadMoreGroups(clients)
        } else {
            mvpView?.showGroups(clients)
        }
    }

    /**
     * This Method will called, when Parent (Fragment or Activity) will be true.
     * If Parent Fragment is true then there is no need to fetch ClientList, Just show the Parent
     * (Fragment or Activity)'s Groups in View
     *
     * @param groups List<Group></Group>>
     */
    fun showParentClients(groups: List<Group>) {
        mvpView?.unregisterSwipeAndScrollListener()
        if (groups.isEmpty()) {
            mvpView?.showEmptyGroups(R.string.group)
        } else {
            mRestApiGroupSyncStatus = true
            mSyncGroupList = groups
            setAlreadyClientSyncStatus()
        }
    }

    /**
     * Setting GroupSync Status True when mRestApiGroupSyncStatus && mDatabaseGroupSyncStatus
     * are true.
     */
    fun setAlreadyClientSyncStatus() {
        if (mRestApiGroupSyncStatus && mDatabaseGroupSyncStatus) {
            showClientList(checkGroupAlreadySyncedOrNot(mSyncGroupList))
        }
    }

    private fun loadGroups(paged: Boolean, offset: Int, limit: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerGroups.getGroups(paged, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Group>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        if (loadmore) {
                            mvpView?.showMessage(R.string.failed_to_fetch_groups)
                        } else {
                            mvpView?.showFetchingError()
                        }
                    }

                    override fun onNext(groupPage: Page<Group>) {
                        mSyncGroupList = groupPage.pageItems
                        if (mSyncGroupList.isEmpty() && !loadmore) {
                            mvpView?.showEmptyGroups(R.string.group)
                            mvpView?.unregisterSwipeAndScrollListener()
                        } else if (mSyncGroupList.isEmpty() && loadmore) {
                            mvpView?.showMessage(R.string.no_more_groups_available)
                        } else {
                            mRestApiGroupSyncStatus = true
                            setAlreadyClientSyncStatus()
                        }
                        mvpView?.showProgressbar(false)
                    }
                }))
    }

    fun loadDatabaseGroups() {
        checkViewAttached()
        mSubscriptions.add(mDataManagerGroups.databaseGroups
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Group>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showMessage(R.string.failed_to_load_db_groups)
                    }

                    override fun onNext(groupPage: Page<Group>) {
                        mDatabaseGroupSyncStatus = true
                        mDbGroupList = groupPage.pageItems
                        setAlreadyClientSyncStatus()
                    }
                })
        )
    }

    /**
     * This Method Filtering the Groups Loaded from Server, is already sync or not. If yes the
     * put the client.setSync(true) and view will show to user that group already synced
     *
     * @param groups
     * @return List<Client>
    </Client> */
    private fun checkGroupAlreadySyncedOrNot(groups: List<Group>): List<Group> {
        if (mDbGroupList.isNotEmpty()) {
            for (dbGroup in mDbGroupList) {
                for (syncGroup in groups) {
                    if (dbGroup.id?.toInt() == syncGroup.id?.toInt()) {
                        syncGroup.sync = true
                        break
                    }
                }
            }
        }
        return groups
    }

    init {
        mDbGroupList = ArrayList()
        mSyncGroupList = ArrayList()
    }
}