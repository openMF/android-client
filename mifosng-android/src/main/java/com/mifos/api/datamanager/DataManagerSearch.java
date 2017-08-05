package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperCenter;
import com.mifos.api.local.databasehelper.DatabaseHelperClient;
import com.mifos.api.local.databasehelper.DatabaseHelperGroups;
import com.mifos.api.local.databasehelper.DatabaseHelperLoan;
import com.mifos.api.local.databasehelper.DatabaseHelperSavings;
import com.mifos.objects.SearchedEntity;
import com.mifos.utils.Constants;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Rajan Maurya on 12/12/16.
 */
@Singleton
public class DataManagerSearch {

    public final BaseApiManager baseApiManager;
    public final DatabaseHelperClient mDatabaseHelperClient;
    public final DatabaseHelperGroups mDatabaseHelperGroup;
    public final DatabaseHelperCenter mDatabaseHelperCenter;
    public final DatabaseHelperLoan mDatabaseHelperLoan;
    public final DatabaseHelperSavings mDatabaseHelperSaving;

    @Inject
    public DataManagerSearch(BaseApiManager baseApiManager,
                             DatabaseHelperClient databaseHelperClient,
                             DatabaseHelperGroups databaseHelperGroup,
                             DatabaseHelperCenter databaseHelperCenter,
                             DatabaseHelperLoan databaseHelperLoan,
                             DatabaseHelperSavings databaseHelperSaving) {
        this.baseApiManager = baseApiManager;
        mDatabaseHelperClient = databaseHelperClient;
        mDatabaseHelperGroup = databaseHelperGroup;
        mDatabaseHelperCenter = databaseHelperCenter;
        mDatabaseHelperLoan = databaseHelperLoan;
        mDatabaseHelperSaving = databaseHelperSaving;
    }

    public Observable<List<SearchedEntity>> searchResources(final String query, String resources,
                                                            final Boolean exactMatch) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return baseApiManager.getSearchApi().searchResources(query, resources, exactMatch);
            case 1:
                if (resources.equals(Constants.ENTITY_TYPE_CLIENTS)) {
                    return mDatabaseHelperClient.readAllClientsByQuery(query, exactMatch);
                } else if (resources.equals(Constants.ENTITY_TYPE_GROUPS)) {
                    return Observable.merge(mDatabaseHelperGroup.readAllGroupsByQuery(query, exactMatch),
                            mDatabaseHelperCenter.readAllCentersByQuery(query, exactMatch));
                } else if (resources.equals(Constants.ENTITY_TYPE_LOANS)) {
                    return mDatabaseHelperLoan.readAllLoansByQuery(query, exactMatch);
                } else if (resources.equals(Constants.ENTITY_TYPE_SAVINGS)) {
                    return mDatabaseHelperSaving.readAllSavingsByQuery(query, exactMatch);
                } else {
                    return Observable.merge(mDatabaseHelperClient.readAllClientsByQuery(query, exactMatch),
                            (mDatabaseHelperGroup.readAllGroupsByQuery(query, exactMatch)),
                            (mDatabaseHelperLoan.readAllLoansByQuery(query,
                                    exactMatch)),
                            (mDatabaseHelperSaving.readAllSavingsByQuery(query,
                                    exactMatch)));
                }

            default:
                return Observable.just((List<SearchedEntity>) new ArrayList<SearchedEntity>());
        }
    }
}
