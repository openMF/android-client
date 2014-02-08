package com.mifos.utils;

import android.util.Log;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 08/02/14.
 */

// Rest Adapter Wrapper
public class MifosRestAdapter {

    private static final String TEST_INSTANCE_URL = "https://demo.openmf.org/mifosng-provider/api/v1";

    private String INSTANCE_URL;
    protected RestAdapter restAdapter;

    public MifosRestAdapter(){
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(TEST_INSTANCE_URL)
                .setErrorHandler(new MyErrorHandler())
                .build();
    }

    public class MyErrorHandler implements ErrorHandler {
        @Override public Throwable handleError(RetrofitError cause) {
            Response r = cause.getResponse();
            if (r != null && r.getStatus() == 401){
                Log.e("Status","401");
            }
            Log.e("Status", cause.getMessage());
            return cause;
        }
    }



    public RestAdapter getRestAdapter() {
        return restAdapter;
    }

    public void setRestAdapter(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    public String getINSTANCE_URL() {
        return INSTANCE_URL;
    }

    public void setINSTANCE_URL(String INSTANCE_URL) {
        this.INSTANCE_URL = INSTANCE_URL;
    }
}
