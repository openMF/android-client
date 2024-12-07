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

import com.mifos.core.objects.noncore.DocumentType
import com.mifos.core.objects.noncore.Identifier
import org.apache.fineract.client.models.GetClientsClientIdIdentifiersResponse
import org.apache.fineract.client.models.GetClientsDocumentType
import org.mifos.core.data.AbstractMapper

/**
 * Created by Aditya Gupta on 30/08/23.
 */
object IdentifierMapper : AbstractMapper<GetClientsClientIdIdentifiersResponse, Identifier>() {
    override fun mapFromEntity(entity: GetClientsClientIdIdentifiersResponse): Identifier {
        return Identifier().apply {
            id = entity.id
            clientId = entity.clientId
            documentKey = entity.documentKey
            description = entity.description
            documentType = entity.documentType?.let {
                DocumentType().apply {
                    id = it.id
                    name = it.name
                }
            }
        }
    }

    override fun mapToEntity(domainModel: Identifier): GetClientsClientIdIdentifiersResponse {
        return GetClientsClientIdIdentifiersResponse().apply {
            id = domainModel.id
            clientId = domainModel.clientId
            documentKey = domainModel.documentKey
            description = domainModel.description
            documentType = GetClientsDocumentType().apply {
                id = this.id
                name = this.name
            }
        }
    }
}
