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

import com.mifos.core.dbobjects.client.Status
import com.mifos.core.dbobjects.group.Group
import org.mifos.core.data.AbstractMapper
import org.openapitools.client.models.GetGroupsPageItems
import org.openapitools.client.models.GetGroupsStatus

object GroupMapper : AbstractMapper<GetGroupsPageItems, Group>() {

    override fun mapFromEntity(entity: GetGroupsPageItems): Group {
        return Group().apply {
            id = entity.id?.toInt()
            name = entity.name
            active = entity.active
            officeId = entity.officeId?.toInt()
            officeName = entity.officeName
            hierarchy = entity.hierarchy
            status = Status().apply {
                id = entity.status?.id!!.toInt()
                code = entity.status?.code
                value = entity.status?.description
            }
        }
    }

    override fun mapToEntity(domainModel: Group): GetGroupsPageItems {
        return GetGroupsPageItems(
            id = domainModel.id?.toLong(),
            name = domainModel.name,
            active = domainModel.active,
            officeId = domainModel.officeId?.toLong(),
            officeName = domainModel.officeName,
            hierarchy = domainModel.hierarchy,
            status = GetGroupsStatus(
                id = domainModel.status?.id?.toLong(),
                code = domainModel.status?.code,
                description = domainModel.status?.value,
            ),
        )
    }
}
