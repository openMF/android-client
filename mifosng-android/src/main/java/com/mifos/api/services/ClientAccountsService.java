package com.mifos.api.services;

import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.api.model.APIEndPoint;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * @author fomenkoo
 */
public interface ClientAccountsService {

    @GET(APIEndPoint.CLIENTS + "/{clientId}/accounts")
    void getAllAccountsOfClient(@Path("clientId") int clientId, Callback<ClientAccounts> clientAccountsCallback);
}
