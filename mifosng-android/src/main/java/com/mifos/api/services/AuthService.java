/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.user.User;
import com.mifos.services.data.UserPayload;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface AuthService {

    @POST(APIEndPoint.AUTHENTICATION)
    Observable<User> authenticate(@Body UserPayload payload);
}
