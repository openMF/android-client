package com.mifos.api;

import com.google.gson.JsonArray;
import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.model.CollectionSheetPayload;
import com.mifos.api.model.Payload;
import com.mifos.api.model.SaveResponse;
import com.mifos.objects.user.User;
import com.mifos.objects.accounts.loan.LoanApproval;
import com.mifos.objects.accounts.loan.LoanDisbursement;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.accounts.loan.SavingsApproval;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.objects.db.OfflineCenter;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.objects.organisation.LoanProducts;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.objects.templates.loans.GroupLoanTemplate;
import com.mifos.objects.templates.loans.LoanDisburseTemplate;
import com.mifos.services.data.CenterPayload;
import com.mifos.services.data.ChargesPayload;
import com.mifos.services.data.GroupLoanPayload;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

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
    //TODO Remove this Method After fixing the Charge Test
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
     * Groups API
     */
    public Observable<GroupWithAssociations> getGroups(int groupid) {
        return mBaseApiManager.getGroupApi().getGroupWithAssociations(groupid);
    }

    public Observable<List<Group>> getGroupsByOffice(int office, Map<String, Object> params) {
        return mBaseApiManager.getGroupApi().getAllGroupsInOffice(office, params);
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

    public Observable<GenericResponse> removeDataTableEntry(String table, int entity, int rowId) {
        return mBaseApiManager.getDataTableApi().deleteEntryOfDataTableManyToMany(
                table, entity, rowId);
    }

    /**
     * Loans API
     */

    public Observable<LoanWithAssociations> getLoanTransactions(int loan) {
        return mBaseApiManager.getLoanApi().getLoanWithTransactions(loan);
    }

    public Observable<List<LoanProducts>> getAllLoans() {
        return mBaseApiManager.getLoanApi().getAllLoans();
    }

    public Observable<GroupLoanTemplate> getGroupLoansAccountTemplate(int groupId, int productId) {
        return mBaseApiManager.getLoanApi().getGroupLoansAccountTemplate(groupId, productId);
    }

    public Observable<Loans> createGroupLoansAccount(GroupLoanPayload loansPayload) {
        return mBaseApiManager.getLoanApi().createGroupLoansAccount(loansPayload);
    }


    public Observable<LoanWithAssociations> getLoanRepaySchedule(int loanId) {
        return mBaseApiManager.getLoanApi().getLoanRepaymentSchedule(loanId);
    }

    public Observable<GenericResponse> approveLoan(int loanId, LoanApproval loanApproval) {
        return mBaseApiManager.getLoanApi().approveLoanApplication(loanId, loanApproval);
    }

    public Observable<LoanDisburseTemplate> getLoanTemplate(int loanId) {
        return mBaseApiManager.getLoanApi().getLoanTemplate(loanId);
    }

    public Observable<Loans> dispurseLoan(int loanId,
                                                    LoanDisbursement loanDisbursement) {
        return mBaseApiManager.getLoanApi().disburseLoan(loanId, loanDisbursement);
    }

    public Observable<List<Charges>> getListOfLoanCharges(int loanId) {
        return mBaseApiManager.getLoanApi().getListOfLoanCharges(loanId);
    }

    public Observable<Page<Charges>> getListOfCharges(int clientId) {
        return mBaseApiManager.getLoanApi().getListOfCharges(clientId);
    }


    /**
     * Savings API
     */
    public Observable<GenericResponse> approveSavingsApplication(int savingsAccountId,
                                                                 SavingsApproval savingsApproval) {
        return mBaseApiManager.getSavingsApi().approveSavingsApplication(
                savingsAccountId, savingsApproval);
    }
}
