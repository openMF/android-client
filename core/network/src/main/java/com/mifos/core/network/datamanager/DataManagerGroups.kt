/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.databasehelper.DatabaseHelperClient
import com.mifos.core.model.objects.clients.Page
import com.mifos.core.model.objects.responses.SaveResponse
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.mappers.groups.GetGroupsResponseMapper
import com.mifos.core.objects.clients.ActivatePayload
import com.mifos.room.entities.accounts.GroupAccounts
import com.mifos.room.entities.group.Group
import com.mifos.room.entities.group.GroupPayload
import com.mifos.room.entities.group.GroupWithAssociations
import com.mifos.room.helper.GroupsDaoHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This DataManager is for Managing Groups API, In which Request is going to Server
 * and In Response, We are getting Groups API Observable Response using Retrofit2.
 * DataManagerGroups saving response in Database and response to Presenter as accordingly
 * Created by Rajan Maurya on 28/06/16.
 */
@Singleton
class DataManagerGroups @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    private val databaseHelperGroups: GroupsDaoHelper,
    private val mDatabaseHelperClient: DatabaseHelperClient,
    private val baseApiManager: org.mifos.core.apimanager.BaseApiManager,
    private val prefManager: com.mifos.core.datastore.PrefManager,
) {
    /**
     * This Method sending the Request to REST API if UserStatus is 0 and
     * get list of the groups. The response is pass to the DatabaseHelperGroups
     * that save the response in Database in different thread and next pass the response to
     * Presenter to show in the view
     *
     *
     * If the offset is zero and UserStatus is 1 then fetch all clients list and show on the view.
     * else if offset is not zero and UserStatus is 1 then return default empty response to
     * presenter
     *
     * @param paged  True Enable the Pagination of the client list REST API
     * @param offset Value give from which position Fetch GroupsList
     * @param limit  Maximum Number of clients will come in response
     * @return Groups List page from offset to max Limit
     */
    suspend fun getGroups(paged: Boolean, offset: Int, limit: Int): Page<Group> {
        return when (prefManager.userStatus) {
            false -> baseApiManager.getGroupApi().retrieveAll24(
                null,
                null,
                null,
                null,
                null,
                paged,
                offset,
                limit,
                null,
                null,
                null,
            ).let(GetGroupsResponseMapper::mapFromEntity)

            true ->
                /**
                 * offset : is the value from which position we want to fetch the list, It means
                 * if offset is 0 and User is in the Offline Mode So fetch all groups
                 * Return All Groups List from DatabaseHelperGroups only one time.
                 * If offset is zero this means this is first request and
                 * return all clients from DatabaseHelperClient
                 */
                databaseHelperGroups.readAllGroups(offset, limit)
        }
    }
//    suspend fun getGroups(paged: Boolean, offset: Int, limit: Int): Observable<Page<Group>> {
//        return when (prefManager.userStatus) {
//            false -> baseApiManager.getGroupApi().retrieveAll24(
//                null,
//                null,
//                null,
//                null,
//                null,
//                paged,
//                offset,
//                limit,
//                null,
//                null,
//                null
//            ).map(GetGroupsResponseMapper::mapFromEntity)
//
//            true -> {
//                /**
//                 * offset : is the value from which position we want to fetch the list, It means
//                 * if offset is 0 and User is in the Offline Mode So fetch all groups
//                 * Return All Groups List from DatabaseHelperGroups only one time.
//                 * If offset is zero this means this is first request and
//                 * return all clients from DatabaseHelperClient
//                 */
//                mDatabaseHelperGroups.readAllGroups(offset, limit)
//            }
//        }
//    }

    /**
     * This method call the DatabaseHelperGroups Helper and mDatabaseHelperGroups.readAllGroups()
     * read the all Groups from the Database Group table and returns the Page<Group>.
     *
     * @return Page<Group>
     </Group></Group> */
    val databaseGroups: Flow<Page<Group>>
        get() = databaseHelperGroups.readAllGroups()

    /**
     * This Method fetch the Group from REST API if the user status is 0,
     * and if user status is 1 then load the Group from Database with groupId.
     *
     * @param groupId Group Id
     * @return Group
     */
    fun getGroup(groupId: Int): Flow<Group> {
        return when (prefManager.userStatus) {
            false -> flow { emit(mBaseApiManager.groupApi.getGroup(groupId)) }
            true ->
                /**
                 * Return Groups from DatabaseHelperGroups.
                 */
                databaseHelperGroups.getGroup(groupId)
        }
    }

    /**
     * This method save the single Group in Database.
     *
     * @param group Group
     * @return Group
     */
    suspend fun syncGroupInDatabase(group: Group) {
        return databaseHelperGroups.saveGroup(group)
    }

    /**
     * This Method Fetch the Clients that are attached to the Group.
     * @param groupId Group Id
     * @return GroupWithAssociations
     */
    fun getGroupWithAssociations(groupId: Int): Observable<GroupWithAssociations> {
        return when (prefManager.userStatus) {
            false -> mBaseApiManager.groupApi.getGroupWithAssociations(groupId)
            true ->
                /**
                 * Return Groups from DatabaseHelperGroups.
                 */
                mDatabaseHelperClient.getGroupAssociateClients(groupId)
        }
    }

    /**
     * This method fetch the Group Accounts if the User status is zero and otherwise load the
     * Group Accounts from the Database with the Group Id.
     *
     * @param groupId Group Id
     * @return GroupAccounts
     */
    fun getGroupAccounts(groupId: Int): Flow<GroupAccounts> {
        return when (prefManager.userStatus) {
            false -> flow { emit(mBaseApiManager.groupApi.getGroupAccounts(groupId)) }
            true ->
                /**
                 * Return Groups from DatabaseHelperGroups.
                 */
                databaseHelperGroups.readGroupAccounts(groupId)
        }
    }

    /**
     * This Method Fetching the Group Accounts (Loan, saving, etc Accounts ) from REST API
     * and then Saving all Accounts into the Database and then returns the Same Group Accounts
     *
     * @param groupId Group Id
     * @return GroupAccounts
     */
    fun syncGroupAccounts(groupId: Int): Flow<GroupAccounts> = flow {
        val groupAccounts = mBaseApiManager.groupApi.getGroupAccounts(groupId)
        databaseHelperGroups.saveGroupAccounts(groupAccounts, groupId)
        emit(groupAccounts)
    }

    /**
     * This method creating the Group if user status is zero otherwise saving the GroupPayload in
     * the Database.
     *
     * @param groupPayload GroupPayload
     * @return Group
     */
    suspend fun createGroup(groupPayload: GroupPayload): SaveResponse {
        return when (prefManager.userStatus) {
            false -> mBaseApiManager.groupApi.createGroup(groupPayload)

            true ->
                /**
                 * Save GroupPayload in Database table.
                 */
                databaseHelperGroups.saveGroupPayload(groupPayload)
        }
    }

    /**
     * This method loading the all GroupPayloads from the Database.
     *
     * @return List<GroupPayload>
     </GroupPayload> */
    val allDatabaseGroupPayload: Flow<List<GroupPayload>>
        get() = databaseHelperGroups.realAllGroupPayload()

    /**
     * This method will called when user is syncing the Database group.
     * whenever a group is synced then request goes to Database to delete that group form
     * Database and reload the list from Database and update the list in UI
     *
     * @param id of the groupPayload in Database
     * @return List<GroupPayload></GroupPayload>>
     */
    fun deleteAndUpdateGroupPayloads(id: Int): Flow<List<GroupPayload>> {
        return databaseHelperGroups.deleteAndUpdateGroupPayloads(id)
    }

    /**
     * This Method updating the GroupPayload in Database and return the same GroupPayload
     *
     * @param groupPayload GroupPayload
     * @return GroupPayload
     */
    suspend fun updateGroupPayload(groupPayload: GroupPayload) {
        databaseHelperGroups.updateGroupPayload(groupPayload)
    }

    /**
     * This method is activating the Group
     *
     * @param groupId
     * @return GenericResponse
     */
    fun activateGroup(
        groupId: Int,
        activatePayload: ActivatePayload?,
    ): Observable<GenericResponse> {
        return mBaseApiManager.groupApi.activateGroup(groupId, activatePayload)
    }
}
