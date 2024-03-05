package com.mifos.core.network.datamanager

import com.mifos.core.data.SavingsPayload
import com.mifos.core.databasehelper.DatabaseHelperSavings
import com.mifos.core.datastore.PrefManager
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.SavingsApproval
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.objects.client.Savings
import com.mifos.core.objects.organisation.ProductSavings
import com.mifos.core.objects.templates.savings.SavingProductsTemplate
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by Rajan Maurya on 17/08/16.
 */
@Singleton
class DataManagerSavings @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    val mDatabaseHelperSavings: DatabaseHelperSavings,
    private val prefManager: PrefManager
) {
    /**
     * This Method Make the Request to the REST API
     * https://demo.openmf.org/fineract-provider/api/v1/savingsaccounts/{savingsAccountId}
     * ?associations={all or transactions or charges}
     * and fetch savings application/account.
     *
     * @param type             Type of the SavingsAccount
     * @param savingsAccountId Savings Account Id
     * @param association      {all or transactions or charges}
     * 'all': Gets data related to all associations e.g. ?associations=all.
     * 'transactions': Gets data related to transactions on the account e.g.
     * ?associations=transactions
     * 'charges':Savings Account charges data.
     * @return SavingsAccountWithAssociations
     */
    fun getSavingsAccount(
        type: String?, savingsAccountId: Int, association: String?
    ): Observable<SavingsAccountWithAssociations> {
        return when (prefManager.userStatus) {
            false -> mBaseApiManager.savingsApi.getSavingsAccountWithAssociations(
                type, savingsAccountId, association
            )

            true ->
                /**
                 * Return SavingsAccountWithAssociations from DatabaseHelperSavings.
                 */
                mDatabaseHelperSavings.readSavingsAccount(savingsAccountId)
        }
    }

    /**
     * This Method Make the Request to the REST API
     * https://demo.openmf.org/fineract-provider/api/v1/savingsaccounts/{savingsAccountId}
     * ?associations={all or transactions or charges}
     * and fetch savings application/account. and After Fetching SavingsAccount DataManager
     * send Fetched SavingsAccount to DatabaseHelperSavings to save the SavingsAccount in Database
     * for Offline use and DatabaseHelperSavings returns saved SavingsAccount.
     *
     * @param type             Type of the SavingsAccount
     * @param savingsAccountId Savings Account Id
     * @param association      {all or transactions or charges}
     * 'all': Gets data related to all associations e.g. ?associations=all.
     * 'transactions': Gets data related to transactions on the account e.g.
     * ?associations=transactions
     * 'charges':Savings Account charges data.
     * @return SavingsAccountWithAssociations
     */
    fun syncSavingsAccount(
        type: String?, savingsAccountId: Int, association: String?
    ): Observable<SavingsAccountWithAssociations> {
        return mBaseApiManager.savingsApi.getSavingsAccountWithAssociations(
            type,
            savingsAccountId, association
        )
            .concatMap { savingsAccountWithAssociations ->
                mDatabaseHelperSavings.saveSavingsAccount(
                    savingsAccountWithAssociations
                )
            }
    }

    fun activateSavings(
        savingsAccountId: Int,
        request: HashMap<String, String>
    ): Observable<GenericResponse> {
        return mBaseApiManager.savingsApi.activateSavings(savingsAccountId, request)
    }

    /**
     * This Method make the Request to REST API, if the User Status if Online at:
     * https://demo.openmf.org/fineract-provider/api/v1/{savingsAccountType}/{savingsAccountId}
     * /transactions/template.
     * using retrofit 2 with SavingsAccountService and the SavingsAccountTransactionTemplate in
     * response.
     * If User Status is Offline then It make the request to the DatabaseHelperSavings and get
     * the SavingAccount TransactionTemplate According to SavingAccount Id
     *
     * @param type             Savings Account Type Example : savingsaccounts
     * @param savingsAccountId SavingsAccount Id
     * @param transactionType  Transaction Type Example : 'Deposit', 'Withdrawal'
     * @return SavingsAccountTransactionTemplate
     */
    fun getSavingsAccountTransactionTemplate(
        type: String?, savingsAccountId: Int, transactionType: String?
    ): Observable<SavingsAccountTransactionTemplate> {
        return when (prefManager.userStatus) {
            false -> mBaseApiManager.savingsApi.getSavingsAccountTransactionTemplate(
                type,
                savingsAccountId, transactionType
            )

            true ->
                /**
                 * Return SavingsAccountTransactionTemplate from DatabaseHelperSavings.
                 */
                mDatabaseHelperSavings.readSavingsAccountTransactionTemplate(savingsAccountId)
        }
    }

    /**
     * This Method make the Request to REST API, if the User Status is Online at:
     * https://demo.openmf.org/fineract-provider/api/v1/{savingsAccountType}/{savingsAccountId}
     * /transactions/template.
     * using retrofit 2 with SavingsAccountService and get SavingsAccountTransactionTemplate in
     * response. and then DataManager send fetched SavingsAccountTransactionTemplate to
     * DatabaseHelperSavings to save in the SavingsAccountTransactionTemplate_Table for offline use
     *
     * @param savingsAccountType Savings Account Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     * @param transactionType    Transaction Type Example : 'Deposit', 'Withdrawal'
     * @return SavingsAccountTransactionTemplate
     */
    fun syncSavingsAccountTransactionTemplate(
        savingsAccountType: String?, savingsAccountId: Int, transactionType: String?
    ): Observable<SavingsAccountTransactionTemplate> {
        return mBaseApiManager.savingsApi.getSavingsAccountTransactionTemplate(
            savingsAccountType,
            savingsAccountId,
            transactionType
        )
            .concatMap { savingsAccountTransactionTemplate ->
                mDatabaseHelperSavings.saveSavingsAccountTransactionTemplate(
                    savingsAccountTransactionTemplate
                )
            }
    }

    /**
     * This Method makes the Transaction of SavingAccount. Here is two mode, one is Online.
     * if the user is Online, then request will be made to server and transaction will be sync to
     * server and if user is on offline mode then transaction will be saved in Database.
     * and User is able to sync that transaction when ever he have good internet connection
     *
     * @param savingsAccountType Type of Transaction
     * @param savingsAccountId   Savings Account Id
     * @param transactionType    Transaction Type Example : 'Deposit', 'Withdrawal'
     * @param request            SavingsAccountTransactionRequest
     * @return SavingsAccountTransactionResponse
     */
    fun processTransaction(
        savingsAccountType: String?, savingsAccountId: Int, transactionType: String?,
        request: SavingsAccountTransactionRequest
    ): Observable<SavingsAccountTransactionResponse> {
        return when (prefManager.userStatus) {
            false -> mBaseApiManager.savingsApi.processTransaction(
                savingsAccountType,
                savingsAccountId, transactionType, request
            )

            true ->
                /**
                 * Return SavingsAccountTransactionResponse from DatabaseHelperSavings.
                 */
                mDatabaseHelperSavings
                    .saveSavingsAccountTransaction(
                        savingsAccountType, savingsAccountId,
                        transactionType, request
                    )
        }
    }

    /**
     * This Method is making Transaction into Database SavingsAccountTransactionRequest_Table
     * and returns the SavingsAccountTransactionRequest with SavingAccount Id {savingAccountId}.
     * If Database does not contain any entry in SavingsAccountTransactionRequest_Table with
     * SavingAccount Id then it return null.
     *
     * @param savingAccountId SavingsAccount Id
     * @return SavingsAccountTransactionRequest
     */
    fun getSavingsAccountTransaction(
        savingAccountId: Int
    ): Observable<SavingsAccountTransactionRequest> {
        return mDatabaseHelperSavings.getSavingsAccountTransaction(savingAccountId)
    }

    /**
     * This Method sent the request to DatabaseHelperSavings and DatabaseHelperSavings load the
     * All SavingsAccountTransactions from the SavingsAccountTransactionRequest_Table.
     * and returns the List<SavingsAccountTransactionRequest>
     *
     * @return List<SavingsAccountTransactionRequest></SavingsAccountTransactionRequest>>
    </SavingsAccountTransactionRequest> */
    val allSavingsAccountTransactions: Observable<List<SavingsAccountTransactionRequest>>
        get() = mDatabaseHelperSavings.allSavingsAccountTransaction

    /**
     * This method sending request DatabaseHelper and Deleting the SavingsAccountTransaction
     * with savingsAccountId from SavingsAccountTransaction_Table and again loading list of
     * SavingsAccountTransaction from Database.
     *
     * @param savingsAccountId Loan Id of the Loan
     * @return List<SavingsAccountTransaction>
    </SavingsAccountTransaction> */
    fun deleteAndUpdateTransactions(
        savingsAccountId: Int
    ): Observable<List<SavingsAccountTransactionRequest>> {
        return mDatabaseHelperSavings.deleteAndUpdateTransaction(savingsAccountId)
    }

    /**
     * This Method updating SavingsAccountTransactionRequest in to Database and return the same
     * SavingsAccountTransactionRequest to the Presenter
     *
     * @param savingsAccountTransactionRequest Updating SavingsAccountTransactionRequest
     * in to Database.
     * @return LoanRepaymentRequest
     */
    fun updateLoanRepaymentTransaction(
        savingsAccountTransactionRequest: SavingsAccountTransactionRequest
    ): Observable<SavingsAccountTransactionRequest> {
        return mDatabaseHelperSavings.updateSavingsAccountTransaction(
            savingsAccountTransactionRequest
        )
    }

    val savingsAccounts: Observable<List<ProductSavings>>
        get() = mBaseApiManager.savingsApi.allSavingsAccounts

    fun createSavingsAccount(savingsPayload: SavingsPayload?): Observable<Savings> {
        return mBaseApiManager.savingsApi.createSavingsAccount(savingsPayload)
    }

    val savingsAccountTemplate: Observable<SavingProductsTemplate>
        get() = mBaseApiManager.savingsApi.savingsAccountTemplate

    fun getClientSavingsAccountTemplateByProduct(
        clientId: Int,
        productId: Int
    ): Observable<SavingProductsTemplate> {
        return mBaseApiManager.savingsApi.getClientSavingsAccountTemplateByProduct(
            clientId,
            productId
        )
    }

    fun getGroupSavingsAccountTemplateByProduct(
        groupId: Int,
        productId: Int
    ): Observable<SavingProductsTemplate> {
        return mBaseApiManager.savingsApi.getGroupSavingsAccountTemplateByProduct(
            groupId,
            productId
        )
    }

    fun approveSavingsApplication(
        savingsAccountId: Int,
        savingsApproval: SavingsApproval?
    ): Observable<GenericResponse> {
        return mBaseApiManager.savingsApi.approveSavingsApplication(
            savingsAccountId, savingsApproval
        )
    }
}