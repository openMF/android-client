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

import com.mifos.core.entity.client.Client
import com.mifos.core.entity.client.ClientPayload
import com.mifos.core.entity.templates.clients.ClientsTemplate
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.Staff
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface CreateNewClientRepository {

    fun clientTemplate(): Observable<ClientsTemplate>

    fun offices(): Flow<List<OfficeEntity>>

    fun getStaffInOffice(officeId: Int): Flow<List<Staff>>

    fun createClient(clientPayload: ClientPayload): Observable<Client>

    fun uploadClientImage(id: Int, file: MultipartBody.Part?): Observable<ResponseBody>
}
