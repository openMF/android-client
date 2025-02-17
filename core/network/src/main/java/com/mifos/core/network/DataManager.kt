/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network

import com.mifos.core.entity.accounts.loan.LoanWithAssociations
import com.mifos.core.entity.accounts.loan.Loans
import com.mifos.core.entity.client.Charges
import com.mifos.core.entity.group.Center
import com.mifos.core.entity.group.CenterWithAssociations
import com.mifos.core.entity.group.Group
import com.mifos.core.entity.group.GroupWithAssociations
import com.mifos.core.entity.organisation.Staff
import com.mifos.core.model.objects.payloads.GroupLoanPayload
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.model.CollectionSheetPayload
import com.mifos.core.network.model.Payload
import com.mifos.core.objects.clients.ChargeCreationResponse
import com.mifos.core.objects.clients.Page
import com.mifos.core.objects.databaseobjects.CollectionSheet
import com.mifos.core.objects.databaseobjects.OfflineCenter
import com.mifos.core.objects.responses.SaveResponse
import com.mifos.core.objects.template.client.ChargeTemplate
import com.mifos.core.objects.template.loan.GroupLoanTemplate
import com.mifos.core.payloads.ChargesPayload
import com.mifos.room.entities.organisation.OfficeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    // TODO : This Constructor is temp after splitting the Datamanager layer into Sub DataManager
    constructor(baseApiManager: BaseApiManager) {
        mBaseApiManager = baseApiManager
    }

    @Inject
    constructor(
        baseApiManager: BaseApiManager,
        dataManagerClient: DataManagerClient?,
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

    suspend fun getCentersInOffice(id: Int, params: Map<String, String>): List<Center> {
        return mBaseApiManager.centerApi.getAllCentersInOffice(id, params)
    }

    fun getCollectionSheet(id: Long, payload: Payload?): Observable<CollectionSheet> {
        return mBaseApiManager.centerApi.getCollectionSheet(id, payload)
    }

    fun saveCollectionSheet(
        centerId: Int,
        collectionSheetPayload: CollectionSheetPayload?,
    ): Observable<SaveResponse> {
        return mBaseApiManager.centerApi.saveCollectionSheet(
            centerId,
            collectionSheetPayload,
        )
    }

    fun saveCollectionSheetAsync(
        id: Int,
        payload: CollectionSheetPayload?,
    ): Observable<SaveResponse> {
        return mBaseApiManager.centerApi.saveCollectionSheetAsync(id, payload)
    }

    fun getCenterList(
        dateFormat: String?,
        locale: String?,
        meetingDate: String?,
        officeId: Int,
        staffId: Int,
    ): Observable<List<OfflineCenter>> {
        return mBaseApiManager.centerApi.getCenterList(
            dateFormat,
            locale,
            meetingDate,
            officeId,
            staffId,
        )
    }

    /**
     * Charges API
     */
    // TODO Remove this Method After fixing the Charge Test
    fun getClientCharges(clientId: Int, offset: Int, limit: Int): Observable<Page<Charges>> {
        return mBaseApiManager.chargeApi.getListOfCharges(clientId, offset, limit)
    }

    suspend fun getAllChargesV2(clientId: Int): ChargeTemplate {
        return mBaseApiManager.chargeApi.getAllChargesS(clientId)
    }

    suspend fun createCharges(
        clientId: Int,
        payload: ChargesPayload,
    ): ChargeCreationResponse {
        return mBaseApiManager.chargeApi.createCharges(clientId, payload)
    }

    suspend fun getAllChargesV3(loanId: Int): ResponseBody {
        return mBaseApiManager.chargeApi.getAllChargeV3(loanId)
    }

    suspend fun createLoanCharges(
        loanId: Int,
        chargesPayload: ChargesPayload,
    ): ChargeCreationResponse {
        return mBaseApiManager.chargeApi.createLoanCharges(loanId, chargesPayload)
    }

    /**
     * Groups API
     */
    fun getGroups(groupid: Int): Observable<GroupWithAssociations> {
        return mBaseApiManager.groupApi.getGroupWithAssociations(groupid)
    }

    suspend fun getGroupsByOffice(
        office: Int,
        params: Map<String, String>,
    ): List<Group> {
        return mBaseApiManager.groupApi.getAllGroupsInOffice(office, params)
    }

    /**
     * Offices API
     */
    fun offices(): Flow<List<OfficeEntity>> {
        return flow {
            emit(mBaseApiManager.officeApi.allOffices())
        }
    }

    /**
     * Staff API
     */
    suspend fun getStaffInOffice(officeId: Int): List<Staff> {
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

    val allLoans: Observable<List<com.mifos.core.model.objects.organisations.LoanProducts>>
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

    fun approveLoan(
        loanId: Int,
        loanApproval: com.mifos.core.model.objects.account.loan.LoanApproval?,
    ): Observable<GenericResponse> {
        return mBaseApiManager.loanApi.approveLoanApplication(loanId, loanApproval)
    }

    suspend fun getListOfLoanCharges(loanId: Int): List<Charges> {
        return mBaseApiManager.loanApi.getListOfLoanCharges(loanId)
    }

    fun getListOfCharges(clientId: Int): Observable<Page<Charges>> {
        return mBaseApiManager.loanApi.getListOfCharges(clientId)
    }
}
