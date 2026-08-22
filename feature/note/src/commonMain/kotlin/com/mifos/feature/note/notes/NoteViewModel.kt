/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.note.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.repository.NoteRepository
import com.mifos.core.domain.useCases.DeleteNoteUseCase
import com.mifos.core.model.objects.note.Note
import kpt.core.base.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel(
    private val repository: NoteRepository,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<NoteState, NoteEvent, NoteAction>(
    initialState = NoteState(),
) {
    private val route = savedStateHandle.toRoute<NoteRoute>()

    init {
        viewModelScope.launch {
            loadNote()
        }
        mutableStateFlow.update {
            it.copy(
                resourceId = route.resourceId,
                resourceType = route.resourceType,
            )
        }
    }

    private suspend fun loadNote() {
        route.resourceType?.let { entityType ->
            if (!state.isRefreshing) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = NoteState.DialogState.Loading,
                        isRefreshing = false,
                    )
                }
            }
            repository.retrieveListNotes(entityType, route.resourceId.toLong())
                .catch { error ->
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = NoteState.DialogState.Error(error.message.orEmpty()),
                            isRefreshing = false,
                        )
                    }
                }
                .collect { notes ->
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = null,
                            notes = notes,
                            expandedNoteId = null,
                            isRefreshing = false,
                        )
                    }
                }
        }
    }

    private suspend fun deleteNote(id: Long?) {
        mutableStateFlow.update {
            it.copy(
                dialogState = NoteState.DialogState.Loading,
            )
        }
        route.resourceType?.let { type ->
            id?.let { id ->
                try {
                    deleteNoteUseCase(type, route.resourceId.toLong(), id)
                    sendAction(NoteAction.Internal.ReceiveDeleteNoteResult)
                } catch (e: Exception) {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = NoteState.DialogState.Error(e.message.orEmpty()),
                        )
                    }
                }
            }
        }
    }

    private fun handleDeleteNoteResult() {
        mutableStateFlow.update {
            it.copy(
                dialogState = null,
            )
        }

        viewModelScope.launch {
            loadNote()
        }
    }

    override fun handleAction(action: NoteAction) {
        when (action) {
            NoteAction.NavigateBack -> {
                sendEvent(NoteEvent.NavigateBack)
                mutableStateFlow.update {
                    it.copy(expandedNoteId = null)
                }
            }

            NoteAction.OnRetry -> {
                viewModelScope.launch {
                    loadNote()
                }
            }

            NoteAction.OnClickEditScreen -> sendEvent(NoteEvent.NavigateEditNote)
            NoteAction.OnClickAddScreen -> sendEvent(NoteEvent.NavigateAddNote)
            is NoteAction.DeleteNote -> {
                viewModelScope.launch {
                    deleteNote(state.expandedNoteId)
                }
            }

            NoteAction.OnRefresh -> {
                mutableStateFlow.update {
                    it.copy(isRefreshing = true)
                }
                viewModelScope.launch {
                    loadNote()
                }
            }

            NoteAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                    )
                }
            }

            is NoteAction.OnToggleExpanded -> {
                mutableStateFlow.update { state ->
                    state.copy(
                        expandedNoteId = if (state.expandedNoteId == action.id) null else action.id,
                    )
                }
            }

            is NoteAction.ShowDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = NoteState.DialogState.ShowDialog,
                    )
                }
            }

            NoteAction.Internal.ReceiveDeleteNoteResult -> {
                handleDeleteNoteResult()
            }
        }
    }
}

data class NoteState(
    val resourceId: Int = -1,
    val resourceType: String? = null,
    val isRefreshing: Boolean = false,
    val notes: List<Note> = emptyList(),
    val dialogState: DialogState? = null,
    val expandedNoteId: Long? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data object ShowDialog : DialogState
    }
}

sealed interface NoteEvent {
    data object NavigateBack : NoteEvent
    data object NavigateEditNote : NoteEvent
    data object NavigateAddNote : NoteEvent
}

sealed interface NoteAction {
    data object NavigateBack : NoteAction
    data object OnRetry : NoteAction
    data object OnRefresh : NoteAction
    data object OnClickAddScreen : NoteAction
    data object OnClickEditScreen : NoteAction
    data object ShowDialog : NoteAction
    data object DismissDialog : NoteAction
    data class OnToggleExpanded(val id: Long?) : NoteAction
    data object DeleteNote : NoteAction

    sealed interface Internal : NoteAction {
        data object ReceiveDeleteNoteResult : Internal
    }
}
