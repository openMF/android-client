/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.notes.Note
import com.mifos.core.model.objects.payloads.NotesPayload
import com.mifos.core.network.GenericResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by Arin Yadav on 24/08/2025.
 */
interface NoteRepository {

    suspend fun addNewNote(
        resourceType: String,
        resourceId: Long,
        notesPayload: NotesPayload,
    ): GenericResponse

    suspend fun deleteNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ): GenericResponse

    fun retrieveNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ): Flow<DataState<Note>>

    fun retrieveListNotes(
        resourceType: String,
        resourceId: Long,
    ): Flow<DataState<List<Note>>>

    suspend fun updateNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
        notesPayload: NotesPayload,
    ): GenericResponse
}
