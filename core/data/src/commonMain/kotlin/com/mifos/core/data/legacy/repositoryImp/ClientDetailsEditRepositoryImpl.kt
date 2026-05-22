/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.legacy.repositoryImp

import com.mifos.core.data.repository.ClientDetailsEditRepository
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.room.client.entity.ClientPayloadEntity

class ClientDetailsEditRepositoryImpl(
    private val dataManagerClient: DataManagerClient,
) : ClientDetailsEditRepository {
    override suspend fun updateClient(clientId: Int, clientPayload: ClientPayloadEntity): Int? {
        return dataManagerClient.updateClient(clientId, clientPayload)
    }
}
