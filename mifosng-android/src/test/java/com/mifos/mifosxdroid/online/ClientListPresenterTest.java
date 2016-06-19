package com.mifos.mifosxdroid.online;

import com.mifos.api.DataManager;
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
    DataManager mDataManager;

    @Mock
    ClientListMvpView mClientListMvpView;

    private Page<Client> clientPage;

    @Before
    public void setUp() throws Exception {

        mClientListPresenter = new ClientListPresenter(mDataManager);
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

        when(mDataManager.getAllClients()).thenReturn(Observable.just(clientPage));

        mClientListPresenter.loadClients();

        verify(mClientListMvpView).showClientList(clientPage);
        verify(mClientListMvpView, never())
                .showErrorFetchingClients("There was some error fetching list");

    }

    @Test
    public void testLoadClientFails() {

        when(mDataManager.getAllClients())
                .thenReturn(Observable.<Page<Client>>error(new RuntimeException()));

        mClientListPresenter.loadClients();
        verify(mClientListMvpView).showErrorFetchingClients("There was some error fetching list");
        verify(mClientListMvpView, never()).showClientList(clientPage);
    }

    @Test
    public void testLoadMoreClients() {

        when(mDataManager.getAllClients(0, clientPage.getPageItems().size()))
                .thenReturn(Observable.just(clientPage));

        mClientListPresenter.loadMoreClients(0, clientPage.getPageItems().size());

        verify(mClientListMvpView).showMoreClientsList(clientPage);
        verify(mClientListMvpView, never())
                .showErrorFetchingClients("There was some error fetching list");

    }

    @Test
    public void testLoadMoreClientFails() {
        when(mDataManager.getAllClients(0, clientPage.getPageItems().size()))
                .thenReturn(Observable.<Page<Client>>error(new RuntimeException()));

        mClientListPresenter.loadMoreClients(0, clientPage.getPageItems().size());

        verify(mClientListMvpView).showErrorFetchingClients("There was some error fetching list");
        verify(mClientListMvpView, never()).showMoreClientsList(clientPage);
    }


}