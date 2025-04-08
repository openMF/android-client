/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.helper

import com.mifos.core.common.network.Dispatcher
import com.mifos.core.common.network.MifosDispatchers
import com.mifos.core.common.utils.Page
import com.mifos.core.model.objects.responses.SaveResponse
import com.mifos.room.dao.GroupsDao
import com.mifos.room.entities.accounts.GroupAccounts
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import com.mifos.room.entities.group.GroupDateEntity
import com.mifos.room.entities.group.GroupEntity
import com.mifos.room.entities.group.GroupPayloadEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * This DatabaseHelper Managing all Database logic and staff (Saving, Update, Delete).
 * Whenever DataManager send response to save or request to read from Database then this class
 * save the response or read the all values from database and return as accordingly.
 */
class GroupsDaoHelper(
    private val groupsDao: GroupsDao,
    @Dispatcher(MifosDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    /**
     * This Method Saving the Single Group in the Database
     *
     * @param group
     * @return Observable.just(Group)
     */
    suspend fun saveGroup(group: GroupEntity) {
        val updatedGroup = if (group.activationDate.isNotEmpty()) {
            val groupDate = group.id?.toLong()?.let {
                GroupDateEntity(
                    it,
                    0,
                    group.activationDate[0],
                    group.activationDate[1],
                    group.activationDate[2],
                )
            }
            group.copy(groupDate = groupDate)
        } else {
            group
        }
        groupsDao.insertGroup(updatedGroup)
    }

    /**
     * Reading All groups from Database table of Group and return the GroupList
     *
     * @return List Of Groups
     */
    fun readAllGroups(): Flow<Page<GroupEntity>> {
        return groupsDao.getAllGroups().map { groups ->
            Page<GroupEntity>().apply { pageItems = groups }
        }.flowOn(ioDispatcher)
    }

    /**
     * Reading All groups from Database table of Group and return the GroupList
     * @param offset
     * @param limit
     * @return List Of Groups
     */
    suspend fun readAllGroups(offset: Int, limit: Int): Page<GroupEntity> {
        val groupPage = Page<GroupEntity>()
        groupPage.pageItems = groupsDao.getAllGroups(offset, limit)
        return groupPage
    }

    /**
     * This Method Retrieving the Group from the Local Database.
     *
     * @param groupId Group Id
     * @return Group
     */
    fun getGroup(groupId: Int): Flow<GroupEntity> {
        return groupsDao.getGroupById(groupId).map { group ->
            group.copy(
                activationDate = listOf(
                    group.groupDate?.day ?: 0,
                    group.groupDate?.month ?: 0,
                    group.groupDate?.year ?: 0,
                ),
            )
        }.flowOn(ioDispatcher)
    }

    /**
     * This Method  write the GroupAccounts in tho DB. According to Schema Defined in Model
     *
     * @param groupAccounts Model of List of LoanAccount and SavingAccount
     * @param groupId       Group Id
     * @return GroupAccounts
     */
    suspend fun saveGroupAccounts(
        groupAccounts: GroupAccounts,
        groupId: Int,
    ) {
        val loanAccounts = groupAccounts.loanAccounts
        val savingsAccounts = groupAccounts.savingsAccounts

        for (loanAccount: LoanAccountEntity in loanAccounts) {
            val updatedLoanAccount = loanAccount.copy(groupId = groupId.toLong())
            groupsDao.insertLoanAccount(updatedLoanAccount)
        }
        for (savingsAccount: SavingsAccountEntity in savingsAccounts) {
            groupsDao.insertSavingsAccount(savingsAccount.copy(groupId = groupId.toLong()))
        }
    }

    /**
     * This Method Read the Table of LoanAccount and SavingAccount and return the List of
     * LoanAccount and SavingAccount according to groupId
     *
     * @param groupId Group Id
     * @return the GroupAccounts according to Group Id
     */
    fun readGroupAccounts(groupId: Int): Flow<GroupAccounts> {
        return flow {
            val loanAccounts = groupsDao.getLoanAccountsByGroupId(groupId).first()
            val savingsAccounts = groupsDao.getSavingsAccountsByGroupId(groupId).first()

            val groupAccounts = GroupAccounts()
            groupAccounts.loanAccounts = loanAccounts
            groupAccounts.savingsAccounts = savingsAccounts
            emit(groupAccounts)
        }.flowOn(ioDispatcher)
    }

    suspend fun saveGroupPayload(groupPayload: GroupPayloadEntity): SaveResponse {
        groupsDao.insertGroupPayload(groupPayload)
        return SaveResponse()
    }

    fun realAllGroupPayload(): Flow<List<GroupPayloadEntity>> {
        return groupsDao.getAllGroupPayloads().flowOn(ioDispatcher)
    }

    /**
     * This Method for deleting the group payload from the Database according to Id and
     * again fetch the group List from the Database GroupPayload_Table
     * @param id is Id of the Client Payload in which reference client was saved into Database
     * @return List<ClientPayload></ClientPayload>>
     */
    fun deleteAndUpdateGroupPayloads(id: Int): Flow<List<GroupPayloadEntity>> {
        return flow {
            groupsDao.deleteGroupPayloadById(id)
            val groupPayloads = groupsDao.getAllGroupPayloads()
            emitAll(groupPayloads)
        }.flowOn(ioDispatcher)
    }

    suspend fun updateGroupPayload(groupPayload: GroupPayloadEntity) {
        groupsDao.updateGroupPayload(groupPayload)
    }
}
