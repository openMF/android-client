/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.search

import androidclient.feature.search.generated.resources.Res
import androidclient.feature.search.generated.resources.feature_search_filter_options_clients_label
import androidclient.feature.search.generated.resources.feature_search_filter_options_clients_value
import androidclient.feature.search.generated.resources.feature_search_filter_options_groups_label
import androidclient.feature.search.generated.resources.feature_search_filter_options_groups_value
import androidclient.feature.search.generated.resources.feature_search_filter_options_loans_label
import androidclient.feature.search.generated.resources.feature_search_filter_options_loans_value
import androidclient.feature.search.generated.resources.feature_search_filter_options_savings_label
import androidclient.feature.search.generated.resources.feature_search_filter_options_savings_value
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.SearchRepository
import com.mifos.core.model.objects.SearchedEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

class SearchViewModel(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    var state = mutableStateOf(SearchScreenState())
        private set

    private val searchResultState = MutableStateFlow<SearchResultState>(SearchResultState.Empty())
    val searchResult = searchResultState.asStateFlow()

    private var searchJob: Job? = null

    fun onEvent(event: SearchScreenEvent) {
        when (event) {
            is SearchScreenEvent.UpdateSearchText -> {
                viewModelScope.launch {
                    state.value = state.value.copy(
                        searchText = event.searchText,
                    )
                }
            }

            is SearchScreenEvent.ClearSearchText -> {
                viewModelScope.launch {
                    state.value = state.value.copy(
                        searchText = "",
                    )

                    searchResultState.update { SearchResultState.Empty() }
                }
            }

            is SearchScreenEvent.UpdateSelectedFilter -> {
                viewModelScope.launch {
                    state.value = state.value.copy(
                        selectedFilter = event.filter,
                    )

                    getSearchResult()
                }
            }

            is SearchScreenEvent.UpdateExactMatch -> {
                viewModelScope.launch {
                    state.value = state.value.copy(
                        exactMatch = if (state.value.exactMatch == null) {
                            true
                        } else {
                            !state.value.exactMatch!!
                        },
                    )

                    getSearchResult()
                }
            }

            is SearchScreenEvent.PerformSearch -> {
                viewModelScope.launch {
                    getSearchResult()
                }
            }
        }
    }

    private suspend fun getSearchResult() {
        searchJob?.cancel()

        if (state.value.searchText.isNotEmpty()) {
            state.value = state.value.copy(showEmptyError = false)
            searchJob = searchRepository.searchResources(
                query = state.value.searchText,
                resources = state.value.selectedFilter?.let { getString(it.valueRes) },
                exactMatch = state.value.exactMatch,
            ).onStart {
                searchResultState.update { SearchResultState.Loading }
            }.catch { throwable ->
                searchResultState.update { SearchResultState.Error(throwable.message.toString()) }
            }
                .onEach { resultState ->
                    when (resultState) {
                        is DataState.Success -> {
                            val results = resultState.data
                            if (results.isEmpty()) {
                                searchResultState.update { SearchResultState.Empty(false) }
                            } else {
                                searchResultState.update { SearchResultState.Success(results) }
                            }
                        }

                        is DataState.Error -> {
                            searchResultState.update { SearchResultState.Error(resultState.message) }
                        }

                        is DataState.Loading -> {
                            searchResultState.update { SearchResultState.Loading }
                        }
                    }
                }
                .launchIn(viewModelScope)
        } else {
            state.value = state.value.copy(showEmptyError = true)
            searchResultState.update { SearchResultState.Empty() }
        }
    }
}

sealed interface SearchResultState {
    data object Loading : SearchResultState
    data class Empty(val initial: Boolean = true) : SearchResultState
    data class Error(val message: String) : SearchResultState
    data class Success(val results: List<SearchedEntity>) : SearchResultState
}

data class SearchScreenState(
    val searchText: String = "",
    val selectedFilter: FilterOption? = null,
    val exactMatch: Boolean? = null,
    val showEmptyError: Boolean = false,
)

sealed interface SearchScreenEvent {
    data class UpdateSearchText(val searchText: String) : SearchScreenEvent

    data class UpdateSelectedFilter(val filter: FilterOption? = null) : SearchScreenEvent

    data object ClearSearchText : SearchScreenEvent

    data object UpdateExactMatch : SearchScreenEvent

    data object PerformSearch : SearchScreenEvent
}

sealed class FilterOption(val labelRes: StringResource, val valueRes: StringResource) {

    data object Clients : FilterOption(
        labelRes = Res.string.feature_search_filter_options_clients_label,
        valueRes = Res.string.feature_search_filter_options_clients_value,
    )

    data object Groups : FilterOption(
        labelRes = Res.string.feature_search_filter_options_groups_label,
        valueRes = Res.string.feature_search_filter_options_groups_value,
    )

    data object LoanAccounts : FilterOption(
        labelRes = Res.string.feature_search_filter_options_loans_label,
        valueRes = Res.string.feature_search_filter_options_loans_value,
    )

    data object SavingsAccounts : FilterOption(
        labelRes = Res.string.feature_search_filter_options_savings_label,
        valueRes = Res.string.feature_search_filter_options_savings_value,
    )

    companion object {
        val values = listOf(Clients, Groups, LoanAccounts, SavingsAccounts)
    }
}
