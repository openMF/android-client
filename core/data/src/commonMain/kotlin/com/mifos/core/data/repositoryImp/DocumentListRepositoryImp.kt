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

import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.core.model.objects.noncoreobjects.Document
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerDocument
import com.mifos.core.store.DocumentKey
import com.mifos.room.entities.document.DocumentEntity
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

/**
 * Offline-first read path for a parent's document list (Store5, mirrors
 * [CheckerInboxRepositoryImp]). The metadata list is served through the Store5 document store
 * (qualifier [AppStoreRegistry.Documents][kpt.core.store.AppStoreRegistry.Documents]) keyed by
 * the composite [DocumentKey] instead of a raw `DataManagerDocument.getDocumentsList()`
 * network call. `StoreReadRequest.cached(refresh = true)` emits the Room-persisted rows
 * immediately (so the list renders offline from cache) AND triggers a background network
 * refresh when connectivity is available (SWR). The fetcher's network error is intentionally
 * swallowed — Store5's source-of-truth reader still emits the cached (possibly empty) list, so
 * the screen shows cached rows offline rather than a network error.
 *
 * The [downloadDocument] / [removeDocument] methods stay on the raw [DataManagerDocument] path
 * — the store is a read-only cache. A remove mutates server state, so the caller re-invokes the
 * document list to force a `refresh = true` re-fetch (the ViewModel already reloads after each
 * write).
 */
class DocumentListRepositoryImp(
    private val documentStore: Store<DocumentKey, List<DocumentEntity>>,
    private val dataManagerDocument: DataManagerDocument,
) : DocumentListRepository {

    override fun getDocumentsList(entityType: String, entityId: Int): Flow<List<Document>> {
        return documentStore
            .stream(StoreReadRequest.cached(key = DocumentKey(entityType, entityId), refresh = true))
            .mapNotNull { response ->
                when (response) {
                    is StoreReadResponse.Data -> response.value.map { it.toDomain() }
                    // Offline-first: fetch error is non-fatal — the SoT reader emits the cached
                    // (possibly empty) list as a separate Data response.
                    else -> null
                }
            }
    }

    override fun downloadDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
    ): Flow<HttpResponse> {
        return dataManagerDocument.downloadDocument(entityType, entityId, documentId)
    }

    override suspend fun removeDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
    ): GenericResponse {
        return dataManagerDocument.removeDocument(entityType, entityId, documentId)
    }
}

// ---------------------------------------------------------------------------
// Inline mapping helper — private to this file (repo side)
// ---------------------------------------------------------------------------

private fun DocumentEntity.toDomain(): Document = Document(
    id = id,
    parentEntityType = parentEntityType,
    parentEntityId = parentEntityId,
    name = name,
    fileName = fileName,
    size = size,
    type = type,
    description = description,
)
