/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.common.utils.DateHelper
import com.mifos.core.common.utils.extractErrorMessage
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.model.objects.account.loan.RepaymentSchedule
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleApprovalRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleRejectionRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleResponse
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleTemplate
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferRequest
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferResponse
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferTemplate
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.dto.loans.CreateGuarantorResponseDto
import com.mifos.core.network.dto.loans.GuarantorRequestDto
import com.mifos.core.network.dto.loans.LoanChargeOffRequestDto
import com.mifos.core.network.dto.loans.LoanChargeOffResponseDto
import com.mifos.core.network.dto.loans.RejectLoanRequestDto
import com.mifos.core.network.dto.loans.RejectLoanResponseDto
import com.mifos.core.network.dto.loans.assignLoanOfficer.AssignLoanOfficerRequestDto
import com.mifos.core.network.dto.loans.assignLoanOfficer.AssignLoanOfficerResponseDto
import com.mifos.core.network.dto.loans.disburse.LoanDisburseRequestDto
import com.mifos.core.network.dto.loans.disburse.LoanDisburseResponseDto
import com.mifos.core.network.dto.loans.template.GuarantorAccountTemplateDto
import com.mifos.core.network.dto.loans.template.GuarantorTemplateDto
import com.mifos.core.network.dto.loans.template.LoanChargeOffTemplateDto
import com.mifos.core.network.dto.loans.template.LoanDisburseTemplateDto
import com.mifos.core.network.dto.loans.template.LoanOfficerOptionsTemplateDto
import com.mifos.core.network.mappers.loan.LoanAccountMapper
import com.mifos.core.network.mappers.loan.toDomain
import com.mifos.core.network.dto.loan.CreditBalanceRefundRequestDto
import com.mifos.core.network.dto.loan.CreditBalanceRefundResponseDto
import com.mifos.core.network.model.LoansPayload
import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.LoanRefundDetailsEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.accounts.loans.toLoanRefundDetailsEntity
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import com.mifos.room.entities.templates.loans.LoanTemplate
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate
import com.mifos.room.helper.LoanDaoHelper
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * Created by Rajan Maurya on 15/07/16.
 */
