package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.GenericResponse;
import com.mifos.api.local.databasehelper.DatabaseHelperClient;
import com.mifos.api.mappers.accounts.GetClientsClientIdAccsMapper;
import com.mifos.api.mappers.client.ClientsClientIdResponseMapper;
import com.mifos.api.mappers.client.GetClientResponseMapper;
import com.mifos.api.mappers.client.PostClientRequestMapper;
import com.mifos.api.mappers.client.PostClientResponseMapper;
import com.mifos.api.mappers.client.identifiers.GetIdentifiersTemplateMapper;
import com.mifos.api.mappers.client.identifiers.IdentifierCreationResponseMapper;
import com.mifos.api.mappers.client.identifiers.IdentifierMapper;
import com.mifos.api.mappers.client.identifiers.PostIdentifierMapper;
import com.mifos.api.mappers.client.template.TemplateMapper;
import com.mifos.api.utils.DataTables;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.ActivatePayload;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.ClientAddressRequest;
import com.mifos.objects.client.ClientAddressResponse;
import com.mifos.objects.client.ClientPayload;
import com.mifos.objects.client.Page;
import com.mifos.objects.noncore.Identifier;
import com.mifos.objects.noncore.IdentifierCreationResponse;
import com.mifos.objects.noncore.IdentifierPayload;
import com.mifos.objects.noncore.IdentifierTemplate;
import com.mifos.objects.templates.clients.ClientsTemplate;
import com.mifos.utils.PrefManager;

import org.apache.fineract.client.models.PostClientsClientIdIdentifiersRequest;
import org.apache.fineract.client.models.PostClientsRequest;
import org.apache.fineract.client.services.ClientApi;
import org.apache.fineract.client.services.ClientIdentifierApi;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import rx.Observable;

/**
 * This DataManager is for Managing Client API, In which Request is going to Server
 * and In Response, We are getting Client API Observable Response using Retrofit2 .
 * Created by Rajan Maurya on 24/06/16.
 */
@Singleton
public class DataManagerClient {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperClient mDatabaseHelperClient;
    public final org.mifos.core.apimanager.BaseApiManager sdkBaseApiManager;

    @Inject
    public DataManagerClient(BaseApiManager baseApiManager,
                             DatabaseHelperClient databaseHelperClient,
                             org.mifos.core.apimanager.BaseApiManager sdkBaseApiManager) {

        mBaseApiManager = baseApiManager;
        mDatabaseHelperClient = databaseHelperClient;
        this.sdkBaseApiManager = sdkBaseApiManager;
    }

    private ClientApi getClientApi() {
        return sdkBaseApiManager.getClientsApi();
    }

    private ClientIdentifierApi getClientIdentifierApi() {
        return sdkBaseApiManager.getClient().clientIdentifiers;
    }

    /**
     * This Method sending the Request to REST API if UserStatus is 0 and
     * get list of the clients. The response is pass to the DatabaseHelperClient
     * that save the response in Database different thread and next pass the response to
     * Presenter to show in the view
     * <p/>
     * If the offset is zero and UserStatus is 1 then fetch all clients list and show on the view.
     * else if offset is not zero and UserStatus is 1 then return default empty response to
     * presenter
     *
     * @param paged  True Enable the Pagination of the client list REST API
     * @param offset Value give from which position Fetch ClientList
     * @param limit  Maximum Number of clients will come in response
     * @return Client List from offset to max Limit
     */
    public Observable<Page<Client>> getAllClients(boolean paged, int offset, int limit) {
        switch (PrefManager.INSTANCE.getUserStatus()) {
            case 0:
                return getClientApi().retrieveAll21(null, null, null,
                        null, null, null,
                        null, null, offset,
                        limit, null, null, null)
                        .map(GetClientResponseMapper.INSTANCE::mapFromEntity)
                        .concatMap(Observable::just);
            case 1:
                /**
                 * Return All Clients List from DatabaseHelperClient only one time.
                 * If offset is zero this means this is first request and
                 * return all clients from DatabaseHelperClient
                 */
                if (offset == 0)
                    return mDatabaseHelperClient.readAllClients();

            default:
                return Observable.just(new Page<Client>());
        }
    }


