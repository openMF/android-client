/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Represents the GPS coordinates request to set the client location.
 */
public class GpsCoordinatesRequest
{
    private double latitude;
    private double longitude;
    // Defaults
    private String dateFormat="dd MMMM YYYY";
    private String locale="en";

    public GpsCoordinatesRequest(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GpsCoordinatesRequest(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
