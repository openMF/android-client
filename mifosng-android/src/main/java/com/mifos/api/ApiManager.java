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
import com.mifos.objects.templates.clients.ClientsTemplate;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
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

    public void login(String username, String password ) {
        Log.i(getClass().getSimpleName(), "Login - " + username + " Password - " + password);
        getAuthApi().authenticate(username, password );
    }

    /**
     * Search API
     */

    public void searchClientsByName(String name, Callback<List<SearchedEntity>> callback) {
        getSearchApi().searchClientsByName(name, callback);
    }

    /**
     * Clients API
     */

    public void listClients(Callback<Page<Client>> callback) {
        getClientsApi().listAllClients(callback);
    }

    public void listClients(int offset, int limit, Callback<Page<Client>> callback) {
        getClientsApi().listAllClients(offset, limit, callback);
    }

    public void getClient(int id) {
        getClientsApi().getClient(id);
    }

    public void uploadClientImage(int id, TypedFile image) {
        getClientsApi().uploadClientImage(id, image);
    }

    public void deleteClientImage(int id) {
        getClientsApi().deleteClientImage(id);
    }

    public void createClient(ClientPayload payload, Callback<Client> callback) {
        getClientsApi().createClient(payload, callback);
    }

    public void getClientTemplate(Callback<ClientsTemplate> callback) {
        getClientsApi().getClientTemplate(callback);
    }

    /**
     * Centers API
     */

    public void getCenters() {
        getCenterApi().getAllCenters();
    }

    public void getCentersGroupAndMeeting(int id) {
        getCenterApi().getCenterWithGroupMembersAndCollectionMeetingCalendar(id);
    }

    public void getCentersInOffice(int id, Map<String, Object> params, Callback<List<Center>> callback) {
        getCenterApi().getAllCentersInOffice(id, params, callback);
    }

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

    /**
     * Client Accounts API
     */

    public void getClientAccounts(int id) {
        getAccountsApi().getAllAccountsOfClient(id);
    }

    /**
     * Data tables API
     */

    public void getSavingsDataTable() {
        getDataTableApi().getTableOf("m_savings_account");
    }

    public void getClientDataTable() {
        getDataTableApi().getTableOf("m_client");
    }

    public void getLoanDataTable() {
        getDataTableApi().getTableOf("m_loan");
    }

    public void getDataTableInfo(String table, int entityId, Callback<JsonArray> callback) {
        getDataTableApi().getDataOfDataTable(table, entityId, callback);
    }

    public void addDataTableEntry(String table, int entityId, Map<String, Object> payload, Callback<GenericResponse> callback) {
        getDataTableApi().createEntryInDataTable(table, entityId, payload, callback);
    }

    public void removeDataTableEntry(String table, int entity, int rowId, Callback<GenericResponse> callback) {
        getDataTableApi().deleteEntryOfDataTableManyToMany(table, entity, rowId, callback);
    }

    /**
     * Loans API
     */

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

    /**
     * Savings API
     */

    public void getSavingsAccount(String type, int accountId, String association, Callback<SavingsAccountWithAssociations> callback) {
        getSavingsApi().getSavingsAccountWithAssociations(type, accountId, association, callback);
    }

    public void getSavingsAccountTemplate(String type, int accountId, String transactionType, Callback<SavingsAccountTransactionTemplate> callback) {
        getSavingsApi().getSavingsAccountTransactionTemplate(type, accountId, transactionType, callback);
    }

    public void processTransaction(String type, int accountId, String transactionType, SavingsAccountTransactionRequest request, Callback<SavingsAccountTransactionResponse> callback) {
        getSavingsApi().processTransaction(type, accountId, transactionType, request, callback);
    }

    /**
     * Surveys API
     */

    public void getAllSurveys(Callback<List<Survey>> callback) {
        getSurveyApi().getAllSurveys(callback);
    }

    public void getSurvey(int survey, Callback<Survey> callback) {
        getSurveyApi().getSurvey(survey, callback);
    }

    public void submitScore(int survey, ScorecardPayload scorecardPayload, Callback<Scorecard> callback) {
        getSurveyApi().submitScore(survey, scorecardPayload, callback);
    }

    /**
     * GPS API
     */

    public void sendGpsData(int client, GpsCoordinatesRequest request, Callback<GpsCoordinatesResponse> callback) {
        getGpsApi().setGpsCoordinates(client, request, callback);
    }

    public void updateGpsData(int client, GpsCoordinatesRequest request, Callback<GpsCoordinatesResponse> callback) {
        getGpsApi().updateGpsCoordinates(client, request, callback);
    }

    /**
     * Groups API
     */

    public void getGroups(int group, Callback<GroupWithAssociations> callback) {
        getGroupApi().getGroupWithAssociations(group, callback);
    }

    public void getGroupsByOffice(int office, Map<String, Object> params, Callback<List<Group>> callback) {
        getGroupApi().getAllGroupsInOffice(office, params, callback);
    }

    public void createGroup(GroupPayload groupPayload, Callback<Group> callback) {

    }

    /**
     * Documents API
     */

    public void getDocumentsList(String type, int id, Callback<List<Document>> callback) {
        getDocumentApi().getListOfDocuments(type, id, callback);
    }

    public void createDocument(String type, int id, String name, String desc, TypedFile file, Callback<GenericResponse> callback) {
        getDocumentApi().createDocument(type, id, name, desc, file, callback);
    }

    /**
     * Identifiers API
     */

    public void getIdentifiers(int clientId) {
        getIdentifierApi().getListOfIdentifiers(clientId);
    }

    public void deleteIdentifier(int clientId, int id, Callback<GenericResponse> callback) {
        getIdentifierApi().deleteIdentifier(clientId, id, callback);
    }

    /**
     * Offices API
     */

    public void getOffices(Callback<List<Office>> callback) {
        getOfficeApi().getAllOffices(callback);
    }

    /**
     * Staff API
     */

    public void getStaffInOffice(int officeId, Callback<List<Staff>> callback) {
        getStaffApi().getStaffForOffice(officeId, callback);
    }

    /**
     * Charge API
     */

    public void getAllCharges(Callback<Response> callback) {
//        getChargeService().getAllCharges(callback);
    }

    public void getAllChargesV2(Callback<List<Charges>> callback) {
        getChargeService().getAllChargesS(callback);
    }

    public void getClientCharges(int clientId) {
        getChargeService().getListOfCharges(clientId);
    }

    public void createCharges(int clientId, ChargesPayload payload, Callback<Charges> callback) {
        getChargeService().createCharges(clientId, payload, callback);
    }

    /**
     * SavingsAccount API
     */

    public void getSavingsAccounts(Callback<List<ProductSavings>> callback) {
        getCreateSavingsAccountService().getAllSavingsAccounts(callback);
    }

    public void createSavingsAccount(SavingsPayload savingsPayload, Callback<Savings> callback) {
        getCreateSavingsAccountService().createSavingsAccount(savingsPayload, callback);
    }

	public void getSavingsAccountTemplate(Callback<SavingProductsTemplate> savingProductsTemplateCallback) {
		getCreateSavingsAccountService().getSavingsAccountTemplate(savingProductsTemplateCallback);
	}

}
