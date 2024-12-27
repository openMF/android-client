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
import com.mifos.core.modelobjects.account.saving.SavingsAccountTransactionResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncSavingsAccountTransactionRepositoryImp @Inject constructor(
    private val dataManagerSavings: DataManagerSavings,
    private val dataManagerLoan: DataManagerLoan,
) : SyncSavingsAccountTransactionRepository {

    override fun allSavingsAccountTransactions(): Observable<List<SavingsAccountTransactionRequest>> {
        return dataManagerSavings.allSavingsAccountTransactions
    }

    override fun paymentTypeOption(): Observable<List<com.mifos.core.objects.PaymentTypeOption>> {
        return dataManagerLoan.paymentTypeOption
    }

    override fun processTransaction(
        savingsAccountType: String?,
        savingsAccountId: Int,
        transactionType: String?,
        request: SavingsAccountTransactionRequest,
    ): Observable<SavingsAccountTransactionResponse> {
        return dataManagerSavings.processTransaction(
            savingsAccountType,
            savingsAccountId,
            transactionType,
            request,
        )
    }

    override fun deleteAndUpdateTransactions(savingsAccountId: Int): Observable<List<SavingsAccountTransactionRequest>> {
        return dataManagerSavings.deleteAndUpdateTransactions(savingsAccountId)
    }

    override fun updateLoanRepaymentTransaction(savingsAccountTransactionRequest: SavingsAccountTransactionRequest): Observable<SavingsAccountTransactionRequest> {
        return dataManagerSavings.updateLoanRepaymentTransaction(savingsAccountTransactionRequest)
    }
}
