package com.mifos.utils;

import android.content.SharedPreferences;
import android.util.Log;

import com.mifos.objects.User;

import java.util.Iterator;
import java.util.List;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class MifosRestErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError retrofitError) {

        Response r = retrofitError.getResponse();
        if (r != null && r.getStatus() == 401) {
            Log.e("Status", "Authentication Error.");


        }else if(r.getStatus() == 400){
            Log.d("Status","Bad Request - Invalid Parameter or Data Integrity Issue.");
            Log.d("URL", r.getUrl());
            List<Header> headersList = r.getHeaders();
            Iterator<Header> iterator = headersList.iterator();
            while(iterator.hasNext())
            {    Header header = iterator.next();
                Log.d("Header ",header.toString());
            }
        }


        return retrofitError;
    }


}
