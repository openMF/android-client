/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.loan.api

import com.mifos.core.model.GenericResponse
import com.mifos.core.model.network.LoansPayload
import com.mifos.core.model.objects.account.loan.LoanApproval
import com.mifos.core.model.objects.account.loan.LoanDisbursement
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleApprovalRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleRejectionRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleResponse
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleTemplate
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferRequest
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferTemplate
import com.mifos.core.model.objects.clients.Page
import com.mifos.core.model.objects.loan.Loan
import com.mifos.core.model.objects.organisations.LoanProducts
import com.mifos.core.model.objects.payloads.GroupLoanPayload
import com.mifos.core.model.objects.template.loan.GroupLoanTemplate
import com.mifos.core.network.APIEndPoint
import com.mifos.room.charge.entity.ChargesEntity
import com.mifos.room.loan.entity.LoanRepaymentRequestEntity
import com.mifos.room.loan.entity.LoanRepaymentResponseEntity
import com.mifos.room.loan.entity.LoanRepaymentTemplateEntity
import com.mifos.room.loan.entity.LoanTemplate
import com.mifos.room.loan.entity.LoanTransactionTemplate
import com.mifos.room.loan.entity.LoanWithAssociationsEntity
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

/** Fineract `loan` domain endpoints — read, mutations, transfers, reschedules. */
interface LoanApi {

    // ─────────────────────────────────────────────────────────────────────
    // region — Loan reads
    // ─────────────────────────────────────────────────────────────────────

    /** Retrieve a loan with all associations (excludes guarantors + future schedule). */
    @GET(APIEndPoint.LOANS + "/{loanId}?associations=all&exclude=guarantors,futureSchedule")
    suspend fun getLoanByIdWithAllAssociations(
        @Path("loanId") loanId: Int,
    ): LoanWithAssociationsEntity

    /** Retrieve a loan with its repayment schedule. */
    @GET(APIEndPoint.LOANS + "/{loanId}?associations=repaymentSchedule")
    suspend fun getRepaymentSchedule(
        @Path("loanId") loanId: Int,
    ): LoanWithAssociationsEntity

    /** Retrieve a loan with its transactions. */
    @GET(APIEndPoint.LOANS + "/{loanId}?associations=transactions")
    suspend fun getLoanWithTransactions(
        @Path("loanId") loanId: Int,
    ): LoanWithAssociationsEntity

    /** List all loan products. */
    @GET(APIEndPoint.CREATE_LOANS_PRODUCTS)
    suspend fun getAllLoanProducts(): List<LoanProducts>

    /** List charges attached to a loan. */
    @GET(APIEndPoint.LOANS + "/{loanId}/" + APIEndPoint.CHARGES)
    suspend fun getLoanCharges(
        @Path("loanId") loanId: Int,
    ): List<ChargesEntity>

    /** List charges attached to a client (paged). */
    @GET(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.CHARGES)
    suspend fun getClientCharges(
        @Path("clientId") clientId: Int,
    ): Page<ChargesEntity>

    // ─────────────────────────────────────────────────────────────────────
    // region — Loan transaction templates
    // ─────────────────────────────────────────────────────────────────────

    /** Retrieve the repayment-transaction template. */
    @GET(APIEndPoint.LOANS + "/{loanId}/transactions/template?command=repayment")
    suspend fun getRepaymentTemplate(
        @Path("loanId") loanId: Int,
    ): LoanRepaymentTemplateEntity

    /**
     * Retrieve a transaction template for an arbitrary command
     * (`repayment`, `disburse`, `waiver`, `refundbycash`, `foreclosure`).
     */
    @GET(APIEndPoint.LOANS + "/{loanId}/transactions/template")
    suspend fun getTransactionTemplate(
        @Path("loanId") loanId: Int,
        @Query("command") command: String?,
    ): LoanTransactionTemplate

    // ─────────────────────────────────────────────────────────────────────
    // region — Loan lifecycle (approve, disburse, repay)
    // ─────────────────────────────────────────────────────────────────────

    /** Approve a pending loan application. */
    @POST(APIEndPoint.LOANS + "/{loanId}?command=approve")
    suspend fun approveLoanApplication(
        @Path("loanId") loanId: Int,
        @Body loanApproval: LoanApproval?,
    ): GenericResponse

    /** Disburse an approved loan. */
    @POST(APIEndPoint.LOANS + "/{loanId}/?command=disburse")
    suspend fun disburseLoan(
        @Path("loanId") loanId: Int,
        @Body loanDisbursement: LoanDisbursement?,
    ): GenericResponse

