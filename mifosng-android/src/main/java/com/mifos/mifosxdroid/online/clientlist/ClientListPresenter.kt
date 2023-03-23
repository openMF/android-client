package com.mifos.mifosxdroid.online.clientlist

import com.mifos.api.datamanager.DataManagerClient
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.client.Client
import com.mifos.objects.client.Page
import com.mifos.utils.EspressoIdlingResource
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 6/6/16.
 * This Presenter Holds the All Logic to request to DataManagerClient and DataManagerClient, Take
 * care of that From Where Data will come Database or REST API.
 */
class ClientListPresenter @Inject constructor(private val mDataManagerClient: DataManagerClient) : BasePresenter<ClientListMvpView?>() {
    private var mSubscriptions: CompositeSubscription? = null
    private var mDbClientList: List<Client>
    private var mSyncClientList: List<Client>?
    private val limit = 100
    private var loadmore = false
    private var mRestApiClientSyncStatus = false
    private var mDatabaseClientSyncStatus = false
    override fun attachView(mvpView: ClientListMvpView?) {
        super.attachView(mvpView)
        mSubscriptions = CompositeSubscription()
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions!!.unsubscribe()
    }

    /**
     * Loading Client List from Rest API and setting loadmore status
     *
     * @param loadmore Status, need ClientList page other then first.
     * @param offset   Index from Where ClientList will be fetched.
     */
    fun loadClients(loadmore: Boolean, offset: Int) {
        this.loadmore = loadmore
        loadClients(true, offset, limit)
    }

    /**
     * Showing Client List in View, If loadmore is true call showLoadMoreClients(...) and else
     * call showClientList(...).
     */
    fun showClientList(clients: List<Client>?) {
        if (loadmore) {
            mvpView!!.showLoadMoreClients(clients)
        } else {
            mvpView!!.showClientList(clients)
        }
    }

    /**
     * This Method will called, when Parent (Fragment or Activity) will be true.
     * If Parent Fragment is true there is no need to fetch ClientList, Just show the Parent
     * (Fragment or Activity) ClientList in View
     *
     * @param clients List<Client></Client>>
     */
    fun showParentClients(clients: List<Client>?) {
        mvpView!!.unregisterSwipeAndScrollListener()
        if (clients!!.size == 0) {
            mvpView!!.showEmptyClientList(R.string.client)
        } else {
            mRestApiClientSyncStatus = true
            mSyncClientList = clients
            setAlreadyClientSyncStatus()
        }
    }

    /**
     * Setting ClientSync Status True when mRestApiClientSyncStatus && mDatabaseClientSyncStatus
     * are true.
     */
    fun setAlreadyClientSyncStatus() {
        if (mRestApiClientSyncStatus && mDatabaseClientSyncStatus) {
            showClientList(checkClientAlreadySyncedOrNot(mSyncClientList))
        }
    }

    /**
     * This Method fetching Client List from Rest API.
     *
     * @param paged  True Enabling the Pagination of the API
     * @param offset Value give from which position Fetch ClientList
     * @param limit  Maximum size of the Center
     */
    fun loadClients(paged: Boolean, offset: Int, limit: Int) {
        EspressoIdlingResource.increment() // App is busy until further notice.
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions!!.add(mDataManagerClient.getAllClients(paged, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Client>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        if (loadmore) {
                            mvpView!!.showMessage(R.string.failed_to_load_client)
                        } else {
                            mvpView!!.showError()
                        }
                        EspressoIdlingResource.decrement() // App is idle.
                    }

                    override fun onNext(clientPage: Page<Client>) {
                        mSyncClientList = clientPage.pageItems
                        if ((mSyncClientList as MutableList<Client>?)!!.size == 0 && !loadmore) {
                            mvpView!!.showEmptyClientList(R.string.client)
                            mvpView!!.unregisterSwipeAndScrollListener()
                        } else if ((mSyncClientList as MutableList<Client>?)!!.size == 0 && loadmore) {
                            mvpView!!.showMessage(R.string.no_more_clients_available)
                        } else {
                            mRestApiClientSyncStatus = true
                            setAlreadyClientSyncStatus()
                        }
                        mvpView!!.showProgressbar(false)
                        EspressoIdlingResource.decrement() // App is idle.
                    }
                }))
    }

    /**
     * This Method Loading the Client From Database. It request Observable to DataManagerClient
     * and DataManagerClient Request to DatabaseHelperClient to load the Client List Page from the
     * Client_Table and As the Client List Page is loaded DataManagerClient gives the Client List
     * Page after getting response from DatabaseHelperClient
     */
    fun loadDatabaseClients() {
        checkViewAttached()
        mSubscriptions!!.add(mDataManagerClient.allDatabaseClients
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Client>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showMessage(R.string.failed_to_load_db_clients)
                    }

                    override fun onNext(clientPage: Page<Client>) {
                        mDatabaseClientSyncStatus = true
                        mDbClientList = clientPage.pageItems as List<Client>
                        setAlreadyClientSyncStatus()
                    }
                })
        )
    }

    /**
     * This Method Filtering the Clients Loaded from Server is already sync or not. If yes the
     * put the client.setSync(true) and view will show those clients as sync already to user
     *
     * @param
     * @return Page<Client>
    </Client> */
    fun checkClientAlreadySyncedOrNot(clients: List<Client>?): List<Client>? {
        if (mDbClientList.size != 0) {
            for (dbClient in mDbClientList) {
                for (syncClient in clients!!) {
                    if (dbClient.id == syncClient.id) {
                        syncClient.isSync = true
                        break
                    }
                }
            }
        }
        return clients
    }

    companion object {
        private val LOG_TAG = ClientListPresenter::class.java.simpleName
    }

    init {
        mDbClientList = ArrayList()
        mSyncClientList = ArrayList()
    }
}