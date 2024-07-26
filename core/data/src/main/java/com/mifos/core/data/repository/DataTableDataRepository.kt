package com.mifos.core.data.repository

import com.google.gson.JsonArray
import org.apache.fineract.client.models.DeleteDataTablesDatatableAppTableIdDatatableIdResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface DataTableDataRepository {

    suspend fun getDataTableInfo(table: String, entityId: Int): JsonArray

    fun deleteDataTableEntry(
        table: String?,
        entity: Int,
        rowId: Int
    ): Observable<DeleteDataTablesDatatableAppTableIdDatatableIdResponse>

}