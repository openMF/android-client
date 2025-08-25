package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.ClientDetailsEditRepository
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.room.entities.client.ClientPayloadEntity

class ClientDetailsEditRepositoryImpl(
    private val dataManagerClient: DataManagerClient,
) : ClientDetailsEditRepository {
    override suspend fun updateClient(clientId:Int ,clientPayload: ClientPayloadEntity): Int? {
        return dataManagerClient.updateClient(clientId,clientPayload)
    }
}