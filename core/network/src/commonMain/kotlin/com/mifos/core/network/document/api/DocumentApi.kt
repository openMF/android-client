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

import com.mifos.core.model.GenericResponse
import com.mifos.core.model.objects.noncoreobjects.Document
import com.mifos.core.network.APIEndPoint
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
 * Fineract `{entityType}/{entityId}/documents` domain endpoints.
 *
 * Upload / update endpoints take a pre-built `MultiPartFormDataContent` so
 * `core.ui.util.multipartRequestBody` works unchanged across platforms — no
 * platform-specific `@Multipart` / `@Part` actuals required.
 */
interface DocumentApi {

    /** List documents attached to [entityType] / [entityId]. */
    @GET("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS)
    suspend fun getDocuments(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
    ): List<Document>

    /**
     * Upload a new document attached to [entityType] / [entityId]. The multipart
     * body must carry the canonical Fineract form fields: `file` (binary), `name`,
     * and optional `description`. Use `core.ui.util.multipartRequestBody(...)`
     * to build the body.
     */
    @POST("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS)
    suspend fun createDocument(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
        @Body request: MultiPartFormDataContent,
    ): GenericResponse

    /** Update an existing document. Same multipart shape as [createDocument]. */
    @PUT("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}")
    suspend fun updateDocument(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
        @Path("documentId") documentId: Int,
        @Body request: MultiPartFormDataContent,
    ): GenericResponse

    /**
     * Download the binary attachment for a document. Returns the raw
     * `HttpResponse` so callers can stream the body themselves; per-platform
     * file persistence happens outside the Api surface.
     */
    @Headers("Accept: text/plain, application/json, */*")
    @GET("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}/attachment")
    suspend fun downloadDocument(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
        @Path("documentId") documentId: Int,
    ): HttpResponse

    /** Remove a document attached to [entityType] / [entityId]. */
    @DELETE("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}")
    suspend fun removeDocument(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
        @Path("documentId") documentId: Int,
    ): GenericResponse
}
