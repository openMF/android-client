/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.model.ServerConfig
import com.mifos.core.model.UserData
import kotlinx.coroutines.flow.Flow
import org.openapitools.client.models.PostAuthenticationResponse

interface PreferenceRepository {
    val isAuthenticated: Flow<Boolean>
    val userDetails: Flow<PostAuthenticationResponse?>
    val serverConfig: Flow<ServerConfig>
    val userData: Flow<UserData>

    fun updateServerConfig(config: ServerConfig)
    fun saveUserDetails(userDetails: PostAuthenticationResponse)
    fun logOut(): Unit
}
