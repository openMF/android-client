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