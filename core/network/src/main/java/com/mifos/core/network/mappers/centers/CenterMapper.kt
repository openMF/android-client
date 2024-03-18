package com.mifos.core.network.mappers.centers

import com.mifos.core.objects.client.Status
import com.mifos.core.objects.group.Center
import org.apache.fineract.client.models.GetCentersPageItems
import org.apache.fineract.client.models.GetCentersStatus
import org.mifos.core.data.AbstractMapper

object CenterMapper : AbstractMapper<GetCentersPageItems, Center>() {

    override fun mapFromEntity(entity: GetCentersPageItems): Center {
        return Center().apply {
            id = entity.id
            active = entity.active
            name = entity.name
            officeName = entity.officeName
            officeId = entity.officeId
            hierarchy = entity.hierarchy
            status = Status().apply {
                id = entity.status?.id!!
                code = entity.status?.code
                value = entity.status?.description
            }
        }
    }

    override fun mapToEntity(domainModel: Center): GetCentersPageItems {
        return GetCentersPageItems().apply {
            id = domainModel.id
            active = domainModel.active
            name = domainModel.name
            officeName = domainModel.officeName
            officeId = domainModel.officeId
            hierarchy = domainModel.hierarchy
            status = GetCentersStatus().apply {
                id = domainModel.status?.id!!
                code = domainModel.status?.code
                description = domainModel.status?.value
            }
        }
    }
}