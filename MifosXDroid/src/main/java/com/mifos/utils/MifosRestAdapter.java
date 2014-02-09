package com.mifos.utils;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

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
                .setErrorHandler(new MifosRestErrorHandler())
                .build();
    }

    public MifosRestAdapter(final String authToken){

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(TEST_INSTANCE_URL)
                .setErrorHandler(new MifosRestErrorHandler())
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        requestFacade.addHeader("Authorization",authToken);
                    }
                })
                .build();

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