    /** Submit a loan repayment transaction. */
    @POST(APIEndPoint.LOANS + "/{loanId}/transactions?command=repayment")
    suspend fun submitRepayment(
        @Path("loanId") loanId: Int,
        @Body loanRepaymentRequest: LoanRepaymentRequestEntity?,
    ): LoanRepaymentResponseEntity

    // ─────────────────────────────────────────────────────────────────────
    // region — Loan creation (individual + group)
    // ─────────────────────────────────────────────────────────────────────

    /** Create an individual-loan account. */
    @POST(APIEndPoint.CREATE_LOANS_ACCOUNTS)
    suspend fun createLoanAccount(@Body loansPayload: LoansPayload?): GenericResponse

    /** Calculate the repayment schedule for a draft loan (no account created). */
    @POST(APIEndPoint.CREATE_LOANS_ACCOUNTS + "?command=calculateLoanSchedule")
    suspend fun calculateLoanSchedule(@Body loansPayload: LoansPayload?): GenericResponse

    /** Retrieve the individual-loan creation template. */
    @GET(APIEndPoint.CREATE_LOANS_ACCOUNTS + "/template?templateType=individual")
    suspend fun getLoanCreateTemplate(
        @Query("clientId") clientId: Int,
        @Query("productId") productId: Int,
    ): LoanTemplate

    /** Create a group-loan account. */
    @POST(APIEndPoint.CREATE_LOANS_ACCOUNTS)
    suspend fun createGroupLoanAccount(@Body loansPayload: GroupLoanPayload?): Loan

    /** Retrieve the group-loan creation template. */
    @GET(APIEndPoint.CREATE_LOANS_ACCOUNTS + "/template?templateType=group")
    suspend fun getGroupLoanCreateTemplate(
        @Query("groupId") groupId: Int,
        @Query("productId") productId: Int,
    ): GroupLoanTemplate

    // ─────────────────────────────────────────────────────────────────────
    // region — Account transfer
    // ─────────────────────────────────────────────────────────────────────

    /** Retrieve the account-transfer template (eligible source/target combinations). */
    @GET(APIEndPoint.ACCOUNT_TRANSFERS + "/template")
    suspend fun getAccountTransferTemplate(
        @Query("fromClientId") fromClientId: Int,
        @Query("fromAccountType") fromAccountType: Int,
        @Query("fromAccountId") fromAccountId: Int,
        @Query("fromOfficeId") fromOfficeId: Int? = null,
        @Query("toOfficeId") toOfficeId: Int? = null,
        @Query("toClientId") toClientId: Int? = null,
        @Query("toAccountType") toAccountType: Int? = null,
        @Query("toAccountId") toAccountId: Int? = null,
    ): AccountTransferTemplate

    /** Submit an account-transfer request. */
    @POST(APIEndPoint.ACCOUNT_TRANSFERS)
    suspend fun submitAccountTransfer(
        @Body request: AccountTransferRequest,
    ): GenericResponse

    // ─────────────────────────────────────────────────────────────────────
    // region — Loan reschedule
    // ─────────────────────────────────────────────────────────────────────

    /** List reschedule requests for a loan. */
    @GET(APIEndPoint.RESCHEDULE_LOANS)
    suspend fun getLoanReschedules(
        @Query("loanId") loanId: Int,
    ): List<LoanRescheduleResponse>

    /** Retrieve the reschedule-template (allowed reasons, defaults). */
    @GET(APIEndPoint.RESCHEDULE_LOANS + "/template")
    suspend fun getRescheduleTemplate(): LoanRescheduleTemplate

    /** Submit a reschedule request. */
    @POST(APIEndPoint.RESCHEDULE_LOANS)
    suspend fun submitReschedule(
        @Body request: LoanRescheduleRequest,
    ): GenericResponse

    /** Approve a reschedule request. */
    @POST(APIEndPoint.RESCHEDULE_LOANS + "/{scheduleId}?command=approve")
    suspend fun approveReschedule(
        @Path("scheduleId") scheduleId: Int,
        @Body request: LoanRescheduleApprovalRequest,
    ): GenericResponse

    /** Reject a reschedule request. */
    @POST(APIEndPoint.RESCHEDULE_LOANS + "/{scheduleId}?command=reject")
    suspend fun rejectReschedule(
        @Path("scheduleId") scheduleId: Int,
        @Body request: LoanRescheduleRejectionRequest,
    ): GenericResponse
}
