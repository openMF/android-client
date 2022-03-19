package com.mifos.api.mappers

import com.mifos.objects.db.Status
import org.apache.fineract.client.models.GetCentersCenterIdStatus
import org.mifos.core.data.AbstractMapper

object SavingsStatusMapper: AbstractMapper<GetCentersCenterIdStatus, Status>() {

    override fun mapFromEntity(entity: GetCentersCenterIdStatus): Status {
        TODO("Not yet implemented")
    }

    override fun mapToEntity(domainModel: Status): GetCentersCenterIdStatus {
        TODO("Not yet implemented")
    }
}