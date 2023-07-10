package com.mifos.mifosxdroid.online

import com.mifos.api.datamanager.DataManagerSearch
import com.mifos.mifosxdroid.FakeRemoteDataSource.searchedEntity
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.online.search.SearchMvpView
import com.mifos.mifosxdroid.online.search.SearchPresenter
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import com.mifos.objects.SearchedEntity
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
 * Created by Rajan Maurya on 17/6/16.
 */
@RunWith(MockitoJUnitRunner::class)
class SearchPresenterTest {
    @get:Rule
    val mOverrideSchedulersRule = RxSchedulersOverrideRule()
    private lateinit var searchPresenter: SearchPresenter

    @Mock
    lateinit var dataManager: DataManagerSearch

    @Mock
    lateinit var searchMvpView: SearchMvpView
    private lateinit var searchedEntities: List<SearchedEntity>
    private var query = "1"
    private var resources: String? = null
    private val exactMatch = false

    @Before
    fun setUp() {
        searchPresenter = SearchPresenter(dataManager)
        searchPresenter.attachView(searchMvpView)
        searchedEntities = searchedEntity
    }

    @After
    fun tearDown() {
        searchPresenter.detachView()
    }

    @Test
    fun testSearchAll() {
        resources = "clients,groups,loans,savingsaccounts"
        stubDataManagerGetSearch(Observable.just(searchedEntities), exactMatch)
        searchPresenter.searchResources(query, resources, exactMatch)
        Mockito.verify(searchMvpView).showProgressbar(true)
        Mockito.verify(searchMvpView).showSearchedResources(searchedEntities)
        Mockito.verify(searchMvpView, Mockito.never())
            .showMessage(R.string.failed_to_fetch_resources_of_query)
        Mockito.verify(searchMvpView).showProgressbar(false)
    }

    @Test
    fun testSearchAllFails() {
        resources = "clients,groups,loans,savingsaccounts"
        stubDataManagerGetSearch(
            Observable.error(RuntimeException()),
            exactMatch
        )
        searchPresenter.searchResources(query, resources, exactMatch)
        Mockito.verify(searchMvpView).showProgressbar(true)
        Mockito.verify(searchMvpView).showMessage(R.string.failed_to_fetch_resources_of_query)
        Mockito.verify(searchMvpView, Mockito.never()).showSearchedResources(
            searchedEntities
        )
        Mockito.verify(searchMvpView).showProgressbar(false)
    }

    @Test
    fun testSearchClients() {
        resources = "clients"
        val clientSearch = filterEntities("CLIENT")
        stubDataManagerGetSearch(Observable.just(clientSearch), exactMatch)
        searchPresenter.searchResources(query, resources, exactMatch)
        Mockito.verify(searchMvpView).showProgressbar(true)
        Mockito.verify(searchMvpView).showSearchedResources(clientSearch)
        Mockito.verify(searchMvpView, Mockito.never())
            .showMessage(R.string.failed_to_fetch_resources_of_query)
        Mockito.verify(searchMvpView).showProgressbar(false)
    }

    @Test
    fun testSearchClientsFails() {
        resources = "clients"
        stubDataManagerGetSearch(
            Observable.error(RuntimeException()),
            exactMatch
        )
        searchPresenter.searchResources(query, resources, exactMatch)
        Mockito.verify(searchMvpView).showProgressbar(true)
        Mockito.verify(searchMvpView).showMessage(R.string.failed_to_fetch_resources_of_query)
        Mockito.verify(searchMvpView, Mockito.never()).showSearchedResources(
            searchedEntities
        )
        Mockito.verify(searchMvpView).showProgressbar(false)
    }

    @Test
    fun testSearchGroups() {
        resources = "groups"
        val groupSearch = filterEntities("GROUP")
        stubDataManagerGetSearch(Observable.just(groupSearch), exactMatch)
        searchPresenter.searchResources(query, resources, exactMatch)
        Mockito.verify(searchMvpView).showProgressbar(true)
        Mockito.verify(searchMvpView).showSearchedResources(groupSearch)
        Mockito.verify(searchMvpView, Mockito.never())
            .showMessage(R.string.failed_to_fetch_resources_of_query)
        Mockito.verify(searchMvpView).showProgressbar(false)
    }

    @Test
    fun testSearchGroupsFails() {
        resources = "groups"
        stubDataManagerGetSearch(
            Observable.error(RuntimeException()),
            exactMatch
        )
        searchPresenter.searchResources(query, resources, exactMatch)
        Mockito.verify(searchMvpView).showProgressbar(true)
        Mockito.verify(searchMvpView).showMessage(R.string.failed_to_fetch_resources_of_query)
        Mockito.verify(searchMvpView, Mockito.never()).showSearchedResources(
            searchedEntities
        )
        Mockito.verify(searchMvpView).showProgressbar(false)
    }

