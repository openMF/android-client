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
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLoanWithAssociations(loanWithAssociations: LoanWithAssociationsEntity)

    @Query("SELECT * FROM LoanWithAssociations WHERE id = :loanId LIMIT 1")
    fun getLoanById(loanId: Int): Flow<LoanWithAssociationsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanRepaymentTransaction(request: LoanRepaymentRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentTypeOption(paymentTypeOption: PaymentTypeOptionEntity)

    @Query("SELECT * FROM PaymentTypeOption")
    fun getPaymentTypeOptions(): Flow<List<PaymentTypeOptionEntity>>

    @Query("SELECT * FROM LoanRepaymentRequestEntity WHERE loanId = :loanId LIMIT 1")
    suspend fun getLoanRepaymentRequest(loanId: Int): LoanRepaymentRequestEntity?

    @Update
    suspend fun updateLoanRepaymentRequest(loanRepaymentRequest: LoanRepaymentRequestEntity)

    @Query("SELECT * FROM LoanRepaymentRequestEntity ORDER BY timeStamp ASC")
    fun readAllLoanRepaymentTransaction(): Flow<List<LoanRepaymentRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanRepaymentTemplate(template: LoanRepaymentTemplateEntity)

    @Query("SELECT * FROM LoanRepaymentTemplate WHERE loanId = :loanId LIMIT 1")
    fun getLoanRepaymentTemplate(loanId: Int): LoanRepaymentTemplateEntity?

    @Query("DELETE FROM LoanRepaymentTemplate WHERE loanId = :loanId")
    suspend fun deleteLoanRepaymentByLoanId(loanId: Int)
}
