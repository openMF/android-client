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

import com.mifos.core.data.LoansPayload
import com.mifos.core.dbobjects.accounts.loan.Loans
import com.mifos.core.dbobjects.templates.loans.LoanTemplate
import com.mifos.core.objects.organisations.LoanProducts
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface LoanAccountRepository {

    suspend fun allLoans(): Observable<List<LoanProducts>>

    suspend fun getLoansAccountTemplate(clientId: Int, productId: Int): Observable<LoanTemplate>

    suspend fun createLoansAccount(loansPayload: LoansPayload): Observable<Loans>
}
