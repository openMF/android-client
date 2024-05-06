package com.mifos.core.network.mappers.dataTable

import com.mifos.core.objects.noncore.ColumnHeader
import com.mifos.core.objects.noncore.DataTable
import org.apache.fineract.client.models.GetDataTablesResponse
import org.apache.fineract.client.models.ResultsetColumnHeaderData
import org.mifos.core.data.AbstractMapper

/**
 * Created by Aditya Gupta on 31/08/23.
 */

object GetDataTablesResponseMapper : AbstractMapper<GetDataTablesResponse, DataTable>() {

    override fun mapFromEntity(entity: GetDataTablesResponse): DataTable {
        return DataTable().apply {
            applicationTableName = entity.applicationTableName
            registeredTableName = entity.registeredTableName
            columnHeaderData = entity.columnHeaderData!!.map {
                ColumnHeader().apply {
                    columnCode = it.columnCode
                    columnType = it.columnType
                    columnDisplayType = it.columnDisplayType
                    columnLength = it.columnLength?.toInt()
                    columnNullable = it.isColumnNullable
                    columnPrimaryKey = it.isColumnPrimaryKey
                }
            }
        }
    }

    override fun mapToEntity(domainModel: DataTable): GetDataTablesResponse {
        return GetDataTablesResponse().apply {
            applicationTableName = domainModel.applicationTableName
            registeredTableName = domainModel.registeredTableName
            columnHeaderData = domainModel.columnHeaderData.map {
                ResultsetColumnHeaderData().apply {
                    columnCode = it.columnCode
                    columnType = it.columnType
                    columnDisplayType = it.columnDisplayType
                    columnLength = it.columnLength?.toLong()
                    isColumnNullable = it.columnNullable
                    isColumnPrimaryKey = it.columnPrimaryKey
                }
            }
        }
    }
}