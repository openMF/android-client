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

import com.mifos.core.network.model.DeleteClientsClientIdIdentifiersIdentifierIdResponse
import com.mifos.core.network.model.GetClientsClientIdIdentifiersResponse
import com.mifos.core.network.model.GetClientsClientIdIdentifiersTemplateResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow

interface ClientIdentifierApi {
    /**
     * List all Identifiers for a Client
     * Example Requests: clients/1/identifiers   clients/1/identifiers?fields&#x3D;documentKey,documentType,description
     * Responses:
     *  - 200: OK
     *
     * @param clientId clientId
     * @return [kotlin.collections.List<GetClientsClientIdIdentifiersResponse>]
     */
    @GET("clients/{clientId}/identifiers")
    fun retrieveAllClientIdentifiers(@Path("clientId") clientId: Long): Flow<List<GetClientsClientIdIdentifiersResponse>>

    /**
     * Retrieve Client Identifier Details Template
     * This is a convenience resource useful for building maintenance user interface screens for client applications. The template data returned consists of any or all of:   Field Defaults  Allowed description Lists   Example Request: clients/1/identifiers/template
     * Responses:
     *  - 200: OK
     *
     * @param clientId clientId
     * @return [GetClientsClientIdIdentifiersTemplateResponse]
     */
    @GET("clients/{clientId}/identifiers/template")
    suspend fun newClientIdentifierDetails(@Path("clientId") clientId: Long): GetClientsClientIdIdentifiersTemplateResponse

    /**
     * Delete a Client Identifier
     * Deletes a Client Identifier
     * Responses:
     *  - 200: OK
     *
     * @param clientId clientId
     * @param identifierId identifierId
     * @return [DeleteClientsClientIdIdentifiersIdentifierIdResponse]
     */
    @DELETE("clients/{clientId}/identifiers/{identifierId}")
    suspend fun deleteClientIdentifier(
        @Path("clientId") clientId: Long,
        @Path("identifierId") identifierId: Long,
    ): DeleteClientsClientIdIdentifiersIdentifierIdResponse
}
