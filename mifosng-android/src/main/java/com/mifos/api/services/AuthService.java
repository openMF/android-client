/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.objects.User;
import com.mifos.api.model.APIEndPoint;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface AuthService {

    @POST(APIEndPoint.AUTHENTICATION)
    Observable<User> authenticate(@Query("username") String username, @Query("password") String password );

}
