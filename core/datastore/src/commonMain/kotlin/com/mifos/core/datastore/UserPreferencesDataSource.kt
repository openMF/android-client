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
import com.mifos.core.common.utils.getInstanceUrl
import com.mifos.core.datastore.model.AppSettings
import com.mifos.core.datastore.model.AppTheme
import com.mifos.core.datastore.model.UserData
import com.mifos.core.model.objects.users.User
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi

private const val USER_DATA = "userData"
private const val AUTH_USER = "user_details"
private const val SERVER_CONFIG_KEY = "server_config"
private const val USER_STATUS = "user_status"
private const val APP_SETTINGS = "appSettings"

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
class UserPreferencesDataSource(
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher,
) {

    private val _settingsInfo = MutableStateFlow(
        settings.decodeValue(
            key = APP_SETTINGS,
            serializer = AppSettings.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = APP_SETTINGS,
                serializer = AppSettings.serializer(),
            ) ?: AppSettings.DEFAULT,
        ),
    )

    private val _userInfo = MutableStateFlow(
        settings.decodeValue(
            key = USER_DATA,
            serializer = UserData.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = USER_DATA,
                serializer = UserData.serializer(),
            ) ?: UserData.DEFAULT,
        ),
    )
    val userInfo = _userInfo.asStateFlow()
    val settingsInfo = _settingsInfo.asStateFlow()
    val appTheme = _settingsInfo.map { it.appTheme }

    private val _userData = MutableStateFlow(
        settings.decodeValue(
            key = AUTH_USER,
            serializer = User.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = AUTH_USER,
                serializer = User.serializer(),
            ) ?: User(),
        ),
    )
    val userData = _userData.asStateFlow()

    private val _serverConfig = MutableStateFlow(
        settings.decodeValue(
            key = SERVER_CONFIG_KEY,
            serializer = ServerConfig.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = SERVER_CONFIG_KEY,
                serializer = ServerConfig.serializer(),
            ) ?: ServerConfig.DEFAULT,
        ),
    )
    val serverConfig = _serverConfig.asStateFlow()

    val userStatus: Boolean
        get() = settings.getBoolean(USER_STATUS, false)

    val isAuthenticated: Boolean
        get() = _userData.value.isAuthenticated

    val token: String
        get() = _userData.value.base64EncodedAuthenticationKey?.let { "Basic $it" } ?: ""

    val instanceUrl: String get() = _serverConfig.value.getInstanceUrl()

    fun updateUserStatus(status: Boolean) {
        settings.putBoolean(USER_STATUS, status)
    }

    suspend fun updateUserInfo(user: UserData) {
        withContext(dispatcher) {
            settings.putUserPreference(user)
            _userInfo.value = user
        }
    }

    suspend fun updateSettingsInfo(appSettings: AppSettings) {
        withContext(dispatcher) {
            settings.putSettingsPreference(appSettings)
            _settingsInfo.value = appSettings
        }
    }

    suspend fun updateUser(user: User) {
        withContext(dispatcher) {
            settings.putAuth(user)
            _userData.value = user
        }
    }

    suspend fun updateServerConfig(serverConfig: ServerConfig) {
        withContext(dispatcher) {
            settings.putServerConfig(serverConfig)
            _serverConfig.value = serverConfig
        }
    }

    suspend fun clearInfo() {
        withContext(dispatcher) {
            settings.putAuth(User())
        }
    }

    suspend fun updateTheme(theme: AppTheme) {
        withContext(dispatcher) {
            settings.putSettingsPreference(
                AppSettings.DEFAULT.copy(
                    appTheme = theme,
                ),
            )
            _settingsInfo.value = AppSettings.DEFAULT.copy(
                appTheme = theme,
            )
        }
    }
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
private fun Settings.putUserPreference(user: UserData) {
    encodeValue(
        key = USER_DATA,
        serializer = UserData.serializer(),
        value = user,
    )
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
private fun Settings.putAuth(user: User) {
    encodeValue(
        key = AUTH_USER,
        serializer = User.serializer(),
        value = user,
    )
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
private fun Settings.putServerConfig(serverConfig: ServerConfig) {
    encodeValue(
        key = SERVER_CONFIG_KEY,
        serializer = ServerConfig.serializer(),
        value = serverConfig,
    )
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
private fun Settings.putSettingsPreference(settings: AppSettings) {
    encodeValue(
        key = APP_SETTINGS,
        serializer = AppSettings.serializer(),
        value = settings,
    )
}
