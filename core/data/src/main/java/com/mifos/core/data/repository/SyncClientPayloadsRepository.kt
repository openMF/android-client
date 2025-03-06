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

import com.mifos.room.entities.client.ClientPayload
import kotlinx.coroutines.flow.Flow

interface SyncClientPayloadsRepository {

    fun allDatabaseClientPayload(): Flow<List<ClientPayload>>

    suspend fun createClient(clientPayload: ClientPayload): Int?

    fun deleteAndUpdatePayloads(
        id: Int,
        clientCreationTIme: Long,
    ): Flow<List<ClientPayload>>

    suspend fun updateClientPayload(clientPayload: ClientPayload)
}
