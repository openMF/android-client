package com.mifos.api.services;

import com.mifos.objects.User;
import com.mifos.api.model.APIEndPoint;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * @author fomenkoo
 */
public interface AuthService {

    @POST(APIEndPoint.AUTHENTICATION)
    void authenticate(@Query("username") String username, @Query("password") String password, Callback<User> userCallback);

}
