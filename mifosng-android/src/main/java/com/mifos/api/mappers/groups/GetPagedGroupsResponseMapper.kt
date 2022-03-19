package com.mifos.api.mappers.groups

import com.mifos.objects.client.Page
import org.apache.fineract.client.models.GetGroupsResponse
import com.mifos.objects.group.Group
import org.mifos.core.data.AbstractMapper

object GetPagedGroupsResponseMapper: AbstractMapper<GetGroupsResponse, Page<Group>>() {
    override fun mapFromEntity(entity: GetGroupsResponse): Page<Group> {
        return Page<Group>().apply {
            totalFilteredRecords = entity.totalFilteredRecords!!
            pageItems = GetGroupsPagedItemsMapper.mapFromEntityList(entity.pageItems!!)
        }
    }

    override fun mapToEntity(domainModel: Page<Group>): GetGroupsResponse {
        return GetGroupsResponse().apply {
            totalFilteredRecords = domainModel.totalFilteredRecords
            pageItems = GetGroupsPagedItemsMapper.mapToEntityList(domainModel.pageItems)
        }
    }

}