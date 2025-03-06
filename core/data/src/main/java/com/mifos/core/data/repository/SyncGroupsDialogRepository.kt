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

import com.mifos.room.entities.accounts.ClientAccounts
import com.mifos.room.entities.accounts.GroupAccounts
import com.mifos.room.entities.accounts.loans.LoanWithAssociations
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociations
import com.mifos.room.entities.group.Group
import com.mifos.room.entities.group.GroupWithAssociations
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplate
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface SyncGroupsDialogRepository {

    fun syncGroupAccounts(groupId: Int): Flow<GroupAccounts>

    fun syncLoanById(loanId: Int): Flow<LoanWithAssociations>

    fun syncLoanRepaymentTemplate(loanId: Int): Flow<LoanRepaymentTemplate>

    fun syncSavingsAccount(
        type: String?,
        savingsAccountId: Int,
        association: String?,
    ): Flow<SavingsAccountWithAssociations>

    fun syncSavingsAccountTransactionTemplate(
        savingsAccountType: String?,
        savingsAccountId: Int,
        transactionType: String?,
    ): Flow<SavingsAccountTransactionTemplate?>

    fun getGroupWithAssociations(groupId: Int): Flow<GroupWithAssociations>

    suspend fun syncClientInDatabase(client: com.mifos.room.entities.client.Client)

    suspend fun syncClientAccounts(clientId: Int): ClientAccounts

    suspend fun syncGroupInDatabase(group: Group)
}
