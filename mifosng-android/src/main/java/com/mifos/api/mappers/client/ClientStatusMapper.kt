package com.mifos.api.mappers.client

import com.mifos.objects.client.Status
import org.apache.fineract.client.models.GetClientStatus
import org.mifos.core.data.AbstractMapper

object ClientStatusMapper: AbstractMapper<GetClientStatus, Status>() {

    override fun mapFromEntity(entity: GetClientStatus): Status {
        return Status().apply {
            id = entity.id!!
            code = entity.code
            value = entity.description
        }
    }

    override fun mapToEntity(domainModel: Status): GetClientStatus {
        return GetClientStatus().apply {
            id = domainModel.id!!
            code = domainModel.code
            description = domainModel.value
        }
    }

}