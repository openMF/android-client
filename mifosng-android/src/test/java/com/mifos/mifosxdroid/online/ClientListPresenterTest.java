package com.mifos.mifosxdroid.online;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.mifosxdroid.FakeRemoteDataSource;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.clientlist.ClientListMvpView;
import com.mifos.mifosxdroid.online.clientlist.ClientListPresenter;
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

/**
 * Created by Rajan Maurya on 15/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientListPresenterTest {


    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    ClientListPresenter mClientListPresenter;

    @Mock
    DataManagerClient mDataManagerClient;

    @Mock
    ClientListMvpView mClientListMvpView;
    int offset = 0;
    int limit = 100;
    private Page<Client> clientPage = null;

    @Before
    public void setUp() throws Exception {

        mClientListPresenter = new ClientListPresenter(mDataManagerClient);
        mClientListPresenter.attachView(mClientListMvpView);


        clientPage = new Page<Client>();
        clientPage = FakeRemoteDataSource.getClientList();

    }

    @After
    public void tearDown() {
        mClientListPresenter.detachView();
    }



    @Test
    public void testLoadClients() {

        stubDatabaseGetAllClients(Observable.just(clientPage));

        mClientListPresenter.loadDatabaseClients();

        mClientListPresenter.setAlreadyClientSyncStatus();
        verify(mClientListMvpView, never()).showMessage(R.string.failed_to_load_db_clients);

        stubDataManagerGetClients(Observable.just(clientPage));

        mClientListPresenter.loadClients(false, offset);

        verify(mClientListMvpView).showProgressbar(true);
        verify(mClientListMvpView).showClientList(clientPage.getPageItems());
        verify(mClientListMvpView, never()).showError();
        verify(mClientListMvpView).showProgressbar(false);
    }

    @Test
    public void testLoadClientFails() {

        stubDataManagerGetClients(Observable.<Page<Client>>error(new RuntimeException()));

        mClientListPresenter.loadClients(false, offset);
        verify(mClientListMvpView).showError();
        verify(mClientListMvpView, never()).showClientList(clientPage.getPageItems());
    }

    @Test
    public void testLoadMoreClients() {

        stubDatabaseGetAllClients(Observable.just(clientPage));

        mClientListPresenter.loadDatabaseClients();

        mClientListPresenter.setAlreadyClientSyncStatus();
        verify(mClientListMvpView, never()).showMessage(R.string.failed_to_load_db_clients);

        stubDataManagerGetClients(Observable.just(clientPage));

        mClientListPresenter.loadClients(true, offset);

        verify(mClientListMvpView).showProgressbar(true);
        verify(mClientListMvpView).showLoadMoreClients(clientPage.getPageItems());
        verify(mClientListMvpView, never()).showError();
        verify(mClientListMvpView).showProgressbar(false);
    }

    @Test
    public void testLoadMoreClientFails() {

        stubDataManagerGetClients(Observable.<Page<Client>>error(new RuntimeException()));

        mClientListPresenter.loadClients(true, offset);
        verify(mClientListMvpView).showMessage(R.string.failed_to_load_client);
        verify(mClientListMvpView, never()).showClientList(clientPage.getPageItems());
    }

    @Test
    public void testEmptyClientList() {

        stubDataManagerGetClients(Observable.just(new Page<Client>()));

        mClientListPresenter.loadClients(false, offset);

        verify(mClientListMvpView).showProgressbar(true);
        verify(mClientListMvpView).showEmptyClientList(R.string.client);
        verify(mClientListMvpView).unregisterSwipeAndScrollListener();
        verify(mClientListMvpView, never()).showError();
        verify(mClientListMvpView).showProgressbar(false);
    }

    @Test
    public void testNoMoreClientsAvailable() {

        stubDataManagerGetClients(Observable.just(new Page<Client>()));

        mClientListPresenter.loadClients(true, offset);

        verify(mClientListMvpView).showProgressbar(true);
        verify(mClientListMvpView).showMessage(R.string.no_more_clients_available);
        verify(mClientListMvpView, never()).showError();
        verify(mClientListMvpView).showProgressbar(false);
    }

    @Test
    public void testLoadDatabaseClients() {
        stubDatabaseGetAllClients(Observable.just(clientPage));

        mClientListPresenter.loadDatabaseClients();
        mClientListPresenter.setAlreadyClientSyncStatus();
        verify(mClientListMvpView, never()).showMessage(R.string.failed_to_load_db_clients);
    }

    @Test
    public void testLoadDatabaseClientsFails() {
        stubDatabaseGetAllClients(Observable.<Page<Client>>error(new RuntimeException()));

        mClientListPresenter.loadDatabaseClients();

        verify(mClientListMvpView).showMessage(R.string.failed_to_load_db_clients);
    }

    public void stubDataManagerGetClients(Observable observable) {
        when(mDataManagerClient.getAllClients(true, offset, limit)).thenReturn(observable);
    }

    public void stubDatabaseGetAllClients(Observable observable) {
        when(mDataManagerClient.getAllDatabaseClients()).thenReturn(observable);
    }
}