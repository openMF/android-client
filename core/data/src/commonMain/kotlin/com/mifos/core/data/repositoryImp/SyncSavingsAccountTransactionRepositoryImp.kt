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

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.SyncSavingsAccountTransactionRepository
import com.mifos.core.model.objects.account.saving.SavingsAccountTransactionResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncSavingsAccountTransactionRepositoryImp(
    private val dataManagerSavings: DataManagerSavings,
    private val dataManagerLoan: DataManagerLoan,
) : SyncSavingsAccountTransactionRepository {

    override fun allSavingsAccountTransactions(): Flow<DataState<List<SavingsAccountTransactionRequestEntity>>> {
        return dataManagerSavings.allSavingsAccountTransactions
            .asDataStateFlow()
    }

    override fun paymentTypeOption(): Flow<DataState<List<PaymentTypeOptionEntity>>> {
        return dataManagerLoan.paymentTypeOption
            .asDataStateFlow()
    }

    override fun processTransaction(
        savingsAccountType: String,
        savingsAccountId: Int,
        transactionType: String?,
        request: SavingsAccountTransactionRequestEntity,
    ): Flow<DataState<SavingsAccountTransactionResponse?>> {
        return dataManagerSavings.processTransaction(
            savingsAccountType,
            savingsAccountId,
            transactionType,
            request,
        ).asDataStateFlow()
    }

    override fun deleteAndUpdateTransactions(
        savingsAccountId: Int,
    ): Flow<DataState<List<SavingsAccountTransactionRequestEntity>>> {
        return dataManagerSavings.deleteAndUpdateTransactions(savingsAccountId)
            .asDataStateFlow()
    }

    override suspend fun updateLoanRepaymentTransaction(savingsAccountTransactionRequest: SavingsAccountTransactionRequestEntity) {
        dataManagerSavings.updateLoanRepaymentTransaction(savingsAccountTransactionRequest)
    }
}
