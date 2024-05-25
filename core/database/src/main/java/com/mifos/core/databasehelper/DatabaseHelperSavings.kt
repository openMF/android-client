package com.mifos.core.databasehelper

import com.mifos.core.objects.PaymentTypeOption
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest_Table
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations_Table
import com.mifos.core.objects.accounts.savings.SavingsTransactionDate
import com.mifos.core.objects.accounts.savings.Transaction
import com.mifos.core.objects.accounts.savings.Transaction_Table
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate_Table
import com.raizlabs.android.dbflow.sql.language.Delete
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable
import rx.functions.Action1
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 17/08/16.
 */
@Singleton
class DatabaseHelperSavings @Inject constructor() {
    /**
     * This Saving the SavingsAccountSummary template into Database
     * SavingsAccountWithAssociations_Table with the Primary key SavingsAccountId.
     *
     * @param savingsAccountWithAssociations SavingAccountSummary Template.
     * @return SavingsAccountWithAssociations.
     */
    fun saveSavingsAccount(
        savingsAccountWithAssociations: SavingsAccountWithAssociations
    ): Observable<SavingsAccountWithAssociations> {
        return Observable.defer {
            val transactions = savingsAccountWithAssociations.transactions
            if (transactions.isNotEmpty()) {
                Observable.from(transactions)
                    .subscribe(Action1 { transaction ->
                        val savingsTransactionDate = SavingsTransactionDate(
                            transaction?.id,
                            transaction?.date?.get(0),
                            transaction?.date?.get(1),
                            transaction?.date?.get(2)
                        )
                        transaction?.savingsAccountId = savingsAccountWithAssociations.id
                        transaction?.savingsTransactionDate = savingsTransactionDate
                        transaction?.save()
                    })
            }
            savingsAccountWithAssociations.summary?.savingsId = savingsAccountWithAssociations.id
            savingsAccountWithAssociations.save()
            Observable.just(savingsAccountWithAssociations)
        }
    }

    /**
     * This Method Read the SavingsAccountSummary Template from the
     * SavingsAccountWithAssociations_Table and give the response SavingsAccountWithAssociations.
     * If the Query returns the null its means SavingsAccountSummary template doest exist into the
     * Database with savingAccountId.
     *
     * @param savingsAccountId Savings Account Id
     * @return SavingsAccountWithAssociations SavingsAccountSummary Template.
     */
    fun readSavingsAccount(
        savingsAccountId: Int
    ): Observable<SavingsAccountWithAssociations> {
        return Observable.defer {
            val savingsAccountWithAssociations = SQLite.select()
                .from(SavingsAccountWithAssociations::class.java)
                .where(SavingsAccountWithAssociations_Table.id.eq(savingsAccountId))
                .querySingle()
            val transactions = SQLite.select()
                .from(Transaction::class.java)
                .where(Transaction_Table.savingsAccountId.eq(savingsAccountId))
                .queryList()
            Observable.from(transactions)
                .subscribe { transaction ->
                    transaction.date = listOf(
                        transaction.savingsTransactionDate?.year,
                        transaction.savingsTransactionDate?.month,
                        transaction.savingsTransactionDate?.day
                    )
                }
            if (savingsAccountWithAssociations != null) {
                savingsAccountWithAssociations.transactions = transactions
            }
            Observable.just(savingsAccountWithAssociations)
        }
    }

    /**
     * This Method is Saving the SavingsAccountTransactionTemplate into Database.
     *
     * @param savingsAccountTransactionTemplate SavingsAccountTransactionTemplate
     * @return SavingsAccountTransactionTemplate
     */
    fun saveSavingsAccountTransactionTemplate(
        savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate
    ): Observable<SavingsAccountTransactionTemplate> {
        return Observable.defer {
            Observable.from(savingsAccountTransactionTemplate.paymentTypeOptions)
                .subscribe { paymentTypeOption -> paymentTypeOption.save() }
            savingsAccountTransactionTemplate.save()
            Observable.just(savingsAccountTransactionTemplate)
        }
    }

    /**
     * This method is making Query to the Database SavingsAccountTransactionTemplate_Table and
     * with primary key SavingsAccount Id and returns the SavingsAccountTransactionTemplate
     * with Primary Key. If SavingsAccountTransactionTemplate does not exist it returns null.
     * otherwise returns SavingsAccountTransactionTemplate.
     *
     * @param savingsAccountId SavingAccount id
     * @return SavingsAccountTransactionTemplate
     */
    fun readSavingsAccountTransactionTemplate(
        savingsAccountId: Int
    ): Observable<SavingsAccountTransactionTemplate> {
        return Observable.defer {
            val savingsAccountTransactionTemplate = SQLite.select()
                .from(SavingsAccountTransactionTemplate::class.java)
                .where(SavingsAccountTransactionTemplate_Table.accountId.eq(savingsAccountId))
                .querySingle()
            val paymentTypeOptions = SQLite.select()
                .from(PaymentTypeOption::class.java)
                .queryList()
            if (savingsAccountTransactionTemplate != null) {
                savingsAccountTransactionTemplate.paymentTypeOptions = paymentTypeOptions
            }
            Observable.just(savingsAccountTransactionTemplate)
        }
    }

