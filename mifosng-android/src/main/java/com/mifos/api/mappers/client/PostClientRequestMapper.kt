package com.mifos.api.mappers.client

import com.mifos.objects.client.ClientPayload
import org.apache.fineract.client.models.PostClientsRequest
import org.mifos.core.data.AbstractMapper

object PostClientRequestMapper: AbstractMapper<PostClientsRequest, ClientPayload>() {
    override fun mapFromEntity(entity: PostClientsRequest): ClientPayload {
        return ClientPayload().apply {
            officeId = entity.officeId
            firstname = entity.fullname!!.split(" ")[0]
            lastname = entity.fullname!!.split(" ")[1]
            dateFormat = entity.dateFormat
            locale = entity.locale
            activationDate = entity.activationDate
            isActive = entity.active!!
        }
    }

    override fun mapToEntity(domainModel: ClientPayload): PostClientsRequest {
        val middleName = if (domainModel.middlename  != null) domainModel.middlename else ""
        return PostClientsRequest().apply {
            officeId = domainModel.officeId
            fullname = "${domainModel.firstname} $middleName ${domainModel.lastname}"
            dateFormat = domainModel.dateFormat
            locale = domainModel.locale
            activationDate = domainModel.activationDate
            active = domainModel.isActive
            groupId = 1
        }
    }

}