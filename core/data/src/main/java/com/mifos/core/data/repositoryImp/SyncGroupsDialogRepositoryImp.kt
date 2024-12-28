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

import com.mifos.core.data.repository.SyncGroupsDialogRepository
import com.mifos.core.dbobjects.accounts.ClientAccounts
import com.mifos.core.dbobjects.accounts.GroupAccounts
import com.mifos.core.dbobjects.accounts.loan.LoanWithAssociations
import com.mifos.core.dbobjects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.dbobjects.client.Client
import com.mifos.core.dbobjects.group.Group
import com.mifos.core.dbobjects.group.GroupWithAssociations
import com.mifos.core.dbobjects.templates.loans.LoanRepaymentTemplate
import com.mifos.core.dbobjects.templates.savings.SavingsAccountTransactionTemplate
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerSavings
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncGroupsDialogRepositoryImp @Inject constructor(
    private val dataManagerGroups: DataManagerGroups,
    private val dataManagerLoan: DataManagerLoan,
    private val dataManagerSavings: DataManagerSavings,
    private val dataManagerClient: DataManagerClient,
) : SyncGroupsDialogRepository {

    override fun syncGroupAccounts(groupId: Int): Observable<GroupAccounts> {
        return dataManagerGroups.syncGroupAccounts(groupId)
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

    override fun getGroupWithAssociations(groupId: Int): Observable<GroupWithAssociations> {
        return dataManagerGroups.getGroupWithAssociations(groupId)
    }

    override fun syncClientInDatabase(client: Client): Observable<Client> {
        return dataManagerClient.syncClientInDatabase(client)
    }

    override suspend fun syncClientAccounts(clientId: Int): ClientAccounts {
        return dataManagerClient.getClientAccounts(clientId)
    }

    override fun syncGroupInDatabase(group: Group): Observable<Group> {
        return dataManagerGroups.syncGroupInDatabase(group)
    }
}
