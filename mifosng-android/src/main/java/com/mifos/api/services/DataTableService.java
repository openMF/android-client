/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.google.gson.JsonArray;
import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.noncore.DataTable;
import com.mifos.objects.user.UserLocation;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface DataTableService {

    @GET(APIEndPoint.DATATABLES)
    Observable<List<DataTable>> getTableOf(@Query("apptable") String table);


    @GET(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
    Observable<JsonArray> getDataOfDataTable(@Path("dataTableName") String dataTableName,
                                             @Path("entityId") int entityId);

    //TODO Improve Body Implementation with Payload
    @POST(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
    Observable<GenericResponse> createEntryInDataTable(
            @Path("dataTableName") String dataTableName,
            @Path("entityId") int entityId,
            @Body Map<String, Object> requestPayload);

    @DELETE(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/{dataTableRowId}")
    Observable<GenericResponse> deleteEntryOfDataTableManyToMany(
            @Path("dataTableName") String dataTableName,
            @Path("entityId") int entityId,
            @Path("dataTableRowId") int dataTableRowId);

    @POST(APIEndPoint.DATATABLES + "/user_tracking/{userId}")
    Observable<GenericResponse> addUserPathTracking(
            @Path("userId") int userId,
            @Body UserLocation userLocation);

    @GET(APIEndPoint.DATATABLES + "/user_tracking/{userId}")
    Observable<List<UserLocation>> getUserPathTracking(@Path("userId") int userId);
}

