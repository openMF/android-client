/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.note.addEditNotes

import androidclient.feature.note.generated.resources.Res
import androidclient.feature.note.generated.resources.feature_note_Unexpected_error
import androidclient.feature.note.generated.resources.feature_note_add_note
import androidclient.feature.note.generated.resources.feature_note_button_add
import androidclient.feature.note.generated.resources.feature_note_button_update
import androidclient.feature.note.generated.resources.feature_note_edit_note_label
import androidclient.feature.note.generated.resources.feature_note_update_note
import androidclient.feature.note.generated.resources.feature_note_write_note_label
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repositoryImp.NoteRepositoryImp
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.AddNoteUseCase
import com.mifos.core.domain.useCases.UpdateNoteUseCase
import com.mifos.core.model.objects.payloads.NotesPayload
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class AddEditNoteViewModel(
    private val repository: NoteRepositoryImp,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<AddEditNoteState, AddEditNoteEvent, AddEditNoteAction>(
    initialState = AddEditNoteState(),
) {
    private val route = savedStateHandle.toRoute<AddEditNoteRoute>()

    init {
        getAddEditNoteOptionsAndObserveNetwork()

        if (route.noteId != null) {
            viewModelScope.launch {
                loadSingleNote()
            }
            mutableStateFlow.update {
                it.copy(
                    editEnabled = true,
                    addUpdateButton = Res.string.feature_note_button_update,
                    label = Res.string.feature_note_edit_note_label,
                    title = Res.string.feature_note_update_note,
                )
            }
        }

        mutableStateFlow.update {
            it.copy(
                resourceId = route.resourceId,
                resourceType = route.resourceType,
            )
        }
    }

    private fun getAddEditNoteOptionsAndObserveNetwork() {
        viewModelScope.launch {
            observeNetwork()
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
            }
        }
    }

    private suspend fun loadSingleNote() {
        route.resourceType?.let { type ->
            route.noteId?.let { id ->
                repository.retrieveNote(type, route.resourceId.toLong(), id)
                    .collect { dataState ->
                        when (dataState) {
                            is DataState.Error -> {
                                mutableStateFlow.update {
                                    it.copy(dialogState = AddEditNoteState.DialogState.Error(Res.string.feature_note_Unexpected_error))
                                }
                            }

                            DataState.Loading -> {
                                mutableStateFlow.update {
                                    it.copy(dialogState = AddEditNoteState.DialogState.Loading)
                                }
                            }

                            is DataState.Success -> {
                                mutableStateFlow.update {
                                    it.copy(
                                        dialogState = null,
                                        textFieldNotesPayload = NotesPayload(dataState.data.note),
                                        notesPayloadInitialData = dataState.data.note,
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }

    private suspend fun addNote(notesPayload: NotesPayload) {
        route.resourceType?.let { type ->
            addNoteUseCase(route.resourceType, route.resourceId.toLong(), notesPayload)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = AddEditNoteState.DialogState.Error(Res.string.feature_note_Unexpected_error),
                                )
                            }
                        }

                        DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(dialogState = AddEditNoteState.DialogState.Loading)
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = null,
                                    notesPayloadInitialData = state.textFieldNotesPayload.note,
                                )
                            }
                            sendEvent(AddEditNoteEvent.NavigateBackWithUpdateList)
                        }
                    }
                }
        }
    }

    private suspend fun editNote(notesPayload: NotesPayload) {
        route.resourceType?.let { type ->
            route.noteId?.let { id ->
                updateNoteUseCase(type, route.resourceId.toLong(), id, notesPayload)
                    .collect { dataState ->
                        when (dataState) {
                            is DataState.Error -> {
                                mutableStateFlow.update {
                                    it.copy(
                                        dialogState = AddEditNoteState.DialogState.Error(Res.string.feature_note_Unexpected_error),
                                    )
                                }
                            }

                            DataState.Loading -> {
                                mutableStateFlow.update {
                                    it.copy(dialogState = AddEditNoteState.DialogState.Loading)
                                }
                            }

                            is DataState.Success -> {
                                mutableStateFlow.update {
                                    it.copy(
                                        dialogState = null,
                                        notesPayloadInitialData = state.textFieldNotesPayload.note,
                                    )
                                }

                                sendEvent(AddEditNoteEvent.NavigateBackWithUpdateList)
                            }
                        }
                    }
            }
        }
    }

    override fun handleAction(action: AddEditNoteAction) {
        when (action) {
            AddEditNoteAction.NavigateBack -> {
                sendEvent(AddEditNoteEvent.NavigateBack)
            }

            AddEditNoteAction.NavigateBackWithUpdateList -> {
                sendEvent(AddEditNoteEvent.NavigateBackWithUpdateList)
            }

            is AddEditNoteAction.AddNote -> {
                viewModelScope.launch {
                    addNote(action.notesPayload)
                }
            }

            is AddEditNoteAction.EditNote -> {
                viewModelScope.launch {
                    editNote(action.notesPayload)
                }
            }

            AddEditNoteAction.OnRetry -> {
                if (state.editEnabled) {
                    viewModelScope.launch {
                        loadSingleNote()
                        editNote(state.textFieldNotesPayload)
                    }
                } else {
                    viewModelScope.launch {
                        addNote(state.textFieldNotesPayload)
                    }
                }
            }

            is AddEditNoteAction.TextFieldNotesPayload -> {
                mutableStateFlow.update {
                    it.copy(
                        textFieldNotesPayload = action.notesPayload,
                    )
                }
            }

            AddEditNoteAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                    )
                }
            }

            AddEditNoteAction.MisTouchBackDialog -> {
                if (state.notesPayloadInitialData != state.textFieldNotesPayload.note) {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = AddEditNoteState.DialogState.MisTouchBack,
                        )
                    }
                } else {
                    sendEvent(AddEditNoteEvent.NavigateBack)
                }
            }
        }
    }
}

data class AddEditNoteState(
    val resourceId: Int = -1,
    val resourceType: String? = null,
    val editEnabled: Boolean = false,
    val addUpdateButton: StringResource = Res.string.feature_note_button_add,
    val label: StringResource = Res.string.feature_note_write_note_label,
    val title: StringResource = Res.string.feature_note_add_note,
    val textFieldNotesPayload: NotesPayload = NotesPayload(null),
    val notesPayloadInitialData: String? = null,
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: StringResource) : DialogState
        data object Loading : DialogState
        data object MisTouchBack : DialogState
    }
}

sealed interface AddEditNoteEvent {
    data object NavigateBack : AddEditNoteEvent
    data object NavigateBackWithUpdateList : AddEditNoteEvent
}

sealed interface AddEditNoteAction {
    data object NavigateBack : AddEditNoteAction
    data object NavigateBackWithUpdateList : AddEditNoteAction
    data object OnRetry : AddEditNoteAction
    data class AddNote(val notesPayload: NotesPayload) : AddEditNoteAction
    data class EditNote(val notesPayload: NotesPayload) : AddEditNoteAction
    data object DismissDialog : AddEditNoteAction
    data object MisTouchBackDialog : AddEditNoteAction
    data class TextFieldNotesPayload(val notesPayload: NotesPayload) : AddEditNoteAction
}
