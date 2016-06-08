package com.mifos.api;

import com.google.gson.JsonArray;
import com.mifos.api.model.ClientPayload;
import com.mifos.api.model.CollectionSheetPayload;
import com.mifos.api.model.GpsCoordinatesRequest;
import com.mifos.api.model.GpsCoordinatesResponse;
import com.mifos.api.model.Payload;
import com.mifos.api.model.SaveResponse;
import com.mifos.objects.SearchedEntity;
import com.mifos.objects.User;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.client.Savings;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.objects.noncore.DataTable;
import com.mifos.objects.noncore.Document;
import com.mifos.objects.noncore.Identifier;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.ProductLoans;
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
import com.mifos.services.data.GroupLoanPayload;
import com.mifos.services.data.GroupPayload;
import com.mifos.services.data.SavingsPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.client.Response;
import retrofit.mime.TypedFile;
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
     * @param password Password
     * @return Basic OAuth
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

    public Observable<CollectionSheet> getCollectionSheet(long id, Payload payload) {
        return mBaseApiManager.getCenterApi().getCollectionSheet(id, payload);
    }

    public Observable<SaveResponse> saveCollectionSheetAsync(int id,
                                                             CollectionSheetPayload payload) {
        return mBaseApiManager.getCenterApi().saveCollectionSheetAsync(id, payload);
    }

    /**
     * Charges API
     */
    public Observable<Page<Charges>> getClientCharges(int id) {
        return mBaseApiManager.getChargeApi().getListOfCharges(id);
    }

    public Observable<Response> getAllChargesV2(int clientId) {
        return mBaseApiManager.getChargeApi().getAllChargesS(clientId);
    }

    public Observable<Charges> createCharges(int clientId, ChargesPayload payload) {
        return mBaseApiManager.getChargeApi().createCharges(clientId, payload);
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

    public Observable<ClientsTemplate> getClientTemplate() {
        return mBaseApiManager.getClientsApi().getClientTemplate();
    }

    public Observable<Client> createClient(ClientPayload clientPayload) {
        return mBaseApiManager.getClientsApi().createClient(clientPayload);
    }

    public Observable<Client> getClient(int id) {
        return mBaseApiManager.getClientsApi().getClient(id);
    }

    public Observable<Response> uploadClientImage(int id, TypedFile image) {
        return mBaseApiManager.getClientsApi().uploadClientImage(id, image);
    }

    public Observable<Response> deleteClientImage(int clientId) {
        return mBaseApiManager.getClientsApi().deleteClientImage(clientId);
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

    public Observable<Group> getGroup(int groupId) {
        return mBaseApiManager.getGroupApi().getGroup(groupId);
    }

    public Observable<Page<Group>> getAllGroup() {
        return mBaseApiManager.getGroupApi().getAllGroup();
    }

    public Observable<Page<Group>> listAllGroups(int offset, int limit) {
        return mBaseApiManager.getGroupApi().listAllGroups(offset, limit);
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


    /**
     * DataTable API
     */
    public Observable<JsonArray> getDataTableInfo(String table, int entityId) {
        return mBaseApiManager.getDataTableApi().getDataOfDataTable(table, entityId);
    }

    public Observable<GenericResponse> addDataTableEntry(
            String table, int entityId, Map<String, Object> payload) {
        return mBaseApiManager.getDataTableApi()
                .createEntryInDataTable(table, entityId, payload);
    }


    /**
     * GroupAccountsService
     */
    public Observable<GroupAccounts> getAllGroupsOfClient(int groupId) {
        return mBaseApiManager.getGroupAccountsServiceApi().getAllGroupsOfClient(groupId);
    }


    /**
     * Data tables API
     */
    public Observable<List<DataTable>> getSavingsDataTable() {
        return mBaseApiManager.getDataTableApi().getTableOf("m_savings_account");
    }

    public Observable<List<DataTable>> getClientDataTable() {
        return mBaseApiManager.getDataTableApi().getTableOf("m_client");
    }

    public Observable<List<DataTable>> getLoanDataTable() {
        return mBaseApiManager.getDataTableApi().getTableOf("m_loan");
    }

    /**
     * Client Accounts API
     */
    public Observable<ClientAccounts> getClientAccounts(int clientId) {
        return mBaseApiManager.getAccountsApi().getAllAccountsOfClient(clientId);
    }

    /**
     * Loans API
     */
    public Observable<LoanWithAssociations> getLoanById(int loanAccountNumber) {
        return mBaseApiManager.getLoanApi().getLoanByIdWithAllAssociations(loanAccountNumber);
    }

    public Observable<LoanWithAssociations> getLoanTransactions(int loan) {
        return mBaseApiManager.getLoanApi().getLoanWithTransactions(loan);
    }

    public Observable<List<ProductLoans>> getAllLoans() {
        return mBaseApiManager.getLoanApi().getAllLoans();
    }

    public Observable<Response> getGroupLoansAccountTemplate(int groupId, int productId) {
        return mBaseApiManager.getLoanApi().getGroupLoansAccountTemplate(groupId, productId);
    }

    public Observable<Loans> createGroupLoansAccount(GroupLoanPayload loansPayload) {
        return mBaseApiManager.getLoanApi().createGroupLoansAccount(loansPayload);
    }

    public Observable<Response> getLoansAccountTemplate(int clientId, int productId) {
        return mBaseApiManager.getLoanApi().getLoansAccountTemplate(clientId, productId);
    }

    public Observable<LoanWithAssociations> getLoanRepaySchedule(int loanId) {
        return mBaseApiManager.getLoanApi().getLoanRepaymentSchedule(loanId);
    }


    /**
     * Savings API
     */
    public Observable<SavingsAccountWithAssociations> getSavingsAccount(
            String type, int accountId, String association) {
        return mBaseApiManager.getSavingsApi().getSavingsAccountWithAssociations(
                type, accountId, association);
    }

    public Observable<GenericResponse> activateSavings(int savingsAccountId,
                                                       HashMap<String, Object> request) {
        return mBaseApiManager.getSavingsApi().activateSavings(savingsAccountId, request);
    }

    public Observable<SavingsAccountTransactionTemplate> getSavingsAccountTemplate(
            String type, int accountId, String transactionType) {
        return mBaseApiManager.getSavingsApi().getSavingsAccountTransactionTemplate(type,
                accountId, transactionType);
    }

    public Observable<SavingsAccountTransactionResponse> processTransaction(
            String type, int accountId, String transactionType,
            SavingsAccountTransactionRequest request) {
        return mBaseApiManager.getSavingsApi().processTransaction(type, accountId, transactionType,
                request);
    }

    /**
     * Loan API
     */
    public Observable<Page<Charges>> getListOfLoanCharges(int loanId) {
        return mBaseApiManager.getLoanApi().getListOfLoanCharges(loanId);
    }

    public Observable<Page<Charges>> getListOfCharges(int clientId) {
        return mBaseApiManager.getLoanApi().getListOfCharges(clientId);
    }

    public Observable<LoanRepaymentTemplate> getLoanRepayTemplate(int loanId) {
        return mBaseApiManager.getLoanApi().getLoanRepaymentTemplate(loanId);
    }

    public Observable<LoanRepaymentResponse> submitPayment(
            int loanId, LoanRepaymentRequest request) {
        return mBaseApiManager.getLoanApi().submitPayment(loanId, request);
    }


    /**
     * SavingsAccount API
     */
    public Observable<List<ProductSavings>> getSavingsAccounts() {
        return mBaseApiManager.getCreateSavingsAccountService().getAllSavingsAccounts();
    }

    public Observable<Savings> createSavingsAccount(SavingsPayload savingsPayload) {
        return mBaseApiManager
                .getCreateSavingsAccountService()
                .createSavingsAccount(savingsPayload);
    }

    public Observable<SavingProductsTemplate> getSavingsAccountTemplate() {
        return mBaseApiManager.getCreateSavingsAccountService().getSavingsAccountTemplate();
    }


    /**
     * Surveys API
     */
    public Observable<List<Survey>> getAllSurvey() {
        return mBaseApiManager.getSurveyApi().getAllSurveys();
    }

    public Observable<Scorecard> submitScore(int survey, Scorecard scorecardPayload) {
        return mBaseApiManager.getSurveyApi().submitScore(survey, scorecardPayload);
    }

    public Observable<Survey> getSurvey(int surveyId) {
        return mBaseApiManager.getSurveyApi().getSurvey(surveyId);
    }

    /**
     * GPS API
     */
    public Observable<GpsCoordinatesResponse> sendGpsData(
            int clientId, GpsCoordinatesRequest gpsCoordinatesRequest) {
        return mBaseApiManager.getGpsApi().setGpsCoordinates(clientId, gpsCoordinatesRequest);
    }

    public Observable<GpsCoordinatesResponse> updateGpsData(int client,
                                                            GpsCoordinatesRequest request) {
        return mBaseApiManager.getGpsApi().updateGpsCoordinates(client, request);
    }


}
