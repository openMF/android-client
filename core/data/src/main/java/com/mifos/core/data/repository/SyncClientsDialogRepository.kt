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

import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface SyncClientsDialogRepository {

    suspend fun syncClientAccounts(clientId: Int): ClientAccounts

    fun syncLoanById(loanId: Int): Observable<LoanWithAssociations>

    fun syncLoanRepaymentTemplate(loanId: Int): Observable<LoanRepaymentTemplate>

    fun syncSavingsAccount(
        type: String?,
        savingsAccountId: Int,
        association: String?,
    ): Observable<SavingsAccountWithAssociations>

    fun syncSavingsAccountTransactionTemplate(
        savingsAccountType: String?,
        savingsAccountId: Int,
        transactionType: String?,
    ): Observable<SavingsAccountTransactionTemplate>

    fun syncClientInDatabase(client: Client): Observable<Client>
}
