/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services

import com.mifos.api.model.APIEndPoint
import com.mifos.api.model.LoginData
import com.mifos.objects.user.User
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

/**
 * @author fomenkoo
 */
interface AuthService {
    @POST(APIEndPoint.AUTHENTICATION)
    fun authenticate(@Body login: LoginData?): Observable<User>
}