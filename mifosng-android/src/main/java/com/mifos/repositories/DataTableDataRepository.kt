package com.mifos.repositories

import com.google.gson.JsonArray
import com.mifos.api.GenericResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface DataTableDataRepository {

    fun getDataTableInfo(table: String?, entityId: Int): Observable<JsonArray>

    fun deleteDataTableEntry(table: String?, entity: Int, rowId: Int): Observable<GenericResponse>

}