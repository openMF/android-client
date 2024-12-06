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

import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface SyncSavingsAccountTransactionRepository {

    fun allSavingsAccountTransactions(): Observable<List<SavingsAccountTransactionRequest>>

    fun paymentTypeOption(): Observable<List<com.mifos.core.objects.PaymentTypeOption>>

    fun processTransaction(
        savingsAccountType: String?,
        savingsAccountId: Int,
        transactionType: String?,
        request: SavingsAccountTransactionRequest,
    ): Observable<SavingsAccountTransactionResponse>

    fun deleteAndUpdateTransactions(
        savingsAccountId: Int,
    ): Observable<List<SavingsAccountTransactionRequest>>

    fun updateLoanRepaymentTransaction(
        savingsAccountTransactionRequest: SavingsAccountTransactionRequest,
    ): Observable<SavingsAccountTransactionRequest>
}
