/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.mappers.client.note.toDomain
import com.mifos.core.data.mappers.client.note.toDto
import com.mifos.core.data.repository.NoteRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.runAsDataState
import com.mifos.core.data.util.withNetworkCheck
import com.mifos.core.model.objects.note.CreateNoteInput
import com.mifos.core.model.objects.note.Note
import com.mifos.core.model.objects.note.UpdateNoteInput
import com.mifos.core.network.datamanager.DataManagerNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import template.core.base.common.manager.DispatcherManager

class NoteRepositoryImp(
    private val dataManagerNote: DataManagerNote,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : NoteRepository {

    override suspend fun addNewNote(
        resourceType: String,
        resourceId: Long,
        createNoteInput: CreateNoteInput,
    ): DataState<Unit> {
        return runAsDataState(
            networkMonitor,
            dispatcher.io,
        ) {
            dataManagerNote.addNewNote(resourceType, resourceId, createNoteInput.toDto())
        }
    }

    override suspend fun deleteNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ): DataState<Unit> {
        return runAsDataState(
            networkMonitor,
            dispatcher.io,
        ) {
            dataManagerNote.deleteNote(resourceType, resourceId, noteId)
        }
    }

    override fun retrieveNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ): Flow<DataState<Note>> =
        networkMonitor.withNetworkCheck(
            dataManagerNote.retrieveNote(resourceType, resourceId, noteId).map { it.toDomain() }
                .asDataStateFlow(),
        ).flowOn(dispatcher.io)

    override fun retrieveListNotes(
        resourceType: String,
        resourceId: Long,
    ): Flow<DataState<List<Note>>> =
        networkMonitor.withNetworkCheck(
            dataManagerNote
                .retrieveListNotes(resourceType, resourceId)
                .map { dtoList ->
                    dtoList.map { dto ->
                        dto.toDomain()
                    }
                }
                .asDataStateFlow(),
        ).flowOn(dispatcher.io)

    override suspend fun updateNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
        updateNoteInput: UpdateNoteInput,
    ): DataState<Unit> {
        return runAsDataState(
            networkMonitor,
            dispatcher.io,
        ) {
            dataManagerNote.updateNote(
                resourceType,
                resourceId,
                noteId,
                updateNoteInput.toDto(),
            )
        }
    }
}
