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

import com.mifos.core.data.mappers.client.note.toDomain
import com.mifos.core.data.mappers.client.note.toDto
import com.mifos.core.data.repository.NoteRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.runSuspendCall
import com.mifos.core.data.util.withNetworkCheck
import com.mifos.core.model.objects.note.CreateNoteInput
import com.mifos.core.model.objects.note.Note
import com.mifos.core.model.objects.note.UpdateNoteInput
import com.mifos.core.network.datamanager.DataManagerNote
import com.mifos.core.store.NoteKey
import com.mifos.room.entities.noncore.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kpt.core.base.common.manager.DispatcherManager
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

/**
 * Offline-first read path for a parent's note list (Store5, mirrors
 * [CheckerInboxRepositoryImp]). [retrieveListNotes] is served through the Store5 note store
 * (qualifier [AppStoreRegistry.Notes][kpt.core.store.AppStoreRegistry.Notes]) keyed by
 * [NoteKey] instead of a raw `DataManagerNote.retrieveListNotes()` network call:
 * `StoreReadRequest.cached(refresh = true)` emits the Room-persisted rows immediately (so the
 * notes list renders offline from cache) AND triggers a background network refresh when
 * connectivity is available (SWR). The write/single-read methods (addNewNote / deleteNote /
 * retrieveNote / updateNote) stay on the raw [DataManagerNote] path — the store is a
 * read-only list cache; callers re-collect [retrieveListNotes] (refresh = true) after a write.
 */
class NoteRepositoryImp(
    private val dataManagerNote: DataManagerNote,
    private val noteStore: Store<NoteKey, List<NoteEntity>>,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : NoteRepository {

    override suspend fun addNewNote(
        resourceType: String,
        resourceId: Long,
        createNoteInput: CreateNoteInput,
    ): Unit {
        return runSuspendCall(
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
    ): Unit {
        return runSuspendCall(
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
    ): Flow<Note> =
        networkMonitor.withNetworkCheck(
            dataManagerNote.retrieveNote(resourceType, resourceId, noteId).map { it.toDomain() }
                ,
        ).flowOn(dispatcher.io)

    override fun retrieveListNotes(
        resourceType: String,
        resourceId: Long,
    ): Flow<List<Note>> =
        noteStore
            .stream(
                StoreReadRequest.cached(
                    key = NoteKey(resourceType, resourceId),
                    refresh = true,
                ),
            )
            .mapNotNull { response ->
                when (response) {
                    is StoreReadResponse.Data -> response.value.map { it.toDomain() }
                    // Offline-first: fetch error is non-fatal — the SoT reader emits the cached
                    // (possibly empty) list as a separate Data response.
                    else -> null
                }
            }
            .flowOn(dispatcher.io)

    override suspend fun updateNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
        updateNoteInput: UpdateNoteInput,
    ): Unit {
        return runSuspendCall(
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
