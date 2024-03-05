/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.google.gson.JsonArray
import com.mifos.core.model.APIEndPoint
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.noncore.DataTable
import com.mifos.core.objects.user.UserLocation
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

/**
 * @author fomenkoo
 */
interface DataTableService {
    @GET(APIEndPoint.DATATABLES)
    fun getTableOf(@Query("apptable") table: String?): Observable<List<DataTable>>

    @GET(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
    fun getDataOfDataTable(
        @Path("dataTableName") dataTableName: String?,
        @Path("entityId") entityId: Int
    ): Observable<JsonArray>

    //TODO Improve Body Implementation with Payload
    @POST(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
    fun createEntryInDataTable(
        @Path("dataTableName") dataTableName: String?,
        @Path("entityId") entityId: Int,
        @Body requestPayload: Map<String, String>
    ): Observable<GenericResponse>

    @DELETE(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/{dataTableRowId}")
    fun deleteEntryOfDataTableManyToMany(
        @Path("dataTableName") dataTableName: String?,
        @Path("entityId") entityId: Int,
        @Path("dataTableRowId") dataTableRowId: Int
    ): Observable<GenericResponse>

    @POST(APIEndPoint.DATATABLES + "/user_tracking/{userId}")
    fun addUserPathTracking(
        @Path("userId") userId: Int,
        @Body userLocation: UserLocation?
    ): Observable<GenericResponse>

    @GET(APIEndPoint.DATATABLES + "/user_tracking/{userId}")
    fun getUserPathTracking(@Path("userId") userId: Int): Observable<List<UserLocation>>
}