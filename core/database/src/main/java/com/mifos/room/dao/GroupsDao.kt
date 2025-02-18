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
import com.mifos.room.entities.group.Group
import com.mifos.room.entities.group.GroupPayload
import kotlinx.coroutines.flow.Flow

/**
 * Created by Pronay Sarker on 15/02/2025 (1:07 PM)
 */
@Dao
interface GroupsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanAccount(loanAccount: LoanAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsAccount(savingsAccount: SavingsAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupPayload(groupPayload: GroupPayload)

    @Update
    suspend fun updateGroupPayload(payload: GroupPayload)

    @Query("DELETE FROM GroupPayload where id = :groupId")
    suspend fun deleteGroupPayloadById(groupId: Int)

    @Query("SELECT * FROM GroupTable")
    fun getAllGroups(): Flow<List<Group>>

    @Query("SELECT * FROM GroupTable LIMIT :limit OFFSET :offset")
    suspend fun getAllGroups(offset: Int, limit: Int): List<Group>

    @Query("SELECT * FROM GroupTable WHERE id = :groupId")
    fun getGroupById(groupId: Int): Flow<Group>

    @Query("SELECT * FROM LoanAccount WHERE groupId = :groupId")
    fun getLoanAccountsByGroupId(groupId: Int): Flow<List<LoanAccount>>

    @Query("SELECT * FROM GroupPayload")
    fun getAllGroupPayloads(): Flow<List<GroupPayload>>

    @Query("SELECT * FROM SavingsAccount WHERE groupId = :groupId")
    fun getSavingsAccountsByGroupId(groupId: Int): Flow<List<SavingsAccount>>
}
