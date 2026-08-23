/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.datastore

import com.mifos.core.common.utils.ServerConfig
import com.mifos.core.datastore.model.AppSettings
import com.mifos.core.datastore.model.DarkThemeConfig
import com.mifos.core.datastore.model.UserData
import com.mifos.core.model.objects.users.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserPreferencesRepository {
    val userInfo: Flow<UserData>
    val userData: Flow<User>
    val settingsInfo: Flow<AppSettings>
    val token: String?
    val instanceUrl: String
    val appTheme: StateFlow<DarkThemeConfig>
    val getServerConfig: StateFlow<ServerConfig>

    suspend fun updateUser(user: User)
    suspend fun updateUserStatus(status: Boolean)
    suspend fun updateSettings(appSettings: AppSettings)

    suspend fun logOut()

    suspend fun updateServerConfig(serverConfig: ServerConfig)

    suspend fun updateUserInfo(user: UserData)

    suspend fun updateTheme(theme: DarkThemeConfig)
}
