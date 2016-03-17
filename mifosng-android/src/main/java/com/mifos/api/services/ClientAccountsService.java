/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.api.model.APIEndPoint;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface ClientAccountsService {

    @GET(APIEndPoint.CLIENTS + "/{clientId}/accounts")
    Observable<ClientAccounts> getAllAccountsOfClient(@Path("clientId") int clientId);
}
