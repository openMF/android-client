package com.mifos.api.model;

/**
 * Created by Tarun on 14/04/2016.
 */
public class ClientAddressAddRequest {
    private String city;
    private String dateFormat = "dd MMMM yyyy";
    private String locale = "en";
    private String state;
    private double latitude;
    private double longitude;

    public ClientAddressAddRequest(String city, String state, double latitude, double longitude) {
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
