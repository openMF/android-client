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

import androidclient.feature.note.generated.resources.Res
import androidclient.feature.note.generated.resources.feature_note_add_note
import androidclient.feature.note.generated.resources.feature_note_button_add
import androidclient.feature.note.generated.resources.feature_note_edit_note_label
import androidclient.feature.note.generated.resources.feature_note_update_note
import androidclient.feature.note.generated.resources.feature_note_write_note_label
import com.mifos.core.data.store.DataFreshness
import com.mifos.core.data.store.ScreenState
import org.jetbrains.compose.resources.StringResource

/**
 * MVI state for the Add/Edit Note screen.
 *
 * `loadState` holds the initial-data fetch result when editing
 * (`ScreenState<String?>` where the value is the existing note text). In add
 * mode it is set to `Content(null)` synchronously.
 *
 * The submission lifecycle (Submitting / Submitted / Failed) is exposed as a
 * separate `SubmitState<Unit>` on the ViewModel, not on this state, per the
 * canonical pattern (see `feature/auth/LoginState` + `feature/activate/ActivateState`).
 */
data class AddEditNoteState(
    val resourceId: Int = -1,
    val resourceType: String? = null,
    val noteId: Long? = null,
    val editEnabled: Boolean = false,
    val addUpdateButton: StringResource = Res.string.feature_note_button_add,
    val label: StringResource = Res.string.feature_note_write_note_label,
    val title: StringResource = Res.string.feature_note_add_note,
    val textFieldNotesPayload: String = "",
    val notesPayloadInitialData: String = "",
    val loadState: ScreenState<String> = ScreenState.Loading,
    val showAccidentalBackDialog: Boolean = false,
) {
    val isDirty: Boolean
        get() = textFieldNotesPayload != notesPayloadInitialData

    companion object {
        fun addMode(): AddEditNoteState = AddEditNoteState(
            editEnabled = false,
            addUpdateButton = Res.string.feature_note_button_add,
            label = Res.string.feature_note_write_note_label,
            title = Res.string.feature_note_add_note,
            loadState = ScreenState.Content(data = "", freshness = DataFreshness.FRESH),
        )

        fun editMode(): AddEditNoteState = AddEditNoteState(
            editEnabled = true,
            addUpdateButton = Res.string.feature_note_update_note,
            label = Res.string.feature_note_edit_note_label,
            title = Res.string.feature_note_update_note,
            // loadState stays Loading until the existing note is fetched.
        )
    }
}

sealed interface AddEditNoteEvent {
    data object NavigateBack : AddEditNoteEvent
    data object NavigateBackWithUpdatedList : AddEditNoteEvent
}

sealed interface AddEditNoteAction {
    data object NavigateBack : AddEditNoteAction
    data object OnRetryLoad : AddEditNoteAction
    data object Submit : AddEditNoteAction
    data class TextFieldNotesPayload(val note: String) : AddEditNoteAction
    data object RequestBack : AddEditNoteAction
    data object DismissAccidentalBackDialog : AddEditNoteAction
}
