package com.mifos.repositories

import com.google.gson.JsonArray
import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerDataTable
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class DataTableDataRepositoryImp @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) :
    DataTableDataRepository {

    override fun getDataTableInfo(table: String?, entityId: Int): Observable<JsonArray> {
        return dataManagerDataTable.getDataTableInfo(table, entityId)
    }

    override fun deleteDataTableEntry(
        table: String?,
        entity: Int,
        rowId: Int
    ): Observable<GenericResponse> {
        return dataManagerDataTable.deleteDataTableEntry(table, entity, rowId)
    }


}