/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api;

import com.mifos.objects.User;
import rx.Observable;

/**
 * Created by Rajan Maurya on 16/3/16.
 */
public class DataManager {


    BaseApiManager BaseAp = new BaseApiManager();

    public DataManager(){

    }


    public Observable<User> login(String username, String password) {
        return BaseAp.getAuthApi().authenticate(username,password);
    }
}
