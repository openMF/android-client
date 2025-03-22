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
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerOffices
import com.mifos.core.network.datamanager.DataManagerStaff
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class CreateNewClientRepositoryImp @Inject constructor(
    private val dataManagerClient: DataManagerClient,
    private val dataManagerOffices: DataManagerOffices,
    private val dataManagerStaff: DataManagerStaff,
) : CreateNewClientRepository {

    override fun clientTemplate(): Flow<ClientsTemplateEntity> {
        return dataManagerClient.clientTemplate
    }

    override fun offices(): Flow<List<OfficeEntity>> {
        return dataManagerOffices.offices()
    }

    override fun getStaffInOffice(officeId: Int): Flow<List<StaffEntity>> {
        return dataManagerStaff.getStaffInOffice(officeId)
    }

    override suspend fun createClient(clientPayload: ClientPayloadEntity): Int? {
        return dataManagerClient.createClient(clientPayload)
    }

    override suspend fun uploadClientImage(id: Int, file: MultipartBody.Part?) {
        dataManagerClient.uploadClientImage(id, file)
    }
}
