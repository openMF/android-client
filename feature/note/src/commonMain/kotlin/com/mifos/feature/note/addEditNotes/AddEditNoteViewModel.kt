/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.note.addEditNotes

import kpt.feature.note.generated.resources.Res
import kpt.feature.note.generated.resources.feature_note_add_note
import kpt.feature.note.generated.resources.feature_note_button_add
import kpt.feature.note.generated.resources.feature_note_button_update
import kpt.feature.note.generated.resources.feature_note_edit_note_label
import kpt.feature.note.generated.resources.feature_note_update_note
import kpt.feature.note.generated.resources.feature_note_write_note_label
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.repository.NoteRepository
import com.mifos.core.domain.useCases.AddNoteUseCase
import com.mifos.core.domain.useCases.UpdateNoteUseCase
import com.mifos.core.model.objects.note.CreateNoteInput
import com.mifos.core.model.objects.note.UpdateNoteInput
import kpt.core.base.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class AddEditNoteViewModel(
    private val repository: NoteRepository,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddEditNoteState, AddEditNoteEvent, AddEditNoteAction>(
    initialState = AddEditNoteState(),
) {
    private val route = savedStateHandle.toRoute<AddEditNoteRoute>()

    init {
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

    private suspend fun loadSingleNote() {
        route.resourceType?.let { type ->
            route.noteId?.let { id ->
                mutableStateFlow.update {
                    it.copy(dialogState = AddEditNoteState.DialogState.Loading)
                }
                repository.retrieveNote(type, route.resourceId.toLong(), id)
                    .catch { error ->
                        mutableStateFlow.update {
                            it.copy(dialogState = AddEditNoteState.DialogState.Error(error.message.orEmpty()))
                        }
                    }
                    .collect { note ->
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = null,
                                textFieldNotesPayload = note.note,
                                notesPayloadInitialData = note.note,
                            )
                        }
                    }
            }
        }
    }

    private suspend fun addNote(createNote: String?) {
        mutableStateFlow.update {
            it.copy(dialogState = AddEditNoteState.DialogState.Loading)
        }
        route.resourceType?.let { type ->
            try {
                addNoteUseCase(type, route.resourceId.toLong(), CreateNoteInput(note = createNote))
                sendAction(AddEditNoteAction.Internal.ReceiveAddNoteResult)
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(dialogState = AddEditNoteState.DialogState.Error(e.message.orEmpty()))
                }
            }
        }
    }

    private fun editNote(updateNote: String?) {
        mutableStateFlow.update {
            it.copy(dialogState = AddEditNoteState.DialogState.Loading)
        }
        viewModelScope.launch {
            route.resourceType?.let { type ->
                route.noteId?.let { id ->
                    try {
                        updateNoteUseCase(type, route.resourceId.toLong(), id, UpdateNoteInput(note = updateNote))
                        sendAction(AddEditNoteAction.Internal.ReceiveEditNoteResult)
                    } catch (e: Exception) {
                        mutableStateFlow.update {
                            it.copy(dialogState = AddEditNoteState.DialogState.Error(e.message.orEmpty()))
                        }
                    }
                }
            }
        }
    }

    private fun handleAddNoteResult() {
        mutableStateFlow.update {
            it.copy(
                dialogState = null,
                notesPayloadInitialData = state.textFieldNotesPayload,
            )
        }
        sendEvent(AddEditNoteEvent.NavigateBackWithUpdateList)
    }

    private fun handleEditNoteResult() {
        mutableStateFlow.update {
            it.copy(
                dialogState = null,
                notesPayloadInitialData = state.textFieldNotesPayload,
            )
        }
        sendEvent(AddEditNoteEvent.NavigateBackWithUpdateList)
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
                    addNote(action.createNote)
                }
            }

            is AddEditNoteAction.EditNote -> {
                viewModelScope.launch {
                    editNote(action.updateNote)
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
                        textFieldNotesPayload = action.note,
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

            AddEditNoteAction.PreventAccidentalBackDialog -> {
                if (state.notesPayloadInitialData != state.textFieldNotesPayload) {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = AddEditNoteState.DialogState.PreventAccidentalBack,
                        )
                    }
                } else {
                    sendEvent(AddEditNoteEvent.NavigateBack)
                }
            }

            AddEditNoteAction.Internal.ReceiveEditNoteResult -> {
                handleEditNoteResult()
            }

            AddEditNoteAction.Internal.ReceiveAddNoteResult -> {
                handleAddNoteResult()
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
    val textFieldNotesPayload: String? = null,
    val notesPayloadInitialData: String? = null,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data object PreventAccidentalBack : DialogState
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
    data class AddNote(val createNote: String?) : AddEditNoteAction
    data class EditNote(val updateNote: String?) : AddEditNoteAction
    data object DismissDialog : AddEditNoteAction
    data object PreventAccidentalBackDialog : AddEditNoteAction
    data class TextFieldNotesPayload(val note: String?) : AddEditNoteAction

    sealed interface Internal : AddEditNoteAction {
        data object ReceiveEditNoteResult : Internal

        data object ReceiveAddNoteResult : Internal
    }
}
