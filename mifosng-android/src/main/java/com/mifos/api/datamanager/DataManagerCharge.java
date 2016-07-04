package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperCharge;

import javax.inject.Singleton;

/**
 * Created by Rajan Maurya on 4/7/16.
 */
@Singleton
public class DataManagerCharge {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperCharge mDatabaseHelperCharge;

    public DataManagerCharge(BaseApiManager baseApiManager,
                             DatabaseHelperCharge databaseHelperCharge) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperCharge = databaseHelperCharge;
    }




}
