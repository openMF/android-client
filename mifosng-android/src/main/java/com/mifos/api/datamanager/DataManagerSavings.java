package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.GenericResponse;
import com.mifos.api.local.databasehelper.DatabaseHelperSavings;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

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
     * This Method
     * @param type
     * @param accountId
     * @param association
     * @return
     */
    public Observable<SavingsAccountWithAssociations> getSavingsAccount(
            String type, int accountId, String association) {
        return mBaseApiManager.getSavingsApi().getSavingsAccountWithAssociations(
                type, accountId, association);
    }


    public Observable<GenericResponse> activateSavings(int savingsAccountId,
                                                       HashMap<String, Object> request) {
        return mBaseApiManager.getSavingsApi().activateSavings(savingsAccountId, request);
    }
}
