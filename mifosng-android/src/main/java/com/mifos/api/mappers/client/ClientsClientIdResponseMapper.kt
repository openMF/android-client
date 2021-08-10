package com.mifos.api.mappers.client

import com.mifos.api.utils.toArray
import com.mifos.objects.client.Client
import com.mifos.objects.client.Status
import org.apache.fineract.client.models.GetClientsClientIdResponse
import org.apache.fineract.client.models.GetClientsClientIdStatus
import org.mifos.core.data.AbstractMapper
import java.util.*

object ClientsClientIdResponseMapper: AbstractMapper<GetClientsClientIdResponse, Client>() {

    override fun mapFromEntity(entity: GetClientsClientIdResponse): Client {
        return Client().apply {
            id = entity.id!!
            accountNo = entity.accountNo.toString()
            fullname = "${entity.firstname} ${entity.lastname}"
            displayName = entity.displayName
            officeId = entity.officeId!!
            officeName = entity.officeName
            isActive = entity.active!!
            status = Status().apply {
                id = entity.status!!.id!!
                code = entity.status!!.code
                value = entity.status!!.description
            }
            activationDate = entity.activationDate?.let { it.toArray() } ?: listOf()
        }
    }

    override fun mapToEntity(domainModel: Client): GetClientsClientIdResponse {
        return GetClientsClientIdResponse().apply {
            id = domainModel.id
            accountNo = domainModel.accountNo
            firstname = domainModel.fullname.split(" ")[0]
            lastname = domainModel.fullname.split(" ")[1]
            displayName = domainModel.displayName
            officeId = domainModel.officeId
            officeName = domainModel.officeName
            active = domainModel.isActive
            status = GetClientsClientIdStatus().apply {
                id = domainModel.status.id
                code = domainModel.status.code
                description = domainModel.status.value
            }
            activationDate = Date(domainModel.activationDate[0], domainModel.activationDate[1], domainModel.activationDate[2])
        }
    }

}