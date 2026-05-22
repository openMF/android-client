/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.note.impl

import com.mifos.core.data.mappers.client.note.toDomain
import com.mifos.core.data.mappers.client.note.toDto
import com.mifos.core.data.note.NoteRepository
import com.mifos.core.model.objects.note.CreateNoteInput
import com.mifos.core.model.objects.note.Note
import com.mifos.core.model.objects.note.UpdateNoteInput
import com.mifos.core.network.note.api.NoteApi

/**
 * Default [NoteRepository] backed by [NoteApi] (per-resource Ktorfit interface,
 * Phase C Wave 7 of store5-adoption).
 *
 * Errors propagate as exceptions — caller is responsible for wrapping in
 * `SubmitHandler.submit { ... }` (mutations) or try/catch → `ScreenState.Error`
 * (reads). No `DataState<*>`, no `withNetworkCheck`, no `runCatching`.
 */
class NoteRepositoryImpl(
    private val noteApi: NoteApi,
) : NoteRepository {

    override suspend fun listNotes(
        resourceType: String,
        resourceId: Long,
    ): List<Note> = noteApi.retrieveListNotes(resourceType, resourceId).map { it.toDomain() }

    override suspend fun getNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ): Note = noteApi.retrieveNote(resourceType, resourceId, noteId).toDomain()

    override suspend fun addNote(
        resourceType: String,
        resourceId: Long,
        createNoteInput: CreateNoteInput,
    ) {
        noteApi.addNote(resourceType, resourceId, createNoteInput.toDto())
    }

    override suspend fun updateNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
        updateNoteInput: UpdateNoteInput,
    ) {
        noteApi.updateNote(resourceType, resourceId, noteId, updateNoteInput.toDto())
    }

    override suspend fun deleteNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ) {
        noteApi.deleteNote(resourceType, resourceId, noteId)
    }
}
