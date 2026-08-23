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

import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import com.mifos.room.entities.group.GroupEntity
import com.mifos.room.entities.group.GroupPayloadEntity
import kotlinx.coroutines.flow.Flow
import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update

/**
 * Created by Pronay Sarker on 15/02/2025 (1:07 PM)
 */
@Dao
interface GroupsDao {

    @Insert(entity = GroupEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)

    // Offline-first paged group-list source-of-truth (GroupListPage Store5 read path).
    // No `page` column on GroupEntity, so pages are windowed by LIMIT/OFFSET over the
    // full GroupTable (ordered by primary key) — no schema change.
    @Query("SELECT * FROM GroupTable ORDER BY id LIMIT :limit OFFSET :offset")
    fun getPageGroups(limit: Int, offset: Int): Flow<List<GroupEntity>>

    @Insert(entity = GroupEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<GroupEntity>)

    @Insert(entity = LoanAccountEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanAccount(loanAccount: LoanAccountEntity)

    @Insert(entity = SavingsAccountEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsAccount(savingsAccount: SavingsAccountEntity)

    @Insert(entity = GroupPayloadEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupPayload(groupPayload: GroupPayloadEntity)

    @Update(entity = GroupPayloadEntity::class, onConflict = OnConflictStrategy.NONE)
    suspend fun updateGroupPayload(payload: GroupPayloadEntity)

    @Query("DELETE FROM GroupPayload where id = :groupId")
    suspend fun deleteGroupPayloadById(groupId: Int)

    @Query("SELECT * FROM GroupTable")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM GroupTable LIMIT :limit OFFSET :offset")
    suspend fun getAllGroups(offset: Int, limit: Int): List<GroupEntity>

    @Query("SELECT * FROM GroupTable WHERE id = :groupId")
    fun getGroupById(groupId: Int): Flow<GroupEntity>

    // Offline-first Store5 source-of-truth delete hooks (getGroup ledger pilot). deleteGroup
    // backs SourceOfTruth.delete(key); deleteAllGroups backs SourceOfTruth.deleteAll() which
    // Store.clear() invokes on logout via StoreCacheManager (D7 cache hygiene).
    @Query("DELETE FROM GroupTable WHERE id = :groupId")
    suspend fun deleteGroup(groupId: Int)

    @Query("DELETE FROM GroupTable")
    suspend fun deleteAllGroups()

    @Query("SELECT * FROM LoanAccountEntity WHERE groupId = :groupId")
    fun getLoanAccountsByGroupId(groupId: Int): Flow<List<LoanAccountEntity>>

    @Query("SELECT * FROM GroupPayload")
    fun getAllGroupPayloads(): Flow<List<GroupPayloadEntity>>

    @Query("SELECT * FROM SavingsAccount WHERE groupId = :groupId")
    fun getSavingsAccountsByGroupId(groupId: Int): Flow<List<SavingsAccountEntity>>
}
