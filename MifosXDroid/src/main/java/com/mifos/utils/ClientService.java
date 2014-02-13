package com.mifos.utils;

import com.mifos.objects.Page;
import com.mifos.objects.PageItem;
import com.mifos.objects.SearchedEntity;

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


    /**
     *
     * @param callback - Callback to handle the response and/or error
     */
    @GET("/clients?tenantIdentifier=default")
    public void listAllClients(Callback<Page> callback);


    /**
     *
     * @param clientId - ID of the client
     * @param callback - Callback to handle the response and/or error
     */

    @GET("/clients/{clientId}?tenantIdentifier=default")
    public void getClient(@Path("clientId") int clientId, Callback<PageItem> callback);


}
