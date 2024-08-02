package com.mifos.core.datastore

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.mifos.core.common.BuildConfig
import com.mifos.core.common.model.user.User
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.asServerConfig
import com.mifos.core.model.ServerConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.apache.fineract.client.models.PostAuthenticationResponse
import org.mifos.core.sharedpreference.Key
import org.mifos.core.sharedpreference.UserPreferences
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 19/08/23.
 */
const val USER_DETAILS = "user_details"
const val AUTH_USERNAME = "auth_username"
const val AUTH_PASSWORD = "auth_password"

class PrefManager @Inject constructor(
    @ApplicationContext context: Context
) : UserPreferences<User>() {

    private val serverConfigKey = Key.Custom("SERVER_CONFIG_KEY")
    
    override val preference: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    override fun getUser(): User {
        return gson.fromJson(preference.getString(USER_DETAILS, ""), User::class.java)
    }

    override fun saveUser(user: User) {
        preference.edit().putString(USER_DETAILS, gson.toJson(user)).apply()
    }

    // Created this to store userDetails
    fun savePostAuthenticationResponse(user: PostAuthenticationResponse) {
        preference.edit().putString(USER_DETAILS, gson.toJson(user)).apply()
    }

    fun setPermissionDeniedStatus(permissionDeniedStatus: String, status: Boolean) {
        preference.edit().putBoolean(permissionDeniedStatus, status).apply()
    }

    fun getPermissionDeniedStatus(permissionDeniedStatus: String): Boolean {
        return preference.getBoolean(permissionDeniedStatus, true)
    }

    var userStatus: Boolean
        get() = preference.getBoolean(Constants.SERVICE_STATUS, false)
        set(status) {
            preference.edit().putBoolean(Constants.SERVICE_STATUS, status).apply()
        }

    var usernamePassword: Pair<String, String>
        get() = Pair(
            preference.getString(AUTH_USERNAME, "")!!,
            preference.getString(AUTH_PASSWORD, "")!!
        )
        set(value) {
            preference.edit().putString(AUTH_USERNAME, value.first).apply()
            preference.edit().putString(AUTH_PASSWORD, value.second).apply()
        }

    val getServerConfig: ServerConfig =
        preference.getString(serverConfigKey.value, null)?.let {
            gson.fromJson(it, ServerConfig::class.java)
        } ?: BuildConfig.DEMO_SERVER_CONFIG.asServerConfig()

    fun updateServerConfig(config: ServerConfig?) {
        this.put(serverConfigKey, config)
    }

    fun getStringValue(key: String): Flow<String?> = flow {
        emit(preference.getString(key, ""))
    }

    fun setStringValue(key: String,value: String) {
        preference.edit().putString(key, value).apply()
    }
}