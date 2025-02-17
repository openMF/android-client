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

import com.mifos.core.entity.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.entity.client.Client
import com.mifos.core.entity.templates.savings.SavingsAccountTransactionTemplate
import com.mifos.room.entities.accounts.CenterAccounts
import com.mifos.room.entities.accounts.ClientAccounts
import com.mifos.room.entities.accounts.GroupAccounts
import com.mifos.room.entities.accounts.loans.LoanWithAssociations
import com.mifos.room.entities.group.Center
import com.mifos.room.entities.group.CenterWithAssociations
import com.mifos.room.entities.group.Group
import com.mifos.room.entities.group.GroupWithAssociations
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplate
import kotlinx.coroutines.flow.Flow
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface SyncCentersDialogRepository {

    fun syncCenterAccounts(centerId: Int): Flow<CenterAccounts>

    fun syncLoanById(loanId: Int): Flow<LoanWithAssociations>

    fun syncLoanRepaymentTemplate(loanId: Int): Flow<LoanRepaymentTemplate>

    fun getCenterWithAssociations(centerId: Int): Flow<CenterWithAssociations>

    fun getGroupWithAssociations(groupId: Int): Observable<GroupWithAssociations>

    fun syncGroupAccounts(groupId: Int): Flow<GroupAccounts>

    suspend fun syncClientAccounts(clientId: Int): ClientAccounts

    suspend fun syncGroupInDatabase(group: Group)

    fun syncClientInDatabase(client: Client): Observable<Client>

    suspend fun syncCenterInDatabase(center: Center)

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