    /**
     * This Method Request to the DatabaseHelperClient and DatabaseHelperClient Read the All
     * clients from Client_Table and give the response Page of List of Client
     *
     * @return Page of Client List
     */
    public Observable<Page<Client>> getAllDatabaseClients() {
        return mDatabaseHelperClient.readAllClients();
    }


    /**
     * This Method
     *
     * @param clientId for Query in database or REST API request.
     * @return The Client Details
     */
    public Observable<Client> getClient(int clientId) {
        switch (PrefManager.INSTANCE.getUserStatus()) {
            case 0:
                return getClientApi().retrieveOne11((long) clientId, false)
                        .map(ClientsClientIdResponseMapper.INSTANCE::mapFromEntity)
                        .concatMap(Observable::just);
            case 1:
                /**
                 * Return Clients from DatabaseHelperClient only one time.
                 */
                return mDatabaseHelperClient.getClient(clientId);

            default:
                return Observable.just(new Client());
        }
    }


    public Observable<Client> syncClientInDatabase(Client client) {
        return mDatabaseHelperClient.saveClient(client);
    }

    /**
     * This Method Checks the User Status and as accordingly call to Database Helper or Client
     * Retrofit Service.
     * If User is Online then this Client DataManager Request to Client Retrofit to make REST API
     * Request for Client Account.
     * If User if Offline Client DataManager request to Client DatabaseHelper to make Transaction
     * for retrieving the Clients Account.
     *
     * @param clientId Client Id
     * @return All Clients Account, Like Savings, Loan etc Accounts.
     */
    public Observable<ClientAccounts> getClientAccounts(final int clientId) {
        switch (PrefManager.INSTANCE.getUserStatus()) {
            case 0:
                return getClientApi().retrieveAssociatedAccounts((long) clientId)
                        .map(GetClientsClientIdAccsMapper.INSTANCE::mapFromEntity);
            case 1:
                /**
                 * Return Clients from DatabaseHelperClient only one time.
                 */
                return mDatabaseHelperClient.realClientAccounts(clientId);

            default:
                return Observable.just(new ClientAccounts());
        }
    }


    /**
     * This Method Fetching the Client Accounts (Loan, saving, etc Accounts ) from REST API
     * and get the ClientAccounts and then Saving all Accounts into the Database and give the
     * ClientAccount in return.
     *
     * @param clientId Client Id
     * @return ClientAccounts
     */
    public Observable<ClientAccounts> syncClientAccounts(final int clientId) {
        return getClientApi().retrieveAssociatedAccounts((long) clientId)
                .map(GetClientsClientIdAccsMapper.INSTANCE::mapFromEntity)
                .concatMap(clientAccounts ->
                        mDatabaseHelperClient.saveClientAccounts(clientAccounts, clientId));
    }

    /**
     * This Method for removing the Client Image from his profile on server
     * if its response is true the client does not have any profile Image and if
     * response is false then failed to update the client image from server profile.
     * There can any problem during updating the client image like Network error.
     *
     * @param clientId Client ID
     * @return ResponseBody is the Retrofit 2 response
     */
    public Observable<ResponseBody> deleteClientImage(int clientId) {
        return mBaseApiManager.getClientsApi().deleteClientImage(clientId);
    }


    /**
     * This Method will be called when ever user want to update the client profile image.
     * The response of the updating the client image can be true or false its depends upon the
     * network and right choice image file.
     *
     * @param id   Client Id
     * @param file MultipartBody of the Image file
     * @return ResponseBody is the Retrofit 2 response
     */
    public Observable<ResponseBody> uploadClientImage(int id, MultipartBody.Part file) {
        return mBaseApiManager.getClientsApi().uploadClientImage(id, file);
    }


