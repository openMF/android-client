/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api;

import com.google.gson.JsonArray;
import com.mifos.api.model.ClientPayload;
import com.mifos.api.model.CollectionSheetPayload;
import com.mifos.api.model.Payload;
import com.mifos.api.model.SaveResponse;
import com.mifos.objects.SearchedEntity;
import com.mifos.objects.User;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.Loan;
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
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupCreationResponse;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.objects.noncore.DataTable;
import com.mifos.objects.noncore.Document;
import com.mifos.objects.noncore.Identifier;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.organisation.Staff;
import com.mifos.objects.templates.clients.ClientsTemplate;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;
import com.mifos.services.data.CenterPayload;
import com.mifos.services.data.GroupPayload;
import com.mifos.services.data.SavingsPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        return mBaseApiManager.getAuthApi().authenticate(username, password);
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

    public Observable<List<DataTable>> getClientDataTable(){
        return mBaseApiManager.getDataTableApi().getTableOf("m_client");
    }

    public Observable<List<Identifier>> getListOfIdentifiers(int clientid){
        return mBaseApiManager.getIdentifierApi().getListOfIdentifiers(clientid);
    }

    public Observable<Page<Client>> getClientList(){
        return mBaseApiManager.getClientsApi().listAllClients();
    }

    public Observable<Page<Client>> getClientList(int offset, int limit){
        return mBaseApiManager.getClientsApi().listAllClients(offset, limit);
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

    public Observable<ClientsTemplate> getClientTemplate(){
        return mBaseApiManager.getClientsApi().getClientTemplate();
    }

    public Observable<Client> createclient(ClientPayload clientPayload){
        return mBaseApiManager.getClientsApi().createClient(clientPayload);
    }

    public Observable<GroupCreationResponse> creategroup(GroupPayload groupPayload){
        return mBaseApiManager.getGroupApi().createGroup(groupPayload);
    }

    public Observable<JsonArray> getDataOfDataTable(String datatablename, int entityId){
        return mBaseApiManager.getDataTableApi().getDataOfDataTable(datatablename, entityId);
    }

    public Observable<List<Document>> getListOfDocuments(String entityType , int entityId){
        return mBaseApiManager.getDocumentApi().getListOfDocuments(entityType, entityId);
    }

    public Observable<List<Center>> getCentersInOffice(int officeid , Map<String, Object> params){
        return mBaseApiManager.getCenterApi().getAllCentersInOffice(officeid, params);
    }

    public Observable<List<Group>> getAllGroupsInOffice(int officeid,  Map<String, Object> params){
        return mBaseApiManager.getGroupApi().getAllGroupsInOffice(officeid, params);
    }

    public Observable<CenterWithAssociations> getAllGroupsForCenter(int centerid){
        return mBaseApiManager.getCenterApi().getAllGroupsForCenter(centerid);
    }

    public Observable<LoanWithAssociations> getLoadById(int loannumber){
        return mBaseApiManager.getLoanApi().getLoanByIdWithAllAssociations(loannumber);
    }

    public Observable<List<DataTable>> getLoanDataTable(){
        return mBaseApiManager.getDataTableApi().getTableOf("m_loan");
    }

    public Observable<GroupWithAssociations> getGroups(int groupid){
        return mBaseApiManager.getGroupApi().getGroupWithAssociations(groupid);
    }

    public Observable<GenericResponse> approveLoan(int loanid, LoanApprovalRequest loanApprovalRequest){
        return mBaseApiManager.getLoanApi().approveLoanApplication(loanid, loanApprovalRequest);
    }

    public Observable<GenericResponse> disputeLoan(int loan, HashMap<String, Object> request){
        return mBaseApiManager.getLoanApi().disburseLoan(loan, request);
    }

    public Observable<LoanRepaymentTemplate> getloanRepaymentTemplate(int loanid){
        return mBaseApiManager.getLoanApi().getLoanRepaymentTemplate(loanid);
    }

    public Observable<LoanRepaymentResponse> submitPayment(int loanid, LoanRepaymentRequest loanRepaymentRequest){
        return mBaseApiManager.getLoanApi().submitPayment(loanid, loanRepaymentRequest);
    }

    public Observable<LoanWithAssociations> getLoanRepaymentSchedule(int loanid){
        return mBaseApiManager.getLoanApi().getLoanRepaymentSchedule(loanid);
    }

    public Observable<LoanWithAssociations> getLoanTransactions(int loanid){
        return mBaseApiManager.getLoanApi().getLoanWithTransactions(loanid);
    }

    public Observable<Savings> createSavingsAccount(SavingsPayload savingsPayload){
        return mBaseApiManager.getCreateSavingsAccountService().createSavingsAccount(savingsPayload);
    }

    public Observable<SavingProductsTemplate> getSavingsProductsTemplate(){
        return mBaseApiManager.getCreateSavingsAccountService().getSavingsProductsTemplate();
    }

    public Observable<List<ProductSavings>> getAllSavingsAccounts(){
        return mBaseApiManager.getCreateSavingsAccountService().getAllSavingsAccounts();
    }

    public Observable<SavingsAccountWithAssociations> getSavingAccount(String savingaccounttype, int savingsaccountId, String association){
        return mBaseApiManager.getSavingsApi().getSavingsAccountWithAssociations(savingaccounttype, savingsaccountId, association);
    }

    public Observable<List<DataTable>> getSavingsDataTable(){
        return mBaseApiManager.getDataTableApi().getTableOf("m_savings_account");
    }

    public Observable<SavingsAccountTransactionTemplate> getSavingsAccountTransactionTemplate(String accounttype, int accountId, String transactionType){
        return mBaseApiManager.getSavingsApi().getSavingsAccountTransactionTemplate(accounttype, accountId, transactionType);
    }

    public Observable<SavingsAccountTransactionResponse> processTransaction(String type, int accountId, String transactionType, SavingsAccountTransactionRequest request){
        return mBaseApiManager.getSavingsApi().processTransaction(type,accountId,transactionType,request);
    }
}
