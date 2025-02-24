/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mifos.room.entities.PaymentTypeOption
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociations
import com.mifos.room.entities.accounts.savings.Transaction
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransactions(transaction: List<Transaction>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsAccountWithAssociations(savingsAccountWithAssociations: SavingsAccountWithAssociations)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsAccountTransactionRequest(transactionRequest: SavingsAccountTransactionRequest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPaymentTypeOption(paymentTypeOption: List<PaymentTypeOption>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsAccountTransactionTemplate(transactionTemplate: SavingsAccountTransactionTemplate)

    @Update
    suspend fun updateSavingsAccountTransactionRequest(transactionRequest: SavingsAccountTransactionRequest)

    @Query("SELECT * FROM TransactionTable WHERE savingsAccountId = :savingsAccountId")
    suspend fun getAllTransactions(savingsAccountId: Int): List<Transaction>

    @Query("DELETE FROM SavingsAccountTransactionRequest where savingAccountId = :savingsAccountId")
    suspend fun deleteSavingsAccountTransactionRequest(savingsAccountId: Int)

    @Query("SELECT * FROM SavingsAccountTransactionRequest")
    fun getAllSavingsAccountTransactionRequest(): Flow<List<SavingsAccountTransactionRequest>>

    @Query("SELECT * FROM SavingsAccountTransactionRequest where savingAccountId = :savingsAccountId")
    fun getSavingsAccountTransactionRequest(savingsAccountId: Int): Flow<SavingsAccountTransactionRequest?>

    @Query("SELECT * FROM SavingsAccountTransactionTemplate where accountId = :savingsAccountId")
    fun getSavingsAccountTransactionTemplate(savingsAccountId: Int): Flow<SavingsAccountTransactionTemplate?>

    @Query("SELECT * FROM SavingsAccountWithAssociations where id = :savingsAccountId")
    fun getSavingsAccountWithAssociations(savingsAccountId: Int): Flow<SavingsAccountWithAssociations?>

    @Query("SELECT * FROM PaymentTypeOption")
    fun getAllPaymentTypeOption(): Flow<List<PaymentTypeOption>>
}
