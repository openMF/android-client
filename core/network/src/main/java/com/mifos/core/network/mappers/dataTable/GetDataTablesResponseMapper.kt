/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.mappers.dataTable

import com.mifos.core.entity.noncore.ColumnHeader
import com.mifos.core.entity.noncore.DataTable
import org.mifos.core.data.AbstractMapper
import org.openapitools.client.models.GetDataTablesResponse
import org.openapitools.client.models.ResultsetColumnHeaderData

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
                    columnType = it.columnType?.name
                    columnDisplayType = it.columnDisplayType?.name
                    columnLength = it.columnLength?.toInt()
                    columnNullable = it.isColumnNullable
                    columnPrimaryKey = it.isColumnPrimaryKey
                }
            }
        }
    }

    override fun mapToEntity(domainModel: DataTable): GetDataTablesResponse {
        return GetDataTablesResponse(
            applicationTableName = domainModel.applicationTableName,
            registeredTableName = domainModel.registeredTableName,
            columnHeaderData = domainModel.columnHeaderData.map {
                ResultsetColumnHeaderData(
                    columnCode = it.columnCode,
                    columnType = ResultsetColumnHeaderData.ColumnType.valueOf(it.columnType!!),
                    columnDisplayType = ResultsetColumnHeaderData.ColumnDisplayType.valueOf(it.columnDisplayType!!),
                    columnLength = it.columnLength?.toLong(),
                    isColumnNullable = it.columnNullable,
                    isColumnPrimaryKey = it.columnPrimaryKey,
                )
            },
        )
    }
}
