/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.note

import com.mifos.core.model.objects.note.CreateNoteInput
import com.mifos.core.model.objects.note.Note
import com.mifos.core.model.objects.note.UpdateNoteInput

/**
 * Per-feature note repository (Phase C Wave 7 of store5-adoption).
 *
 * Modern suspend-only contract. All methods throw on HTTP failure or transport
 * error — callers wrap mutations in `SubmitHandler.submit { ... }` and wrap
 * reads in explicit try/catch (re-throwing `CancellationException`) to drive
 * `ScreenState<T>`. No legacy `DataState<*>` / `Flow<DataState<*>>`.
 *
 * Notes are not Store5-cached: they are resource-scoped lists (per client / group /
 * loan / savings account) that are CRUD-mutated frequently and have no useful
 * offline value once the user is editing. See RULE-STORE5-FETCH-001 — direct
 * suspend reads + try/catch → `ScreenState` is the documented exception.
 */
interface NoteRepository {

    /**
     * Retrieve the list of notes for [resourceType] / [resourceId] in descending
     * `createOn` order. Throws on HTTP failure or transport error.
     */
    suspend fun listNotes(
        resourceType: String,
        resourceId: Long,
    ): List<Note>

    /**
     * Retrieve a single note. Throws on HTTP failure or transport error.
     */
    suspend fun getNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ): Note

    /**
     * Add a new note. Throws on HTTP failure or transport error.
     */
    suspend fun addNote(
        resourceType: String,
        resourceId: Long,
        createNoteInput: CreateNoteInput,
    )

    /**
     * Update an existing note. Throws on HTTP failure or transport error.
     */
    suspend fun updateNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
        updateNoteInput: UpdateNoteInput,
    )

    /**
     * Delete a note. Throws on HTTP failure or transport error.
     */
    suspend fun deleteNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    )
}
