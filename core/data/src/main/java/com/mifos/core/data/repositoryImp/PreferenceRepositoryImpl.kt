/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.PreferenceRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.model.ServerConfig
import com.mifos.core.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.openapitools.client.models.PostAuthenticationResponse
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
    private val prefManager: PrefManager,
) : PreferenceRepository {
    override val isAuthenticated: Flow<Boolean>
        get() = prefManager.userDetails.map { it != null && it.authenticated == true }

    override val userDetails: Flow<PostAuthenticationResponse?>
        get() = prefManager.userDetails

    override val serverConfig: Flow<ServerConfig>
        get() = prefManager.serverConfigFlow

    override val userData: Flow<UserData>
        get() = prefManager.userData

    override fun updateServerConfig(config: ServerConfig) {
        prefManager.updateServerConfig(config)
    }

    override fun saveUserDetails(userDetails: PostAuthenticationResponse) {
        prefManager.saveUserDetails(userDetails)
    }

    override fun logOut() {
        prefManager.clearUserDetails()
    }
}
