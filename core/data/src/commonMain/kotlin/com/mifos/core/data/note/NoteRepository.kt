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

import com.mifos.core.data.note.store.NoteListKey
import com.mifos.core.model.objects.note.CreateNoteInput
import com.mifos.core.model.objects.note.Note
import com.mifos.core.model.objects.note.UpdateNoteInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import template.core.base.store.screen.ScreenDataStream

/**
 * Per-feature note repository (Phase C Wave 7 of store5-adoption).
 *
 * Offline-first per RULE-STORE5-FETCH-001:
 * - **Reads** ([notesStream], [getNote]) flow through Store5 — Room is the SourceOfTruth,
 *   Ktorfit is the Fetcher, `DecisionEngine` resolves cache-then-network + auto-refresh
 *   on reconnect.
 * - **Mutations** ([addNote], [updateNote], [deleteNote]) are suspend writes against the
 *   server; the impl calls `store.fresh(key)` after success so the cache + subscribers
 *   see the new state.
 */
interface NoteRepository {

    /**
     * Reactive stream of notes for [keyFlow]. Re-streams on key change. Wraps
     * the underlying `Store<NoteListKey, List<Note>>` via `asScreenStream` so
     * the screen gets `Loading / Empty / Error / NoNetwork / Content` slots
     * out-of-box plus auto-refresh on reconnect.
     */
    fun notesStream(
        keyFlow: Flow<NoteListKey>,
        scope: CoroutineScope,
    ): ScreenDataStream<List<Note>>

    /**
     * Retrieve a single note (deep-link reload). Throws on failure.
     */
    suspend fun getNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ): Note

    /**
     * Add a new note. After success, invalidates the matching list cache so
     * the new note appears in [notesStream]. Throws on failure.
     */
    suspend fun addNote(
        resourceType: String,
        resourceId: Long,
        createNoteInput: CreateNoteInput,
    )

    /**
     * Update an existing note. After success, invalidates the matching list cache.
     */
    suspend fun updateNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
        updateNoteInput: UpdateNoteInput,
    )

    /**
     * Delete a note. After success, invalidates the matching list cache.
     */
    suspend fun deleteNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    )
}
