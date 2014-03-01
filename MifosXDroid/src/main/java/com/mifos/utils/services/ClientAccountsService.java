package com.mifos.utils.services;

import com.mifos.objects.accounts.ClientAccounts;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

/**
 * Created by ishankhanna on 01/03/14.
 */
public interface ClientAccountsService {

    @Headers("X-Mifos-Platform-TenantId: default")
    @GET("/clients/{clientId}/accounts")
    public void getAllAccountsOfClient(@Path("clientId")int clientId, Callback<ClientAccounts> callback);

}
