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
import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.client.Client
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class ClientDetailsRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) :
    ClientDetailsRepository {

    override fun uploadClientImage(id: Int, file: MultipartBody.Part?): Observable<ResponseBody> {
        return dataManagerClient.uploadClientImage(id, file)
    }

    override fun deleteClientImage(clientId: Int): Observable<ResponseBody> {
        return dataManagerClient.deleteClientImage(clientId)
    }

    override suspend fun getClientAccounts(clientId: Int): ClientAccounts {
        return dataManagerClient.getClientAccounts(clientId)
    }

    override suspend fun getClient(clientId: Int): Client {
        return dataManagerClient.getClient(clientId)
    }
}
