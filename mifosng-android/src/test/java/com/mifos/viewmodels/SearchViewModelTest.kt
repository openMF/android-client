package com.mifos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mifos.core.objects.SearchedEntity
import com.mifos.mifosxdroid.online.search.SearchRepository
import com.mifos.mifosxdroid.online.search.SearchUiState
import com.mifos.mifosxdroid.online.search.SearchViewModel
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import rx.Observable

/**
 * Created by Aditya Gupta on 02/09/23.
 */
@RunWith(MockitoJUnitRunner::class)
class SearchViewModelTest {

    @get:Rule
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var searchRepository: SearchRepository

    @Mock
    lateinit var searchUiStateObserver: Observer<SearchUiState>

    private lateinit var searchViewModel: SearchViewModel

    @Mock
    private lateinit var searchedEntities: List<SearchedEntity>


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        searchViewModel = SearchViewModel(searchRepository)
        searchViewModel.searchUiState.observeForever(searchUiStateObserver)
    }


    @Test
    fun testSearchAll_SuccessfulSearchAllReceivedFromRepository_ReturnsSuccess() {

        Mockito.`when`(
            searchRepository.searchResources(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
            )
        ).thenReturn(
            Observable.just(searchedEntities)
        )
        searchViewModel.searchResources("query", "resources", false)
        Mockito.verify(searchUiStateObserver).onChanged(SearchUiState.ShowProgress(true))
        Mockito.verify(searchUiStateObserver, Mockito.never())
            .onChanged(SearchUiState.ShowError("error"))
        Mockito.verify(searchUiStateObserver)
            .onChanged(SearchUiState.ShowSearchedResources(searchedEntities))
    }

    @Test
    fun testSearchAll_UnsuccessfulSearchAllReceivedFromRepository_ReturnsError() {
        Mockito.`when`(
            searchRepository.searchResources(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
            )
        ).thenReturn(
            Observable.error(RuntimeException("some error message"))
        )
        searchViewModel.searchResources("query", "resources", false)
        Mockito.verify(searchUiStateObserver).onChanged(SearchUiState.ShowProgress(true))
        Mockito.verify(searchUiStateObserver)
            .onChanged(SearchUiState.ShowError("some error message"))
        Mockito.verify(searchUiStateObserver, Mockito.never())
            .onChanged(SearchUiState.ShowSearchedResources(searchedEntities))
    }

    @After
    fun tearDown() {
        searchViewModel.searchUiState.removeObserver(searchUiStateObserver)
    }
}