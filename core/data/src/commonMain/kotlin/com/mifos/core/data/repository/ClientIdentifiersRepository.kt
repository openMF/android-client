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
import com.mifos.core.model.objects.noncoreobjects.Identifier
import com.mifos.core.model.objects.noncoreobjects.IdentifierPayload
import com.mifos.core.model.objects.noncoreobjects.IdentifierTemplate
import com.mifos.core.network.GenericResponse
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by Arin Yadav on 12/09/2025.
 */
interface ClientIdentifiersRepository {

    fun getClientListIdentifiers(clientId: Long): Flow<DataState<List<Identifier>>>

    fun getClientIdentifiers(clientId: Long, identifierId: Long): Flow<DataState<Identifier>>

    fun getClientIdentifierTemplate(clientId: Long): Flow<DataState<IdentifierTemplate>>

    suspend fun deleteClientIdentifier(clientId: Long, identifierId: Long): GenericResponse

    suspend fun createClientIdentifier(
        clientId: Long,
        identifierPayload: IdentifierPayload,
    ): HttpResponse

    suspend fun updateClientIdentifier(
        clientId: Long,
        identifierId: Long,
        identifierPayload: IdentifierPayload,
    ): GenericResponse
}
