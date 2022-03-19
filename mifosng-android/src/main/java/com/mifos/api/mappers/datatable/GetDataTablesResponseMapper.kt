package com.mifos.api.mappers.datatable

import com.mifos.objects.noncore.DataTable
import org.apache.fineract.client.models.GetDataTablesResponse
import org.mifos.core.data.AbstractMapper

object GetDataTablesResponseMapper: AbstractMapper<GetDataTablesResponse, DataTable>() {
    override fun mapFromEntity(entity: GetDataTablesResponse): DataTable {
        return DataTable().apply {
            applicationTableName = entity.applicationTableName
            registeredTableName = entity.registeredTableName
            columnHeaderData = entity.columnHeaderData?.let { ColumnHeaderDataMapper.mapFromEntityList(it) }
                ?: listOf()
        }
    }

    override fun mapToEntity(domainModel: DataTable): GetDataTablesResponse {
        return GetDataTablesResponse().apply {
            applicationTableName = domainModel.applicationTableName
            registeredTableName = domainModel.registeredTableName
            columnHeaderData = domainModel.columnHeaderData?.let { ColumnHeaderDataMapper.mapToEntityList(it) }
                ?: listOf()
        }
    }
}