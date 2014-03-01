package com.mifos.utils.services;

import com.mifos.objects.client.Page;
import com.mifos.objects.client.PageItem;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

/**
 * Created by ishankhanna on 09/02/14.
 */
public interface ClientService {


    /**
     *
     * @param callback - Callback to handle the response and/or error
     */
    @Headers("X-Mifos-Platform-TenantId: default")
    @GET("/clients")
    public void listAllClients(Callback<Page> callback);


    /**
     *
     * @param clientId - ID of the client
     * @param callback - Callback to handle the response and/or error
     */
    @Headers("X-Mifos-Platform-TenantId: default")
    @GET("/clients/{clientId}")
    public void getClient(@Path("clientId") int clientId, Callback<PageItem> callback);


}