    /**
     * This Method saving the SavingAccountTransaction into Database
     * SavingsAccountTransactionRequest_Table , If user have no internet
     * connection or if user making transaction in offline mode.
     *
     * @param savingsAccountTransactionRequest SavingsAccountTransactionRequest Body
     * @param savingsAccountId                 SavingAccount Id
     * @param savingsAccountType               SavingAccountType
     * @param transactionType                  Transaction Type
     *
     * @return SavingsAccountTransactionResponse
     */
    fun saveSavingsAccountTransaction(
        savingsAccountType: String?, savingsAccountId: Int,
        transactionType: String?,
        savingsAccountTransactionRequest: SavingsAccountTransactionRequest
    ): Observable<SavingsAccountTransactionResponse> {
        return Observable.defer {
            savingsAccountTransactionRequest.savingAccountId = savingsAccountId
            savingsAccountTransactionRequest.savingsAccountType = savingsAccountType
            savingsAccountTransactionRequest.transactionType = transactionType
            savingsAccountTransactionRequest.save()
            Observable.just(SavingsAccountTransactionResponse())
        }
    }

    /**
     * This Method, retrieving SavingsAccountTransactionRequest with the Saving Id from Database
     * SavingsAccountTransactionRequest_Table. If no entry found with the SavingsAccount Id. It
     * returns null.
     *
     * @param savingsAccountId SavingAccount Id
     * @return SavingsAccountTransactionRequest
     */
    fun getSavingsAccountTransaction(
        savingsAccountId: Int
    ): Observable<SavingsAccountTransactionRequest> {
        return Observable.defer {
            val savingsAccountTransactionRequest = SQLite.select()
                .from(SavingsAccountTransactionRequest::class.java)
                .where(
                    SavingsAccountTransactionRequest_Table.savingAccountId.eq(
                        savingsAccountId
                    )
                )
                .querySingle()
            Observable.just(savingsAccountTransactionRequest)
        }
    }

    /**
     * This Method Load all Transactions from the SavingsAccountTransactionRequest_Table
     * and give the List<SavingsAccountTransactionRequest> response.
     *
     * @return List<SavingsAccountTransactionRequest>
    </SavingsAccountTransactionRequest></SavingsAccountTransactionRequest> */
    val allSavingsAccountTransaction: Observable<List<SavingsAccountTransactionRequest>>
        get() = Observable.defer {
            val savingsAccountTransactionRequests: List<SavingsAccountTransactionRequest> =
                SQLite.select()
                    .from(SavingsAccountTransactionRequest::class.java)
                    .queryList()
            Observable.just(savingsAccountTransactionRequests)
        }

    /**
     * This Method Deleting the SavingsAccountTransaction with the SavingsAccount Id and loading the
     * List<SavingsAccountTransactionRequest> from Database and return
     * List<SavingsAccountTransactionRequest> to DataManagerSavings and DataManagerSaving sync the
     * List<SavingsAccountTransactionRequest> to the SyncSavingsAccountTransaction.
     *
     * @param  savingsAccountId SavingsAccount Id
     * @return List<SavingsAccountTransactionRequest>
    </SavingsAccountTransactionRequest></SavingsAccountTransactionRequest></SavingsAccountTransactionRequest></SavingsAccountTransactionRequest> */
    fun deleteAndUpdateTransaction(
        savingsAccountId: Int
    ): Observable<List<SavingsAccountTransactionRequest>> {
        return Observable.defer { //Deleting Entry from SavingsAccountTransactionRequest_Table with SavingsAccountId
            Delete.table(
                SavingsAccountTransactionRequest::class.java,
                SavingsAccountTransactionRequest_Table.savingAccountId.eq(savingsAccountId)
            )
            val savingsAccountTransactionRequests = SQLite.select()
                .from(SavingsAccountTransactionRequest::class.java)
                .queryList()
            Observable.just(savingsAccountTransactionRequests)
        }
    }

    /**
     * This Method updating the SavingsAccountTransactionRequest to Database Table.
     * this method will be called whenever error will come during sync the LoanRepayment. This
     * method saving the Error message to the Error Table Column .
     *
     * @param savingsAccountTransactionRequest SavingsAccountTransaction to update
     * @return SavingsAccountTransactionRequest
     */
    fun updateSavingsAccountTransaction(
        savingsAccountTransactionRequest: SavingsAccountTransactionRequest
    ): Observable<SavingsAccountTransactionRequest> {
        return Observable.defer {
            savingsAccountTransactionRequest.update()
            Observable.just(savingsAccountTransactionRequest)
        }
    }
}