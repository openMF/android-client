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

import com.mifos.core.objects.client.Status
import com.mifos.core.objects.group.Group
import org.apache.fineract.client.models.GetGroupsPageItems
import org.apache.fineract.client.models.GetGroupsStatus
import org.mifos.core.data.AbstractMapper

object GroupMapper : AbstractMapper<GetGroupsPageItems, Group>() {

    override fun mapFromEntity(entity: GetGroupsPageItems): Group {
        return Group().apply {
            id = entity.id
            name = entity.name
            active = entity.active
            officeId = entity.officeId
            officeName = entity.officeName
            hierarchy = entity.hierarchy
            status = Status().apply {
                id = entity.status?.id!!
                code = entity.status?.code
                value = entity.status?.description
            }
        }
    }

    override fun mapToEntity(domainModel: Group): GetGroupsPageItems {
        return GetGroupsPageItems().apply {
            id = domainModel.id
            name = domainModel.name
            active = domainModel.active
            officeId = domainModel.officeId
            officeName = domainModel.officeName
            hierarchy = domainModel.hierarchy
            status = GetGroupsStatus().apply {
                id = domainModel.status?.id
                code = domainModel.status?.code
                description = domainModel.status?.value
            }
        }
    }
}
