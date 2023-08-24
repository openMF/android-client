package com.mifos.mappers.groups

import com.mifos.objects.client.Page
import com.mifos.objects.group.Group
import org.apache.fineract.client.models.GetGroupsResponse
import org.mifos.core.data.AbstractMapper

object GetGroupsResponseMapper : AbstractMapper<GetGroupsResponse, Page<Group>>() {

    override fun mapFromEntity(entity: GetGroupsResponse): Page<Group> {
        return Page<Group>().apply {
            totalFilteredRecords = entity.totalFilteredRecords!!
            pageItems = GroupMapper.mapFromEntityList(entity.pageItems!!)
        }
    }

    override fun mapToEntity(domainModel: Page<Group>): GetGroupsResponse {
        return GetGroupsResponse().apply {
            totalFilteredRecords = domainModel.totalFilteredRecords
            pageItems = GroupMapper.mapToEntityList(domainModel.pageItems)
        }
    }
}