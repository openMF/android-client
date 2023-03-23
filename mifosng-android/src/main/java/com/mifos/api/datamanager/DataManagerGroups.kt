package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.api.GenericResponse
import com.mifos.api.local.databasehelper.DatabaseHelperClient
import com.mifos.api.local.databasehelper.DatabaseHelperGroups
import com.mifos.objects.accounts.GroupAccounts
import com.mifos.objects.client.ActivatePayload
import com.mifos.objects.client.Page
import com.mifos.objects.group.Group
import com.mifos.objects.group.GroupPayload
import com.mifos.objects.group.GroupWithAssociations
import com.mifos.objects.response.SaveResponse
import com.mifos.utils.PrefManager
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This DataManager is for Managing Groups API, In which Request is going to Server
 * and In Response, We are getting Groups API Observable Response using Retrofit2.
 * DataManagerGroups saving response in Database and response to Presenter as accordingly
 * Created by Atharv Tare on 23/03/23.
 */
@Singleton
class DataManagerGroups @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    val mDatabaseHelperGroups: DatabaseHelperGroups,
    val mDatabaseHelperClient: DatabaseHelperClient
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
    fun getGroups(paged: Boolean, offset: Int, limit: Int): Observable<Page<Group>> {
        return when (PrefManager.getUserStatus()) {
            0 -> mBaseApiManager.groupApi.getGroups(paged, offset, limit)
            1 -> {
                /**
                 * offset : is the value from which position we want to fetch the list, It means
                 * if offset is 0 and User is in the Offline Mode So fetch all groups
                 * Return All Groups List from DatabaseHelperGroups only one time.
                 * If offset is zero this means this is first request and
                 * return all clients from DatabaseHelperClient
                 */
                if (offset == 0) mDatabaseHelperGroups.readAllGroups() else Observable.just(Page())
            }
            else -> Observable.just(Page())
        }
    }

    /**
     * This method call the DatabaseHelperGroups Helper and mDatabaseHelperGroups.readAllGroups()
     * read the all Groups from the Database Group table and returns the Page<Group>.
     *
     * @return Page<Group>
    </Group></Group> */
    val databaseGroups: Observable<Page<Group>>
        get() = mDatabaseHelperGroups.readAllGroups()

    /**
     * This Method fetch the Group from REST API if the user status is 0,
     * and if user status is 1 then load the Group from Database with groupId.
     *
     * @param groupId Group Id
     * @return Group
     */
    fun getGroup(groupId: Int): Observable<Group> {
        return when (PrefManager.getUserStatus()) {
            0 -> mBaseApiManager.groupApi.getGroup(groupId)
            1 ->
                /**
                 * Return Groups from DatabaseHelperGroups.
                 */
                mDatabaseHelperGroups.getGroup(groupId)
            else -> Observable.just(Group())
        }
    }

    /**
     * This method save the single Group in Database.
     *
     * @param group Group
     * @return Group
     */
    fun syncGroupInDatabase(group: Group?): Observable<Group> {
        return mDatabaseHelperGroups.saveGroup(group)
    }

    /**
     * This Method Fetch the Clients that are attached to the Group.
     * @param groupId Group Id
     * @return GroupWithAssociations
     */
    fun getGroupWithAssociations(groupId: Int): Observable<GroupWithAssociations> {
        return when (PrefManager.getUserStatus()) {
            0 -> mBaseApiManager.groupApi.getGroupWithAssociations(groupId)
            1 ->
                /**
                 * Return Groups from DatabaseHelperGroups.
                 */
                mDatabaseHelperClient.getGroupAssociateClients(groupId)
            else -> Observable.just(GroupWithAssociations())
        }
    }

    /**
     * This method fetch the Group Accounts if the User status is zero and otherwise load the
     * Group Accounts from the Database with the Group Id.
     *
     * @param groupId Group Id
     * @return GroupAccounts
     */
    fun getGroupAccounts(groupId: Int): Observable<GroupAccounts> {
        return when (PrefManager.getUserStatus()) {
            0 -> mBaseApiManager.groupApi.getGroupAccounts(groupId)
            1 ->
                /**
                 * Return Groups from DatabaseHelperGroups.
                 */
                mDatabaseHelperGroups.readGroupAccounts(groupId)
            else -> Observable.just(GroupAccounts())
        }
    }

    /**
     * This Method Fetching the Group Accounts (Loan, saving, etc Accounts ) from REST API
     * and then Saving all Accounts into the Database and then returns the Same Group Accounts
     *
     * @param groupId Group Id
     * @return GroupAccounts
     */
    fun syncGroupAccounts(groupId: Int): Observable<GroupAccounts> {
        return mBaseApiManager.groupApi.getGroupAccounts(groupId)
            .concatMap { groupAccounts ->
                mDatabaseHelperGroups.saveGroupAccounts(
                    groupAccounts,
                    groupId
                )
            }
    }

    /**
     * This method creating the Group if user status is zero otherwise saving the GroupPayload in
     * the Database.
     *
     * @param groupPayload GroupPayload
     * @return Group
     */
    fun createGroup(groupPayload: GroupPayload?): Observable<SaveResponse> {
        return when (PrefManager.getUserStatus()) {
            0 -> mBaseApiManager.groupApi.createGroup(groupPayload)
            1 ->
                /**
                 * Save GroupPayload in Database table.
                 */
                mDatabaseHelperGroups.saveGroupPayload(groupPayload)
            else -> Observable.just(SaveResponse())
        }
    }

    /**
     * This method loading the all GroupPayloads from the Database.
     *
     * @return List<GroupPayload>
    </GroupPayload> */
    val allDatabaseGroupPayload: Observable<List<GroupPayload>>
        get() = mDatabaseHelperGroups.realAllGroupPayload()

    /**
     * This method will called when user is syncing the Database group.
     * whenever a group is synced then request goes to Database to delete that group form
     * Database and reload the list from Database and update the list in UI
     *
     * @param id of the groupPayload in Database
     * @return List<GroupPayload></GroupPayload>>
     */
    fun deleteAndUpdateGroupPayloads(id: Int): Observable<List<GroupPayload>> {
        return mDatabaseHelperGroups.deleteAndUpdateGroupPayloads(id)
    }

    /**
     * This Method updating the GroupPayload in Database and return the same GroupPayload
     *
     * @param groupPayload GroupPayload
     * @return GroupPayload
     */
    fun updateGroupPayload(groupPayload: GroupPayload?): Observable<GroupPayload> {
        return mDatabaseHelperGroups.updateDatabaseGroupPayload(groupPayload)
    }

    /**
     * This method is activating the Group
     *
     * @param groupId
     * @return GenericResponse
     */
    fun activateGroup(
        groupId: Int,
        activatePayload: ActivatePayload?
    ): Observable<GenericResponse> {
        return mBaseApiManager.groupApi.activateGroup(groupId, activatePayload)
    }
}
