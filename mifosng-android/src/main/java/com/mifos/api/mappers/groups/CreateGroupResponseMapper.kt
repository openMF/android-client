package com.mifos.api.mappers.groups

import com.mifos.objects.response.SaveResponse
import org.apache.fineract.client.models.PostGroupsResponse
import org.mifos.core.data.AbstractMapper

object CreateGroupResponseMapper: AbstractMapper<PostGroupsResponse, SaveResponse>() {
    override fun mapFromEntity(entity: PostGroupsResponse): SaveResponse {
        return SaveResponse().apply {
            officeId = entity.officeId
            groupId = entity.groupId
            resourceId = entity.resourceId
        }
    }

    override fun mapToEntity(domainModel: SaveResponse): PostGroupsResponse {
        return PostGroupsResponse().apply {
            officeId = domainModel.officeId
            groupId = domainModel.groupId
            resourceId = domainModel.resourceId
        }
    }
}