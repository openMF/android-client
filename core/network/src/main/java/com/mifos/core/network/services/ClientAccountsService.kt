/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.APIEndPoint
import com.mifos.core.objects.accounts.ClientAccounts
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable

/**
 * @author fomenkoo
 */
interface ClientAccountsService {
    @GET(APIEndPoint.CLIENTS + "/{clientId}/accounts")
    fun getAllAccountsOfClient(@Path("clientId") clientId: Int): Observable<ClientAccounts>
}