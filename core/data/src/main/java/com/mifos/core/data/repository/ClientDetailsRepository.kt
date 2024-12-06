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

import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.client.Client
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface ClientDetailsRepository {

    fun uploadClientImage(id: Int, file: MultipartBody.Part?): Observable<ResponseBody>

    fun deleteClientImage(clientId: Int): Observable<ResponseBody>

    fun getClientAccounts(clientId: Int): Observable<ClientAccounts>

    fun getClient(clientId: Int): Observable<Client>
}
