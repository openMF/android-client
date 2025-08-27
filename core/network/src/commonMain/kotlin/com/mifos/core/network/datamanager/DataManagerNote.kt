/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.model.objects.notes.Note
import com.mifos.core.model.objects.payloads.NotesPayload
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import kotlinx.coroutines.flow.Flow

class DataManagerNote(
    val mBaseApiManager: BaseApiManager,
) {
    suspend fun addNewNote(
        resourceType: String,
        resourceId: Long,
        notesPayload: NotesPayload,
    ): GenericResponse {
        return mBaseApiManager.noteService.addNewNote(
            resourceType,
            resourceId,
            notesPayload,
        )
    }

    suspend fun deleteNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ): GenericResponse {
        return mBaseApiManager.noteService.deleteNote(
            resourceType,
            resourceId,
            noteId,
        )
    }

    fun retrieveNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ): Flow<Note> {
        return mBaseApiManager.noteService.retrieveNote(
            resourceType,
            resourceId,
            noteId,
        )
    }

    fun retrieveListNotes(
        resourceType: String,
        resourceId: Long,
    ): Flow<List<Note>> {
        return mBaseApiManager.noteService.retrieveListNotes(
            resourceType,
            resourceId,
        )
    }

    suspend fun updateNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
        notesPayload: NotesPayload,
    ): GenericResponse {
        return mBaseApiManager.noteService.updateNote(
            resourceType,
            resourceId,
            noteId,
            notesPayload,
        )
    }
}
