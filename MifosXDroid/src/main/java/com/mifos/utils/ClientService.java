package com.mifos.utils;

import com.mifos.objects.Page;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Query;

/**
 * Created by ishankhanna on 09/02/14.
 */
public interface ClientService {

    @GET("/clients?tenantIdentifier=default")
    public Page listAllClients();


}
