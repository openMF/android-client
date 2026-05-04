/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.dto.note.CreateNoteRequestDto
import com.mifos.core.network.dto.note.CreateNoteResponseDto
import com.mifos.core.network.dto.note.DeleteNoteResponseDto
import com.mifos.core.network.dto.note.NoteDto
import com.mifos.core.network.dto.note.UpdateNoteResponseDto
import kotlinx.coroutines.flow.Flow

class DataManagerNote(
    val mBaseApiManager: BaseApiManager,
) {
    suspend fun addNewNote(
        resourceType: String,
        resourceId: Long,
        createNoteRequestDto: CreateNoteRequestDto,
    ): CreateNoteResponseDto {
        return mBaseApiManager.noteService.addNewNote(
            resourceType,
            resourceId,
            createNoteRequestDto,
        )
    }

    suspend fun deleteNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ): DeleteNoteResponseDto {
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
    ): Flow<NoteDto> {
        return mBaseApiManager.noteService.retrieveNote(
            resourceType,
            resourceId,
            noteId,
        )
    }

    fun retrieveListNotes(
        resourceType: String,
        resourceId: Long,
    ): Flow<List<NoteDto>> {
        return mBaseApiManager.noteService.retrieveListNotes(
            resourceType,
            resourceId,
        )
    }

    suspend fun updateNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
        createNoteRequestDto: CreateNoteRequestDto,
    ): UpdateNoteResponseDto {
        return mBaseApiManager.noteService.updateNote(
            resourceType,
            resourceId,
            noteId,
            createNoteRequestDto,
        )
    }
}
