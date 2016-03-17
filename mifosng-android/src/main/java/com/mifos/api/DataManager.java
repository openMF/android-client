/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api;

import com.mifos.objects.User;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.List;

import rx.Observable;

/**
 * Created by Rajan Maurya on 16/3/16.
 */
public class DataManager {


    BaseApiManager mBaseApiManager = new BaseApiManager();

    public DataManager(){

    }

    /**
     * @param username Username
     * @param password  Password
     * @return  Basic OAuth
     */
    public Observable<User> login(String username, String password) {
        return mBaseApiManager.getAuthApi().authenticate(username,password);
    }

    /**
     * @return List of All Centers
     */
    public Observable<List<Center>> getCenters(){
        return mBaseApiManager.getCenterApi().getAllCenters();
    }

    public Observable<CenterWithAssociations> getCentersGroupAndMeeting(int centerId){
        return mBaseApiManager.getCenterApi().getCenterWithGroupMembersAndCollectionMeetingCalendar(centerId);
    }

    /**
     *
     * @param clientId User Client ID
     * @return
     */
    public Observable<Page<Charges>> getClientChargesList(int clientId){
        return mBaseApiManager.getChargeService().getListOfCharges(clientId);
    }

    public Observable<>
}
