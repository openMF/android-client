package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.api.model.GpsLocationSurvey;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Tarun on 16/03/2016.
 */
public interface GpsSurveyService {
    @POST(APIEndPoint.DATATABLES + "/SurveyLocation/{clientId}")
    void setSurveyLocation(@Path("clientId") int clientId, @Body GpsLocationSurvey gpsLocationSurvey,
                           Callback<GpsLocationSurvey> gpsLocationSurveyCallback);

}
