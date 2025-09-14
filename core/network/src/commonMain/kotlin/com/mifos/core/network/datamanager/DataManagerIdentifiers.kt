/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.model.objects.noncoreobjects.Identifier
import com.mifos.core.model.objects.noncoreobjects.IdentifierPayload
import com.mifos.core.model.objects.noncoreobjects.IdentifierTemplate
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by Arin Yadav on 12/09/25.
 */
class DataManagerIdentifiers(
    val mBaseApiManager: BaseApiManager,
) {

    /**
     * Fetches the list of identifiers for a given client.
     *
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers
     *
     * @param clientId The unique ID of the client.
     * @return [Flow] emitting a list of [Identifier]s for the specified client.
     */
    fun getClientListIdentifiers(clientId: Long): Flow<List<Identifier>> {
        return mBaseApiManager.clientIdentifiersApi.getClientListIdentifiers(clientId)
    }

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
    fun getClientIdentifiers(clientId: Long, identifierId: Long): Flow<Identifier> {
        return mBaseApiManager.clientIdentifiersApi.getClientIdentifiers(clientId, identifierId)
    }

    /**
     * Fetches the client identifier template for a given client.
     *
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers/template
     *
     * @param clientId The unique ID of the client.
     * @return [Flow] emitting the [IdentifierTemplate] for the specified client.
     */
    fun getClientIdentifierTemplate(clientId: Long): Flow<IdentifierTemplate> {
        return mBaseApiManager.clientIdentifiersApi.getClientIdentifierTemplate(clientId)
    }

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
    suspend fun deleteClientIdentifier(clientId: Long, identifierId: Long): GenericResponse {
        return mBaseApiManager.clientIdentifiersApi.deleteClientIdentifier(clientId, identifierId)
    }

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
    suspend fun createClientIdentifier(
        clientId: Long,
        identifierPayload: IdentifierPayload,
    ): HttpResponse {
        return mBaseApiManager.clientIdentifiersApi.createClientIdentifier(clientId, identifierPayload)
    }

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
    suspend fun updateClientIdentifier(
        clientId: Long,
        identifierId: Long,
        identifierPayload: IdentifierPayload,
    ): GenericResponse {
        return mBaseApiManager.clientIdentifiersApi.updateClientIdentifier(clientId, identifierId, identifierPayload)
    }
}
