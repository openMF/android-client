package com.mifos.core.data.repository

import com.mifos.core.network.GenericResponse

/**
 * Created by Aditya Gupta on 13/08/23.
 */
interface DataTableRowDialogRepository {

    suspend fun addDataTableEntry(
        table: String, entityId: Int, payload: Map<String, String>
    ): GenericResponse

}