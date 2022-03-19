package com.mifos.api.mappers.client

import com.mifos.objects.client.Client
import org.apache.fineract.client.models.GetClientsPageItemsResponse
import org.mifos.core.data.AbstractMapper

object ClientMapper: AbstractMapper<GetClientsPageItemsResponse, Client>() {

    override fun mapFromEntity(entity: GetClientsPageItemsResponse): Client {
        return Client().apply {
            id = entity.id!!
            accountNo = entity.accountNo
            fullname = entity.fullname
            firstname = entity.displayName!!.split(" ")[0]
            lastname = if (entity.displayName!!.split(" ").size >= 2) entity.displayName!!.split(" ")[1] else ""
            displayName = entity.displayName
            officeId = entity.officeId!!
            officeName = entity.officeName
            isActive = entity.active!!
            status = ClientStatusMapper.mapFromEntity(entity.status!!)
        }
    }

    override fun mapToEntity(domainModel: Client): GetClientsPageItemsResponse {
        return GetClientsPageItemsResponse().apply {
            id = domainModel.id
            accountNo = domainModel.accountNo
            fullname = domainModel.fullname
            displayName = domainModel.displayName
            officeId = domainModel.officeId
            officeName = domainModel.officeName
            active = domainModel.isActive
            status = ClientStatusMapper.mapToEntity(domainModel.status!!)
        }
    }
}