/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.store

import com.mifos.core.network.datamanager.DataManagerNote
import com.mifos.core.network.dto.note.NoteDto
import com.mifos.room.dao.NoteDao
import com.mifos.room.entities.noncore.NoteEntity
import kotlinx.coroutines.flow.map
import kpt.core.base.store.infra.StoreFactory
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import kotlin.time.Duration

/**
 * Composite key scoping the note cache to a single parent — one `Note` table serves many
 * parents (a client, a loan, a savings account, ...), so a store read/write is keyed by BOTH
 * the parent's [entityType] (e.g. `"clients"`) and its [entityId].
 */
data class NoteKey(
    val entityType: String,
    val entityId: Long,
)

/**
 * Build the read-only offline-first [Store] for a parent's note list, keyed by [NoteKey] and
 * streaming the persisted [NoteEntity] rows scoped to that (entityType, entityId).
 *
 * ### Full-list replace per parent, not incremental append
 * Fineract's notes endpoint returns the FULL current list for a parent on every call (no
 * since-keyed delta), and a note deleted server-side disappears from that list. So the
 * source-of-truth writer is a wholesale [NoteDao.replaceForParent] (delete-this-parent + insert
 * inside one `@Transaction`) rather than an append — this keeps the cache from retaining notes
 * that have dropped off the server list, while only ever touching the one parent's rows.
 *
 * ### Fetcher shape
 * [DataManagerNote.retrieveListNotes] returns a cold `Flow<List<NoteDto>>`, so [Fetcher.ofFlow]
 * composes it straight through with a `map` that stamps each DTO with the key's parent scoping
 * via the file-private [NoteDto.toEntity] below — matching how `StoreFactory` constructs
 * Flow-backed fetchers. The mapper is inlined here (not imported from `core/data`) because
 * `core/data` depends on `core/store` (`api(projects.core.store)`); importing the `core/data`
 * `NoteEntityMapper` would be a circular dependency — the same reason the reference
 * `CheckerTaskStore` / `LoanTransactionStore` inline their fetcher-side mappers.
 *
 * @param api the network access seam ([DataManagerNote]) exposing the list call.
 * @param dao the Room DAO backing the local note source-of-truth.
 * @return a [Store] whose key is a [NoteKey] and whose value is the cached note list.
 */
fun provideNoteStore(
    api: DataManagerNote,
    dao: NoteDao,
): Store<NoteKey, List<NoteEntity>> {
    return StoreFactory.createStore(
        fetcher = Fetcher.ofFlow { key: NoteKey ->
            api.retrieveListNotes(key.entityType, key.entityId).map { dtos ->
                dtos.map { it.toEntity(key.entityType, key.entityId) }
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key: NoteKey -> dao.pageFlow(key.entityType, key.entityId) },
            writer = { key: NoteKey, rows: List<NoteEntity> ->
                dao.replaceForParent(key.entityType, key.entityId, rows)
            },
            delete = { key: NoteKey -> dao.deleteForParent(key.entityType, key.entityId) },
            deleteAll = { dao.deleteAll() },
        ),
        // Note rows — once written they never expire in-memory; refresh is caller-driven.
        memoryPolicy = MemoryPolicy.builder<NoteKey, List<NoteEntity>>()
            .setExpireAfterWrite(Duration.INFINITE)
            .build(),
    )
}

// ---------------------------------------------------------------------------
// Inline mapping helper — private to this file (fetcher side). Kept here rather than reused
// from core/data's NoteEntityMapper because core/data depends on core/store (circular).
// ---------------------------------------------------------------------------

private fun NoteDto.toEntity(entityType: String, entityId: Long): NoteEntity = NoteEntity(
    id = id,
    clientId = clientId,
    noteContent = note,
    createdById = createdById,
    createdByUsername = createdByUsername,
    createdOn = createdOn,
    updatedById = updatedById,
    updatedByUsername = updatedByUsername,
    updatedOn = updatedOn,
    entityType = entityType,
    entityId = entityId,
)
