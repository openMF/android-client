/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.note.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repositoryImp.NoteRepositoryImp
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.notes.Note
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.note.navigation.NoteScreenRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel(
    private val repository: NoteRepositoryImp,
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<NoteState, NoteEvent, NoteAction>(
    initialState = NoteState(),
) {

    private val route = savedStateHandle.toRoute<NoteScreenRoute>()

    init {
        mutableStateFlow.update {
            it.copy(
                entityId = route.entityId,
                entityType = route.entityType,
            )
        }
        getNoteOptionsAndObserveNetwork()
    }

    private fun getNoteOptionsAndObserveNetwork() {
        viewModelScope.launch {
            observeNetwork()
            loadNote()
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
            }
        }
    }

    private suspend fun loadNote() {
        route.entityType?.let { entityType ->
            repository.retrieveListNotes(entityType, route.entityId.toLong())
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> mutableStateFlow.update {
                            it.copy(
                                dialogState = NoteState.DialogState.Error(dataState.message),
                            )
                        }

                        is DataState.Loading -> {
                            if (!state.isRefreshing) {
                                mutableStateFlow.update {
                                    it.copy(
                                        dialogState = NoteState.DialogState.Loading,
                                    )
                                }
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = null,
                                    notes = dataState.data,
                                    isDeleteError = false,
                                    isRefreshing = false,
                                )
                            }
                        }
                    }
                }
        }

        mutableStateFlow.update {
            it.copy(
                isRefreshing = false,
            )
        }
    }

    private suspend fun deleteNote(id: Long?) {
        route.entityType?.let { type ->
            mutableStateFlow.update {
                it.copy(
                    dialogState = NoteState.DialogState.Loading,
                )
            }
            try {
                id?.let { id ->
                    repository.deleteNote(type, route.entityId.toLong(), id)
                }
                getNoteOptionsAndObserveNetwork()
                mutableStateFlow.update {
                    it.copy(
                        isDeleteError = false,
                    )
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = NoteState.DialogState.Error(e.message.toString()),
                        isDeleteError = true,
                    )
                }
            }
        }
    }

    override fun handleAction(action: NoteAction) {
        when (action) {
            NoteAction.NavigateBack -> sendEvent(NoteEvent.NavigateBack)
            NoteAction.OnRetry -> {
                getNoteOptionsAndObserveNetwork()
            }

            NoteAction.OnNext -> sendEvent(NoteEvent.NavigateNext)
            is NoteAction.DeleteNote -> {
                viewModelScope.launch {
                    deleteNote(action.id)
                }
            }

            NoteAction.OnRefresh -> {
                mutableStateFlow.update {
                    it.copy(isRefreshing = true)
                }
                getNoteOptionsAndObserveNetwork()
            }

            NoteAction.ShowDialog -> {
                mutableStateFlow.update {
                    it.copy(showDialog = true)
                }
            }

            NoteAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(showDialog = false)
                }
            }

            is NoteAction.OnToggleExpanded -> {
                mutableStateFlow.update { state ->
                    state.copy(
                        expandedNoteId = if (state.expandedNoteId == action.id) null else action.id,
                    )
                }
            }
        }
    }
}

data class NoteState(
    val entityId: Int = -1,
    val entityType: String? = null,
    val isRefreshing: Boolean = false,
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDialog: Boolean = false,
    val dialogState: DialogState? = null,
    val expandedNoteId: Long? = null,
    val isDeleteError: Boolean = false,
    val networkConnection: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }
}

sealed interface NoteEvent {
    data object NavigateBack : NoteEvent
    data object NavigateNext : NoteEvent
}

sealed interface NoteAction {
    data object NavigateBack : NoteAction
    data object OnRetry : NoteAction
    data object OnRefresh : NoteAction
    data object OnNext : NoteAction
    data object ShowDialog : NoteAction
    data object DismissDialog : NoteAction

    data class OnToggleExpanded(val id: Long?) : NoteAction

    data class DeleteNote(val id: Long?) : NoteAction
}
