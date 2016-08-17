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
     *
     * @param type
     * @param accountId
     * @param transactionType
     * @return
     */
    public Observable<SavingsAccountTransactionTemplate> getSavingsAccountTransactionTemplate(
            String type, int accountId, String transactionType) {
        return mBaseApiManager.getSavingsApi().getSavingsAccountTransactionTemplate(type,
                accountId, transactionType);
    }


    public Observable<SavingsAccountTransactionResponse> processTransaction(
            String type, int accountId, String transactionType,
            SavingsAccountTransactionRequest request) {
        return mBaseApiManager.getSavingsApi().processTransaction(type, accountId, transactionType,
                request);
    }
}
