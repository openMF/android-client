package com.mifos.mifosxdroid.activity.login

import org.openapitools.client.models.PostAuthenticationResponse

/**
 * Created by Aditya Gupta on 06/08/23.
 */

interface LoginRepository {

    suspend fun login(username: String, password: String): PostAuthenticationResponse

}