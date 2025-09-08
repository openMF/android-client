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

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.clients.ClientAddressEntity
import com.mifos.core.network.model.PostClientAddressRequest
import com.mifos.core.network.model.PostClientAddressResponse
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
interface CreateNewClientRepository {

    fun clientTemplate(): Flow<DataState<ClientsTemplateEntity>>

    fun offices(): Flow<DataState<List<OfficeEntity>>>

    fun getStaffInOffice(officeId: Int): Flow<DataState<List<StaffEntity>>>

    suspend fun createClient(clientPayload: ClientPayloadEntity): Int?

    suspend fun uploadClientImage(clientId: Int, image: MultiPartFormDataContent)

    suspend fun getAddressConfiguration(): AddressConfiguration

    suspend fun getAddressTemplate(): AddressTemplate

    suspend fun getAddresses(clientId: Int): List<ClientAddressEntity>

    suspend fun createClientAddress(
        clientId: Int,
        addressTypeId: Int,
        addressRequest: PostClientAddressRequest,
    ): PostClientAddressResponse
}
