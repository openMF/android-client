/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.mappers.client

import com.mifos.core.model.objects.noncoreobjects.DocumentType
import com.mifos.core.model.objects.noncoreobjects.Identifier
import com.mifos.core.network.data.AbstractMapper
import com.mifos.room.entities.client.ClientIdentifierEntity

object ClientIdentifierMapper : AbstractMapper<Identifier, ClientIdentifierEntity>() {

    override fun mapFromEntity(entity: Identifier): ClientIdentifierEntity {
        return ClientIdentifierEntity(
            localId = 0,
            id = entity.id,
            clientId = entity.clientId,
            documentKey = entity.documentKey,
            documentTypeName = entity.documentType?.name,
            documentTypeId = entity.documentType?.id,
            description = entity.description,
            status = entity.status,
        )
    }

    override fun mapToEntity(roomEntity: ClientIdentifierEntity): Identifier {
        return Identifier(
            id = roomEntity.id,
            clientId = roomEntity.clientId,
            documentKey = roomEntity.documentKey,
            description = roomEntity.description,
            status = roomEntity.status,
            documentType = if (
                roomEntity.documentTypeId != null || roomEntity.documentTypeName != null
            ) {
                DocumentType(
                    id = roomEntity.documentTypeId,
                    name = roomEntity.documentTypeName,
                )
            } else {
                null
            },
        )
    }
}
