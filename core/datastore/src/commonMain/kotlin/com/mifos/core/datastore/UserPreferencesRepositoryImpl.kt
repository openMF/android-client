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

import com.mifos.core.common.utils.DataState
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

    override val appTheme: StateFlow<AppTheme>
        get() = preferenceManager.appTheme.stateIn(
            scope = unconfinedScope,
            initialValue = AppTheme.SYSTEM,
            started = SharingStarted.Eagerly,
        )

    override suspend fun updateUserInfo(user: UserData): DataState<Unit> {
        return withContext(ioDispatcher) {
            try {
                preferenceManager.updateUserInfo(user)
                DataState.Success(Unit)
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }

    override val token: String?
        get() = preferenceManager.token

    override val instanceUrl: String
        get() = preferenceManager.instanceUrl

    override suspend fun updateTheme(theme: AppTheme): DataState<Unit> {
        return try {
            val result = preferenceManager.updateTheme(theme)
            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateUserStatus(status: Boolean): DataState<Unit> {
        return withContext(ioDispatcher) {
            try {
                preferenceManager.updateUserStatus(status)
                DataState.Success(Unit)
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }

    override suspend fun updateSettings(appSettings: AppSettings): DataState<Unit> {
        return try {
            val result = preferenceManager.updateSettingsInfo(appSettings)
            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override val getServerConfig: StateFlow<ServerConfig>
        get() = preferenceManager.serverConfig

    override suspend fun updateUser(user: User): DataState<Unit> {
        return withContext(ioDispatcher) {
            try {
                preferenceManager.updateUser(user)
                DataState.Success(Unit)
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }

    override suspend fun updateServerConfig(serverConfig: ServerConfig): DataState<Unit> {
        return withContext(ioDispatcher) {
            try {
                preferenceManager.updateServerConfig(serverConfig)
                DataState.Success(Unit)
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }

    override suspend fun logOut() {
        withContext(ioDispatcher) {
            preferenceManager.clearInfo()
        }
    }
}
