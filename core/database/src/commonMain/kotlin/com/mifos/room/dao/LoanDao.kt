/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.dao

import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import kotlinx.coroutines.flow.Flow
import template.core.base.database.Dao
import template.core.base.database.Insert
import template.core.base.database.OnConflictStrategy
import template.core.base.database.Query
import template.core.base.database.Update

@Dao
interface LoanDao {

    @Insert(entity = LoanWithAssociationsEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLoanWithAssociations(loanWithAssociations: LoanWithAssociationsEntity)

    @Query("SELECT * FROM LoanWithAssociations WHERE id = :loanId LIMIT 1")
    fun getLoanById(loanId: Int): Flow<LoanWithAssociationsEntity?>

    @Insert(entity = LoanRepaymentRequestEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanRepaymentTransaction(request: LoanRepaymentRequestEntity)

    @Insert(entity = PaymentTypeOptionEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentTypeOption(paymentTypeOption: PaymentTypeOptionEntity)

    @Query("SELECT * FROM PaymentTypeOption")
    fun getPaymentTypeOptions(): Flow<List<PaymentTypeOptionEntity>>

    @Query("SELECT * FROM LoanRepaymentRequestEntity WHERE loanId = :loanId LIMIT 1")
    suspend fun getLoanRepaymentRequest(loanId: Int): LoanRepaymentRequestEntity?

    @Update(entity = LoanRepaymentRequestEntity::class, onConflict = OnConflictStrategy.NONE)
    suspend fun updateLoanRepaymentRequest(loanRepaymentRequest: LoanRepaymentRequestEntity)

    @Query("SELECT * FROM LoanRepaymentRequestEntity ORDER BY timeStamp ASC")
    fun readAllLoanRepaymentTransaction(): Flow<List<LoanRepaymentRequestEntity>>

    @Insert(entity = LoanRepaymentTemplateEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanRepaymentTemplate(template: LoanRepaymentTemplateEntity)

    @Query("SELECT * FROM LoanRepaymentTemplate WHERE loanId = :loanId LIMIT 1")
    suspend fun getLoanRepaymentTemplate(loanId: Int): LoanRepaymentTemplateEntity?

    @Query("DELETE FROM LoanRepaymentTemplate WHERE loanId = :loanId")
    suspend fun deleteLoanRepaymentByLoanId(loanId: Int)
}
