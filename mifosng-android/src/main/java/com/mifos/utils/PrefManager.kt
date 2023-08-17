package com.mifos.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.TextUtils
import com.google.gson.Gson
import com.mifos.App
import com.mifos.api.BaseUrl
import com.mifos.objects.user.User
import org.apache.fineract.client.models.PostAuthenticationResponse

/**
 * @author fomenkoo
 */
object PrefManager {
    private const val USER_ID = "preferences_user_id"
    private const val TOKEN = "preferences_token"
    private const val TENANT = "preferences_tenant"
    private const val INSTANCE_URL = "preferences_instance"
    private const val INSTANCE_DOMAIN = "preferences_domain"
    private const val PORT = "preferences_port"
    private const val USER_STATUS = "user_status"
    private const val USER_DETAILS = "user_details"
    private const val PASSCODE = "passcode"
    private const val PASSCODE_STATUS = "passcode_status"
    private const val FIRST_TIME_APP_LAUNCH = "preferences_app_launched"
    private val gson = Gson()

    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(
            App.instance
                ?.applicationContext
        )


    fun clearPrefs() {
        val editor = preferences.edit()
        preferences.let { prefs ->
            for (key in prefs.all.keys) {
                if (key != "language_type") {
                    editor?.remove(key)
                }
            }
        }
        editor?.apply()
    }

    fun getInt(preferenceKey: String?, preferenceDefaultValue: Int): Int {
        return preferences.getInt(preferenceKey, preferenceDefaultValue)
    }

    fun putInt(preferenceKey: String?, preferenceValue: Int) {
        preferences.edit()?.putInt(preferenceKey, preferenceValue)?.apply()
    }

    fun getLong(preferenceKey: String?, preferenceDefaultValue: Long): Long {
        return preferences.getLong(preferenceKey, preferenceDefaultValue)
    }

    fun putLong(preferenceKey: String?, preferenceValue: Long) {
        preferences.edit()?.putLong(preferenceKey, preferenceValue)?.apply()
    }

    fun getFloat(preferenceKey: String?, preferenceDefaultValue: Float): Float {
        return preferences.getFloat(preferenceKey, preferenceDefaultValue)
    }

    fun putFloat(preferenceKey: String?, preferenceValue: Float) {
        preferences.edit()?.putFloat(preferenceKey, preferenceValue)?.apply()
    }

    fun getBoolean(preferenceKey: String?, preferenceDefaultValue: Boolean): Boolean {
        return preferences.getBoolean(preferenceKey, preferenceDefaultValue)
    }

    fun putBoolean(preferenceKey: String?, preferenceValue: Boolean) {
        preferences.edit()?.putBoolean(preferenceKey, preferenceValue)?.apply()
    }

    fun getString(preferenceKey: String?, preferenceDefaultValue: String?): String {
        return preferences.getString(preferenceKey, preferenceDefaultValue).toString()
    }

    fun putString(preferenceKey: String?, preferenceValue: String?) {
        preferences.edit()?.putString(preferenceKey, preferenceValue)?.apply()
    }

    fun putStringSet(preferencesKey: String?, values: Set<String?>?) {
        preferences.edit()?.putStringSet(preferencesKey, values)?.apply()
    }

    fun getStringSet(preferencesKey: String?): Set<String>? {
        return preferences.getStringSet(preferencesKey, null)
    }
    // Concrete methods
    /**
     * Authentication
     */

    fun saveUser(user: PostAuthenticationResponse) {
        putString(USER_DETAILS, gson.toJson(user))
    }

    val user: User
        get() = gson.fromJson(
            getString(USER_DETAILS, "null"),
            User::class.java
        )

    fun saveToken(token: String?) {
        putString(TOKEN, token)
    }

    fun clearToken() {
        putString(TOKEN, "")
    }


    val token: String
        get() = getString(TOKEN, "")
    val isAuthenticated: Boolean
        get() = !TextUtils.isEmpty(token)

    var userId: Int
        get() = getInt(USER_ID, -1)
        set(id) {
            putInt(USER_ID, id)
        }

    var tenant: String
        get() = getString(TENANT, "default")
        set(tenant) {
            if (!TextUtils.isEmpty(tenant)) putString(TENANT, tenant)
        }

    /**
     * Connection
     */
    var instanceUrl: String
        get() = getString(INSTANCE_URL, "")
        set(instanceUrl) {
            putString(INSTANCE_URL, instanceUrl)
        }

    var instanceDomain: String
        get() = getString(INSTANCE_DOMAIN, BaseUrl.API_ENDPOINT)
        set(instanceDomain) {
            putString(INSTANCE_DOMAIN, instanceDomain)
        }

    var port: String
        get() = getString(PORT, BaseUrl.PORT)
        set(port) {
            if (!TextUtils.isEmpty(port)) putString(PORT, port)
        }
    var passCode: String
        get() = getString(PASSCODE, "")
        set(passCode) {
            putString(PASSCODE, passCode)
            passCodeStatus = true
        }
    /**
     * @return the Pref value of User status.
     * default is 0(User is online)
     */
    /**
     * Set User Status,
     * If O then user is Online
     * If 1 then User is offline
     */
    var userStatus: Int
        get() = getInt(USER_STATUS, 0)
        set(statusCode) {
            putInt(USER_STATUS, statusCode)
        }
    /**
     * @return the Pref value of pass code status.
     * default is false(pass code is not set)
     */
    /**
     * Set Pass Code Status,
     * If false then pass code is not set
     * If true then pass code is set
     */
    var passCodeStatus: Boolean
        get() = getBoolean(PASSCODE_STATUS, false)
        set(statusCode) {
            putBoolean(PASSCODE_STATUS, true)
        }

    /**
     * @return true if app is launched is first time and set the value to
     * false which is returned whenever next time it is called
     */
    val isAppFirstTimeLaunched: Boolean
        get() {
            val result = getBoolean(FIRST_TIME_APP_LAUNCH, true)
            if (result) {
                putBoolean(FIRST_TIME_APP_LAUNCH, false)
            }
            return result
        }
}