/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.helper

import com.mifos.room.dao.LoanDao
import com.mifos.room.entities.PaymentTypeOption
import com.mifos.room.entities.accounts.loans.ActualDisbursementDate
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequest
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponse
import com.mifos.room.entities.accounts.loans.LoanWithAssociations
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// TODO update docs
class LoanDaoHelper @Inject constructor(
    private val loanDao: LoanDao,
) {
    /**
     * This Method Saving the Loan In Database table according to Loan Id
     *
     * @param loanWithAssociations
     */
    fun saveLoanById(loanWithAssociations: LoanWithAssociations): Flow<LoanWithAssociations> {
        return flow {
            // Setting Loan Id in Summary Table
            val updatedSummary = loanWithAssociations.summary.copy(
                loanId = loanWithAssociations.id,
            )

            val updatedTimeline = loanWithAssociations.timeline.copy(
                loanId = loanWithAssociations.id,
                actualDisburseDate = loanWithAssociations.timeline.actualDisbursementDate?.let {
                    ActualDisbursementDate(
                        loanId = loanWithAssociations.id,
                        year = it.getOrNull(0),
                        month = it.getOrNull(1),
                        date = it.getOrNull(2),
                    )
                },
            )

            val updatedLoanWithAssociations = loanWithAssociations.copy(
                summary = updatedSummary,
                timeline = updatedTimeline,
            )

            loanDao.saveLoanWithAssociations(updatedLoanWithAssociations)
            emit(updatedLoanWithAssociations)
        }
    }

    /**
     * Retrieving LoanWithAssociation according to Loan Id from Database Table
     *
     * @param loanId
     * @return LoanWithAssociation
     */
    fun getLoanById(loanId: Int): Flow<LoanWithAssociations?> {
        return flow {
            val loan = loanDao.getLoanById(loanId).firstOrNull()

            val updatedLoan = loan?.let {
                val updatedTimeline = it.timeline.copy(
                    actualDisbursementDate = listOf(
                        it.timeline.actualDisburseDate?.year,
                        it.timeline.actualDisburseDate?.month,
                        it.timeline.actualDisburseDate?.date,
                    ),
                )
                it.copy(timeline = updatedTimeline)
            }

            emit(updatedLoan)
        }
    }

    /**
     * This Method Saving the Loan Transaction Offline in Database Table
     *
     * @param loanId               Loan Id
     * @param loanRepaymentRequest Request Body of Loan Transaction
     * @return LoanRepaymentResponse
     */
    suspend fun saveLoanRepaymentTransaction(
        loanId: Int,
        loanRepaymentRequest: LoanRepaymentRequest,
    ): LoanRepaymentResponse {
        // Setting Loan Id and Time Stamp
        val updatedLoanRepaymentRequest = loanRepaymentRequest.copy(
            loanId = loanId,
            timeStamp = System.currentTimeMillis() / 1000,
        )

        // Saving Transaction In Database Table
        loanDao.insertLoanRepaymentTransaction(updatedLoanRepaymentRequest)
        return LoanRepaymentResponse()
    }

    /**
     * Read All LoanRepaymentRequest from Database ascending Order of TimeStamp
     *
     * @return Flow<List<LoanRepaymentRequest>>
     </LoanRepaymentRequest> */
    fun readAllLoanRepaymentTransaction(): Flow<List<LoanRepaymentRequest>> {
        return loanDao.readAllLoanRepaymentTransaction()
    }

    /**
     * This Method send a query to Sqlite Database and get the LoanRepaymentRequest Where
     * Loan Id is Loan Id,
     *
     * This method used to check that LoanRepayment in offline mode,
     * Is already done with this loanId or not, If Yes then new Transaction can be made if
     * old one will be sync to server.
     *
     * @param loanId Loan Id
     * @return LoanRepaymentRequest by Loan Id
     */
    suspend fun getDatabaseLoanRepaymentByLoanId(loanId: Int): LoanRepaymentRequest? {
        return loanDao.getLoanRepaymentRequest(loanId)
    }

    /**
     * This Method updating the LoanRepayment to Database Table. this method will be called
     * whenever error will come during sync the LoanRepayment. This method saving the Error
     * message to the Table entry.
     *
     * @param loanRepaymentRequest LoanRepayment for update
     * @return LoanRepaymentRequest
     */
    suspend fun updateLoanRepaymentTransaction(
        loanRepaymentRequest: LoanRepaymentRequest,
    ) {
        loanDao.updateLoanRepaymentRequest(loanRepaymentRequest)
    }

    /**
     * This Method Deleting the LoanRepayment with the loanId and loading the
     * List<LoanRepaymentRequest> from Database and return to the DataManagerLoan
     * that synced LoanRepayment is deleted from Database and updated Database Table entries.
     *
     * @param loanId loan Id of the LoanRepayment
     * @return List<LoanRepaymentRequest>
     </LoanRepaymentRequest></LoanRepaymentRequest> */
    // TODO recheck logic in DatabaseHelperLoan.deleteAndUpdateLoanRepayments()
    fun deleteAndUpdateLoanRepayments(loanId: Int): Flow<List<LoanRepaymentRequest>> {
        return flow {
            loanDao.deleteLoanRepaymentByLoanId(loanId)
            emitAll(loanDao.readAllLoanRepaymentTransaction())
        }
    }

    /**
     * This method saves the LoanRepaymentTemplate in Database for making Transaction In offline
     * and As the Template is saved in the Database, its return the same LoanRepaymentTemplate.
     *
     * @param loanId                Loan Id of the LoanTemplate
     * @param loanRepaymentTemplate LoanRepaymentTemplate for saving in Database
     * @return LoanRepaymentTemplate
     */
    suspend fun saveLoanRepaymentTemplate(
        loanId: Int,
        loanRepaymentTemplate: LoanRepaymentTemplate,
    ): LoanRepaymentTemplate {
        val updatedLoanRepaymentTemplate = loanRepaymentTemplate.copy(loanId = loanId)

        updatedLoanRepaymentTemplate.paymentTypeOptions?.forEach { paymentTypeOption ->
            loanDao.insertPaymentTypeOption(paymentTypeOption)
        }

        loanDao.insertLoanRepaymentTemplate(updatedLoanRepaymentTemplate)
        return updatedLoanRepaymentTemplate
    }

    /**
     * This Method request a query to Database in PaymentTypeOption_Table with argument paymentType
     * and return the list of PaymentTypeOption
     *
     * @return List<PaymentTypeOption>
     </PaymentTypeOption> */

    val getAllPaymentTypeOption: Flow<List<PaymentTypeOption>> = loanDao.getPaymentTypeOptions()

    /**
     * This Method retrieve the LoanRepaymentTemplate from Database LoanRepaymentTemplate_Table
     * according to Loan Id and retrieve the PaymentTypeOptions according to templateType
     * LoanRepaymentTemplate
     *
     * @param loanId Loan Id of the LoanRepaymentTemplate.
     * @return LoanRepaymentTemplate from Database Query.
     */
    fun getLoanRepayTemplate(loanId: Int): Flow<LoanRepaymentTemplate?> {
        return flow {
            val loanRepaymentTemplate = loanDao.getLoanRepaymentTemplate(loanId)
            val paymentTypeOptions = loanDao.getPaymentTypeOptions().first()

            val updatedLoanRepaymentTemplate = loanRepaymentTemplate?.copy(
                paymentTypeOptions = paymentTypeOptions.toMutableList(),
            )

            emit(updatedLoanRepaymentTemplate)
        }
    }
}
