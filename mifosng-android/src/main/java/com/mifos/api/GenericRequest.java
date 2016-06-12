/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api;

import java.util.HashMap;

/**
 * Created by ishankhanna on 24/06/14.
 */
public class GenericRequest {

    HashMap<String, Object> requestFields = new HashMap<String, Object>();

    public HashMap<String, Object> getRequestFields() {
        return requestFields;
    }

    public void setRequestFields(HashMap<String, Object> requestFields) {
        this.requestFields = requestFields;
    }
}
