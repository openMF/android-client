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

    override val serverConfig: Flow<ServerConfig>
        get() = preferenceManager.serverConfig

    override suspend fun updateUserInfo(user: UserData): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                preferenceManager.updateUserInfo(user)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateTheme(theme: AppTheme): Result<Unit> {
        return try {
            val result = preferenceManager.updateTheme(theme)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserStatus(status: Boolean): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                preferenceManager.updateUserStatus(status)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateSettings(appSettings: AppSettings): Result<Unit> {
        return try {
            val result = preferenceManager.updateSettingsInfo(appSettings)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override val getServerConfig: StateFlow<ServerConfig>
        get() = preferenceManager.serverConfig.stateIn(
            scope = unconfinedScope,
            initialValue = ServerConfig.DEFAULT,
            started = SharingStarted.Eagerly,
        )

    override suspend fun updateUser(user: User): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                preferenceManager.updateUser(user)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateServerConfig(serverConfig: ServerConfig): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                preferenceManager.updateServerConfig(serverConfig)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun logOut() {
        withContext(ioDispatcher) {
            preferenceManager.clearInfo()
        }
    }
}
