/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.SyncClientsDialogRepository
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncClientsDialogRepositoryImp @Inject constructor(
    private val dataManagerClient: DataManagerClient,
    private val dataManagerLoan: DataManagerLoan,
    private val dataManagerSavings: DataManagerSavings,
) : SyncClientsDialogRepository {

    override fun syncClientAccounts(clientId: Int): Observable<ClientAccounts> {
        return dataManagerClient.getClientAccounts(clientId)
    }

    override fun syncLoanById(loanId: Int): Observable<LoanWithAssociations> {
        return dataManagerLoan.syncLoanById(loanId)
    }

    override fun syncLoanRepaymentTemplate(loanId: Int): Observable<LoanRepaymentTemplate> {
        return dataManagerLoan.syncLoanRepaymentTemplate(loanId)
    }

    override fun syncSavingsAccount(
        type: String?,
        savingsAccountId: Int,
        association: String?,
    ): Observable<SavingsAccountWithAssociations> {
        return dataManagerSavings.syncSavingsAccount(type, savingsAccountId, association)
    }

    override fun syncSavingsAccountTransactionTemplate(
        savingsAccountType: String?,
        savingsAccountId: Int,
        transactionType: String?,
    ): Observable<SavingsAccountTransactionTemplate> {
        return dataManagerSavings.getSavingsAccountTransactionTemplate(
            savingsAccountType,
            savingsAccountId,
            transactionType,
        )
    }

    override fun syncClientInDatabase(client: Client): Observable<Client> {
        return dataManagerClient.syncClientInDatabase(client)
    }
}
