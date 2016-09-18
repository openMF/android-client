package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperGroups;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupPayload;
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

    @Inject
    public DataManagerGroups(BaseApiManager baseApiManager,
                             DatabaseHelperGroups databaseHelperGroups) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperGroups = databaseHelperGroups;
    }

    /**
     * This Method sending the Request to REST API if UserStatus is 0 and
     * get list of the groups. The response is pass to the DatabaseHelperGroups
     * that save the response in Database in different thread and next pass the response to
     * Presenter to show in the view
     * <p/>
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
                return mBaseApiManager.getGroupApi().getGroups(paged, offset, limit)
                        .concatMap(new Func1<Page<Group>, Observable<? extends Page<Group>>>() {
                            @Override
                            public Observable<? extends Page<Group>> call(Page<Group> groupPage) {
                                return Observable.just(groupPage);
                            }
                        });
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


    public Observable<Group> createGroup(GroupPayload groupPayload) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getGroupApi().createGroup(groupPayload)
                        .concatMap(new Func1<Group, Observable<? extends Group>>() {
                            @Override
                            public Observable<? extends Group> call(Group group) {
                                return Observable.just(group);
                            }
                        });
            case 1:
                /**
                 * offset : is the value from which position we want to fetch the list, It means
                 * if offset is 0 and User is in the Offline Mode So fetch all groups
                 * Return All Groups List from DatabaseHelperGroups only one time.
                 * If offset is zero this means this is first request and
                 * return all clients from DatabaseHelperClient
                 */
                return mDatabaseHelperGroups.saveGroupPayload(groupPayload);

            default:
                return Observable.just(new Group());
        }
    }

    public Observable<List<GroupPayload>> getAllDatabaseGroupPayload() {
        return mDatabaseHelperGroups.realAllGroupPayload();
    }

    /**
     * This method will called when user is syncing the group created from Database.
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

    public Observable<Group> getGroup(int groupId) {
        return mBaseApiManager.getGroupApi().getGroup(groupId);
    }

    public Observable<GroupAccounts> getGroupAccounts(int groupId) {
        return mBaseApiManager.getGroupApi().getGroupAccounts(groupId);
    }

    public Observable<Group> syncGroupInDatabase(Group group) {
        return mDatabaseHelperGroups.saveGroup(group);
    }
}
