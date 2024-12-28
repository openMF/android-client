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

import com.mifos.core.dbobjects.accounts.ClientAccounts
import com.mifos.core.dbobjects.accounts.GroupAccounts
import com.mifos.core.dbobjects.accounts.loan.LoanWithAssociations
import com.mifos.core.dbobjects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.dbobjects.client.Client
import com.mifos.core.dbobjects.group.Group
import com.mifos.core.dbobjects.group.GroupWithAssociations
import com.mifos.core.dbobjects.templates.loans.LoanRepaymentTemplate
import com.mifos.core.dbobjects.templates.savings.SavingsAccountTransactionTemplate
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface SyncGroupsDialogRepository {

    fun syncGroupAccounts(groupId: Int): Observable<GroupAccounts>

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

    fun getGroupWithAssociations(groupId: Int): Observable<GroupWithAssociations>

    fun syncClientInDatabase(client: Client): Observable<Client>

    suspend fun syncClientAccounts(clientId: Int): ClientAccounts

    fun syncGroupInDatabase(group: Group): Observable<Group>
}
