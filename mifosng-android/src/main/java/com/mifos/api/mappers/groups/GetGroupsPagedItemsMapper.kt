package com.mifos.api.mappers.groups

import com.mifos.objects.client.Status
import com.mifos.objects.group.Group
import org.apache.fineract.client.models.GetGroupsPageItems
import org.apache.fineract.client.models.GetGroupsStatus
import org.mifos.core.data.AbstractMapper

object GetGroupsPagedItemsMapper: AbstractMapper<GetGroupsPageItems, Group>() {
    override fun mapFromEntity(entity: GetGroupsPageItems): Group {
        return Group().apply {
            id = entity.id
            name = entity.name
            status = Status().apply {
                id = entity.status!!.id!!
                code = entity.status!!.code
                value = entity.status!!.description
            }
            active = entity.active
            officeId = entity.officeId
            officeName = entity.officeName
            hierarchy = entity.hierarchy
        }
    }

    override fun mapToEntity(domainModel: Group): GetGroupsPageItems {
        return GetGroupsPageItems().apply {
            id = domainModel.id
            name = domainModel.name
            status = GetGroupsStatus().apply {
                id = domainModel.status.id
                code = domainModel.status.code
                description = domainModel.status.value
            }
            active = domainModel.active
            officeId = domainModel.officeId
            officeName = domainModel.officeName
            hierarchy = domainModel.hierarchy
        }
    }
}