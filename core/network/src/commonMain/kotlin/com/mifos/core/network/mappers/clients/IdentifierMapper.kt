/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.mappers.clients

import com.mifos.core.model.objects.noncoreobjects.DocumentType
import com.mifos.core.model.objects.noncoreobjects.Identifier
import com.mifos.core.network.data.AbstractMapper
import com.mifos.core.network.model.GetClientsClientIdIdentifiersResponse
import com.mifos.core.network.model.GetClientsDocumentType

/**
 * Created by Aditya Gupta on 30/08/23.
 */
object IdentifierMapper : AbstractMapper<GetClientsClientIdIdentifiersResponse, Identifier>() {
    override fun mapFromEntity(entity: GetClientsClientIdIdentifiersResponse): Identifier {
        return Identifier(
            id = entity.id?.toInt(),
            clientId = entity.clientId?.toInt(),
            documentKey = entity.documentKey,
            description = entity.description,
            documentType = entity.documentType?.let {
                DocumentType(
                    id = it.id?.toInt(),
                    name = it.name,
                )
            },
            status = entity.status,
        )
    }

    override fun mapToEntity(domainModel: Identifier): GetClientsClientIdIdentifiersResponse {
        return GetClientsClientIdIdentifiersResponse(
            id = domainModel.id?.toLong(),
            clientId = domainModel.clientId?.toLong(),
            documentKey = domainModel.documentKey,
            description = domainModel.description,
            documentType = domainModel.documentType?.let {
                GetClientsDocumentType(
                    id = it.id?.toLong(),
                    name = it.name,
                )
            },
            status = domainModel.status,
        )
    }
}
