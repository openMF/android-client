package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperGroups;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;
import com.mifos.utils.PrefManager;

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
                /*if (offset == 0)
                    return mDatabaseHelperClient.readAllClients();*/

            default:
                return Observable.just(new Page<Group>());
        }
    }

}
