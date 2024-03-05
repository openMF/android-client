package com.mifos.mifosxdroid.activity.login

import com.mifos.core.network.datamanager.DataManagerAuth
import org.apache.fineract.client.models.PostAuthenticationResponse
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */

class LoginRepositoryImp @Inject constructor(private val dataManagerAuth: DataManagerAuth) :
    LoginRepository {

    override fun login(username: String, password: String): Observable<PostAuthenticationResponse> {
        return dataManagerAuth.login(username, password)
    }
}