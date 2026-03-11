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
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.repository.SearchRecordRepository
import com.mifos.core.model.objects.searchrecord.GenericSearchRecord
import com.mifos.core.model.objects.searchrecord.RecordType
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.searchrecord.navigation.SearchRecordRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class SearchRecordViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: SearchRecordRepository,
) : BaseViewModel<SearchRecordState, SearchRecordEvent, SearchRecordAction>(
    initialState = SearchRecordState(),
) {

    private val route = savedStateHandle.toRoute<SearchRecordRoute>()
    private val recordType: RecordType = RecordType.valueOf(route.type)
    private var searchJob: Job? = null

    init {
        mutableStateFlow.update {
            it.copy(
                displayTitle = when (recordType) {
                    RecordType.ADDRESS -> "Address"
                    RecordType.IDENTIFIER -> "Identifiers"
                },
            )
        }
        observeSearchQuery()
    }

    override fun handleAction(action: SearchRecordAction) {
        when (action) {
            is SearchRecordAction.SearchQueryChanged -> {
                mutableStateFlow.update { it.copy(searchQuery = action.query) }
            }

            SearchRecordAction.ClearSearch -> {
                mutableStateFlow.update {
                    it.copy(
                        searchQuery = "",
                        searchRecords = emptyList(),
                        isNoResultsFound = false,
                        dialogState = null,
                    )
                }
            }

            SearchRecordAction.NavigateBack -> {
                sendEvent(SearchRecordEvent.NavigateBack)
            }

            is SearchRecordAction.SelectRecord -> {
                sendEvent(SearchRecordEvent.NavigateToRecord(action.record))
            }

            SearchRecordAction.CloseDialog -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            mutableStateFlow
                .map { it.searchQuery }
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        mutableStateFlow.update {
                            it.copy(
                                searchRecords = emptyList(),
                                isNoResultsFound = false,
                                dialogState = null,
                            )
                        }
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
        mutableStateFlow.update {
            it.copy(dialogState = SearchRecordState.DialogState.Loading)
        }

        repository.searchRecords(recordType, query)
            .collect { result ->
                result.onSuccess { records ->
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = null,
                            searchRecords = records,
                            isNoResultsFound = records.isEmpty(),
                        )
                    }
                }
                result.onFailure { exception ->
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = SearchRecordState.DialogState.Error(
                                message = exception.message ?: "",
                                messageRes = Res.string.error_searching_records,
                            ),
                        )
                    }
                }
            }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MS = 300L
    }
}

data class SearchRecordState(
    val searchQuery: String = "",
    val displayTitle: String = "",
    val searchRecords: List<GenericSearchRecord> = emptyList(),
    val dialogState: DialogState? = null,
    val isNoResultsFound: Boolean = false,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String, val messageRes: StringResource? = null) : DialogState
    }
}

sealed interface SearchRecordEvent {
    data object NavigateBack : SearchRecordEvent
    data class NavigateToRecord(val record: GenericSearchRecord) : SearchRecordEvent
}

sealed interface SearchRecordAction {
    data class SearchQueryChanged(val query: String) : SearchRecordAction
    data object ClearSearch : SearchRecordAction
    data object NavigateBack : SearchRecordAction
    data class SelectRecord(val record: GenericSearchRecord) : SearchRecordAction
    data object CloseDialog : SearchRecordAction
}
