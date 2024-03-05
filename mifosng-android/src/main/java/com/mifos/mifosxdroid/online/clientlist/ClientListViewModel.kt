package com.mifos.mifosxdroid.online.clientlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Page
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class ClientListViewModel @Inject constructor(private val repository: ClientListRepository) :
    ViewModel() {

    private val _clientListUiState = MutableLiveData<ClientListUiState>()

    val clientListUiState: LiveData<ClientListUiState>
        get() = _clientListUiState

    private var mDbClientList: List<Client> = ArrayList()
    private var mSyncClientList: List<Client> = ArrayList()
    private val limit = 100
    private var loadmore = false
    private var mRestApiClientSyncStatus = false
    private var mDatabaseClientSyncStatus = false


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
    private fun showClientList(clients: List<Client>) {
        if (loadmore) {
            _clientListUiState.value = ClientListUiState.ShowLoadMoreClients(clients)
        } else {
            _clientListUiState.value = ClientListUiState.ShowClientList(clients)
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
        _clientListUiState.value = ClientListUiState.UnregisterSwipeAndScrollListener
        if (clients != null) {
            if (clients.isEmpty()) {
                _clientListUiState.value = ClientListUiState.ShowEmptyClientList(R.string.client)
            } else {
                mRestApiClientSyncStatus = true
                mSyncClientList = clients
                setAlreadyClientSyncStatus()
            }
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
    private fun loadClients(paged: Boolean, offset: Int, limit: Int) {
        _clientListUiState.value = ClientListUiState.ShowProgressbar(true)
        repository.getAllClients(offset, limit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Page<Client>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    if (loadmore) {
                        _clientListUiState.value =
                            ClientListUiState.ShowMessage(R.string.failed_to_load_client)
                    } else {
                        _clientListUiState.value = ClientListUiState.ShowError
                    }
                }

                override fun onNext(clientPage: Page<Client>) {
                    mSyncClientList = clientPage.pageItems
                    if (mSyncClientList.isEmpty() && !loadmore) {
                        _clientListUiState.value =
                            ClientListUiState.ShowEmptyClientList(R.string.client)
                        _clientListUiState.value =
                            ClientListUiState.UnregisterSwipeAndScrollListener
                    } else if (mSyncClientList.isEmpty() && loadmore) {
                        _clientListUiState.value =
                            ClientListUiState.ShowMessage(R.string.no_more_clients_available)
                    } else {
                        mRestApiClientSyncStatus = true
                        setAlreadyClientSyncStatus()
                    }
                }
            })
    }

    /**
     * This Method Loading the Client From Database. It request Observable to DataManagerClient
     * and DataManagerClient Request to DatabaseHelperClient to load the Client List Page from the
     * Client_Table and As the Client List Page is loaded DataManagerClient gives the Client List
     * Page after getting response from DatabaseHelperClient
     */
    fun loadDatabaseClients() {
        repository.allDatabaseClients()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Page<Client>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _clientListUiState.value =
                        ClientListUiState.ShowMessage(R.string.failed_to_load_db_clients)
                }

                override fun onNext(clientPage: Page<Client>) {
                    mDatabaseClientSyncStatus = true
                    mDbClientList = clientPage.pageItems
                    setAlreadyClientSyncStatus()
                }
            })
    }

    /**
     * This Method Filtering the Clients Loaded from Server is already sync or not. If yes the
     * put the client.setSync(true) and view will show those clients as sync already to user
     *
     * @param
     * @return Page<Client>
    </Client> */
    private fun checkClientAlreadySyncedOrNot(clients: List<Client>): List<Client> {
        if (mDbClientList.isNotEmpty()) {
            for (dbClient in mDbClientList) {
                for (syncClient in clients) {
                    if (dbClient.id == syncClient.id) {
                        syncClient.sync = true
                        break
                    }
                }
            }
        }
        return clients
    }
}