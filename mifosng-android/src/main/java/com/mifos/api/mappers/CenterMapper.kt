package com.mifos.api.mappers

import com.mifos.objects.group.Center
import org.apache.fineract.client.models.GetCentersPageItems
import org.mifos.core.data.AbstractMapper


object CenterMapper: AbstractMapper<GetCentersPageItems, Center>() {

    override fun mapFromEntity(entity: GetCentersPageItems): Center {
        return Center().apply {
            id = entity.id
            name = entity.name
            officeId = entity.officeId
            officeName = entity.officeName
            active = entity.active
        }
    }

    override fun mapToEntity(domainModel: Center): GetCentersPageItems {
        return GetCentersPageItems().apply {
            id = domainModel.id
            name = domainModel.name
            officeId = domainModel.officeId
            officeName = domainModel.officeName
            active = domainModel.active
        }
    }

}