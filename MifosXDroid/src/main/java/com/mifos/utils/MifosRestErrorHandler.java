package com.mifos.utils;

import android.util.Log;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class MifosRestErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError retrofitError) {

        Response r = retrofitError.getResponse();
        if (r != null && r.getStatus() == 401) {
            Log.e("Status", "401");
        }
        return null;
    }
}
