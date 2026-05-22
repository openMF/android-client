/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.room.center.entity.CenterAccounts
import com.mifos.room.center.entity.CenterEntity
import com.mifos.room.center.entity.CenterWithAssociations
import com.mifos.room.client.entity.ClientAccounts
import com.mifos.room.client.entity.ClientEntity
import com.mifos.room.group.entity.GroupAccounts
import com.mifos.room.group.entity.GroupEntity
import com.mifos.room.group.entity.GroupWithAssociations
import com.mifos.room.loan.entity.LoanRepaymentTemplateEntity
import com.mifos.room.loan.entity.LoanWithAssociationsEntity
import com.mifos.room.savings.entity.SavingsAccountTransactionTemplateEntity
import com.mifos.room.savings.entity.SavingsAccountWithAssociationsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface SyncCentersDialogRepository {

    fun syncCenterAccounts(centerId: Int): Flow<DataState<CenterAccounts>>

    fun syncLoanById(loanId: Int): Flow<DataState<LoanWithAssociationsEntity>>

    fun syncLoanRepaymentTemplate(loanId: Int): Flow<DataState<LoanRepaymentTemplateEntity>>

    fun getCenterWithAssociations(centerId: Int): Flow<DataState<CenterWithAssociations>>

    fun getGroupWithAssociations(groupId: Int): Flow<DataState<GroupWithAssociations>>

    fun syncGroupAccounts(groupId: Int): Flow<DataState<GroupAccounts>>

    suspend fun syncClientAccounts(clientId: Int): ClientAccounts

    suspend fun syncGroupInDatabase(group: GroupEntity)

    suspend fun syncClientInDatabase(client: ClientEntity)

    suspend fun syncCenterInDatabase(center: CenterEntity)

    fun syncSavingsAccount(
        type: String,
        savingsAccountId: Int,
        association: String?,
    ): Flow<DataState<SavingsAccountWithAssociationsEntity>>

    fun syncSavingsAccountTransactionTemplate(
        savingsAccountType: String,
        savingsAccountId: Int,
        transactionType: String?,
    ): Flow<DataState<SavingsAccountTransactionTemplateEntity>>
}
