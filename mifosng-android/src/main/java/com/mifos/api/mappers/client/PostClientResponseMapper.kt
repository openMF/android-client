package com.mifos.api.mappers.client

import com.mifos.objects.client.Client
import org.apache.fineract.client.models.PostClientsResponse
import org.mifos.core.data.AbstractMapper

object PostClientResponseMapper: AbstractMapper<PostClientsResponse, Client>() {
    override fun mapFromEntity(entity: PostClientsResponse): Client {
        return Client().apply {
            id = entity.clientId!!
            officeId = entity.officeId!!
            groupId = entity.groupId!!
            isImagePresent = false
        }
    }

    override fun mapToEntity(domainModel: Client): PostClientsResponse {
        TODO("Not yet implemented")
    }
}