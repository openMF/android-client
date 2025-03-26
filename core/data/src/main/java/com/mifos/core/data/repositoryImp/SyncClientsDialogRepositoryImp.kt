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
import com.mifos.room.entities.accounts.ClientAccounts
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplateEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncClientsDialogRepositoryImp(
    private val dataManagerClient: DataManagerClient,
    private val dataManagerLoan: DataManagerLoan,
    private val dataManagerSavings: DataManagerSavings,
) : SyncClientsDialogRepository {

    override suspend fun syncClientAccounts(clientId: Int): ClientAccounts {
        return dataManagerClient.getClientAccounts(clientId)
    }

    override fun syncLoanById(loanId: Int): Flow<LoanWithAssociationsEntity> {
        return dataManagerLoan.syncLoanById(loanId)
    }

    override fun syncLoanRepaymentTemplate(loanId: Int): Flow<LoanRepaymentTemplateEntity> {
        return dataManagerLoan.syncLoanRepaymentTemplate(loanId)
    }

    override fun syncSavingsAccount(
        type: String?,
        savingsAccountId: Int,
        association: String?,
    ): Flow<SavingsAccountWithAssociationsEntity> {
        return dataManagerSavings.syncSavingsAccount(type, savingsAccountId, association)
    }

    override fun syncSavingsAccountTransactionTemplate(
        savingsAccountType: String?,
        savingsAccountId: Int,
        transactionType: String?,
    ): Flow<SavingsAccountTransactionTemplateEntity?> {
        return dataManagerSavings.getSavingsAccountTransactionTemplate(
            savingsAccountType,
            savingsAccountId,
            transactionType,
        )
    }

    override suspend fun syncClientInDatabase(client: ClientEntity) {
        dataManagerClient.syncClientInDatabase(client)
    }
}
