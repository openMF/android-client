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

import com.mifos.core.objects.accounts.CenterAccounts
import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.accounts.GroupAccounts
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.core.objects.group.Group
import com.mifos.core.objects.group.GroupWithAssociations
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface SyncCentersDialogRepository {

    fun syncCenterAccounts(centerId: Int): Observable<CenterAccounts>

    fun syncLoanById(loanId: Int): Observable<LoanWithAssociations>

    fun syncLoanRepaymentTemplate(loanId: Int): Observable<LoanRepaymentTemplate>

    fun getCenterWithAssociations(centerId: Int): Observable<CenterWithAssociations>

    fun getGroupWithAssociations(groupId: Int): Observable<GroupWithAssociations>

    fun syncGroupAccounts(groupId: Int): Observable<GroupAccounts>

    fun syncClientAccounts(clientId: Int): Observable<ClientAccounts>

    fun syncGroupInDatabase(group: Group): Observable<Group>

    fun syncClientInDatabase(client: Client): Observable<Client>

    fun syncCenterInDatabase(center: Center): Observable<Center>

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
}
