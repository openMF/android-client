package com.mifos.services.data;

/**
 * Represents the GPS coordinates request to set the client location.
 */
public class GpsCoordinatesRequest
{
    private int clientId;
    private double latitude;
    private double longitude;
    // Defaults
    private String dateFormat="dd MMMM YYYY";
    private String locale="en";

    public GpsCoordinatesRequest(int clientId, double latitude, double longitude) {
        this.clientId = clientId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

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
