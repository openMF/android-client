/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

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
import kotlinx.coroutines.flow.Flow

/**
 * @deprecated Replaced by `com.mifos.core.network.document.api.DocumentApi` (Phase C Wave 9
 * of store5-adoption). The new interface is suspend-only and lives next to the per-feature
 * `core/network/<resource>/api/` layout. This legacy `DocumentService` is retained ONLY for
 * `core.network.BaseApiManager` + the Phase-D-pending `DataManagerDocument` (still consumed
 * by `SignatureRepositoryImp` until Wave 10).
 */
interface DocumentService {
    @Deprecated(
        message = "Use DocumentApi.getDocuments — suspend returning List<Document>.",
        replaceWith = ReplaceWith(
            "DocumentApi.getDocuments(entityType, entityId)",
            "com.mifos.core.network.document.api.DocumentApi",
        ),
    )
    @GET("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS)
    fun getDocuments(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
    ): Flow<List<Document>>

    /**
     * @param entityType              - Type for which document is being uploaded (Client, Loan
     * or Savings etc)
     * @param entityId                - Id of Entity
     * @param nameOfDocument          - Document Name
     * @param description             - Mandatory - Document Description
     * @param typedFile               - Mandatory
     */
    @Deprecated(
        message = "Use DocumentApi.createDocument.",
        replaceWith = ReplaceWith(
            "DocumentApi.createDocument(entityType, entityId, request)",
            "com.mifos.core.network.document.api.DocumentApi",
        ),
    )
    @POST("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS)
    suspend fun createDocument(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
        @Body request: MultiPartFormDataContent,
    ): GenericResponse

    /**
     * This Service is for downloading the Document with EntityType and EntityId and Document Id
     * Rest End Point :
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}/attachment
     *
     * @param entityType    - Type for which document is being uploaded (Client, Loan
     * or Savings etc)
     * @param entityId      - Id of Entity
     * @param documentId    - Document Id
     * @return ResponseBody
     */

    @Deprecated(
        message = "Use DocumentApi.downloadDocument — suspend returning HttpResponse.",
        replaceWith = ReplaceWith(
            "DocumentApi.downloadDocument(entityType, entityId, documentId)",
            "com.mifos.core.network.document.api.DocumentApi",
        ),
    )
    @Headers("Accept: text/plain, application/json, */*")
    @GET("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}/attachment")
    fun downloadDocument(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
        @Path("documentId") documentId: Int,
    ): Flow<HttpResponse>

    /**
     * This Service is for Deleting the Document with EntityType and EntityId and Document Id.
     * Rest End Point :
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}
     *
     * @param entityType    - Type for which document is being uploaded (Client, Loan
     * or Savings etc)
     * @param entityId      - Id of Entity
     * @param documentId    - Document Id
     * @return
     */
    @Deprecated(
        message = "Use DocumentApi.removeDocument.",
        replaceWith = ReplaceWith(
            "DocumentApi.removeDocument(entityType, entityId, documentId)",
            "com.mifos.core.network.document.api.DocumentApi",
        ),
    )
    @DELETE("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}")
    suspend fun removeDocument(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
        @Path("documentId") documentId: Int,
    ): GenericResponse

    /**
     * This Service for Updating the Document with EntityType and EntityId and Document Id.
     * Rest End Point :
     * PUT
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}
     *
     * @param entityType              - Type for which document is being uploaded (Client, Loan
     * or Savings etc)
     * @param entityId                - Id of Entity
     * @param documentId              - Id of document
     * @param nameOfDocument          - Document Name
     * @param description             - Mandatory - Document Description
     * @param typedFile               - Mandatory
     */
    @Deprecated(
        message = "Use DocumentApi.updateDocument.",
        replaceWith = ReplaceWith(
            "DocumentApi.updateDocument(entityType, entityId, documentId, request)",
            "com.mifos.core.network.document.api.DocumentApi",
        ),
    )
    @PUT("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}")
    suspend fun updateDocument(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Int,
        @Path("documentId") documentId: Int,
        @Body request: MultiPartFormDataContent,
    ): GenericResponse
}
