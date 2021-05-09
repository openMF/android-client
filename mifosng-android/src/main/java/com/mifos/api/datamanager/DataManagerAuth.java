package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.objects.user.User;
import com.mifos.services.data.UserPayload;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Rajan Maurya on 19/02/17.
 */
@Singleton
public class DataManagerAuth {

    private final BaseApiManager baseApiManager;

    @Inject
    public DataManagerAuth(BaseApiManager baseApiManager) {
        this.baseApiManager = baseApiManager;
    }

    /**
     * @param payload UserPayload
     * @return Basic OAuth
     */
    public Observable<User> login(UserPayload payload) {
        return baseApiManager.getAuthApi().authenticate(payload);
    }
}
