/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.document.api

import com.mifos.core.model.objects.noncoreobjects.Document
import com.mifos.core.network.GenericResponse
import com.mifos.room.basemodel.APIEndPoint
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.HttpResponse

/**
 * Fineract `{entityType}/{entityId}/documents` endpoints. Per-feature API surface
 * (Phase C Wave 9 of store5-adoption). Modern suspend-only contract — `getDocuments`
 * and `downloadDocument` return `List<Document>` / `HttpResponse` directly instead
 * of `Flow<...>` (legacy `DocumentService` shape).
 *
 * Errors throw (`HttpException` / transport exceptions); callers handle via
 * `SubmitHandler.submit { ... }` for mutations or explicit try/catch wrapped to
 * `ScreenState<T>` for reads. No `runCatching` per RULE-NO-RUN-CATCHING-001.
 *
 * Upload / update endpoints take a pre-built `MultiPartFormDataContent` (the
 * canonical Ktor multipart shape used by the legacy `DocumentService`) so the
 * existing `core.ui.util.multipartRequestBody` builder continues to work
 * unchanged across platforms — no platform-specific `@Multipart`/`@Part`
 * actuals required.
 *
 * The legacy `DocumentService` is retained for `core.network.BaseApiManager`
 * + the still-Phase-D `core.network.datamanager.DataManagerDocument` (consumed
 * by `core.data.repositoryImp.SignatureRepositoryImp`). It will be removed in
 * Wave 10 when `SignatureRepository` migrates.
 */
interface DocumentApi {

    /**
     * Retrieve the list of documents attached to [entityType] / [entityId].
     * Throws on HTTP failure or transport error.
     *
     * @param entityType  Resource type — `clients`, `loans`, `savingsaccounts`, `groups`, etc.
     * @param entityId    Resource id.
     */
    @GET("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS)
    suspend fun getDocuments(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
    ): List<Document>

    /**
     * Create (upload) a new document attached to [entityType] / [entityId]. The
     * multipart body MUST carry the canonical Fineract form fields:
     * `file` (binary), `name`, and optional `description`. Use
     * `core.ui.util.multipartRequestBody(...)` to build the body.
     *
     * Throws on HTTP failure or transport error.
     */
    @POST("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS)
    suspend fun createDocument(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
        @Body request: MultiPartFormDataContent,
    ): GenericResponse

    /**
     * Update an existing document attached to [entityType] / [entityId]. Same
     * multipart shape as [createDocument]. Throws on HTTP failure or transport
     * error.
     */
    @PUT("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}")
    suspend fun updateDocument(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
        @Path("documentId") documentId: Int,
        @Body request: MultiPartFormDataContent,
    ): GenericResponse

    /**
     * Download the binary attachment associated with a document. Returns the raw
     * Ktor `HttpResponse` so callers can stream / persist the body themselves
     * (per-platform file write happens outside the API surface). Throws on HTTP
     * failure or transport error.
     */
    @Headers("Accept: text/plain, application/json, */*")
    @GET("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}/attachment")
    suspend fun downloadDocument(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
        @Path("documentId") documentId: Int,
    ): HttpResponse

    /**
     * Remove a document attached to [entityType] / [entityId]. Throws on HTTP
     * failure or transport error.
     */
    @DELETE("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}")
    suspend fun removeDocument(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
        @Path("documentId") documentId: Int,
    ): GenericResponse
}
