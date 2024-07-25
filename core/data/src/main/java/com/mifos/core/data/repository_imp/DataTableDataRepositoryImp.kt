package com.mifos.core.data.repository_imp

import com.google.gson.JsonArray
import com.mifos.core.data.repository.DataTableDataRepository
import com.mifos.core.network.datamanager.DataManagerDataTable
import org.apache.fineract.client.models.DeleteDataTablesDatatableAppTableIdDatatableIdResponse
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class DataTableDataRepositoryImp @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) :
    DataTableDataRepository {

    override suspend fun getDataTableInfo(table: String, entityId: Int): JsonArray {
        return dataManagerDataTable.getDataTableInfo(table, entityId)
    }

    override fun deleteDataTableEntry(
        table: String?,
        entity: Int,
        rowId: Int
    ): Observable<DeleteDataTablesDatatableAppTableIdDatatableIdResponse> {
        return dataManagerDataTable.deleteDataTableEntry(table, entity, rowId)
    }

}