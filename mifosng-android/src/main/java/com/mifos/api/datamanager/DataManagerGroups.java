package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.GenericResponse;
import com.mifos.api.local.databasehelper.DatabaseHelperClient;
import com.mifos.api.local.databasehelper.DatabaseHelperGroups;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.client.ActivatePayload;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupPayload;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.objects.response.SaveResponse;
import com.mifos.utils.PrefManager;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * This DataManager is for Managing Groups API, In which Request is going to Server
 * and In Response, We are getting Groups API Observable Response using Retrofit2.
 * DataManagerGroups saving response in Database and response to Presenter as accordingly
 * Created by Rajan Maurya on 28/06/16.
 */
@Singleton
public class DataManagerGroups {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperGroups mDatabaseHelperGroups;
    public final DatabaseHelperClient mDatabaseHelperClient;

    @Inject
    public DataManagerGroups(BaseApiManager baseApiManager,
                             DatabaseHelperGroups databaseHelperGroups,
                             DatabaseHelperClient databaseHelperClient) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperGroups = databaseHelperGroups;
        mDatabaseHelperClient = databaseHelperClient;
    }

    /**
     * This Method sending the Request to REST API if UserStatus is 0 and
     * get list of the groups. The response is pass to the DatabaseHelperGroups
     * that save the response in Database in different thread and next pass the response to
     * Presenter to show in the view
     * <p>
     * If the offset is zero and UserStatus is 1 then fetch all clients list and show on the view.
     * else if offset is not zero and UserStatus is 1 then return default empty response to
     * presenter
     *
     * @param paged  True Enable the Pagination of the client list REST API
     * @param offset Value give from which position Fetch GroupsList
     * @param limit  Maximum Number of clients will come in response
     * @return Groups List page from offset to max Limit
     */
    public Observable<Page<Group>> getGroups(boolean paged, int offset, int limit) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getGroupApi().getGroups(paged, offset, limit);
            case 1:
                /**
                 * offset : is the value from which position we want to fetch the list, It means
                 * if offset is 0 and User is in the Offline Mode So fetch all groups
                 * Return All Groups List from DatabaseHelperGroups only one time.
                 * If offset is zero this means this is first request and
                 * return all clients from DatabaseHelperClient
                 */
                if (offset == 0)
                    return mDatabaseHelperGroups.readAllGroups();

            default:
                return Observable.just(new Page<Group>());
        }
    }

    /**
     * This method call the DatabaseHelperGroups Helper and mDatabaseHelperGroups.readAllGroups()
     * read the all Groups from the Database Group table and returns the Page<Group>.
     *
     * @return Page<Group>
     */
    public Observable<Page<Group>> getDatabaseGroups() {
        return mDatabaseHelperGroups.readAllGroups();
    }

    /**
     * This Method fetch the Group from REST API if the user status is 0,
     * and if user status is 1 then load the Group from Database with groupId.
     *
     * @param groupId Group Id
     * @return Group
     */
    public Observable<Group> getGroup(int groupId) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getGroupApi().getGroup(groupId);
            case 1:
                /**
                 * Return Groups from DatabaseHelperGroups.
                 */
                return mDatabaseHelperGroups.getGroup(groupId);

            default:
                return Observable.just(new Group());
        }
    }

    /**
     * This method save the single Group in Database.
     *
     * @param group Group
     * @return Group
     */
    public Observable<Group> syncGroupInDatabase(Group group) {
        return mDatabaseHelperGroups.saveGroup(group);
    }

    /**
     * This Method Fetch the Clients that are attached to the Group.
     * @param groupId Group Id
     * @return GroupWithAssociations
     */
    public Observable<GroupWithAssociations> getGroupWithAssociations(int groupId) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getGroupApi().getGroupWithAssociations(groupId);
            case 1:
                /**
                 * Return Groups from DatabaseHelperGroups.
                 */
                return mDatabaseHelperClient.getGroupAssociateClients(groupId);

            default:
                return Observable.just(new GroupWithAssociations());
        }
    }

    /**
     * This method fetch the Group Accounts if the User status is zero and otherwise load the
     * Group Accounts from the Database with the Group Id.
     *
     * @param groupId Group Id
     * @return GroupAccounts
     */
    public Observable<GroupAccounts> getGroupAccounts(int groupId) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getGroupApi().getGroupAccounts(groupId);
            case 1:
                /**
                 * Return Groups from DatabaseHelperGroups.
                 */
                return mDatabaseHelperGroups.readGroupAccounts(groupId);

            default:
                return Observable.just(new GroupAccounts());
        }
    }

    /**
     * This Method Fetching the Group Accounts (Loan, saving, etc Accounts ) from REST API
     * and then Saving all Accounts into the Database and then returns the Same Group Accounts
     *
     * @param groupId Group Id
     * @return GroupAccounts
     */
    public Observable<GroupAccounts> syncGroupAccounts(final int groupId) {
        return mBaseApiManager.getGroupApi().getGroupAccounts(groupId)
                .concatMap(new Func1<GroupAccounts, Observable<? extends GroupAccounts>>() {
                    @Override
                    public Observable<? extends GroupAccounts> call(GroupAccounts
                                                                            groupAccounts) {
                        return mDatabaseHelperGroups.saveGroupAccounts(groupAccounts, groupId);
                    }
                });
    }

    /**
     * This method creating the Group if user status is zero otherwise saving the GroupPayload in
     * the Database.
     *
     * @param groupPayload GroupPayload
     * @return Group
     */
    public Observable<SaveResponse> createGroup(GroupPayload groupPayload) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getGroupApi().createGroup(groupPayload);
            case 1:
                /**
                 * Save GroupPayload in Database table.
                 */
                return mDatabaseHelperGroups.saveGroupPayload(groupPayload);

            default:
                return Observable.just(new SaveResponse());
        }
    }

    /**
     * This method loading the all GroupPayloads from the Database.
     *
     * @return List<GroupPayload>
     */
    public Observable<List<GroupPayload>> getAllDatabaseGroupPayload() {
        return mDatabaseHelperGroups.realAllGroupPayload();
    }

    /**
     * This method will called when user is syncing the Database group.
     * whenever a group is synced then request goes to Database to delete that group form
     * Database and reload the list from Database and update the list in UI
     *
     * @param id of the groupPayload in Database
     * @return List<GroupPayload></>
     */
    public Observable<List<GroupPayload>> deleteAndUpdateGroupPayloads(int id) {
        return mDatabaseHelperGroups.deleteAndUpdateGroupPayloads(id);
    }


    /**
     * This Method updating the GroupPayload in Database and return the same GroupPayload
     *
     * @param groupPayload GroupPayload
     * @return GroupPayload
     */
    public Observable<GroupPayload> updateGroupPayload(GroupPayload groupPayload) {
        return mDatabaseHelperGroups.updateDatabaseGroupPayload(groupPayload);
    }

    /**
     * This method is activating the Group
     *
     * @param groupId
     * @return GenericResponse
     */
    public Observable<GenericResponse> activateGroup(int groupId,
                                                      ActivatePayload activatePayload) {
        return mBaseApiManager.getGroupApi().activateGroup(groupId, activatePayload);
    }
}
