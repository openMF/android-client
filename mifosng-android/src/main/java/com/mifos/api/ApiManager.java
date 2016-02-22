/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api;

import android.util.Log;

import com.google.gson.JsonArray;
import com.mifos.api.model.ClientPayload;
import com.mifos.api.model.CollectionSheetPayload;
import com.mifos.api.model.GpsCoordinatesRequest;
import com.mifos.api.model.GpsCoordinatesResponse;
import com.mifos.api.model.Payload;
import com.mifos.api.model.SaveResponse;
import com.mifos.api.model.ScorecardPayload;
import com.mifos.objects.SearchedEntity;
import com.mifos.objects.User;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanApprovalRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.client.Savings;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.objects.db.OfflineCenter;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.objects.noncore.DataTable;
import com.mifos.objects.noncore.Document;
import com.mifos.objects.noncore.Identifier;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.organisation.Staff;
import com.mifos.objects.survey.Scorecard;
import com.mifos.objects.survey.Survey;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;
import com.mifos.services.data.CenterPayload;
import com.mifos.services.data.ChargesPayload;
import com.mifos.services.data.GroupPayload;
import com.mifos.services.data.SavingsPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * @author fomenkoo
 */
public class ApiManager extends BaseApiManager {

    // Basic authentication
    public void login(String username, String password, Callback<User> callback) {
        Log.i(getClass().getSimpleName(), "Login - " + username + " Password - " + password);
        getAuthApi().authenticate(username, password, callback);
    }

    //
    // Search API
    //
    // Looking for client by name
    public void searchClientsByName(String name, Callback<List<SearchedEntity>> callback) {
        getSearchApi().searchClientsByName(name, callback);
    }

    //
    // Clients API
    //
    // Getting clients List
    public void listClients(Callback<Page<Client>> callback) {
        getClientsApi().listAllClients(callback);
    }

    // Getting clients List (fetching with pagination)
    public void listClients(int offset, int limit, Callback<Page<Client>> callback) {
        getClientsApi().listAllClients(offset, limit, callback);
    }

    // Getting client data
    public void getClient(int id, Callback<Client> callback) {
        getClientsApi().getClient(id, callback);
    }

    // Uploads new client image to server
    public void uploadClientImage(int id, TypedFile image, Callback<Response> callback) {
        getClientsApi().uploadClientImage(id, image, callback);
    }

    // Deletes current image
    public void deleteClientImage(int id, Callback<Response> callback) {
        getClientsApi().deleteClientImage(id, callback);
    }

    // Creating client
    public void createClient(ClientPayload payload, Callback<Client> callback) {
        getClientsApi().createClient(payload, callback);
    }

    // Creating client
    public void getClientTemplate(Callback<Response> callback) {
        getClientsApi().getClientTemplate(callback);
    }


    //
    // Centers API
    //
    // Getting all centers list
    public void getCenters(Callback<List<Center>> callback) {
        getCenterApi().getAllCenters(callback);
    }

    // Returns centers with group members and calendar
    public void getCentersGroupAndMeeting(int id, Callback<CenterWithAssociations> callback) {
        getCenterApi().getCenterWithGroupMembersAndCollectionMeetingCalendar(id, callback);
    }

    // Getting centers that attached to office
    public void getCentersInOffice(int id, Map<String, Object> params, Callback<List<Center>> callback) {
        getCenterApi().getAllCentersInOffice(id, params, callback);
    }

    // Getting all groups attached to center
    public void getGroupsByCenter(int id, Callback<CenterWithAssociations> callback) {
        getCenterApi().getAllGroupsForCenter(id, callback);
    }

    public void getCollectionSheet(long id, Payload payload, Callback<CollectionSheet> callback) {
        getCenterApi().getCollectionSheet(id, payload, callback);
    }

    public SaveResponse saveCollectionSheet(int id, CollectionSheetPayload payload) {
        return getCenterApi().saveCollectionSheet(id, payload);
    }

