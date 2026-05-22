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
import com.mifos.core.data.note.store.NoteListKey
import com.mifos.core.data.note.store.cacheKey
import com.mifos.core.model.objects.note.CreateNoteInput
import com.mifos.core.model.objects.note.Note
import com.mifos.core.model.objects.note.UpdateNoteInput
import com.mifos.core.network.note.api.NoteApi
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import template.core.base.store.infra.FetchedAtRepository
import template.core.base.store.screen.ScreenDataStream
import template.core.base.store.screen.asScreenStream

/**
 * Default [NoteRepository] backed by:
 *  - [noteApi] for direct writes (mutations) + the single-note read.
 *  - [noteListStore] for the offline-first list read (Store5; see [provideNoteListStore]).
 *
 * Mutations propagate exceptions to the caller (wrapped via `SubmitHandler`).
 * Reads via [notesStream] route errors through `ScreenState.Error` / `ScreenState.NoNetwork`
 * inside the Store5 DecisionEngine.
 */
class NoteRepositoryImpl(
    private val noteApi: NoteApi,
    private val noteListStore: Store<NoteListKey, List<Note>>,
    private val networkMonitor: NetworkMonitor,
    private val fetchedAtRepository: FetchedAtRepository,
) : NoteRepository {

    override fun notesStream(
        keyFlow: Flow<NoteListKey>,
        scope: CoroutineScope,
    ): ScreenDataStream<List<Note>> = noteListStore.asScreenStream(
        keyFlow = keyFlow,
        networkMonitor = networkMonitor,
        fetchedAtRepository = fetchedAtRepository,
        cacheKeyFor = { it.cacheKey() },
        scope = scope,
    )

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
        invalidateList(resourceType, resourceId)
    }

    override suspend fun updateNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
        updateNoteInput: UpdateNoteInput,
    ) {
        noteApi.updateNote(resourceType, resourceId, noteId, updateNoteInput.toDto())
        invalidateList(resourceType, resourceId)
    }

    override suspend fun deleteNote(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ) {
        noteApi.deleteNote(resourceType, resourceId, noteId)
        invalidateList(resourceType, resourceId)
    }

    /**
     * Force the Store5 cache to refetch from network after a successful write.
     * Stream subscribers receive the new list automatically via SourceOfTruth.
     */
    private suspend fun invalidateList(resourceType: String, resourceId: Long) {
        val key = NoteListKey(resourceType, resourceId)
        // Consume the first terminal response so the store actually executes the fetch.
        noteListStore.stream(StoreReadRequest.fresh(key))
            .first { response ->
                response is StoreReadResponse.Data<*> || response is StoreReadResponse.Error
            }
    }
}
