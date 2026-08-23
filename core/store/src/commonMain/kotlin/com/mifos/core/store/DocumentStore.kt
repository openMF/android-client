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

import com.mifos.core.model.objects.noncoreobjects.Document
import com.mifos.core.network.datamanager.DataManagerDocument
import com.mifos.room.dao.DocumentDao
import com.mifos.room.entities.document.DocumentEntity
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kpt.core.base.store.infra.StoreFactory
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import kotlin.time.Duration

/**
 * Composite Store5 key for a document list — a parent `(entityType, entityId)` pair.
 *
 * Store5 keys need a stable `equals`/`hashCode`; a Kotlin `data class` provides both, so the
 * same `(entityType, entityId)` always resolves to the same cached page. `@Serializable` is
 * kept for parity with the fork's other flat cache keys/rows.
 */
@Serializable
data class DocumentKey(val entityType: String, val entityId: Int)

/**
 * Build the read-only offline-first [Store] for a parent's document-metadata list, keyed by
 * the composite [DocumentKey] and streaming the persisted [DocumentEntity] rows.
 *
 * ### Full-list replace per parent, not incremental append
 * Fineract's documents endpoint (`GET .../{entityType}/{entityId}/documents`) has no
 * since-keyed delta — it returns the FULL current metadata list for a parent on every call,
 * and a document dropped on the server disappears from the list. So the source-of-truth
 * writer is a wholesale [DocumentDao.replaceForParent] (delete-for-parent + insert inside one
 * `@Transaction`) rather than the append-only path the loan ledger uses — this keeps the
 * cache from retaining documents that dropped off the server list, while leaving OTHER
 * parents' cached lists untouched.
 *
 * ### Fetcher shape
 * [DataManagerDocument.getDocumentsList] returns a cold `Flow<List<Document>>`, so
 * [Fetcher.ofFlow] composes it straight through with a `map` that projects each domain
 * [Document] to a flat [DocumentEntity] row (the parent scoping columns come from the KEY) —
 * matching how `StoreFactory` constructs Flow-backed fetchers.
 *
 * @param api the network access seam ([DataManagerDocument]) exposing the list call.
 * @param dao the Room DAO backing the local document-metadata source-of-truth.
 * @return a [Store] whose key is [DocumentKey] and whose value is the cached document list.
 */
fun provideDocumentStore(
    api: DataManagerDocument,
    dao: DocumentDao,
): Store<DocumentKey, List<DocumentEntity>> {
    return StoreFactory.createStore(
        fetcher = Fetcher.ofFlow { key: DocumentKey ->
            api.getDocumentsList(key.entityType, key.entityId).map { list ->
                list.map { it.toEntity(key) }
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key: DocumentKey -> dao.pageFlow(key.entityType, key.entityId) },
            writer = { key: DocumentKey, rows: List<DocumentEntity> ->
                dao.replaceForParent(key.entityType, key.entityId, rows)
            },
            delete = { key: DocumentKey -> dao.deleteForParent(key.entityType, key.entityId) },
            deleteAll = { dao.deleteAll() },
        ),
        // Document metadata rows — immutable once written; never expire in-memory.
        memoryPolicy = MemoryPolicy.builder<DocumentKey, List<DocumentEntity>>()
            .setExpireAfterWrite(Duration.INFINITE)
            .build(),
    )
}

// ---------------------------------------------------------------------------
// Inline mapping helper — private to this file (fetcher side)
// ---------------------------------------------------------------------------

private fun Document.toEntity(key: DocumentKey): DocumentEntity = DocumentEntity(
    id = id,
    // Parent scoping columns come from the store KEY (non-null), not the domain object's own
    // nullable parentEntityType — mirrors LoanTransactionEntity taking loanId from the key.
    parentEntityType = key.entityType,
    parentEntityId = key.entityId,
    name = name,
    fileName = fileName,
    size = size,
    type = type,
    description = description,
)