    /**
     * This Method will be called when ever user create the client. if user is in online mode
     * then request goes to the server to get the client template and in response client template
     * saves in table in background and return the Observable.just(clientTemplate) to the presenter
     * and if user in offline mode then we load the client Template from Database.
     *
     * @return ClientTemplate
     */
    public Observable<ClientsTemplate> getClientTemplate() {
        switch (PrefManager.INSTANCE.getUserStatus()) {
            case 0:
                return sdkBaseApiManager.getClientsApi()
                        .retrieveTemplate5(null, null, null)
                        .map(TemplateMapper.INSTANCE::mapFromEntity)
                        .concatMap(mDatabaseHelperClient::saveClientTemplate);
            case 1:
                /**
                 * Return Clients from DatabaseHelperClient only one time.
                 */
                return mDatabaseHelperClient.readClientTemplate();

            default:
                return Observable.just(new ClientsTemplate());
        }

    }


    /**
     * This Method create the client by making directly request to server when User is Online
     * and give response client type is created otherwise give error string.
     * if user if offline mode then client details failed by user is saved into Database directly.
     *
     * @param clientPayload Client details filled by user
     * @return Client
     */
    public Observable<Client> createClient(final ClientPayload clientPayload) {
        switch (PrefManager.INSTANCE.getUserStatus()) {
            case 0:
                PostClientsRequest request = PostClientRequestMapper.INSTANCE
                        .mapToEntity(clientPayload);
                return getClientApi()
                        .create6(request)
                        .map(PostClientResponseMapper.INSTANCE::mapFromEntity)
                        .concatMap(Observable::just);
            case 1:
                /**
                 * If user is in offline mode and he is making client. client payload will be saved
                 * in Database for future synchronization to sever.
                 */
                return mDatabaseHelperClient.saveClientPayloadToDB(clientPayload);

            default:
                return null;
        }
    }


    /**
     * Loading All Client payload from database to sync to the server
     *
     * @return List<ClientPayload></>
     */
    public Observable<List<ClientPayload>> getAllDatabaseClientPayload() {
        return mDatabaseHelperClient.readAllClientPayload();
    }

    /**
     * This method will called when user is syncing the client created from Database.
     * whenever a client is synced then request goes to Database to delete that client form
     * Database and reload the list from Database and update the list in UI
     *
     * @param id of the clientPayload in Database
     * @return List<ClientPayload></>
     */
    public Observable<List<ClientPayload>> deleteAndUpdatePayloads(int id,
                                                                   long clientCreationTIme) {
        return mDatabaseHelperClient.deleteAndUpdatePayloads(id, clientCreationTIme);
    }

    /**
     * This Method for Updating the Client Payload into the Database.
     *
     * @param clientPayload ClientPayload
     * @return ClientPayload
     */
    public Observable<ClientPayload> updateClientPayload(ClientPayload clientPayload) {
        return mDatabaseHelperClient.updateDatabaseClientPayload(clientPayload);
    }

    /**
     * This Method is for fetching the Client identifier from the REST API.
     *
     * @param clientId Client Id
     * @return List<Identifier>
     */
    public Observable<List<Identifier>> getClientIdentifiers(int clientId) {
        return getClientIdentifierApi().retrieveAllClientIdentifiers((long) clientId)
                .map(IdentifierMapper.INSTANCE::mapFromEntityList);
    }

    /**
     * This Method is, for creating the Client Identifier.
     *
     * @param clientId          Client Id
     * @param identifierPayload IdentifierPayload
     * @return IdentifierCreationResponse
     */
    public Observable<IdentifierCreationResponse> createClientIdentifier(
            int clientId, IdentifierPayload identifierPayload) {

        PostClientsClientIdIdentifiersRequest requestPayload = PostIdentifierMapper.INSTANCE
                .mapToEntity(identifierPayload);

        return getClientIdentifierApi().createClientIdentifier((long) clientId, requestPayload)
                .map(IdentifierCreationResponseMapper.INSTANCE::mapFromEntity);
    }

