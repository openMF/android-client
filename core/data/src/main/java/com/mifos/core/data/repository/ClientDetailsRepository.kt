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

import com.mifos.room.entities.accounts.ClientAccounts
import com.mifos.room.entities.client.Client
import okhttp3.MultipartBody
import okhttp3.ResponseBody

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface ClientDetailsRepository {

    suspend fun uploadClientImage(id: Int, file: MultipartBody.Part?)

    suspend fun deleteClientImage(clientId: Int): ResponseBody

    suspend fun getClientAccounts(clientId: Int): ClientAccounts

    suspend fun getClient(clientId: Int): Client
}
