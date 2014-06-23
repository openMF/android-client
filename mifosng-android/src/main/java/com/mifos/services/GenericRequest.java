package com.mifos.services;

import java.util.HashMap;

/**
 * Created by ishankhanna on 24/06/14.
 */
public class GenericRequest {

    HashMap<String,Object> requestFields = new HashMap<String, Object>();

    public HashMap<String, Object> getRequestFields() {
        return requestFields;
    }

    public void setRequestFields(HashMap<String, Object> requestFields) {
        this.requestFields = requestFields;
    }
}
