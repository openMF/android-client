package com.mifos.mifosxdroid.online;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.FakeRemoteDataSource;
import com.mifos.mifosxdroid.online.clientsearch.ClientSearchMvpView;
import com.mifos.mifosxdroid.online.clientsearch.ClientSearchPresenter;
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule;
import com.mifos.objects.SearchedEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import rx.Observable;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rajan Maurya on 17/6/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientSearchPresenterTest {

    public static final String QUERY = "ab";
    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();
    ClientSearchPresenter mClientSearchPresenter;
    @Mock
    DataManager mDataManager;
    @Mock
    ClientSearchMvpView mClientSearchMvpView;
    List<SearchedEntity> searchedEntities;

    @Before
    public void setUp() throws Exception {

        mClientSearchPresenter = new ClientSearchPresenter(mDataManager);
        mClientSearchPresenter.attachView(mClientSearchMvpView);

        searchedEntities = FakeRemoteDataSource.getSearchedEntity();
    }

    @After
    public void tearDown() throws Exception {
        mClientSearchPresenter.detachView();
    }

    @Test
    public void testSearchClients() {

        when(mDataManager.searchClientsByName(QUERY))
                .thenReturn(Observable.just(searchedEntities));

        mClientSearchPresenter.searchClients(QUERY);

        verify(mClientSearchMvpView).showClientsSearched(searchedEntities);
        verify(mClientSearchMvpView, never()).showFetchingError("Failed to load clients");
    }

    @Test
    public void testSearchClientsFails() {

        when(mDataManager.searchClientsByName(QUERY))
                .thenReturn(Observable.<List<SearchedEntity>>error(new RuntimeException()));

        mClientSearchPresenter.searchClients(QUERY);

        verify(mClientSearchMvpView).showFetchingError("Failed to load clients");
        verify(mClientSearchMvpView, never()).showClientsSearched(searchedEntities);
    }


}