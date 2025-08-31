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
import com.mifos.core.domain.useCases.DeleteNoteUseCase
import com.mifos.core.model.objects.notes.Note
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel(
    private val repository: NoteRepositoryImp,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<NoteState, NoteEvent, NoteAction>(
    initialState = NoteState(),
) {
    private val route = savedStateHandle.toRoute<NoteRoute>()

    init {

        getNoteOptionsAndObserveNetwork()
        mutableStateFlow.update {
            it.copy(
                resourceId = route.resourceId,
                resourceType = route.resourceType,
            )
        }
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
        route.resourceType?.let { entityType ->
            repository.retrieveListNotes(entityType, route.resourceId.toLong())
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> mutableStateFlow.update {
                            it.copy(
                                dialogState = NoteState.DialogState.Error(dataState.message),
                                isError = true,
                                isRefreshing = false,
                            )
                        }

                        is DataState.Loading -> {
                            if (!state.isRefreshing) {
                                mutableStateFlow.update {
                                    it.copy(
                                        dialogState = NoteState.DialogState.Loading,
                                        isRefreshing = false,
                                    )
                                }
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = null,
                                    notes = dataState.data,
                                    isError = false,
                                    expandedNoteId = null,
                                    isRefreshing = false,
                                )
                            }
                        }
                    }
                }
        }
    }

    private suspend fun deleteNote(id: Long?) {
        route.resourceType?.let { type ->
            id?.let { id ->
                deleteNoteUseCase(type, route.resourceId.toLong(), id).collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = NoteState.DialogState.Error(dataState.message),
                                    isError = true,
                                )
                            }
                        }

                        is DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = NoteState.DialogState.Loading,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = null,
                                )
                            }
                            getNoteOptionsAndObserveNetwork()
                        }
                    }
                }
            }
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
                getNoteOptionsAndObserveNetwork()
            }

            NoteAction.OnClickEditScreen -> sendEvent(NoteEvent.NavigateEditNote)
            NoteAction.OnClickAddScreen -> sendEvent(NoteEvent.NavigateAddNote)
            is NoteAction.DeleteNote -> {
                mutableStateFlow.update {
                    it.copy(
                        showDialog = false,
                        isError = false,
                    )
                }
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
    val resourceId: Int = -1,
    val resourceType: String? = null,
    val isRefreshing: Boolean = false,
    val notes: List<Note> = emptyList(),
    val showDialog: Boolean = false,
    val dialogState: DialogState? = null,
    val expandedNoteId: Long? = null,
    val isError: Boolean = false,
    val networkConnection: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
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
    data class DeleteNote(val id: Long?) : NoteAction
}
