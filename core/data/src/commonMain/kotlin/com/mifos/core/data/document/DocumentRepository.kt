/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.document

import com.mifos.core.model.objects.noncoreobjects.Document
import io.ktor.client.request.forms.MultiPartFormDataContent

/**
 * Per-feature document repository (Phase C Wave 9 of store5-adoption).
 *
 * Modern suspend-only contract. All methods throw on HTTP failure or transport
 * error — callers wrap mutations in `SubmitHandler.submit { ... }` and wrap
 * reads in explicit try/catch (re-throwing `CancellationException`) to drive
 * `ScreenState<T>`. No legacy `DataState<*>` / `Flow<DataState<*>>`.
 *
 * Documents are **not** Store5-cached: they are resource-scoped lists (per
 * client / loan / savings account / group) that are CRUD-mutated whenever the
 * user uploads / updates / removes a document, and have no useful offline
 * value because the binary file body is fetched on-demand by
 * [downloadDocument]. The list itself is small enough that re-fetching on
 * pull-to-refresh is cheaper than a Store5 read pipeline. See
 * RULE-STORE5-FETCH-001 — direct suspend reads + try/catch → `ScreenState`
 * is the documented exception. Same call-site shape as `NoteRepository`
 * (Wave 7) and `PathTrackingRepository` (Wave 8).
 *
 * Upload / update take a pre-built `MultiPartFormDataContent` so the existing
 * `core.ui.util.multipartRequestBody(...)` builder remains the single
 * source-of-truth for multipart shape across the codebase. No platform actuals
 * required for the upload path itself — file-picking + byte-reading uses
 * `io.github.vinceglb.filekit` (KMP) and the resulting `ByteArray` feeds into
 * the same builder on every platform.
 */
interface DocumentRepository {

    /**
     * Retrieve the documents attached to [entityType] / [entityId]. Throws on
     * HTTP failure or transport error.
     */
    suspend fun getDocuments(
        entityType: String,
        entityId: Int,
    ): List<Document>

    /**
     * Upload a new document. The multipart body must carry the canonical
     * Fineract form fields (`file`, `name`, optional `description`) — build it
     * with `core.ui.util.multipartRequestBody(...)`. Throws on HTTP failure or
     * transport error.
     */
    suspend fun createDocument(
        entityType: String,
        entityId: Int,
        file: MultiPartFormDataContent,
    )

    /**
     * Replace an existing document. Same multipart shape as [createDocument].
     * Throws on HTTP failure or transport error.
     */
    suspend fun updateDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
        file: MultiPartFormDataContent,
    )

    /**
     * Download the binary attachment for a document. Returns `Unit` because the
     * Wave 9 UI only needs the success signal (the legacy ViewModel surfaced
     * only a snackbar — no on-device persistence). Throws on HTTP failure or
     * transport error.
     *
     * If/when first-class on-device file persistence lands, this method's
     * signature can grow a return type (e.g. `ByteArray` or a typed path)
     * without breaking the upload/delete callers.
     */
    suspend fun downloadDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
    )

    /**
     * Delete a document. Throws on HTTP failure or transport error.
     */
    suspend fun removeDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
    )
}
