package com.mifos.mifosxdroid.online;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.mifosxdroid.FakeRemoteDataSource;
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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private Page<Client> clientPage;

    @Before
    public void setUp() throws Exception {

        mClientListPresenter = new ClientListPresenter(mDataManagerClient);
        mClientListPresenter.attachView(mClientListMvpView);


        clientPage = new Page<>();
        clientPage = FakeRemoteDataSource.getClientList();

    }

    @After
    public void tearDown() {
        mClientListPresenter.detachView();
    }



    @Test
    public void testLoadClients() {

        when(mDataManagerClient.getAllClients(true, offset, limit)).thenReturn(Observable.just
                (clientPage));

        mClientListPresenter.loadClients(false, offset);

        //verify(mClientListMvpView).showClientList(clientPage.getPageItems());
        mClientListPresenter.setAlreadyClientSyncStatus();
        verify(mClientListMvpView, never())
                .showError();

    }

    @Test
    public void testLoadClientFails() {

        when(mDataManagerClient.getAllClients(true, offset, limit))
                .thenReturn(Observable.<Page<Client>>error(new RuntimeException()));

        mClientListPresenter.loadClients(true, offset, limit);
        verify(mClientListMvpView).showError();
        verify(mClientListMvpView, never()).showClientList(clientPage.getPageItems());
    }

}