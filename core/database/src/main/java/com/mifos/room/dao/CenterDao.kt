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
import com.mifos.room.entities.accounts.loans.LoanAccount
import com.mifos.room.entities.accounts.savings.SavingsAccount
import com.mifos.room.entities.center.CenterPayload
import com.mifos.room.entities.group.Center
import com.mifos.room.entities.group.Group
import kotlinx.coroutines.flow.Flow

@Dao
interface CenterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCenter(center: Center)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLoanAccount(loanAccount: LoanAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSavingsAccount(savingsAccount: SavingsAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMemberLoanAccount(loanAccount: LoanAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCenterPayload(centerPayload: CenterPayload?)

    @Update
    suspend fun updateCenterPayload(centerPayload: CenterPayload?)

    @Query("DELETE FROM CenterPayload WHERE id = :id")
    suspend fun deleteCenterPayloadById(id: Int)

    @Query("SELECT * FROM Center")
    fun readAllCenters(): Flow<List<Center>>

    @Query("SELECT * FROM CenterPayload")
    fun readAllCenterPayload(): Flow<List<CenterPayload>>

    @Query("SELECT * FROM GroupTable WHERE centerId = :centerId")
    fun getCenterAssociateGroups(centerId: Int): Flow<List<Group>>
}
