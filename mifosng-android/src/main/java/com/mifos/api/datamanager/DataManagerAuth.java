package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.mappers.UserMapper;
import com.mifos.objects.user.User;
import com.mifos.utils.PrefManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Rajan Maurya on 19/02/17.
 */
@Singleton
public class DataManagerAuth {

    private final BaseApiManager baseApiManager;
    private final org.mifos.core.apimanager.BaseApiManager sdkBaseApiManager;

    @Inject
    public DataManagerAuth(BaseApiManager baseApiManager,
                           org.mifos.core.apimanager.BaseApiManager sdkBaseApiManager) {
        this.baseApiManager = baseApiManager;
        this.sdkBaseApiManager = sdkBaseApiManager;
    }

    /**
     * @param username Username
     * @param password Password
     * @return Basic OAuth
     */
    public Observable<User> login(String username, String password) {
        sdkBaseApiManager.createService(username, password,
                PrefManager.INSTANCE.getInstanceUrl(),
                PrefManager.INSTANCE.getTenant());
        String body = String.format("{\"username\": \"%s\", \"password\": \"%s\"}",
                username, password);
        return sdkBaseApiManager.getAuthApi().authenticate(true, body)
                .map(UserMapper.INSTANCE::mapFromEntity);
    }
}
