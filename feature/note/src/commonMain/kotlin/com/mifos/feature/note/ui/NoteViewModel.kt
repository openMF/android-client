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
import com.mifos.core.data.note.store.NoteListKey
import template.core.base.store.screen.ScreenState
import template.core.base.store.submit.SubmitState
import com.mifos.core.data.store.submitHandler
import com.mifos.core.model.objects.note.Note
import com.mifos.core.ui.store.BaseViewModel
import com.mifos.feature.note.navigation.NoteRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Note list ViewModel.
 *
 * Reads flow through Store5 (`repository.notesStream(...) → ScreenDataStream<List<Note>>`)
 * per RULE-STORE5-FETCH-001 — cache-then-network, auto-refresh on reconnect,
 * `lastContent` preservation, captive-portal detection out-of-box.
 *
 * Delete uses `submitHandler<Unit>` (SubmitState lifecycle). After a successful
 * delete the repository invalidates the Store5 cache via `store.fresh(key)`,
 * which propagates back through `screenState` automatically — the VM does NOT
 * need to manually trigger a reload.
 */
class NoteViewModel(
    private val repository: NoteRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<NoteState, NoteEvent, NoteAction>(
    initialState = NoteState(),
) {
    private val route = savedStateHandle.toRoute<NoteRoute>()

    /** Store5 key — drives cache-then-network reads for this resource. */
    private val keyFlow = MutableStateFlow(
        NoteListKey(
            resourceType = route.resourceType.orEmpty(),
            resourceId = route.resourceId.toLong(),
        ),
    )

    /** Cold ScreenDataStream from Store5. */
    private val notesStream = repository.notesStream(keyFlow, viewModelScope)

    /**
     * Screen-state for the Compose layer. Maps a Store5 `Content` with an empty
     * list to `Empty` so `ScreenContent` renders the "no notes" slot.
     */
    val screenState: StateFlow<ScreenState<List<Note>>> = notesStream.state
        .map { state ->
            if (state is ScreenState.Content<List<Note>> && state.data.isEmpty()) {
                ScreenState.Empty
            } else {
                state
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ScreenState.Loading,
        )

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

            NoteAction.OnRetry -> notesStream.retry()
            NoteAction.OnRefresh -> notesStream.refresh()

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
                mutableStateFlow.update {
                    it.copy(showDeleteDialog = false, expandedNoteId = null)
                }
                deleteSubmit.submit {
                    repository.deleteNote(type, route.resourceId.toLong(), id)
                    // Repository.deleteNote calls store.fresh(key) internally — the
                    // screenState StateFlow will receive the updated list automatically.
                }
            }
        }
    }
}
