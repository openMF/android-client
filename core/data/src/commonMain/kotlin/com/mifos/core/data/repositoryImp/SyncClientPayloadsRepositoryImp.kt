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
import com.mifos.core.data.repository.SyncClientPayloadsRepository
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.room.entities.client.ClientPayloadEntity
import kotlinx.coroutines.flow.Flow

class SyncClientPayloadsRepositoryImp(
    private val dataManagerClient: DataManagerClient,
) : SyncClientPayloadsRepository {

    override fun allDatabaseClientPayload(): Flow<DataState<List<ClientPayloadEntity>>> {
        return dataManagerClient.allDatabaseClientPayload
            .asDataStateFlow()
    }

    override suspend fun createClient(clientPayload: ClientPayloadEntity): Int? {
        return dataManagerClient.createClient(clientPayload)
    }

    override fun deleteAndUpdatePayloads(
        id: Int,
        clientCreationTIme: Long,
    ): Flow<DataState<List<ClientPayloadEntity>>> {
        return dataManagerClient.deleteAndUpdatePayloads(id, clientCreationTIme)
            .asDataStateFlow()
    }

    override suspend fun updateClientPayload(clientPayload: ClientPayloadEntity) {
        dataManagerClient.updateClientPayload(clientPayload)
    }
}
