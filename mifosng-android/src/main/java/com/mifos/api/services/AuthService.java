/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.User;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * @author fomenkoo
 */
public interface AuthService {

    @POST(APIEndPoint.AUTHENTICATION)
    void authenticate(@Query("username") String username, @Query("password") String password,
                      Callback<User> userCallback);

}
