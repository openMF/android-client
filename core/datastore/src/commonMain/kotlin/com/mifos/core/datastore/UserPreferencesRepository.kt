/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.datastore

import com.mifos.core.model.objects.ServerConfig
import com.mifos.core.model.objects.UserData
import com.mifos.core.model.objects.users.User
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userInfo: Flow<UserData>
    val userData: Flow<User>
    val serverConfig: Flow<ServerConfig>

    suspend fun updateUser(user: UserData): Result<Unit>

    suspend fun logOut(): Unit

    suspend fun updateServerConfig(serverConfig: ServerConfig): Result<Unit>

    suspend fun updateUserInfo(user: UserData): Result<Unit>
}
