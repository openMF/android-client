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

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerOffices
import com.mifos.core.network.datamanager.DataManagerStaff
import com.mifos.room.entities.client.AddressConfiguration
import com.mifos.room.entities.client.AddressTemplate
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class CreateNewClientRepositoryImp(
    private val dataManagerClient: DataManagerClient,
    private val dataManagerOffices: DataManagerOffices,
    private val dataManagerStaff: DataManagerStaff,
) : CreateNewClientRepository {

    override fun clientTemplate(): Flow<DataState<ClientsTemplateEntity>> {
        return dataManagerClient.clientTemplate
            .asDataStateFlow()
    }

    override fun offices(): Flow<DataState<List<OfficeEntity>>> {
        return dataManagerOffices.fetchOffices()
            .asDataStateFlow()
    }

    override fun getStaffInOffice(officeId: Int): Flow<DataState<List<StaffEntity>>> {
        return dataManagerStaff.getStaffInOffice(officeId)
            .asDataStateFlow()
    }

    override suspend fun createClient(clientPayload: ClientPayloadEntity): Int? {
        return dataManagerClient.createClient(clientPayload)
    }

    override suspend fun uploadClientImage(clientId: Int, image: MultiPartFormDataContent) {
        return dataManagerClient.uploadClientImage(clientId, image)
    }

    override suspend fun getAddressConfiguration(): AddressConfiguration {
        return dataManagerClient.getAddressConfiguration()
    }

    override suspend fun getAddressTemplate(): AddressTemplate {
        return dataManagerClient.getAddressTemplate()
    }
}
