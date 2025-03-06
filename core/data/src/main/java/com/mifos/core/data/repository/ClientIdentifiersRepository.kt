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

import com.mifos.core.model.objects.noncoreobjects.Identifier
import kotlinx.coroutines.flow.Flow
import org.openapitools.client.models.DeleteClientsClientIdIdentifiersIdentifierIdResponse

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface ClientIdentifiersRepository {

    fun getClientIdentifiers(clientId: Int): Flow<List<Identifier>>

    suspend fun deleteClientIdentifier(
        clientId: Int,
        identifierId: Int,
    ): DeleteClientsClientIdIdentifiersIdentifierIdResponse
}
