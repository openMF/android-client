package com.mifos.api.mappers.office

import com.mifos.api.utils.toArray
import com.mifos.api.utils.toDate
import com.mifos.objects.organisation.Office
import org.apache.fineract.client.models.GetOfficesResponse
import org.mifos.core.data.AbstractMapper

object GetOfficeResponseMapper: AbstractMapper<GetOfficesResponse, Office>() {
    override fun mapFromEntity(entity: GetOfficesResponse): Office {
        return Office().apply {
            id = entity.id!!.toInt()
            name = entity.name
            nameDecorated = entity.nameDecorated
            externalId = entity.externalId
            openingDate = entity.openingDate?.toArray()
        }
    }

    override fun mapToEntity(domainModel: Office): GetOfficesResponse {
        return GetOfficesResponse().apply {
            id = domainModel.id.toLong()
            name = domainModel.name
            nameDecorated = domainModel.nameDecorated
            externalId = domainModel.externalId
            openingDate = domainModel.openingDate.toDate()
        }
    }
}