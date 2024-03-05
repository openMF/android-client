package com.mifos.core.databasehelper

import com.mifos.core.objects.PaymentTypeOption
import com.mifos.core.objects.accounts.loan.ActualDisbursementDate
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest_Table
import com.mifos.core.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.core.objects.accounts.loan.LoanWithAssociations_Table
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate_Table
import com.raizlabs.android.dbflow.sql.language.Delete
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable
import rx.functions.Func0
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 15/07/16.
 */
@Singleton
class DatabaseHelperLoan @Inject constructor() {
    /**
     * This Method Saving the Loan In Database table according to Loan Id
     *
     * @param loanWithAssociations
     * @return LoanWithAssociation
     */
    fun saveLoanById(loanWithAssociations: LoanWithAssociations): Observable<LoanWithAssociations> {
        return Observable.defer(Func0 { //Setting Loan Id in Summary Table
            loanWithAssociations.summary.loanId = loanWithAssociations.id

            // Setting Timeline
            val timeline = loanWithAssociations.timeline
            timeline.loanId = loanWithAssociations.id

            //Setting ActualDisbursement in Table
            val actualDisbursementDate = ActualDisbursementDate(
                loanWithAssociations.id,
                loanWithAssociations.timeline.actualDisbursementDate?.get(0),
                loanWithAssociations.timeline.actualDisbursementDate?.get(1),
                loanWithAssociations.timeline.actualDisbursementDate?.get(2)
            )
            timeline.actualDisburseDate = actualDisbursementDate
            loanWithAssociations.timeline = timeline

            // save LoanWithAssociation
            loanWithAssociations.save()
            Observable.just(loanWithAssociations)
        })
    }

    /**
     * Retrieving LoanWithAssociation according to Loan Id from Database Table
     *
     * @param loanId
     * @return LoanWithAssociation
     */
    fun getLoanById(loanId: Int): Observable<LoanWithAssociations> {
        return Observable.defer {
            val loanWithAssociations = SQLite.select()
                .from(LoanWithAssociations::class.java)
                .where(LoanWithAssociations_Table.id.eq(loanId))
                .querySingle()

            // Setting the actualDisbursementDate
            if (loanWithAssociations != null) {
                loanWithAssociations.timeline.actualDisbursementDate = listOf(
                    loanWithAssociations.timeline.actualDisburseDate
                        ?.year, loanWithAssociations.timeline
                        .actualDisburseDate?.month,
                    loanWithAssociations
                        .timeline.actualDisburseDate?.date
                )
            }
            Observable.just(loanWithAssociations)
        }
    }

    /**
     * This Method Saving the Loan Transaction Offline in Database Table
     *
     * @param loanId               Loan Id
     * @param loanRepaymentRequest Request Body of Loan Transaction
     * @return LoanRepaymentResponse
     */
    fun saveLoanRepaymentTransaction(
        loanId: Int, loanRepaymentRequest: LoanRepaymentRequest
    ): Observable<LoanRepaymentResponse> {
        return Observable.defer { //Setting Loan Id and Time Stamp
            loanRepaymentRequest.loanId = loanId
            loanRepaymentRequest.timeStamp = (System.currentTimeMillis() / 1000)

            //Saving Transaction In Database Table
            loanRepaymentRequest.save()
            Observable.just(LoanRepaymentResponse())
        }
    }

