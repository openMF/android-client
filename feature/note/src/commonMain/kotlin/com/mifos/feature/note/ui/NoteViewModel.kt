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
import com.mifos.core.ui.store.BaseViewModel
import com.mifos.feature.note.navigation.NoteRoute
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Note list ViewModel.
 *
 * Read flow (`listNotes`) uses direct suspend + try/catch → `ScreenState<List<Note>>`
 * (no Store5 — see [NoteRepository] doc / RULE-STORE5-FETCH-001 exception for
 * resource-scoped CRUD lists). Re-throws `CancellationException` per
 * structured-concurrency contract (RULE-NO-RUN-CATCHING-001 doesn't permit
 * `runCatching` because it swallows cancellation).
 *
 * Delete uses [submitHandler] / [SubmitState] (Submitting → Submitted → Failed)
 * — same pattern as `feature/auth/LoginViewModel` and `feature/activate`.
 */
class NoteViewModel(
    private val repository: NoteRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<NoteState, NoteEvent, NoteAction>(
    initialState = NoteState(),
) {
    private val route = savedStateHandle.toRoute<NoteRoute>()

    private val deleteSubmit = viewModelScope.submitHandler<Unit>()

    val deleteState: StateFlow<SubmitState<Unit>> = deleteSubmit.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SubmitState.Idle,
    )

    init {
        mutableStateFlow.update {
            it.copy(
                resourceId = route.resourceId,
                resourceType = route.resourceType,
            )
        }
        loadNotes(initial = true)

        // After a successful delete, reload the list and reset the submit handler.
        deleteSubmit.state
            .onEach { submit ->
                if (submit is SubmitState.Submitted) {
                    loadNotes(initial = false)
                    deleteSubmit.reset()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadNotes(initial: Boolean) {
        val type = state.resourceType ?: return
        viewModelScope.launch {
            if (initial) {
                mutableStateFlow.update { it.copy(screenState = ScreenState.Loading) }
            }
            try {
                val notes = repository.listNotes(type, route.resourceId.toLong())
                val screen = if (notes.isEmpty()) {
                    ScreenState.Empty
                } else {
                    ScreenState.Content(data = notes, freshness = DataFreshness.FRESH)
                }
                mutableStateFlow.update {
                    it.copy(
                        screenState = screen,
                        isRefreshing = false,
                        expandedNoteId = null,
                    )
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (t: Throwable) {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Error(t),
                        isRefreshing = false,
                    )
                }
            }
        }
    }

    fun onDeleteConsumed() {
        deleteSubmit.reset()
    }

    override fun handleAction(action: NoteAction) {
        when (action) {
            NoteAction.NavigateBack -> {
                mutableStateFlow.update { it.copy(expandedNoteId = null) }
                sendEvent(NoteEvent.NavigateBack)
            }

            NoteAction.OnRetry -> loadNotes(initial = true)

            NoteAction.OnRefresh -> {
                mutableStateFlow.update { it.copy(isRefreshing = true) }
                loadNotes(initial = false)
            }

            NoteAction.OnClickEditScreen -> sendEvent(NoteEvent.NavigateEditNote)
            NoteAction.OnClickAddScreen -> sendEvent(NoteEvent.NavigateAddNote)

            is NoteAction.OnToggleExpanded -> mutableStateFlow.update { s ->
                s.copy(expandedNoteId = if (s.expandedNoteId == action.id) null else action.id)
            }

            NoteAction.ShowDeleteDialog -> mutableStateFlow.update {
                it.copy(showDeleteDialog = true)
            }

            NoteAction.DismissDeleteDialog -> mutableStateFlow.update {
                it.copy(showDeleteDialog = false)
            }

            NoteAction.DeleteNote -> {
                val type = state.resourceType ?: return
                val id = state.expandedNoteId ?: return
                mutableStateFlow.update { it.copy(showDeleteDialog = false) }
                deleteSubmit.submit {
                    repository.deleteNote(type, route.resourceId.toLong(), id)
                }
            }

            is NoteAction.Internal.NotesLoaded -> Unit
        }
    }
}
