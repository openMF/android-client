package com.mifos.utils.services;

import com.mifos.objects.Center;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;

/**
 * Created by ishankhanna on 11/03/14.
 */
public interface CenterService {

    @Headers("X-Mifos-Platform-TenantId: default")
    @GET("/centers")
    public void getAllCenters(Callback<List<Center>> callback);


}
