/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.objects.account.loan.LoanApproval
import com.mifos.core.model.objects.account.loan.LoanDisbursement
import com.mifos.core.model.objects.clients.Page
import com.mifos.core.model.objects.organisations.LoanProducts
import com.mifos.core.model.objects.payloads.GroupLoanPayload
import com.mifos.core.model.objects.template.loan.GroupLoanTemplate
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.model.LoansPayload
import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.accounts.loans.Loan
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.client.ChargesEntity
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import com.mifos.room.entities.templates.loans.LoanTemplate
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow

/**
 * @author fomenkoo
 */
interface LoanService {
    @GET(APIEndPoint.LOANS + "/{loanId}?associations=all&exclude=guarantors,futureSchedule")
    suspend fun getLoanByIdWithAllAssociations(@Path("loanId") loanId: Int): LoanWithAssociationsEntity

    @GET(APIEndPoint.LOANS + "/{loanId}/transactions/template?command=repayment")
    suspend fun getLoanRepaymentTemplate(@Path("loanId") loanId: Int): LoanRepaymentTemplateEntity

    //  Mandatory Fields
    //  1. String approvedOnDate
    @POST(APIEndPoint.LOANS + "/{loanId}?command=approve")
    fun approveLoanApplication(
        @Path("loanId") loanId: Int,
        @Body loanApproval: LoanApproval?,
    ): Flow<GenericResponse>

    //  Mandatory Fields
    //  String actualDisbursementDate
    @POST(APIEndPoint.LOANS + "/{loanId}/?command=disburse")
    fun disburseLoan(
        @Path("loanId") loanId: Int,
        @Body loanDisbursement: LoanDisbursement?,
    ): Flow<GenericResponse>

    @POST(APIEndPoint.LOANS + "/{loanId}/transactions?command=repayment")
    suspend fun submitPayment(
        @Path("loanId") loanId: Int,
        @Body loanRepaymentRequest: LoanRepaymentRequestEntity?,
    ): LoanRepaymentResponseEntity

    @GET(APIEndPoint.LOANS + "/{loanId}?associations=repaymentSchedule")
    fun getLoanRepaymentSchedule(@Path("loanId") loanId: Int): Flow<LoanWithAssociationsEntity>

    @GET(APIEndPoint.LOANS + "/{loanId}?associations=transactions")
    fun getLoanWithTransactions(@Path("loanId") loanId: Int): Flow<LoanWithAssociationsEntity>

    @GET(APIEndPoint.CREATE_LOANS_PRODUCTS)
    fun getAllLoans(): Flow<List<LoanProducts>>

    @POST(APIEndPoint.CREATE_LOANS_ACCOUNTS)
    fun createLoansAccount(@Body loansPayload: LoansPayload?): Flow<Loan>

    @GET(APIEndPoint.CREATE_LOANS_ACCOUNTS + "/template?templateType=individual")
    fun getLoansAccountTemplate(
        @Query("clientId") clientId: Int,
        @Query("productId") productId: Int,
    ): Flow<LoanTemplate>

    /**
     * For fetching any type of loan template.
     * Example:
     * 1. repayment
     * 2. disburse
     * 3. waiver
     * 4. refundbycash
     * 5. foreclosure
     *
     * @param loanId Loan Id
     * @param command Template Type
     * @return
     */
    @GET(APIEndPoint.LOANS + "/{loanId}/transactions/template")
    fun getLoanTransactionTemplate(
        @Path("loanId") loanId: Int,
        @Query("command") command: String?,
    ): Flow<LoanTransactionTemplate>

    @POST(APIEndPoint.CREATE_LOANS_ACCOUNTS)
    fun createGroupLoansAccount(@Body loansPayload: GroupLoanPayload?): Flow<Loan>

    @GET(APIEndPoint.CREATE_LOANS_ACCOUNTS + "/template?templateType=group")
    fun getGroupLoansAccountTemplate(
        @Query("groupId") groupId: Int,
        @Query("productId") productId: Int,
    ): Flow<GroupLoanTemplate>

    @GET(APIEndPoint.LOANS + "/{loanId}/" + APIEndPoint.CHARGES)
    fun getListOfLoanCharges(@Path("loanId") loanId: Int): Flow<List<ChargesEntity>>

    @GET(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.CHARGES)
    fun getListOfCharges(@Path("clientId") clientId: Int): Flow<Page<ChargesEntity>>
}
