package com.mifos.core.network.mappers.offices

import com.mifos.core.objects.organisation.Office
import org.mifos.core.data.AbstractMapper
import org.openapitools.client.models.GetOfficesResponse

object GetOfficeResponseMapper : AbstractMapper<GetOfficesResponse, Office>() {

    override fun mapFromEntity(entity: GetOfficesResponse): Office {
        return Office().apply {
            id = entity.id?.toInt()
            name = entity.name
            nameDecorated = entity.nameDecorated
            externalId = entity.externalId
        }
    }

    override fun mapToEntity(domainModel: Office): GetOfficesResponse {
        return GetOfficesResponse(
            id = domainModel.id?.toLong(),
            name = domainModel.name,
            nameDecorated = domainModel.nameDecorated,
            externalId = domainModel.externalId
        )
    }
}