package com.mifos.feature.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repositoryImp.NoteRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val repository: NoteRepositoryImp) : ViewModel() {

    private val _noteUiState = MutableStateFlow<NoteUiState>(NoteUiState.ShowProgressbar)
    val noteUiState: StateFlow<NoteUiState> get() = _noteUiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    fun refresh(type: String?, id: Int) {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            loadNote(type, id)
        }
    }

    /**
     * This method load the Notes.
     * Response: List<Note>
    </Note> */
    fun loadNote(type: String?, id: Int) {
        viewModelScope.launch {
            _noteUiState.value = NoteUiState.ShowProgressbar
            try {
                val notes = withContext(Dispatchers.IO) {
                    repository.getNotes(type, id)
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