    /**
     * Read All LoanRepaymentRequest from Database ascending Order of TimeStamp
     *
     * @return List<LoanRepaymentRequest>
    </LoanRepaymentRequest> */
    fun readAllLoanRepaymentTransaction(): Observable<List<LoanRepaymentRequest>> {
        return Observable.defer {
            val loanRepaymentRequests = SQLite.select()
                .from(LoanRepaymentRequest::class.java)
                .orderBy(LoanRepaymentRequest_Table.timeStamp, true)
                .queryList()
            Observable.just(loanRepaymentRequests)
        }
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
    fun getDatabaseLoanRepaymentByLoanId(loanId: Int): Observable<LoanRepaymentRequest> {
        return Observable.defer {
            val loanRepaymentRequest = SQLite.select()
                .from(LoanRepaymentRequest::class.java)
                .where(LoanRepaymentRequest_Table.loanId.eq(loanId))
                .querySingle()
            Observable.just(loanRepaymentRequest)
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
    fun saveLoanRepaymentTemplate(
        loanId: Int, loanRepaymentTemplate: LoanRepaymentTemplate
    ): Observable<LoanRepaymentTemplate> {
        return Observable.defer {
            loanRepaymentTemplate.loanId = loanId
            for (paymentTypeOption: PaymentTypeOption in loanRepaymentTemplate.paymentTypeOptions!!) {
                paymentTypeOption.save()
            }
            loanRepaymentTemplate.save()
            Observable.just(loanRepaymentTemplate)
        }
    }

    /**
     * This Method retrieve the LoanRepaymentTemplate from Database LoanRepaymentTemplate_Table
     * according to Loan Id and retrieve the PaymentTypeOptions according to templateType
     * LoanRepaymentTemplate
     *
     * @param loanId Loan Id of the LoanRepaymentTemplate.
     * @return LoanRepaymentTemplate from Database Query.
     */
    fun getLoanRepayTemplate(loanId: Int): Observable<LoanRepaymentTemplate> {
        return Observable.defer {
            val loanRepaymentTemplate = SQLite.select()
                .from(LoanRepaymentTemplate::class.java)
                .where(LoanRepaymentTemplate_Table.loanId.eq(loanId))
                .querySingle()
            val paymentTypeOptions = SQLite.select()
                .from(PaymentTypeOption::class.java)
                .queryList()
            if (loanRepaymentTemplate != null) {
                loanRepaymentTemplate.paymentTypeOptions = paymentTypeOptions
            }
            Observable.just(loanRepaymentTemplate)
        }
    }

    /**
     * This Method request a query to Database in PaymentTypeOption_Table with argument paymentType
     * and return the list of PaymentTypeOption
     *
     * @return List<PaymentTypeOption>
    </PaymentTypeOption> */
    val paymentTypeOption: Observable<List<PaymentTypeOption>>
        get() = Observable.defer {
            val paymentTypeOptions: List<PaymentTypeOption> = SQLite.select()
                .from(PaymentTypeOption::class.java)
                .queryList()
            Observable.just(paymentTypeOptions)
        }

    /**
     * This Method Deleting the LoanRepayment with the loanId and loading the
     * List<LoanRepaymentRequest> from Database and return to the DataManagerLoan
     * that synced LoanRepayment is deleted from Database and updated Database Table entries.
     *
     * @param loanId loan Id of the LoanRepayment
     * @return List<LoanRepaymentRequest>
    </LoanRepaymentRequest></LoanRepaymentRequest> */
    fun deleteAndUpdateLoanRepayments(loanId: Int): Observable<List<LoanRepaymentRequest>> {
        return Observable.defer {
            Delete.table(
                LoanRepaymentRequest::class.java,
                LoanRepaymentRequest_Table.loanId.eq(loanId)
            )
            val loanRepaymentRequests = SQLite.select()
                .from(LoanRepaymentRequest::class.java)
                .orderBy(LoanRepaymentRequest_Table.timeStamp, true)
                .queryList()
            Observable.just(loanRepaymentRequests)
        }
    }

    /**
     * This Method updating the LoanRepayment to Database Table. this method will be called
     * whenever error will come during sync the LoanRepayment. This method saving the Error
     * message to the Table entry.
     *
     * @param loanRepaymentRequest LoanRepayment for update
     * @return LoanRepaymentRequest
     */
    fun updateLoanRepaymentTransaction(
        loanRepaymentRequest: LoanRepaymentRequest
    ): Observable<LoanRepaymentRequest> {
        return Observable.defer {
            loanRepaymentRequest.update()
            Observable.just(loanRepaymentRequest)
        }
    }
}