/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.api.model.GpsCoordinatesRequest;
import com.mifos.api.model.GpsCoordinatesResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * @author fomenkoo
 */
public interface GpsCoordinatesService {
    @POST(APIEndPoint.DATATABLES + "/gps_coordinates/{clientId}")
    void setGpsCoordinates(@Path("clientId") int clientId,
                           @Body GpsCoordinatesRequest gpsCoordinatesRequest,
                           Callback<GpsCoordinatesResponse> gpsCoordinatesResponseCallback);

    @PUT(APIEndPoint.DATATABLES + "/gps_coordinates/{clientId}")
    void updateGpsCoordinates(@Path("clientId") int clientId,
                              @Body GpsCoordinatesRequest gpsCoordinatesRequest,
                              Callback<GpsCoordinatesResponse> gpsCoordinatesResponseCallback);

}
