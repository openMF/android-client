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
import com.mifos.room.entities.client.Client
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class ClientDetailsRepositoryImp @Inject constructor(
    private val dataManagerClient: DataManagerClient,
) : ClientDetailsRepository {

    override suspend fun uploadClientImage(id: Int, file: MultipartBody.Part?) {
        dataManagerClient.uploadClientImage(id, file)
    }

    override suspend fun deleteClientImage(clientId: Int): ResponseBody {
        return dataManagerClient.deleteClientImage(clientId)
    }

    override suspend fun getClientAccounts(clientId: Int): ClientAccounts {
        return dataManagerClient.getClientAccounts(clientId)
    }

    override suspend fun getClient(clientId: Int): Client {
        return dataManagerClient.getClient(clientId)
    }
}
