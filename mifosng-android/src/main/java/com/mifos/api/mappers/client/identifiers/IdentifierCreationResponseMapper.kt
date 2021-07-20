package com.mifos.api.mappers.client.identifiers

import com.mifos.objects.noncore.IdentifierCreationResponse
import org.apache.fineract.client.models.PostClientsClientIdIdentifiersResponse
import org.mifos.core.data.AbstractMapper

object IdentifierCreationResponseMapper :
    AbstractMapper<PostClientsClientIdIdentifiersResponse, IdentifierCreationResponse>() {
    override fun mapFromEntity(entity: PostClientsClientIdIdentifiersResponse): IdentifierCreationResponse {
        return IdentifierCreationResponse().apply {
            clientId = entity.clientId!!
            officeId = entity.officeId!!
            resourceId = entity.resourceId!!
        }
    }

    override fun mapToEntity(domainModel: IdentifierCreationResponse): PostClientsClientIdIdentifiersResponse {
        return PostClientsClientIdIdentifiersResponse().apply {
            clientId = domainModel.clientId
            officeId = domainModel.officeId
            resourceId = domainModel.resourceId
        }
    }
}