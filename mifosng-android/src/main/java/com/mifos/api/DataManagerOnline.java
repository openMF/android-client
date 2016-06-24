package com.mifos.api;

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
