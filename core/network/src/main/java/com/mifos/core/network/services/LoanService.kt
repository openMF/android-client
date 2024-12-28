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

import com.mifos.core.data.LoansPayload
import com.mifos.core.dbobjects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.dbobjects.accounts.loan.LoanWithAssociations
import com.mifos.core.dbobjects.accounts.loan.Loans
import com.mifos.core.dbobjects.client.Charges
import com.mifos.core.dbobjects.templates.loans.LoanRepaymentTemplate
import com.mifos.core.dbobjects.templates.loans.LoanTemplate
import com.mifos.core.dbobjects.templates.loans.LoanTransactionTemplate
import com.mifos.core.model.APIEndPoint
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.account.loan.LoanApproval
import com.mifos.core.objects.account.loan.LoanDisbursement
import com.mifos.core.objects.account.loan.LoanRepaymentResponse
import com.mifos.core.objects.clients.Page
import com.mifos.core.objects.organisations.LoanProducts
import com.mifos.core.objects.template.loan.GroupLoanTemplate
import com.mifos.core.payloads.GroupLoanPayload
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

/**
 * @author fomenkoo
 */
interface LoanService {
    @GET(APIEndPoint.LOANS + "/{loanId}?associations=all&exclude=guarantors,futureSchedule")
    fun getLoanByIdWithAllAssociations(@Path("loanId") loanId: Int): Observable<LoanWithAssociations>

    @GET(APIEndPoint.LOANS + "/{loanId}/transactions/template?command=repayment")
    fun getLoanRepaymentTemplate(@Path("loanId") loanId: Int): Observable<LoanRepaymentTemplate>

    //  Mandatory Fields
    //  1. String approvedOnDate
    @POST(APIEndPoint.LOANS + "/{loanId}?command=approve")
    fun approveLoanApplication(
        @Path("loanId") loanId: Int,
        @Body loanApproval: LoanApproval?,
    ): Observable<GenericResponse>

    //  Mandatory Fields
    //  String actualDisbursementDate
    @POST(APIEndPoint.LOANS + "/{loanId}/?command=disburse")
    fun disburseLoan(
        @Path("loanId") loanId: Int,
        @Body loanDisbursement: LoanDisbursement?,
    ): Observable<GenericResponse>

    @POST(APIEndPoint.LOANS + "/{loanId}/transactions?command=repayment")
    fun submitPayment(
        @Path("loanId") loanId: Int,
        @Body loanRepaymentRequest: LoanRepaymentRequest?,
    ): Observable<LoanRepaymentResponse>

    @GET(APIEndPoint.LOANS + "/{loanId}?associations=repaymentSchedule")
    fun getLoanRepaymentSchedule(@Path("loanId") loanId: Int): Observable<LoanWithAssociations>

    @GET(APIEndPoint.LOANS + "/{loanId}?associations=transactions")
    fun getLoanWithTransactions(@Path("loanId") loanId: Int): Observable<LoanWithAssociations>

    @get:GET(APIEndPoint.CREATE_LOANS_PRODUCTS)
    val allLoans: Observable<List<LoanProducts>>

    @POST(APIEndPoint.CREATE_LOANS_ACCOUNTS)
    fun createLoansAccount(@Body loansPayload: LoansPayload?): Observable<Loans>

    @GET(APIEndPoint.CREATE_LOANS_ACCOUNTS + "/template?templateType=individual")
    fun getLoansAccountTemplate(
        @Query("clientId") clientId: Int,
        @Query("productId") productId: Int,
    ): Observable<LoanTemplate>

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
    ): Observable<LoanTransactionTemplate>

    @POST(APIEndPoint.CREATE_LOANS_ACCOUNTS)
    fun createGroupLoansAccount(@Body loansPayload: GroupLoanPayload?): Observable<Loans>

    @GET(APIEndPoint.CREATE_LOANS_ACCOUNTS + "/template?templateType=group")
    fun getGroupLoansAccountTemplate(
        @Query("groupId") groupId: Int,
        @Query("productId") productId: Int,
    ): Observable<GroupLoanTemplate>

    @GET(APIEndPoint.LOANS + "/{loanId}/" + APIEndPoint.CHARGES)
    suspend fun getListOfLoanCharges(@Path("loanId") loanId: Int): List<Charges>

    @GET(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.CHARGES)
    fun getListOfCharges(@Path("clientId") clientId: Int): Observable<Page<Charges>>
}
