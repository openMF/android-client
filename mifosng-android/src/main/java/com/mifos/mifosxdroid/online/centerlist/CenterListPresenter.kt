package com.mifos.mifosxdroid.online.centerlist

import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.client.Page
import com.mifos.objects.group.Center
import com.mifos.objects.group.CenterWithAssociations
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 5/6/16.
 */
class CenterListPresenter @Inject constructor(private val mDataManagerCenter: DataManagerCenter) : BasePresenter<CenterListMvpView?>() {
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()
    private var mDbCenterList: List<Center>
    private var mSyncCenterList: List<Center?>
    private val limit = 100
    private var loadmore = false
    private var mRestApiCenterSyncStatus = false
    private var mDatabaseCenterSyncStatus = false
    override fun attachView(mvpView: CenterListMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    /**
     * This Method for loading the first 100 centers.
     *
     * @param loadmore
     * @param offset
     */
    fun loadCenters(loadmore: Boolean, offset: Int) {
        this.loadmore = loadmore
        loadCenters(true, offset, limit)
    }

    /**
     * This Method For showing Center List in UI.
     *
     * @param centers
     */
    fun showCenters(centers: List<Center?>?) {
        if (loadmore) {
            mvpView?.showMoreCenters(centers)
        } else {
            mvpView?.showCenters(centers)
        }
    }

    /**
     * Setting CenterSync Status True when mRestApiCenterSyncStatus && mDatabaseCenterSyncStatus
     * are true.
     */
    fun setAlreadyCenterSyncStatus() {
        if (mRestApiCenterSyncStatus && mDatabaseCenterSyncStatus) {
            showCenters(checkCenterAlreadySyncedOrNot(mSyncCenterList))
        }
    }

    /**
     * @param paged  True Enabling the Pagination of the API
     * @param offset Value given from which position Center List will be fetched.
     * @param limit  Number of Centers to fetch.
     */
    private fun loadCenters(paged: Boolean, offset: Int, limit: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerCenter.getCenters(paged, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Center>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        if (loadmore) {
                            mvpView?.showMessage(R.string.failed_to_fetch_centers)
                        } else {
                            mvpView?.showFetchingError()
                        }
                    }

                    override fun onNext(centerPage: Page<Center>) {
                        mSyncCenterList = centerPage.pageItems
                        if (mSyncCenterList.isEmpty() && !loadmore) {
                            mvpView?.showEmptyCenters(R.string.center)
                            mvpView?.unregisterSwipeAndScrollListener()
                        } else if (mSyncCenterList.isEmpty() && loadmore) {
                            mvpView?.showMessage(R.string.no_more_centers_available)
                        } else {
                            showCenters(mSyncCenterList)
                            mRestApiCenterSyncStatus = true
                            setAlreadyCenterSyncStatus()
                        }
                        mvpView?.showProgressbar(false)
                    }
                }))
    }

    fun loadCentersGroupAndMeeting(id: Int) {
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerCenter.getCentersGroupAndMeeting(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<CenterWithAssociations?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showMessage(R.string.failed_to_fetch_Group_and_meeting)
                    }

                    override fun onNext(centerWithAssociations: CenterWithAssociations?) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showCentersGroupAndMeeting(centerWithAssociations, id)
                    }
                }))
    }

    /**
     * This Method Loading the Center From Database. It request Observable to DataManagerCenter
     * and DataManagerCenter Request to DatabaseHelperCenter to load the Center List Page from the
     * Center_Table and As the Center List Page is loaded DataManagerCenter gives the Center List
     * Page after getting response from DatabaseHelperCenter
     */
    fun loadDatabaseCenters() {
        checkViewAttached()
        mSubscriptions.add(mDataManagerCenter.allDatabaseCenters
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Center>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showMessage(R.string.failed_to_load_db_centers)
                    }

                    override fun onNext(centerPage: Page<Center>) {
                        mDatabaseCenterSyncStatus = true
                        if (centerPage != null) {
                            mDbCenterList = centerPage.pageItems as List<Center>
                        }
                        setAlreadyCenterSyncStatus()
                    }
                })
        )
    }

    /**
     * This Method Filtering the Centers Loaded from Server is already sync or not. If yes the
     * put the center.setSync(true) and view will show those centers as sync already to user
     *
     * @param
     * @return Page<Center>
    </Center> */
    private fun checkCenterAlreadySyncedOrNot(centers: List<Center?>): List<Center?> {
        if (mDbCenterList.isNotEmpty()) {
            for (dbCenter in mDbCenterList) {
                for (syncCenter in centers) {
                    if (dbCenter.id == syncCenter?.id) {
                        syncCenter?.isSync = true
                        break
                    }
                }
            }
        }
        return centers
    }

    init {
        mDbCenterList = ArrayList()
        mSyncCenterList = ArrayList()
    }
}