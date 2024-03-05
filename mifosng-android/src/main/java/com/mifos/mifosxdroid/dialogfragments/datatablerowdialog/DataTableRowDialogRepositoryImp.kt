package com.mifos.mifosxdroid.dialogfragments.datatablerowdialog

import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerDataTable
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
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