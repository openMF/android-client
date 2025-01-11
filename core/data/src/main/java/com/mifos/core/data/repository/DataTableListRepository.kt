/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.entity.accounts.loan.Loans
import com.mifos.core.entity.client.Client
import com.mifos.core.entity.client.ClientPayload
import com.mifos.core.network.model.LoansPayload
import com.mifos.core.payloads.GroupLoanPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface DataTableListRepository {

    fun createLoansAccount(loansPayload: LoansPayload?): Observable<Loans>

    fun createGroupLoansAccount(loansPayload: GroupLoanPayload?): Observable<Loans>

    fun createClient(clientPayload: ClientPayload): Observable<Client>
}
