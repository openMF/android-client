/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.databasehelper

import com.mifos.core.entity.accounts.loan.LoanAccount
import com.mifos.core.entity.accounts.loan.LoanAccount_Table
import com.mifos.core.entity.accounts.savings.SavingsAccount
import com.mifos.core.entity.accounts.savings.SavingsAccount_Table
import com.mifos.core.entity.group.Group
import com.mifos.core.entity.group.GroupDate
import com.mifos.core.entity.group.GroupPayload
import com.mifos.core.entity.group.GroupPayload_Table
import com.mifos.core.entity.group.Group_Table
import com.mifos.core.objects.clients.Page
import com.mifos.core.objects.responses.SaveResponse
import com.mifos.room.entities.accounts.GroupAccounts
import com.raizlabs.android.dbflow.sql.language.Delete
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable
import rx.functions.Func0
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This DatabaseHelper Managing all Database logic and staff (Saving, Update, Delete).
 * Whenever DataManager send response to save or request to read from Database then this class
 * save the response or read the all values from database and return as accordingly.
 * Created by Rajan Maurya on 28/06/16.
 */
@Singleton
class DatabaseHelperGroups @Inject constructor() {
    /**
     * This Method Saving the Single Group in the Database
     *
     * @param group
     * @return Observable.just(Group)
     */
    fun saveGroup(group: Group): Observable<Group> {
        return Observable.defer(
            Func0 {
                if (group.activationDate.isNotEmpty()) {
                    val groupDate = group.id?.toLong()?.let {
                        group.activationDate[0].let { it1 ->
                            group.activationDate[1].let { it2 ->
                                group.activationDate[2].let { it3 ->
                                    GroupDate(
                                        it,
                                        0,
                                        it1,
                                        it2,
                                        it3,
                                    )
                                }
                            }
                        }
                    }
                    group.groupDate = groupDate
                }
                group.save()
                Observable.just(group)
            },
        )
    }

    /**
     * Reading All groups from Database table of Group and return the GroupList
     *
     * @return List Of Groups
     */
    fun readAllGroups(): Observable<Page<Group>> {
        return Observable.defer {
            val groupPage = Page<Group>()
            groupPage.pageItems = SQLite.select()
                .from(Group::class.java)
                .queryList()
            Observable.just(groupPage)
        }
    }

    /**
     * Reading All groups from Database table of Group and return the GroupList
     * @param offset
     * @param limit
     * @return List Of Groups
     */
    fun readAllGroups(offset: Int, limit: Int): Observable<Page<Group>> {
        return Observable.defer {
            val groupPage = Page<Group>()
            groupPage.pageItems = SQLite.select()
                .from(Group::class.java)
                .offset(offset)
                .limit(limit)
                .queryList()
            Observable.just(groupPage)
        }
    }

    /**
     * This Method RetrieFloing the Group from the Local Database.
     *
     * @param groupId Group Id
     * @return Group
     */
    fun getGroup(groupId: Int): Observable<Group> {
        return Observable.defer {
            val group = SQLite.select()
                .from(Group::class.java)
                .where(Group_Table.id.eq(groupId))
                .querySingle()
            if (group != null) {
                group.activationDate = listOf(
                    group.groupDate?.day,
                    group.groupDate?.month, group.groupDate?.year,
                ) as List<Int>
            }
            Observable.just(group)
        }
    }

    /**
     * This Method  write the GroupAccounts in tho DB. According to Schema Defined in Model
     *
     * @param groupAccounts Model of List of LoanAccount and SavingAccount
     * @param groupId       Group Id
     * @return GroupAccounts
     */
    fun saveGroupAccounts(
        groupAccounts: GroupAccounts,
        groupId: Int,
    ): Observable<GroupAccounts> {
        return Observable.defer {
            val loanAccounts = groupAccounts.loanAccounts
            val savingsAccounts = groupAccounts.savingsAccounts
            for (loanAccount: LoanAccount in loanAccounts) {
                loanAccount.groupId = groupId.toLong()
                loanAccount.save()
            }
            for (savingsAccount: SavingsAccount in savingsAccounts) {
                savingsAccount.groupId = groupId.toLong()
                savingsAccount.save()
            }
            Observable.just(groupAccounts)
        }
    }

    /**
     * This Method Read the Table of LoanAccount and SavingAccount and return the List of
     * LoanAccount and SavingAccount according to groupId
     *
     * @param groupId Group Id
     * @return the GroupAccounts according to Group Id
     */
    fun readGroupAccounts(groupId: Int): Observable<GroupAccounts> {
        return Observable.defer {
            val loanAccounts = SQLite.select()
                .from(LoanAccount::class.java)
                .where(LoanAccount_Table.groupId.eq(groupId.toLong()))
                .queryList()
            val savingsAccounts = SQLite.select()
                .from(SavingsAccount::class.java)
                .where(SavingsAccount_Table.groupId.eq(groupId.toLong()))
                .queryList()
            val groupAccounts = GroupAccounts()
            groupAccounts.loanAccounts = loanAccounts
            groupAccounts.savingsAccounts = savingsAccounts
            Observable.just(groupAccounts)
        }
    }

    fun saveGroupPayload(groupPayload: GroupPayload): Observable<SaveResponse> {
        return Observable.defer {
            groupPayload.save()
            Observable.just(SaveResponse())
        }
    }

    fun realAllGroupPayload(): Observable<List<GroupPayload>> {
        return Observable.defer {
            val groupPayloads = SQLite.select()
                .from(GroupPayload::class.java)
                .queryList()
            Observable.just(groupPayloads)
        }
    }

    /**
     * This Method for deleting the group payload from the Database according to Id and
     * again fetch the group List from the Database GroupPayload_Table
     * @param id is Id of the Client Payload in which reference client was saved into Database
     * @return List<ClientPayload></ClientPayload>>
     */
    fun deleteAndUpdateGroupPayloads(id: Int): Observable<List<GroupPayload>> {
        return Observable.defer {
            Delete.table(GroupPayload::class.java, GroupPayload_Table.id.eq(id))
            val groupPayloads = SQLite.select()
                .from(GroupPayload::class.java)
                .queryList()
            Observable.just(groupPayloads)
        }
    }

    fun updateDatabaseGroupPayload(groupPayload: GroupPayload): Observable<GroupPayload> {
        return Observable.defer {
            groupPayload.update()
            Observable.just(groupPayload)
        }
    }
}
