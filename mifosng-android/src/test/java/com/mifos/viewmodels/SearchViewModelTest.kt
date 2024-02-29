package com.mifos.viewmodels

import com.mifos.core.common.utils.Resource
import com.mifos.objects.SearchedEntity
import com.mifos.mifosxdroid.online.search.SearchUseCase
import com.mifos.mifosxdroid.online.search.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by Aditya Gupta on 02/09/23.
 */
@RunWith(MockitoJUnitRunner::class)
class SearchViewModelTest {

    @Mock
    lateinit var searchUseCase: SearchUseCase

    private lateinit var searchViewModel: SearchViewModel

    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        searchViewModel = SearchViewModel(searchUseCase)
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testSearchAll_SuccessfulSearchAllReceivedFromUseCase_ReturnsSuccess() {
        Mockito.`when`(
            searchUseCase(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
            )
        ).thenReturn(
            flowOf(Resource.Success(listOf(SearchedEntity())))
        )

        runTest {
            searchViewModel.searchResources("query", "resources", false)
        }

        assertNotEquals(0, searchViewModel.searchUiState.value.searchedEntities.size)
        assertEquals(searchViewModel.searchUiState.value.isLoading, false)
        assertNull(searchViewModel.searchUiState.value.error)
    }

    @Test
    fun testSearchAll_UnsuccessfulSearchAllReceivedFromUseCase_ReturnsError() {
        Mockito.`when`(
            searchUseCase(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
            )
        ).thenReturn(
            flowOf(Resource.Error("some error message"))
        )

        runTest {
            searchViewModel.searchResources("query", "resources", false)
        }

        assertEquals(0, searchViewModel.searchUiState.value.searchedEntities.size)
        assertEquals(searchViewModel.searchUiState.value.isLoading, false)
        assertNotNull(searchViewModel.searchUiState.value.error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
