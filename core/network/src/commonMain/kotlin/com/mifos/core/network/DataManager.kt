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

import com.mifos.core.model.objects.clients.ChargeCreationResponse
import com.mifos.core.model.objects.clients.Page
import com.mifos.core.model.objects.databaseobjects.CollectionSheet
import com.mifos.core.model.objects.databaseobjects.OfflineCenter
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.model.objects.payloads.GroupLoanPayload
import com.mifos.core.model.objects.responses.SaveResponse
import com.mifos.core.model.objects.template.client.ChargeTemplate
import com.mifos.core.model.objects.template.loan.GroupLoanTemplate
import com.mifos.core.network.model.CollectionSheetPayload
import com.mifos.core.network.model.Payload
import com.mifos.room.entities.accounts.loans.Loan
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.client.ChargesEntity
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.CenterWithAssociations
import com.mifos.room.entities.group.GroupEntity
import com.mifos.room.entities.group.GroupWithAssociations
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Created by Rajan Maurya on 4/6/16.
 */
class DataManager : KoinComponent {

    private val mBaseApiManager: BaseApiManager by inject()
//    private val mDataManagerClient: DataManagerClient by inject()

    /**
     * Center API
     */
    fun getGroupsByCenter(id: Int): Flow<CenterWithAssociations> {
        return mBaseApiManager.centerApi.getAllGroupsForCenter(id)
    }

    fun getCentersInOffice(id: Int, params: Map<String, String>): Flow<List<CenterEntity>> {
        return mBaseApiManager.centerApi.getAllCentersInOffice(id, params)
    }

    fun getCollectionSheet(id: Long, payload: Payload?): Flow<CollectionSheet> {
        return mBaseApiManager.centerApi.getCollectionSheet(id, payload)
    }

    fun saveCollectionSheet(
        centerId: Int,
        collectionSheetPayload: CollectionSheetPayload?,
    ): Flow<SaveResponse> {
        return mBaseApiManager.centerApi.saveCollectionSheet(
            centerId,
            collectionSheetPayload,
        )
    }

    fun saveCollectionSheetAsync(
        id: Int,
        payload: CollectionSheetPayload?,
    ): Flow<SaveResponse> {
        return mBaseApiManager.centerApi.saveCollectionSheetAsync(id, payload)
    }

    fun getCenterList(
        dateFormat: String?,
        locale: String?,
        meetingDate: String?,
        officeId: Int,
        staffId: Int,
    ): Flow<List<OfflineCenter>> {
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
    fun getClientCharges(clientId: Int, offset: Int, limit: Int): Flow<Page<ChargesEntity>> {
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

    suspend fun getAllChargesV3(loanId: Int): HttpResponse {
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
    fun getGroups(groupid: Int): Flow<GroupWithAssociations> {
        return mBaseApiManager.groupApi.getGroupWithAssociations(groupid)
    }

    fun getGroupsByOffice(
        office: Int,
        params: Map<String, String>,
    ): Flow<List<GroupEntity>> {
        return mBaseApiManager.groupApi.getAllGroupsInOffice(office, params)
    }

    /**
     * Offices API
     */
    fun offices(): Flow<List<OfficeEntity>> {
        return mBaseApiManager.officeApi.allOffices()
    }

    /**
     * Staff API
     */
    fun getStaffInOffice(officeId: Int): Flow<List<StaffEntity>> {
        return mBaseApiManager.staffApi.getStaffForOffice(officeId)
    }

    val allStaff: Flow<List<StaffEntity>>
        get() = mBaseApiManager.staffApi.allStaff()

    /**
     * Loans API
     */
    fun getLoanTransactions(loan: Int): Flow<LoanWithAssociationsEntity> {
        return mBaseApiManager.loanApi.getLoanWithTransactions(loan)
    }

    val allLoans: Flow<List<com.mifos.core.model.objects.organisations.LoanProducts>>
        get() = mBaseApiManager.loanApi.getAllLoans()

    fun getGroupLoansAccountTemplate(groupId: Int, productId: Int): Flow<GroupLoanTemplate> {
        return mBaseApiManager.loanApi.getGroupLoansAccountTemplate(groupId, productId)
    }

    fun createGroupLoansAccount(loansPayload: GroupLoanPayload?): Flow<Loan> {
        return mBaseApiManager.loanApi.createGroupLoansAccount(loansPayload)
    }

    fun getLoanRepaySchedule(loanId: Int): Flow<LoanWithAssociationsEntity> {
        return mBaseApiManager.loanApi.getLoanRepaymentSchedule(loanId)
    }

    fun approveLoan(
        loanId: Int,
        loanApproval: com.mifos.core.model.objects.account.loan.LoanApproval?,
    ): Flow<GenericResponse> {
        return mBaseApiManager.loanApi.approveLoanApplication(loanId, loanApproval)
    }

    fun getListOfLoanCharges(loanId: Int): Flow<List<ChargesEntity>> {
        return mBaseApiManager.loanApi.getListOfLoanCharges(loanId)
    }

    fun getListOfCharges(clientId: Int): Flow<Page<ChargesEntity>> {
        return mBaseApiManager.loanApi.getListOfCharges(clientId)
    }
}
