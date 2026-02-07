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

import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.mappers.client.ClientAddressMapper
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.model.objects.clients.ClientAddressEntity
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerOffices
import com.mifos.core.network.datamanager.DataManagerStaff
import com.mifos.core.network.model.PostClientAddressRequest
import com.mifos.core.network.model.PostClientAddressResponse
import com.mifos.room.entities.client.AddressConfiguration
import com.mifos.room.entities.client.AddressTemplate
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import com.mifos.room.helper.ClientDaoHelper
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class CreateNewClientRepositoryImp(
    private val dataManagerClient: DataManagerClient,
    private val dataManagerOffices: DataManagerOffices,
    private val dataManagerStaff: DataManagerStaff,
    private val clientDaoHelper: ClientDaoHelper,
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

    override suspend fun getAddresses(clientId: Int): List<ClientAddressEntity> {
        val addresses = dataManagerClient.getClientAddresses(clientId = clientId)

        try {
            clientDaoHelper.deleteAddressesByClientId(clientId)

            if (addresses.isNotEmpty()) {
                val roomEntities = addresses.map {
                    ClientAddressMapper.mapFromEntity(it.copy(clientID = clientId))
                }
                clientDaoHelper.insertAddresses(roomEntities)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(e) { "Failed to update local cache for client addresses" }
        }
        return addresses
    }

    override suspend fun createClientAddress(
        clientId: Int,
        addressTypeId: Int,
        addressRequest: PostClientAddressRequest,
    ): PostClientAddressResponse {
        return dataManagerClient.createClientAddress(
            clientId = clientId,
            addressTypeId = addressTypeId,
            addressRequest = addressRequest,
        )
    }
}
