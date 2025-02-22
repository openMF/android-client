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

import com.mifos.core.model.objects.account.saving.SavingsAccountTransactionResponse
import com.mifos.room.entities.PaymentTypeOption
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequest
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface SyncSavingsAccountTransactionRepository {

    fun allSavingsAccountTransactions(): Flow<List<SavingsAccountTransactionRequest>>

    fun paymentTypeOption(): Flow<List<PaymentTypeOption>>

    fun processTransaction(
        savingsAccountType: String?,
        savingsAccountId: Int,
        transactionType: String?,
        request: SavingsAccountTransactionRequest,
    ): Flow<SavingsAccountTransactionResponse?>

    fun deleteAndUpdateTransactions(savingsAccountId: Int): Flow<List<SavingsAccountTransactionRequest>>

    suspend fun updateLoanRepaymentTransaction(savingsAccountTransactionRequest: SavingsAccountTransactionRequest)
}
