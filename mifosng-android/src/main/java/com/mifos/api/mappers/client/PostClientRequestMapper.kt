package com.mifos.api.mappers.client

import com.mifos.objects.client.ClientPayload
import com.mifos.objects.noncore.DataTablePayload
import org.apache.fineract.client.models.PostClientsDatatable
import org.apache.fineract.client.models.PostClientsRequest
import org.mifos.core.data.AbstractMapper
import java.util.HashMap

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
            datatables = entity.datatables?.let { it.map { DataTablePayload().apply {
                registeredTableName = it.registeredTableName
                data = it.data as HashMap<String, Any>?
            } } }
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
            datatables = domainModel.datatables?.let { it.map { PostClientsDatatable().apply {
                registeredTableName = it.registeredTableName
                data = it.data as HashMap<String, Any>?
            } } }
        }
    }

}