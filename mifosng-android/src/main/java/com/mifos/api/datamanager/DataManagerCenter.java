package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperCenter;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Rajan Maurya on 28/6/16.
 */
@Singleton
public class DataManagerCenter {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperCenter mDatabaseHelperCenter;

    @Inject
    public DataManagerCenter(BaseApiManager baseApiManager,
                             DatabaseHelperCenter databaseHelperCenter) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperCenter = databaseHelperCenter;
    }



}
