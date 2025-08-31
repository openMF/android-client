/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.NoteRepository
import com.mifos.core.model.objects.notes.Note
import com.mifos.core.model.objects.payloads.NotesPayload
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerNote
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImp(
    private val dataManagerNote: DataManagerNote,
) : NoteRepository {

    override suspend fun addNewNote(
        resourceType: String,
        resourceId: Long,
        notesPayload: NotesPayload,
    ): GenericResponse {
        return dataManagerNote.addNewNote(resourceType, resourceId, notesPayload)
    }

    override suspend fun deleteNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ): GenericResponse {
        return dataManagerNote.deleteNote(resourceType, resourceId, noteId)
    }

    override fun retrieveNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ): Flow<DataState<Note>> {
        return dataManagerNote.retrieveNote(resourceType, resourceId, noteId).asDataStateFlow()
    }

    override fun retrieveListNotes(
        resourceType: String,
        resourceId: Long,
    ): Flow<DataState<List<Note>>> {
        return dataManagerNote.retrieveListNotes(resourceType, resourceId).asDataStateFlow()
    }

    override suspend fun updateNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
        notesPayload: NotesPayload,
    ): GenericResponse {
        return dataManagerNote.updateNote(resourceType, resourceId, noteId, notesPayload)
    }
}
