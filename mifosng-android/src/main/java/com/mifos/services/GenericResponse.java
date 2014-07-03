package com.mifos.services;

import java.util.HashMap;

/**
 * Created by ishankhanna on 24/06/14.
 */
public class GenericResponse {

    HashMap<String,Object> responseFields = new HashMap<String, Object>();

    public HashMap<String, Object> getResponseFields() {
        return responseFields;
    }

    public void setResponseFields(HashMap<String, Object> responseFields) {
        this.responseFields = responseFields;
    }

    @Override
    public String toString() {
        return "GenericResponse{" +
                "responseFields=" + responseFields +
                '}';
    }
}
