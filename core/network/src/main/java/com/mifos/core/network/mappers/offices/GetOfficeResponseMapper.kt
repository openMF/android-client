/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.mappers.offices

import com.mifos.room.entities.organisation.OfficeEntity
import org.mifos.core.data.AbstractMapper
import org.openapitools.client.models.GetOfficesResponse

object GetOfficeResponseMapper : AbstractMapper<GetOfficesResponse, OfficeEntity>() {

    override fun mapFromEntity(entity: GetOfficesResponse): OfficeEntity {
        return OfficeEntity(
            id = entity.id?.toInt(),
            externalId = entity.externalId,
            name = entity.name,
            nameDecorated = entity.nameDecorated,
            officeOpeningDate = null,
            openingDate = emptyList(),
        )
    }

    override fun mapToEntity(domainModel: OfficeEntity): GetOfficesResponse {
        return GetOfficesResponse(
            id = domainModel.id?.toLong(),
            name = domainModel.name,
            nameDecorated = domainModel.nameDecorated,
            externalId = domainModel.externalId,
        )
    }
}
