/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.mappers.centers

import com.mifos.core.common.utils.Page
import com.mifos.room.entities.group.CenterEntity
import org.mifos.core.data.AbstractMapper
import org.openapitools.client.models.GetCentersResponse

object GetCentersResponseMapper : AbstractMapper<GetCentersResponse, Page<CenterEntity>>() {

    override fun mapFromEntity(entity: GetCentersResponse): Page<CenterEntity> {
        return Page<CenterEntity>().apply {
            totalFilteredRecords = entity.totalFilteredRecords!!
            pageItems = CenterMapper.mapFromEntityList(entity.pageItems!!.toList())
        }
    }

    override fun mapToEntity(domainModel: Page<CenterEntity>): GetCentersResponse {
        return GetCentersResponse(
            totalFilteredRecords = domainModel.totalFilteredRecords,
            pageItems = CenterMapper.mapToEntityList(domainModel.pageItems).toSet(),
        )
    }
}
