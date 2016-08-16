package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperSavings;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;

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
}
