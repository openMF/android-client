package com.mifos.core.network.datamanager

import com.mifos.core.data.LoansPayload
import com.mifos.core.databasehelper.DatabaseHelperLoan
import com.mifos.core.datastore.PrefManager
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.LoanDisbursement
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.organisation.LoanProducts
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.core.objects.templates.loans.LoanTemplate
import com.mifos.core.objects.templates.loans.LoanTransactionTemplate
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 15/07/16.
 */
@Singleton
class DataManagerLoan @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    val mDatabaseHelperLoan: DatabaseHelperLoan,
    private val prefManager: PrefManager
) {
    /**
     * This Method sending the Request to REST API if UserStatus is 0 and
     * get the LoanWithAssociation. The response is pass to the DatabaseHelperLoan
     * that save the response in Database with Observable.defer and next pass the response to
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
    fun getLoanById(loanId: Int): Observable<LoanWithAssociations> {
        return when (prefManager.userStatus) {
            false -> mBaseApiManager.loanApi.getLoanByIdWithAllAssociations(loanId)
            true ->
                /**
                 * Return LoanWithAssociation from DatabaseHelperLoan.
                 */
                mDatabaseHelperLoan.getLoanById(loanId)
        }
    }

    /**
     * This Method sending the Request to REST API and
     * get the LoanWithAssociation. The response is pass to the DatabaseHelperLoan
     * that save the response in Database with Observable.defer and next pass the response to
     * DataManager to pass to Presenter to show in the view.
     *
     * @param loanId Loan Id
     * @return LoanWithAssociations
     */
    fun syncLoanById(loanId: Int): Observable<LoanWithAssociations> {
        return mBaseApiManager.loanApi
            .getLoanByIdWithAllAssociations(loanId)
            .concatMap { loanWithAssociations ->
                mDatabaseHelperLoan.saveLoanById(
                    loanWithAssociations
                )
            }
    }

    val allLoans: Observable<List<LoanProducts>>
        get() = mBaseApiManager.loanApi.allLoans

    fun getLoansAccountTemplate(clientId: Int, productId: Int): Observable<LoanTemplate> {
        return mBaseApiManager.loanApi.getLoansAccountTemplate(clientId, productId)
    }

    fun createLoansAccount(loansPayload: LoansPayload?): Observable<Loans> {
        return mBaseApiManager.loanApi.createLoansAccount(loansPayload)
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
    fun getLoanRepayTemplate(loanId: Int): Observable<LoanRepaymentTemplate> {
        return when (prefManager.userStatus) {
            false -> mBaseApiManager.loanApi.getLoanRepaymentTemplate(loanId)
            true ->
                /**
                 * Return LoanRepaymentTemplate from DatabaseHelperLoan.
                 */
                mDatabaseHelperLoan.getLoanRepayTemplate(loanId)
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
    fun syncLoanRepaymentTemplate(loanId: Int): Observable<LoanRepaymentTemplate> {
        return mBaseApiManager.loanApi.getLoanRepaymentTemplate(loanId)
            .concatMap { loanRepaymentTemplate ->
                mDatabaseHelperLoan.saveLoanRepaymentTemplate(
                    loanId,
                    loanRepaymentTemplate
                )
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
    fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequest
    ): Observable<LoanRepaymentResponse> {
        return when (prefManager.userStatus) {
            false -> mBaseApiManager.loanApi.submitPayment(loanId, request)
                .concatMap { loanRepaymentResponse -> Observable.just(loanRepaymentResponse) }

            true ->
                /**
                 * Return LoanRepaymentResponse from DatabaseHelperLoan.
                 */
                mDatabaseHelperLoan.saveLoanRepaymentTransaction(loanId, request)
        }
    }

    /**
     * This Method send Query to DatabaseLoan and get the List<LoanRepayment> saved LoanRepayments.
     * These LoanRepayment are those LoanRepayment that are saved during the Offline LoanRepayment.
     *
     * @return List<LoanRepaymentRequest>
    </LoanRepaymentRequest></LoanRepayment> */
    val databaseLoanRepayments: Observable<List<LoanRepaymentRequest>>
        get() = mDatabaseHelperLoan.readAllLoanRepaymentTransaction()

    /**
     * This method request a Observable to DatabaseHelperLoan and DatabaseHelper check in
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
    fun getDatabaseLoanRepaymentByLoanId(loanId: Int): Observable<LoanRepaymentRequest> {
        return mDatabaseHelperLoan.getDatabaseLoanRepaymentByLoanId(loanId)
    }

    /**
     * This Method Load the PaymentTypeOption of any Loan, Saving, Reoccurring from Database table
     * PaymentTypeOption_Table.
     *
     * @return List<PaymentTypeOption>
    </PaymentTypeOption> */
    val paymentTypeOption: Observable<List<com.mifos.core.objects.PaymentTypeOption>>
        get() = mDatabaseHelperLoan.paymentTypeOption

    /**
     * This method sending request DatabaseHelper and Deleting the LoanRepayment with loanId
     * from LoanRepayment_Table and again loading list of LoanRepayment from Database.
     *
     * @param loanId Loan Id of the Loan
     * @return List<LoanRepaymentRequest>
    </LoanRepaymentRequest> */
    fun deleteAndUpdateLoanRepayments(loanId: Int): Observable<List<LoanRepaymentRequest>> {
        return mDatabaseHelperLoan.deleteAndUpdateLoanRepayments(loanId)
    }

    /**
     * This Method updating LoanRepayment in to Database and return the same LoanRepayment
     *
     * @param loanRepaymentRequest Updating LoanRepaymentRequest in to Database
     * @return LoanRepaymentRequest
     */
    fun updateLoanRepaymentTransaction(
        loanRepaymentRequest: LoanRepaymentRequest
    ): Observable<LoanRepaymentRequest> {
        return mDatabaseHelperLoan.updateLoanRepaymentTransaction(loanRepaymentRequest)
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
        command: String?
    ): Observable<LoanTransactionTemplate> {
        return mBaseApiManager.loanApi.getLoanTransactionTemplate(loanId, command)
    }

    fun disburseLoan(
        loanId: Int,
        loanDisbursement: LoanDisbursement?
    ): Observable<GenericResponse> {
        return mBaseApiManager.loanApi.disburseLoan(loanId, loanDisbursement)
    }
}