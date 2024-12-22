package com.mifos.core.data.repository

import org.openapitools.client.models.PostAuthenticationResponse

/**
 * Created by Aditya Gupta on 06/08/23.
 */

interface LoginRepository {

    suspend fun login(username: String, password: String): PostAuthenticationResponse

}