package com.mifos.api.mappers.datatable

import com.mifos.objects.noncore.ColumnHeader
import org.apache.fineract.client.models.ResultsetColumnHeaderData
import org.mifos.core.data.AbstractMapper

object ColumnHeaderDataMapper: AbstractMapper<ResultsetColumnHeaderData, ColumnHeader>() {
    override fun mapFromEntity(entity: ResultsetColumnHeaderData): ColumnHeader {
        return ColumnHeader().apply {
            columnName = entity.columnName
            columnType = entity.columnType
            columnLength = entity.columnLength!!.toInt()
            columnDisplayType = entity.columnDisplayType
            columnCode = entity.columnCode
            columnPrimaryKey = false
        }
    }

    override fun mapToEntity(domainModel: ColumnHeader): ResultsetColumnHeaderData {
        return ResultsetColumnHeaderData().apply {
            columnName = domainModel.columnName
            columnType = domainModel.columnType
            columnLength = domainModel.columnLength.toLong()
            columnDisplayType = domainModel.columnDisplayType
            columnCode = domainModel.columnCode
        }
    }
}