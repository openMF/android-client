package com.mifos.api.mappers.client

import com.mifos.objects.client.ActivatePayload
import org.apache.fineract.client.models.PostClientsClientIdRequest
import org.mifos.core.data.AbstractMapper

object ClientActivatingRequestMapper: AbstractMapper<PostClientsClientIdRequest, ActivatePayload>() {
    override fun mapFromEntity(entity: PostClientsClientIdRequest): ActivatePayload {
        return ActivatePayload().apply {
            activationDate = entity.activationDate
            dateFormat = entity.dateFormat
            locale = entity.locale
        }
    }

    override fun mapToEntity(domainModel: ActivatePayload): PostClientsClientIdRequest {
        return PostClientsClientIdRequest().apply {
            activationDate = domainModel.activationDate
            dateFormat = domainModel.dateFormat
            locale = domainModel.locale
        }
    }
}