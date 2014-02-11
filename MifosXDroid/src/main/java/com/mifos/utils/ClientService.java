package com.mifos.utils;

import com.mifos.objects.Page;
import com.mifos.objects.PageItem;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by ishankhanna on 09/02/14.
 */
public interface ClientService {

    @GET("/clients?tenantIdentifier=default")
    public Page listAllClients();

    @GET("/clients/{clientId}?tenantIdentifier=default")
    public void getClient(@Path("clientId") int clientId, Callback<PageItem> callback);

}
