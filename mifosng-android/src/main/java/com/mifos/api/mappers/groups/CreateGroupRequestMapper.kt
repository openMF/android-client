package com.mifos.api.mappers.groups

import com.mifos.objects.group.GroupPayload
import org.apache.fineract.client.models.PostGroupsRequest
import org.mifos.core.data.AbstractMapper

object CreateGroupRequestMapper: AbstractMapper<PostGroupsRequest, GroupPayload>() {
    override fun mapFromEntity(entity: PostGroupsRequest): GroupPayload {
        return GroupPayload().apply {
            officeId = entity.officeId!!
            name = entity.name
            isActive = entity.active!!
        }
    }

    override fun mapToEntity(domainModel: GroupPayload): PostGroupsRequest {
        return PostGroupsRequest().apply {
            officeId = domainModel.officeId
            name = domainModel.name
            active = domainModel.isActive
        }
    }
}