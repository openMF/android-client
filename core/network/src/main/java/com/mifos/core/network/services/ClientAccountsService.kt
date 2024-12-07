/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
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
