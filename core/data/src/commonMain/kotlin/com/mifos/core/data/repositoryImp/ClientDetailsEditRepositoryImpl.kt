/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.ClientDetailsEditRepository
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.room.entities.client.ClientPayloadEntity

class ClientDetailsEditRepositoryImpl(
    private val dataManagerClient: DataManagerClient,
) : ClientDetailsEditRepository {
    override suspend fun updateClient(clientId: Int, clientPayload: ClientPayloadEntity): Int? {
        return dataManagerClient.updateClient(clientId, clientPayload)
    }
}
