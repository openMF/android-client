package com.mifos.api.datamanageronline;

import com.mifos.api.BaseApiManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Rajan Maurya on 24/06/16.
 */
@Singleton
public class DataManagerOnline {

    BaseApiManager mBaseApiManager;

    @Inject
    public DataManagerOnline(BaseApiManager baseApiManager) {
        mBaseApiManager = baseApiManager;
    }

}
