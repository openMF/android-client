/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.searchrecord.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.searchrecord.SearchRecordRepository
import com.mifos.core.data.store.DataFreshness
import com.mifos.core.data.store.ScreenState
import com.mifos.core.model.objects.searchrecord.RecordType
import com.mifos.core.ui.store.BaseViewModel
import com.mifos.feature.searchrecord.navigation.SearchRecordRoute
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchRecordViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: SearchRecordRepository,
) : BaseViewModel<SearchRecordState, SearchRecordEvent, SearchRecordAction>(
    initialState = SearchRecordState(
        recordType = RecordType.valueOf(savedStateHandle.toRoute<SearchRecordRoute>().type),
    ),
) {

    private var searchJob: Job? = null

    init {
        observeSearchQuery()
    }

    override fun handleAction(action: SearchRecordAction) {
        when (action) {
            is SearchRecordAction.SearchQueryChanged -> {
                mutableStateFlow.value = state.copy(searchQuery = action.query)
            }

            SearchRecordAction.ClearSearch -> {
                searchJob?.cancel()
                mutableStateFlow.value = state.copy(
                    searchQuery = "",
                    screenState = ScreenState.Empty,
                )
            }

            SearchRecordAction.NavigateBack -> sendEvent(SearchRecordEvent.NavigateBack)

            is SearchRecordAction.SelectRecord -> {
                sendEvent(SearchRecordEvent.NavigateToRecord(action.record))
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            mutableStateFlow
                .map { it.searchQuery }
                .distinctUntilChanged()
                .collect { query ->
                    searchJob?.cancel()
                    if (query.isBlank()) {
                        mutableStateFlow.value = state.copy(screenState = ScreenState.Empty)
                    } else {
                        searchJob = launch {
                            delay(SEARCH_DEBOUNCE_DELAY_MS)
                            performSearch(query)
                        }
                    }
                }
        }
    }

    private fun performSearch(query: String) {
        mutableStateFlow.value = state.copy(screenState = ScreenState.Loading)
        viewModelScope.launch {
            repository.searchRecords(state.recordType, query)
                .catch { error ->
                    if (error is CancellationException) throw error
                    mutableStateFlow.value = state.copy(
                        screenState = ScreenState.Error(error = error),
                    )
                }
                .onEach { records ->
                    mutableStateFlow.value = state.copy(
                        screenState = if (records.isEmpty()) {
                            ScreenState.Empty
                        } else {
                            ScreenState.Content(
                                data = records,
                                freshness = DataFreshness.FRESH,
                            )
                        },
                    )
                }
                .collect()
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MS = 300L
    }
}
