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

import com.mifos.core.data.store.ScreenState
import com.mifos.core.model.objects.note.Note

/**
 * MVI state for the Note list screen. The note-list read flow lives in
 * [screenState] (`ScreenState<List<Note>>`); refresh / delete-confirmation /
 * expanded-row UI lives in this top-level state.
 *
 * Note delete is a mutation — the VM exposes a separate `SubmitState<Unit>` for
 * it (see `NoteViewModel.deleteState`). This `NoteState` carries only the read
 * + per-row UI bits.
 */
data class NoteState(
    val resourceId: Int = -1,
    val resourceType: String? = null,
    val screenState: ScreenState<List<Note>> = ScreenState.Loading,
    val isRefreshing: Boolean = false,
    val expandedNoteId: Long? = null,
    val showDeleteDialog: Boolean = false,
)

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
    data object ShowDeleteDialog : NoteAction
    data object DismissDeleteDialog : NoteAction
    data class OnToggleExpanded(val id: Long?) : NoteAction
    data object DeleteNote : NoteAction

    sealed interface Internal : NoteAction {
        data object NotesLoaded : Internal
    }
}
