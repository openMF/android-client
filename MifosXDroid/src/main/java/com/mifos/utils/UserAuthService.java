package com.mifos.utils;

import com.mifos.objects.User;

import retrofit.Callback;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by ishankhanna on 08/02/14.
 */
public interface UserAuthService {

    @Headers("X-Mifos-Platform-TenantId: default")
    @POST("/authentication")
    public void authenticate(@Query("username") String username, @Query("password") String password, Callback<User> userCallback);
}
