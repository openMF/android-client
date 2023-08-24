package com.mifos.mappers.centers

import com.mifos.objects.client.Page
import com.mifos.objects.group.Center
import org.apache.fineract.client.models.GetCentersResponse
import org.mifos.core.data.AbstractMapper

object GetCentersResponseMapper : AbstractMapper<GetCentersResponse, Page<Center>>() {

    override fun mapFromEntity(entity: GetCentersResponse): Page<Center> {
        return Page<Center>().apply {
            totalFilteredRecords = entity.totalFilteredRecords!!
            pageItems = CenterMapper.mapFromEntityList(entity.pageItems!!)
        }
    }

    override fun mapToEntity(domainModel: Page<Center>): GetCentersResponse {
        return GetCentersResponse().apply {
            totalFilteredRecords = domainModel.totalFilteredRecords
            pageItems = CenterMapper.mapToEntityList(domainModel.pageItems)
        }
    }
}