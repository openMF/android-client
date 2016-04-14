package com.mifos.api.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Tarun on 16/03/2016.
 */
public class GpsLocationSurvey {
    private double latitude;
    private double longitude;
    private int surveyId;
    private String dateFormat="dd MMMM yyyy";
    private String locale = "en";

    public GpsLocationSurvey(LatLng location, int surveyId){
        latitude = location.latitude;
        longitude = location.longitude;
        this.surveyId = surveyId;
    }
}
