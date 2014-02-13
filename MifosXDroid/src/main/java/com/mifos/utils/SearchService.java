package com.mifos.utils;

import com.mifos.objects.SearchedEntity;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ishankhanna on 14/02/14.
 */
public interface SearchService {

    @GET("/search?tenantIdentifier=default&resource=clients")
    public void searchClientsByName(@Query("query") String clientName, Callback<List<SearchedEntity>> callback);



}
