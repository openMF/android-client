/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.search.ui

import androidx.lifecycle.viewModelScope
import com.mifos.core.data.search.SearchRepository
import template.core.base.store.screen.DataFreshness
import template.core.base.store.screen.ScreenState
import com.mifos.core.ui.store.BaseViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class SearchViewModel(
    private val searchRepository: SearchRepository,
) : BaseViewModel<SearchScreenState, Nothing, SearchAction>(initialState = SearchScreenState()) {

    private var searchJob: Job? = null

    override fun handleAction(action: SearchAction) {
        when (action) {
            is SearchAction.UpdateSearchText ->
                mutableStateFlow.value =
                    state.copy(searchText = action.searchText)

            is SearchAction.ClearSearchText -> {
                searchJob?.cancel()
                mutableStateFlow.value = state.copy(
                    searchText = "",
                    showEmptyError = false,
                    resultState = ScreenState.Empty,
                )
            }

            is SearchAction.UpdateSelectedFilter -> {
                mutableStateFlow.value = state.copy(selectedFilter = action.filter)
                triggerSearch()
            }

            is SearchAction.UpdateExactMatch -> {
                val current = state.exactMatch
                mutableStateFlow.value = state.copy(
                    exactMatch = if (current == null) true else !current,
                )
                triggerSearch()
            }

            is SearchAction.PerformSearch -> triggerSearch()
        }
    }

    private fun triggerSearch() {
        searchJob?.cancel()

        val query = state.searchText
        if (query.isEmpty()) {
            mutableStateFlow.value = state.copy(
                showEmptyError = true,
                resultState = ScreenState.Empty,
            )
            return
        }

        mutableStateFlow.value = state.copy(
            showEmptyError = false,
            resultState = ScreenState.Loading,
        )

        searchJob = viewModelScope.launch {
            val resource = state.selectedFilter?.let { getString(it.valueRes) }
            val nextState = try {
                val results = searchRepository.searchResources(
                    query = query,
                    resources = resource,
                    exactMatch = state.exactMatch,
                )
                if (results.isEmpty()) {
                    ScreenState.Empty
                } else {
                    ScreenState.Content(
                        data = results,
                        freshness = DataFreshness.FRESH,
                    )
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Throwable) {
                ScreenState.Error(error = error)
            }
            mutableStateFlow.value = state.copy(resultState = nextState)
        }
    }
}
