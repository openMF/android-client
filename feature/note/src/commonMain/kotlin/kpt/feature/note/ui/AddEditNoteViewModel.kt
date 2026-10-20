/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.note.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kpt.core.base.store.mutation.BlockReason
import kpt.core.base.store.mutation.MutationResult
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.ui.viewmodel.BaseViewModel
import kpt.core.common.utils.Constants
import kpt.core.data.note.NoteCommandRepository
import kpt.core.data.note.NoteRepository
import kpt.core.network.mifos.note.dto.NoteRequestDto


/**
 * Add or edit one client note — ported from
 * `6b66e8a43:feature/note/src/commonMain/kotlin/com/mifos/feature/note/addEditNotes/AddEditNoteViewModel.kt`.
 *
 * ## What changed from the original, and why
 *
 * **1. The original put `StringResource` in its STATE.** `addUpdateButton`, `label` and `title` were
 * all `StringResource` fields that `init` reassigned when `noteId != null`. That makes the state
 * un-testable without a Compose resource runtime, and it puts a presentation concern in a layer
 * that should only know whether it is adding or editing. Here the state carries `editEnabled` and
 * the screen picks its own strings from it.
 *
 * **2. `loadSingleNote()` called `repository.retrieveNote(...)` — a dedicated single-note read.**
 * The migrated substrate has no such endpoint, and does not need one: the note being edited is
 * already in the list the user just came from, in the same Store. Reading it from the cached stream
 * avoids a second network call for data the device already has, and means opening the editor works
 * offline.
 *
 * **3. `OnRetry` in edit mode called `loadSingleNote()` and THEN `editNote(...)`.** Those race —
 * the load writes `textFieldNotesPayload`, so a retry could submit the SERVER's text rather than
 * what the user typed, silently discarding their edit. Retry here re-submits what is in the field.
 */
