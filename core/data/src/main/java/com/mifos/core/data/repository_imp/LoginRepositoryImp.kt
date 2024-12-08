package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.LoginRepository
import com.mifos.core.network.datamanger.DataManagerAuth
import org.openapitools.client.models.PostAuthenticationResponse
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */

class LoginRepositoryImp @Inject constructor(private val dataManagerAuth: DataManagerAuth) :
    LoginRepository {

    override suspend fun login(username: String, password: String): PostAuthenticationResponse {
        return dataManagerAuth.login(username, password)
    }
}