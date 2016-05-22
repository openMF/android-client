/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.google.gson.JsonArray;
import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.noncore.DataTable;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * @author fomenkoo
 */
public interface DataTableService {

    @GET(APIEndPoint.DATATABLES)
    void getTableOf(@Query("apptable") String table, Callback<List<DataTable>> callback);


    @GET(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
    void getDataOfDataTable(@Path("dataTableName") String dataTableName, @Path("entityId") int entityId, Callback<JsonArray> jsonArrayCallback);

    //TODO Improve Body Implementation with Payload
    @POST(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
    void createEntryInDataTable(@Path("dataTableName") String dataTableName, @Path("entityId") int entityId, @Body Map<String, Object> requestPayload,
                                Callback<GenericResponse> callback);

    @DELETE(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/{dataTableRowId}")
    void deleteEntryOfDataTableManyToMany(@Path("dataTableName") String dataTableName, @Path("entityId") int entityId,
                                          @Path("dataTableRowId") int dataTableRowId, Callback<GenericResponse> callback);
}
