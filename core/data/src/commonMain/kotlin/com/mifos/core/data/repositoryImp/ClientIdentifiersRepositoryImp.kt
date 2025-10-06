/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.model.objects.noncoreobjects.Identifier
import com.mifos.core.model.objects.noncoreobjects.IdentifierPayload
import com.mifos.core.model.objects.noncoreobjects.IdentifierTemplate
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerIdentifiers
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by Arin Yadav on 12/09/2025.
 */
class ClientIdentifiersRepositoryImp(
    private val dataManagerIdentifiers: DataManagerIdentifiers,
) : ClientIdentifiersRepository {

    override fun getClientListIdentifiers(clientId: Long): Flow<DataState<List<Identifier>>> {
        return dataManagerIdentifiers.getClientListIdentifiers(clientId).asDataStateFlow()
    }

    override fun getClientIdentifiers(clientId: Long, identifierId: Long): Flow<DataState<Identifier>> {
        return dataManagerIdentifiers.getClientIdentifiers(clientId, identifierId).asDataStateFlow()
    }

    override fun getClientIdentifierTemplate(clientId: Long): Flow<DataState<IdentifierTemplate>> {
        return dataManagerIdentifiers.getClientIdentifierTemplate(clientId).asDataStateFlow()
    }

    override suspend fun deleteClientIdentifier(clientId: Long, identifierId: Long): GenericResponse {
        return dataManagerIdentifiers.deleteClientIdentifier(clientId, identifierId)
    }

    override suspend fun createClientIdentifier(
        clientId: Long,
        identifierPayload: IdentifierPayload,
    ): HttpResponse {
        return dataManagerIdentifiers.createClientIdentifier(clientId, identifierPayload)
    }

    override suspend fun updateClientIdentifier(
        clientId: Long,
        identifierId: Long,
        identifierPayload: IdentifierPayload,
    ): GenericResponse {
        return dataManagerIdentifiers.updateClientIdentifier(clientId, identifierId, identifierPayload)
    }
}
