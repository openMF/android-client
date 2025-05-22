/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.note

import androidclient.feature.note.generated.resources.Res
import androidclient.feature.note.generated.resources.feature_note_failed_to_fetch_notes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repositoryImp.NoteRepositoryImp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteViewModel(
    private val repository: NoteRepositoryImp,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val entityId = savedStateHandle.getStateFlow(key = Constants.ENTITY_ID, initialValue = 0)
    val entityType: StateFlow<String?> = savedStateHandle.getStateFlow(key = Constants.ENTITY_TYPE, initialValue = null)

    private val _noteUiState = MutableStateFlow<NoteUiState>(NoteUiState.ShowProgressbar)
    val noteUiState: StateFlow<NoteUiState> get() = _noteUiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            loadNote()
        }
    }

    /**
     * This method load the Notes.
     * Response: List<Note>
     </Note> */
    fun loadNote() {
        //println("NoteScreen Debug: id=${entityId.value}, type=${entityType.value}")

        viewModelScope.launch {
            _noteUiState.value = NoteUiState.ShowProgressbar

            try {
                val entityTypeValue = entityType.value
                if (entityTypeValue != null) {
                    val notesFlow = repository.getNotes(entityTypeValue, entityId.value)

                    notesFlow?.collect { dataState ->
                        when (dataState) {
                            is DataState.Loading -> {
                                _noteUiState.value = NoteUiState.ShowProgressbar
                            }

                            is DataState.Success -> {
                                val notes = dataState.data

                                if (notes.isNullOrEmpty()) {
                                    _noteUiState.value = NoteUiState.ShowEmptyNotes
                                } else {
                                    _noteUiState.value = NoteUiState.ShowNote(notes)
                                }
                            }

                            is DataState.Error -> {
                                _noteUiState.value = NoteUiState.ShowError(
                                    message = Res.string.feature_note_failed_to_fetch_notes,
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _noteUiState.value = NoteUiState.ShowError(
                    message = Res.string.feature_note_failed_to_fetch_notes,
                )
            }

            _isRefreshing.emit(false)
        }
    }
}