    public void saveCollectionSheetAsync(int id, CollectionSheetPayload payload, Callback<SaveResponse> callback) {
        getCenterApi().saveCollectionSheet(id, payload, callback);
    }

    public void getCenterList(String dateFormat, String locale, String meetingDate, int officeId, int staffId, Callback<List<OfflineCenter>> callback) {
        getCenterApi().getCenterList(dateFormat, locale, meetingDate, officeId, staffId, callback);
    }

    public void createCenter(CenterPayload centerPayload, Callback<Center> callback) {
        getCenterApi().createCenter(centerPayload, callback);
    }

    //
    // Client Accounts API
    //
    // Getting all accounts attached to client
    public void getClientAccounts(int id, Callback<ClientAccounts> callback) {
        getAccountsApi().getAllAccountsOfClient(id, callback);
    }

    //
    // Data tables API
    //
    // Getting data table for savings
    public void getSavingsDataTable(Callback<List<DataTable>> callback) {
        getDataTableApi().getTableOf("m_savings_account", callback);
    }

    // Getting data table for client
    public void getClientDataTable(Callback<List<DataTable>> callback) {
        getDataTableApi().getTableOf("m_client", callback);
    }

    // Getting data table for client
    public void getLoanDataTable(Callback<List<DataTable>> callback) {
        getDataTableApi().getTableOf("m_loan", callback);
    }

    // Getting info for data table
    public void getDataTableInfo(String table, int entityId, Callback<JsonArray> callback) {
        getDataTableApi().getDataOfDataTable(table, entityId, callback);
    }

    // Adding entry to data table
    public void addDataTableEntry(String table, int entityId, Map<String, Object> payload, Callback<GenericResponse> callback) {
        getDataTableApi().createEntryInDataTable(table, entityId, payload, callback);
    }

    // Removes entry from data table
    public void removeDataTableEntry(String table, int entity, int rowId, Callback<GenericResponse> callback) {
        getDataTableApi().deleteEntryOfDataTableManyToMany(table, entity, rowId, callback);
    }

    //
    // Loans API
    //
    public void getLoanById(int loan, Callback<LoanWithAssociations> callback) {
        getLoanApi().getLoanByIdWithAllAssociations(loan, callback);
    }

    public void getLoanRepayTemplate(int loan, Callback<LoanRepaymentTemplate> callback) {
        getLoanApi().getLoanRepaymentTemplate(loan, callback);
    }

    public void approveLoan(int loan, LoanApprovalRequest request, Callback<GenericResponse> callback) {
        getLoanApi().approveLoanApplication(loan, request, callback);
    }

    public void disputeLoan(int loan, HashMap<String, Object> request, Callback<GenericResponse> callback) {
        getLoanApi().disburseLoan(loan, request, callback);
    }

    public void submitPayment(int loan, LoanRepaymentRequest request, Callback<LoanRepaymentResponse> callback) {
        getLoanApi().submitPayment(loan, request, callback);
    }

    public void getLoanRepaySchedule(int loan, Callback<LoanWithAssociations> callback) {
        getLoanApi().getLoanRepaymentSchedule(loan, callback);
    }

    public void getLoanTransactions(int loan, Callback<LoanWithAssociations> callback) {
        getLoanApi().getLoanWithTransactions(loan, callback);
    }

    //
    // Savings API
    //
    public void getSavingsAccount(String type, int accountId, String association, Callback<SavingsAccountWithAssociations> callback) {
        getSavingsApi().getSavingsAccountWithAssociations(type, accountId, association, callback);
    }

    public void getSavingsAccountTemplate(String type, int accountId, String transactionType, Callback<SavingsAccountTransactionTemplate> callback) {
        getSavingsApi().getSavingsAccountTransactionTemplate(type, accountId, transactionType, callback);
    }

