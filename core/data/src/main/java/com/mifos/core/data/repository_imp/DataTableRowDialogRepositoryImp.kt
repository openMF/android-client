package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.DataTableRowDialogRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerDataTable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
class DataTableRowDialogRepositoryImp @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) :
    DataTableRowDialogRepository {

    override suspend fun addDataTableEntry(
        table: String,
        entityId: Int,
        payload: Map<String, String>
    ): GenericResponse {
        return dataManagerDataTable.addDataTableEntry(table, entityId, payload)
    }
}