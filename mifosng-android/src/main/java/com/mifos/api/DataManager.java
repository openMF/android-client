package com.mifos.api;

import com.google.gson.JsonArray;
import com.mifos.api.datamanager.DataManagerClient;
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
import com.mifos.objects.accounts.loan.LoanApproval;
import com.mifos.objects.accounts.loan.LoanDisbursement;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.accounts.loan.SavingsApproval;
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
import com.mifos.services.data.LoansPayload;
import com.mifos.services.data.SavingsPayload;
import com.mifos.utils.PrefManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MultipartBody.Part;
import okhttp3.ResponseBody;
import rx.Observable;

/**
 * Created by Rajan Maurya on 4/6/16.
 */

@Singleton
public class DataManager {

    private final BaseApiManager mBaseApiManager;
    private DataManagerClient mDataManagerClient;


    //TODO : This Constructor is temp after splitting the Datamanager layer into Sub DataManager
    public DataManager(BaseApiManager baseApiManager) {
        mBaseApiManager = baseApiManager;
    }

    @Inject
    public DataManager(BaseApiManager baseApiManager,
                       DataManagerClient dataManagerClient) {
        mBaseApiManager = baseApiManager;
        mDataManagerClient = dataManagerClient;
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
    //Return Centers List according to offset and limit parameter
    public Observable<Page<Center>> getCenters(boolean b, int offset, int limit) {
        return mBaseApiManager.getCenterApi().getCenters(b, offset, limit);
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

    public Observable<SaveResponse> saveCollectionSheet(int centerId, CollectionSheetPayload
            collectionSheetPayload) {
        return mBaseApiManager.getCenterApi().saveCollectionSheet(
                centerId, collectionSheetPayload);
    }

    public Observable<SaveResponse> saveCollectionSheetAsync(int id,
                                                             CollectionSheetPayload payload) {
        return mBaseApiManager.getCenterApi().saveCollectionSheetAsync(id, payload);
    }

    public Observable<List<OfflineCenter>> getCenterList(
            String dateFormat, String locale, String meetingDate, int officeId, int staffId) {
        return mBaseApiManager.getCenterApi().getCenterList(dateFormat, locale, meetingDate,
                officeId, staffId);
    }

    /**
     * Charges API
     */
    public Observable<Page<Charges>> getClientCharges(int clientId, int offset, int limit) {
        return mBaseApiManager.getChargeApi().getListOfCharges(clientId, offset, limit);
    }

    public Observable<ResponseBody> getAllChargesV2(int clientId) {
        return mBaseApiManager.getChargeApi().getAllChargesS(clientId);
    }

    public Observable<Charges> createCharges(int clientId, ChargesPayload payload) {
        return mBaseApiManager.getChargeApi().createCharges(clientId, payload);
    }

    public Observable<ResponseBody> getAllChargesV3(int loanId) {
        return mBaseApiManager.getChargeApi().getAllChargev3(loanId);
    }

    public Observable<Charges> createLoanCharges(int loanId, ChargesPayload chargesPayload) {
        return mBaseApiManager.getChargeApi().createLoanCharges(loanId, chargesPayload);
    }


    /**
     * Client API
     */
    public Observable<Page<Client>> getAllClients() {

        switch (PrefManager.getUserStatus()) {
            case 0:
                return mDataManagerClient.getAllClients();

            case 1:
                //Return the offline Datamanager
                return null;

            default:
                return mDataManagerClient.getAllClients();
        }
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

    public Observable<ResponseBody> uploadClientImage(int id, Part file) {
        return mBaseApiManager.getClientsApi().uploadClientImage(id, file);
    }

    public Observable<ResponseBody> deleteClientImage(int clientId) {
        return mBaseApiManager.getClientsApi().deleteClientImage(clientId);
    }


    /**
     * Identifiers API
     */
    public Observable<List<Identifier>> getIdentifiers(int clientid) {
        return mBaseApiManager.getIdentifierApi().getListOfIdentifiers(clientid);
    }

    public Observable<GenericResponse> deleteIdentifier(int clientId, int id) {
        return mBaseApiManager.getIdentifierApi().deleteIdentifier(clientId, id);
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

    public Observable<GenericResponse> createDocument(
            String type, int id, String name, String desc, Part file) {
        return mBaseApiManager.getDocumentApi().createDocument(type, id, name, desc, file);
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

    public Observable<List<Staff>> getAllStaff() {
        return mBaseApiManager.getStaffApi().getAllStaff();
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

    public Observable<List<DataTable>> getSavingsDataTable() {
        return mBaseApiManager.getDataTableApi().getTableOf("m_savings_account");
    }

    public Observable<List<DataTable>> getClientDataTable() {
        return mBaseApiManager.getDataTableApi().getTableOf("m_client");
    }

    public Observable<List<DataTable>> getLoanDataTable() {
        return mBaseApiManager.getDataTableApi().getTableOf("m_loan");
    }

    public Observable<GenericResponse> removeDataTableEntry(String table, int entity, int rowId) {
        return mBaseApiManager.getDataTableApi().deleteEntryOfDataTableManyToMany(
                table, entity, rowId);
    }


    /**
     * GroupAccountsService
     */
    public Observable<GroupAccounts> getAllGroupsOfClient(int groupId) {
        return mBaseApiManager.getGroupAccountsServiceApi().getAllGroupsOfClient(groupId);
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

    public Observable<ResponseBody> getGroupLoansAccountTemplate(int groupId, int productId) {
        return mBaseApiManager.getLoanApi().getGroupLoansAccountTemplate(groupId, productId);
    }

    public Observable<Loans> createGroupLoansAccount(GroupLoanPayload loansPayload) {
        return mBaseApiManager.getLoanApi().createGroupLoansAccount(loansPayload);
    }

    public Observable<ResponseBody> getLoansAccountTemplate(int clientId, int productId) {
        return mBaseApiManager.getLoanApi().getLoansAccountTemplate(clientId, productId);
    }

    public Observable<LoanWithAssociations> getLoanRepaySchedule(int loanId) {
        return mBaseApiManager.getLoanApi().getLoanRepaymentSchedule(loanId);
    }

    public Observable<GenericResponse> approveLoan(int loanId, LoanApproval loanApproval) {
        return mBaseApiManager.getLoanApi().approveLoanApplication(loanId, loanApproval);
    }

    public Observable<ResponseBody> getLoanTemplate(int loanId) {
        return mBaseApiManager.getLoanApi().getLoanTemplate(loanId);
    }

    public Observable<GenericResponse> dispurseLoan(int loanId,
                                                    LoanDisbursement loanDisbursement) {
        return mBaseApiManager.getLoanApi().disburseLoan(loanId, loanDisbursement);
    }

    public Observable<List<Charges>> getListOfLoanCharges(int loanId) {
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

    public Observable<Loans> createLoansAccount(LoansPayload loansPayload) {
        return mBaseApiManager.getLoanApi().createLoansAccount(loansPayload);
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

    public Observable<GenericResponse> approveSavingsApplication(int savingsAccountId,
                                                                 SavingsApproval savingsApproval) {
        return mBaseApiManager.getSavingsApi().approveSavingsApplication(
                savingsAccountId, savingsApproval);
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
