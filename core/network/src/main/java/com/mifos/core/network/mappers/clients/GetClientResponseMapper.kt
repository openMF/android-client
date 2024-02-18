package com.mifos.core.network.mappers.clients

import com.mifos.core.common.utils.Page
import com.mifos.core.data.model.client.Client
import org.apache.fineract.client.models.GetClientsResponse
import org.mifos.core.data.AbstractMapper

object GetClientResponseMapper : AbstractMapper<GetClientsResponse, Page<Client>>() {

    override fun mapFromEntity(entity: GetClientsResponse): Page<Client> {
        return Page<Client>().apply {
            totalFilteredRecords = entity.totalFilteredRecords!!
            pageItems = ClientMapper.mapFromEntityList(entity.pageItems!!)
        }
    }

    override fun mapToEntity(domainModel: Page<Client>): GetClientsResponse {
        return GetClientsResponse().apply {
            totalFilteredRecords = domainModel.totalFilteredRecords
            pageItems = ClientMapper.mapToEntityList(domainModel.pageItems)
        }
    }
}