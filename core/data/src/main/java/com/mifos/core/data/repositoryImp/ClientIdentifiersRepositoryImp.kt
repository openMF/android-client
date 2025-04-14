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

import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.model.objects.noncoreobjects.Identifier
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.model.DeleteClientsClientIdIdentifiersIdentifierIdResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class ClientIdentifiersRepositoryImp(
    private val dataManagerClient: DataManagerClient,
) : ClientIdentifiersRepository {

    override fun getClientIdentifiers(clientId: Int): Flow<List<Identifier>> {
        return flow {
            emit(dataManagerClient.getClientIdentifiers(clientId))
        }
    }

    override suspend fun deleteClientIdentifier(
        clientId: Int,
        identifierId: Int,
    ): DeleteClientsClientIdIdentifiersIdentifierIdResponse {
        return dataManagerClient.deleteClientIdentifier(clientId, identifierId)
    }
}