    /**
     * This Method is, for fetching the Client Identifier Template.
     *
     * @param clientId Client Id
     * @return IdentifierTemplate
     */
    public Observable<IdentifierTemplate> getClientIdentifierTemplate(int clientId) {
        return getClientIdentifierApi().newClientIdentifierDetails((long) clientId)
                .map(GetIdentifiersTemplateMapper.INSTANCE::mapFromEntity);
    }

    /**
     * This Method is for deleting the Client Identifier.
     *
     * @param clientId     Client Id
     * @param identifierId Identifier Id
     * @return GenericResponse
     */
    public Observable<GenericResponse> deleteClientIdentifier(int clientId, int identifierId) {
        return getClientIdentifierApi().deleteClientIdentifier((long) clientId, (long) identifierId)
                .map(it -> {
                        HashMap<String, Object> map = new HashMap<String, Object>() { {
                                put("officeId", it);
                                put("clientId", it);
                                put("resourceId", it);
                            } };
                        return new GenericResponse() { {
                                setResponseFields(map);
                            } };
                    });
    }

    /**
     * This Method is fetching the Client Pinpoint location from the DataTable
     * "client_pinpoint_location"
     *
     * @param clientId Client Id
     * @return ClientAddressResponse
     */
    public Observable<List<ClientAddressResponse>> getClientPinpointLocations(int clientId) {
        sdkBaseApiManager.getDataTableApi().getDatatable1(DataTables.ClientPinPointLocation
                .getDatatable(), (long) clientId, null);
        return mBaseApiManager.getClientsApi().getClientPinpointLocations(clientId);
    }

    /**
     * This Method is adding the new address in the DataTable "client_pinpoint_location"
     *
     * @param clientId Client Id
     * @param address  Client Address
     * @return GenericResponse
     */
    public Observable<GenericResponse> addClientPinpointLocation(int clientId,
                                                                 ClientAddressRequest address) {
        return mBaseApiManager.getClientsApi().addClientPinpointLocation(clientId, address);
    }

    /**
     * This Method is deleting the client address from the DataTable "client_pinpoint_location"
     *
     * @param apptableId
     * @param datatableId
     * @return GenericResponse
     */
    public Observable<GenericResponse> deleteClientAddressPinpointLocation(int apptableId,
                                                                           int datatableId) {
        return mBaseApiManager.getClientsApi()
                .deleteClientPinpointLocation(apptableId, datatableId);
    }

    /**
     * This Method is updating the client address from the DataTable "client_pinpoint_location"
     *
     * @param apptableId
     * @param datatableId
     * @param address     Client Address
     * @return GenericResponse
     */
    public Observable<GenericResponse> updateClientPinpointLocation(int apptableId,
                                                                    int datatableId,
                                                                    ClientAddressRequest address) {
        return mBaseApiManager.getClientsApi().updateClientPinpointLocation(
                apptableId, datatableId, address);
    }

    /**
     * This method is activating the client
     *
     * @param clientId
     * @return GenericResponse
     */
    public Observable<GenericResponse> activateClient(int clientId,
                                                      ActivatePayload clientActivate) {
        /*PostClientsClientIdRequest request = new PostClientsClientIdRequest();
        request
        .setNote("We cannot accept transfers of clients having loans with
        less than 1 repayment left");
        return getClientApi().activate1((long)clientId, request, "activate")
        .map(it -> {
            HashMap map = new HashMap<String, Object>();
            map.put("clientId", it);
            map.put("resourceId", it);
            GenericResponse response = new GenericResponse();
            response.setResponseFields(map);
            return response;
        });*/
        // todo: this is work around to make older request work, changing YYYY to yyyy
        clientActivate.setDateFormat("dd MMMM yyyy");
        return mBaseApiManager.getClientsApi().activateClient(clientId, clientActivate);
    }
}
