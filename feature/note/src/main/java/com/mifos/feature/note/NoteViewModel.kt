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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repositoryImp.NoteRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepositoryImp,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val entityId = savedStateHandle.getStateFlow(key = Constants.ENTITY_ID, initialValue = "0")
    val entityType: StateFlow<String?> =
        savedStateHandle.getStateFlow(key = Constants.ENTITY_TYPE, initialValue = null)

    private val _noteUiState = MutableStateFlow<NoteUiState>(NoteUiState.ShowProgressbar)
    val noteUiState: StateFlow<NoteUiState> get() = _noteUiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            loadNote()
            _isRefreshing.emit(false)
        }
    }

    /**
     * This method load the Notes.
     * Response: List<Note>
     </Note> */
    fun loadNote() {
        viewModelScope.launch {
            _noteUiState.value = NoteUiState.ShowProgressbar

            repository.getNotes(entityType.value, entityId.value.toInt())
                .catch {
                    _noteUiState.value =
                        NoteUiState.ShowError(R.string.feature_note_failed_to_fetch_notes)
                }.collect { notes ->
                    if (notes.isNotEmpty()) {
                        _noteUiState.value = NoteUiState.ShowNote(notes)
                    } else {
                        _noteUiState.value = NoteUiState.ShowEmptyNotes
                    }
                }
        }
    }
}
