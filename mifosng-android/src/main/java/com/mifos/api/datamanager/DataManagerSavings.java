package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.GenericResponse;
import com.mifos.api.local.databasehelper.DatabaseHelperSavings;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;
import com.mifos.utils.PrefManager;

import java.util.HashMap;

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
                        type, savingsAccountId, association)
                        .concatMap(new Func1<SavingsAccountWithAssociations,
                                Observable<? extends SavingsAccountWithAssociations>>() {
                            @Override
                            public Observable<? extends SavingsAccountWithAssociations> call(
                                    SavingsAccountWithAssociations savingsAccountWithAssociations) {
                                return mDatabaseHelperSavings.saveSavingsAccount
                                        (savingsAccountWithAssociations);
                            }
                        });

            case 1:
                /**
                 * Return SavingsAccountWithAssociations from DatabaseHelperSavings.
                 */
                return mDatabaseHelperSavings.readSavingsAccount(savingsAccountId);

            default:
                return Observable.just(new SavingsAccountWithAssociations());
        }
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
                        savingsAccountId, transactionType)
                        .concatMap(new Func1<SavingsAccountTransactionTemplate,
                                Observable<? extends SavingsAccountTransactionTemplate>>() {
                            @Override
                            public Observable<? extends SavingsAccountTransactionTemplate> call
                                    (SavingsAccountTransactionTemplate
                                             savingsAccountTransactionTemplate) {
                                return mDatabaseHelperSavings.saveSavingsAccountTransactionTemplate(
                                        savingsAccountTransactionTemplate);
                            }
                        });
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
     * This Method makes the Transaction of SavingAccount. Here is two mode, one is Online.
     * if the user is Online, then request will be made to server and transaction will be sync to
     * server and if user is on offline mode then transaction will be saved in Database.
     * and User is able to sync that transaction when ever he have good internet connection
     *
     * @param type             Type of Transaction
     * @param savingsAccountId Savings Account Id
     * @param transactionType  Transaction Type Example : 'Deposit', 'Withdrawal'
     * @param request          SavingsAccountTransactionRequest
     * @return SavingsAccountTransactionResponse
     */
    public Observable<SavingsAccountTransactionResponse> processTransaction(
            String type, int savingsAccountId, String transactionType,
            SavingsAccountTransactionRequest request) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getSavingsApi().processTransaction(type,
                        savingsAccountId, transactionType, request);

            case 1:
                /**
                 * Return SavingsAccountTransactionResponse from DatabaseHelperSavings.
                 */
                return mDatabaseHelperSavings
                        .saveSavingsAccountTransaction(request, savingsAccountId);

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
    public Observable<SavingsAccountTransactionRequest> getDatabaseSavingsAccountTransaction(
            int savingAccountId) {
        return mDatabaseHelperSavings.getDatabaseSavingsAccountTransaction(savingAccountId);
    }

}
