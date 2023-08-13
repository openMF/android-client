package com.mifos.repositories

import com.mifos.api.GenericResponse
import rx.Observable

interface DataTableRowDialogRepository {

    fun addDataTableEntry(
        table: String?, entityId: Int, payload: Map<String, String>
    ): Observable<GenericResponse>

}