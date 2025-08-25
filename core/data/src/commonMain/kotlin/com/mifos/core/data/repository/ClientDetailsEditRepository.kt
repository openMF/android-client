package com.mifos.core.data.repository

import com.mifos.room.entities.client.ClientPayloadEntity

interface ClientDetailsEditRepository {
    suspend fun updateClient(clientId: Int, clientPayload: ClientPayloadEntity): Int?
}