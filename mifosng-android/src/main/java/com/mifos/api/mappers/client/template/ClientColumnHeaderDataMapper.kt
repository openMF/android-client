package com.mifos.api.mappers.client.template

import com.mifos.objects.noncore.ColumnHeader
import com.mifos.objects.noncore.ColumnValue
import org.apache.fineract.client.models.GetClientsColumnHeaderData
import org.mifos.core.data.AbstractMapper

object ClientColumnHeaderDataMapper: AbstractMapper<GetClientsColumnHeaderData, ColumnHeader>() {
    override fun mapFromEntity(entity: GetClientsColumnHeaderData): ColumnHeader {
        return ColumnHeader().apply {
            columnName = entity.columnName
            columnType = entity.columnType
            columnLength = entity.columnLength
            columnDisplayType = entity.columnDisplayType
            columnNullable = entity.isColumnNullable
            columnPrimaryKey = entity.isColumnPrimaryKey
            columnValues = entity.columnValues?.map { cv -> ColumnValue().apply {
                value = cv
            } }
        }
    }

    override fun mapToEntity(domainModel: ColumnHeader): GetClientsColumnHeaderData {
        TODO("Not yet implemented")
    }
}