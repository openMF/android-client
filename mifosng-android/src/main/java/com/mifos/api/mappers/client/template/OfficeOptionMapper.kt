package com.mifos.api.mappers.client.template

import com.mifos.objects.templates.clients.OfficeOptions
import org.apache.fineract.client.models.GetClientsOfficeOptions
import org.mifos.core.data.AbstractMapper

object OfficeOptionMapper: AbstractMapper<GetClientsOfficeOptions, OfficeOptions>() {
    override fun mapFromEntity(entity: GetClientsOfficeOptions): OfficeOptions {
        return OfficeOptions().apply {
            id = entity.id!!
            name = entity.name
            nameDecorated = entity.nameDecorated
        }
    }

    override fun mapToEntity(domainModel: OfficeOptions): GetClientsOfficeOptions {
        return GetClientsOfficeOptions().apply {
            id = domainModel.id
            name = domainModel.name
            nameDecorated = domainModel.nameDecorated
        }
    }
}