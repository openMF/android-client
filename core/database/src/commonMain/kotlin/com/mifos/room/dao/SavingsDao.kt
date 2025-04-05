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

import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplateEntity
import com.mifos.room.utils.Dao
import com.mifos.room.utils.Insert
import com.mifos.room.utils.OnConflictStrategy
import com.mifos.room.utils.Query
import com.mifos.room.utils.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDao {

    @Insert(entity = SavingsAccountTransactionEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransactions(transaction: List<SavingsAccountTransactionEntity>)

    @Insert(entity = SavingsAccountWithAssociationsEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsAccountWithAssociations(savingsAccountWithAssociations: SavingsAccountWithAssociationsEntity)

    @Insert(entity = SavingsAccountTransactionRequestEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsAccountTransactionRequest(transactionRequest: SavingsAccountTransactionRequestEntity)

    @Insert(entity = PaymentTypeOptionEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPaymentTypeOption(paymentTypeOption: List<PaymentTypeOptionEntity>)

    @Insert(entity = SavingsAccountTransactionTemplateEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsAccountTransactionTemplate(transactionTemplate: SavingsAccountTransactionTemplateEntity)

    @Update(SavingsAccountTransactionRequestEntity::class)
    suspend fun updateSavingsAccountTransactionRequest(transactionRequest: SavingsAccountTransactionRequestEntity)

    @Query("SELECT * FROM TransactionTable WHERE savingsAccountId = :savingsAccountId")
    suspend fun getAllTransactions(savingsAccountId: Int): List<SavingsAccountTransactionEntity>

    @Query("DELETE FROM SavingsAccountTransactionRequest where savingAccountId = :savingsAccountId")
    suspend fun deleteSavingsAccountTransactionRequest(savingsAccountId: Int)

    @Query("SELECT * FROM SavingsAccountTransactionRequest")
    fun getAllSavingsAccountTransactionRequest(): Flow<List<SavingsAccountTransactionRequestEntity>>

    @Query("SELECT * FROM SavingsAccountTransactionRequest where savingAccountId = :savingsAccountId")
    fun getSavingsAccountTransactionRequest(savingsAccountId: Int): Flow<SavingsAccountTransactionRequestEntity?>

    @Query("SELECT * FROM SavingsAccountTransactionTemplate where accountId = :savingsAccountId")
    fun getSavingsAccountTransactionTemplate(savingsAccountId: Int): Flow<SavingsAccountTransactionTemplateEntity?>

    @Query("SELECT * FROM SavingsAccountWithAssociations where id = :savingsAccountId")
    fun getSavingsAccountWithAssociations(savingsAccountId: Int): Flow<SavingsAccountWithAssociationsEntity?>

    @Query("SELECT * FROM PaymentTypeOption")
    fun getAllPaymentTypeOption(): Flow<List<PaymentTypeOptionEntity>>
}
