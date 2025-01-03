/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

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
