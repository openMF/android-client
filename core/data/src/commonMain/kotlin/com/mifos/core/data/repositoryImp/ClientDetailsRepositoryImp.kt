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

import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.room.entities.accounts.ClientAccounts
import com.mifos.room.entities.client.ClientEntity

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class ClientDetailsRepositoryImp(
    private val dataManagerClient: DataManagerClient,
) : ClientDetailsRepository {

    override suspend fun uploadClientImage(clientId: Int, image: String) {
        dataManagerClient.uploadClientImage(
            clientId = clientId,
            typedFile = "data:image/png;base64,$image",
        )
    }

    override suspend fun deleteClientImage(clientId: Int) {
        dataManagerClient.deleteClientImage(clientId)
    }

    override suspend fun getClientAccounts(clientId: Int): ClientAccounts {
        return dataManagerClient.getClientAccounts(clientId)
    }

    override suspend fun getClient(clientId: Int): ClientEntity {
        return dataManagerClient.getClient(clientId)
    }
}
