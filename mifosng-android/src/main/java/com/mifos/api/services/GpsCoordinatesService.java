/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.api.model.GpsCoordinatesRequest;
import com.mifos.api.model.GpsCoordinatesResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface GpsCoordinatesService {

    @POST(APIEndPoint.DATATABLES + "/gps_coordinates/{clientId}")
    Observable<GpsCoordinatesResponse> setGpsCoordinates(@Path("clientId") int clientId,
                                                         @Body GpsCoordinatesRequest
                                                                 gpsCoordinatesRequest);

    @PUT(APIEndPoint.DATATABLES + "/gps_coordinates/{clientId}")
    Observable<GpsCoordinatesResponse> updateGpsCoordinates(
            @Path("clientId") int clientId,
            @Body GpsCoordinatesRequest gpsCoordinatesRequest);

}
