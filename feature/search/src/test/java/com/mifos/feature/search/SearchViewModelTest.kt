package com.mifos.feature.search

import com.mifos.core.testing.repository.TestSearchRepository
import com.mifos.core.testing.util.MainDispatcherRule
import com.mifos.core.ui.util.SearchResultPreviewData
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertIsNot


class SearchViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestSearchRepository()
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        viewModel = SearchViewModel(repository)
    }

    @Test
    fun testStateInitiallyIsEmpty() = runTest {
        assertEquals(SearchResultState.Empty(true), viewModel.searchResult.value)
    }

    @Test
    fun performSearchAndReturnEmptyResult() = runTest{
        repository.addSampleResults(SearchResultPreviewData.searchResults)

        viewModel.onEvent(SearchScreenEvent.UpdateSearchText("kkk"))
        viewModel.onEvent(SearchScreenEvent.PerformSearch)

        assertEquals(SearchResultState.Empty(false), viewModel.searchResult.value)
    }

    @Test
    fun performSearchAndReturnSuccessResult() = runTest {
        repository.addSampleResults(SearchResultPreviewData.searchResults)

        viewModel.onEvent(SearchScreenEvent.UpdateSearchText("John"))
        viewModel.onEvent(SearchScreenEvent.PerformSearch)

        assertIs<SearchResultState.Success>(viewModel.searchResult.value)
    }

    @Test
    fun performSearchAndClearSearchText_StateShouldBeEmpty() = runTest {
        repository.addSampleResults(SearchResultPreviewData.searchResults)

        viewModel.onEvent(SearchScreenEvent.UpdateSearchText("Account"))
        viewModel.onEvent(SearchScreenEvent.PerformSearch)

        assertIs<SearchResultState.Success>(viewModel.searchResult.value)

        viewModel.onEvent(SearchScreenEvent.ClearSearchText)

        assertIs<SearchResultState.Empty>(viewModel.searchResult.value)
    }

    @Test
    fun performSearchWithFilterResources_ResultShouldBeEmpty() = runTest {
        repository.addSampleResults(SearchResultPreviewData.searchResults)

        viewModel.onEvent(SearchScreenEvent.UpdateSearchText("Account"))
        viewModel.onEvent(SearchScreenEvent.UpdateSelectedFilter(FilterOption.Clients))
        viewModel.onEvent(SearchScreenEvent.PerformSearch)

        assertIs<SearchResultState.Empty>(viewModel.searchResult.value)
    }

    @Test
    fun performSearchWithFilterResources_ResultShouldBeSuccess() = runTest {
        repository.addSampleResults(SearchResultPreviewData.searchResults)

        viewModel.onEvent(SearchScreenEvent.UpdateSearchText("Group"))
        viewModel.onEvent(SearchScreenEvent.UpdateSelectedFilter(FilterOption.Groups))
        viewModel.onEvent(SearchScreenEvent.PerformSearch)

        assertIsNot<SearchResultState.Empty>(viewModel.searchResult.value)
        assertIs<SearchResultState.Success>(viewModel.searchResult.value)
    }

    @Test
    fun performSearchWithExactMatch_ReturnEmptyResult() {
        repository.addSampleResults(SearchResultPreviewData.searchResults)

        viewModel.onEvent(SearchScreenEvent.UpdateSearchText("Group"))
        viewModel.onEvent(SearchScreenEvent.UpdateExactMatch)
        viewModel.onEvent(SearchScreenEvent.PerformSearch)

        assertIs<SearchResultState.Empty>(viewModel.searchResult.value)
        assertIsNot<SearchResultState.Success>(viewModel.searchResult.value)
    }

    @Test
    fun performSearchWithExactMatch_ReturnSuccessResult() {
        repository.addSampleResults(SearchResultPreviewData.searchResults)

        viewModel.onEvent(SearchScreenEvent.UpdateSearchText("456789123"))
        viewModel.onEvent(SearchScreenEvent.UpdateExactMatch)
        viewModel.onEvent(SearchScreenEvent.PerformSearch)

        assertIsNot<SearchResultState.Empty>(viewModel.searchResult.value)
        assertIs<SearchResultState.Success>(viewModel.searchResult.value)
    }

    @Test
    fun performSearchWithExactMatchAndFilter_ReturnSuccessResult() {
        repository.addSampleResults(SearchResultPreviewData.searchResults)

        viewModel.onEvent(SearchScreenEvent.UpdateSearchText("456789123"))
        viewModel.onEvent(SearchScreenEvent.UpdateSelectedFilter(FilterOption.Groups))
        viewModel.onEvent(SearchScreenEvent.UpdateExactMatch)
        viewModel.onEvent(SearchScreenEvent.PerformSearch)

        assertIsNot<SearchResultState.Empty>(viewModel.searchResult.value)
        assertIs<SearchResultState.Success>(viewModel.searchResult.value)
    }
}
