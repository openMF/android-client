package com.mifos.repositories

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerDataTable
import rx.Observable
import javax.inject.Inject

class DataTableRowDialogRepositoryImp @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) :
    DataTableRowDialogRepository {

    override fun addDataTableEntry(
        table: String?,
        entityId: Int,
        payload: Map<String, String>
    ): Observable<GenericResponse> {
        return dataManagerDataTable.addDataTableEntry(table, entityId, payload)
    }


}