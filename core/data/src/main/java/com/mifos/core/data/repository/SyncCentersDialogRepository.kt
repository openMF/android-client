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

import com.mifos.room.entities.accounts.CenterAccounts
import com.mifos.room.entities.accounts.ClientAccounts
import com.mifos.room.entities.accounts.GroupAccounts
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.CenterWithAssociations
import com.mifos.room.entities.group.GroupEntity
import com.mifos.room.entities.group.GroupWithAssociations
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplateEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface SyncCentersDialogRepository {

    fun syncCenterAccounts(centerId: Int): Flow<CenterAccounts>

    fun syncLoanById(loanId: Int): Flow<LoanWithAssociationsEntity>

    fun syncLoanRepaymentTemplate(loanId: Int): Flow<LoanRepaymentTemplateEntity>

    fun getCenterWithAssociations(centerId: Int): Flow<CenterWithAssociations>

    fun getGroupWithAssociations(groupId: Int): Flow<GroupWithAssociations>

    fun syncGroupAccounts(groupId: Int): Flow<GroupAccounts>

    suspend fun syncClientAccounts(clientId: Int): ClientAccounts

    suspend fun syncGroupInDatabase(group: GroupEntity)

    suspend fun syncClientInDatabase(client: ClientEntity)

    suspend fun syncCenterInDatabase(center: CenterEntity)

    fun syncSavingsAccount(
        type: String?,
        savingsAccountId: Int,
        association: String?,
    ): Flow<SavingsAccountWithAssociationsEntity>

    fun syncSavingsAccountTransactionTemplate(
        savingsAccountType: String?,
        savingsAccountId: Int,
        transactionType: String?,
    ): Flow<SavingsAccountTransactionTemplateEntity>
}