class DataManagerLoan(
    val mBaseApiManager: BaseApiManager,
//    val mDatabaseHelperLoan: DatabaseHelperLoan,
    val loanDaoHelper: LoanDaoHelper,
    private val prefManager: UserPreferencesRepository,
) {
    /**
     * This Method sending the Request to REST API if UserStatus is 0 and
     * get the LoanWithAssociation. The response is pass to the DatabaseHelperLoan
     * that save the response in Database with Flow.defer and next pass the response to
     * DataManager to pass to Presenter to show in the view.
     *
     *
     * If UserStatus is 1 means User is in the Offline mode, SO it send request to
     * DatabaseHelperLon to fetch Data from Database and give back to DataManager and DataManager
     * gives to Presenter to show on the view.
     *
     * @param loanId Loan Id of the Loan
     * @return LoanWithAssociation
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLoanById(loanId: Int): Flow<LoanWithAssociations?> {
        return prefManager.userInfo.flatMapLatest { userData ->
            when (userData.userStatus) {
                false -> flow {
                    emit(
                        mBaseApiManager.loanService.getLoanByIdWithAllAssociations(
                            loanId,
                        ).toDomain(),
                    )
                }

                true ->
                    /**
                     * offline Mode, Return LoanWithAssociation from LoanDaoHelper.
                     */
                    loanDaoHelper.getLoanById(loanId).map { entity -> entity?.let(LoanAccountMapper::mapFromEntity) }
            }
        }
    }

    /**
     * This Method sending the Request to REST API and
     * get the LoanWithAssociation. The response is pass to the DatabaseHelperLoan
     * that save the response in Database with Flow.defer and next pass the response to
     * DataManager to pass to Presenter to show in the view.
     *
     * @param loanId Loan Id
     * @return LoanWithAssociations
     */
    fun syncLoanById(loanId: Int): Flow<LoanWithAssociations> {
        return flow {
            val loanWithAssociations =
                mBaseApiManager.loanService.getLoanByIdWithAllAssociations(loanId).toDomain()

            val loanWithAssociationsEntity = LoanAccountMapper.mapToEntity(loanWithAssociations)

            loanDaoHelper.saveLoanById(loanWithAssociationsEntity).collect()

            emit(loanWithAssociations)
        }
    }

    val allLoans: Flow<List<com.mifos.core.model.objects.organisations.LoanProducts>>
        get() = mBaseApiManager.loanService.getAllLoans()

    fun getLoansAccountTemplate(clientId: Int, productId: Int): Flow<LoanTemplate> {
        return mBaseApiManager.loanService.getLoansAccountTemplate(clientId, productId)
    }

    suspend fun getGuarantorTemplate(loanId: Int): GuarantorTemplateDto {
        return mBaseApiManager.loanService.getGuarantorTemplate(loanId)
    }

    suspend fun createGuarantor(
        loanId: Int,
        request: GuarantorRequestDto,
    ): CreateGuarantorResponseDto {
        return mBaseApiManager.loanService.createGuarantor(loanId, request)
    }

    suspend fun getGuarantorAccountTemplate(
        loanId: Int,
        clientId: Int,
    ): GuarantorAccountTemplateDto {
        return mBaseApiManager.loanService.getGuarantorAccountTemplate(
            loanId = loanId,
            clientId = clientId,
        )
    }

    suspend fun getChargeOffTemplate(loanId: Int): LoanChargeOffTemplateDto {
        return mBaseApiManager.loanService.getChargeOffTemplate(loanId)
    }

    suspend fun chargeOff(
        loanId: Int,
        loanChargeOffRequestDto: LoanChargeOffRequestDto,
    ): LoanChargeOffResponseDto {
        return mBaseApiManager.loanService.chargeOff(loanId, loanChargeOffRequestDto)
    }

    suspend fun rejectLoan(
        loanId: Int,
        request: RejectLoanRequestDto,
    ): RejectLoanResponseDto {
        return mBaseApiManager.loanService.rejectLoan(loanId, request)
    }

    fun createLoansAccount(loansPayload: LoansPayload?): Flow<HttpResponse> {
        return mBaseApiManager.loanService.createLoansAccount(loansPayload).map { response ->

            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)

                throw IllegalStateException(errorMessage)
            }

            response
        }
    }

    /**
     * This Method to request the LoanRepaymentTemplate according to Loan Id and get
     * LoanRepaymentTemplate in Response. This method work in both mode Online and Offline.
     * if PrefManager.getUserStatus() is 0, means user is in Online Mode the Request goes to the
     * Server End Point directly. Here is End Point :
     * {https://demo.openmf.org/fineract-provider/api/v1/loans/{loanId}/transactions/template
     * ?command=repayment}
     * and get LoanRepaymentTemplate in response and then call the
     * mDatabaseHelperLoan.saveLoanRepaymentTemplate(loanId,loanRepaymentTemplate); to save the
     * Template into Database for accessing in the Offline.
     *
     *
     * if PrefManager.getUserStatus() is 1, It means user is Offline Mode, Request goes to the
     * mDatabaseHelperLoan to load the LoanRepaymentTemplate according loanId and gives the
     * LoanRepaymentTemplate in Response.
     *
     * @param loanId Loan Id of the LoanRepaymentTemplate
     * @return LoanRepaymentTemplate
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLoanRepayTemplate(loanId: Int): Flow<LoanRepaymentTemplateEntity?> {
        return prefManager.userInfo.flatMapLatest { userData ->
            if (userData.userStatus) {
                loanDaoHelper.getLoanRepayTemplate(loanId)
            } else {
                flow {
                    val result = mBaseApiManager.loanService.getLoanRepaymentTemplate(loanId)
                    emit(result)
                }
            }
        }
    }

    /**
     * This Method to request the LoanRepaymentTemplate according to Loan Id and get
     * LoanRepaymentTemplate in Response. Request goes to the Server End Point directly.
     * Here is End Point :
     * {https://demo.openmf.org/fineract-provider/api/v1/loans/{loanId}/transactions/template
     * ?command=repayment}
     * and get LoanRepaymentTemplate in response and then call the
     * mDatabaseHelperLoan.saveLoanRepaymentTemplate(loanId,loanRepaymentTemplate); to save the
     * Template into Database for accessing in the Offline.
     *
     * @param loanId Loan Id
     * @return LoanRepaymentTemplate
     */

    fun syncLoanRepaymentTemplate(loanId: Int): Flow<LoanRepaymentTemplateEntity> {
        return flow {
            mBaseApiManager.loanService.getLoanRepaymentTemplate(loanId).also {
                loanDaoHelper.saveLoanRepaymentTemplate(loanId, it)
            }
        }
    }

    /**
     * This Method For submitting the Loan Payment. This Method have two mode, One if Online when
     * PrefManager.getUserStatus() is 0, Whenever User Online the Post request going to Server
     * Directly, here is the End Point
     * {https://demo.openmf.org/fineract-provider/api/v1/loans/{loanId}/transactions?command
     * =repayment}
     * and get the LoanRepaymentResponse in response of Successful Transaction.
     * And Whenever User in Offline Mode the Request goes to DatabaseHelperLoan and DatabaseHelper
     * Save the Transaction on Database and in Response give the Empty LoanRepaymentResponse.
     *
     * @param loanId  Loan id of The Loan
     * @param request Request Body of POST Request
     * @return LoanRepaymentResponse
     */
    suspend fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequestEntity,
    ): LoanRepaymentResponseEntity {
        return when (prefManager.userInfo.first().userStatus) {
            false -> mBaseApiManager.loanService.submitPayment(loanId, request)

            true ->
                /**
                 * Return LoanRepaymentResponse from DatabaseHelperLoan.
                 */
                loanDaoHelper.saveLoanRepaymentTransaction(loanId, request)
        }
    }

    /**
     * This Method submits a credit balance refund transaction for a loan account.
     * Calls the network API directly.
     * Endpoint: POST /loans/{loanId}/transactions?command=creditBalanceRefund
     * Returns LoanRepaymentResponseEntity with the transaction details.
     *
     * @param loanId Loan id of the loan
     * @param request Request body containing refund transaction details
     * @return LoanRepaymentResponseEntity
     */
    suspend fun submitCreditBalanceRefund(
        loanId: Int,
        request: CreditBalanceRefundRequestDto,
    ): CreditBalanceRefundResponseDto {
        val response = mBaseApiManager.loanService.submitCreditBalanceRefund(loanId, request)

        if (!response.status.isSuccess()) {
            val errorMessage = extractErrorMessage(response)
            throw IllegalStateException(errorMessage)
        }

        return Json.decodeFromString(response.bodyAsText())
    }

    /**
     * Fetches loan refund details for the credit balance refund form.
     * In online mode: fetches from API and caches in database.
     * If network fails in online mode, falls back to database cache.
     * In offline mode: reads from database cache.
     *
     * @param loanId Loan id
     * @return LoanRefundDetailsEntity with refund form data
     */
    suspend fun getLoanRefundDetails(loanId: Int): LoanRefundDetailsEntity? {
        val userStatus = prefManager.userInfo.first().userStatus
        return if (!userStatus) {
            try {
                val loan = mBaseApiManager.loanService.getLoanByIdWithAllAssociations(loanId)
                val template = mBaseApiManager.loanService
                    .getLoanTransactionTemplate(loanId, APIEndPoint.CREDIT_BALANCE_REFUND)
                    .first()

                val formattedDate = DateHelper.getDateAsString(template.date)

                loan.toLoanRefundDetailsEntity(formattedDate).also {
                    loanDaoHelper.saveLoanRefundDetails(it)
                }
            } catch (e: Exception) {
                loanDaoHelper.getLoanRefundDetails(loanId).first()
            }
        } else {
            loanDaoHelper.getLoanRefundDetails(loanId).first()
        }
    }

    /**
     * This method deletes the LoanRefundDetails from Database according to Loan Id.
     *
     * @param loanId Loan Id of the LoanRefundDetails to delete
     */
    suspend fun deleteLoanRefundDetails(loanId: Int) {
        loanDaoHelper.deleteLoanRefundDetails(loanId)
    }

    /**
     * This Method send Query to DatabaseLoan and get the List<LoanRepayment> saved LoanRepayments.
     * These LoanRepayment are those LoanRepayment that are saved during the Offline LoanRepayment.
     *
     * @return List<LoanRepaymentRequest>
     *
     </LoanRepaymentRequest></LoanRepayment> */
    val databaseLoanRepayments: Flow<List<LoanRepaymentRequestEntity>>
        get() = loanDaoHelper.readAllLoanRepaymentTransaction()

    /**
     * This method request a Flow to DatabaseHelperLoan and DatabaseHelper check in
     * LoanRepayment Table that with this loan Id, any entry is available or not.
     *
     *
     * If yes, It means with this loan id already a LoanRepayment had been done. and return the
     * LoanRepayment, Now User cannot make new LoanRepayment till that He/She will not sync the
     * previous LoanRepayment.
     *
     *
     * If NO, this will return null that represent that there is no previous LoanRepayment In
     * Database with this Loan Id, user is able to make new one.
     *
     * @param loanId
     * @return LoanRepayment with this Loan Id reference.
     */
    fun getDatabaseLoanRepaymentByLoanId(loanId: Int): Flow<LoanRepaymentRequestEntity?> {
        return flow {
            emit(loanDaoHelper.getDatabaseLoanRepaymentByLoanId(loanId))
        }
    }

    /**
     * This Method Load the PaymentTypeOption of any Loan, Saving, Reoccurring from Database table
     * PaymentTypeOption_Table.
     *
     * @return List<PaymentTypeOption>
     </PaymentTypeOption> */
    val paymentTypeOption: Flow<List<PaymentTypeOptionEntity>>
        get() = loanDaoHelper.getAllPaymentTypeOption

    /**
     * This method sending request DatabaseHelper and Deleting the LoanRepayment with loanId
     * from LoanRepayment_Table and again loading list of LoanRepayment from Database.
     *
     * @param loanId Loan Id of the Loan
     * @return List<LoanRepaymentRequest>
     </LoanRepaymentRequest> */
    fun deleteAndUpdateLoanRepayments(loanId: Int): Flow<List<LoanRepaymentRequestEntity>> {
        return loanDaoHelper.deleteAndUpdateLoanRepayments(loanId)
    }

    /**
     * This Method updating LoanRepayment in to Database and return the same LoanRepayment
     *
     * @param loanRepaymentRequest Updating LoanRepaymentRequest in to Database
     * @return LoanRepaymentRequest
     */
    fun updateLoanRepaymentTransaction(
        loanRepaymentRequest: LoanRepaymentRequestEntity,
    ): Flow<LoanRepaymentRequestEntity> {
        return flow {
            loanDaoHelper.updateLoanRepaymentTransaction(loanRepaymentRequest)
            emit(loanRepaymentRequest)
        }
    }

    /**
     * This is for fetching the any type of template.
     * Example:
     * 1. disburse
     * 2. repayment
     * 3. waiver
     * 4. refundbycash
     * 5. foreclosure
     *
     * @param loanId Loan Id
     * @param command Template Type
     * @return
     */
    fun getLoanTransactionTemplate(
        loanId: Int,
        command: String?,
    ): Flow<LoanTransactionTemplate> {
        return mBaseApiManager.loanService.getLoanTransactionTemplate(loanId, command)
    }

    /**
     * Account Transfer Methods
     */

    /**
     * Retrieve account transfer template for populating UI dropdowns
     *
     * @param fromOfficeId Source office ID
     * @param fromClientId Source client ID
     * @param fromAccountType Source account type ID
     * @param fromAccountId Source account ID
     * @param toOfficeId Destination office ID (optional)
     * @param toClientId Destination client ID (optional)
     * @param toAccountType Destination account type ID (optional)
     * @return AccountTransferTemplate with available options
     */
    fun getAccountTransferTemplate(
        fromClientId: Int,
        fromAccountType: Int,
        fromAccountId: Int,
        toOfficeId: Int? = null,
        fromOfficeId: Int? = null,
        toClientId: Int? = null,
        toAccountType: Int? = null,
    ): Flow<AccountTransferTemplate> {
        return mBaseApiManager.loanService.getAccountTransferTemplate(
            fromOfficeId = fromOfficeId,
            fromClientId = fromClientId,
            fromAccountType = fromAccountType,
            fromAccountId = fromAccountId,
            toOfficeId = toOfficeId,
            toClientId = toClientId,
            toAccountType = toAccountType,
        )
    }

    /**
     * Submit an account transfer
     *
     * @param request Account transfer request payload
     * @return AccountTransferResponse with transfer details
     * @throws IllegalStateException if the request fails with the error message
     */
    suspend fun submitAccountTransfer(
        request: AccountTransferRequest,
    ): AccountTransferResponse {
        val response = mBaseApiManager.loanService.submitAccountTransfer(request)
        if (!response.status.isSuccess()) {
            val errorMessage = extractErrorMessage(response)
            throw IllegalStateException(errorMessage)
        }
        return Json.decodeFromString<AccountTransferResponse>(response.bodyAsText())
    }

    /**
     * Calculate loan repayment schedule without creating the loan.
     * Used to preview the schedule before submitting the loan application.
     *
     * @param loansPayload The loan parameters to calculate the schedule for
     * @return LoanWithAssociationsEntity containing the calculated repayment schedule
     */
    fun calculateLoanSchedule(loansPayload: LoansPayload): Flow<RepaymentSchedule> {
        return mBaseApiManager.loanService.calculateLoanSchedule(loansPayload).map { response ->
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)

                throw IllegalStateException(errorMessage)
            }

            Json {
                ignoreUnknownKeys = true
            }.decodeFromString<RepaymentSchedule>(response.bodyAsText())
        }
    }

    fun getLoanReschedules(loanId: Int): Flow<List<LoanRescheduleResponse>> {
        return mBaseApiManager.loanService.getLoanReschedules(loanId)
    }

    fun getLoanRescheduleTemplate(): Flow<LoanRescheduleTemplate> {
        return mBaseApiManager.loanService.getLoanRescheduleTemplate()
    }

    suspend fun submitLoanReschedule(request: LoanRescheduleRequest): GenericResponse {
        val response = mBaseApiManager.loanService.submitLoanReschedule(request)
        if (!response.status.isSuccess()) {
            val errorMessage = extractErrorMessage(response)
            throw IllegalStateException(errorMessage)
        }
        return Json {
            ignoreUnknownKeys = true
        }.decodeFromString<GenericResponse>(response.bodyAsText())
    }

    suspend fun approveLoanReschedule(scheduleId: Int, request: LoanRescheduleApprovalRequest) {
        val response = mBaseApiManager.loanService.approveLoanReschedule(
            scheduleId = scheduleId,
            request = request,
        )
        if (!response.status.isSuccess()) {
            throw IllegalStateException(extractErrorMessage(response))
        }
    }

    suspend fun rejectLoanReschedule(scheduleId: Int, request: LoanRescheduleRejectionRequest) {
        val response = mBaseApiManager.loanService.rejectLoanReschedule(
            scheduleId = scheduleId,
            request = request,
        )
        if (!response.status.isSuccess()) {
            throw IllegalStateException(extractErrorMessage(response))
        }
    }

    suspend fun getLoanOfficerTemplate(loanId: Int): LoanOfficerOptionsTemplateDto {
        return mBaseApiManager.loanService.getLoanOfficerTemplate(loanId)
    }

    suspend fun assignLoanOfficer(
        loanId: Int,
        request: AssignLoanOfficerRequestDto,
    ): AssignLoanOfficerResponseDto {
        return mBaseApiManager.loanService.assignLoanOfficer(loanId = loanId, request = request)
    }

    suspend fun getDisburseTemplate(loanId: Int): LoanDisburseTemplateDto {
        return mBaseApiManager.loanService.getDisburseTemplate(loanId)
    }

    suspend fun disburse(
        loanId: Int,
        loanDisburseRequestDto: LoanDisburseRequestDto,
    ): LoanDisburseResponseDto {
        return mBaseApiManager.loanService.disburse(loanId, loanDisburseRequestDto)
    }
}
