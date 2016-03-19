/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.google.gson.JsonArray;
import com.mifos.objects.noncore.DataTable;
import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface DataTableService {

    @GET(APIEndPoint.DATATABLES)
    Observable<List<DataTable>> getTableOf(@Query("apptable") String table);


    @GET(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
    Observable<JsonArray> getDataOfDataTable(@Path("dataTableName") String dataTableName, @Path("entityId") int entityId);

    //TODO Improve Body Implementation with Payload
    @POST(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
    Observable<GenericResponse> createEntryInDataTable(@Path("dataTableName") String dataTableName, @Path("entityId") int entityId, @Body Map<String, Object> requestPayload);

    @DELETE(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/{dataTableRowId}")
    Observable<GenericResponse> deleteEntryOfDataTableManyToMany(@Path("dataTableName") String dataTableName, @Path("entityId") int entityId,
                                          @Path("dataTableRowId") int dataTableRowId);
}
