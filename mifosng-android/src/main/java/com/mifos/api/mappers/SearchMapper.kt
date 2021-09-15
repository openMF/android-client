package com.mifos.api.mappers

import com.mifos.objects.SearchedEntity
import org.apache.fineract.client.models.GetSearchResponse
import org.mifos.core.data.AbstractMapper

object SearchMapper: AbstractMapper<GetSearchResponse, SearchedEntity>() {

    override fun mapFromEntity(entity: GetSearchResponse): SearchedEntity {
        return SearchedEntity().apply {
            entityId = entity.entityId?.toInt() ?: 0
            entityAccountNo = entity.entityAccountNo.toString()
            entityName = entity.entityName
            entityType = entity.entityType
            parentId = entity.parentId?.toInt() ?: 0
            parentName = entity.parentName
        }
    }

    override fun mapToEntity(domainModel: SearchedEntity): GetSearchResponse {
        return GetSearchResponse().apply {
            entityId = domainModel.entityId.toLong()
            entityAccountNo = domainModel.entityAccountNo.toLong()
            entityName = domainModel.entityName
            entityType = domainModel.entityType
            parentId = domainModel.parentId.toLong()
            parentName = domainModel.parentName
        }
    }
}