internal class AddEditNoteViewModel(
    private val repository: NoteRepository,
    private val commands: NoteCommandRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddEditNoteState, AddEditNoteEvent, AddEditNoteAction>(AddEditNoteState()) {

    private val clientId: Int = savedStateHandle.get<Int>(CLIENT_ID_KEY) ?: 0

    /** `null` means ADD. Read once; the original branched on it in four separate places. */
    private val noteId: Long? = savedStateHandle.get<Long>(NOTE_ID_KEY)

    init {
        mutableStateFlow.value = state.copy(editEnabled = noteId != null)
        if (noteId != null) loadExistingNote(noteId)
    }

    /**
     * Seed the field from the note already in the Store.
     *
     * `first { it !is Loading }` waits for the stream to settle rather than sampling it — opening
     * the editor directly (deep link, process death) must not show an empty field just because the
     * cache had not emitted yet.
     */
    private fun loadExistingNote(id: Long) {
        viewModelScope.launch {
            runCatching {
                repository.notesStream(clientId.toLong(), viewModelScope).state
                    .first { it !is ScreenState.Loading }
            }.onSuccess { screenState ->
                val existing = (screenState as? ScreenState.Content)
                    ?.data
                    ?.firstOrNull { it.id == id }
                    ?.noteContent
                    .orEmpty()
                mutableStateFlow.value = state.copy(text = existing, loadedText = existing)
            }.onFailure {
                Logger.e(tag = TAG, throwable = it) { "could not seed note $id" }
            }
        }
    }

    override fun handleAction(action: AddEditNoteAction) {
        when (action) {
            is AddEditNoteAction.TextChanged ->
                mutableStateFlow.value = state.copy(text = action.value, writeError = null)

            AddEditNoteAction.Submit, AddEditNoteAction.Retry -> submit()

            AddEditNoteAction.DismissDialog ->
                mutableStateFlow.value = state.copy(showDiscardConfirmation = false, writeError = null)

            AddEditNoteAction.ConfirmDiscard -> sendEvent(AddEditNoteEvent.NavigateBack)

            // The back affordance asks ONLY when there is something to lose. The original ran the
            // same comparison, and it is the reason this dialog is not merely annoying.
            AddEditNoteAction.BackClicked ->
                if (state.isDirty) {
                    mutableStateFlow.value = state.copy(showDiscardConfirmation = true)
                } else {
                    sendEvent(AddEditNoteEvent.NavigateBack)
                }
        }
    }

    /**
     * One submit path for both add and edit.
     *
     * A successful write invalidates the Store, so the list the user returns to updates itself —
     * the original's `popUpTo(inclusive) + launchSingleTop` route trick exists only because its
     * list could not be told the data had changed.
     */
    private fun submit() {
        val text = state.text.trim()
        // `submitted` matters as much as `isSubmitting`. Guarding only the IN-FLIGHT window leaves
        // the gap between a successful write and the screen actually leaving — a navigation
        // transition is a few hundred milliseconds, and a second tap inside it wrote the note
        // twice. Found by `aDoubleTapSendsOneWrite`, which completes the write instantly and so
        // lands in exactly that gap every run.
        if (text.isEmpty() || state.isSubmitting || state.submitted) return

        mutableStateFlow.value = state.copy(isSubmitting = true, writeError = null)
        viewModelScope.launch {
            val dto = NoteRequestDto(note = text)
            val result = if (noteId == null) {
                commands.addNewNote(Constants.ENTITY_TYPE_CLIENTS, clientId.toLong(), dto)
            } else {
                commands.updateNote(Constants.ENTITY_TYPE_CLIENTS, clientId.toLong(), noteId, dto)
            }

            when (result) {
                is MutationResult.Applied -> {
                    // `submitted` latches; only a SUCCESS sets it. A failed write must stay
                    // retryable, which is why this is not simply `!isSubmitting` inverted.
                    mutableStateFlow.value =
                        state.copy(isSubmitting = false, submitted = true, loadedText = text)
                    sendEvent(AddEditNoteEvent.NavigateBack)
                }

                // The form is LEFT INTACT on every failure. The user's text is the one thing that
                // cannot be recovered from anywhere else, so it is never cleared by an error.
                is MutationResult.Blocked ->
                    mutableStateFlow.value = state.copy(
                        isSubmitting = false,
                        writeError = if (result.reason == BlockReason.OFFLINE) {
                            NoteWriteError.Offline
                        } else {
                            NoteWriteError.Failed
                        },
                    )

                is MutationResult.Failed -> {
                    Logger.e(tag = TAG, throwable = result.cause) { "note write failed" }
                    mutableStateFlow.value =
                        state.copy(isSubmitting = false, writeError = NoteWriteError.Failed)
                }

                is MutationResult.Conflicted ->
                    mutableStateFlow.value =
                        state.copy(isSubmitting = false, writeError = NoteWriteError.Failed)
            }
        }
    }

    private companion object {
        const val CLIENT_ID_KEY = "clientId"
        const val NOTE_ID_KEY = "noteId"
        const val TAG = "AddEditNoteViewModel"
    }
}

internal data class AddEditNoteState(
    val text: String = "",
    /** What the server last gave us. `isDirty` is the difference, not a separate flag to maintain. */
    val loadedText: String = "",
    val editEnabled: Boolean = false,
    val isSubmitting: Boolean = false,
    /** Latched on a successful write. Closes the tap-again-while-navigating-away window. */
    val submitted: Boolean = false,
    val showDiscardConfirmation: Boolean = false,
    val writeError: NoteWriteError? = null,
) {
    val isDirty: Boolean get() = text.trim() != loadedText.trim()

    /** An empty note is not a note. The original let it through and the server rejected it. */
    val canSubmit: Boolean get() = text.isNotBlank() && !isSubmitting && !submitted
}

internal sealed interface AddEditNoteEvent {
    data object NavigateBack : AddEditNoteEvent
}

internal sealed interface AddEditNoteAction {
    data class TextChanged(val value: String) : AddEditNoteAction
    data object Submit : AddEditNoteAction
    data object Retry : AddEditNoteAction
    data object BackClicked : AddEditNoteAction
    data object ConfirmDiscard : AddEditNoteAction
    data object DismissDialog : AddEditNoteAction
}
