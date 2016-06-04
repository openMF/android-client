package com.mifos.api;

import com.mifos.objects.User;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Rajan Maurya on 4/6/16.
 */

@Singleton
public class DataManager {

    public final BaseApiManager mBaseApiManager;

    @Inject
    public DataManager(BaseApiManager baseApiManager) {

        mBaseApiManager = baseApiManager;
    }

    /**
     * @param username Username
     * @param password  Password
     * @return  Basic OAuth
     */
    public Observable<User> login(String username, String password) {
        return mBaseApiManager.getAuthApi().authenticate(username,password);
    }
}
