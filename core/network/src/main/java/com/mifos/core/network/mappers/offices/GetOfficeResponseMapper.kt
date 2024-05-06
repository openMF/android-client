package com.mifos.core.network.mappers.offices

import com.mifos.core.objects.organisation.Office
import org.apache.fineract.client.models.GetOfficesResponse
import org.mifos.core.data.AbstractMapper
import java.util.Date

object GetOfficeResponseMapper : AbstractMapper<GetOfficesResponse, Office>() {

    override fun mapFromEntity(entity: GetOfficesResponse): Office {
        return Office().apply {
            id = entity.id?.toInt()
            name = entity.name
            nameDecorated = entity.nameDecorated
            openingDate = listOf(
                entity.openingDate?.year,
                entity.openingDate?.month,
                entity.openingDate?.year
            )
            externalId = entity.externalId
        }
    }

    override fun mapToEntity(domainModel: Office): GetOfficesResponse {
        return GetOfficesResponse().apply {
            id = domainModel.id?.toLong()
            name = domainModel.name
            nameDecorated = domainModel.nameDecorated
            openingDate = Date().apply {
                year = domainModel.openingDate[0]!!
                month = domainModel.openingDate[1]!!
                date = domainModel.openingDate[2]!!
            }
            externalId = domainModel.externalId
        }
    }
}