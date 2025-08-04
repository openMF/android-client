/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.apis

import com.mifos.core.network.model.DeleteDataTablesDatatableAppTableIdDatatableIdResponse
import com.mifos.core.network.model.GetDataTablesResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow

interface DataTablesApi {

    /**
     * List Data Tables
     * Lists registered data tables and the Apache Fineract Core application table they are registered to.  ARGUMENTS  apptable  - optional The Apache Fineract core application table.  Example Requests:  datatables?apptable&#x3D;m_client   datatables
     * Responses:
     *  - 200: OK
     *
     * @param apptable apptable (optional)
     * @return [kotlin.collections.List<GetDataTablesResponse>]
     */
    @GET("datatables")
    fun getDatatables(@Query("apptable") apptable: String? = null): Flow<List<GetDataTablesResponse>>

    /**
     * Delete Entry in Datatable (One to Many)
     * Deletes the entry (if it exists) for data tables that are one to many with the application table.
     * Responses:
     *  - 200: OK
     *
     * @param datatable datatable
     * @param apptableId apptableId
     * @param datatableId datatableId
     * @return [DeleteDataTablesDatatableAppTableIdDatatableIdResponse]
     */
    @DELETE("datatables/{datatable}/{apptableId}/{datatableId}")
    suspend fun deleteDatatableEntry(
        @Path("datatable") datatable: String,
        @Path("apptableId") apptableId: Long,
        @Path("datatableId") datatableId: Long,
    ): DeleteDataTablesDatatableAppTableIdDatatableIdResponse
}
