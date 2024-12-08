/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.mappers.groups

import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Group
import org.mifos.core.data.AbstractMapper
import org.openapitools.client.models.GetGroupsResponse

object GetGroupsResponseMapper : AbstractMapper<GetGroupsResponse, Page<Group>>() {

    override fun mapFromEntity(entity: GetGroupsResponse): Page<Group> {
        return Page<Group>().apply {
            totalFilteredRecords = entity.totalFilteredRecords!!
            pageItems = GroupMapper.mapFromEntityList(entity.pageItems!!.toList())
        }
    }

    override fun mapToEntity(domainModel: Page<Group>): GetGroupsResponse {
        return GetGroupsResponse(
            totalFilteredRecords = domainModel.totalFilteredRecords,
            pageItems = GroupMapper.mapToEntityList(domainModel.pageItems).toSet()
        )
    }
}
