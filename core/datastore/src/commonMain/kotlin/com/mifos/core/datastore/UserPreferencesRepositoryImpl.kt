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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class UserPreferencesRepositoryImpl(
    private val preferenceManager: UserPreferencesDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    unconfinedDispatcher: CoroutineDispatcher,
) : UserPreferencesRepository {

    private val unconfinedScope = CoroutineScope(unconfinedDispatcher)

    override val settingsInfo: Flow<AppSettings>
        get() = preferenceManager.settingsInfo

    override val userInfo: Flow<UserData>
        get() = preferenceManager.userInfo

    override val userData: Flow<User>
        get() = preferenceManager.userData

    override val appTheme: StateFlow<DarkThemeConfig>
        get() = preferenceManager.appTheme.stateIn(
            scope = unconfinedScope,
            initialValue = DarkThemeConfig.FOLLOW_SYSTEM,
            started = SharingStarted.Eagerly,
        )

    override suspend fun updateUserInfo(user: UserData) {
        withContext(ioDispatcher) {
            preferenceManager.updateUserInfo(user)
        }
    }

    override val token: String?
        get() = preferenceManager.token

    override val instanceUrl: String
        get() = preferenceManager.instanceUrl

    override suspend fun updateTheme(theme: DarkThemeConfig) {
        preferenceManager.updateTheme(theme)
    }

    override suspend fun updateUserStatus(status: Boolean) {
        withContext(ioDispatcher) {
            preferenceManager.updateUserStatus(status)
        }
    }

    override suspend fun updateSettings(appSettings: AppSettings) {
        preferenceManager.updateSettingsInfo(appSettings)
    }

    override val getServerConfig: StateFlow<ServerConfig>
        get() = preferenceManager.serverConfig

    override suspend fun updateUser(user: User) {
        withContext(ioDispatcher) {
            preferenceManager.updateUser(user)
        }
    }

    override suspend fun updateServerConfig(serverConfig: ServerConfig) {
        withContext(ioDispatcher) {
            preferenceManager.updateServerConfig(serverConfig)
        }
    }

    override suspend fun logOut() {
        withContext(ioDispatcher) {
            preferenceManager.clearInfo()
        }
    }
}
