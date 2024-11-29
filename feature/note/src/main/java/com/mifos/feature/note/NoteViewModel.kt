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

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository_imp.NoteRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepositoryImp,
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
        Log.d("NoteScreendebug1", "id ${entityId.value} type ${entityType.value}")
        viewModelScope.launch {
            _noteUiState.value = NoteUiState.ShowProgressbar
            try {
                val notes = withContext(Dispatchers.IO) {
                    repository.getNotes(entityType.value, entityId.value)
                }
                if (notes.isNotEmpty()) {
                    _noteUiState.value = NoteUiState.ShowNote(notes)
                } else {
                    _noteUiState.value = NoteUiState.ShowEmptyNotes
                }
            } catch (e: Exception) {
                _noteUiState.value =
                    NoteUiState.ShowError(R.string.feature_note_failed_to_fetch_notes)
            }
            _isRefreshing.emit(false)
        }
    }
}
