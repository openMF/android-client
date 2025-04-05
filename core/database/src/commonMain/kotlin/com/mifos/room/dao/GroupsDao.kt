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

import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import com.mifos.room.entities.group.GroupEntity
import com.mifos.room.entities.group.GroupPayloadEntity
import com.mifos.room.utils.Dao
import com.mifos.room.utils.Insert
import com.mifos.room.utils.OnConflictStrategy
import com.mifos.room.utils.Query
import com.mifos.room.utils.Update
import kotlinx.coroutines.flow.Flow

/**
 * Created by Pronay Sarker on 15/02/2025 (1:07 PM)
 */
@Dao
interface GroupsDao {

    @Insert(entity = GroupEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)

    @Insert(entity = LoanAccountEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanAccount(loanAccount: LoanAccountEntity)

    @Insert(entity = SavingsAccountEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsAccount(savingsAccount: SavingsAccountEntity)

    @Insert(entity = GroupPayloadEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupPayload(groupPayload: GroupPayloadEntity)

    @Update(entity = GroupPayloadEntity::class)
    suspend fun updateGroupPayload(payload: GroupPayloadEntity)

    @Query("DELETE FROM GroupPayload where id = :groupId")
    suspend fun deleteGroupPayloadById(groupId: Int)

    @Query("SELECT * FROM GroupTable")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM GroupTable LIMIT :limit OFFSET :offset")
    suspend fun getAllGroups(offset: Int, limit: Int): List<GroupEntity>

    @Query("SELECT * FROM GroupTable WHERE id = :groupId")
    fun getGroupById(groupId: Int): Flow<GroupEntity>

    @Query("SELECT * FROM LoanAccountEntity WHERE groupId = :groupId")
    fun getLoanAccountsByGroupId(groupId: Int): Flow<List<LoanAccountEntity>>

    @Query("SELECT * FROM GroupPayload")
    fun getAllGroupPayloads(): Flow<List<GroupPayloadEntity>>

    @Query("SELECT * FROM SavingsAccount WHERE groupId = :groupId")
    fun getSavingsAccountsByGroupId(groupId: Int): Flow<List<SavingsAccountEntity>>
}
