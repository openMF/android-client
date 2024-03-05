package com.mifos.core.network

import com.mifos.core.data.ChargesPayload
import com.mifos.core.data.GroupLoanPayload
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.model.CollectionSheetPayload
import com.mifos.core.network.model.Payload
import com.mifos.core.objects.accounts.loan.LoanApproval
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.client.ChargeCreationResponse
import com.mifos.core.objects.client.Charges
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.db.CollectionSheet
import com.mifos.core.objects.db.OfflineCenter
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.core.objects.group.Group
import com.mifos.core.objects.group.GroupWithAssociations
import com.mifos.core.objects.organisation.LoanProducts
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import com.mifos.core.objects.response.SaveResponse
import com.mifos.core.objects.templates.clients.ChargeTemplate
import com.mifos.core.objects.templates.loans.GroupLoanTemplate
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 4/6/16.
 */
@Singleton
class DataManager {
    private val mBaseApiManager: BaseApiManager
    private var mDataManagerClient: DataManagerClient? = null

    //TODO : This Constructor is temp after splitting the Datamanager layer into Sub DataManager
    constructor(baseApiManager: BaseApiManager) {
        mBaseApiManager = baseApiManager
    }

    @Inject
    constructor(
        baseApiManager: BaseApiManager,
        dataManagerClient: DataManagerClient?
    ) {
        mBaseApiManager = baseApiManager
        mDataManagerClient = dataManagerClient
    }

    /**
     * Center API
     */
    fun getGroupsByCenter(id: Int): Observable<CenterWithAssociations> {
        return mBaseApiManager.centerApi.getAllGroupsForCenter(id)
    }

    fun getCentersInOffice(id: Int, params: Map<String, String>): Observable<List<Center>> {
        return mBaseApiManager.centerApi.getAllCentersInOffice(id, params)
    }

    fun getCollectionSheet(id: Long, payload: Payload?): Observable<CollectionSheet> {
        return mBaseApiManager.centerApi.getCollectionSheet(id, payload)
    }

    fun saveCollectionSheet(
        centerId: Int,
        collectionSheetPayload: CollectionSheetPayload?
    ): Observable<SaveResponse> {
        return mBaseApiManager.centerApi.saveCollectionSheet(
            centerId, collectionSheetPayload
        )
    }

    fun saveCollectionSheetAsync(
        id: Int,
        payload: CollectionSheetPayload?
    ): Observable<SaveResponse> {
        return mBaseApiManager.centerApi.saveCollectionSheetAsync(id, payload)
    }

    fun getCenterList(
        dateFormat: String?, locale: String?, meetingDate: String?, officeId: Int, staffId: Int
    ): Observable<List<OfflineCenter>> {
        return mBaseApiManager.centerApi.getCenterList(
            dateFormat, locale, meetingDate,
            officeId, staffId
        )
    }

    /**
     * Charges API
     */
    //TODO Remove this Method After fixing the Charge Test
    fun getClientCharges(clientId: Int, offset: Int, limit: Int): Observable<Page<Charges>> {
        return mBaseApiManager.chargeApi.getListOfCharges(clientId, offset, limit)
    }

    fun getAllChargesV2(clientId: Int): Observable<ChargeTemplate> {
        return mBaseApiManager.chargeApi.getAllChargesS(clientId)
    }

    fun createCharges(
        clientId: Int,
        payload: ChargesPayload?
    ): Observable<ChargeCreationResponse> {
        return mBaseApiManager.chargeApi.createCharges(clientId, payload)
    }

    fun getAllChargesV3(loanId: Int): Observable<ResponseBody> {
        return mBaseApiManager.chargeApi.getAllChargeV3(loanId)
    }

    fun createLoanCharges(
        loanId: Int,
        chargesPayload: ChargesPayload?
    ): Observable<ChargeCreationResponse> {
        return mBaseApiManager.chargeApi.createLoanCharges(loanId, chargesPayload)
    }

    /**
     * Groups API
     */
    fun getGroups(groupid: Int): Observable<GroupWithAssociations> {
        return mBaseApiManager.groupApi.getGroupWithAssociations(groupid)
    }

    fun getGroupsByOffice(
        office: Int,
        params: Map<String, String>
    ): Observable<List<Group>> {
        return mBaseApiManager.groupApi.getAllGroupsInOffice(office, params)
    }

    /**
     * Offices API
     */
    val offices: Observable<List<Office>>
        get() = mBaseApiManager.officeApi.allOffices

    /**
     * Staff API
     */
    fun getStaffInOffice(officeId: Int): Observable<List<Staff>> {
        return mBaseApiManager.staffApi.getStaffForOffice(officeId)
    }

    val allStaff: Observable<List<Staff>>
        get() = mBaseApiManager.staffApi.allStaff

    /**
     * Loans API
     */
    fun getLoanTransactions(loan: Int): Observable<LoanWithAssociations> {
        return mBaseApiManager.loanApi.getLoanWithTransactions(loan)
    }

    val allLoans: Observable<List<LoanProducts>>
        get() = mBaseApiManager.loanApi.allLoans

    fun getGroupLoansAccountTemplate(groupId: Int, productId: Int): Observable<GroupLoanTemplate> {
        return mBaseApiManager.loanApi.getGroupLoansAccountTemplate(groupId, productId)
    }

    fun createGroupLoansAccount(loansPayload: GroupLoanPayload?): Observable<Loans> {
        return mBaseApiManager.loanApi.createGroupLoansAccount(loansPayload)
    }

    fun getLoanRepaySchedule(loanId: Int): Observable<LoanWithAssociations> {
        return mBaseApiManager.loanApi.getLoanRepaymentSchedule(loanId)
    }

    fun approveLoan(loanId: Int, loanApproval: LoanApproval?): Observable<GenericResponse> {
        return mBaseApiManager.loanApi.approveLoanApplication(loanId, loanApproval)
    }

    fun getListOfLoanCharges(loanId: Int): Observable<List<Charges>> {
        return mBaseApiManager.loanApi.getListOfLoanCharges(loanId)
    }

    fun getListOfCharges(clientId: Int): Observable<Page<Charges>> {
        return mBaseApiManager.loanApi.getListOfCharges(clientId)
    }
}