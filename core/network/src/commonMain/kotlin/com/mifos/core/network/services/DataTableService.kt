/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.objects.users.UserLocation
import com.mifos.core.network.GenericResponse
import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.noncore.DataTableEntity
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonArray

/**
 * @author fomenkoo
 */
interface DataTableService {
    @GET(APIEndPoint.DATATABLES)
    fun getTableOf(@Query("apptable") table: String?): Flow<List<DataTableEntity>>

    @GET(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
    suspend fun getDataOfDataTable(
        @Path("dataTableName") dataTableName: String,
        @Path("entityId") entityId: Int,
    ): JsonArray

    // TODO Improve Body Implementation with Payload
    @POST(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
    suspend fun createEntryInDataTable(
        @Path("dataTableName") dataTableName: String,
        @Path("entityId") entityId: Int,
        @Body requestPayload: Map<String, String>,
    ): GenericResponse

    @DELETE(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/{dataTableRowId}")
    fun deleteEntryOfDataTableManyToMany(
        @Path("dataTableName") dataTableName: String,
        @Path("entityId") entityId: Int,
        @Path("dataTableRowId") dataTableRowId: Int,
    ): Flow<GenericResponse>

    @POST(APIEndPoint.DATATABLES + "/m_staff_path_tracking/{userId}")
    fun addUserPathTracking(
        @Path("userId") userId: Int,
        @Body userLocation: UserLocation?,
    ): Flow<GenericResponse>

    @GET(APIEndPoint.DATATABLES + "/m_staff_path_tracking/{userId}")
    fun getUserPathTracking(@Path("userId") userId: Int): Flow<List<UserLocation>>
}
