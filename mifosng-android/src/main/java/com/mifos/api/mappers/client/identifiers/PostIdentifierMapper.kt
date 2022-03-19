package com.mifos.api.mappers.client.identifiers

import com.mifos.objects.noncore.IdentifierPayload
import org.apache.fineract.client.models.PostClientsClientIdIdentifiersRequest
import org.mifos.core.data.AbstractMapper

object PostIdentifierMapper :
    AbstractMapper<PostClientsClientIdIdentifiersRequest, IdentifierPayload>() {
    override fun mapFromEntity(entity: PostClientsClientIdIdentifiersRequest): IdentifierPayload {
        return IdentifierPayload().apply {
            documentTypeId = entity.documentTypeId
            documentKey = entity.documentKey
            description = entity.description
        }
    }

    override fun mapToEntity(domainModel: IdentifierPayload): PostClientsClientIdIdentifiersRequest {
        return PostClientsClientIdIdentifiersRequest().apply {
            documentTypeId = domainModel.documentTypeId
            documentKey = domainModel.documentKey
            description = domainModel.description
        }
    }
}