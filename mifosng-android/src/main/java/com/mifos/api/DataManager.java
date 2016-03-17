/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api;

import com.mifos.api.model.CollectionSheetPayload;
import com.mifos.api.model.Payload;
import com.mifos.api.model.SaveResponse;
import com.mifos.objects.SearchedEntity;
import com.mifos.objects.User;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.noncore.DataTable;
import com.mifos.objects.noncore.Identifier;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.services.data.CenterPayload;

import java.util.List;

import retrofit.client.Response;
import retrofit.mime.TypedFile;
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

    public Observable<Response> deleteClientImage(int clientid){
        return mBaseApiManager.getClientsApi().deleteClientImage(clientid);
    }

    public Observable<Response> uploadclientimage(int clientid , TypedFile typedFile){
        return mBaseApiManager.getClientsApi().uploadClientImage(clientid, typedFile);
    }

    public Observable<Client> getclientdetails(int clientid){
        return mBaseApiManager.getClientsApi().getClient(clientid);
    }

    public Observable<ClientAccounts> getclientaccount(int clientid){
        return mBaseApiManager.getAccountsApi().getAllAccountsOfClient(clientid);
    }

    public Observable<List<DataTable>> getClientDataTable(String m_client){
        return mBaseApiManager.getDataTableApi().getTableOf(m_client);
    }

    public Observable<List<Identifier>> getListOfIdentifiers(int clientid){
        return mBaseApiManager.getIdentifierApi().getListOfIdentifiers(clientid);
    }

    public Observable<Page<Client>> getClientList(){
        return mBaseApiManager.getClientsApi().listAllClients();
    }

    public Observable<Page<Client>> getClientList(int offset, int limit){
        return mBaseApiManager.getClientsApi().listAllClients(offset,limit);
    }

    public Observable<List<SearchedEntity>> searchClientsByName(String name){
        return mBaseApiManager.getSearchApi().searchClientsByName(name);
    }

    public Observable<CollectionSheet> getCallectionSheet(long centerid , Payload payload){
        return mBaseApiManager.getCenterApi().getCollectionSheet(centerid, payload);
    }

    public Observable<SaveResponse> savecallectionsheetAsync(int centerid , CollectionSheetPayload collectionSheetPayload){
        return mBaseApiManager.getCenterApi().saveCollectionSheetAsync(centerid, collectionSheetPayload);
    }

    public Observable<List<Office>> getAllOffices(){
        return mBaseApiManager.getOfficeApi().getAllOffices();
    }

    public Observable<List<Staff>> getStaffForOffice(int officeid){
        return mBaseApiManager.getStaffApi().getStaffForOffice(officeid);
    }

    public Observable<Center> createCenter(CenterPayload centerPayload){
        return mBaseApiManager.getCenterApi().createCenter(centerPayload);
    }
}
