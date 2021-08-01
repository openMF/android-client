package com.mifos.api.mappers.centers

import com.mifos.api.mappers.client.ClientStatusMapper
import com.mifos.objects.client.Status
import com.mifos.objects.group.Center
import org.apache.fineract.client.models.GetCentersPageItems
import org.apache.fineract.client.models.GetCentersStatus
import org.mifos.core.data.AbstractMapper

object CentersPageItemsMapper: AbstractMapper<GetCentersPageItems, Center>() {
    override fun mapFromEntity(entity: GetCentersPageItems): Center {
        return Center().apply {
            id = entity.id
            active = entity.active
            name = entity.name
            officeId = entity.officeId
            officeName = entity.officeName
            hierarchy = entity.hierarchy
            status = Status().apply {
                id = entity.status!!.id!!
                code = entity.status!!.code
                value = entity.status!!.description
            }
        }
    }

    override fun mapToEntity(domainModel: Center): GetCentersPageItems {
        return GetCentersPageItems().apply {
            id = domainModel.id
            active = domainModel.active
            name = domainModel.name
            officeId = domainModel.officeId
            officeName = domainModel.officeName
            hierarchy = domainModel.hierarchy
            status = GetCentersStatus().apply {
                id = domainModel.status.id
                code = domainModel.status.code
                description = domainModel.status.value
            }
        }
    }
}