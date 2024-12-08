package com.mifos.core.network.datamanger

import org.mifos.core.apimanager.BaseApiManager
import org.openapitools.client.models.PostAuthenticationRequest
import org.openapitools.client.models.PostAuthenticationResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 19/02/17.
 */
@Singleton
class DataManagerAuth @Inject constructor(private val baseApiManager: BaseApiManager) {
    /**
     * @param username Username
     * @param password Password
     * @return Basic OAuth
     */
    suspend fun login(username: String, password: String): PostAuthenticationResponse {
        val body = PostAuthenticationRequest(username = username, password = password)
        return baseApiManager.getAuthApi().authenticate(body, true)
    }
}