    @Test
    fun testSearchLoans() {
        resources = "loans"
        val loansSearch = filterEntities("LOAN")
        stubDataManagerGetSearch(Observable.just(loansSearch), exactMatch)
        searchPresenter.searchResources(query, resources, exactMatch)
        Mockito.verify(searchMvpView).showProgressbar(true)
        Mockito.verify(searchMvpView).showSearchedResources(loansSearch)
        Mockito.verify(searchMvpView, Mockito.never())
            .showMessage(R.string.failed_to_fetch_resources_of_query)
        Mockito.verify(searchMvpView, Mockito.never()).showNoResultFound()
        Mockito.verify(searchMvpView).showProgressbar(false)
    }

    @Test
    fun testSearchLoansFails() {
        resources = "loans"
        stubDataManagerGetSearch(
            Observable.error(RuntimeException()),
            exactMatch
        )
        searchPresenter.searchResources(query, resources, exactMatch)
        Mockito.verify(searchMvpView).showProgressbar(true)
        Mockito.verify(searchMvpView).showMessage(R.string.failed_to_fetch_resources_of_query)
        Mockito.verify(searchMvpView, Mockito.never()).showSearchedResources(
            searchedEntities
        )
        Mockito.verify(searchMvpView).showProgressbar(false)
    }

    @Test
    fun testSearchSavings() {
        resources = "savingsaccounts"
        val savingSearch = filterEntities("SAVING")
        stubDataManagerGetSearch(Observable.just(savingSearch), exactMatch)
        searchPresenter.searchResources(query, resources, exactMatch)
        Mockito.verify(searchMvpView).showProgressbar(true)
        Mockito.verify(searchMvpView).showSearchedResources(savingSearch)
        Mockito.verify(searchMvpView, Mockito.never())
            .showMessage(R.string.failed_to_fetch_resources_of_query)
        Mockito.verify(searchMvpView).showProgressbar(false)
    }

    @Test
    fun testSearchSavingsFails() {
        resources = "savingsaccounts"
        stubDataManagerGetSearch(
            Observable.error(RuntimeException()),
            exactMatch
        )
        searchPresenter.searchResources(query, resources, exactMatch)
        Mockito.verify(searchMvpView).showProgressbar(true)
        Mockito.verify(searchMvpView).showMessage(R.string.failed_to_fetch_resources_of_query)
        Mockito.verify(searchMvpView, Mockito.never()).showSearchedResources(
            searchedEntities
        )
        Mockito.verify(searchMvpView).showProgressbar(false)
    }

    @Test
    fun testSearchExactMatch() {
        query = "Madenge Pimbi"
        resources = "clients"
        val exactSearch: MutableList<SearchedEntity> = ArrayList()
        Observable.from(searchedEntities)
            .subscribe { searchedEntity ->
                if (searchedEntity.entityName == "Madenge Pimbi") {
                    exactSearch.add(searchedEntity)
                }
            }
        stubDataManagerGetSearch(Observable.just(exactSearch), !exactMatch)
        searchPresenter.searchResources(query, resources, !exactMatch)
        Mockito.verify(searchMvpView).showProgressbar(true)
        Mockito.verify(searchMvpView).showSearchedResources(exactSearch)
        Mockito.verify(searchMvpView, Mockito.never())
            .showMessage(R.string.failed_to_fetch_resources_of_query)
        Mockito.verify(searchMvpView).showProgressbar(false)
    }

    @Test
    fun testSearchExactMatchFails() {
        query = "Smith R"
        resources = "clients"
        stubDataManagerGetSearch(
            Observable.error(RuntimeException()),
            !exactMatch
        )
        searchPresenter.searchResources(query, resources, !exactMatch)
        Mockito.verify(searchMvpView).showProgressbar(true)
        Mockito.verify(searchMvpView).showMessage(R.string.failed_to_fetch_resources_of_query)
        Mockito.verify(searchMvpView, Mockito.never()).showSearchedResources(
            searchedEntities
        )
        Mockito.verify(searchMvpView).showProgressbar(false)
    }

    @Test
    fun testSearchNoResultFound() {
        resources = "savingsaccounts"
        val savingSearch: List<SearchedEntity> = ArrayList()
        stubDataManagerGetSearch(Observable.just(savingSearch), exactMatch)
        searchPresenter.searchResources(query, resources, exactMatch)
        Mockito.verify(searchMvpView).showProgressbar(true)
        Mockito.verify(searchMvpView).showNoResultFound()
        Mockito.verify(searchMvpView, Mockito.never()).showSearchedResources(savingSearch)
        Mockito.verify(searchMvpView, Mockito.never())
            .showMessage(R.string.failed_to_fetch_resources_of_query)
        Mockito.verify(searchMvpView).showProgressbar(false)
    }

    private fun filterEntities(entityType: String): List<SearchedEntity> {
        val search: MutableList<SearchedEntity> = ArrayList()
        Observable.from(searchedEntities)
            .subscribe { searchedEntity ->
                if (searchedEntity.entityType == entityType) {
                    search.add(searchedEntity)
                }
            }
        return search
    }

    private fun stubDataManagerGetSearch(
        observable: Observable<List<SearchedEntity>>,
        exactMatch: Boolean
    ) {
        Mockito.`when`(dataManager.searchResources(query, resources, exactMatch))
            .thenReturn(observable)
    }
}