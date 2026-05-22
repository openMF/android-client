/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.center.dao

import com.mifos.room.center.entity.CenterEntity
import com.mifos.room.center.entity.CenterPayloadEntity
import com.mifos.room.group.entity.GroupEntity
import com.mifos.room.loan.entity.LoanAccountEntity
import com.mifos.room.savings.entity.SavingsAccountEntity
import kotlinx.coroutines.flow.Flow
import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update

@Dao
interface CenterDao {

    @Insert(entity = CenterEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCenter(center: CenterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = LoanAccountEntity::class)
    suspend fun saveLoanAccount(loanAccount: LoanAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = SavingsAccountEntity::class)
    suspend fun saveSavingsAccount(savingsAccount: SavingsAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = LoanAccountEntity::class)
    suspend fun saveMemberLoanAccount(loanAccount: LoanAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = CenterPayloadEntity::class)
    suspend fun saveCenterPayload(centerPayload: CenterPayloadEntity)

    @Update(entity = CenterPayloadEntity::class, onConflict = OnConflictStrategy.NONE)
    suspend fun updateCenterPayload(centerPayload: CenterPayloadEntity)

    @Query("DELETE FROM CenterPayload WHERE id = :id")
    suspend fun deleteCenterPayloadById(id: Int)

    @Query("SELECT * FROM Center")
    fun readAllCenters(): Flow<List<CenterEntity>>

    @Query("SELECT * FROM CenterPayload")
    fun readAllCenterPayload(): Flow<List<CenterPayloadEntity>>

    @Query("SELECT * FROM GroupTable WHERE centerId = :centerId")
    fun getCenterAssociateGroups(centerId: Int): Flow<List<GroupEntity>>
}
