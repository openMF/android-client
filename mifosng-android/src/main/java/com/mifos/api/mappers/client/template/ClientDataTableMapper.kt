package com.mifos.api.mappers.client.template

import com.mifos.objects.noncore.DataTable
import org.apache.fineract.client.models.GetClientsDataTables
import org.mifos.core.data.AbstractMapper

object ClientDataTableMapper: AbstractMapper<GetClientsDataTables, DataTable>() {
    override fun mapFromEntity(entity: GetClientsDataTables): DataTable {
        return DataTable().apply {
            applicationTableName = entity.applicationTableName
            registeredTableName = entity.registeredTableName
            columnHeaderData = ClientColumnHeaderDataMapper
                .mapFromEntityList(entity.columnHeaderData!!)
        }
    }

    override fun mapToEntity(domainModel: DataTable): GetClientsDataTables {
        return GetClientsDataTables().apply {
            applicationTableName = domainModel.applicationTableName
            registeredTableName = domainModel.registeredTableName
            columnHeaderData = ClientColumnHeaderDataMapper
                .mapToEntityList(domainModel.columnHeaderData)
        }
    }
}