package com.mifos.api.model;

/**
 * Created by Tarun on 05/04/2016.
 */
public class ClientAddressUpdateRequest {
    private String city;
    private String state;
    private double latitude;
    private double longitude;
    private String dateFormat = "dd MMMM yyyy";
    private String locale = "en";

    public ClientAddressUpdateRequest(String city, String state, double lat, double longi) {
        this.city = city;
        this.state = state;
        latitude = lat;
        longitude = longi;
    }
}
