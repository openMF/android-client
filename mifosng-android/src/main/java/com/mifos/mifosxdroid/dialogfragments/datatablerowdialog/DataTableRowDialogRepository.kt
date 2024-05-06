package com.mifos.mifosxdroid.dialogfragments.datatablerowdialog

import com.mifos.core.network.GenericResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 13/08/23.
 */
interface DataTableRowDialogRepository {

    fun addDataTableEntry(
        table: String?, entityId: Int, payload: Map<String, String>
    ): Observable<GenericResponse>

}