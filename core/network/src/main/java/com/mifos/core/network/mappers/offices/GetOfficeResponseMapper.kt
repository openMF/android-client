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

import com.mifos.core.objects.organisation.Office
import org.apache.fineract.client.models.GetOfficesResponse
import org.mifos.core.data.AbstractMapper
import java.util.Date

object GetOfficeResponseMapper : AbstractMapper<GetOfficesResponse, Office>() {

    override fun mapFromEntity(entity: GetOfficesResponse): Office {
        return Office().apply {
            id = entity.id?.toInt()
            name = entity.name
            nameDecorated = entity.nameDecorated
            openingDate = listOf(
                entity.openingDate?.year,
                entity.openingDate?.month,
                entity.openingDate?.year,
            )
            externalId = entity.externalId
        }
    }

    override fun mapToEntity(domainModel: Office): GetOfficesResponse {
        return GetOfficesResponse().apply {
            id = domainModel.id?.toLong()
            name = domainModel.name
            nameDecorated = domainModel.nameDecorated
            openingDate = Date().apply {
                year = domainModel.openingDate[0]!!
                month = domainModel.openingDate[1]!!
                date = domainModel.openingDate[2]!!
            }
            externalId = domainModel.externalId
        }
    }
}
