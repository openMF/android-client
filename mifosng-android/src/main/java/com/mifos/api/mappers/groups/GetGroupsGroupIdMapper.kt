package com.mifos.api.mappers.groups

import com.mifos.objects.client.Status
import com.mifos.objects.group.Group
import org.apache.fineract.client.models.GetGroupsGroupIdResponse
import org.mifos.core.data.AbstractMapper

/**
 * active field missing in [GetGroupsGroupIdResponse]]
 */
object GetGroupsGroupIdMapper: AbstractMapper<GetGroupsGroupIdResponse, Group>() {


    override fun mapFromEntity(entity: GetGroupsGroupIdResponse): Group {
        return Group().apply {
            id = entity.id
            name = entity.name
            externalId = entity.externalId
            active = true
            officeId = entity.officeId
            officeName = entity.officeName
            hierarchy = entity.hierarchy
            timeline = entity.timeline?.let { GroupsTimelineMapper.mapFromEntity(it) }
        }
    }

    override fun mapToEntity(domainModel: Group): GetGroupsGroupIdResponse {
        return GetGroupsGroupIdResponse().apply {
            id = domainModel.id
            name = domainModel.name
            externalId = domainModel.externalId
            officeId = domainModel.officeId
            officeName = domainModel.officeName
            hierarchy = domainModel.hierarchy
            timeline = GroupsTimelineMapper.mapToEntity(domainModel.timeline)
        }
    }
}