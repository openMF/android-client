package com.mifos.utils.services;

import com.mifos.objects.SearchedEntity;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

/**
 * Created by ishankhanna on 14/02/14.
 */
public interface SearchService {

    @Headers("X-Mifos-Platform-TenantId: default")
    @GET("/search?resource=clients")
    public void searchClientsByName(@Query("query") String clientName, Callback<List<SearchedEntity>> callback);



}
