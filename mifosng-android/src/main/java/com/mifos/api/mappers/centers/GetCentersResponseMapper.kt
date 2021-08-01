package com.mifos.api.mappers.centers

import com.mifos.objects.client.Page
import com.mifos.objects.group.Center
import org.apache.fineract.client.models.GetCentersResponse
import org.mifos.core.data.AbstractMapper

object GetCentersResponseMapper: AbstractMapper<GetCentersResponse, Page<Center>>() {
    override fun mapFromEntity(entity: GetCentersResponse): Page<Center> {
        return Page<Center>().apply{
            totalFilteredRecords = entity.totalFilteredRecords!!
            pageItems = entity.pageItems?.let { CentersPageItemsMapper.mapFromEntityList(it) } ?:
            listOf()
        }
    }

    override fun mapToEntity(domainModel: Page<Center>): GetCentersResponse {
        return GetCentersResponse().apply{
            totalFilteredRecords = domainModel.totalFilteredRecords!!
            pageItems = CentersPageItemsMapper.mapToEntityList(domainModel.pageItems)
        }
    }
}