package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.GenericResponse;
import com.mifos.api.local.databasehelper.DatabaseHelperSavings;
import com.mifos.objects.accounts.loan.SavingsApproval;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.client.Savings;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;
import com.mifos.services.data.SavingsPayload;
import com.mifos.utils.PrefManager;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Rajan Maurya on 17/08/16.
 */
@Singleton
public class DataManagerSavings {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperSavings mDatabaseHelperSavings;

    @Inject
    public DataManagerSavings(BaseApiManager baseApiManager,
                              DatabaseHelperSavings databaseHelperSavings) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperSavings = databaseHelperSavings;
    }


    /**
     * This Method Make the Request to the REST API
     * https://demo.openmf.org/fineract-provider/api/v1/savingsaccounts/{savingsAccountId}
     * ?associations={all or transactions or charges}
     * and fetch savings application/account.
     *
     * @param type             Type of the SavingsAccount
     * @param savingsAccountId Savings Account Id
     * @param association      {all or transactions or charges}
     *                         'all': Gets data related to all associations e.g. ?associations=all.
     *                         'transactions': Gets data related to transactions on the account e.g.
     *                         ?associations=transactions
     *                         'charges':Savings Account charges data.
     * @return SavingsAccountWithAssociations
     */
    public Observable<SavingsAccountWithAssociations> getSavingsAccount(
            String type, int savingsAccountId, String association) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getSavingsApi().getSavingsAccountWithAssociations(
                        type, savingsAccountId, association);
            case 1:
                /**
                 * Return SavingsAccountWithAssociations from DatabaseHelperSavings.
                 */
                return mDatabaseHelperSavings.readSavingsAccount(savingsAccountId);

            default:
                return Observable.just(new SavingsAccountWithAssociations());
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
     *                         'all': Gets data related to all associations e.g. ?associations=all.
     *                         'transactions': Gets data related to transactions on the account e.g.
     *                         ?associations=transactions
     *                         'charges':Savings Account charges data.
     * @return SavingsAccountWithAssociations
     */
    public Observable<SavingsAccountWithAssociations> syncSavingsAccount(
            String type, int savingsAccountId, String association) {
        return mBaseApiManager.getSavingsApi().getSavingsAccountWithAssociations(type,
                savingsAccountId, association)
                .concatMap(new Func1<SavingsAccountWithAssociations,
                        Observable<? extends SavingsAccountWithAssociations>>() {
                    @Override
                    public Observable<? extends SavingsAccountWithAssociations> call
                            (SavingsAccountWithAssociations savingsAccountWithAssociations) {
                        return mDatabaseHelperSavings.saveSavingsAccount(
                                savingsAccountWithAssociations);
                    }
                });
    }


    public Observable<GenericResponse> activateSavings(int savingsAccountId,
                                                       HashMap<String, Object> request) {
        return mBaseApiManager.getSavingsApi().activateSavings(savingsAccountId, request);
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
    public Observable<SavingsAccountTransactionTemplate> getSavingsAccountTransactionTemplate(
            String type, final int savingsAccountId, String transactionType) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getSavingsApi().getSavingsAccountTransactionTemplate(type,
                        savingsAccountId, transactionType);
            case 1:
                /**
                 * Return SavingsAccountTransactionTemplate from DatabaseHelperSavings.
                 */
                return mDatabaseHelperSavings.readSavingsAccountTransactionTemplate
                        (savingsAccountId);

            default:
                return Observable.just(new SavingsAccountTransactionTemplate());
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
    public Observable<SavingsAccountTransactionTemplate> syncSavingsAccountTransactionTemplate(
            String savingsAccountType, int savingsAccountId, String transactionType) {
        return mBaseApiManager.getSavingsApi().getSavingsAccountTransactionTemplate
                (savingsAccountType, savingsAccountId, transactionType)
                .concatMap(new Func1<SavingsAccountTransactionTemplate,
                        Observable<? extends SavingsAccountTransactionTemplate>>() {
                    @Override
                    public Observable<? extends SavingsAccountTransactionTemplate> call
                            (SavingsAccountTransactionTemplate savingsAccountTransactionTemplate) {
                        return mDatabaseHelperSavings.saveSavingsAccountTransactionTemplate(
                                savingsAccountTransactionTemplate);
                    }
                });
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
    public Observable<SavingsAccountTransactionResponse> processTransaction(
            String savingsAccountType, int savingsAccountId, String transactionType,
            SavingsAccountTransactionRequest request) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getSavingsApi().processTransaction(savingsAccountType,
                        savingsAccountId, transactionType, request);

            case 1:
                /**
                 * Return SavingsAccountTransactionResponse from DatabaseHelperSavings.
                 */
                return mDatabaseHelperSavings
                        .saveSavingsAccountTransaction(savingsAccountType, savingsAccountId,
                                transactionType, request);

            default:
                return Observable.just(new SavingsAccountTransactionResponse());
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
    public Observable<SavingsAccountTransactionRequest> getSavingsAccountTransaction(
            int savingAccountId) {
        return mDatabaseHelperSavings.getSavingsAccountTransaction(savingAccountId);
    }


    /**
     * This Method sent the request to DatabaseHelperSavings and DatabaseHelperSavings load the
     * All SavingsAccountTransactions from the SavingsAccountTransactionRequest_Table.
     * and returns the List<SavingsAccountTransactionRequest>
     *
     * @return List<SavingsAccountTransactionRequest></>
     */
    public Observable<List<SavingsAccountTransactionRequest>> getAllSavingsAccountTransactions() {
        return mDatabaseHelperSavings.getAllSavingsAccountTransaction();
    }


    /**
     * This method sending request DatabaseHelper and Deleting the SavingsAccountTransaction
     * with savingsAccountId from SavingsAccountTransaction_Table and again loading list of
     * SavingsAccountTransaction from Database.
     *
     * @param savingsAccountId Loan Id of the Loan
     * @return List<SavingsAccountTransaction>
     */
    public Observable<List<SavingsAccountTransactionRequest>> deleteAndUpdateTransactions(
            int savingsAccountId) {
        return mDatabaseHelperSavings.deleteAndUpdateTransaction(savingsAccountId);
    }


    /**
     * This Method updating SavingsAccountTransactionRequest in to Database and return the same
     * SavingsAccountTransactionRequest to the Presenter
     *
     * @param savingsAccountTransactionRequest Updating SavingsAccountTransactionRequest
     *                                         in to Database.
     * @return LoanRepaymentRequest
     */
    public Observable<SavingsAccountTransactionRequest> updateLoanRepaymentTransaction(
            SavingsAccountTransactionRequest savingsAccountTransactionRequest) {
        return mDatabaseHelperSavings.updateSavingsAccountTransaction(
                savingsAccountTransactionRequest);
    }

    public Observable<List<ProductSavings>> getSavingsAccounts() {
        return mBaseApiManager.getSavingsApi().getAllSavingsAccounts();
    }

    public Observable<Savings> createSavingsAccount(SavingsPayload savingsPayload) {
        return mBaseApiManager.getSavingsApi().createSavingsAccount(savingsPayload);
    }

    public Observable<SavingProductsTemplate> getSavingsAccountTemplate() {
        return mBaseApiManager.getSavingsApi().getSavingsAccountTemplate();
    }

    public Observable<SavingProductsTemplate>
            getClientSavingsAccountTemplateByProduct(int clientId, int productId) {
        return mBaseApiManager.getSavingsApi().
                getClientSavingsAccountTemplateByProduct(clientId, productId);
    }

    public Observable<SavingProductsTemplate>
            getGroupSavingsAccountTemplateByProduct(int groupId, int productId) {
        return mBaseApiManager.getSavingsApi().
                getGroupSavingsAccountTemplateByProduct(groupId, productId);
    }

    public Observable<GenericResponse> approveSavingsApplication(int savingsAccountId,
                                                                 SavingsApproval savingsApproval) {
        return mBaseApiManager.getSavingsApi().approveSavingsApplication(
                savingsAccountId, savingsApproval);
    }
}
