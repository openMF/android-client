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

import com.mifos.core.data.repository.SyncSavingsAccountTransactionRepository
import com.mifos.core.model.objects.account.saving.SavingsAccountTransactionResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.room.entities.PaymentTypeOption
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncSavingsAccountTransactionRepositoryImp @Inject constructor(
    private val dataManagerSavings: DataManagerSavings,
    private val dataManagerLoan: DataManagerLoan,
) : SyncSavingsAccountTransactionRepository {

    override fun allSavingsAccountTransactions(): Flow<List<SavingsAccountTransactionRequest>> {
        return dataManagerSavings.allSavingsAccountTransactions
    }

    override fun paymentTypeOption(): Flow<List<PaymentTypeOption>> {
        return dataManagerLoan.paymentTypeOption
    }

    override fun processTransaction(
        savingsAccountType: String?,
        savingsAccountId: Int,
        transactionType: String?,
        request: SavingsAccountTransactionRequest,
    ): Flow<SavingsAccountTransactionResponse?> {
        return dataManagerSavings.processTransaction(
            savingsAccountType,
            savingsAccountId,
            transactionType,
            request,
        )
    }

    override fun deleteAndUpdateTransactions(savingsAccountId: Int): Flow<List<SavingsAccountTransactionRequest>> {
        return dataManagerSavings.deleteAndUpdateTransactions(savingsAccountId)
    }

    override suspend fun updateLoanRepaymentTransaction(savingsAccountTransactionRequest: SavingsAccountTransactionRequest) {
        dataManagerSavings.updateLoanRepaymentTransaction(savingsAccountTransactionRequest)
    }
}
