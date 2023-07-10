package com.mifos.mifosxdroid.online

import com.mifos.api.datamanager.DataManagerClient
import com.mifos.mifosxdroid.FakeRemoteDataSource.clientList
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.online.clientlist.ClientListMvpView
import com.mifos.mifosxdroid.online.clientlist.ClientListPresenter
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import com.mifos.objects.client.Client
import com.mifos.objects.client.Page
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import rx.Observable

/**
 * Created by Rajan Maurya on 15/06/16.
 */
@RunWith(MockitoJUnitRunner::class)
class ClientListPresenterTest {
    @get:Rule
    val mOverrideSchedulersRule = RxSchedulersOverrideRule()
    private lateinit var mClientListPresenter: ClientListPresenter

    @Mock
    lateinit var mDataManagerClient: DataManagerClient

    @Mock
    lateinit var mClientListMvpView: ClientListMvpView
    var offset = 0
    var limit = 100
    private lateinit var clientPage: Page<Client>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mClientListPresenter = ClientListPresenter(mDataManagerClient)
        mClientListPresenter.attachView(mClientListMvpView)
        clientPage = Page()
        clientPage = clientList
    }

    @After
    fun tearDown() {
        mClientListPresenter.detachView()
    }

    @Test
    fun testLoadClients() {
        stubDatabaseGetAllClients(Observable.just(clientPage))
        mClientListPresenter.loadDatabaseClients()
        mClientListPresenter.setAlreadyClientSyncStatus()
        Mockito.verify(mClientListMvpView, Mockito.never())
            .showMessage(R.string.failed_to_load_db_clients)
        stubDataManagerGetClients(Observable.just(clientPage))
        mClientListPresenter.loadClients(false, offset)
        Mockito.verify(mClientListMvpView).showProgressbar(true)
        Mockito.verify(mClientListMvpView).showClientList(clientPage.pageItems)
        Mockito.verify(mClientListMvpView, Mockito.never()).showError()
        Mockito.verify(mClientListMvpView).showProgressbar(false)
    }

    @Test
    fun testLoadClientFails() {
        stubDataManagerGetClients(Observable.error<Page<Client>>(RuntimeException()))
        mClientListPresenter.loadClients(false, offset)
        Mockito.verify(mClientListMvpView).showError()
        Mockito.verify(mClientListMvpView, Mockito.never()).showClientList(
            clientPage.pageItems
        )
    }

    @Test
    fun testLoadMoreClients() {
        stubDatabaseGetAllClients(Observable.just(clientPage))
        mClientListPresenter.loadDatabaseClients()
        mClientListPresenter.setAlreadyClientSyncStatus()
        Mockito.verify(mClientListMvpView, Mockito.never())
            .showMessage(R.string.failed_to_load_db_clients)
        stubDataManagerGetClients(Observable.just(clientPage))
        mClientListPresenter.loadClients(true, offset)
        Mockito.verify(mClientListMvpView).showProgressbar(true)
        Mockito.verify(mClientListMvpView).showLoadMoreClients(clientPage.pageItems)
        Mockito.verify(mClientListMvpView, Mockito.never()).showError()
        Mockito.verify(mClientListMvpView).showProgressbar(false)
    }

    @Test
    fun testLoadMoreClientFails() {
        stubDataManagerGetClients(Observable.error<Page<Client>>(RuntimeException()))
        mClientListPresenter.loadClients(true, offset)
        Mockito.verify(mClientListMvpView).showMessage(R.string.failed_to_load_client)
        Mockito.verify(mClientListMvpView, Mockito.never()).showClientList(
            clientPage.pageItems
        )
    }

    @Test
    fun testEmptyClientList() {
        stubDataManagerGetClients(Observable.just(Page<Client>()))
        mClientListPresenter.loadClients(false, offset)
        Mockito.verify(mClientListMvpView).showProgressbar(true)
        Mockito.verify(mClientListMvpView).showEmptyClientList(R.string.client)
        Mockito.verify(mClientListMvpView).unregisterSwipeAndScrollListener()
        Mockito.verify(mClientListMvpView, Mockito.never()).showError()
        Mockito.verify(mClientListMvpView).showProgressbar(false)
    }

    @Test
    fun testNoMoreClientsAvailable() {
        stubDataManagerGetClients(Observable.just(Page<Client>()))
        mClientListPresenter.loadClients(true, offset)
        Mockito.verify(mClientListMvpView).showProgressbar(true)
        Mockito.verify(mClientListMvpView).showMessage(R.string.no_more_clients_available)
        Mockito.verify(mClientListMvpView, Mockito.never()).showError()
        Mockito.verify(mClientListMvpView).showProgressbar(false)
    }

    @Test
    fun testLoadDatabaseClients() {
        stubDatabaseGetAllClients(Observable.just(clientPage))
        mClientListPresenter.loadDatabaseClients()
        mClientListPresenter.setAlreadyClientSyncStatus()
        Mockito.verify(mClientListMvpView, Mockito.never())
            .showMessage(R.string.failed_to_load_db_clients)
    }

    @Test
    fun testLoadDatabaseClientsFails() {
        stubDatabaseGetAllClients(Observable.error(RuntimeException()))
        mClientListPresenter.loadDatabaseClients()
        Mockito.verify(mClientListMvpView).showMessage(R.string.failed_to_load_db_clients)
    }

    private fun stubDataManagerGetClients(observable: Observable<Page<Client>>) {
        Mockito.`when`(
            mDataManagerClient.getAllClients(true, offset, limit)
        ).thenReturn(observable)
    }

    private fun stubDatabaseGetAllClients(observable: Observable<Page<Client>>) {
        Mockito.`when`(
            mDataManagerClient.allDatabaseClients
        ).thenReturn(observable)
    }
}