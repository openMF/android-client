package com.mifos.api;

import com.mifos.objects.SearchedEntity;
import com.mifos.objects.User;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.noncore.Identifier;

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


    /**
     * Charges API
     */
    public Observable<Page<Charges>> getClientCharges(int id) {
        return mBaseApiManager.getChargeApi().getListOfCharges(id);
    }


    /**
     * Client API
     */
    public Observable<Page<Client>> getAllClients() {
        return mBaseApiManager.getClientsApi().getAllClients();
    }

    public Observable<Page<Client>> getAllClients(int offset, int limit) {
        return mBaseApiManager.getClientsApi().getAllClients(offset, limit);
    }

    /**
     * Identifiers API
     */

    public Observable<List<Identifier>> getIdentifiers(int clientid) {
        return mBaseApiManager.getIdentifierApi().getListOfIdentifiers(clientid);
    }

    /**
     * Search API
     */
    public Observable<List<SearchedEntity>> searchClientsByName(String query) {
        return mBaseApiManager.getSearchApi().searchClientsByName(query);
    }


}
