package com.mifos.services.data;

/**
 * Represents the GPS coordinates of a client location.
 * TODO: this needs to represent the response.
 */
public class GpsCoordinatesResponse
{
    private int clientId;
    private double latitude;
    private double longitude;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
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
