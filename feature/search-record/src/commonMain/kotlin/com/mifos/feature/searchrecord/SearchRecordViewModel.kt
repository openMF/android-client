/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.searchrecord

import androidclient.feature.search_record.generated.resources.Res
import androidclient.feature.search_record.generated.resources.error_searching_records
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.repository.SearchRecordRepository
import com.mifos.core.model.objects.searchrecord.RecordType
import com.mifos.feature.searchrecord.navigation.SearchRecordRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchRecordViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: SearchRecordRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<SearchRecordRoute>()

    private val recordType: RecordType = try {
        RecordType.valueOf(route.recordType.uppercase())
    } catch (e: IllegalArgumentException) {
        RecordType.ADDRESS
    }

    val searchLabel: String = "Search ${recordType.displayName}"
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<SearchRecordUiState>(SearchRecordUiState.Idle)
    val uiState: StateFlow<SearchRecordUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery.collectLatest { query ->
                if (query.isBlank()) {
                    _uiState.update { SearchRecordUiState.EmptyQuery }
                    searchJob?.cancel()
                } else {
                    searchJob?.cancel()
                    searchJob = launch {
                        delay(SEARCH_DEBOUNCE_DELAY_MS)
                        performSearch(query)
                    }
                }
            }
        }
    }

    private suspend fun performSearch(query: String) {
        _uiState.update { SearchRecordUiState.Loading }

        repository.searchRecords(recordType, query)
            .collectLatest { result ->
                result.onSuccess { records ->
                    _uiState.update {
                        if (records.isEmpty()) {
                            SearchRecordUiState.NoResults
                        } else {
                            SearchRecordUiState.Success(records)
                        }
                    }
                }
                result.onFailure { exception ->
                    _uiState.update {
                        SearchRecordUiState.Error(
                            message = exception.message ?: "",
                            messageRes = Res.string.error_searching_records,
                        )
                    }
                }
            }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.update { query }
    }

    fun clearSearch() {
        _searchQuery.update { "" }
        _uiState.update { SearchRecordUiState.Idle }
    }
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MS = 300L
    }
}
