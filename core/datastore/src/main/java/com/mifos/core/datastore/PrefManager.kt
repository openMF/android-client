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

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.mifos.core.model.ServerConfig
import com.mifos.core.model.UserData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import org.openapitools.client.models.PostAuthenticationResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefManager @Inject constructor(
    @ApplicationContext context: Context,
) {
    companion object {
        private const val PREF_NAME = "mifos_pref"
        private const val AUTH_USER = "user_details"
        private const val SERVER_CONFIG_KEY = "server_config"
        private const val USER_DATA = "user_data"
        private const val USER_STATUS = "user_status"
        private const val AUTH_USERNAME = "auth_username"
        private const val AUTH_PASSWORD = "auth_password"
    }

    private val preference: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE,
    )

    private val gson: Gson = Gson()

    val serverConfigFlow = preference.getStringFlowForKey(SERVER_CONFIG_KEY)
        .map { gson.fromJson(it, ServerConfig::class.java) ?: ServerConfig.DEFAULT }

    val serverConfig: ServerConfig
        get() = preference.getString(SERVER_CONFIG_KEY, null)
            ?.let { gson.fromJson(it, ServerConfig::class.java) }
            ?: ServerConfig.DEFAULT

    // Usage example in PrefManager
    val userDetails: Flow<PostAuthenticationResponse?> = preference.getFlow(
        key = AUTH_USER,
        deserializer = { gson.fromJson(it, PostAuthenticationResponse::class.java) },
        defaultValue = null,
    )

    val userData: Flow<UserData> = preference.getFlow(
        key = USER_DATA,
        deserializer = { gson.fromJson(it, UserData::class.java) },
        defaultValue = UserData.DEFAULT,
    )

    val user: PostAuthenticationResponse?
        get() = preference.getString(AUTH_USER, null)
            ?.let { gson.fromJson(it, PostAuthenticationResponse::class.java) }

    val isAuthenticated: Boolean
        get() = user?.authenticated == true

    val token: String
        get() = user?.base64EncodedAuthenticationKey?.let { "Basic $it" } ?: ""

    val userStatus: Boolean
        get() = preference.getBoolean(USER_STATUS, false)

    fun updateUserStatus(status: Boolean) {
        preference.edit { putBoolean(USER_STATUS, status) }
    }

    fun updateServerConfig(config: ServerConfig?) {
        config?.let {
            val jsonConfig = gson.toJson(it, ServerConfig::class.java)

            jsonConfig?.let {
                preference.edit {
                    putString(SERVER_CONFIG_KEY, jsonConfig)
                }
            }
        }
    }

    fun saveUserDetails(userDetails: PostAuthenticationResponse) {
        val jsonUserDetails = gson.toJson(userDetails, PostAuthenticationResponse::class.java)

        jsonUserDetails?.let {
            preference.edit { putString(AUTH_USER, jsonUserDetails) }
        }
    }

    fun saveUserData(userData: UserData) {
        val jsonUserData = gson.toJson(userData, UserData::class.java)

        jsonUserData?.let {
            preference.edit { putString(USER_DATA, jsonUserData) }
        }
    }

    @WorkerThread
    fun clearUserDetails() {
        preference.edit { remove(AUTH_USER) }
    }

    var usernamePassword: Pair<String, String>
        get() = Pair(
            preference.getString(AUTH_USERNAME, "") ?: "",
            preference.getString(AUTH_PASSWORD, "") ?: "",
        )
        set(value) {
            preference.edit {
                putString(AUTH_USERNAME, value.first)
                putString(AUTH_PASSWORD, value.second)
            }
        }

    // Extension function for simplified SharedPreferences editing
    private inline fun SharedPreferences.edit(action: SharedPreferences.Editor.() -> Unit) {
        edit().apply(action).apply()
    }
}

// Extension function for creating a Flow from SharedPreferences
private fun SharedPreferences.getStringFlowForKey(keyForString: String): Flow<String> =
    callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (keyForString == key) {
                trySend(getString(key, "") ?: "")
            }
        }
        registerOnSharedPreferenceChangeListener(listener)

        // Initial value emission
        if (contains(keyForString)) {
            trySend(getString(keyForString, "") ?: "")
        }

        awaitClose {
            unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

private fun <T> SharedPreferences.getFlow(
    key: String,
    deserializer: (String) -> T,
    defaultValue: T,
): Flow<T> {
    val stateFlow = MutableStateFlow(
        getString(key, null)?.let { deserializer(it) } ?: defaultValue,
    )

    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
        if (changedKey == key) {
            val newValue = getString(key, null)?.let { deserializer(it) } ?: defaultValue
            stateFlow.value = newValue
        }
    }

    registerOnSharedPreferenceChangeListener(listener)

    return stateFlow
        .asStateFlow()
        .map { it ?: defaultValue }
}