    public void processTransaction(String type, int accountId, String transactionType, SavingsAccountTransactionRequest request, Callback<SavingsAccountTransactionResponse> callback) {
        getSavingsApi().processTransaction(type, accountId, transactionType, request, callback);
    }

    //
    // Surveys API
    //
    public void getAllSurveys(Callback<List<Survey>> callback) {
        getSurveyApi().getAllSurveys(callback);
    }

    public void getSurvey(int survey, Callback<Survey> callback) {
        getSurveyApi().getSurvey(survey, callback);
    }

    public void submitScore(int survey, ScorecardPayload scorecardPayload, Callback<Scorecard> callback) {
        getSurveyApi().submitScore(survey, scorecardPayload, callback);
    }

    //
    // GPS API
    //
    public void sendGpsData(int client, GpsCoordinatesRequest request, Callback<GpsCoordinatesResponse> callback) {
        getGpsApi().setGpsCoordinates(client, request, callback);
    }

    public void updateGpsData(int client, GpsCoordinatesRequest request, Callback<GpsCoordinatesResponse> callback) {
        getGpsApi().updateGpsCoordinates(client, request, callback);
    }

    //
    // Groups API
    //
    // Getting all groups with associations
    public void getGroups(int group, Callback<GroupWithAssociations> callback) {
        getGroupApi().getGroupWithAssociations(group, callback);
    }

    public void getGroupsByOffice(int office, Map<String, Object> params, Callback<List<Group>> callback) {
        getGroupApi().getAllGroupsInOffice(office, params, callback);
    }

    public void createGroup(GroupPayload groupPayload, Callback<Group> callback) {

    }

    //
    // Documents API
    //
    // Returning documents list
    public void getDocumentsList(String type, int id, Callback<List<Document>> callback) {
        getDocumentApi().getListOfDocuments(type, id, callback);
    }

    public void createDocument(String type, int id, String name, String desc, TypedFile file, Callback<GenericResponse> callback) {
        getDocumentApi().createDocument(type, id, name, desc, file, callback);
    }

    //
    // Identifiers API
    //
    public void getIdentifiers(int clientId, Callback<List<Identifier>> callback) {
        getIdentifierApi().getListOfIdentifiers(clientId, callback);
    }

    public void deleteIdentifier(int clientId, int id, Callback<GenericResponse> callback) {
        getIdentifierApi().deleteIdentifier(clientId, id, callback);
    }

    //
    // Officess API
    //
    public void getOffices(Callback<List<Office>> callback) {
        getOfficeApi().getAllOffices(callback);
    }

    //
    // Staff API
    //
    public void getStaffInOffice(int officeId, Callback<List<Staff>> callback) {
        getStaffApi().getStaffForOffice(officeId, callback);
    }

    //
    // Charge API
    //
    public void getAllCharges(Callback<Response> callback) {
//        getChargeService().getAllCharges(callback);
    }

    public void getAllChargesV2(Callback<List<Charges>> callback) {
        getChargeService().getAllChargesS(callback);
    }

    public void getClientCharges(int clientId, Callback<Page<Charges>> callback) {
        getChargeService().getListOfCharges(clientId, callback);
    }

    public void createCharges(int clientId, ChargesPayload payload, Callback<Charges> callback) {
        getChargeService().createCharges(clientId, payload, callback);
    }

    //
    // SavingsAccount API?
    //
    public void getSavingsAccounts(Callback<List<ProductSavings>> callback) {
        getCreateSavingsAccountService().getAllSavingsAccounts(callback);
    }

    public void createSavingsAccount(SavingsPayload savingsPayload, Callback<Savings> callback) {
        getCreateSavingsAccountService().createSavingsAccount(savingsPayload, callback);
    }

    public void getSavingsAccountTemplate(Callback<Response> clientCallback) {
        getCreateSavingsAccountService().getSavingsAccountTemplate(clientCallback);
    }
}
