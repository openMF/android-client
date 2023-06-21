package com.mifos.mifosxdroid.online;

import com.mifos.api.datamanager.DataManagerSearch;
import com.mifos.mifosxdroid.FakeRemoteDataSource;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.search.SearchMvpView;
import com.mifos.mifosxdroid.online.search.SearchPresenter;
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule;
import com.mifos.objects.SearchedEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rajan Maurya on 17/6/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchPresenterTest {

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    SearchPresenter searchPresenter;

    @Mock
    DataManagerSearch dataManager;

    @Mock
    SearchMvpView searchMvpView;

    List<SearchedEntity> searchedEntities;

    private String query = "1";
    private String resources;
    private Boolean exactMatch = false;

    @Before
    public void setUp() {

        searchPresenter = new SearchPresenter(dataManager);
        searchPresenter.attachView(searchMvpView);

        searchedEntities = FakeRemoteDataSource.getSearchedEntity();
    }

    @After
    public void tearDown() {
        searchPresenter.detachView();
    }

    @Test
    public void testSearchAll() {
        resources = "clients,groups,loans,savingsaccounts";
        stubDataManagerGetSearch(Observable.just(searchedEntities), exactMatch);

        searchPresenter.searchResources(query, resources, exactMatch);

        verify(searchMvpView).showProgressbar(true);
        verify(searchMvpView).showSearchedResources(searchedEntities);
        verify(searchMvpView, never()).showMessage(R.string.failed_to_fetch_resources_of_query);
        verify(searchMvpView).showProgressbar(false);
    }

    @Test
    public void testSearchAllFails() {
        resources = "clients,groups,loans,savingsaccounts";
        stubDataManagerGetSearch(Observable.<List<SearchedEntity>>error(new RuntimeException()),
                exactMatch);

        searchPresenter.searchResources(query, resources, exactMatch);

        verify(searchMvpView).showProgressbar(true);
        verify(searchMvpView).showMessage(R.string.failed_to_fetch_resources_of_query);
        verify(searchMvpView, never()).showSearchedResources(searchedEntities);
        verify(searchMvpView).showProgressbar(false);
    }

    @Test
    public void testSearchClients() {
        resources = "clients";
        List<SearchedEntity> clientSearch = filterEntities("CLIENT");
        stubDataManagerGetSearch(Observable.just(clientSearch), exactMatch);

        searchPresenter.searchResources(query, resources, exactMatch);

        verify(searchMvpView).showProgressbar(true);
        verify(searchMvpView).showSearchedResources(clientSearch);
        verify(searchMvpView, never()).showMessage(R.string.failed_to_fetch_resources_of_query);
        verify(searchMvpView).showProgressbar(false);
    }

    @Test
    public void testSearchClientsFails() {
        resources = "clients";
        stubDataManagerGetSearch(Observable.<List<SearchedEntity>>error(new RuntimeException()),
                exactMatch);

        searchPresenter.searchResources(query, resources, exactMatch);

        verify(searchMvpView).showProgressbar(true);
        verify(searchMvpView).showMessage(R.string.failed_to_fetch_resources_of_query);
        verify(searchMvpView, never()).showSearchedResources(searchedEntities);
        verify(searchMvpView).showProgressbar(false);
    }

    @Test
    public void testSearchGroups() {
        resources = "groups";
        List<SearchedEntity> groupSearch = filterEntities("GROUP");
        stubDataManagerGetSearch(Observable.just(groupSearch), exactMatch);

        searchPresenter.searchResources(query, resources, exactMatch);

        verify(searchMvpView).showProgressbar(true);
        verify(searchMvpView).showSearchedResources(groupSearch);
        verify(searchMvpView, never()).showMessage(R.string.failed_to_fetch_resources_of_query);
        verify(searchMvpView).showProgressbar(false);
    }

    @Test
    public void testSearchGroupsFails() {
        resources = "groups";
        stubDataManagerGetSearch(Observable.<List<SearchedEntity>>error(new RuntimeException()),
                exactMatch);

        searchPresenter.searchResources(query, resources, exactMatch);

        verify(searchMvpView).showProgressbar(true);
        verify(searchMvpView).showMessage(R.string.failed_to_fetch_resources_of_query);
        verify(searchMvpView, never()).showSearchedResources(searchedEntities);
        verify(searchMvpView).showProgressbar(false);
    }

    @Test
    public void testSearchLoans() {
        resources = "loans";
        List<SearchedEntity> loansSearch = filterEntities("LOAN");
        stubDataManagerGetSearch(Observable.just(loansSearch), exactMatch);

        searchPresenter.searchResources(query, resources, exactMatch);

        verify(searchMvpView).showProgressbar(true);
        verify(searchMvpView).showSearchedResources(loansSearch);
        verify(searchMvpView, never()).showMessage(R.string.failed_to_fetch_resources_of_query);
        verify(searchMvpView, never()).showNoResultFound();
        verify(searchMvpView).showProgressbar(false);
    }

    @Test
    public void testSearchLoansFails() {
        resources = "loans";
        stubDataManagerGetSearch(Observable.<List<SearchedEntity>>error(new RuntimeException()),
                exactMatch);

        searchPresenter.searchResources(query, resources, exactMatch);

        verify(searchMvpView).showProgressbar(true);
        verify(searchMvpView).showMessage(R.string.failed_to_fetch_resources_of_query);
        verify(searchMvpView, never()).showSearchedResources(searchedEntities);
        verify(searchMvpView).showProgressbar(false);
    }

    @Test
    public void testSearchSavings() {
        resources = "savingsaccounts";
        List<SearchedEntity> savingSearch = filterEntities("SAVING");
        stubDataManagerGetSearch(Observable.just(savingSearch), exactMatch);

        searchPresenter.searchResources(query, resources, exactMatch);

        verify(searchMvpView).showProgressbar(true);
        verify(searchMvpView).showSearchedResources(savingSearch);
        verify(searchMvpView, never()).showMessage(R.string.failed_to_fetch_resources_of_query);
        verify(searchMvpView).showProgressbar(false);
    }

    @Test
    public void testSearchSavingsFails() {
        resources = "savingsaccounts";
        stubDataManagerGetSearch(Observable.<List<SearchedEntity>>error(new RuntimeException()),
                exactMatch);

        searchPresenter.searchResources(query, resources, exactMatch);

        verify(searchMvpView).showProgressbar(true);
        verify(searchMvpView).showMessage(R.string.failed_to_fetch_resources_of_query);
        verify(searchMvpView, never()).showSearchedResources(searchedEntities);
        verify(searchMvpView).showProgressbar(false);
    }

    @Test
    public void testSearchExactMatch() {
        query = "Madenge Pimbi";
        resources = "clients";
        final List<SearchedEntity> exactSearch = new ArrayList<>();
        Observable.from(searchedEntities)
                .subscribe(new Action1<SearchedEntity>() {
                    @Override
                    public void call(SearchedEntity searchedEntity) {
                        if (searchedEntity.getEntityName().equals("Madenge Pimbi")) {
                            exactSearch.add(searchedEntity);
                        }
                    }
                });

        stubDataManagerGetSearch(Observable.just(exactSearch), !exactMatch);

        searchPresenter.searchResources(query, resources, !exactMatch);

        verify(searchMvpView).showProgressbar(true);
        verify(searchMvpView).showSearchedResources(exactSearch);
        verify(searchMvpView, never()).showMessage(R.string.failed_to_fetch_resources_of_query);
        verify(searchMvpView).showProgressbar(false);
    }

    @Test
    public void testSearchExactMatchFails() {
        query = "Smith R";
        resources = "clients";
        stubDataManagerGetSearch(Observable.<List<SearchedEntity>>error(new RuntimeException()),
                !exactMatch);

        searchPresenter.searchResources(query, resources, !exactMatch);

        verify(searchMvpView).showProgressbar(true);
        verify(searchMvpView).showMessage(R.string.failed_to_fetch_resources_of_query);
        verify(searchMvpView, never()).showSearchedResources(searchedEntities);
        verify(searchMvpView).showProgressbar(false);
    }

    @Test
    public void testSearchNoResultFound() {
        resources = "savingsaccounts";
        List<SearchedEntity> savingSearch = new ArrayList<>();
        stubDataManagerGetSearch(Observable.just(savingSearch), exactMatch);

        searchPresenter.searchResources(query, resources, exactMatch);

        verify(searchMvpView).showProgressbar(true);
        verify(searchMvpView).showNoResultFound();
        verify(searchMvpView, never()).showSearchedResources(savingSearch);
        verify(searchMvpView, never()).showMessage(R.string.failed_to_fetch_resources_of_query);
        verify(searchMvpView).showProgressbar(false);
    }

    public List<SearchedEntity> filterEntities(final String entityType) {
        final List<SearchedEntity> search = new ArrayList<>();
        Observable.from(searchedEntities)
                .subscribe(new Action1<SearchedEntity>() {
                    @Override
                    public void call(SearchedEntity searchedEntity) {
                        if (searchedEntity.getEntityType().equals(entityType)) {
                            search.add(searchedEntity);
                        }
                    }
                });
        return search;
    }

    public void stubDataManagerGetSearch(Observable observable, boolean exactMatch) {
        when(dataManager.searchResources(query, resources, exactMatch)).thenReturn(observable);
    }

}