package com.mifos.api;

import com.mifos.objects.User;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Rajan Maurya on 4/6/16.
 */

@Singleton
public class DataManager {

    private final BaseApiManager mBaseApiManager;

    @Inject
    public DataManager(BaseApiManager baseApiManager) {

        mBaseApiManager = baseApiManager;
    }

    /**
     * @param username Username
     * @param password  Password
     * @return  Basic OAuth
     */
    public Observable<User> login(String username, String password) {
        return mBaseApiManager.getAuthApi().authenticate(username, password);
    }

    /**
     * Center API
     */


    //Return Centers List
    public Observable<List<Center>> getCenters() {
        return mBaseApiManager.getCenterApi().getAllCenters();
    }

    //Return Center With Association
    public Observable<CenterWithAssociations> getCentersGroupAndMeeting(int id) {
        return mBaseApiManager
                .getCenterApi()
                .getCenterWithGroupMembersAndCollectionMeetingCalendar(id);
    }

}
