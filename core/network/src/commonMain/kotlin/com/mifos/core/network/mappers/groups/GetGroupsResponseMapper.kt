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

import com.mifos.core.common.utils.Page
import com.mifos.core.network.data.AbstractMapper
import com.mifos.core.network.model.GetGroupsResponse
import com.mifos.room.entities.group.GroupEntity

object GetGroupsResponseMapper : AbstractMapper<GetGroupsResponse, Page<GroupEntity>>() {

    override fun mapFromEntity(entity: GetGroupsResponse): Page<GroupEntity> {
        return Page<GroupEntity>().apply {
            totalFilteredRecords = entity.totalFilteredRecords!!
            pageItems = GroupMapper.mapFromEntityList(entity.pageItems!!.toList())
        }
    }

    override fun mapToEntity(domainModel: Page<GroupEntity>): GetGroupsResponse {
        return GetGroupsResponse(
            totalFilteredRecords = domainModel.totalFilteredRecords,
            pageItems = GroupMapper.mapToEntityList(domainModel.pageItems).toSet(),
        )
    }
}
