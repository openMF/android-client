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

import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.dbobjects.client.Client
import com.mifos.core.dbobjects.client.ClientPayload
import com.mifos.core.dbobjects.organisation.Office
import com.mifos.core.dbobjects.organisation.Staff
import com.mifos.core.dbobjects.templates.clients.ClientsTemplate
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerOffices
import com.mifos.core.network.datamanager.DataManagerStaff
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class CreateNewClientRepositoryImp @Inject constructor(
    private val dataManagerClient: DataManagerClient,
    private val dataManagerOffices: DataManagerOffices,
    private val dataManagerStaff: DataManagerStaff,
) : CreateNewClientRepository {

    override fun clientTemplate(): Observable<ClientsTemplate> {
        return dataManagerClient.clientTemplate
    }

    override suspend fun offices(): List<Office> {
        return dataManagerOffices.offices()
    }

    override suspend fun getStaffInOffice(officeId: Int): List<Staff> {
        return dataManagerStaff.getStaffInOffice(officeId)
    }

    override fun createClient(clientPayload: ClientPayload): Observable<Client> {
        return dataManagerClient.createClient(clientPayload)
    }

    override fun uploadClientImage(id: Int, file: MultipartBody.Part?): Observable<ResponseBody> {
        return dataManagerClient.uploadClientImage(id, file)
    }
}
