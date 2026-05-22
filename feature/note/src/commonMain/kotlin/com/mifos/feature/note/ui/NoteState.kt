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

/**
 * MVI state for the Note list screen.
 *
 * The note-list read flow no longer lives on this state — it comes from
 * `NoteViewModel.screenState` (`StateFlow<ScreenState<List<Note>>>`) driven by
 * the Store5 `ScreenDataStream` per RULE-STORE5-FETCH-001.
 *
 * Delete is a mutation — `NoteViewModel.deleteState` exposes `SubmitState<Unit>`.
 *
 * This state carries only the per-screen UI bits (resource identity,
 * delete-confirmation dialog visibility, expanded row id).
 */
data class NoteState(
    val resourceId: Int = -1,
    val resourceType: String? = null,
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
}
