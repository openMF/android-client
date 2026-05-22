/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.datatable.api

import com.mifos.core.model.GenericResponse
import com.mifos.core.network.APIEndPoint
import com.mifos.room.datatable.entity.DataTableEntity
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.serialization.json.JsonArray

/**
 * Fineract `datatable` domain endpoints — generic table CRUD over
 * `/datatables/{dataTableName}/{entityId}`. Datatable sub-resources with a hardcoded
 * parent (e.g. `m_staff_path_tracking`) live under their dedicated domain Api.
 */
interface DataTableApi {

    @GET(APIEndPoint.DATATABLES)
    suspend fun getTableOf(@Query("apptable") table: String?): List<DataTableEntity>

    @GET(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
    suspend fun getDataOfDataTable(
        @Path("dataTableName") dataTableName: String,
        @Path("entityId") entityId: Int,
    ): JsonArray

    @POST(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
    suspend fun createEntryInDataTable(
        @Path("dataTableName") dataTableName: String,
        @Path("entityId") entityId: Int,
        @Body requestPayload: Map<String, String>,
    ): GenericResponse

    @DELETE(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/{dataTableRowId}")
    suspend fun deleteEntryOfDataTableManyToMany(
        @Path("dataTableName") dataTableName: String,
        @Path("entityId") entityId: Int,
        @Path("dataTableRowId") dataTableRowId: Int,
    ): GenericResponse
}
