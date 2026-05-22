/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.note.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.note.NoteRepository
import com.mifos.core.data.store.DataFreshness
import com.mifos.core.data.store.ScreenState
import com.mifos.core.data.store.SubmitState
import com.mifos.core.data.store.submitHandler
import com.mifos.core.model.objects.note.CreateNoteInput
import com.mifos.core.model.objects.note.UpdateNoteInput
import com.mifos.core.ui.store.BaseViewModel
import com.mifos.feature.note.navigation.AddEditNoteRoute
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Add/Edit Note ViewModel.
 *
 * Edit mode: initial-data fetch via direct suspend + try/catch → `ScreenState<String>`
 * (re-throws `CancellationException`, no `runCatching` per RULE-NO-RUN-CATCHING-001).
 * Add mode: skips the fetch and starts at `Content("")`.
 *
 * Submit (POST add / PUT update) uses [submitHandler] / [SubmitState] — same
 * pattern as `feature/auth/LoginViewModel` + `feature/activate`.
 */
class AddEditNoteViewModel(
    private val repository: NoteRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddEditNoteState, AddEditNoteEvent, AddEditNoteAction>(
    initialState = AddEditNoteState(),
) {
    private val route = savedStateHandle.toRoute<AddEditNoteRoute>()

    private val submit = viewModelScope.submitHandler<Unit>()

    val submitState: StateFlow<SubmitState<Unit>> = submit.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SubmitState.Idle,
    )

    init {
        val isEdit = route.noteId != null
        val seed = if (isEdit) AddEditNoteState.editMode() else AddEditNoteState.addMode()
        mutableStateFlow.update {
            seed.copy(
                resourceId = route.resourceId,
                resourceType = route.resourceType,
                noteId = route.noteId,
            )
        }
        if (isEdit) loadExistingNote()
    }

    private fun loadExistingNote() {
        val type = state.resourceType ?: return
        val id = state.noteId ?: return
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(loadState = ScreenState.Loading) }
            try {
                val note = repository.getNote(type, route.resourceId.toLong(), id)
                val text = note.note.orEmpty()
                mutableStateFlow.update {
                    it.copy(
                        loadState = ScreenState.Content(data = text, freshness = DataFreshness.FRESH),
                        textFieldNotesPayload = text,
                        notesPayloadInitialData = text,
                    )
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (t: Throwable) {
                mutableStateFlow.update { it.copy(loadState = ScreenState.Error(t)) }
            }
        }
    }

    fun onSubmitConsumed() {
        submit.reset()
    }

    override fun handleAction(action: AddEditNoteAction) {
        when (action) {
            AddEditNoteAction.NavigateBack -> sendEvent(AddEditNoteEvent.NavigateBack)

            AddEditNoteAction.OnRetryLoad -> loadExistingNote()

            is AddEditNoteAction.TextFieldNotesPayload -> mutableStateFlow.update {
                it.copy(textFieldNotesPayload = action.note)
            }

            AddEditNoteAction.RequestBack -> {
                if (state.isDirty) {
                    mutableStateFlow.update { it.copy(showAccidentalBackDialog = true) }
                } else {
                    sendEvent(AddEditNoteEvent.NavigateBack)
                }
            }

            AddEditNoteAction.DismissAccidentalBackDialog -> mutableStateFlow.update {
                it.copy(showAccidentalBackDialog = false)
            }

            AddEditNoteAction.Submit -> doSubmit()
        }
    }

    private fun doSubmit() {
        val type = state.resourceType ?: return
        val resourceId = route.resourceId.toLong()
        val payload = state.textFieldNotesPayload
        val noteId = state.noteId
        submit.submit {
            if (state.editEnabled && noteId != null) {
                repository.updateNote(type, resourceId, noteId, UpdateNoteInput(note = payload))
            } else {
                repository.addNote(type, resourceId, CreateNoteInput(note = payload))
            }
        }
    }
}
