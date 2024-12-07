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

import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Center
import org.apache.fineract.client.models.GetCentersResponse
import org.mifos.core.data.AbstractMapper

object GetCentersResponseMapper : AbstractMapper<GetCentersResponse, Page<Center>>() {

    override fun mapFromEntity(entity: GetCentersResponse): Page<Center> {
        return Page<Center>().apply {
            totalFilteredRecords = entity.totalFilteredRecords!!
            pageItems = CenterMapper.mapFromEntityList(entity.pageItems!!)
        }
    }

    override fun mapToEntity(domainModel: Page<Center>): GetCentersResponse {
        return GetCentersResponse().apply {
            totalFilteredRecords = domainModel.totalFilteredRecords
            pageItems = CenterMapper.mapToEntityList(domainModel.pageItems)
        }
    }
}
