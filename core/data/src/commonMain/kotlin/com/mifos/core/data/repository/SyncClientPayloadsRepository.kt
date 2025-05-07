/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.room.entities.client.ClientPayloadEntity
import kotlinx.coroutines.flow.Flow

interface SyncClientPayloadsRepository {

    fun allDatabaseClientPayload(): Flow<DataState<List<ClientPayloadEntity>>>

    suspend fun createClient(clientPayload: ClientPayloadEntity): Int?

    fun deleteAndUpdatePayloads(
        id: Int,
        clientCreationTIme: Long,
    ): Flow<DataState<List<ClientPayloadEntity>>>

    suspend fun updateClientPayload(clientPayload: ClientPayloadEntity)
}
