package com.mifos.core.data.repository

import com.google.gson.JsonArray
import org.openapitools.client.models.DeleteDataTablesDatatableAppTableIdDatatableIdResponse

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface DataTableDataRepository {

    suspend fun getDataTableInfo(table: String, entityId: Int): JsonArray

    suspend fun deleteDataTableEntry(
        table: String,
        entity: Int,
        rowId: Int
    ): DeleteDataTablesDatatableAppTableIdDatatableIdResponse

}