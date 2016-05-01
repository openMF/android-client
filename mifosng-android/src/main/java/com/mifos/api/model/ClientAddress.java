package com.mifos.api.model;

/**
 * Created by Tarun on 04/04/2016.
 */
public class ClientAddress {
    private  int id;
    private String city;
    private String state;
    private double latitude;
    private double longitude;

    public int getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public double getLat() {
        return latitude;
    }

    public double getLong() {
        return longitude;
    }
}
