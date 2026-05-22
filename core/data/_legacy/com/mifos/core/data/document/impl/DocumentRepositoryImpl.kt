/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.document.impl

import com.mifos.core.data.document.DocumentRepository
import com.mifos.core.model.objects.noncoreobjects.Document
import com.mifos.core.network.document.api.DocumentApi
import io.ktor.client.request.forms.MultiPartFormDataContent

/**
 * Default [DocumentRepository] backed by [DocumentApi] (per-feature Ktorfit
 * interface, Phase C Wave 9 of store5-adoption).
 *
 * Errors propagate as exceptions — caller is responsible for wrapping in
 * `SubmitHandler.submit { ... }` (mutations) or try/catch → `ScreenState.Error`
 * (reads). No `DataState<*>`, no `withNetworkCheck`, no `runCatching`. The
 * shared `Document` model is the on-wire shape — no DTO/mapper needed
 * (mirrors the legacy `DataManagerDocument.getDocumentsList` call, which
 * already returned the model type directly).
 */
class DocumentRepositoryImpl(
    private val documentApi: DocumentApi,
) : DocumentRepository {

    override suspend fun getDocuments(
        entityType: String,
        entityId: Int,
    ): List<Document> = documentApi.getDocuments(entityType, entityId)

    override suspend fun createDocument(
        entityType: String,
        entityId: Int,
        file: MultiPartFormDataContent,
    ) {
        documentApi.createDocument(entityType, entityId, file)
    }

    override suspend fun updateDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
        file: MultiPartFormDataContent,
    ) {
        documentApi.updateDocument(entityType, entityId, documentId, file)
    }

    override suspend fun downloadDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
    ) {
        // The current UI surface only needs the success signal — the legacy
        // VM emitted a snackbar but never persisted the body. We discard the
        // `HttpResponse` until first-class on-device file persistence lands;
        // when it does, the return type grows (see DocumentRepository kdoc).
        documentApi.downloadDocument(entityType, entityId, documentId)
    }

    override suspend fun removeDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
    ) {
        documentApi.removeDocument(entityType, entityId, documentId)
    }
}
