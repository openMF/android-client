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

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.SearchRepository
import com.mifos.core.`object`.SearchedEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
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
            searchJob = searchRepository.searchResources(
                query = state.value.searchText,
                resources = state.value.selectedFilter?.value,
                exactMatch = state.value.exactMatch,
            ).onStart {
                searchResultState.update { SearchResultState.Loading }
            }.catch { throwable ->
                searchResultState.update { SearchResultState.Error(throwable.message.toString()) }
            }.onEach { results ->
                if (results.isEmpty()) {
                    searchResultState.update { SearchResultState.Empty(false) }
                } else {
                    searchResultState.update { SearchResultState.Success(results) }
                }
            }.launchIn(viewModelScope)
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
)

sealed interface SearchScreenEvent {
    data class UpdateSearchText(val searchText: String) : SearchScreenEvent

    data class UpdateSelectedFilter(val filter: FilterOption? = null) : SearchScreenEvent

    data object ClearSearchText : SearchScreenEvent

    data object UpdateExactMatch : SearchScreenEvent

    data object PerformSearch : SearchScreenEvent
}

sealed class FilterOption(val label: String, val value: String) {

    data object Clients : FilterOption(label = "Clients", value = "clients")

    data object Groups : FilterOption(label = "Groups", value = "groups")

    data object LoanAccounts : FilterOption(label = "Loan Accounts", value = "loans")

    data object SavingsAccounts :
        FilterOption(label = "Savings Accounts", value = "savingsaccounts")

    companion object {
        val values = listOf(Clients, Groups, LoanAccounts, SavingsAccounts)
    }
}
