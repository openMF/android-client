/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.datastore

import com.mifos.core.common.utils.ServerConfig
import com.mifos.core.datastore.model.AppSettings
import com.mifos.core.datastore.model.AppTheme
import com.mifos.core.datastore.model.UserData
import com.mifos.core.model.objects.users.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserPreferencesRepository {
    val userInfo: Flow<UserData>
    val userData: Flow<User>
    val settingsInfo: Flow<AppSettings>
    val serverConfig: Flow<ServerConfig>

    suspend fun updateUser(user: User): Result<Unit>
    suspend fun updateUserStatus(status: Boolean): Result<Unit>
    suspend fun updateSettings(appSettings: AppSettings): Result<Unit>

    suspend fun logOut(): Unit

    suspend fun updateServerConfig(serverConfig: ServerConfig): Result<Unit>

    suspend fun updateUserInfo(user: UserData): Result<Unit>

    suspend fun updateTheme(theme: AppTheme): Result<Unit>

    val getServerConfig: StateFlow<ServerConfig>
}
