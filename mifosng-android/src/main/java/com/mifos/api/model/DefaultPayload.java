package com.mifos.api.model;

/**
 * Created by ADMIN on 16-Jun-15.
 */
public class DefaultPayload {

    private String dateFormat = "dd MMMM YYYY";
    private String locale = "en";


    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
