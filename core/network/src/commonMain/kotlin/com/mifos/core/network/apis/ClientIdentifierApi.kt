/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.apis

import com.mifos.core.model.objects.noncoreobjects.Identifier
import com.mifos.core.model.objects.noncoreobjects.IdentifierPayload
import com.mifos.core.model.objects.noncoreobjects.IdentifierTemplate
import com.mifos.core.network.GenericResponse
import com.mifos.room.basemodel.APIEndPoint
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

interface ClientIdentifierApi {

    /**
     * Fetches the list of identifiers for a given client.
     *
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers
     *
     * @param clientId The unique ID of the client.
     * @return [Flow] emitting a list of [Identifier]s for the specified client.
     */
    @GET(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.IDENTIFIERS)
    fun getClientListIdentifiers(@Path("clientId") clientId: Long): Flow<List<Identifier>>

    /**
     * Retrieves a specific client identifier.
     *
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers/{identifierId}
     *
     * @param clientId The unique ID of the client.
     * @param identifierId The unique ID of the identifier.
     * @return [Flow] emitting the [Identifier] object.
     */
    @GET(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.IDENTIFIERS + "/{identifierId}")
    fun getClientIdentifiers(
        @Path("clientId") clientId: Long,
        @Path("identifierId") identifierId: Long,
    ): Flow<Identifier>

    /**
     * Fetches the client identifier template for a given client.
     *
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers/template
     *
     * @param clientId The unique ID of the client.
     * @return [Flow] emitting the [IdentifierTemplate] for the specified client.
     */
    @GET(APIEndPoint.CLIENTS + "/{clientId}/identifiers/template")
    fun getClientIdentifierTemplate(@Path("clientId") clientId: Long): Flow<IdentifierTemplate>

    /**
     * Deletes a client identifier for a given client.
     *
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers/{identifierId}
     *
     * @param clientId The unique ID of the client.
     * @param identifierId The unique ID of the identifier to be deleted.
     * @return [GenericResponse] indicating the result of the delete operation.
     */
    @DELETE(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.IDENTIFIERS + "/{identifierId}")
    suspend fun deleteClientIdentifier(
        @Path("clientId") clientId: Long,
        @Path("identifierId") identifierId: Long,
    ): GenericResponse

    /**
     * Creates a new identifier for a given client.
     *
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers
     *
     * @param clientId The unique ID of the client.
     * @param identifierPayload The payload containing identifier details.
     * @return [GenericResponse] indicating the result of the create operation.
     */
    @POST(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.IDENTIFIERS)
    suspend fun createClientIdentifier(
        @Path("clientId") clientId: Long,
        @Body identifierPayload: IdentifierPayload,
    ): HttpResponse

    /**
     * Updates an existing client identifier for a given client.
     *
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers/{identifierId}
     *
     * @param clientId The unique ID of the client.
     * @param identifierId The unique ID of the identifier to be updated.
     * @param identifierPayload The updated payload for the identifier.
     * @return [GenericResponse] indicating the result of the update operation.
     */
    @PUT(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.IDENTIFIERS + "/{identifierId}")
    suspend fun updateClientIdentifier(
        @Path("clientId") clientId: Long,
        @Path("identifierId") identifierId: Long,
        @Body identifierPayload: IdentifierPayload,
    ): GenericResponse
}
