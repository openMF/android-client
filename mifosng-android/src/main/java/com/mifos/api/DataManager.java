package com.mifos.api;

import com.mifos.objects.SearchedEntity;
import com.mifos.objects.User;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.objects.noncore.Document;
import com.mifos.objects.noncore.Identifier;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.services.data.CenterPayload;
import com.mifos.services.data.GroupPayload;

import java.util.List;
import java.util.Map;

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

    public Observable<CenterWithAssociations> getGroupsByCenter(int id) {
        return mBaseApiManager.getCenterApi().getAllGroupsForCenter(id);
    }

    public Observable<List<Center>> getCentersInOffice(int id, Map<String, Object> params) {
        return mBaseApiManager.getCenterApi().getAllCentersInOffice(id, params);
    }

    public Observable<Center> createCenter(CenterPayload centerPayload) {
        return mBaseApiManager.getCenterApi().createCenter(centerPayload);
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


    /**
     * Documents API
     */
    public Observable<List<Document>> getDocumentsList(String type, int id) {
        return mBaseApiManager.getDocumentApi().getListOfDocuments(type, id);
    }


    /**
     * Groups API
     */
    public Observable<GroupWithAssociations> getGroups(int groupid) {
        return mBaseApiManager.getGroupApi().getGroupWithAssociations(groupid);
    }

    public Observable<List<Group>> getGroupsByOffice(int office, Map<String, Object> params) {
        return mBaseApiManager.getGroupApi().getAllGroupsInOffice(office, params);
    }

    public Observable<Group> createGroup(GroupPayload groupPayload) {
        return mBaseApiManager.getGroupApi().createGroup(groupPayload);
    }


    /**
     * Offices API
     */
    public Observable<List<Office>> getOffices() {
        return mBaseApiManager.getOfficeApi().getAllOffices();
    }


    /**
     * Staff API
     */
    public Observable<List<Staff>> getStaffInOffice(int officeId) {
        return mBaseApiManager.getStaffApi().getStaffForOffice(officeId);
    }


}
